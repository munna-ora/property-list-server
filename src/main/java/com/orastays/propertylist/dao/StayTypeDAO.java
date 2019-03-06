package com.orastays.propertylist.dao;

import org.springframework.stereotype.Repository;

import com.orastays.propertylist.entity.StayTypeEntity;

@Repository
public class StayTypeDAO extends GenericDAO<StayTypeEntity, Long>{

	private static final long serialVersionUID = -2825648313579082324L;

	public StayTypeDAO() {
		super(StayTypeEntity.class);
	}
}
