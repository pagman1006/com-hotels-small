package com.inad.crm.hotel.small.commons.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class DtoInFileExcel {

	private Long id;
	private String codePostal;
	private String colony;
	private String city;
	private String state;
}
