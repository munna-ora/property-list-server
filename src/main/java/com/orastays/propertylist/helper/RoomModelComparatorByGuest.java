package com.orastays.propertylist.helper;

import java.util.Comparator;

import com.orastays.propertylist.model.RoomModel;

public class RoomModelComparatorByGuest implements Comparator<RoomModel> {

	@Override
	public int compare(RoomModel obj1, RoomModel obj2) {

		if (Integer.parseInt(obj1.getNoOfGuest()) > Integer.parseInt(obj2.getNoOfGuest()))
			return 1;
		else if (Integer.parseInt(obj1.getNoOfGuest()) < Integer.parseInt(obj2.getNoOfGuest()))
			return -1;
		else
			return 0;
	}
}
