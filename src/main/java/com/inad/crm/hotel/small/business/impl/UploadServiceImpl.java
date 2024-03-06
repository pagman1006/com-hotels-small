package com.inad.crm.hotel.small.business.impl;

import com.inad.crm.hotel.small.business.IUploadService;
import com.inad.crm.hotel.small.commons.dto.DtoInFileExcel;
import com.inad.crm.hotel.small.commons.dto.DtoInFileResponse;
import com.inad.crm.hotel.small.commons.enums.I18Constants;
import com.inad.crm.hotel.small.core.exception.exceptions.ParseFileException;
import com.inad.crm.hotel.small.core.exception.exceptions.ValidFileException;
import com.inad.crm.hotel.small.dao.demographic.ICityDao;
import com.inad.crm.hotel.small.dao.demographic.IColonyDao;
import com.inad.crm.hotel.small.dao.demographic.IStateDao;
import com.inad.crm.hotel.small.dao.demographic.model.City;
import com.inad.crm.hotel.small.dao.demographic.model.Colony;
import com.inad.crm.hotel.small.dao.demographic.model.State;
import com.inad.crm.hotel.small.utils.Constants;
import com.inad.crm.hotel.small.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;

@Slf4j
@Service
public class UploadServiceImpl implements IUploadService {

    private static final DecimalFormat decimalF = new DecimalFormat("00.00");
    private static final DecimalFormat df = new DecimalFormat("#,###");

    @Autowired
    private IStateDao stateDao;
    @Autowired
    private ICityDao cityDao;
    @Autowired
    private IColonyDao colonyDao;

    @Autowired
    private MessageSource messageSource;
    private Integer statesCount;
    private Integer citiesCount;
    private Integer coloniesCount;

