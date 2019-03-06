package com.orastays.propertylist.model;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
@JsonInclude(Include.NON_NULL)
public class PropertyListViewModel {

	@JsonProperty("propertyId")
	private String propertyId;
	
	@JsonProperty("oraName")
	private String oraName;
	
	@JsonProperty("address")
	private String address;
	
	@JsonProperty("latitude")
	private String latitude;
	
	@JsonProperty("longitude")
	private String longitude;
	
	@JsonProperty("coverImageURL")
	private String coverImageURL;
	
	@JsonProperty("roomStandard")
	private String roomStandard; // Set Premium if any
	
	@JsonProperty("rating")
	private String rating;
	
	@JsonProperty("reviewCount")
	private String reviewCount;
	
	@JsonProperty("spaceRules")
	private List<SpaceRuleModel> spaceRuleModels; // Couple Friendly, Pet Friendly
	
	@JsonProperty("pgCategorySex")
	private String pgCategorySex; // Male/Female
	
	@JsonProperty("totalPrice")
	private String totalPrice;
	
	@JsonProperty("discountedPrice")
	private String discountedPrice;
	
	@JsonProperty("mealFlag")
	private Boolean mealFlag;
	
	@JsonProperty("ratingText")
	private String ratingText;
	
	@JsonProperty("analyticsText")
	private String analyticsText;
	
	@JsonProperty("amenities")
	private Set<AmenitiesModel> amenitiesModels;
	
	@JsonProperty("isBookmark")
	private Boolean isBookmark;
	
	@JsonProperty("stayType")
	private String stayType;
}