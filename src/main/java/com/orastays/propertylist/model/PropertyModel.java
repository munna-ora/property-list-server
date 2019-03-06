package com.orastays.propertylist.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.orastays.propertylist.model.review.UserReviewModel;
import com.orastays.propertylist.model.user.UserModel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
@JsonInclude(Include.NON_NULL)
public class PropertyModel extends CommonModel {

	@JsonProperty("propertyId")
	private String propertyId;

	@JsonProperty("name")
	private String name;

	@JsonProperty("oraname")
	private String oraname;

	@JsonProperty("entireApartment")
	private String entireApartment;

	@JsonProperty("sexCategory")
	private String sexCategory;

	@JsonProperty("apartmentName")
	private String apartmentName;

	@JsonProperty("apartmentNumber")
	private String apartmentNumber;

	@JsonProperty("latitude")
	private String latitude;

	@JsonProperty("longitude")
	private String longitude;

	@JsonProperty("address")
	private String address;

	@JsonProperty("startDate")
	private String startDate;

	@JsonProperty("endDate")
	private String endDate;

	@JsonProperty("checkinTime")
	private String checkinTime;

	@JsonProperty("checkoutTime")
	private String checkoutTime;

	@JsonProperty("coverImageUrl")
	private String coverImageUrl;

	@JsonProperty("priceDrop")
	private String priceDrop;

	@JsonProperty("immediateBooking")
	private String immediateBooking;

	@JsonProperty("strictCheckin")
	private String strictCheckin;

	@JsonProperty("contactName")
	private String contactName;

	@JsonProperty("altMobile")
	private String altMobile;

	@JsonProperty("altEmail")
	private String altEmail;

	@JsonProperty("landline")
	private String landline;

	@JsonProperty("advancePercentage")
	private String advancePercentage;
	
	@JsonProperty("location")
	private String location;

	@JsonProperty("cityType")
	private CityModel cityModel;
	
	@JsonProperty("propertyVsToiletrys")
	private PropertyVsToiletryModel propertyVsToiletryModel;

	@JsonProperty("userTypeId")
	private String userTypeId;

	@JsonProperty("propertyType")
	private PropertyTypeModel propertyTypeModel;

	@JsonProperty("stayType")
	private StayTypeModel stayTypeModel;

	@JsonProperty("hostVsAccount")
	private HostVsAccountModel hostVsAccountModel;

	@JsonProperty("propertyVsDocuments")
	private List<PropertyVsDocumentModel> propertyVsDocumentModels;

	@JsonProperty("propertyVsDescriptions")
	private List<PropertyVsDescriptionModel> propertyVsDescriptionModels;

	@JsonProperty("propertyVsGuestAccess")
	private List<PropertyVsGuestAccessModel> propertyVsGuestAccessModels;

	@JsonProperty("propertyVsImages")
	private List<PropertyVsImageModel> propertyVsImageModels;

	@JsonProperty("propertyVsNearbys")
	private List<PropertyVsNearbyModel> propertyVsNearbyModels;

	@JsonProperty("propertyVsPriceDrops")
	private List<PropertyVsPriceDropModel> propertyVsPriceDropModels;

	@JsonProperty("propertyVsSpaceRules")
	private List<PropertyVsSpaceRuleModel> propertyVsSpaceRuleModels;

	@JsonProperty("propertyVsSpecialExperiences")
	private List<PropertyVsSpecialExperienceModel> propertyVsSpecialExperienceModels;

	@JsonProperty("rooms")
	private List<RoomModel> roomModels;

	@JsonProperty("propertyOffer")
	private String propertyOffer;

	@JsonProperty("convenienceFee")
	private String convenienceFee;

	@JsonProperty("convenienceGSTPercentage")
	private String convenienceGSTPercentage;

	@JsonProperty("convenienceGSTAmount")
	private String convenienceGSTAmount;
	
	@JsonProperty("totalAmount")
	private String totalAmount;
	
	@JsonProperty("totalDiscount")
	private String totalDiscount;
	
	@JsonProperty("finalPrice")
	private String finalPrice;

	@JsonProperty("amountPayable")
	private String amountPayable;
	
	@JsonProperty("rating")
	private String rating;
	
	@JsonProperty("reviewCount")
	private String reviewCount;
	
	@JsonProperty("ratingText")
	private String ratingText;
	
	@JsonProperty("analyticsText")
	private String analyticsText;
	
	@JsonProperty("isBookmark")
	private Boolean isBookmark;
	
	@JsonProperty("reviews")
	private List<UserReviewModel> userReviewModels;
	
	@JsonProperty("hostDetails")
	private UserModel userModel;
	
	@JsonProperty("priceDetails")
	private Map<String, String> priceDetails;
}
