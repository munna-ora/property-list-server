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
public class PropertyVsSpaceRuleModel extends CommonModel {

	@JsonProperty("propertySpaceId")
	private String propertySpaceId;
	
	@JsonProperty("answer")
	private String answer;
	
	@JsonProperty("spaceRule")
	private SpaceRuleModel spaceRuleModel;
	
	@JsonProperty("property")
	private PropertyModel propertyModel;
}
