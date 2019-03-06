package com.orastays.propertylist.service.impl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orastays.propertylist.entity.PropertyEntity;
import com.orastays.propertylist.entity.PropertyVsSpaceRuleEntity;
import com.orastays.propertylist.entity.RoomEntity;
import com.orastays.propertylist.entity.RoomVsAmenitiesEntity;
import com.orastays.propertylist.entity.RoomVsMealEntity;
import com.orastays.propertylist.exceptions.FormExceptions;
import com.orastays.propertylist.helper.Accommodation;
import com.orastays.propertylist.helper.MealPriceCategory;
import com.orastays.propertylist.helper.PropertyListConstant;
import com.orastays.propertylist.helper.Status;
import com.orastays.propertylist.helper.UserType;
import com.orastays.propertylist.helper.Util;
import com.orastays.propertylist.model.AmenitiesModel;
import com.orastays.propertylist.model.FilterCiteriaModel;
import com.orastays.propertylist.model.OfferModel;
import com.orastays.propertylist.model.PriceCalculatorModel;
import com.orastays.propertylist.model.PropertyListViewModel;
import com.orastays.propertylist.model.PropertyModel;
import com.orastays.propertylist.model.ResponseModel;
import com.orastays.propertylist.model.SpaceRuleModel;
import com.orastays.propertylist.model.booking.BookingModel;
import com.orastays.propertylist.model.review.BookingVsRatingModel;
import com.orastays.propertylist.model.review.UserReviewModel;
import com.orastays.propertylist.model.user.UserModel;
import com.orastays.propertylist.service.HomeService;

@Service
@Transactional(readOnly = true)
public class HomeServiceImpl extends BaseServiceImpl implements HomeService {

	private static final Logger logger = LogManager.getLogger(HomeServiceImpl.class);

	@Override
	public List<PropertyListViewModel> fetchPropertyByType(String propertyTypeId, String userToken) throws FormExceptions {

		if (logger.isInfoEnabled()) {
			logger.info("fetchPropertyByType -- START");
		}

		UserModel userModel = homeValidation.validatePropertyType(propertyTypeId, userToken);
		List<PropertyListViewModel> propertyListViewModels = new ArrayList<>();
		try {
			Map<String, String> innerMap1 = new LinkedHashMap<>();
			innerMap1.put(PropertyListConstant.STATUS, String.valueOf(Status.ACTIVE.ordinal()));

			Map<String, Map<String, String>> outerMap1 = new LinkedHashMap<>();
			outerMap1.put("eq", innerMap1);

			Map<String, Map<String, Map<String, String>>> alliasMap = new LinkedHashMap<>();
			alliasMap.put(entitymanagerPackagesToScan + ".PropertyEntity", outerMap1);

			Map<String, String> innerMap2 = new LinkedHashMap<>();
			innerMap2.put("propertyTypeId", propertyTypeId);

			Map<String, Map<String, String>> outerMap2 = new LinkedHashMap<>();
			outerMap2.put("eq", innerMap2);

			alliasMap.put("propertyTypeEntity", outerMap2);

			List<PropertyEntity> propertyEntities = propertyDAO.fetchListBySubCiteria(alliasMap);
			if (!CollectionUtils.isEmpty(propertyEntities)) {
				List<PropertyEntity> propertyEntities2 = propertyEntities.stream().collect(Collectors.toList());
				PropertyListViewModel propertyListViewModel = findByRating(propertyEntities2);
				if(Objects.nonNull(userModel)) {
					setBookMark(propertyListViewModel, userModel);
				}
				propertyListViewModel.setAnalyticsText("Got Highest Rating based on Services");
				propertyListViewModels.add(propertyListViewModel);
				propertyEntities2.removeIf(p -> StringUtils.equals(String.valueOf(p.getPropertyId()), propertyListViewModel.getPropertyId()));
				
				PropertyListViewModel propertyListViewModel2 = findByPrice(propertyEntities2);
				if(Objects.nonNull(userModel)) {
					setBookMark(propertyListViewModel2, userModel);
				}
				propertyListViewModel.setAnalyticsText("Offering Lowest Price");
				propertyListViewModels.add(propertyListViewModel2);
				propertyEntities2.removeIf(p -> StringUtils.equals(String.valueOf(p.getPropertyId()), propertyListViewModel2.getPropertyId()));
				
				PropertyListViewModel propertyListViewModel3 = findByBooking(propertyEntities2);
				if(Objects.nonNull(userModel)) {
					setBookMark(propertyListViewModel3, userModel);
				}
				propertyListViewModels.add(propertyListViewModel3);
				propertyEntities2.removeIf(p -> StringUtils.equals(String.valueOf(p.getPropertyId()), propertyListViewModel3.getPropertyId()));
				
				PropertyListViewModel propertyListViewModel4 = findByAmmenities(propertyEntities2);
				if(Objects.nonNull(userModel)) {
					setBookMark(propertyListViewModel4, userModel);
				}
				propertyListViewModel.setAnalyticsText("Contains Highest Ammenities");
				propertyListViewModels.add(propertyListViewModel4);
				
			}

		} catch(Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception in getPropertyByType -- " + Util.errorToString(e));
			}
		}

