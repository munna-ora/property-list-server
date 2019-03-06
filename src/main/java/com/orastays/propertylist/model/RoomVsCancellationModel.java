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
public class RoomVsCancellationModel extends CommonModel {

	@JsonProperty("rcId")
	private String rcId;
	
	@JsonProperty("percentage")
	private String percentage;
	
	@JsonProperty("cancellationSlab")
	private CancellationSlabModel cancellationSlabModel;
	
	@JsonProperty("room")
	private RoomModel roomModel;
}
