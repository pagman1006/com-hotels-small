package com.inad.crm.hotel.small.commons.mapper;

import com.inad.crm.hotel.small.commons.dto.DtoInState;
import com.inad.crm.hotel.small.dao.demographic.model.State;
import com.inad.crm.hotel.small.utils.Constants;
import org.mapstruct.Mapper;

@Mapper(componentModel = Constants.SPRING)
public interface StateMapper {

	DtoInState stateToDtoInState(State state);
	State stateDtoInToState(DtoInState dtoInState);
}
