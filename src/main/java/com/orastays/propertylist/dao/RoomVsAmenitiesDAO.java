package com.orastays.propertylist.dao;

import org.springframework.stereotype.Repository;

import com.orastays.propertylist.entity.RoomVsAmenitiesEntity;

@Repository
public class RoomVsAmenitiesDAO  extends GenericDAO<RoomVsAmenitiesEntity, Long>{

	private static final long serialVersionUID = -6937519057315280276L;

	public RoomVsAmenitiesDAO() {
		super(RoomVsAmenitiesEntity.class);
	}
}
