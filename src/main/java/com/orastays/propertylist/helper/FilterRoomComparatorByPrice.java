package com.orastays.propertylist.helper;

import java.util.Comparator;

import com.orastays.propertylist.model.FilterRoomModel;

public class FilterRoomComparatorByPrice implements Comparator<FilterRoomModel> {

	@Override
	public int compare(FilterRoomModel obj1, FilterRoomModel obj2) {

		if (obj1.getTotalPrice() > obj2.getTotalPrice())
			return 1;
		else if (obj1.getTotalPrice() < obj2.getTotalPrice())
			return -1;
		else
			return 0;
	}
}
