package com.inad.crm.hotel.small.commons.mapper;

import com.inad.crm.hotel.small.commons.dto.DtoInColony;
import com.inad.crm.hotel.small.dao.demographic.model.Colony;
import com.inad.crm.hotel.small.utils.Constants;
import org.mapstruct.Mapper;

@Mapper(componentModel = Constants.SPRING)
public interface ColonyMapper {

    DtoInColony colonyToDtoInColony(Colony colony);
}
