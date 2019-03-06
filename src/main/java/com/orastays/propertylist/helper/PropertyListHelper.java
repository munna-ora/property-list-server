package com.orastays.propertylist.helper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orastays.propertylist.converter.AmenitiesConverter;
import com.orastays.propertylist.converter.SpaceRuleConverter;
import com.orastays.propertylist.entity.OfferEntity;
import com.orastays.propertylist.entity.PropertyEntity;
import com.orastays.propertylist.entity.PropertyVsPriceDropEntity;
import com.orastays.propertylist.entity.PropertyVsSpaceRuleEntity;
import com.orastays.propertylist.entity.RoomEntity;
import com.orastays.propertylist.entity.RoomVsAmenitiesEntity;
import com.orastays.propertylist.entity.RoomVsMealEntity;
import com.orastays.propertylist.entity.RoomVsOfferEntity;
import com.orastays.propertylist.exceptions.FormExceptions;
import com.orastays.propertylist.model.AmenitiesModel;
import com.orastays.propertylist.model.FilterCiteriaModel;
import com.orastays.propertylist.model.FilterRoomModel;
import com.orastays.propertylist.model.GstSlabModel;
import com.orastays.propertylist.model.PropertyListViewModel;
import com.orastays.propertylist.model.PropertyModel;
import com.orastays.propertylist.model.ResponseModel;
import com.orastays.propertylist.model.RoomModel;
import com.orastays.propertylist.model.SpaceRuleModel;
import com.orastays.propertylist.model.booking.BookingModel;
import com.orastays.propertylist.model.booking.BookingVsRoomModel;
import com.orastays.propertylist.model.review.BookingVsRatingModel;
import com.orastays.propertylist.model.review.UserReviewModel;
import com.orastays.propertylist.service.GstSlabService;

@Component
public class PropertyListHelper {

	private static final Logger logger = LogManager.getLogger(PropertyListHelper.class);
	
	@Autowired
	protected RestTemplate restTemplate;

	@Autowired
	protected MessageUtil messageUtil;
	
	@Autowired
	protected AmenitiesConverter amenitiesConverter;
	
	@Autowired
	protected SpaceRuleConverter spaceRuleConverter;
	
	@Autowired
	protected GstSlabService gstSlabService;
	
	public Boolean filterByPropertyDate(PropertyEntity propertyEntity, FilterCiteriaModel filterCiteriaModel) {
		
		if (logger.isInfoEnabled()) {
			logger.info("filterByPropertyDate -- START");
		}
		
		boolean flag = false;
		if(Util.getDayDiff(filterCiteriaModel.getCheckInDate(), propertyEntity.getStartDate()) <= 0 && Util.getDayDiff(filterCiteriaModel.getCheckOutDate(), propertyEntity.getEndDate()) >= 0) {
			flag = true;
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("filterByPropertyDate -- END");
		}
		
		return flag;
	}
	
	public Boolean filterByLocation(PropertyEntity propertyEntity, List<PropertyEntity> filteredPropertyEntitiesByRadius) {
		
		if (logger.isInfoEnabled()) {
			logger.info("filterByLocation -- START");
		}
		
		boolean flag = false;
		
		PropertyEntity propertyEntity2 = filteredPropertyEntitiesByRadius.parallelStream().filter(p -> StringUtils.equals(String.valueOf(p.getPropertyId()), String.valueOf(propertyEntity.getPropertyId()))).findAny().orElse(null);
		if(Objects.nonNull(propertyEntity2)) {
			flag = true;
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("filterByLocation -- END");
		}
		
		return flag;
	}
	
