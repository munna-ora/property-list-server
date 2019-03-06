package com.orastays.propertylist.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
@JsonInclude(Include.NON_NULL)
public class PropertyBookingModel {

	@JsonProperty("userToken")
	private String userToken;
	
	@JsonProperty("priceDetails")
	private Map<String, String> priceDetails;
	
	@JsonProperty("rooms")
	private List<RoomModel> roomModels;
}
