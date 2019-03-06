package com.orastays.propertylist.validation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.orastays.propertylist.entity.PropertyTypeEntity;
import com.orastays.propertylist.exceptions.FormExceptions;
import com.orastays.propertylist.helper.Status;
import com.orastays.propertylist.helper.Util;
import com.orastays.propertylist.model.FilterCiteriaModel;
import com.orastays.propertylist.model.PriceCalculatorModel;
import com.orastays.propertylist.model.user.UserModel;

@Component
public class HomeValidation extends AuthorizeUserValidation {

	private static final Logger logger = LogManager.getLogger(HomeValidation.class);
	
	public UserModel validatePropertyType(String propertyTypeId, String userToken) throws FormExceptions {

		if (logger.isInfoEnabled()) {
			logger.info("validatePropertyType -- Start");
		}

		Map<String, Exception> exceptions = new LinkedHashMap<>();
		UserModel userModel = null;
		if (!StringUtils.isBlank(userToken)) {
			userModel = getUserDetails(userToken);
		}
		
		// Validate Property Type
		if (StringUtils.isBlank(propertyTypeId)) {
			exceptions.put(messageUtil.getBundle("property.type.id.null.code"), new Exception(messageUtil.getBundle("property.type.id.null.message")));
		} else {
			if (!Util.isNumeric(propertyTypeId)) {
				exceptions.put(messageUtil.getBundle("property.type.id.invalid.code"), new Exception(messageUtil.getBundle("property.type.id.invalid.message")));
			} else {
				PropertyTypeEntity propertyTypeEntity = propertyTypeDAO.find(Long.parseLong(propertyTypeId));
				if (Objects.isNull(propertyTypeEntity) && propertyTypeEntity.getStatus() != Status.ACTIVE.ordinal()) {
					exceptions.put(messageUtil.getBundle("property.type.id.invalid.code"), new Exception(messageUtil.getBundle("property.type.id.invalid.message")));
				}
			}
		}

		if (exceptions.size() > 0)
			throw new FormExceptions(exceptions);

		if (logger.isInfoEnabled()) {
			logger.info("validatePropertyType -- End");
		}
		
		return userModel;
	}
	
	public FilterCiteriaModel validatePriceCalculator(PriceCalculatorModel priceCalculatorModel) throws FormExceptions {

		if (logger.isInfoEnabled()) {
			logger.info("validatePriceCalculator -- Start");
		}

		//UserModel userModel = getUserDetails(priceCalculatorModel.getUserToken());
		Map<String, Exception> exceptions = new LinkedHashMap<>();
		FilterCiteriaModel filterCiteriaModel = null;
		// Validate Property Type
		if (StringUtils.isBlank(priceCalculatorModel.getPropertyTypeId())) {
			exceptions.put(messageUtil.getBundle("property.type.id.null.code"), new Exception(messageUtil.getBundle("property.type.id.null.message")));
		} else {
			if (!Util.isNumeric(priceCalculatorModel.getPropertyTypeId())) {
				exceptions.put(messageUtil.getBundle("property.type.id.invalid.code"), new Exception(messageUtil.getBundle("property.type.id.invalid.message")));
			} else {
				PropertyTypeEntity propertyTypeEntity = propertyTypeDAO.find(Long.parseLong(priceCalculatorModel.getPropertyTypeId()));
				if (Objects.isNull(propertyTypeEntity) && propertyTypeEntity.getStatus() != Status.ACTIVE.ordinal()) {
					exceptions.put(messageUtil.getBundle("property.type.id.invalid.code"), new Exception(messageUtil.getBundle("property.type.id.invalid.message")));
				}
			}
		}
		
		// Validate Latitude
		if (StringUtils.isBlank(priceCalculatorModel.getLatitude())) {
			exceptions.put(messageUtil.getBundle("latitude.null.code"), new Exception(messageUtil.getBundle("latitude.null.message")));
		} else {
			if (!Util.checkLatitude(priceCalculatorModel.getLatitude())) {
				exceptions.put(messageUtil.getBundle("latitude.invalid.code"), new Exception(messageUtil.getBundle("latitude.invalid.message")));
			}
		}

		// Validate Longitude
		if (StringUtils.isBlank(priceCalculatorModel.getLongitude())) {
			exceptions.put(messageUtil.getBundle("longitude.null.code"), new Exception(messageUtil.getBundle("longitude.null.message")));
		} else {
			if (!Util.checkLongitude(priceCalculatorModel.getLongitude())) {
				exceptions.put(messageUtil.getBundle("longitude.invalid.code"), new Exception(messageUtil.getBundle("longitude.invalid.message")));
			}
		}
		
		// Validate NoOfGuest
		if(StringUtils.isBlank(priceCalculatorModel.getNoOfGuest())) {
			exceptions.put(messageUtil.getBundle("noofguest.null.code"), new Exception(messageUtil.getBundle("noofguest.null.message")));
		} else {
			if ((!Util.isNumeric(priceCalculatorModel.getNoOfGuest())) && (Integer.parseInt(priceCalculatorModel.getNoOfGuest()) > 0)) {
				exceptions.put(messageUtil.getBundle("noofguest.invalid.code"), new Exception(messageUtil.getBundle("noofguest.invalid.message")));
			}
		}
		
		if (exceptions.size() > 0)
			throw new FormExceptions(exceptions);
		else {
			filterCiteriaModel = new FilterCiteriaModel();
			filterCiteriaModel.setLatitude(priceCalculatorModel.getLatitude());
			filterCiteriaModel.setLongitude(priceCalculatorModel.getLongitude());
		}

		if (logger.isInfoEnabled()) {
			logger.info("validatePriceCalculator -- End");
		}
		
		return filterCiteriaModel;
	}
}
