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
public class PropertyVsGuestAccessModel extends CommonModel {

	@JsonProperty("propertyGAccessId")
	private String propertyGAccessId;
	
	@JsonProperty("guestAccess")
	private String guestAccess;
	
	@JsonProperty("property")
	private PropertyModel propertyModel;
}
