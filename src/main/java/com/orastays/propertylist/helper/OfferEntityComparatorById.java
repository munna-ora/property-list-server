package com.orastays.propertylist.helper;

import java.util.Comparator;

import com.orastays.propertylist.entity.OfferEntity;

public class OfferEntityComparatorById implements Comparator<OfferEntity> {

	@Override
	public int compare(OfferEntity obj1, OfferEntity obj2) {

		if (obj1.getOfferId() > obj2.getOfferId())
			return 1;
		else if (obj1.getOfferId() < obj2.getOfferId())
			return -1;
		else
			return 0;
	}
}
