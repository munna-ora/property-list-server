package com.orastays.propertylist.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.orastays.propertylist.helper.MessageUtil;
import com.orastays.propertylist.service.BookingService;
import com.orastays.propertylist.service.BookmarkService;
import com.orastays.propertylist.service.HomeService;
import com.orastays.propertylist.service.PropertyListService;

public class BaseController {

	@Autowired
	protected HttpServletRequest request;
	
	@Autowired
	protected HttpServletResponse response;
	
	@Autowired
	protected PropertyListService propertyService;
	
	@Autowired
	protected HomeService homeService;
	
	@Autowired
	protected BookingService bookingService;
	
	@Autowired
	protected BookmarkService bookmarkService;
	
	@Autowired
	protected MessageUtil messageUtil;
}
