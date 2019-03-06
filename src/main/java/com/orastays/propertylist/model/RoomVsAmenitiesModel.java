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
public class RoomVsAmenitiesModel extends CommonModel {

	@JsonProperty("roomVsAminitiesId")
	private String roomVsAminitiesId;
	
	@JsonProperty("amenities")
	private AmenitiesModel amenitiesModel;
	
	@JsonProperty("room")
	private RoomModel roomModel;
}
