package com.orastays.propertylist.service.impl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orastays.propertylist.entity.PropertyEntity;
import com.orastays.propertylist.entity.RoomEntity;
import com.orastays.propertylist.exceptions.FormExceptions;
import com.orastays.propertylist.helper.PropertyListConstant;
import com.orastays.propertylist.helper.Status;
import com.orastays.propertylist.helper.Util;
import com.orastays.propertylist.model.ConvenienceModel;
import com.orastays.propertylist.model.FilterCiteriaModel;
import com.orastays.propertylist.model.FilterRoomModel;
import com.orastays.propertylist.model.PropertyListViewModel;
import com.orastays.propertylist.model.PropertyModel;
import com.orastays.propertylist.model.ResponseModel;
import com.orastays.propertylist.model.booking.BookingVsRoomModel;
import com.orastays.propertylist.model.review.BookingVsRatingModel;
import com.orastays.propertylist.model.review.UserReviewModel;
import com.orastays.propertylist.model.user.UserModel;
import com.orastays.propertylist.service.PropertyListService;

@Service
@Transactional(readOnly = true)
public class PropertyListServiceImpl extends BaseServiceImpl implements PropertyListService {

	private static final Logger logger = LogManager.getLogger(PropertyListServiceImpl.class);
	
	@Override
	public List<PropertyListViewModel> fetchProperties(FilterCiteriaModel filterCiteriaModel) throws FormExceptions {

		if (logger.isInfoEnabled()) {
			logger.info("fetchProperties -- START");
		}
		
		UserModel userModel = propertyListValidation.validateFetchProperties(filterCiteriaModel);
		List<PropertyListViewModel> propertyListViewModels = new ArrayList<PropertyListViewModel>();
		
		try {
			Map<String, String> innerMap1 = new LinkedHashMap<>();
			innerMap1.put("status", String.valueOf(Status.ACTIVE.ordinal()));
	
			Map<String, Map<String, String>> outerMap1 = new LinkedHashMap<>();
			outerMap1.put("eq", innerMap1);
	
			Map<String, Map<String, Map<String, String>>> alliasMap = new LinkedHashMap<>();
			alliasMap.put(entitymanagerPackagesToScan+".PropertyEntity", outerMap1);
			
			Map<String, String> innerMap2 = new LinkedHashMap<>();
			innerMap2.put("propertyTypeId", filterCiteriaModel.getPropertyTypeId());

			Map<String, Map<String, String>> outerMap2 = new LinkedHashMap<>();
			outerMap2.put("eq", innerMap2);

			alliasMap.put("propertyTypeEntity", outerMap2);
			
			List<PropertyEntity> propertyEntities = propertyDAO.fetchListBySubCiteria(alliasMap);
			if(!CollectionUtils.isEmpty(propertyEntities)) {
				
				List<PropertyEntity> filteredPropertyEntitiesByRadius = propertyDAO.selectByRadius(filterCiteriaModel);
				AtomicBoolean isContinueRating = new AtomicBoolean(false);
				propertyEntities.parallelStream().forEach(propertyEntity -> {
					//for(PropertyEntity propertyEntity : propertyEntities) {
					isContinueRating.set(false);
					
					// Filter By Property Start Date and End Date
					if(propertyListHelper.filterByPropertyDate(propertyEntity, filterCiteriaModel)) {
						
						// Filter by location // Mandatory
						if(propertyListHelper.filterByLocation(propertyEntity, filteredPropertyEntitiesByRadius)) {
							// Filter by checkInDate // Mandatory
							// Filter by checkOutDate // Mandatory
							// Filter by roomModels // Mandatory
							Map<Boolean, Map<String, FilterRoomModel>> filterResult = propertyListHelper.filterBycheckInDate(propertyEntity, filterCiteriaModel);
							if (filterResult.containsKey(true)) {
								
								Map<String, FilterRoomModel> filteredRooms = filterResult.get(true);
								System.err.println("filteredRooms ==>> "+filteredRooms);
								// Filter By Rating
								if (!CollectionUtils.isEmpty(filterCiteriaModel.getRatings())) {
									if (!propertyListHelper.filterByRating(propertyEntity, filterCiteriaModel)) {
										isContinueRating.set(true);
									}
								}
								
								// Filter by amenitiesModels
								if (!CollectionUtils.isEmpty(filterCiteriaModel.getAmenitiesModels())) {
									if (!propertyListHelper.filterByAmmenities(propertyEntity, filterCiteriaModel)) {
										isContinueRating.set(true);
									}
								}
								
								
								// Filter by budgets
								if(!CollectionUtils.isEmpty(filterCiteriaModel.getBudgets())) {
									if (!propertyListHelper.filterByBudget(propertyEntity, filterCiteriaModel, filteredRooms)) {
										isContinueRating.set(true);
									}
								}
								
								
								// Filter by popularLocations
								if(!CollectionUtils.isEmpty(filterCiteriaModel.getPopularLocations())) {
									if (!propertyListHelper.filterByPopularLocation(propertyEntity, filterCiteriaModel)) {
										isContinueRating.set(true);
									}
								}
								
								
								// Filter by spaceRuleModels // Couple Friendly, Pet Friendly
								if(!CollectionUtils.isEmpty(filterCiteriaModel.getSpaceRuleModels())) {
									if (!propertyListHelper.filterBySpaceRule(propertyEntity, filterCiteriaModel)) {
										isContinueRating.set(true);
									}
								}
								
								
								// Filter by pgCategorySexModels // Male/Female
								if(!StringUtils.isBlank(filterCiteriaModel.getPgCategorySex())) {
									if (!propertyListHelper.filterBySex(propertyEntity, filterCiteriaModel)) {
										isContinueRating.set(true);
									}
								}
								
								if(!isContinueRating.get()) {
									PropertyListViewModel propertyListViewModel = propertyListHelper.setPropertyListView(propertyEntity, filterCiteriaModel, filteredRooms);
									if(Objects.nonNull(userModel)) {
										setBookMark(propertyListViewModel, userModel);
									}
									propertyListViewModels.add(propertyListViewModel);
								}
							} 
						} 
					}
				});
				
				// TODO Sorting, Pagination if any
			}
								
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception in fetchProperty -- "+Util.errorToString(e));
			}
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("fetchProperties -- END");
		}
		