		if(logger.isInfoEnabled()) {
			logger.info("fetchPropertyByType -- END");
		}
	
		return propertyListViewModels;
	}
	
	private void setBookMark(PropertyListViewModel propertyListViewModel, UserModel userModel) {
		
		if (logger.isInfoEnabled()) {
			logger.info("setBookMark -- START");
		}
		
		try {
			Map<String, String> innerMap1 = new LinkedHashMap<>();
			innerMap1.put(PropertyListConstant.STATUS, String.valueOf(Status.ACTIVE.ordinal()));
			innerMap1.put("userId", userModel.getUserId());

			Map<String, Map<String, String>> outerMap1 = new LinkedHashMap<>();
			outerMap1.put("eq", innerMap1);

			Map<String, Map<String, Map<String, String>>> alliasMap = new LinkedHashMap<>();
			alliasMap.put(entitymanagerPackagesToScan + ".WishlistEntity", outerMap1);

			Map<String, String> innerMap2 = new LinkedHashMap<>();
			innerMap2.put("propertyId", propertyListViewModel.getPropertyId());

			Map<String, Map<String, String>> outerMap2 = new LinkedHashMap<>();
			outerMap2.put("eq", innerMap2);

			alliasMap.put("propertyEntity", outerMap2);
			if(Objects.nonNull(wishlistDAO.fetchObjectBySubCiteria(alliasMap))) {
				propertyListViewModel.setIsBookmark(true);
			}
			
		} catch(Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception in getPropertyByType -- " + Util.errorToString(e));
			}
		}
		
		if(logger.isInfoEnabled()) {
			logger.info("setBookMark -- END");
		}
	}
	
	private PropertyListViewModel findByRating(List<PropertyEntity> propertyEntities) {
		
		if (logger.isInfoEnabled()) {
			logger.info("findByRating -- START");
		}
		
		PropertyListViewModel propertyListViewModel = null;
		Map<PropertyEntity, Integer> ratingCount = new LinkedHashMap<>();
		propertyEntities.parallelStream().forEach(propertyEntity -> {
		//for(PropertyEntity propertyEntity : propertyEntities) {
			try {
				UserReviewModel userReviewModel2 = new UserReviewModel();
				userReviewModel2.setPropertyId(String.valueOf(propertyEntity.getPropertyId()));
				userReviewModel2.setUserTypeId(String.valueOf(Status.INACTIVE.ordinal()));
				ResponseModel responseModel = restTemplate.postForObject(messageUtil.getBundle("review.server.url") +"fetch-review", userReviewModel2, ResponseModel.class);
				Gson gson = new Gson();
				String jsonString = gson.toJson(responseModel.getResponseBody());
				gson = new Gson();
				Type listType = new TypeToken<List<UserReviewModel>>() {}.getType();
				List<UserReviewModel> userReviewModels = gson.fromJson(jsonString, listType);
				
				if (logger.isInfoEnabled()) {
					logger.info("userReviewModels ==>> "+userReviewModels);
				}
				
				System.err.println("userReviewModels ==>> "+userReviewModels);
				
				if(Objects.nonNull(userReviewModels) && !CollectionUtils.isEmpty(userReviewModels)) {
					// Calculate Rating
					Map<String, String> ratingTypes = new LinkedHashMap<>();
					AtomicBoolean isContinueRating = new AtomicBoolean(false);
					userReviewModels.parallelStream().forEach(userReviewModel -> {
					isContinueRating.set(false);
					//for(UserReviewModel userReviewModel : userReviewModels) {
						if(!CollectionUtils.isEmpty(userReviewModel.getBookingVsRatings()) && !isContinueRating.get()) {
							for(BookingVsRatingModel bookingVsRatingModel : userReviewModel.getBookingVsRatings()) {
								if(ratingTypes.isEmpty()) { // First Time
									ratingTypes.put(bookingVsRatingModel.getRatings().getRatingId(), bookingVsRatingModel.getRating());
								} else {
									
									String reviews = ratingTypes.get(bookingVsRatingModel.getRatings().getRatingId());
									if (StringUtils.isBlank(reviews)) { // No Such Rating ID Found
										ratingTypes.put(bookingVsRatingModel.getRatings().getRatingId(), bookingVsRatingModel.getRating());
									} else {
										reviews = String.valueOf(Long.parseLong(reviews) + Long.parseLong(bookingVsRatingModel.getRating()));
										ratingTypes.put(bookingVsRatingModel.getRatings().getRatingId(), reviews);
									}
								}
							}
						} else {
							ratingCount.put(propertyEntity, 0); // Rating
							isContinueRating.set(true);
						}
					});
					
					Integer totalRating = 0;
					for (Map.Entry<String,String> entry : ratingTypes.entrySet()) { 
						totalRating = totalRating + Integer.parseInt(entry.getValue());
					}
					ratingCount.put(propertyEntity, (totalRating / ratingTypes.size())); // Rating
				}
				
			} catch (Exception e) {
				//e.printStackTrace();
				if (logger.isInfoEnabled()) {
					logger.info("Exception in findByRating -- "+Util.errorToString(e));
				}
				System.err.println("propertyEntity ==>> "+propertyEntity);
				ratingCount.put(propertyEntity, 0); // Rating
			}
		});
		
		PropertyEntity propertyEntity = null;
		RoomEntity roomEntity = null;
		int maxValueInMap = (Collections.max(ratingCount.values()));
        for (Entry<PropertyEntity, Integer> entry : ratingCount.entrySet()) {
            if (entry.getValue() == maxValueInMap) {
            	propertyEntity = entry.getKey();
            	if(!CollectionUtils.isEmpty(propertyEntity.getRoomEntities())) {
            		
					for(RoomEntity roomEntity2 :propertyEntity.getRoomEntities()) {
						if(roomEntity2.getStatus() == Status.ACTIVE.ordinal()) { // Fetching only ACTIVE Rooms
							roomEntity = roomEntity2; // Considering First Room
							break;
						}
					}
					
            	}
            }
        }
        
        propertyListViewModel = setPropertyListViewForLandingPage(propertyEntity);
        List<String> prices = priceCalculation(propertyEntity, roomEntity);
        propertyListViewModel.setTotalPrice(prices.get(0));
		propertyListViewModel.setDiscountedPrice(prices.get(1));
        
		if (logger.isInfoEnabled()) {
			logger.info("findByRating -- END");
		}
		
		return propertyListViewModel;
	}
	
	private PropertyListViewModel findByBooking(List<PropertyEntity> propertyEntities) {
		
		if (logger.isInfoEnabled()) {
			logger.info("findByBooking -- START");
		}
		
		PropertyListViewModel propertyListViewModel = null;
		Map<PropertyEntity, Integer> bookingCount = new LinkedHashMap<>();
		propertyEntities.parallelStream().forEach(propertyEntity -> {
			//for(PropertyEntity propertyEntity : propertyEntities) {
			try {
				BookingModel bookingModel = new BookingModel();
				bookingModel.setPropertyId(String.valueOf(propertyEntity.getPropertyId()));
				ResponseModel responseModel = restTemplate.postForObject(messageUtil.getBundle("booking.server.url") +"get-property-bookings", bookingModel, ResponseModel.class);
				Gson gson = new Gson();
				String jsonString = gson.toJson(responseModel.getResponseBody());
				gson = new Gson();
				Type listType = new TypeToken<List<BookingModel>>() {}.getType();
				List<BookingModel> bookingModels = gson.fromJson(jsonString, listType);
				
				if (logger.isInfoEnabled()) {
					logger.info("bookingModels ==>> "+bookingModels);
				}
				
				System.err.println("bookingModels ==>> "+bookingModels);
				
				if (!CollectionUtils.isEmpty(bookingModels)) {
					bookingCount.put(propertyEntity, bookingModels.size());
				} else {
					bookingCount.put(propertyEntity, 0);
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (logger.isInfoEnabled()) {
					logger.info("Exception in findByBooking -- "+Util.errorToString(e));
				}
				bookingCount.put(propertyEntity, 0);
			}
		});
		
		PropertyEntity propertyEntity = null;
		RoomEntity roomEntity = null;
		int maxValueInMap = (Collections.max(bookingCount.values()));
        for (Entry<PropertyEntity, Integer> entry : bookingCount.entrySet()) {
            if (entry.getValue() == maxValueInMap) {
            	propertyEntity = entry.getKey();
            	if(!CollectionUtils.isEmpty(propertyEntity.getRoomEntities())) {
					for(RoomEntity roomEntity2 :propertyEntity.getRoomEntities()) {
						if(roomEntity2.getStatus() == Status.ACTIVE.ordinal()) { // Fetching only ACTIVE Rooms
							roomEntity = roomEntity2; // Considering First Room
							break;
						}
					}
            	}
            }
        }
		
        propertyListViewModel = setPropertyListViewForLandingPage(propertyEntity);
        propertyListViewModel.setAnalyticsText("Booked "+maxValueInMap+ " times");
        List<String> prices = priceCalculation(propertyEntity, roomEntity);
        propertyListViewModel.setTotalPrice(prices.get(0));
		//propertyListViewModel.setDiscountedPrice(prices.get(1));
		
		if (logger.isInfoEnabled()) {
			logger.info("findByBooking -- END");
		}
		
		return propertyListViewModel;
	}
	
	private PropertyListViewModel findByAmmenities(List<PropertyEntity> propertyEntities) {
			
		if (logger.isInfoEnabled()) {
			logger.info("findByAmmenities -- START");
		}
		
		PropertyListViewModel propertyListViewModel = null;
		Map<RoomEntity, Integer> ammenitiesCount = new LinkedHashMap<>();
		propertyEntities.parallelStream().forEach(propertyEntity -> {
			//for(PropertyEntity propertyEntity : propertyEntities) {
			if(!CollectionUtils.isEmpty(propertyEntity.getRoomEntities())) {
				for(RoomEntity roomEntity : propertyEntity.getRoomEntities()) {
					if(Objects.nonNull(roomEntity)) {
						if(!CollectionUtils.isEmpty(roomEntity.getRoomVsAmenitiesEntities())) {
							ammenitiesCount.put(roomEntity, roomEntity.getRoomVsAmenitiesEntities().size());
						}
					}
				}
			}
		});
		
		PropertyEntity propertyEntity = null;
		RoomEntity roomEntity = null;
		int maxValueInMap = (Collections.max(ammenitiesCount.values()));
        for (Entry<RoomEntity, Integer> entry : ammenitiesCount.entrySet()) {
            if (entry.getValue() == maxValueInMap) {
            	propertyEntity = entry.getKey().getPropertyEntity();
            	roomEntity = entry.getKey();
            }
        }
        
        propertyListViewModel = setPropertyListViewForLandingPage(propertyEntity);
        List<String> prices = priceCalculation(propertyEntity, roomEntity);
        propertyListViewModel.setTotalPrice(prices.get(0));
		//propertyListViewModel.setDiscountedPrice(prices.get(1));
		
		if (logger.isInfoEnabled()) {
			logger.info("findByAmmenities -- END");
		}
		
		return propertyListViewModel;
	}
	
	private PropertyListViewModel findByPrice(List<PropertyEntity> propertyEntities) {
		
		if (logger.isInfoEnabled()) {
			logger.info("findByPrice -- START");
		}
		
		PropertyListViewModel propertyListViewModel = new PropertyListViewModel();
		Map<RoomEntity, Double> priceCount = new LinkedHashMap<>();
		propertyEntities.parallelStream().forEach(propertyEntity -> {
			//for(PropertyEntity propertyEntity : propertyEntities) {
			if(!CollectionUtils.isEmpty(propertyEntity.getRoomEntities())) {
				for(RoomEntity roomEntity : propertyEntity.getRoomEntities()) {
					if(Objects.nonNull(roomEntity)) {
						Double price = 0.0D;
						Double totalPrice = 0.0D;
						if(propertyEntity.getStayTypeEntity().getStayTypeId() == Status.ACTIVE.ordinal()){   //Long term
							
							if(StringUtils.equals(roomEntity.getAccomodationName(), Accommodation.SHARED.name())) { //shared
								//Shared Month price 
								price = (Double.parseDouble(roomEntity.getSharedBedPricePerMonth())/30);
							} else { //private
								 //Private Month Price
								price = (Double.parseDouble(roomEntity.getRoomPricePerMonth())/30);
							}
					
						} else {   //both & Short term
							
							if(StringUtils.equals(roomEntity.getAccomodationName(), Accommodation.SHARED.name())) { //shared
								//Shared Night price 
								price = Double.parseDouble(roomEntity.getSharedBedPricePerNight());
							} else {   //private
								 //Private Night Price
								price = Double.parseDouble(roomEntity.getRoomPricePerNight());
							}
						}
						System.out.println("price ==>> "+price);
						System.err.println("roomEntity.getOraPercentage() ==>> "+roomEntity.getOraPercentage());
						System.out.println("totalPrice before ==>> "+totalPrice);
						totalPrice = totalPrice + price + (Double.parseDouble(roomEntity.getOraPercentage()) * price / 100);
						System.err.println("totalPrice after including OraPercentage ==>> "+totalPrice); 
						
						priceCount.put(roomEntity, totalPrice);
					}
				}
			}
		});
		
		PropertyEntity propertyEntity = null;
		RoomEntity roomEntity = null;
		Double minValueInMap = (Collections.min(priceCount.values()));
        for (Entry<RoomEntity, Double> entry : priceCount.entrySet()) {
            if (entry.getValue() == minValueInMap) {
            	propertyEntity = entry.getKey().getPropertyEntity();
            	roomEntity = entry.getKey();
            }
        }
		
        propertyListViewModel = setPropertyListViewForLandingPage(propertyEntity);
        List<String> prices = priceCalculation(propertyEntity, roomEntity);
        propertyListViewModel.setTotalPrice(prices.get(0));
		propertyListViewModel.setDiscountedPrice(prices.get(1));
        
		if (logger.isInfoEnabled()) {
			logger.info("findByPrice -- END");
		}
		
		return propertyListViewModel;
	}
	
	private List<String> priceCalculation(PropertyEntity propertyEntity, RoomEntity roomEntity) {
		
		if (logger.isInfoEnabled()) {
			logger.info("priceCalculation -- START");
		}
		
		List<String> prices = new ArrayList<>();
		Double price = 0.0D;
		Double totalPrice = 0.0D;
		Double discountedPrice = 0.0D;
		if(propertyEntity.getStayTypeEntity().getStayTypeId() == Status.ACTIVE.ordinal()){   //Long term
			
			if(StringUtils.equals(roomEntity.getAccomodationName(), Accommodation.SHARED.name())) { //shared
				//Shared Month price 
				price = (Double.parseDouble(roomEntity.getSharedBedPricePerMonth())/30);
			} else { //private
				 //Private Month Price
				price = (Double.parseDouble(roomEntity.getRoomPricePerMonth())/30);
			}
	
		} else {   //both & Short term
			
			if(StringUtils.equals(roomEntity.getAccomodationName(), Accommodation.SHARED.name())) { //shared
				//Shared Night price 
				price = Double.parseDouble(roomEntity.getSharedBedPricePerNight());
			} else {   //private
				 //Private Night Price
				price = Double.parseDouble(roomEntity.getRoomPricePerNight());
			}
		}
		
		System.out.println("price ==>> "+price);
		System.err.println("roomEntity.getOraPercentage() ==>> "+roomEntity.getOraPercentage());
		System.out.println("totalPrice before ==>> "+totalPrice);
		totalPrice = totalPrice + price + (Double.parseDouble(roomEntity.getOraPercentage()) * price / 100);
		System.err.println("totalPrice after including OraPercentage ==>> "+totalPrice); 
		
		Double oraDiscount = 0.0D;
		// Room Vs ORA Discount Percentage
		if (!StringUtils.isBlank(roomEntity.getOraDiscountPercentage())) {
			oraDiscount = Double.parseDouble(roomEntity.getOraDiscountPercentage()) * price / 100;
		}
		
		discountedPrice = totalPrice - oraDiscount;
		
		prices.add(String.valueOf(Math.round(totalPrice * 100D) / 100D));
		prices.add(String.valueOf(Math.round(discountedPrice * 100D) / 100D));
		
		if (logger.isInfoEnabled()) {
			logger.info("priceCalculation -- END");
		}
		
		return prices;
	}

	@Override
	public Object priceCalculator(PriceCalculatorModel priceCalculatorModel) throws FormExceptions {

		if (logger.isInfoEnabled()) {
			logger.info("priceCalculation -- START");
		}
		
		FilterCiteriaModel filterCiteriaModel = homeValidation.validatePriceCalculator(priceCalculatorModel);
		Object price = 0;
		if(Objects.nonNull(filterCiteriaModel)) {
			List<PropertyEntity> propertyEntities = propertyDAO.selectByRadius(filterCiteriaModel);
			if(!CollectionUtils.isEmpty(propertyEntities)) {
				List<PropertyEntity> filteredPropertyEntities = new ArrayList<>();
				propertyEntities.parallelStream().forEach(propertyEntity -> {
					//for(PropertyEntity propertyEntity : propertyEntities2) {
					if(StringUtils.equals(String.valueOf(propertyEntity.getPropertyTypeEntity().getPropertyTypeId()) , priceCalculatorModel.getPropertyTypeId())) {
						filteredPropertyEntities.add(propertyEntity);
					}
				});
				
				if(!CollectionUtils.isEmpty(filteredPropertyEntities)) {
					 price = findByPrice(filteredPropertyEntities, priceCalculatorModel.getNoOfGuest());
				}
			}
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("priceCalculation -- END");
		}
		
		return price;
	}
	
	private Object findByPrice(List<PropertyEntity> propertyEntities, String noOfGuest) {
		
		if (logger.isInfoEnabled()) {
			logger.info("findByPrice -- START");
		}
		
		Object returnPrice = 0;
		Map<RoomEntity, Double> priceCount = new LinkedHashMap<>();
		//propertyEntities.parallelStream().forEach(propertyEntity -> {
		for(PropertyEntity propertyEntity : propertyEntities) {
			if(!CollectionUtils.isEmpty(propertyEntity.getRoomEntities())) {
				for(RoomEntity roomEntity : propertyEntity.getRoomEntities()) {
					if(Objects.nonNull(roomEntity)) {
						Double price = 0.0D;
						if(propertyEntity.getStayTypeEntity().getStayTypeId() == Status.ACTIVE.ordinal()){   //Long term
							
							if(StringUtils.equals(roomEntity.getAccomodationName(), Accommodation.SHARED.name())) { //shared
								//Shared Month price 
								price = (Double.parseDouble(roomEntity.getSharedBedPricePerMonth())/30);
							} else { //private
								 //Private Month Price
								price = (Double.parseDouble(roomEntity.getRoomPricePerMonth())/30/Double.parseDouble(roomEntity.getNoOfGuest()));
							}
					
						} else {   //both & Short term
							
							if(StringUtils.equals(roomEntity.getAccomodationName(), Accommodation.SHARED.name())) { //shared
								//Shared Night price 
								price = Double.parseDouble(roomEntity.getSharedBedPricePerNight());
							} else {   //private
								 //Private Night Price
								price = Double.parseDouble(roomEntity.getRoomPricePerNight())/Double.parseDouble(roomEntity.getNoOfGuest());
							}
						}
						
						//System.out.println("price ==>> "+price);
						priceCount.put(roomEntity, price);
					}
				}
			}
		//});
		}
		
//		RoomEntity roomEntity = null;
		Double maxValueInMap = (Collections.max(priceCount.values()));
//        for (Entry<RoomEntity, Double> entry : priceCount.entrySet()) {
//            if (entry.getValue() == maxValueInMap) {
//            	roomEntity = entry.getKey();
//            	break;
//            }
//        }
        
        returnPrice = maxValueInMap * Double.parseDouble(noOfGuest) * 30;
		
		if (logger.isInfoEnabled()) {
			logger.info("findByPrice -- END");
		}
		
		return returnPrice;
	}
	
	private PropertyListViewModel setPropertyListViewForLandingPage(PropertyEntity propertyEntity) {
		
		if (logger.isInfoEnabled()) {
			logger.info("setPropertyListViewForLandingPage -- START");
		}
		
		PropertyListViewModel propertyListViewModel = new PropertyListViewModel();
		propertyListViewModel.setPropertyId(String.valueOf(propertyEntity.getPropertyId()));
		propertyListViewModel.setOraName(propertyEntity.getOraname());
		propertyListViewModel.setAddress(propertyEntity.getAddress());
		propertyListViewModel.setLatitude(propertyEntity.getLatitude());
		propertyListViewModel.setLongitude(propertyEntity.getLongitude());
		propertyListViewModel.setCoverImageURL(propertyEntity.getCoverImageUrl());
		
		if(!CollectionUtils.isEmpty(propertyEntity.getRoomEntities())) {
			for(RoomEntity roomEntity : propertyEntity.getRoomEntities()) {
				if(Objects.nonNull(roomEntity)) {
					if(StringUtils.equals(roomEntity.getRoomStandard(), PropertyListConstant.ROOM_STANDARD_PREMIUM)) {
						propertyListViewModel.setRoomStandard(PropertyListConstant.ROOM_STANDARD_PREMIUM);
						break;
					} else if(StringUtils.equals(roomEntity.getRoomStandard(), PropertyListConstant.ROOM_STANDARD_EXPRESS)) {
						propertyListViewModel.setRoomStandard(PropertyListConstant.ROOM_STANDARD_EXPRESS);
					} else {
						propertyListViewModel.setRoomStandard(PropertyListConstant.ROOM_STANDARD_NORMAL);
					}
				}
			}
		} else {
			propertyListViewModel.setRoomStandard(PropertyListConstant.ROOM_STANDARD_NORMAL);
		}
		
		propertyListViewModel.setRating(getRatingAndReview(propertyEntity).get(0));
		propertyListViewModel.setReviewCount(getRatingAndReview(propertyEntity).get(1));
		
		if(Double.parseDouble(propertyListViewModel.getRating()) >= Double.parseDouble(messageUtil.getBundle("rating.key1"))) {
			propertyListViewModel.setRatingText(messageUtil.getBundle("rating.value1"));
		} else if(Double.parseDouble(propertyListViewModel.getRating()) >= Double.parseDouble(messageUtil.getBundle("rating.key2"))) {
			propertyListViewModel.setRatingText(messageUtil.getBundle("rating.value2"));
		} else {
			propertyListViewModel.setRatingText(messageUtil.getBundle("rating.value3"));
		}
		
		List<SpaceRuleModel> spaceRuleModels = null;
		if(!CollectionUtils.isEmpty(propertyEntity.getPropertyVsSpaceRuleEntities())) {
			spaceRuleModels = new ArrayList<>();
			for(PropertyVsSpaceRuleEntity propertyVsSpaceRuleEntity : propertyEntity.getPropertyVsSpaceRuleEntities()) {
				spaceRuleModels.add(spaceRuleConverter.entityToModel(propertyVsSpaceRuleEntity.getSpaceRuleEntity()));
			}
		} else {
			propertyListViewModel.setSpaceRuleModels(spaceRuleModels);
		}
		propertyListViewModel.setSpaceRuleModels(spaceRuleModels);
		
		propertyListViewModel.setPgCategorySex(propertyEntity.getSexCategory());
		
		// Meal Section
		propertyListViewModel.setMealFlag(false);
		if(!CollectionUtils.isEmpty(propertyEntity.getRoomEntities())) {
			for(RoomEntity roomEntity :propertyEntity.getRoomEntities()) {
				if(Objects.nonNull(roomEntity)) {
					if(!CollectionUtils.isEmpty(roomEntity.getRoomVsMealEntities())) {
						for(RoomVsMealEntity roomVsMealEntity : roomEntity.getRoomVsMealEntities()) {
							if((StringUtils.equals(roomVsMealEntity.getMealPriceCategorySunday(), MealPriceCategory.COMPLIMENTARY.name()))
									|| (StringUtils.equals(roomVsMealEntity.getMealPriceCategoryMonday(), MealPriceCategory.COMPLIMENTARY.name()))
									|| (StringUtils.equals(roomVsMealEntity.getMealPriceCategoryTuesday(), MealPriceCategory.COMPLIMENTARY.name()))
									|| (StringUtils.equals(roomVsMealEntity.getMealPriceCategoryWednesday(), MealPriceCategory.COMPLIMENTARY.name()))
									|| (StringUtils.equals(roomVsMealEntity.getMealPriceCategoryThursday(), MealPriceCategory.COMPLIMENTARY.name()))
									|| (StringUtils.equals(roomVsMealEntity.getMealPriceCategoryFriday(), MealPriceCategory.COMPLIMENTARY.name()))
									|| (StringUtils.equals(roomVsMealEntity.getMealPriceCategorySaturday(), MealPriceCategory.COMPLIMENTARY.name()))) {
									propertyListViewModel.setMealFlag(true);
									break;
							}
						}
					}
				}
			}
		}
		
		Set<AmenitiesModel> amenitiesModels = new LinkedHashSet<>();
		if(!CollectionUtils.isEmpty(propertyEntity.getRoomEntities())) {
			for(RoomEntity roomEntity : propertyEntity.getRoomEntities()) {
				if(Objects.nonNull(roomEntity)) {
					if(!CollectionUtils.isEmpty(roomEntity.getRoomVsAmenitiesEntities())) {
						for(RoomVsAmenitiesEntity roomVsAmenitiesEntity : roomEntity.getRoomVsAmenitiesEntities()) {
							amenitiesModels.add(amenitiesConverter.entityToModel(roomVsAmenitiesEntity.getAmenitiesEntity()));
						}
					}
				}
			}
		}
		
		propertyListViewModel.setAmenitiesModels(amenitiesModels);
		propertyListViewModel.setIsBookmark(false);
		
		if (logger.isInfoEnabled()) {
			logger.info("setPropertyListViewForLandingPage -- END");
		}
		
		return propertyListViewModel;
	}
	
	private List<String> getRatingAndReview(PropertyEntity propertyEntity) {
		
		if (logger.isInfoEnabled()) {
			logger.info("getRatingAndReview -- START");
		}
		
		List<String> ratings = new ArrayList<>();
		try {
			PropertyModel propertyModel = new PropertyModel();
			propertyModel.setPropertyId(String.valueOf(propertyEntity.getPropertyId()));
			propertyModel.setUserTypeId(String.valueOf(UserType.CUSTOMER.ordinal()));
			ResponseModel responseModel = restTemplate.postForObject(messageUtil.getBundle("review.server.url") +"fetch-review", propertyModel, ResponseModel.class);
			Gson gson = new Gson();
			String jsonString = gson.toJson(responseModel.getResponseBody());
			gson = new Gson();
			Type listType = new TypeToken<List<UserReviewModel>>() {}.getType();
			List<UserReviewModel> userReviewModels = gson.fromJson(jsonString, listType);
			
			if (logger.isInfoEnabled()) {
				logger.info("userReviewModels ==>> "+userReviewModels);
			}
			System.out.println("userReviewModels ==>> "+userReviewModels);
			
			if(!CollectionUtils.isEmpty(userReviewModels)) {
				// Calculate Rating
				Map<String, String> ratingTypes = new LinkedHashMap<>();
				for(UserReviewModel userReviewModel : userReviewModels) {
					if(!CollectionUtils.isEmpty(userReviewModel.getBookingVsRatings())) {
						for(BookingVsRatingModel bookingVsRatingModel : userReviewModel.getBookingVsRatings()) {
							if(ratingTypes.isEmpty()) { // First Time
								ratingTypes.put(bookingVsRatingModel.getRatings().getRatingId(), bookingVsRatingModel.getRating());
							} else {
								
								String reviews = ratingTypes.get(bookingVsRatingModel.getRatings().getRatingId());
								if (StringUtils.isBlank(reviews)) { // No Such Rating ID Found
									ratingTypes.put(bookingVsRatingModel.getRatings().getRatingId(), bookingVsRatingModel.getRating());
								} else {
									reviews = String.valueOf(Long.parseLong(reviews) + Long.parseLong(bookingVsRatingModel.getRating()));
									ratingTypes.put(bookingVsRatingModel.getRatings().getRatingId(), reviews);
								}
							}
						}
					} else {
						ratings.add(0, "0"); // Rating
						break;
					}
				}
				
				Double totalRating = 0.0D;
				for (Map.Entry<String,String> entry : ratingTypes.entrySet()) { 
					totalRating = totalRating + Double.parseDouble(entry.getValue());
				}
				
				if (!CollectionUtils.isEmpty(ratingTypes)) {
					ratings.add(0, String.valueOf(Math.round(totalRating / ratingTypes.size() / userReviewModels.size()) * 100D / 100D)); // Rating
				} else {
					ratings.add("0"); // Rating
				}
				
				// Calculate Review Count
				ratings.add(1, String.valueOf(userReviewModels.size())); // Review Count
			} else {
				ratings.add("0"); // Rating
				ratings.add("0"); // Review Count
			}
			
		} catch (HttpClientErrorException e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception in getRatingAndReview -- "+(e.getCause()));
			}
			ratings.add("0");
			ratings.add("0");
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception in getRatingAndReview -- "+Util.errorToString(e));
			}
			ratings.add("0");
			ratings.add("0");
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("getRatingAndReview -- END");
		}
		
		return ratings;
	}
	
	@Override
	public List<OfferModel> fetchOffer() throws FormExceptions {
		
		if (logger.isInfoEnabled()) {
			logger.info("fetchOffer -- START");
		}
		
		List<OfferModel> offerModels = null;
		
		try {
			Map<String, String> innerMap1 = new LinkedHashMap<>();
			innerMap1.put(PropertyListConstant.STATUS, String.valueOf(Status.ACTIVE.ordinal()));
	
			Map<String, Map<String, String>> outerMap1 = new LinkedHashMap<>();
			outerMap1.put("eq", innerMap1);
	
			Map<String, Map<String, Map<String, String>>> alliasMap = new LinkedHashMap<>();
			alliasMap.put(entitymanagerPackagesToScan+".OfferEntity", outerMap1);
			
			offerModels = offerConverter.entityListToModelList(offerDAO.fetchListBySubCiteria(alliasMap));

		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception in fetchOffer -- "+Util.errorToString(e));
			}
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("fetchOffer -- END");
		}
		
		return offerModels;
	}
}