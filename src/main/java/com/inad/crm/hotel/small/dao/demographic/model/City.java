package com.inad.crm.hotel.small.dao.demographic.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Document("cities")
public class City implements Serializable, Comparable<City> {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	private String name;
	@DBRef(lazy = true)
	private List<Colony> colonies;

	@Override
	public int compareTo(City c) {
		return name.compareTo(c.getName());
	}
}
