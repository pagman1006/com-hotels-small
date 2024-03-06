package com.inad.crm.hotel.small.dao.demographic.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Document("states")
public class State implements Serializable, Comparable<State> {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	private String name;
	@DBRef(lazy = true)
	private List<City> cities;

	@Override
	public int compareTo(State s) {
		if (StringUtils.isBlank(name) || StringUtils.isBlank(s.getName())) {
			return 0;
		}
		return name.compareTo(s.getName());
	}
}
