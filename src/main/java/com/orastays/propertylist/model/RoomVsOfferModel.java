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
public class RoomVsOfferModel extends CommonModel {

	@JsonProperty("roomVsOfferId")
	private String roomVsOfferId;

	@JsonProperty("offer")
	private OfferModel offerModel;

	@JsonProperty("room")
	private RoomModel roomModel;
}
