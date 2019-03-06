package com.orastays.propertylist.dao;

import org.springframework.stereotype.Repository;

import com.orastays.propertylist.entity.PropertyVsDocumentEntity;

@Repository
public class PropertyVsDocumentDAO extends GenericDAO<PropertyVsDocumentEntity, Long> {

	private static final long serialVersionUID = 4321245486048335283L;

	public PropertyVsDocumentDAO() {
		super(PropertyVsDocumentEntity.class);

	}

}
