package com.orastays.propertylist.service;

import com.orastays.propertylist.exceptions.FormExceptions;
import com.orastays.propertylist.model.FilterCiteriaModel;
import com.orastays.propertylist.model.booking.PaymentModel;

public interface BookingService {

	PaymentModel propertyBooking(FilterCiteriaModel filterCiteriaModel) throws FormExceptions;
}