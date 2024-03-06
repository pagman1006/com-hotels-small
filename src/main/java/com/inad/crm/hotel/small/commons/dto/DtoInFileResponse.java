package com.inad.crm.hotel.small.commons.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DtoInFileResponse implements Serializable {

	private String colonies;
	private String cities;
	private String states;
}
