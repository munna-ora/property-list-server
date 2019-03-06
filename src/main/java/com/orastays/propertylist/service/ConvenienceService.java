package com.orastays.propertylist.service;

import com.orastays.propertylist.entity.ConvenienceEntity;
import com.orastays.propertylist.exceptions.FormExceptions;
import com.orastays.propertylist.model.ConvenienceModel;

public interface ConvenienceService {

	ConvenienceEntity getActiveConvenienceEntity();
	ConvenienceModel getActiveConvenienceModel() throws FormExceptions;
}