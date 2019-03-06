package com.orastays.propertylist.service;

import com.orastays.propertylist.entity.GstSlabEntity;
import com.orastays.propertylist.exceptions.FormExceptions;
import com.orastays.propertylist.model.GstSlabModel;

public interface GstSlabService {

	GstSlabEntity getActiveGstEntity(Double amount) throws FormExceptions;
	GstSlabModel getActiveGstModel(Double amount) throws FormExceptions;
}