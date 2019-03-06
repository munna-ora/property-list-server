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
public class SpecialtiesModel extends CommonModel {

	@JsonProperty("specialtiesId")
	private String specialtiesId;
	
	@JsonProperty("specialitiesName")
	private String specialitiesName;
	
	@JsonProperty("languageId")
	private String languageId;
	
	@JsonProperty("parentId")
	private String parentId;
	
	@JsonProperty("roomVsSpecialities")
	private List<RoomVsSpecialitiesModel> roomVsSpecialitiesModels;
}
