package com.orastays.propertylist.model;

import java.util.List;

import javax.persistence.Column;

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
public class AmenitiesModel extends CommonModel {

	@JsonProperty("aminitiesId")
	private String aminitiesId;

	@JsonProperty("aminitiesName")
	private String aminitiesName;

	@JsonProperty("filterFlag")
	private String filterFlag;

	@JsonProperty("priority")
	private String priority;

	@JsonProperty("expressFlag")
	private String expressFlag;

	@JsonProperty("premiumFlag")
	private String premiumFlag;

	@JsonProperty("aminitiesType")
	private String aminitiesType;

	@JsonProperty("languageId")
	private String languageId;

	@JsonProperty("parentId")
	private String parentId;
	
	@JsonProperty("imgUrl1")
	private String imgUrl1;
	
	@JsonProperty("imgUrl2")
	private String imgUrl2;
	
	@Column(name = "smimg_url")
	@JsonProperty("smImgUrl")
	private String smImgUrl;

	@JsonProperty("roomVsAmenities")
	private List<RoomVsAmenitiesModel> roomVsAmenitiesModels;
}
