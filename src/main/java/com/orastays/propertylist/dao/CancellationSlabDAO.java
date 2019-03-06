package com.orastays.propertylist.dao;

import org.springframework.stereotype.Repository;

import com.orastays.propertylist.entity.CancellationSlabEntity;

@Repository
public class CancellationSlabDAO extends GenericDAO<CancellationSlabEntity, Long>{
	
	private static final long serialVersionUID = 5448093017460189614L;

	public CancellationSlabDAO() {
		super(CancellationSlabEntity.class);

	}

}
