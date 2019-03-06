package com.orastays.propertylist.model;

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
public class PriceCalculatorModel {

	@JsonProperty("propertyTypeId")
	private String propertyTypeId; // Mandatory
	
	@JsonProperty("latitude")
	private String latitude; // Mandatory
	
	@JsonProperty("longitude")
	private String longitude; // Mandatory
	
	@JsonProperty("noOfGuest")
	private String noOfGuest; // Mandatory
}
