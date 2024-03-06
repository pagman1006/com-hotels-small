package com.inad.crm.hotel.small.commons.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@JsonInclude(Include.NON_NULL)
public class DtoInCity implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
}
