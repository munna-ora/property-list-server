package com.orastays.propertylist.dao;

import org.springframework.stereotype.Repository;

import com.orastays.propertylist.entity.PropertyVsNearbyEntity;

@Repository
public class PropertyVsNearbyDAO extends GenericDAO<PropertyVsNearbyEntity, Long>{

	private static final long serialVersionUID = 8277658716493876351L;

	public PropertyVsNearbyDAO() {
		super(PropertyVsNearbyEntity.class);
	}
}
