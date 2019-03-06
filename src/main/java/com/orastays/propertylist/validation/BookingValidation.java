package com.orastays.propertylist.validation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.orastays.propertylist.exceptions.FormExceptions;
import com.orastays.propertylist.model.FilterCiteriaModel;
import com.orastays.propertylist.model.user.UserModel;

@Component
public class BookingValidation extends AuthorizeUserValidation {

	private static final Logger logger = LogManager.getLogger(BookingValidation.class);
	
	public UserModel validatePropertyBooking(FilterCiteriaModel filterCiteriaModel) throws FormExceptions {

		if (logger.isInfoEnabled()) {
			logger.info("validatePropertyBooking -- Start");
		}

		UserModel userModel = propertyListValidation.validateFetchPriceDetails(filterCiteriaModel);
		Map<String, Exception> exceptions = new LinkedHashMap<>();
		
		// Validate Total Price
		if (StringUtils.isBlank(filterCiteriaModel.getTotalAmt())) {
			exceptions.put(messageUtil.getBundle("totalAmt.null.code"), new Exception(messageUtil.getBundle("totalAmt.null.message")));
		} else {
			try {
				if ((Double.parseDouble(filterCiteriaModel.getTotalAmt()) < 0)) {
					exceptions.put(messageUtil.getBundle("totalAmt.invalid.code"), new Exception(messageUtil.getBundle("totalAmt.invalid.message")));
				}
			} catch (NumberFormatException e) {
				exceptions.put(messageUtil.getBundle("totalAmt.invalid.code"), new Exception(messageUtil.getBundle("totalAmt.invalid.message")));
			}
		}
		
		if (StringUtils.isBlank(filterCiteriaModel.getSuccessURL())) {
			exceptions.put(messageUtil.getBundle("successURL.null.code"), new Exception(messageUtil.getBundle("successURL.null.message")));
		}
		
		if (StringUtils.isBlank(filterCiteriaModel.getFailureURL())) {
			exceptions.put(messageUtil.getBundle("failureURL.null.code"), new Exception(messageUtil.getBundle("failureURL.null.message")));
		}
		
		if(Objects.isNull(filterCiteriaModel.getBookingInfoModel())) {
			exceptions.put(messageUtil.getBundle("travellerinfo.null.code"), new Exception(messageUtil.getBundle("travellerinfo.null.message")));
		} else {
			
			if (StringUtils.isBlank(filterCiteriaModel.getBookingInfoModel().getName())) {
				exceptions.put(messageUtil.getBundle("traveller.name.null.code"), new Exception(messageUtil.getBundle("traveller.name.null.message")));
			}
			
			if (StringUtils.isBlank(filterCiteriaModel.getBookingInfoModel().getAddress())) {
				exceptions.put(messageUtil.getBundle("traveller.address.null.code"), new Exception(messageUtil.getBundle("traveller.address.null.message")));
			}
			
			if (StringUtils.isBlank(filterCiteriaModel.getBookingInfoModel().getMobile())) {
				exceptions.put(messageUtil.getBundle("traveller.mobile.null.code"), new Exception(messageUtil.getBundle("traveller.mobile.null.message")));
			}
			
			// TODO if anymore validation is needed
		}
		
		if (exceptions.size() > 0)
			throw new FormExceptions(exceptions);
		
		if (logger.isInfoEnabled()) {
			logger.info("validatePropertyBooking -- End");
		}
		
		return userModel;
	}
}
