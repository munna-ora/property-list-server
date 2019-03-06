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
public class RoomVsSpecialitiesModel extends CommonModel {

	@JsonProperty("roomspecId")
	private String roomspecId;
	
	@JsonProperty("specialties")
	private SpecialtiesModel specialtiesModel;
	
	@JsonProperty("room")
	private RoomModel roomModel;
}
