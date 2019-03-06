package com.orastays.propertylist.dao;

import org.springframework.stereotype.Repository;

import com.orastays.propertylist.entity.GstSlabEntity;

@Repository
public class GstSlabDAO extends GenericDAO<GstSlabEntity, Long> {

	private static final long serialVersionUID = 4458145644390220480L;

	public GstSlabDAO() {
		super(GstSlabEntity.class);

	}
}
