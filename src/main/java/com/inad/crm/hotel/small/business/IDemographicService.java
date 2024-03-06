package com.inad.crm.hotel.small.business;

import com.inad.crm.hotel.small.commons.ResponseData;
import com.inad.crm.hotel.small.commons.dto.DtoInCity;
import com.inad.crm.hotel.small.commons.dto.DtoInColony;
import com.inad.crm.hotel.small.commons.dto.DtoInState;

public interface IDemographicService {

	ResponseData<DtoInState> getStates();

	DtoInState getStateById(String stateId);

	ResponseData<DtoInCity> getCitiesByStateId(String stateId, Integer page, Integer pageSize);

	ResponseData<DtoInColony> findColoniesByStateIdAndCityId(String stateId, String cityId, String colonyName, String postalCode, Integer page, Integer pageSize);

	ResponseData<DtoInColony> getAllColonies(String state, String city, String colony, String postalCode, Integer page, Integer pageSize);

}
