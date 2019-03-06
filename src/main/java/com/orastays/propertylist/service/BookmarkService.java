package com.orastays.propertylist.service;

import com.orastays.propertylist.exceptions.FormExceptions;
import com.orastays.propertylist.model.WishlistModel;

public interface BookmarkService {

	void addWishlist(WishlistModel wishlistModel) throws FormExceptions;

}
