package com.orastays.propertylist.dao;

import org.springframework.stereotype.Repository;

import com.orastays.propertylist.entity.CityEntity;

@Repository
public class CityDAO extends GenericDAO<CityEntity, Long>{


	private static final long serialVersionUID = 1349708224691598471L;

	public CityDAO() {
		super(CityEntity.class);
	}
}
