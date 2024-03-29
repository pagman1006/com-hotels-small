package com.inad.crm.hotel.small.dao.demographic;


import com.inad.crm.hotel.small.dao.demographic.model.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ICityDao extends MongoRepository<City, String> {

	@Query(value = "{'_id': {'$in': ?0}}")
	Page<City> findByStateIdIn(List<String> names, Pageable pageable);

	@Query(value = "{'_id': {'$in': ?0}}")
	List<City> findAllByStateIdIn(List<String> names);
}
