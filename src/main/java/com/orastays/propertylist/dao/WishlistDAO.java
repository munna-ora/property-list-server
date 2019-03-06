package com.orastays.propertylist.dao;

import org.springframework.stereotype.Repository;

import com.orastays.propertylist.entity.WishlistEntity;

@Repository
public class WishlistDAO extends GenericDAO<WishlistEntity, Long>{

	private static final long serialVersionUID = 1217815487039405279L;

	public WishlistDAO() {
		super(WishlistEntity.class);
	}
}
