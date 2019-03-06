package com.orastays.propertylist.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
public class RoomModel extends CommonModel {

	@JsonProperty("roomId")
	private String roomId;
	
	@JsonProperty("oraRoomName")
	private String oraRoomName;

	@JsonProperty("sharedSpace")
	private String sharedSpace;

	@JsonProperty("cotAvailable")
	private String cotAvailable;

	@JsonProperty("noOfGuest")
	private String noOfGuest;

	@JsonProperty("noOfChild")
	private String noOfChild;

	@JsonProperty("numOfCot")
	private String numOfCot;

	@JsonProperty("roomCurrentStatus")
	private String roomCurrentStatus;

	@JsonProperty("roomCategory")
	private RoomCategoryModel roomCategoryModel;

	@JsonProperty("property")
	private PropertyModel propertyModel;

	@JsonProperty("roomPricePerNight")
	private String roomPricePerNight;

	@JsonProperty("roomPricePerMonth")
	private String roomPricePerMonth;

	@JsonProperty("sharedBedPricePerNight")
	private String sharedBedPricePerNight;

	@JsonProperty("sharedBedPricePerMonth")
	private String sharedBedPricePerMonth;

	@JsonProperty("cotPrice")
	private String cotPrice;

	@JsonProperty("sharedBedPrice")
	private String sharedBedPrice;

	@JsonProperty("commission")
	private String commission;

	@JsonProperty("oraPercentage")
	private String oraPercentage;

	@JsonProperty("hostDiscountWeekly")
	private String hostDiscountWeekly;

	@JsonProperty("hostDiscountMonthly")
	private String hostDiscountMonthly;
	
	@JsonProperty("oraDiscountPercentage")
	private String oraDiscountPercentage;
	
	@JsonProperty("accomodationName")
	private String accomodationName;
	
	@JsonProperty("roomStandard")
	private String roomStandard;
	
	@JsonProperty("numOfBed")
	private String numOfBed;

	@JsonProperty("roomVsAmenities")
	private List<RoomVsAmenitiesModel> roomVsAmenitiesModels;

	@JsonProperty("roomVsCancellations")
	private List<RoomVsCancellationModel> roomVsCancellationModels;

	@JsonProperty("roomVsImages")
	private List<RoomVsImageModel> roomVsImageModels;

	@JsonProperty("roomVsSpecialities")
	private List<RoomVsSpecialitiesModel> roomVsSpecialitiesModels;

	@JsonProperty("roomVsMeals")
	private List<RoomVsMealModel> roomVsMealModels;
	
	@JsonProperty("roomVsOffers")
	private List<RoomVsOfferModel> roomVsOfferModels;
	
	@JsonProperty("isSelected")
	private String isSelected;
	
	@JsonProperty("bedRequired")
	private String bedRequired;
	
	@JsonProperty("cotRequired")
	private String cotRequired;
	
	@JsonProperty("extraPersonPrice")
	private String extraPersonPrice;
	
	@JsonProperty("oraPrice")
	private String oraPrice;
	
	@JsonProperty("oraDiscount")
	private String oraDiscount;
	
	@JsonProperty("hostBasePrice")
	private String hostBasePrice;

	@JsonProperty("hostPrice")
	private String hostPrice;
	
	@JsonProperty("hostDiscount")
	private String hostDiscount;
	
	@JsonProperty("roomOffer")
	private String roomOffer;
	
	@JsonProperty("priceDrop")
	private String priceDrop;
	
	@JsonProperty("gstPercentage")
	private String gstPercentage;
	
	@JsonProperty("amountWithGST")
	private String amountWithGST;
	
	@JsonProperty("oraFinalPrice")
	private String oraFinalPrice;

	@JsonProperty("bedAllocated")
	private String bedAllocated;
	
	@JsonProperty("sgst")
	private String sgst;

	@JsonProperty("cgst")
	private String cgst;

	@JsonProperty("igst")
	private String igst;
	
	@JsonProperty("gstAmt")
	private String gstAmt;
	
	@JsonProperty("totalAmt")
	private String totalAmt;
	
	@JsonProperty("numOfBedBooked")
	private String numOfBedBooked;
	
	@JsonProperty("numOfCotBooked")
	private String numOfCotBooked;
}
