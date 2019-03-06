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
public class StayTypeModel extends CommonModel {

	@JsonProperty("stayTypeId")
	private String stayTypeId;
	
	@JsonProperty("languageId")
	private String languageId;
	
	@JsonProperty("parentId")
	private String parentId;
	
	@JsonProperty("stayTypeName")
	private String stayTypeName;
	
	@JsonProperty("property")
	private PropertyModel propertyModel;
}
