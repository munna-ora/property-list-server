package com.orastays.propertylist.dao;

import org.springframework.stereotype.Repository;

import com.orastays.propertylist.entity.CountryEntity;

@Repository
public class CountryDAO extends GenericDAO<CountryEntity, Long>{

	private static final long serialVersionUID = -2499320089097062624L;

	public CountryDAO() {
		super(CountryEntity.class);
	}
}
