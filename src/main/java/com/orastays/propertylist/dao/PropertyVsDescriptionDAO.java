package com.orastays.propertylist.dao;

import org.springframework.stereotype.Repository;

import com.orastays.propertylist.entity.PropertyVsDescriptionEntity;

@Repository
public class PropertyVsDescriptionDAO extends GenericDAO<PropertyVsDescriptionEntity, Long>{

	private static final long serialVersionUID = 8623581499705987423L;

	public PropertyVsDescriptionDAO() {
		super(PropertyVsDescriptionEntity.class);
	}
}
