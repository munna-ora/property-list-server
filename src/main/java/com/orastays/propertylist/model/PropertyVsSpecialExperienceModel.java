package com.orastays.propertylist.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class PropertyVsSpecialExperienceModel extends CommonModel {

	@JsonProperty("propertyExpId")
	private String propertyExpId;
	
	@JsonProperty("specialExperience")
	private SpecialExperienceModel specialExperienceModel;
	
	@JsonProperty("property")
	private PropertyModel propertyModel;
}
