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
public class PriceDropModel extends CommonModel {

	@JsonProperty("priceDropId")
	private String priceDropId;
	
	@JsonProperty("afterTime")
	private String afterTime;
	
	@JsonProperty("propertyVsPriceDrops")
	private List<PropertyVsPriceDropModel> propertyVsPriceDropModels;
}
