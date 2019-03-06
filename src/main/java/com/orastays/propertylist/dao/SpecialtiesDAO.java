package com.orastays.propertylist.dao;

import org.springframework.stereotype.Repository;

import com.orastays.propertylist.entity.SpecialtiesEntity;

@Repository
public class SpecialtiesDAO extends GenericDAO<SpecialtiesEntity, Long>{

	private static final long serialVersionUID = 8334316579662908618L;

	public SpecialtiesDAO() {
		super(SpecialtiesEntity.class);
	}
}