	public Boolean filterByRating(PropertyEntity propertyEntity, FilterCiteriaModel filterCiteriaModel) {
		
		if (logger.isInfoEnabled()) {
			logger.info("filterByRating -- START");
		}
		
		boolean flag = false;
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
				List<String> ratingsInput = filterCiteriaModel.getRatings();
				List<String> ratings = new ArrayList<>();
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
				
				Long totalRating = 0L;
				for (Map.Entry<String,String> entry : ratingTypes.entrySet()) { 
					totalRating = totalRating + Long.parseLong(entry.getValue());
				}
				
				ratings.add(0, String.valueOf(Math.round(totalRating / ratingTypes.size() / userReviewModels.size()) * 100D / 100D)); // Rating
				for(String input : ratingsInput) {
					if(Long.parseLong(input) >= Long.parseLong(ratings.get(0))) {
						flag = true;
						break;
					}
				}
			}
			
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception in filterByRating -- "+Util.errorToString(e));
			}
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("filterByRating -- END");
		}
		
		return flag;
	}
	
	public Boolean filterByAmmenities(PropertyEntity propertyEntity, FilterCiteriaModel filterCiteriaModel) {
			
		if (logger.isInfoEnabled()) {
			logger.info("filterByAmmenities -- START");
		}
		
		boolean flag = false;
		Set<String> amenitiesModels = new LinkedHashSet<>();
		if(!CollectionUtils.isEmpty(propertyEntity.getRoomEntities())) {
			propertyEntity.getRoomEntities().parallelStream().forEach(roomEntity -> {
			//for(RoomEntity roomEntity : propertyEntity.getRoomEntities()) {
				if(Objects.nonNull(roomEntity)) {
					if(!CollectionUtils.isEmpty(roomEntity.getRoomVsAmenitiesEntities())) {
						for(RoomVsAmenitiesEntity roomVsAmenitiesEntity : roomEntity.getRoomVsAmenitiesEntities()) {
							amenitiesModels.add(amenitiesConverter.entityToModel(roomVsAmenitiesEntity.getAmenitiesEntity()).getAminitiesId());
						}
					}
				}
			});
		}
		
		List<Boolean> count = new ArrayList<>();
		for(AmenitiesModel amenitiesModel : filterCiteriaModel.getAmenitiesModels()) {
			if(amenitiesModels.contains(amenitiesModel.getAminitiesId())) {
				count.add(true);
			} else {
				count.add(false);
			}
		}
		
		if(!count.contains(false)) {
			flag = true;
		}
		
		
		if (logger.isInfoEnabled()) {
			logger.info("filterByAmmenities -- END");
		}
		
		return flag;
	}
	
	public Boolean filterByPopularLocation(PropertyEntity propertyEntity, FilterCiteriaModel filterCiteriaModel) {
		
		if (logger.isInfoEnabled()) {
			logger.info("filterByPopularLocation -- START");
		}
		
		boolean flag = false;
		
		if (logger.isInfoEnabled()) {
			logger.info("filterByPopularLocation -- END");
		}
		
		return flag;
	}
	
	public Boolean filterBySpaceRule(PropertyEntity propertyEntity, FilterCiteriaModel filterCiteriaModel) {
		
		if (logger.isInfoEnabled()) {
			logger.info("filterBySpaceRule -- START");
		}
		
		boolean flag = false;
		List<Boolean> count = new ArrayList<>();
		if(!CollectionUtils.isEmpty(propertyEntity.getPropertyVsSpaceRuleEntities())) {
			Set<Long> spruleId = new LinkedHashSet<>();
			for(PropertyVsSpaceRuleEntity propertyVsSpaceRuleEntity : propertyEntity.getPropertyVsSpaceRuleEntities()) {
				if(StringUtils.equals(propertyVsSpaceRuleEntity.getAnswer(), PropertyListConstant.STR_Y)) {
					spruleId.add(propertyVsSpaceRuleEntity.getSpaceRuleEntity().getSpruleId());
				}
			}
			
			for(SpaceRuleModel spaceRuleModel : filterCiteriaModel.getSpaceRuleModels()) {
				if(spruleId.contains(Long.valueOf(spaceRuleModel.getSpruleId()))) {
					count.add(true);
				} else {
					count.add(false);
				}
			}
		}
		
		if(!count.contains(false)) {
			flag = true;
		}
		
		
		if (logger.isInfoEnabled()) {
			logger.info("filterBySpaceRule -- END");
		}
		
		return flag;
	}
	
	public Boolean filterBySex(PropertyEntity propertyEntity, FilterCiteriaModel filterCiteriaModel) {
		
		if (logger.isInfoEnabled()) {
			logger.info("filterBySex -- START");
		}
		
		boolean flag = false;
		if(StringUtils.equals(String.valueOf(propertyEntity.getSexCategory()), String.valueOf(Sex.BOTH))) {
			flag = true;
		} else if(StringUtils.equals(filterCiteriaModel.getPgCategorySex(), String.valueOf(propertyEntity.getSexCategory()))) {
			flag = true;
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("filterBySex -- END");
		}
		
		return flag;
	}
	
	public Boolean filterByBudget(PropertyEntity propertyEntity, FilterCiteriaModel filterCiteriaModel, Map<String, FilterRoomModel> filteredRooms) {
		
		if (logger.isInfoEnabled()) {
			logger.info("filterByBudget -- START");
		}
		
		boolean flag = false;
		List<String> prices = priceCalculation(propertyEntity, filterCiteriaModel, filteredRooms);
		Double price = Double.parseDouble(prices.get(1));
		for(String budget : filterCiteriaModel.getBudgets()) {
			Double start = Double.parseDouble(budget.split("-")[0]);
			Double end = Double.parseDouble(budget.split("-")[1]);
			if(price >= start && price <= end) {
				flag = true;
				break;
			}
		}
		
		
		if (logger.isInfoEnabled()) {
			logger.info("filterByBudget -- END");
		}
		
		return flag;
	}
	
	public Map<Boolean, Map<String, FilterRoomModel>> filterBycheckInDate(PropertyEntity propertyEntity, FilterCiteriaModel filterCiteriaModel) {
		
		if (logger.isInfoEnabled()) {
			logger.info("filterBycheckInDate -- START");
		}
		
		Boolean flag = false;
		Map<Boolean, Map<String, FilterRoomModel>> filterResult = new HashMap<>();
		Map<String, FilterRoomModel> filteredRooms = new ConcurrentHashMap<>();
		try {
			
			int noOfGuest = Integer.parseInt(filterCiteriaModel.getNoOfGuest());
			System.err.println("noOfGuest ==>> "+noOfGuest);
			if (StringUtils.equals(filterCiteriaModel.getStayType(), PropertyListConstant.SHARED)) { // Customer Wants SHARED Rooms
				
				if(!CollectionUtils.isEmpty(propertyEntity.getRoomEntities())) {
					//propertyEntity.getRoomEntities().parallelStream().forEach(roomEntity -> {
					for(RoomEntity roomEntity : propertyEntity.getRoomEntities()) {
						
						if(roomEntity.getStatus() == Status.ACTIVE.ordinal()) { // Fetching only ACTIVE Rooms
							System.err.println("roomEntity ==>> "+roomEntity);
							if(StringUtils.equals(roomEntity.getAccomodationName(), Accommodation.SHARED.name())) { //shared
								
								FilterRoomModel filterRoomModel = new FilterRoomModel();
								filterRoomModel.setRoomId(roomEntity.getRoomId());
								filterRoomModel.setRoomEntity(roomEntity);
								if(Integer.parseInt(roomEntity.getNumOfBed()) >= noOfGuest && noOfGuest > 0) {
									filterRoomModel.setBedAvailable(Integer.parseInt(roomEntity.getNumOfBed()));
									filterRoomModel.setBedAllocated(noOfGuest);
									noOfGuest = 0;
								} else if(Integer.parseInt(roomEntity.getNumOfBed()) < noOfGuest && noOfGuest > 0) {
									filterRoomModel.setBedAvailable(Integer.parseInt(roomEntity.getNumOfBed()));
									filterRoomModel.setBedAllocated(Integer.parseInt(roomEntity.getNumOfBed()));
									noOfGuest = noOfGuest - Integer.parseInt(roomEntity.getNumOfBed());
								} else {
									filterRoomModel.setBedAvailable(Integer.parseInt(roomEntity.getNumOfBed()));
									filterRoomModel.setBedAllocated(0);
								}
								
								Double hostBasePrice = calculateHostBasePrice(propertyEntity, filterCiteriaModel, roomEntity);
								System.out.println("hostBasePrice ==>> "+hostBasePrice);
								Double oraMarkUp = Double.parseDouble(roomEntity.getOraPercentage());
								System.err.println("oraMarkUp ==>> "+oraMarkUp);
								Double oraPrice = hostBasePrice + (hostBasePrice * oraMarkUp / 100);
								System.out.println("oraPrice ==>> "+oraPrice);
								// Room Vs ORA Discount Percentage
								if (!StringUtils.isBlank(roomEntity.getOraDiscountPercentage())) {
									Double oraDiscount = Double.parseDouble(roomEntity.getOraDiscountPercentage()) * hostBasePrice / 100;
									System.err.println("oraDiscount ==>> "+oraDiscount);
									oraPrice = oraPrice - oraDiscount;
								}
								filterRoomModel.setTotalPrice(oraPrice * filterRoomModel.getBedAllocated());
								
								filteredRooms.put(roomEntity.getOraRoomName(), filterRoomModel);
							}
						}
					}
				}
				
				Map<String, List<BookingVsRoomModel>> roomByOraName = callBookingService(String.valueOf(propertyEntity.getPropertyId()), filterCiteriaModel.getCheckInDate(), Accommodation.SHARED.name());
				if(CollectionUtils.isEmpty(roomByOraName)) {
					int guestCanHave = 0;
					for (Map.Entry<String, FilterRoomModel> entry : filteredRooms.entrySet()) {
						guestCanHave = guestCanHave + entry.getValue().getBedAvailable();
						entry.getValue().setNumOfBedBooked("0");
						entry.getValue().setNumOfCotBooked("0");
				    }
					
					if(guestCanHave >= noOfGuest) {
						flag = true;
					}
					
				} else {
					int guestCanHave = 0;
					for (Map.Entry<String, FilterRoomModel> entry : filteredRooms.entrySet()) {
						if(roomByOraName.containsKey(entry.getKey())) {
							int maxBedBooked = 0;
							int maxCotBooked = 0;
							List<BookingVsRoomModel> roomBooked = roomByOraName.get(entry.getKey());
							for(BookingVsRoomModel bookingVsRoomModel : roomBooked) {
								if(maxBedBooked < Integer.parseInt(bookingVsRoomModel.getNumOfSharedBed())) {
									maxBedBooked = Integer.parseInt(bookingVsRoomModel.getNumOfSharedBed()); // Getting Highest Number of bed booked for that room
								}
								
								if(maxCotBooked < Integer.parseInt(bookingVsRoomModel.getNumOfSharedCot())) {
									maxCotBooked = Integer.parseInt(bookingVsRoomModel.getNumOfSharedCot()); // Getting Highest Number of cot booked for that room
								}
							}
							
							entry.getValue().setNumOfBedBooked(String.valueOf(maxBedBooked));
							entry.getValue().setNumOfCotBooked(String.valueOf(maxCotBooked));
							
							if((entry.getValue().getBedAvailable() - maxBedBooked) > 0) { // Min 1 Bed Available
								entry.getValue().setBedAvailable(entry.getValue().getBedAvailable() - maxBedBooked);
								entry.getValue().setBedAllocated(noOfGuest - entry.getValue().getBedAvailable() - maxBedBooked);
								filteredRooms.replace(entry.getKey(), entry.getValue());
								guestCanHave = guestCanHave + entry.getValue().getBedAvailable() - maxBedBooked;
							} else {
								filteredRooms.remove(entry.getKey());
							}
							
						} else {
							guestCanHave = guestCanHave + entry.getValue().getBedAvailable();
						}
				    }
					
					if(guestCanHave >= noOfGuest) {
						flag = true;
					}
				}
				
			} else { // Customer Wants PRIVATE Rooms
				
				Map<String, List<BookingVsRoomModel>> roomByOraName = callBookingService(String.valueOf(propertyEntity.getPropertyId()), filterCiteriaModel.getCheckInDate(), Accommodation.PRIVATE.name());
				AtomicInteger guestCanHave = new AtomicInteger(0);
				if(!CollectionUtils.isEmpty(propertyEntity.getRoomEntities())) {
					propertyEntity.getRoomEntities().parallelStream().forEach(roomEntity -> {
					//for(RoomEntity roomEntity : propertyEntity.getRoomEntities()) {
						
						if(roomEntity.getStatus() == Status.ACTIVE.ordinal()) { // Fetching only ACTIVE Rooms
							System.err.println("roomEntity ==>> "+roomEntity);
							if(StringUtils.equals(roomEntity.getAccomodationName(), Accommodation.PRIVATE.name())) { //private
								if(CollectionUtils.isEmpty(roomByOraName) && !roomByOraName.containsKey(roomEntity.getOraRoomName())) {
									guestCanHave.addAndGet(Integer.parseInt(roomEntity.getNoOfGuest()));
									FilterRoomModel filterRoomModel = new FilterRoomModel();
									filterRoomModel.setRoomId(roomEntity.getRoomId());
									filterRoomModel.setRoomEntity(roomEntity);
									filterRoomModel.setNumOfAdult(Integer.parseInt(roomEntity.getNoOfGuest()));
									filterRoomModel.setNumOfChild(Integer.parseInt(roomEntity.getNoOfChild()));
									
									Double hostBasePrice = calculateHostBasePrice(propertyEntity, filterCiteriaModel, roomEntity);
									System.out.println("hostBasePrice ==>> "+hostBasePrice);
									Double oraMarkUp = Double.parseDouble(roomEntity.getOraPercentage());
									System.err.println("oraMarkUp ==>> "+oraMarkUp);
									Double oraPrice = hostBasePrice + (hostBasePrice * oraMarkUp / 100);
									System.out.println("oraPrice ==>> "+oraPrice);
									// Room Vs ORA Discount Percentage
									if (!StringUtils.isBlank(roomEntity.getOraDiscountPercentage())) {
										Double oraDiscount = Double.parseDouble(roomEntity.getOraDiscountPercentage()) * hostBasePrice / 100;
										System.err.println("oraDiscount ==>> "+oraDiscount);
										oraPrice = oraPrice - oraDiscount;
									}
									filterRoomModel.setTotalPrice(oraPrice);
									
									filteredRooms.put(roomEntity.getOraRoomName(), filterRoomModel);
								}
							}
						}
					});
				}
				
				if(guestCanHave.get() >= noOfGuest) {
					flag = true;
				}
			}
			
			filterResult.put(flag, filteredRooms);
			
		} catch (Exception e) {
			e.printStackTrace();
			if (logger.isInfoEnabled()) {
				logger.info("Exception in filterBycheckInDate -- "+Util.errorToString(e));
			}
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("filterBycheckInDate -- END");
		}
		
		return filterResult;
	}
	
	private Map<String, List<BookingVsRoomModel>> callBookingService(String propertyId, String checkInDate, String accomodationType) {
		
		if (logger.isInfoEnabled()) {
			logger.info("callBookingService -- START");
		}
		
		Map<String, List<BookingVsRoomModel>> roomByOraName = new LinkedHashMap<>();
		try {
			BookingModel bookingModel = new BookingModel();
			bookingModel.setPropertyId(propertyId);
			bookingModel.setCheckinDate(checkInDate);
			bookingModel.setAccomodationType(accomodationType);
			ResponseModel responseModel = restTemplate.postForObject(messageUtil.getBundle("booking.server.url") + "get-bookings", bookingModel, ResponseModel.class);
			Gson gson = new Gson();
			String jsonString = gson.toJson(responseModel.getResponseBody());
			gson = new Gson();
			Type listType = new TypeToken<List<BookingModel>>() {}.getType();
			List<BookingModel> bookingModels = gson.fromJson(jsonString, listType);

			
			if (!CollectionUtils.isEmpty(bookingModels)) {

				List<BookingVsRoomModel> bookingVsRoomModels = new ArrayList<>();
				for (BookingModel bookingModel2 : bookingModels) {
					if (Objects.nonNull(bookingModel2) && !CollectionUtils.isEmpty(bookingModel2.getBookingVsRooms())) {
						bookingVsRoomModels.addAll(bookingModel2.getBookingVsRooms());
					}
				}

				if (!CollectionUtils.isEmpty(bookingVsRoomModels)) {
					for (BookingVsRoomModel bookingVsRoomModel : bookingVsRoomModels) {
						if (roomByOraName.isEmpty()) { // First Time
							List<BookingVsRoomModel> mapBookingVsRoomModels = new ArrayList<>();
							mapBookingVsRoomModels.add(bookingVsRoomModel);
							roomByOraName.put(bookingVsRoomModel.getOraRoomName(), mapBookingVsRoomModels);
						} else {
							if (roomByOraName.containsKey(bookingVsRoomModel.getOraRoomName())) { // KEY Present
								List<BookingVsRoomModel> mapBookingVsRoomModels = roomByOraName.get(bookingVsRoomModel.getOraRoomName());
								mapBookingVsRoomModels.add(bookingVsRoomModel);
								roomByOraName.put(bookingVsRoomModel.getOraRoomName(), mapBookingVsRoomModels);
							} else { // KEY not present
								List<BookingVsRoomModel> mapBookingVsRoomModels = new ArrayList<>();
								mapBookingVsRoomModels.add(bookingVsRoomModel);
								roomByOraName.put(bookingVsRoomModel.getOraRoomName(), mapBookingVsRoomModels);
							}
						}
					}
				}

			}
		} catch(HttpClientErrorException e) {
			System.err.println(e.getRawStatusCode());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		System.err.println("roomByOraName ==>> "+roomByOraName);
		
		if (logger.isInfoEnabled()) {
			logger.info("callBookingService -- END");
		}
		
		return roomByOraName;
	}
	
	private List<String> priceCalculation(PropertyEntity propertyEntity, FilterCiteriaModel filterCiteriaModel, Map<String, FilterRoomModel> filteredRooms) {
		
		if (logger.isInfoEnabled()) {
			logger.info("priceCalculation -- START");
		}
		
		List<String> prices = new ArrayList<>();
		int numOfDays = Util.getDayDiff(filterCiteriaModel.getCheckInDate(), filterCiteriaModel.getCheckOutDate());
		Double hostBasePrice = 0.0D;
		Double hostDiscount = 0.0D;
		Double hostPrice = 0.0D;
		Double oraMarkUp = 0.0D;
		Double oraPrice = 0.0D;
		Double oraDiscount = 0.0D;
		Double oraFinalPrice = 0.0D;
		Double offerPrice = 0.0D;
		Set<OfferEntity> offerEntities = new HashSet<>();
		
		Map<RoomEntity, Double> oraPriceCount = new LinkedHashMap<>();
		Map<RoomEntity, Double> oraFinalPriceCount = new LinkedHashMap<>();
		for (Map.Entry<String, FilterRoomModel> entry : filteredRooms.entrySet()) {
			
			hostBasePrice = calculateHostBasePrice(propertyEntity, filterCiteriaModel, entry.getValue().getRoomEntity());
			System.out.println("hostBasePrice ==>> "+hostBasePrice);
			oraMarkUp = Double.parseDouble(entry.getValue().getRoomEntity().getOraPercentage());
			System.err.println("oraMarkUp ==>> "+oraMarkUp);
			oraPrice = hostBasePrice + (hostBasePrice * oraMarkUp / 100);
			System.out.println("oraPrice ==>> "+oraPrice);
			// Room Vs ORA Discount Percentage
			if (!StringUtils.isBlank(entry.getValue().getRoomEntity().getOraDiscountPercentage())) {
				oraDiscount = Double.parseDouble(entry.getValue().getRoomEntity().getOraDiscountPercentage()) * hostBasePrice / 100;
			}
			System.err.println("oraDiscount ==>> "+oraDiscount);
			
			Double priceDropDiscount = 0.0D;
			
			// Check Pricedrop if any
			if(Util.getDateDiff1(filterCiteriaModel.getCheckInDate()) == 0) { // Current Date
				if(!CollectionUtils.isEmpty(propertyEntity.getPropertyVsPriceDropEntities())) { // Price Drop Present
					int hourDifference = Util.getMinuteDiff(Util.getCurrentDate() + " " +propertyEntity.getCheckinTime()) / 60;
					for(int i = 0; i< propertyEntity.getPropertyVsPriceDropEntities().size(); i++) {
						PropertyVsPriceDropEntity propertyVsPriceDropEntity = propertyEntity.getPropertyVsPriceDropEntities().get(i);
						if(hourDifference <= Integer.parseInt(propertyVsPriceDropEntity.getPriceDropEntity().getAfterTime())) {
							if(i == 0) { // First Condition
								priceDropDiscount = Double.parseDouble(propertyVsPriceDropEntity.getPercentage()) * hostBasePrice / 100;
								break;
							} else {
								propertyVsPriceDropEntity = propertyEntity.getPropertyVsPriceDropEntities().get(i -1);
								priceDropDiscount = Double.parseDouble(propertyVsPriceDropEntity.getPercentage()) * hostBasePrice / 100;
								break;
							}
						}
					}
				}
			} else {
				// Host Discount if any
				if (numOfDays >= 7 && numOfDays < 30) { // Weekly
					hostDiscount = Double.parseDouble(entry.getValue().getRoomEntity().getHostDiscountWeekly()) * hostBasePrice / 100;
				} else if(numOfDays >= 30) { // Monthly
					if (!StringUtils.isBlank(entry.getValue().getRoomEntity().getHostDiscountMonthly())) { // Check if monthly present
						hostDiscount = Double.parseDouble(entry.getValue().getRoomEntity().getHostDiscountMonthly()) * hostBasePrice / 100;
					} else if (!StringUtils.isBlank(entry.getValue().getRoomEntity().getHostDiscountWeekly())) { // otherwise calculate with weekly
						hostDiscount = Double.parseDouble(entry.getValue().getRoomEntity().getHostDiscountWeekly()) * hostBasePrice / 100;
					}
				}
				
				System.err.println("hostDiscount ==>> "+hostDiscount);
				if(hostDiscount > 0.0) {
					hostPrice = hostBasePrice - hostDiscount;
				}
				System.err.println("hostPrice ==>> "+hostPrice);
				
				// Offer
				if(!CollectionUtils.isEmpty(entry.getValue().getRoomEntity().getRoomVsOfferEntities())) {
					for(RoomVsOfferEntity roomVsOfferEntity : entry.getValue().getRoomEntity().getRoomVsOfferEntities()) {
						if(Objects.nonNull(roomVsOfferEntity)) {
							if(Objects.nonNull(roomVsOfferEntity.getOfferEntity())) {
								offerEntities.add(roomVsOfferEntity.getOfferEntity());
							}
						}
					}
				}
			}
			
			System.err.println("priceDropDiscount ==>> "+priceDropDiscount);
			if(priceDropDiscount > 0.0) {
				oraDiscount = priceDropDiscount;
				System.err.println("oraDiscount = priceDropDiscount ==>> "+oraDiscount);
			}
			
			oraFinalPrice = oraPrice - oraDiscount;
			System.err.println("oraFinalPrice(oraPrice - oraDiscount) ==>> "+oraFinalPrice);
			offerPrice = calculateOffer(offerEntities, oraFinalPrice);
			System.err.println("offerPrice ==>> "+offerPrice);
			oraFinalPrice = oraFinalPrice - offerPrice;
			System.err.println("oraFinalPrice(oraFinalPrice - offerPrice) ==>> "+oraFinalPrice);
			oraPriceCount.put(entry.getValue().getRoomEntity(), oraPrice);
			oraFinalPriceCount.put(entry.getValue().getRoomEntity(), oraFinalPrice);
		}
		
		RoomEntity roomEntity = null;
		Double minValueInMap = (Collections.min(oraFinalPriceCount.values()));
        for (Entry<RoomEntity, Double> entry : oraFinalPriceCount.entrySet()) {
            if (entry.getValue() == minValueInMap) {
            	roomEntity = entry.getKey();
            }
        }
        
        prices.add(String.valueOf(Math.round(oraPriceCount.get(roomEntity) * 100D) / 100D)); // Ora Price
		prices.add(String.valueOf(Math.round(minValueInMap * 100D) / 100D)); // Ora Discount
		prices.add(String.valueOf(Math.round(oraDiscount * 100D) / 100D)); // Ora Discount (Alongwith Price Drop if any)
		prices.add(String.valueOf(Math.round(offerPrice * 100D) / 100D)); // Offer Price
		prices.add(String.valueOf(Math.round(hostDiscount * 100D) / 100D)); // Host Discount
		System.err.println("prices ==>> "+prices);
		
		if (logger.isInfoEnabled()) {
			logger.info("priceCalculation -- END");
		}
		
		return prices;
	}
	
	public PropertyListViewModel setPropertyListView(PropertyEntity propertyEntity, FilterCiteriaModel filterCiteriaModel, Map<String, FilterRoomModel> filteredRooms) {
		
		if (logger.isInfoEnabled()) {
			logger.info("setPropertyListView -- START");
		}
		
		PropertyListViewModel propertyListViewModel = new PropertyListViewModel();
		propertyListViewModel.setPropertyId(String.valueOf(propertyEntity.getPropertyId()));
		propertyListViewModel.setOraName(propertyEntity.getOraname());
		propertyListViewModel.setAddress(propertyEntity.getAddress());
		propertyListViewModel.setLatitude(propertyEntity.getLatitude());
		propertyListViewModel.setLongitude(propertyEntity.getLongitude());
		propertyListViewModel.setCoverImageURL(propertyEntity.getCoverImageUrl());
		propertyListViewModel.setStayType(filterCiteriaModel.getStayType());
		
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
		
		// Price Section
		List<String> prices = priceCalculation(propertyEntity, filterCiteriaModel, filteredRooms);
		propertyListViewModel.setTotalPrice(prices.get(0));
		propertyListViewModel.setDiscountedPrice(prices.get(1));
		
		// Meal Section
		propertyListViewModel.setMealFlag(false);
		if(!CollectionUtils.isEmpty(propertyEntity.getRoomEntities())) {
			propertyEntity.getRoomEntities().parallelStream().forEach(roomEntity -> {
			//for(RoomEntity roomEntity : propertyEntity.getRoomEntities()) {
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
			});
		}
		
		Set<AmenitiesModel> amenitiesModels = new LinkedHashSet<>();
		if(!CollectionUtils.isEmpty(propertyEntity.getRoomEntities())) {
			propertyEntity.getRoomEntities().parallelStream().forEach(roomEntity -> {
			//for(RoomEntity roomEntity : propertyEntity.getRoomEntities()) {
				if(Objects.nonNull(roomEntity)) {
					if(!CollectionUtils.isEmpty(roomEntity.getRoomVsAmenitiesEntities())) {
						for(RoomVsAmenitiesEntity roomVsAmenitiesEntity : roomEntity.getRoomVsAmenitiesEntities()) {
							amenitiesModels.add(amenitiesConverter.entityToModel(roomVsAmenitiesEntity.getAmenitiesEntity()));
						}
					}
				}
			});
		}
		
		propertyListViewModel.setAmenitiesModels(amenitiesModels);
		
		propertyListViewModel.setIsBookmark(false);
		
		// TODO Analysis Text
		propertyListViewModel.setAnalyticsText("Booked "+propertyEntity.getRoomEntities().size()+ " times in the last 24 hours");
		
		if (logger.isInfoEnabled()) {
			logger.info("setPropertyListView -- END");
		}
		
		return propertyListViewModel;
	}
	
	public List<String> getRatingAndReview(PropertyEntity propertyEntity) {
		
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
	
	private Double calculateOffer(Set<OfferEntity> offerEntities, Double oraFinalPrice) {
		
		if (logger.isInfoEnabled()) {
			logger.info("calculateOffer -- END");
		}
		
		Double offerPrice = 0.0D;
		// Offer Calculation
		if(!CollectionUtils.isEmpty(offerEntities)) {
			
			List<OfferEntity> sortedList = new ArrayList<>(offerEntities);
			Collections.sort(sortedList, new OfferEntityComparatorById());
			System.out.println("sortedList ==>> "+sortedList);
			for(OfferEntity offerEntity : sortedList) {
				System.out.println("offerEntity ==>> "+offerEntity);
				if(Objects.nonNull(offerEntity)) {
					
					System.out.println("offerEntity.getMaxAmount() ==>> "+offerEntity.getMaxAmount());
					if (!StringUtils.isBlank(offerEntity.getMaxAmount()) && offerPrice == 0.0) { // Calculate with Max Amount
						
						if (Double.parseDouble(offerEntity.getMaxAmount()) <= oraFinalPrice) {
							if (!StringUtils.isBlank(offerEntity.getAmount())) { // Amount Check
								offerPrice = offerPrice + Double.parseDouble(offerEntity.getAmount());
								System.out.println("Calculate with Max Amount offerPrice ==>> "+offerPrice);
							} else if (!StringUtils.isBlank(offerEntity.getPercentage())) { // Percentage Check
								offerPrice = offerPrice + (Double.parseDouble(offerEntity.getPercentage()) * oraFinalPrice / 100);
								System.out.println("Calculate with Max Amount offerPrice ==>> "+offerPrice);
							}
						}
					}
					
					if(offerPrice == 0.0) {
						System.out.println("offerEntity.getStartDateRange() ==>> "+offerEntity.getStartDateRange());
						if (!StringUtils.isBlank(offerEntity.getStartDateRange()) && !StringUtils.isBlank(offerEntity.getEndDateRange())) { // Calculate with Date Range
							if (Util.getDateDiff(offerEntity.getStartDateRange()) >= 0 && Util.getDateDiff(offerEntity.getEndDateRange()) <= 0) {
								if (!StringUtils.isBlank(offerEntity.getAmount())) { // Amount Check
									offerPrice = offerPrice + Double.parseDouble(offerEntity.getAmount());
									System.out.println("Calculate with Date Range offerPrice ==>> "+offerPrice);
								} else if (!StringUtils.isBlank(offerEntity.getPercentage())) { // Percentage Check
									offerPrice = offerPrice + (Double.parseDouble(offerEntity.getPercentage()) * oraFinalPrice / 100);
									System.out.println("Calculate with Date Range offerPrice ==>> "+offerPrice);
								}
							}
						}
					}
					
					if(offerPrice == 0.0) {
						if (StringUtils.isBlank(offerEntity.getMaxAmount()) && StringUtils.isBlank(offerEntity.getStartDateRange()) && StringUtils.isBlank(offerEntity.getOnline())) { // Calculate other than Date Range & Max Amount
							if (!StringUtils.isBlank(offerEntity.getAmount())) { // Amount Check
								offerPrice = offerPrice + Double.parseDouble(offerEntity.getAmount());
								System.out.println("Calculate other than Date Range & Max Amount offerPrice ==>> "+offerPrice);
							} else if (!StringUtils.isBlank(offerEntity.getPercentage())) { // Percentage Check
								offerPrice = offerPrice + (Double.parseDouble(offerEntity.getPercentage()) * oraFinalPrice / 100);
								System.out.println("Calculate other than Date Range & Max Amount offerPrice ==>> "+offerPrice);
							}
						}
					}
				}
			}
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("calculateOffer -- END");
		}
		
		return offerPrice;
	}
	
	private Double calculateHostBasePrice(PropertyEntity propertyEntity, FilterCiteriaModel filterCiteriaModel, RoomEntity roomEntity) {
		
		if (logger.isInfoEnabled()) {
			logger.info("calculateHostBasePrice -- START");
		}
		
		Double hostBasePrice = 0.0D;
		int numOfDays = Util.getDayDiff(filterCiteriaModel.getCheckInDate(), filterCiteriaModel.getCheckOutDate());
		if (StringUtils.equals(filterCiteriaModel.getStayType(), PropertyListConstant.SHARED)) { // Customer Wants SHARED Rooms
			
			if (numOfDays >= 30 ) {
				if(propertyEntity.getStayTypeEntity().getStayTypeId() == Status.INACTIVE.ordinal()) {   //short term (ID = 2)
					//Shared night price 
					hostBasePrice = Double.parseDouble(roomEntity.getSharedBedPricePerNight());
					
				} else {   //both & long term
					//Shared Month price 
					hostBasePrice = (Double.parseDouble(roomEntity.getSharedBedPricePerMonth())/30);
				}
			} else {
				
				if(propertyEntity.getStayTypeEntity().getStayTypeId() == Status.ACTIVE.ordinal()){   //Long term
					//Shared Month price 
					hostBasePrice = (Double.parseDouble(roomEntity.getSharedBedPricePerMonth())/30);
				} else {   //both & Short term
					//Shared Night price 
					hostBasePrice = Double.parseDouble(roomEntity.getSharedBedPricePerNight());
				}
			}
		} else { // Customer Wants PRIVATE Rooms
			
			if (numOfDays >= 30 ) {
				if(propertyEntity.getStayTypeEntity().getStayTypeId() == Status.INACTIVE.ordinal()) {   //short term (ID = 2)
					//Private night Price
					hostBasePrice = Double.parseDouble(roomEntity.getRoomPricePerNight());
					
				} else {   //both & long term
					//Private Month Price
					hostBasePrice = (Double.parseDouble(roomEntity.getRoomPricePerMonth())/30);
				}
			} else {
				
				if(propertyEntity.getStayTypeEntity().getStayTypeId() == Status.ACTIVE.ordinal()){   //Long term
					//Private Month Price
					hostBasePrice = (Double.parseDouble(roomEntity.getRoomPricePerMonth())/30);
				} else {   //both & Short term
					//Private Night Price
					hostBasePrice = Double.parseDouble(roomEntity.getRoomPricePerNight());
				}
			}
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("calculateHostBasePrice -- END");
		}
		
		return hostBasePrice;
	}
	
	public void priceCalculationForRoomDetails(PropertyEntity propertyEntity, FilterCiteriaModel filterCiteriaModel, Map<String, FilterRoomModel> filteredRooms, PropertyModel propertyModel) {
		
		if (logger.isInfoEnabled()) {
			logger.info("priceCalculationForRoomDetails -- START");
		}
		
		System.out.println("filteredRooms ==>> "+filteredRooms);
		int noOfGuest = Integer.parseInt(filterCiteriaModel.getNoOfGuest());
		System.err.println("noOfGuest ==>> "+noOfGuest);
		Map<String, FilterRoomModel> sortedMap = new LinkedHashMap<>();
		if (StringUtils.equals(filterCiteriaModel.getStayType(), PropertyListConstant.SHARED)) { // Customer Wants SHARED Rooms
			
			sortedMap = filteredRooms.entrySet().stream().sorted(Map.Entry.comparingByValue(new FilterRoomComparatorByPrice().reversed())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(oldValue, newValue) -> oldValue, LinkedHashMap::new));
			System.out.println("sortedMap SHARED ==>> "+sortedMap);
			for (Map.Entry<String, FilterRoomModel> entry : sortedMap.entrySet()) {
				if(noOfGuest <= entry.getValue().getBedAllocated()) {
					entry.getValue().setIsSelected(true);
					break;
				} else if(noOfGuest >= 0) {
					noOfGuest = noOfGuest - entry.getValue().getBedAllocated();
					entry.getValue().setIsSelected(true);
				} else {
					break;
				}
			}
			System.out.println("sortedMap SHARED After ==>> "+sortedMap);
		} else { // Customer Wants PRIVATE Rooms
			
			sortedMap = filteredRooms.entrySet().stream().sorted(Map.Entry.comparingByValue(new FilterRoomComparatorByAdult().reversed())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(oldValue, newValue) -> oldValue, LinkedHashMap::new));
			System.out.println("sortedMap PRIVATE Before ==>> "+sortedMap);
			for (Map.Entry<String, FilterRoomModel> entry : sortedMap.entrySet()) {
				if(noOfGuest <= entry.getValue().getNumOfAdult()) {
					entry.getValue().setIsSelected(true);
					break;
				} else if(noOfGuest >= 0) {
					noOfGuest = noOfGuest - entry.getValue().getNumOfAdult();
					entry.getValue().setIsSelected(true);
				} else {
					break;
				}
			}
			System.out.println("sortedMap PRIVATE After ==>> "+sortedMap);
		}
		
		
		int numOfDays = Util.getDayDiff(filterCiteriaModel.getCheckInDate(), filterCiteriaModel.getCheckOutDate());
		Double hostBasePrice = 0.0D;
		Double hostDiscount = 0.0D;
		Double hostPrice = 0.0D;
		Double oraMarkUp = 0.0D;
		Double oraPrice = 0.0D;
		Double oraDiscount = 0.0D;
		Double oraFinalPrice = 0.0D;
		Double offerPrice = 0.0D;
		Set<OfferEntity> offerEntities = new HashSet<>();
		
		Double totalPrice = 0.0D;
		Double totalDiscountedPrice = 0.0D;
		for (Map.Entry<String, FilterRoomModel> entry : sortedMap.entrySet()) {
				
			RoomEntity roomEntity = entry.getValue().getRoomEntity();
			hostBasePrice = (calculateHostBasePrice(propertyEntity, filterCiteriaModel, roomEntity)) * numOfDays;
			if(!Util.isEmpty(entry.getValue().getBedAllocated()) && entry.getValue().getBedAllocated() != 0) {
				hostBasePrice = hostBasePrice * entry.getValue().getBedAllocated();
			}
			System.out.println("hostBasePrice ==>> "+hostBasePrice);
			oraMarkUp = Double.parseDouble(roomEntity.getOraPercentage());
			System.err.println("oraMarkUp ==>> "+oraMarkUp);
			oraPrice = hostBasePrice + (hostBasePrice * oraMarkUp / 100);
			System.out.println("oraPrice ==>> "+oraPrice);
			// Room Vs ORA Discount Percentage
			if (!StringUtils.isBlank(roomEntity.getOraDiscountPercentage())) {
				oraDiscount = Double.parseDouble(roomEntity.getOraDiscountPercentage()) * hostBasePrice / 100;
			}
			System.err.println("oraDiscount ==>> "+oraDiscount);
			
			Double priceDropDiscount = 0.0D;
			
			// Check Pricedrop if any
			if(Util.getDateDiff1(filterCiteriaModel.getCheckInDate()) == 0) { // Current Date
				if(!CollectionUtils.isEmpty(propertyEntity.getPropertyVsPriceDropEntities())) { // Price Drop Present
					int hourDifference = Util.getMinuteDiff(Util.getCurrentDate() + " " +propertyEntity.getCheckinTime()) / 60;
					for(int i = 0; i< propertyEntity.getPropertyVsPriceDropEntities().size(); i++) {
						PropertyVsPriceDropEntity propertyVsPriceDropEntity = propertyEntity.getPropertyVsPriceDropEntities().get(i);
						if(hourDifference <= Integer.parseInt(propertyVsPriceDropEntity.getPriceDropEntity().getAfterTime())) {
							if(i == 0) { // First Condition
								priceDropDiscount = Double.parseDouble(propertyVsPriceDropEntity.getPercentage()) * hostBasePrice / 100;
								break;
							} else {
								propertyVsPriceDropEntity = propertyEntity.getPropertyVsPriceDropEntities().get(i -1);
								priceDropDiscount = Double.parseDouble(propertyVsPriceDropEntity.getPercentage()) * hostBasePrice / 100;
								break;
							}
						}
					}
				}
			} else {
				// Host Discount if any
				if (numOfDays >= 7 && numOfDays < 30) { // Weekly
					hostDiscount = Double.parseDouble(roomEntity.getHostDiscountWeekly()) * hostBasePrice / 100;
				} else if(numOfDays >= 30) { // Monthly
					if (!StringUtils.isBlank(roomEntity.getHostDiscountMonthly())) { // Check if monthly present
						hostDiscount = Double.parseDouble(roomEntity.getHostDiscountMonthly()) * hostBasePrice / 100;
					} else if (!StringUtils.isBlank(roomEntity.getHostDiscountWeekly())) { // otherwise calculate with weekly
						hostDiscount = Double.parseDouble(roomEntity.getHostDiscountWeekly()) * hostBasePrice / 100;
					}
				}
				
				System.err.println("hostDiscount ==>> "+hostDiscount);
				if(hostDiscount > 0.0) {
					hostPrice = hostBasePrice - hostDiscount;
				}
				System.err.println("hostPrice ==>> "+hostPrice);
				
				// Offer
				if(!CollectionUtils.isEmpty(roomEntity.getRoomVsOfferEntities())) {
					for(RoomVsOfferEntity roomVsOfferEntity : roomEntity.getRoomVsOfferEntities()) {
						if(Objects.nonNull(roomVsOfferEntity)) {
							if(Objects.nonNull(roomVsOfferEntity.getOfferEntity())) {
								offerEntities.add(roomVsOfferEntity.getOfferEntity());
							}
						}
					}
				}
			}
			
			System.err.println("priceDropDiscount ==>> "+priceDropDiscount);
			if(priceDropDiscount > 0.0) {
				oraDiscount = priceDropDiscount;
				System.err.println("oraDiscount = priceDropDiscount ==>> "+oraDiscount);
			}
			
			oraFinalPrice = oraPrice - oraDiscount;
			System.err.println("oraFinalPrice(oraPrice - oraDiscount) ==>> "+oraFinalPrice);
			
			for(RoomModel roomModel : propertyModel.getRoomModels()) {
				if(StringUtils.equals(roomModel.getOraRoomName(), roomEntity.getOraRoomName())) {
					roomModel.setPriceDrop(String.valueOf(priceDropDiscount));
					roomModel.setOraPrice(String.valueOf(oraPrice));
					roomModel.setOraDiscount(String.valueOf(oraDiscount));
					roomModel.setOraFinalPrice(String.valueOf(oraFinalPrice));
					roomModel.setIsSelected("false");
					roomModel.setBedAllocated("0");
					if(Objects.nonNull(entry.getValue().getIsSelected()) && entry.getValue().getIsSelected()) {
						
						if(!Util.isEmpty(entry.getValue().getBedAllocated()) && entry.getValue().getBedAllocated() != 0) {
							roomModel.setBedAllocated(String.valueOf(entry.getValue().getBedAllocated()));
							roomModel.setNumOfBedBooked(entry.getValue().getNumOfBedBooked());
							roomModel.setNumOfCotBooked(entry.getValue().getNumOfCotBooked());
						}
						
						roomModel.setIsSelected("true");
						totalPrice = totalPrice + oraPrice;
						totalDiscountedPrice = totalDiscountedPrice + oraDiscount;
					}
					break;
				}
			}
		}
		
		offerPrice = calculateOffer(offerEntities, totalPrice);
		System.err.println("offerPrice ==>> "+offerPrice);
		propertyModel.setPropertyOffer(String.valueOf(offerPrice));
		propertyModel.setTotalAmount(String.valueOf(totalPrice));
		propertyModel.setTotalDiscount(String.valueOf(totalDiscountedPrice));
		propertyModel.setAmountPayable(String.valueOf(totalPrice - totalDiscountedPrice));
		
        
		if (logger.isInfoEnabled()) {
			logger.info("priceCalculationForRoomDetails -- END");
		}
	}
	
	public Boolean filterBycheckInDateForBooking(PropertyEntity propertyEntity, FilterCiteriaModel filterCiteriaModel) {
		
		if (logger.isInfoEnabled()) {
			logger.info("filterBycheckInDateForBooking -- START");
		}
		
		Boolean flag = false;
		try {
			
			int noOfGuest = Integer.parseInt(filterCiteriaModel.getNoOfGuest());
			System.err.println("noOfGuest ==>> "+noOfGuest);
			List<Boolean> count = new ArrayList<>();
			for(RoomModel roomModel : filterCiteriaModel.getRoomModels()) {
				RoomEntity roomEntity = propertyEntity.getRoomEntities().stream().filter(r -> StringUtils.equals(r.getOraRoomName(), roomModel.getOraRoomName()) && r.getStatus() == Status.ACTIVE.ordinal()).findFirst().orElse(null);
				if(Objects.nonNull(roomEntity)) {
					
					if (StringUtils.equals(filterCiteriaModel.getStayType(), PropertyListConstant.SHARED)) { // Customer Wants SHARED Rooms
						
						Map<String, List<BookingVsRoomModel>> roomByOraName = callBookingService(String.valueOf(propertyEntity.getPropertyId()), filterCiteriaModel.getCheckInDate(), Accommodation.SHARED.name());
						if(CollectionUtils.isEmpty(roomByOraName)) { // No Booking Present
							
							roomModel.setNumOfBedBooked("0");
							roomModel.setNumOfCotBooked("0");
							
							if(Integer.parseInt(roomModel.getNoOfGuest()) > Integer.parseInt(roomEntity.getNumOfBed())) {
								count.add(false);
								break;
							}
							if(!StringUtils.isEmpty(roomModel.getNumOfCot()) && Integer.parseInt(roomModel.getNumOfCot()) > Integer.parseInt(roomEntity.getNumOfCot())) {
								count.add(false);
								break;
							}
						} else {
							if(roomByOraName.containsKey(roomModel.getOraRoomName())) {
								int maxBedBooked = 0;
								int maxCotBooked = 0;
								List<BookingVsRoomModel> roomBooked = roomByOraName.get(roomModel.getOraRoomName());
								for(BookingVsRoomModel bookingVsRoomModel : roomBooked) {
									if(maxBedBooked < Integer.parseInt(bookingVsRoomModel.getNumOfSharedBed())) {
										maxBedBooked = Integer.parseInt(bookingVsRoomModel.getNumOfSharedBed()); // Getting Highest Number of bed booked for that room
									}
									
									if(maxCotBooked < Integer.parseInt(bookingVsRoomModel.getNumOfSharedCot())) {
										maxCotBooked = Integer.parseInt(bookingVsRoomModel.getNumOfSharedCot()); // Getting Highest Number of cot booked for that room
									}
								}
								
								roomModel.setNumOfBedBooked(String.valueOf(maxBedBooked));
								roomModel.setNumOfCotBooked(String.valueOf(maxCotBooked));
								;
								if(Integer.parseInt(roomModel.getNoOfGuest()) > (Integer.parseInt(roomEntity.getNumOfBed()) - maxBedBooked)) {
									count.add(false);
									break;
								}
								
								if(!StringUtils.isEmpty(roomModel.getNumOfCot()) && Integer.parseInt(roomModel.getNumOfCot()) > (Integer.parseInt(roomEntity.getNumOfCot()) - maxCotBooked)) {
									count.add(false);
									break;
								}
							} else {
								if(Integer.parseInt(roomModel.getNoOfGuest()) >Integer.parseInt( roomEntity.getNumOfBed())) {
									count.add(false);
									break;
								}
								
								if(!StringUtils.isEmpty(roomModel.getNumOfCot()) && Integer.parseInt(roomModel.getNumOfCot()) > Integer.parseInt(roomEntity.getNumOfCot())) {
									count.add(false);
									break;
								}
							}
						}
					} else { // Customer Wants PRIVATE Rooms
						Map<String, List<BookingVsRoomModel>> roomByOraName = callBookingService(String.valueOf(propertyEntity.getPropertyId()), filterCiteriaModel.getCheckInDate(), Accommodation.PRIVATE.name());
						if(CollectionUtils.isEmpty(roomByOraName) && !roomByOraName.containsKey(roomModel.getOraRoomName())) {
							
							if(Integer.parseInt(roomModel.getNoOfGuest()) > Integer.parseInt(roomEntity.getNoOfGuest())) {
								count.add(false);
								break;
							}
							
							if(!StringUtils.isEmpty(roomModel.getNumOfCot()) && Integer.parseInt(roomModel.getNumOfCot()) > Integer.parseInt(roomEntity.getNumOfCot())) {
								count.add(false);
								break;
							}
							
							if(!StringUtils.isEmpty(roomModel.getNoOfChild()) && Integer.parseInt(roomModel.getNoOfChild()) > Integer.parseInt(roomEntity.getNoOfChild())) {
								count.add(false);
								break;
							}
						} else {
							count.add(false);
							break;
						}
					}
				}
			}
			
			if(!count.contains(false)) {
				flag = true;
			}	
				
		} catch (Exception e) {
			e.printStackTrace();
			if (logger.isInfoEnabled()) {
				logger.info("Exception in filterBycheckInDateForBooking -- "+Util.errorToString(e));
			}
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("filterBycheckInDateForBooking -- END");
		}
		
		return flag;
	}
	
	public void priceCalculationForBooking(PropertyEntity propertyEntity, FilterCiteriaModel filterCiteriaModel, PropertyModel propertyModel) throws FormExceptions {
		
		if (logger.isInfoEnabled()) {
			logger.info("priceCalculationForBooking -- START");
		}
		
		int numOfDays = Util.getDayDiff(filterCiteriaModel.getCheckInDate(), filterCiteriaModel.getCheckOutDate());
		Double hostBasePrice = 0.0D;
		Double hostDiscount = 0.0D;
		Double hostPrice = 0.0D;
		Double oraMarkUp = 0.0D;
		Double oraPrice = 0.0D;
		Double oraDiscount = 0.0D;
		Double oraFinalPrice = 0.0D;
		Double offerPrice = 0.0D;
		Set<OfferEntity> offerEntities = new HashSet<>();
		
		Double totalPrice = 0.0D;
		Double totalDiscountedPrice = 0.0D;
		List<RoomModel> selectedRooms = new ArrayList<>();
		for(RoomModel roomModel : filterCiteriaModel.getRoomModels()) {
			RoomEntity roomEntity = propertyEntity.getRoomEntities().stream().filter(r -> StringUtils.equals(r.getOraRoomName(), roomModel.getOraRoomName()) && r.getStatus() == Status.ACTIVE.ordinal()).findFirst().orElse(null);
			if(Objects.nonNull(roomEntity)) {
				hostBasePrice = (calculateHostBasePrice(propertyEntity, filterCiteriaModel, roomEntity)) * numOfDays;
				if (StringUtils.equals(filterCiteriaModel.getStayType(), PropertyListConstant.SHARED)) { // Customer Wants SHARED Rooms
					hostBasePrice = hostBasePrice * Integer.parseInt(roomModel.getNoOfGuest());
				}
				
				if(!StringUtils.isEmpty(roomModel.getNumOfCot())) {
					hostBasePrice = hostBasePrice + (Double.parseDouble(roomEntity.getCotPrice()) * Integer.parseInt(roomModel.getNumOfCot()));
				}
				
				roomModel.setHostBasePrice(String.valueOf(hostBasePrice));
				System.out.println("hostBasePrice ==>> "+hostBasePrice);
				oraMarkUp = Double.parseDouble(roomEntity.getOraPercentage());
				System.err.println("oraMarkUp ==>> "+oraMarkUp);
				oraPrice = hostBasePrice + (hostBasePrice * oraMarkUp / 100);
				System.out.println("oraPrice ==>> "+oraPrice);
				// Room Vs ORA Discount Percentage
				if (!StringUtils.isBlank(roomEntity.getOraDiscountPercentage())) {
					oraDiscount = Double.parseDouble(roomEntity.getOraDiscountPercentage()) * hostBasePrice / 100;
				}
				System.err.println("oraDiscount ==>> "+oraDiscount);
				
				Double priceDropDiscount = 0.0D;
				
				// Check Pricedrop if any
				if(Util.getDateDiff1(filterCiteriaModel.getCheckInDate()) == 0) { // Current Date
					if(!CollectionUtils.isEmpty(propertyEntity.getPropertyVsPriceDropEntities())) { // Price Drop Present
						int hourDifference = Util.getMinuteDiff(Util.getCurrentDate() + " " +propertyEntity.getCheckinTime()) / 60;
						for(int i = 0; i< propertyEntity.getPropertyVsPriceDropEntities().size(); i++) {
							PropertyVsPriceDropEntity propertyVsPriceDropEntity = propertyEntity.getPropertyVsPriceDropEntities().get(i);
							if(hourDifference <= Integer.parseInt(propertyVsPriceDropEntity.getPriceDropEntity().getAfterTime())) {
								if(i == 0) { // First Condition
									priceDropDiscount = Double.parseDouble(propertyVsPriceDropEntity.getPercentage()) * hostBasePrice / 100;
									break;
								} else {
									propertyVsPriceDropEntity = propertyEntity.getPropertyVsPriceDropEntities().get(i -1);
									priceDropDiscount = Double.parseDouble(propertyVsPriceDropEntity.getPercentage()) * hostBasePrice / 100;
									break;
								}
							}
						}
					}
				} else {
					// Host Discount if any
					if (numOfDays >= 7 && numOfDays < 30) { // Weekly
						hostDiscount = Double.parseDouble(roomEntity.getHostDiscountWeekly()) * hostBasePrice / 100;
					} else if(numOfDays >= 30) { // Monthly
						if (!StringUtils.isBlank(roomEntity.getHostDiscountMonthly())) { // Check if monthly present
							hostDiscount = Double.parseDouble(roomEntity.getHostDiscountMonthly()) * hostBasePrice / 100;
						} else if (!StringUtils.isBlank(roomEntity.getHostDiscountWeekly())) { // otherwise calculate with weekly
							hostDiscount = Double.parseDouble(roomEntity.getHostDiscountWeekly()) * hostBasePrice / 100;
						}
					}
					roomModel.setHostDiscount(String.valueOf(hostDiscount));
					System.err.println("hostDiscount ==>> "+hostDiscount);
					if(hostDiscount > 0.0) {
						hostPrice = hostBasePrice - hostDiscount;
					}
					System.err.println("hostPrice ==>> "+hostPrice);
					
					// Offer
					if(!CollectionUtils.isEmpty(roomEntity.getRoomVsOfferEntities())) {
						for(RoomVsOfferEntity roomVsOfferEntity : roomEntity.getRoomVsOfferEntities()) {
							if(Objects.nonNull(roomVsOfferEntity)) {
								if(Objects.nonNull(roomVsOfferEntity.getOfferEntity())) {
									offerEntities.add(roomVsOfferEntity.getOfferEntity());
								}
							}
						}
					}
				}
				
				System.err.println("priceDropDiscount ==>> "+priceDropDiscount);
				if(priceDropDiscount > 0.0) {
					oraDiscount = priceDropDiscount;
					System.err.println("oraDiscount = priceDropDiscount ==>> "+oraDiscount);
				}
				
				oraFinalPrice = oraPrice - oraDiscount;
				System.err.println("oraFinalPrice(oraPrice - oraDiscount) ==>> "+oraFinalPrice);
				
				roomModel.setPriceDrop(String.valueOf(priceDropDiscount));
				roomModel.setOraPrice(String.valueOf(oraPrice));
				roomModel.setOraDiscount(String.valueOf(oraDiscount));
				roomModel.setOraFinalPrice(String.valueOf(oraFinalPrice));
				GstSlabModel gstSlabModel = gstSlabService.getActiveGstModel(oraFinalPrice);
				
				roomModel.setSgst(gstSlabModel.getPercentage());
				Double gstAmount = Double.parseDouble(gstSlabModel.getPercentage()) * oraFinalPrice / 100;
				roomModel.setGstAmt(String.valueOf(gstAmount));
				Double totalAmt = oraFinalPrice + gstAmount;
				roomModel.setTotalAmt(String.valueOf(totalAmt));
				
				totalPrice = totalPrice + totalAmt;
				totalDiscountedPrice = totalDiscountedPrice + oraDiscount;
			}
			
			selectedRooms.add(roomModel);
		}
		
		offerPrice = calculateOffer(offerEntities, totalPrice);
		System.err.println("offerPrice ==>> "+offerPrice);
		propertyModel.setPropertyOffer(String.valueOf(offerPrice));
		propertyModel.setTotalAmount(String.valueOf(totalPrice));
		propertyModel.setTotalDiscount(String.valueOf(totalDiscountedPrice));
		propertyModel.setAmountPayable(String.valueOf(totalPrice - totalDiscountedPrice));
		
        // Setting Final RoomModel in Property
		for(RoomModel roomModel : selectedRooms) {
			RoomModel propertyRoomModel = propertyModel.getRoomModels().stream().filter(r -> StringUtils.equals(r.getOraRoomName(), roomModel.getOraRoomName()) && r.getStatus() == Status.ACTIVE.ordinal()).findFirst().orElse(null);
			if(Objects.nonNull(propertyRoomModel)) {
				
				propertyRoomModel.setNoOfGuest(roomModel.getNoOfGuest());
				propertyRoomModel.setNumOfCot(roomModel.getNumOfCot());
				propertyRoomModel.setNoOfChild(roomModel.getNoOfChild());
				propertyRoomModel.setPriceDrop(roomModel.getPriceDrop());
				propertyRoomModel.setHostBasePrice(roomModel.getHostBasePrice());
				propertyRoomModel.setHostDiscount(roomModel.getHostDiscount());
				propertyRoomModel.setOraPrice(roomModel.getOraPrice());
				propertyRoomModel.setOraDiscount(roomModel.getOraDiscount());
				propertyRoomModel.setOraFinalPrice(roomModel.getOraFinalPrice());
				propertyRoomModel.setSgst(roomModel.getSgst());
				propertyRoomModel.setGstAmt(roomModel.getGstAmt());
				propertyRoomModel.setTotalAmt(roomModel.getTotalAmt());
				propertyRoomModel.setIsSelected("true");
			}
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("priceCalculationForBooking -- END");
		}
	}
}