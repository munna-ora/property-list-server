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
public class InterestModel extends CommonModel {

	
	@JsonProperty("interestId")
	private String interestId;
	
	@JsonProperty("interestName")
	private String interestName;
	
	@JsonProperty("hostVsInterests")
	private List<HostVsInterestModel> hostVsInterestModels;

}