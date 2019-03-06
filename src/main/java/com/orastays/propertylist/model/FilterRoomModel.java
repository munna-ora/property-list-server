package com.orastays.propertylist.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.orastays.propertylist.entity.RoomEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
@JsonInclude(Include.NON_NULL)
public class FilterRoomModel {

	private Long roomId;
	private Integer bedAvailable;
	private Integer bedAllocated;
	private Double perBedCost;
	private Integer numOfAdult;
	private Integer numOfChild;
	private RoomEntity roomEntity;
	private Boolean isSelected;
	private Double totalPrice;
	private String numOfBedBooked;
	private String numOfCotBooked;
	
}