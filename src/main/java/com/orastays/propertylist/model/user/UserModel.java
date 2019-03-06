/**
 * @author Abhideep
 */
package com.orastays.propertylist.model.user;

import java.util.List;

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
public class UserModel extends CommonModel {

	@JsonProperty("userId")
	private String userId;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("mobileNumber")
	private String mobileNumber;
	
	@JsonProperty("emailId")
	private String emailId;
	
	@JsonProperty("userVsInfo")
	private UserVsInfoModel userVsInfo;
	
	@JsonProperty("hostVsInterests")
	private List<HostVsInterestModel> hostVsInterests;
	
	@JsonProperty("hostVsDomains")
	private List<HostVsDomainModel> hostVsDomains;
	
}