    @Override
    public DtoInFileResponse uploadExcelFile(MultipartFile file) {
        long timeStart = new Date().getTime();
        if (!Utils.hasExcelFormat(file)) {
            throw new ValidFileException(Utils.getLocalMessage(messageSource, I18Constants.NOT_VALID_EXCEL.getKey()));
        }
        statesCount = 0;
        citiesCount = 0;
        coloniesCount = 0;

        try {
            List<State> listStates = setupListStates(excelToListStates(file.getInputStream()));
            long timeUpload = new Date().getTime();
            String strTimeUpload = getFinishTimeStr(timeStart, timeUpload);
            showMessagesConsole(listStates.size(), strTimeUpload);
            saveColoniesAsync(listStates, coloniesCount);
            return new DtoInFileResponse(df.format(statesCount), df.format(citiesCount), df.format(coloniesCount));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private void showMessagesConsole(int size, String strTimeUpload) {
        log.debug("States loaded: {}", statesCount);
        log.debug("Cities loaded: {}", citiesCount);
        log.debug("Colonies loaded: {}", coloniesCount);
        log.debug("ListStates loaded: {}", size);
        log.debug("Upload time: {}", strTimeUpload);
    }

    private List<State> setupListStates(Set<State> setState) {
        List<State> listState = new ArrayList<>(setState);
        Collections.sort(listState);
        for (State state : listState) {
            statesCount++;
            List<City> cities = new ArrayList<>(state.getCities());
            Collections.sort(cities);
            state.setCities(cities);
            if (state.getCities() != null && !state.getCities().isEmpty()) {
                int coloniesCount = 0;
                for (City city : state.getCities()) {
                    citiesCount++;
                    setupCity(city);
                    coloniesCount = coloniesCount + city.getColonies().size();
                    this.coloniesCount = this.coloniesCount + city.getColonies().size();
                }
                log.debug("{} -> cities: {}, colonies: {}", state.getName(), state.getCities().size(), coloniesCount);
            }
        }
        return listState;
    }

    private void setupCity(City city) {
        List<Colony> colonies = new ArrayList<>(city.getColonies());
        Collections.sort(colonies);
        city.setColonies(colonies);
    }

    public Set<State> excelToListStates(InputStream input) {
        try (Workbook workbook = new XSSFWorkbook(input)) {
            Sheet sheet = workbook.getSheet(Constants.SHEET);
            Iterator<Row> rows = sheet.iterator();
            int rowNumber = 0;
            Set<State> listSetStates = new HashSet<>();
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                if (rowNumber == 0) { // Skip header
                    rowNumber++;
                    continue;
                }
                Iterator<Cell> cellsInRow = currentRow.iterator();
                DtoInFileExcel file = reedCells(cellsInRow);
                String nameState = file.getState();
                String nameCity = file.getCity();
                String nameColony = file.getColony();
                String codePostal = file.getCodePostal();
                setStateFromSetStates(listSetStates, nameState, nameCity, nameColony, codePostal);
            }
            log.debug("states: {}", listSetStates.size());
            return listSetStates;
        } catch (IOException e) {
            throw new ParseFileException(
                    Utils.getLocalMessage(messageSource, I18Constants.FAIL_PARSE_EXCEL_FILE.getKey(), Constants.SHEET));
        }
    }

    private DtoInFileExcel reedCells(Iterator<Cell> cellsInRow) {
        DtoInFileExcel file = new DtoInFileExcel();
        int cellIdx = 0;
        while (cellsInRow.hasNext()) {
            Cell currentCell = cellsInRow.next();
            switch (cellIdx) {
                case 1:
                    file.setCodePostal(Utils.removeAccents(currentCell.getStringCellValue()));
                    break;
                case 2:
                    file.setColony(Utils.removeAccents(currentCell.getStringCellValue()));
                    break;
                case 3:
                    file.setCity(Utils.removeAccents(currentCell.getStringCellValue()));
                    break;
                case 4:
                    file.setState(Utils.removeAccents(currentCell.getStringCellValue()));
                    break;
                default:
                    break;
            }
            cellIdx++;
        }
        return file;
    }

    private void setStateFromSetStates(Set<State> listSetStates, String nameState, String nameCity, String nameColony, String codePostal) {
        State state = listSetStates.stream().filter(s -> s.getName().equals(nameState)).toList().stream().findFirst()
                .orElse(null);

        if (state == null) {
            state = new State();
            state.setName(nameState);
            state.setCities(new ArrayList<>());
            listSetStates.add(state);
        }
        City city = setCity(state, nameCity);
        city.getColonies().add(setColony(nameColony, codePostal));
    }

    private City setCity(State state, String nameCity) {
        City city = null;
        if (state.getCities() != null && !state.getCities().isEmpty()) {
            city = state.getCities().stream().filter(c -> c.getName().equals(nameCity)).toList().stream().findFirst()
                    .orElse(null);
        }
        if (city == null) {
            city = new City();
            city.setName(nameCity);
            city.setColonies(new ArrayList<>());
            state.getCities().add(city);
        }
        return city;
    }

    private Colony setColony(String nameColony, String codePostal) {
        Colony colony = new Colony();
        colony.setName(nameColony);
        colony.setPostalCode(codePostal);
        return colony;
    }

    private String getFinishTimeStr(long timeStart, long timeFinish) {
        if (timeStart >= timeFinish) {
            return "";
        }
        long total = timeFinish - timeStart;
        int seconds = (int) (total / 1000);

        int minutes = seconds / 60;
        seconds = seconds - (minutes * 60);

        int milliSeconds = (int) total - (seconds * 1000);
        return String.valueOf(minutes).concat(":").concat(String.valueOf(seconds).concat(":").concat(String.valueOf(milliSeconds)));
    }

    private void saveColoniesAsync(List<State> listStates, int totalElements) {
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.debug("Start load to DBB");
            log.debug("00.00%");
            double elements = 0;
            for (State state : listStates) {
                state.getCities().forEach(city -> {
                    colonyDao.saveAll(city.getColonies());
                    cityDao.save(city);
                });
                stateDao.save(state);
                elements += state.getCities().stream().mapToInt(city -> city.getColonies().size()).sum();
                double progress = (elements * 100) / (double) totalElements;
                log.debug("{}% -> {}, {}/{}", decimalF.format(progress), state.getName(), df.format(elements), df.format(totalElements));
            }
        }).start();
    }
}
