package com.inad.crm.hotel.small.commons.mapper;

import com.inad.crm.hotel.small.commons.dto.DtoInCity;
import com.inad.crm.hotel.small.dao.demographic.model.City;
import com.inad.crm.hotel.small.utils.Constants;
import org.mapstruct.Mapper;

@Mapper(componentModel = Constants.SPRING)
public interface CityMapper {

    DtoInCity cityToDtoInCity(City city);
}
