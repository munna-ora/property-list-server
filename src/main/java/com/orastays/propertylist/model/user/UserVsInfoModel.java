/**
 * @author Krishanu
 */
package com.orastays.propertylist.model.user;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.orastays.propertylist.model.CommonModel;
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
public class UserVsInfoModel extends CommonModel {
	
	@JsonProperty("userVsInfoId")
	private String userVsInfoId;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("imageUrl")
	private String imageUrl;
	
	private MultipartFile image;
	
	@JsonProperty("altPhno")
	private String altPhno;
	
	@JsonProperty("bioInfo")
	private String bioInfo;
	
	@JsonProperty("dob")
	private String dob;
	
	@JsonProperty("companyName")
	private String companyName;
	
	@JsonProperty("location")
	private String location;
	
	@JsonProperty("noShowCount")
	private String noShowCount;
}
