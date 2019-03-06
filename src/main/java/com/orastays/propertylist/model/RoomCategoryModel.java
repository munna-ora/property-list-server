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
public class RoomCategoryModel extends CommonModel {

	@JsonProperty("roomCatId")
	private String roomCatId;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("languageId")
	private String languageId;
	
	@JsonProperty("parentId")
	private String parentId;
	
	@JsonProperty("rooms")
	private List<RoomModel> roomModels;
	
	@JsonProperty("propertyType")
	private PropertyTypeModel propertyTypeModel;
}
