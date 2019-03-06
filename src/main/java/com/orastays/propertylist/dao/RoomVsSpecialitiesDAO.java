package com.orastays.propertylist.dao;

import org.springframework.stereotype.Repository;

import com.orastays.propertylist.entity.RoomVsSpecialitiesEntity;

@Repository
public class RoomVsSpecialitiesDAO extends GenericDAO<RoomVsSpecialitiesEntity, Long>{

	private static final long serialVersionUID = 3004555723409032771L;

	public RoomVsSpecialitiesDAO() {
		super(RoomVsSpecialitiesEntity.class);
	}
}
