package com.orastays.propertylist.dao;

import org.springframework.stereotype.Repository;

import com.orastays.propertylist.entity.MealPlanEntity;

@Repository
public class MealPlanDAO extends GenericDAO<MealPlanEntity, Long>{

	private static final long serialVersionUID = -1053659675187967291L;

	public MealPlanDAO() {
		super(MealPlanEntity.class);
	}
}
