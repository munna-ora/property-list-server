package com.orastays.propertylist.model;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class PropertyVsImageModel extends CommonModel {

	@JsonProperty("propertyImageId")
	private String propertyImageId;
	
	@JsonProperty("imageURL")
	private String imageURL;
	
	@JsonProperty("images")
	private List<MultipartFile> multipartFiles;
	
	@JsonProperty("property")
	private PropertyModel propertyModel;
}
