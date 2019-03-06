package com.orastays.propertylist.dao;

import org.springframework.stereotype.Repository;

import com.orastays.propertylist.entity.PropertyVsSpecialExperienceEntity;

@Repository
public class PropertyVsSpecialExperienceDAO extends GenericDAO<PropertyVsSpecialExperienceEntity, Long>{

	private static final long serialVersionUID = -3355682512048247119L;

	public PropertyVsSpecialExperienceDAO() {
		super(PropertyVsSpecialExperienceEntity.class);
	}
}
