package com.orastays.propertylist.service;

import java.util.List;

import com.orastays.propertylist.exceptions.FormExceptions;
import com.orastays.propertylist.model.OfferModel;
import com.orastays.propertylist.model.PriceCalculatorModel;
import com.orastays.propertylist.model.PropertyListViewModel;

public interface HomeService {

	List<PropertyListViewModel> fetchPropertyByType(String propertyTypeId, String userToken) throws FormExceptions;

	Object priceCalculator(PriceCalculatorModel priceCalculatorModel) throws FormExceptions;
	
	List<OfferModel> fetchOffer() throws FormExceptions;
	
	
}