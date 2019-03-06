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
public class PropertyVsDescriptionModel extends CommonModel {

	@JsonProperty("propertyDescId")
	private String propertyDescId;
	
	@JsonProperty("description")
	private String description;
	
	@JsonProperty("languageId")
	private String languageId;
	
	@JsonProperty("property")
	private PropertyModel propertyModel;
}
