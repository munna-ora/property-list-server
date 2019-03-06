package com.orastays.propertylist.model;

import java.util.List;

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
public class SpaceRuleModel extends CommonModel {

	@JsonProperty("spruleId")
	private String spruleId;

	@JsonProperty("languageId")
	private String languageId;

	@JsonProperty("parentId")
	private String parentId;

	@JsonProperty("ruleName")
	private String ruleName;
	
	@JsonProperty("imgUrl")
	private String imgUrl;
	
	@JsonProperty("smImgUrl")
	private String smImgUrl;
	
	@JsonProperty("listingFlag")
	private String listingFlag;

	@JsonProperty("propertyVsSpaceRules")
	private List<PropertyVsSpaceRuleModel> propertyVsSpaceRuleModels;
}
