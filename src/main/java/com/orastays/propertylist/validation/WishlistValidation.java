package com.orastays.propertylist.validation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.orastays.propertylist.entity.PropertyEntity;
import com.orastays.propertylist.exceptions.FormExceptions;
import com.orastays.propertylist.helper.Status;
import com.orastays.propertylist.helper.Util;
import com.orastays.propertylist.model.WishlistModel;
import com.orastays.propertylist.model.user.UserModel;

@Component
public class WishlistValidation extends AuthorizeUserValidation {

private static final Logger logger = LogManager.getLogger(HomeValidation.class);
	
	public UserModel validateBookmark(WishlistModel wishlistModel) throws FormExceptions {

		if (logger.isInfoEnabled()) {
			logger.info("validateBookmark -- Start");
		}

		UserModel userModel = getUserDetails(wishlistModel.getUserToken());
		Map<String, Exception> exceptions = new LinkedHashMap<>();
		PropertyEntity propertyEntity = null;
		
		// Validate Property
		if(Objects.nonNull(wishlistModel.getPropertyModel())) {
			if (StringUtils.isBlank(wishlistModel.getPropertyModel().getPropertyId())) {
				exceptions.put(messageUtil.getBundle("property.id.null.code"), new Exception(messageUtil.getBundle("property.id.null.message")));
			} else {
				if (!Util.isNumeric(wishlistModel.getPropertyModel().getPropertyId())) {
					exceptions.put(messageUtil.getBundle("property.id.invalid.code"), new Exception(messageUtil.getBundle("property.id.invalid.message")));
				} else {
					propertyEntity = propertyDAO.find(Long.parseLong(wishlistModel.getPropertyModel().getPropertyId()));
					if (Objects.isNull(propertyEntity) && propertyEntity.getStatus() != Status.ACTIVE.ordinal()) {
						exceptions.put(messageUtil.getBundle("property.id.invalid.code"), new Exception(messageUtil.getBundle("property.id.invalid.message")));
					}
				}
			}
		} else {
			exceptions.put(messageUtil.getBundle("property.id.null.code"), new Exception(messageUtil.getBundle("property.id.null.message")));
		}

		if (exceptions.size() > 0)
			throw new FormExceptions(exceptions);

		if (logger.isInfoEnabled()) {
			logger.info("validateBookmark -- End");
		}
		
		return userModel;
	}
}
