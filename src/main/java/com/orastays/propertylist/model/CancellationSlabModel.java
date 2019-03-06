package com.orastays.propertylist.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class CancellationSlabModel extends CommonModel {

	@JsonProperty("cancellationSlabId")
	private String cancellationSlabId;
	
	@JsonProperty("startTime")
	private String startTime;
	
	@JsonProperty("endTime")
	private String endTime;
	
	@JsonProperty("roomVsCancellations")
	private List<RoomVsCancellationModel> roomVsCancellationModels;
}
