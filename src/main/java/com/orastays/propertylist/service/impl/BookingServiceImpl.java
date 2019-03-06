package com.orastays.propertylist.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.orastays.propertylist.exceptions.FormExceptions;
import com.orastays.propertylist.helper.PropertyListConstant;
import com.orastays.propertylist.helper.Util;
import com.orastays.propertylist.model.FilterCiteriaModel;
import com.orastays.propertylist.model.PropertyModel;
import com.orastays.propertylist.model.ResponseModel;
import com.orastays.propertylist.model.RoomModel;
import com.orastays.propertylist.model.booking.BookingModel;
import com.orastays.propertylist.model.booking.BookingVsRoomModel;
import com.orastays.propertylist.model.booking.FormOfPayment;
import com.orastays.propertylist.model.booking.PaymentModel;
import com.orastays.propertylist.model.user.UserModel;
import com.orastays.propertylist.service.BookingService;

@Service
@Transactional
public class BookingServiceImpl extends BaseServiceImpl implements BookingService {

	private static final Logger logger = LogManager.getLogger(BookingServiceImpl.class);
	
	@Override
	public PaymentModel propertyBooking(FilterCiteriaModel filterCiteriaModel) throws FormExceptions {

		if (logger.isInfoEnabled()) {
			logger.info("propertyBooking -- START");
		}
		
		Map<String, Exception> exceptions = new LinkedHashMap<>();
		PaymentModel paymentModel = null;
		UserModel userModel = bookingValidation.validatePropertyBooking(filterCiteriaModel);
		if(Objects.nonNull(userModel)) {
			PropertyModel propertyModel = propertyListService.fetchPriceDetails(filterCiteriaModel);
			if(Objects.isNull(propertyModel)) {
				exceptions.put(messageUtil.getBundle("property.notavailable.code"), new Exception(messageUtil.getBundle("property.notavailable.message")));
				throw new FormExceptions(exceptions);
			} else {
				BookingModel bookingModel = new BookingModel();
				
				// Setting Booking Vs Traveller Informations
				bookingModel.setBookingInfoModel(filterCiteriaModel.getBookingInfoModel());
				
				bookingModel.setAccomodationType(filterCiteriaModel.getStayType());
				bookingModel.setBookingApproval("true");
				bookingModel.setCheckinDate(filterCiteriaModel.getCheckInDate());
				bookingModel.setCheckoutDate(filterCiteriaModel.getCheckOutDate());
				bookingModel.setNumOfDays(String.valueOf(Util.getDayDiff(filterCiteriaModel.getCheckInDate(), filterCiteriaModel.getCheckOutDate())));
				bookingModel.setSuccessURL(filterCiteriaModel.getSuccessURL());
				bookingModel.setFailureURL(filterCiteriaModel.getFailureURL());
				
				String finalPrice = propertyModel.getPriceDetails().get("finalPrice");
				if(!StringUtils.equals(finalPrice, filterCiteriaModel.getTotalAmt())) {
					exceptions.put(messageUtil.getBundle("price.mismatch.code"), new Exception(messageUtil.getBundle("price.mismatch.message")));
					throw new FormExceptions(exceptions);
				} else {
					bookingModel.setPropertyId(propertyModel.getPropertyId());
					bookingModel.setConvenienceAmtWgst(String.valueOf(Double.parseDouble(propertyModel.getConvenienceFee()) + Double.parseDouble(propertyModel.getConvenienceGSTAmount())));
					bookingModel.setConvenienceFeeAmt(propertyModel.getConvenienceFee());
					bookingModel.setConvenienceFeePerc(propertyModel.getConvenienceGSTPercentage());
					bookingModel.setConvenienceGstAmt(propertyModel.getConvenienceGSTAmount());
					bookingModel.setOraSpecialOfferAmt(propertyModel.getPropertyOffer());
					bookingModel.setTotalPrice(propertyModel.getTotalAmount());
					bookingModel.setUserFinalPrice(finalPrice);
					bookingModel.setUserId(userModel.getUserId());
					
					FormOfPayment formOfPayment = new FormOfPayment();
					formOfPayment.setCurrency("INR");
					formOfPayment.setMode(PropertyListConstant.MODE_CASHLESS);
					formOfPayment.setPercentage(messageUtil.getBundle("payment_percentage"));
					bookingModel.setFormOfPayment(formOfPayment);
					
					List<BookingVsRoomModel> bookingVsRooms = new ArrayList<>();
					for(RoomModel roomModel : propertyModel.getRoomModels()) {
						
						if(StringUtils.equals(roomModel.getIsSelected(), "true")) {
							BookingVsRoomModel bookingVsRoomModel = new BookingVsRoomModel();
							
							bookingVsRoomModel.setGstAmt(roomModel.getGstAmt());
							bookingVsRoomModel.setHostBasePrice(roomModel.getHostBasePrice());
							bookingVsRoomModel.setHostDiscount(roomModel.getHostDiscount());
							bookingVsRoomModel.setHostPrice(String.valueOf(Double.parseDouble(roomModel.getHostBasePrice()) - Double.parseDouble(roomModel.getHostDiscount())));
							bookingVsRoomModel.setNumOfAdult(roomModel.getNoOfGuest());
							bookingVsRoomModel.setNumOfChild(roomModel.getNoOfChild());
							bookingVsRoomModel.setNumOfCot(roomModel.getNumOfCot());
							
							if (StringUtils.equals(filterCiteriaModel.getStayType(), PropertyListConstant.SHARED)) { // Customer Wants SHARED Rooms
								bookingVsRoomModel.setNumOfSharedBed(roomModel.getNoOfGuest());
								bookingVsRoomModel.setNumOfSharedCot(roomModel.getNumOfCot());
								bookingVsRoomModel.setTotalNumOfSharedBed(roomModel.getNumOfBed());
								bookingVsRoomModel.setTotalNumOfSharedCot(roomModel.getNumOfCot());
							}
							
							bookingVsRoomModel.setOraDiscount(roomModel.getOraDiscount());
							bookingVsRoomModel.setOraFinalPrice(roomModel.getOraFinalPrice());
							bookingVsRoomModel.setOraMarkUp(roomModel.getOraPercentage());
							bookingVsRoomModel.setOraPrice(roomModel.getOraPrice());
							bookingVsRoomModel.setOraRoomName(roomModel.getOraRoomName());
							bookingVsRoomModel.setTotalAmt(roomModel.getTotalAmt());
							bookingVsRoomModel.setRoomId(roomModel.getRoomId());
							
							bookingVsRooms.add(bookingVsRoomModel);
						}
					}
					
					bookingModel.setBookingVsRooms(bookingVsRooms);
					try {
						System.out.println("bookingModel ==>> "+new ObjectMapper().writeValueAsString(bookingModel));
					} catch (JsonProcessingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					Gson gson = new Gson();
					try {
						ResponseModel responseModel = restTemplate.postForObject(messageUtil.getBundle("booking.server.url") +"add-booking", bookingModel, ResponseModel.class);
						String jsonString = gson.toJson(responseModel.getResponseBody());
						paymentModel = gson.fromJson(jsonString, PaymentModel.class);
						
						if (logger.isInfoEnabled()) {
							logger.info("paymentModel ==>> "+paymentModel);
						}
						System.err.println("paymentModel ==>> "+paymentModel);
						
					} catch(HttpClientErrorException e) {
						String jsonString = gson.toJson(e.getResponseBodyAsString());
						ResponseModel responseModel = gson.fromJson(jsonString, ResponseModel.class);
						exceptions.put(responseModel.getResponseCode(), new Exception(responseModel.getResponseMessage()));
					} catch (Exception e) {
						if (logger.isInfoEnabled()) {
							logger.info("Exception in propertyBooking -- "+Util.errorToString(e));
						}
					}
				}
			}
		} else {
			exceptions.put(messageUtil.getBundle("login.required.code"), new Exception(messageUtil.getBundle("login.required.message")));
			throw new FormExceptions(exceptions);
		}
		
		
		if (logger.isInfoEnabled()) {
			logger.info("fetchProperties -- END");
		}
		
		return paymentModel;
	}
}