		return propertyListViewModels;
	}
	
	@Override
	public PropertyModel fetchPropertyDetails(FilterCiteriaModel filterCiteriaModel) throws FormExceptions {
		
		if (logger.isInfoEnabled()) {
			logger.info("fetchPropertyDetails -- START");
		}
		
		UserModel userModel = propertyListValidation.validateFetchPropertyDetails(filterCiteriaModel);
		PropertyModel propertyModel = null;
		
		try {
			Map<String, String> innerMap1 = new LinkedHashMap<>();
			innerMap1.put("status", String.valueOf(Status.ACTIVE.ordinal()));
			innerMap1.put("propertyId", String.valueOf(Long.parseLong(filterCiteriaModel.getPropertyId())));
	
			Map<String, Map<String, String>> outerMap1 = new LinkedHashMap<>();
			outerMap1.put("eq", innerMap1);
	
			Map<String, Map<String, Map<String, String>>> alliasMap = new LinkedHashMap<>();
			alliasMap.put(entitymanagerPackagesToScan+".PropertyEntity", outerMap1);
			
			Map<String, String> innerMap2 = new LinkedHashMap<>();
			innerMap2.put("propertyTypeId", filterCiteriaModel.getPropertyTypeId());

			Map<String, Map<String, String>> outerMap2 = new LinkedHashMap<>();
			outerMap2.put("eq", innerMap2);

			alliasMap.put("propertyTypeEntity", outerMap2);
			
			PropertyEntity propertyEntity = propertyDAO.fetchObjectBySubCiteria(alliasMap);
			if(Objects.nonNull(propertyEntity)) {
				
				boolean flag = true;
				List<PropertyEntity> filteredPropertyEntitiesByRadius = propertyDAO.selectByRadius(filterCiteriaModel);
					// Filter By Property Start Date and End Date
					if(propertyListHelper.filterByPropertyDate(propertyEntity, filterCiteriaModel)) {
						
						// Filter by location // Mandatory
						if(propertyListHelper.filterByLocation(propertyEntity, filteredPropertyEntitiesByRadius)) {
							// Filter by checkInDate // Mandatory
							// Filter by checkOutDate // Mandatory
							// Filter by roomModels // Mandatory
							Map<Boolean, Map<String, FilterRoomModel>> filterResult = propertyListHelper.filterBycheckInDate(propertyEntity, filterCiteriaModel);
							if (filterResult.containsKey(true)) {
								
								Map<String, FilterRoomModel> filteredRooms = filterResult.get(true);
								System.err.println("filteredRooms ==>> "+filteredRooms);
								// Filter By Rating
								if (!CollectionUtils.isEmpty(filterCiteriaModel.getRatings())) {
									if (!propertyListHelper.filterByRating(propertyEntity, filterCiteriaModel)) {
										flag = false;
									}
								}
								
								// Filter by amenitiesModels
								if (!CollectionUtils.isEmpty(filterCiteriaModel.getAmenitiesModels())) {
									if (!propertyListHelper.filterByAmmenities(propertyEntity, filterCiteriaModel)) {
										flag = false;
									}
								}
								
								
								// Filter by budgets
								if(!CollectionUtils.isEmpty(filterCiteriaModel.getBudgets())) {
									if (!propertyListHelper.filterByBudget(propertyEntity, filterCiteriaModel, filteredRooms)) {
										flag = false;
									}
								}
								
								
								// Filter by popularLocations
								if(!CollectionUtils.isEmpty(filterCiteriaModel.getPopularLocations())) {
									if (!propertyListHelper.filterByPopularLocation(propertyEntity, filterCiteriaModel)) {
										flag = false;
									}
								}
								
								
								// Filter by spaceRuleModels // Couple Friendly, Pet Friendly
								if(!CollectionUtils.isEmpty(filterCiteriaModel.getSpaceRuleModels())) {
									if (!propertyListHelper.filterBySpaceRule(propertyEntity, filterCiteriaModel)) {
										flag = false;
									}
								}
								
								
								// Filter by pgCategorySexModels // Male/Female
								if(!StringUtils.isBlank(filterCiteriaModel.getPgCategorySex())) {
									if (!propertyListHelper.filterBySex(propertyEntity, filterCiteriaModel)) {
										flag = false;
									}
								}
								
								if(flag) {
									propertyModel = propertyConverter.entityToModel(propertyEntity);
									
									//Calculate Price
									propertyListHelper.priceCalculationForRoomDetails(propertyEntity, filterCiteriaModel, filteredRooms, propertyModel);
									
									// Calculate Convenience
									ConvenienceModel convenienceModel = convenienceService.getActiveConvenienceModel();
									if (logger.isInfoEnabled()) {
										logger.info("convenienceModel ==>> "+convenienceModel);
									}
									System.err.println("convenienceModel ==>> "+convenienceModel);
									
									if (Objects.nonNull(convenienceModel)) {
										propertyModel.setConvenienceFee(convenienceModel.getAmount());
										propertyModel.setConvenienceGSTPercentage(convenienceModel.getGstPercentage());
										propertyModel.setConvenienceGSTAmount(String.valueOf(Math.round(Double.parseDouble(convenienceModel.getAmount()) * Double.parseDouble(convenienceModel.getGstPercentage()) / 100 * 100D) / 100D));
									} else {
										
										propertyModel.setConvenienceFee("0");
										propertyModel.setConvenienceGSTPercentage("0");
										propertyModel.setConvenienceGSTAmount("0");
									}
									
									// Setting Price Details in Key Value Pair
									setPriceDetails(propertyModel);
									
									// Setting Reviews for the property
									propertyModel.setUserReviewModels(fetchPropertyReviews(propertyModel.getPropertyId()));
									
									// Set Rating, Rating Text And Review Count
									propertyModel.setRating(propertyListHelper.getRatingAndReview(propertyEntity).get(0));
									propertyModel.setReviewCount(propertyListHelper.getRatingAndReview(propertyEntity).get(1));
									
									if(Double.parseDouble(propertyModel.getRating()) >= Double.parseDouble(messageUtil.getBundle("rating.key1"))) {
										propertyModel.setRatingText(messageUtil.getBundle("rating.value1"));
									} else if(Double.parseDouble(propertyModel.getRating()) >= Double.parseDouble(messageUtil.getBundle("rating.key2"))) {
										propertyModel.setRatingText(messageUtil.getBundle("rating.value2"));
									} else {
										propertyModel.setRatingText(messageUtil.getBundle("rating.value3"));
									}
									
									// Setting Host Details
									propertyModel.setUserModel(getUserDetails(propertyEntity.getHostVsAccountEntity().getUserId()));
									
									if(Objects.nonNull(userModel)) {
										setBookMark(propertyModel, userModel);
									}
									
									// TODO Analysis Text
									propertyModel.setAnalyticsText("");
									
								}
							} 
						} 
					} 
				}
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception in fetchPropertyDetails -- "+Util.errorToString(e));
			}
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("fetchPropertyDetails -- END");
		}
			
		return propertyModel;
	}
	
	private void setBookMark(Object object, UserModel userModel) {
		
		if (logger.isInfoEnabled()) {
			logger.info("setBookMark -- START");
		}
		
		String propertyId = null;
		if(object instanceof PropertyListViewModel) {
			PropertyListViewModel propertyListViewModel = (PropertyListViewModel) object;
			propertyId = propertyListViewModel.getPropertyId();
		} else if(object instanceof PropertyModel) {
			PropertyModel propertyModel = (PropertyModel) object;
			propertyId = propertyModel.getPropertyId();
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
			innerMap2.put("propertyId", propertyId);

			Map<String, Map<String, String>> outerMap2 = new LinkedHashMap<>();
			outerMap2.put("eq", innerMap2);

			alliasMap.put("propertyEntity", outerMap2);
			if(Objects.nonNull(wishlistDAO.fetchObjectBySubCiteria(alliasMap))) {
				
				if(object instanceof PropertyListViewModel) {
					PropertyListViewModel propertyListViewModel = (PropertyListViewModel) object;
					propertyListViewModel.setIsBookmark(true);
				} else if(object instanceof PropertyModel) {
					PropertyModel propertyModel = (PropertyModel) object;
					propertyModel.setIsBookmark(true);
				}
				
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
	
	@Override
	public List<BookingVsRoomModel> roomDetailsByOraRoomName(List<BookingVsRoomModel> bookingVsRoomModels) {

		if (logger.isInfoEnabled()) {
			logger.info("roomDetailsByOraRoomName -- START");
		}
		
		List<BookingVsRoomModel> bookingVsRoomModels2 = new ArrayList<>();
		for(BookingVsRoomModel input : bookingVsRoomModels) {
			BookingVsRoomModel bookingVsRoomModel = new BookingVsRoomModel();
			
			try {
				Map<String, String> innerMap1 = new LinkedHashMap<>();
				innerMap1.put("status", String.valueOf(Status.ACTIVE.ordinal()));
				innerMap1.put("oraRoomName", input.getOraRoomName());
		
				Map<String, Map<String, String>> outerMap1 = new LinkedHashMap<>();
				outerMap1.put("eq", innerMap1);
		
				Map<String, Map<String, Map<String, String>>> alliasMap = new LinkedHashMap<>();
				alliasMap.put(entitymanagerPackagesToScan+".RoomEntity", outerMap1);
				
				RoomEntity roomEntity = roomDAO.fetchObjectBySubCiteria(alliasMap);
				if(Objects.nonNull(roomEntity)) {
					bookingVsRoomModel.setTotalNumOfSharedBed(roomEntity.getNumOfBed());
					bookingVsRoomModel.setTotalNumOfSharedCot(roomEntity.getNumOfCot());
				}
				
			} catch (Exception e) {
				if (logger.isInfoEnabled()) {
					logger.info("Exception in roomDetailsByOraRoomName -- "+Util.errorToString(e));
				}
			}
			
			bookingVsRoomModels2.add(bookingVsRoomModel);
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("roomDetailsByOraRoomName -- END");
		}
		
		return bookingVsRoomModels2;
	}

	@Override
	public PropertyModel fetchPropertyById(String propertyId) {
		
		if (logger.isInfoEnabled()) {
			logger.info("fetchPropertyById -- START");
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("fetchPropertyById -- END");
		}
		
		return propertyConverter.entityToModel(propertyDAO.find(Long.valueOf(propertyId)));
	}

	@Override
	public Object budgets() {

		if (logger.isInfoEnabled()) {
			logger.info("budgets -- START");
		}
		
		Map<String, String> budgets = new LinkedHashMap<>();
		budgets.put(messageUtil.getBundle("budget.key1"), messageUtil.getBundle("budget.value1"));
		budgets.put(messageUtil.getBundle("budget.key2"), messageUtil.getBundle("budget.value2"));
		budgets.put(messageUtil.getBundle("budget.key3"), messageUtil.getBundle("budget.value3"));
		budgets.put(messageUtil.getBundle("budget.key4"), messageUtil.getBundle("budget.value4"));
		budgets.put(messageUtil.getBundle("budget.key5"), messageUtil.getBundle("budget.value5"));
		budgets.put(messageUtil.getBundle("budget.key6"), messageUtil.getBundle("budget.value6"));
		
		if (logger.isInfoEnabled()) {
			logger.info("budgets -- END");
		}
		
		return budgets;
	}

	@Override
	public Object ratings() {

		if (logger.isInfoEnabled()) {
			logger.info("ratings -- START");
		}
		
		Map<String, String> ratings = new LinkedHashMap<>();
		ratings.put(messageUtil.getBundle("rating.key1"), messageUtil.getBundle("rating.value1"));
		ratings.put(messageUtil.getBundle("rating.key2"), messageUtil.getBundle("rating.value2"));
		ratings.put(messageUtil.getBundle("rating.key3"), messageUtil.getBundle("rating.value3"));
		
		if (logger.isInfoEnabled()) {
			logger.info("ratings -- END");
		}
		
		return ratings;
	}
	
	@Override
	public UserModel getUserDetails(String userId) throws FormExceptions {

		if (logger.isInfoEnabled()) {
			logger.info("getUserDetails -- START");
		}

		Map<String, Exception> exceptions = new LinkedHashMap<>();
		UserModel userModel = null;
		try {
			ResponseModel responseModel = restTemplate.getForObject(messageUtil.getBundle("auth.server.url") + "fetch-user-by-id?userId=" + userId, ResponseModel.class);
			Gson gson = new Gson();
			String jsonString = gson.toJson(responseModel.getResponseBody());
			userModel = gson.fromJson(jsonString, UserModel.class);
			if (Objects.isNull(userModel)) {
				exceptions.put(messageUtil.getBundle("user.invalid.code"), new Exception(messageUtil.getBundle("user.invalid.message")));
			}

			if (logger.isInfoEnabled()) {
				logger.info("userModel ==>> " + userModel);
			}
			
			System.out.println("userModel ==>> " + userModel);
			
		} catch (Exception e) {
			e.printStackTrace();
			// Disabled the below line to pass the Token Validation
			exceptions.put(messageUtil.getBundle("user.invalid.code"), new Exception(messageUtil.getBundle("user.invalid.message")));
		}

		if (exceptions.size() > 0)
			throw new FormExceptions(exceptions);

		if (logger.isInfoEnabled()) {
			logger.info("getUserDetails -- END");
		}

		return userModel;
	}
	
	private List<UserReviewModel> fetchPropertyReviews(String propertyId) {
		
		if (logger.isInfoEnabled()) {
			logger.info("fetchPropertyReviews -- START");
		}
		
		List<UserReviewModel> userReviewModels = null;
		try {
			UserReviewModel userReviewModel = new UserReviewModel();
			userReviewModel.setPropertyId(propertyId);
			userReviewModel.setUserTypeId(String.valueOf(Status.INACTIVE.ordinal()));
			ResponseModel responseModel = restTemplate.postForObject(messageUtil.getBundle("review.server.url") +"fetch-review", userReviewModel, ResponseModel.class);
			Gson gson = new Gson();
			String jsonString = gson.toJson(responseModel.getResponseBody());
			gson = new Gson();
			Type listType = new TypeToken<List<UserReviewModel>>() {}.getType();
			userReviewModels = gson.fromJson(jsonString, listType);
			
			if (logger.isInfoEnabled()) {
				logger.info("userReviewModels ==>> "+userReviewModels);
			}
			
			System.err.println("userReviewModels ==>> "+userReviewModels);
			
			if(!CollectionUtils.isEmpty(userReviewModels)) {
				for(UserReviewModel userReviewModel2 : userReviewModels) {
					userReviewModel2.setUserModel(getUserDetails(userReviewModel2.getUserId()));
					// Calculate Rating
					Map<String, String> ratingTypes = new LinkedHashMap<>();
					if(!CollectionUtils.isEmpty(userReviewModel2.getBookingVsRatings())) {
						for(BookingVsRatingModel bookingVsRatingModel : userReviewModel2.getBookingVsRatings()) {
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
						
						Double totalRating = 0.0D;
						for (Map.Entry<String,String> entry : ratingTypes.entrySet()) { 
							totalRating = totalRating + Double.parseDouble(entry.getValue());
						}
						
						if (!CollectionUtils.isEmpty(ratingTypes)) {
							userReviewModel2.setUserRating(String.valueOf(Math.round(totalRating / ratingTypes.size() / userReviewModels.size()) * 100D / 100D)); // Rating
						}
					} else {
						userReviewModel2.setUserRating("0"); // Rating
					}
				}
			}
			
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception in fetchPropertyReviews -- "+Util.errorToString(e));
			}
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("fetchPropertyReviews -- END");
		}
		
		return userReviewModels;
	}
	
	private void setPriceDetails(PropertyModel propertyModel) {
	
		if (logger.isInfoEnabled()) {
			logger.info("setPriceDetails -- START");
		}
		
		Map<String, String> priceDetails = new LinkedHashMap<>();
		priceDetails.put("totalAmount",  String.valueOf(Math.round(Double.parseDouble(propertyModel.getTotalAmount())) * 100D / 100D));
		if(!StringUtils.isEmpty(propertyModel.getPropertyOffer())) {
			priceDetails.put("propertyOffer",  String.valueOf(Math.round(Double.parseDouble(propertyModel.getPropertyOffer())) * 100D / 100D));;
		} else {
			priceDetails.put("propertyOffer", "0.0");
		}
		priceDetails.put("discount", String.valueOf(Math.round( Double.parseDouble(propertyModel.getTotalDiscount())) * 100D / 100D));
		priceDetails.put("convenienceFee", propertyModel.getConvenienceFee());
		priceDetails.put("convenienceGSTPercentage", propertyModel.getConvenienceGSTPercentage());
		priceDetails.put("convenienceGSTAmount", propertyModel.getConvenienceGSTAmount());
		priceDetails.put("amountPayable",  String.valueOf(Math.round(Double.parseDouble(propertyModel.getAmountPayable())) * 100D / 100D));
		Double finalPrice = Double.parseDouble(propertyModel.getAmountPayable()) + Double.parseDouble(propertyModel.getConvenienceFee()) + Double.parseDouble(propertyModel.getConvenienceGSTAmount()) - Double.parseDouble(propertyModel.getPropertyOffer());
		finalPrice = Math.round(finalPrice) * 100D / 100D;
		priceDetails.put("finalPrice", String.valueOf(finalPrice));
		propertyModel.setPriceDetails(priceDetails);
		System.out.println("priceDetails ==>> "+priceDetails);
		
		if (logger.isInfoEnabled()) {
			logger.info("setPriceDetails -- END");
		}
	}

	@Override
	public PropertyModel fetchPriceDetails(FilterCiteriaModel filterCiteriaModel) throws FormExceptions {

		if (logger.isInfoEnabled()) {
			logger.info("fetchPriceDetails -- START");
		}
		
		UserModel userModel = propertyListValidation.validateFetchPriceDetails(filterCiteriaModel);
		PropertyModel propertyModel = null;
		Map<String, Exception> exceptions = new LinkedHashMap<>();
		try {
			Map<String, String> innerMap1 = new LinkedHashMap<>();
			innerMap1.put("status", String.valueOf(Status.ACTIVE.ordinal()));
			innerMap1.put("propertyId", String.valueOf(Long.parseLong(filterCiteriaModel.getPropertyId())));
	
			Map<String, Map<String, String>> outerMap1 = new LinkedHashMap<>();
			outerMap1.put("eq", innerMap1);
	
			Map<String, Map<String, Map<String, String>>> alliasMap = new LinkedHashMap<>();
			alliasMap.put(entitymanagerPackagesToScan+".PropertyEntity", outerMap1);
			
			Map<String, String> innerMap2 = new LinkedHashMap<>();
			innerMap2.put("propertyTypeId", filterCiteriaModel.getPropertyTypeId());

			Map<String, Map<String, String>> outerMap2 = new LinkedHashMap<>();
			outerMap2.put("eq", innerMap2);

			alliasMap.put("propertyTypeEntity", outerMap2);
			
			PropertyEntity propertyEntity = propertyDAO.fetchObjectBySubCiteria(alliasMap);
			if(Objects.nonNull(propertyEntity)) {
				
				boolean flag = true;
				List<PropertyEntity> filteredPropertyEntitiesByRadius = propertyDAO.selectByRadius(filterCiteriaModel);
					// Filter By Property Start Date and End Date
					if(propertyListHelper.filterByPropertyDate(propertyEntity, filterCiteriaModel)) {
						
						// Filter by location // Mandatory
						if(propertyListHelper.filterByLocation(propertyEntity, filteredPropertyEntitiesByRadius)) {
							// Filter by checkInDate // Mandatory
							// Filter by checkOutDate // Mandatory
							// Filter by roomModels // Mandatory
							if (propertyListHelper.filterBycheckInDateForBooking(propertyEntity, filterCiteriaModel)) {
								
//								// Filter By Rating
//								if (!CollectionUtils.isEmpty(filterCiteriaModel.getRatings())) {
//									if (!propertyListHelper.filterByRating(propertyEntity, filterCiteriaModel)) {
//										flag = false;
//									}
//								}
//								
//								// Filter by amenitiesModels
//								if (!CollectionUtils.isEmpty(filterCiteriaModel.getAmenitiesModels())) {
//									if (!propertyListHelper.filterByAmmenities(propertyEntity, filterCiteriaModel)) {
//										flag = false;
//									}
//								}
//								
//								
//								// Filter by budgets
//								if(!CollectionUtils.isEmpty(filterCiteriaModel.getBudgets())) {
//									if (!propertyListHelper.filterByBudget(propertyEntity, filterCiteriaModel, filteredRooms)) {
//										flag = false;
//									}
//								}
//								
//								
//								// Filter by popularLocations
//								if(!CollectionUtils.isEmpty(filterCiteriaModel.getPopularLocations())) {
//									if (!propertyListHelper.filterByPopularLocation(propertyEntity, filterCiteriaModel)) {
//										flag = false;
//									}
//								}
//								
//								
//								// Filter by spaceRuleModels // Couple Friendly, Pet Friendly
//								if(!CollectionUtils.isEmpty(filterCiteriaModel.getSpaceRuleModels())) {
//									if (!propertyListHelper.filterBySpaceRule(propertyEntity, filterCiteriaModel)) {
//										flag = false;
//									}
//								}
//								
//								
//								// Filter by pgCategorySexModels // Male/Female
//								if(!StringUtils.isBlank(filterCiteriaModel.getPgCategorySex())) {
//									if (!propertyListHelper.filterBySex(propertyEntity, filterCiteriaModel)) {
//										flag = false;
//									}
//								}
								
								if(flag) {
									
									
									propertyModel = propertyConverter.entityToModel(propertyEntity);
									
									//Calculate Price
									propertyListHelper.priceCalculationForBooking(propertyEntity, filterCiteriaModel, propertyModel);
									
									// Calculate Convenience
									ConvenienceModel convenienceModel = convenienceService.getActiveConvenienceModel();
									if (logger.isInfoEnabled()) {
										logger.info("convenienceModel ==>> "+convenienceModel);
									}
									System.err.println("convenienceModel ==>> "+convenienceModel);
									
									if (Objects.nonNull(convenienceModel)) {
										propertyModel.setConvenienceFee(convenienceModel.getAmount());
										propertyModel.setConvenienceGSTPercentage(convenienceModel.getGstPercentage());
										propertyModel.setConvenienceGSTAmount(String.valueOf(Math.round(Double.parseDouble(convenienceModel.getAmount()) * Double.parseDouble(convenienceModel.getGstPercentage()) / 100 * 100D) / 100D));
									} else {
										
										propertyModel.setConvenienceFee("0");
										propertyModel.setConvenienceGSTPercentage("0");
										propertyModel.setConvenienceGSTAmount("0");
									}
									
									// Setting Price Details in Key Value Pair
									setPriceDetails(propertyModel);
									
									// Setting Reviews for the property
									propertyModel.setUserReviewModels(fetchPropertyReviews(propertyModel.getPropertyId()));
									
									// Set Rating, Rating Text And Review Count
									propertyModel.setRating(propertyListHelper.getRatingAndReview(propertyEntity).get(0));
									propertyModel.setReviewCount(propertyListHelper.getRatingAndReview(propertyEntity).get(1));
									
									if(Double.parseDouble(propertyModel.getRating()) >= Double.parseDouble(messageUtil.getBundle("rating.key1"))) {
										propertyModel.setRatingText(messageUtil.getBundle("rating.value1"));
									} else if(Double.parseDouble(propertyModel.getRating()) >= Double.parseDouble(messageUtil.getBundle("rating.key2"))) {
										propertyModel.setRatingText(messageUtil.getBundle("rating.value2"));
									} else {
										propertyModel.setRatingText(messageUtil.getBundle("rating.value3"));
									}
									
									// Setting Host Details
									propertyModel.setUserModel(getUserDetails(propertyEntity.getHostVsAccountEntity().getUserId()));
									
									if(Objects.nonNull(userModel)) {
										setBookMark(propertyModel, userModel);
									}
									
									// TODO Analysis Text
									propertyModel.setAnalyticsText("");
									
								} else {
									exceptions.put(messageUtil.getBundle("property.notavailable.code"), new Exception(messageUtil.getBundle("property.notavailable.message")));
									throw new FormExceptions(exceptions);
								}
							} 
						} 
					} 
				}
		} catch (FormExceptions fe) {
			throw fe;
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception in fetchPriceDetails -- "+Util.errorToString(e));
			}
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("fetchPriceDetails -- END");
		}
		
		return propertyModel;
	}

}