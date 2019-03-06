package com.orastays.propertylist.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class MealPlanModel extends CommonModel {

	@JsonProperty("mealPlanId")
	private String mealPlanId;

	@JsonProperty("mealPlanName")
	private String mealPlanName;

	@JsonProperty("languageId")
	private String languageId;

	@JsonProperty("parentId")
	private String parentId;

	@JsonProperty("mealPlanCatVsMealPlans")
	private List<MealPlanCatVsMealPlanModel> mealPlanCatVsMealPlanModels;

	@JsonProperty("roomVsMeals")
	private List<RoomVsMealModel> roomVsMealModels;
}
