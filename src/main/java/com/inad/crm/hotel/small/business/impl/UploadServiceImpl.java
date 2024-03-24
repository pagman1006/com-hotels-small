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
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class UploadServiceImpl implements IUploadService {

    private static final DecimalFormat decimalF = new DecimalFormat("00.00");
    private static final DecimalFormat df = new DecimalFormat("#,###");
    private final AtomicInteger statesCount = new AtomicInteger(0);
    private final AtomicInteger citiesCount = new AtomicInteger(0);
    private final AtomicInteger coloniesCount = new AtomicInteger(0);
    double elements = 0;
    @Autowired
    private IStateDao stateDao;
    @Autowired
    private ICityDao cityDao;
    @Autowired
    private IColonyDao colonyDao;
    @Autowired
    private MessageSource messageSource;

    @Override
    public DtoInFileResponse uploadExcelFile(MultipartFile file) {
        final LocalDateTime timeStart = LocalDateTime.now();
        if (!Utils.hasExcelFormat(file)) {
            throw new ValidFileException(Utils.getLocalMessage(messageSource, I18Constants.NOT_VALID_EXCEL.getKey()));
        }
        statesCount.set(0);
        citiesCount.set(0);
        coloniesCount.set(0);

        try {
            List<State> listStates = setupListStates(excelToListStates(file.getInputStream()));
            final LocalDateTime timeUpload = LocalDateTime.now();
            final String strTimeUpload = getFinishTimeStr(timeStart, timeUpload);
            showMessagesConsole(listStates.size(), strTimeUpload);
            saveColoniesAsync(listStates, getColoniesCount());
            return new DtoInFileResponse(df.format(coloniesCount), df.format(citiesCount), df.format(statesCount));
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
        final List<State> listState = new ArrayList<>(setState);
        Collections.sort(listState);
        for (State state : listState) {
            incrementStatesCount();
            final List<City> cities = new ArrayList<>(state.getCities());
            Collections.sort(cities);
            state.setCities(cities);
            if (state.getCities() != null && !state.getCities().isEmpty()) {
                int coloniesCount = 0;
                for (City city : state.getCities()) {
                    incrementCitiesCount();
                    setupCity(city);
                    coloniesCount = coloniesCount + city.getColonies().size();
                    incrementColoniesCount(city.getColonies().size());
                }
                log.debug("{} -> cities: {}, colonies: {}", state.getName(), state.getCities().size(), coloniesCount);
            }
        }
        return listState;
    }

    private void setupCity(City city) {
        final List<Colony> colonies = new ArrayList<>(city.getColonies());
        Collections.sort(colonies);
        city.setColonies(colonies);
    }

    public Set<State> excelToListStates(InputStream input) {
        try (Workbook workbook = new XSSFWorkbook(input)) {
            final Sheet sheet = workbook.getSheet(Constants.SHEET);
            final Iterator<Row> rows = sheet.iterator();
            int rowNumber = 0;
            final Set<State> listSetStates = new HashSet<>();
            while (rows.hasNext()) {
                final Row currentRow = rows.next();
                if (rowNumber == 0) { // Skip header
                    rowNumber++;
                    continue;
                }
                final Iterator<Cell> cellsInRow = currentRow.iterator();
                final DtoInFileExcel file = reedCells(cellsInRow);
                final String nameState = file.getState();
                final String nameCity = file.getCity();
                final String nameColony = file.getColony();
                final String codePostal = file.getCodePostal();
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
        final DtoInFileExcel file = new DtoInFileExcel();
        int cellIdx = 0;
        while (cellsInRow.hasNext()) {
            final Cell currentCell = cellsInRow.next();
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
        final Colony colony = new Colony();
        colony.setName(nameColony);
        colony.setPostalCode(codePostal);
        return colony;
    }

    private String getFinishTimeStr(LocalDateTime timeStart, LocalDateTime timeFinish) {
        final LocalDateTime dif = timeFinish.minusSeconds(timeStart.getSecond());
        return dif.getHour() + ":" + dif.getMinute() + ":" + dif.getSecond();
    }

    private void saveColoniesAsync(List<State> listStates, int totalElements) {
        log.debug("Start load to DBB");
        log.debug("00.00%");
        for (State state : listStates) {
            ExecutorService service = null;
            try {
                service = Executors.newSingleThreadExecutor();
                service.submit(() -> {
                    state.getCities().forEach(city -> {
                        colonyDao.saveAll(city.getColonies());
                        cityDao.save(city);
                    });
                    stateDao.save(state);
                    elements += state.getCities().stream().mapToInt(city -> city.getColonies().size()).sum();
                    double progress = (elements * 100) / (double) totalElements;
                    log.debug("{}% -> {}, {}/{}", decimalF.format(progress), state.getName(), df.format(elements), df.format(totalElements));
                });
            } finally {
                if (service != null) service.shutdown();
            }
        }
    }

    private synchronized int getColoniesCount() {
        return coloniesCount.get();
    }

    private synchronized void incrementStatesCount() {
        statesCount.incrementAndGet();
    }

    private synchronized void incrementCitiesCount() {
        citiesCount.incrementAndGet();
    }

    private synchronized void incrementColoniesCount(int increment) {
        coloniesCount.set(coloniesCount.get() + increment);
    }
}
