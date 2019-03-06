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
public class WishlistModel extends CommonModel {

	@JsonProperty("wishlistId")
	private String wishlistId;
	
	@JsonProperty("userId")
	private String userId;
	
	@JsonProperty("property")
	private PropertyModel propertyModel;
}
