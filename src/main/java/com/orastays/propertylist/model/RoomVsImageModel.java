package com.orastays.propertylist.model;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class RoomVsImageModel extends CommonModel {

	@JsonProperty("roomVsImageId")
	private String roomVsImageId;
	
	@JsonProperty("imageUrl")
	private String imageUrl;
	
	@JsonProperty("image")
	private List<MultipartFile> images;
	
	@JsonProperty("room")
	private RoomModel roomModel;
}
