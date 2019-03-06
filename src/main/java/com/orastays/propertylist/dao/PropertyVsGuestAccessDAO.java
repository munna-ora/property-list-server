package com.orastays.propertylist.dao;

import org.springframework.stereotype.Repository;

import com.orastays.propertylist.entity.PropertyVsGuestAccessEntity;

@Repository
public class PropertyVsGuestAccessDAO extends GenericDAO<PropertyVsGuestAccessEntity, Long>{

	private static final long serialVersionUID = -204920984856521903L;

	public PropertyVsGuestAccessDAO() {
		super(PropertyVsGuestAccessEntity.class);
	}
}
