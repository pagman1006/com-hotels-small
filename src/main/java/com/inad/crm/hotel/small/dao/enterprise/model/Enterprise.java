package com.inad.crm.hotel.small.dao.enterprise.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;

@Data
@Document("enterprises")
public class Enterprise implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

}
