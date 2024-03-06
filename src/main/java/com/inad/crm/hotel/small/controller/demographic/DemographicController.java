package com.inad.crm.hotel.small.controller.demographic;

import com.inad.crm.hotel.small.business.IDemographicService;
import com.inad.crm.hotel.small.commons.ResponseData;
import com.inad.crm.hotel.small.commons.dto.DtoInCity;
import com.inad.crm.hotel.small.commons.dto.DtoInColony;
import com.inad.crm.hotel.small.commons.dto.DtoInState;
import com.inad.crm.hotel.small.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(Constants.URL_BASE + "/demographics")
public class DemographicController {

	@Autowired
	private IDemographicService demographicService;

	@GetMapping(path = "/states", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseData<DtoInState>> getStates() {
		log.debug("getListStates: ALL");
		return new ResponseEntity<>(demographicService.getStates(), HttpStatus.OK);
	}

	@GetMapping(path = "/states/{stateId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DtoInState> getStateById(@PathVariable(name = "stateId") String stateId) {
		log.debug("getState: {}}", stateId);
		return new ResponseEntity<>(demographicService.getStateById(stateId), HttpStatus.OK);
	}

	@GetMapping(path = "/states/{stateId}/cities", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseData<DtoInCity>> getCitiesByState(
			@PathVariable(name = "stateId") String stateId,
			@RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
		log.debug("State Id: {}", stateId);
		return new ResponseEntity<>(demographicService.getCitiesByStateId(stateId, page, pageSize), HttpStatus.OK);
	}

	@GetMapping(path = "/states/{stateId}/cities/{cityId}/colonies", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseData<DtoInColony>> getColonies(
			@PathVariable(name = "stateId") String stateId,
			@PathVariable(name = "cityId") String cityId, @RequestParam(name = "postalCode", required = false) String postalCode,
			@RequestParam(name = "name", required = false) String colonyName,
			@RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize) {

		log.debug("StateId: {}, cityId: {}", stateId, cityId);
		return new ResponseEntity<>(demographicService.findColoniesByStateIdAndCityId(stateId, cityId, colonyName, postalCode, page, pageSize), HttpStatus.OK);
	}

	@GetMapping(path = "/colonies", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseData<DtoInColony>> getAllColonies(
			@RequestParam(name = "state", defaultValue = "", required = false) String state,
			@RequestParam(name = "city", defaultValue = "", required = false) String city,
			@RequestParam(name = "colony", defaultValue = "", required = false) String colony,
			@RequestParam(name = "postalCode", defaultValue = "", required = false) String postalCode,
			@RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
			@RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize) {

		log.debug("State: {}, city: {}, colony: {}, postalCode: {}", state, city, colony, postalCode);
		return new ResponseEntity<>(demographicService.getAllColonies(state, city, colony, postalCode, page, pageSize), HttpStatus.OK);
	}

}
