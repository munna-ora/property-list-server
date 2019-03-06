package com.orastays.propertylist.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class RoomVsMealModel extends CommonModel {

	@JsonProperty("roomVsMealId")
	private String roomVsMealId;

	@JsonProperty("mealType")
	private String mealType;

	@JsonProperty("mealDaysSunday")
	private String mealDaysSunday;

	@JsonProperty("mealDaysMonday")
	private String mealDaysMonday;

	@JsonProperty("mealDaysTuesday")
	private String mealDaysTuesday;

	@JsonProperty("mealDaysWednesday")
	private String mealDaysWednesday;

	@JsonProperty("mealDaysThursday")
	private String mealDaysThursday;

	@JsonProperty("mealDaysFriday")
	private String mealDaysFriday;

	@JsonProperty("mealDaysSaturday")
	private String mealDaysSaturday;

	@JsonProperty("mealPriceCategory")
	private String mealPriceCategory;

	@JsonProperty("room")
	private RoomModel roomModel;

	@JsonProperty("mealPlan")
	private MealPlanModel mealPlanModel;
}
