package com.orastays.propertylist.controller;

import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.orastays.propertylist.exceptions.FormExceptions;
import com.orastays.propertylist.helper.PropertyListConstant;
import com.orastays.propertylist.helper.Util;
import com.orastays.propertylist.model.FilterCiteriaModel;
import com.orastays.propertylist.model.PropertyListViewModel;
import com.orastays.propertylist.model.PropertyModel;
import com.orastays.propertylist.model.ResponseModel;
import com.orastays.propertylist.model.booking.BookingVsRoomModel;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
@Api(value = "Property List", tags = "Property List")
public class PropertyListController extends BaseController {

	private static final Logger logger = LogManager.getLogger(PropertyListController.class);
	
	@PostMapping(value = "/fetch-properties", produces = "application/json")
	@ApiOperation(value = "Fetch Properties", response = ResponseModel.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Please Try after Sometime!!!"),
			@ApiResponse(code = 320, message = "Session expires!!! Please Login to continue..."),
			@ApiResponse(code = 321, message = "Please give User Token"),
			@ApiResponse(code = 322, message = "Invalid user Token") })
	public ResponseEntity<ResponseModel> fetchProperties(@RequestBody FilterCiteriaModel filterCiteriaModel) {

		if (logger.isInfoEnabled()) {
			logger.info("fetchProperties -- START");
		}

		ResponseModel responseModel = new ResponseModel();
		Util.printLog(filterCiteriaModel, PropertyListConstant.INCOMING, "Fetch Properties", request);
		try {
			long startTime = System.currentTimeMillis();
			List<PropertyListViewModel> propertyListViewModels = propertyService.fetchProperties(filterCiteriaModel);
			long elapsedTimeNs = System.currentTimeMillis() - startTime;
			System.err.println("Total Time Taken ==>> "+(elapsedTimeNs/1000));
			responseModel.setResponseBody(propertyListViewModels);
			responseModel.setResponseCode(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_CODE));
			responseModel.setResponseMessage(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_MESSAGE));
		} catch (FormExceptions fe) {

			for (Entry<String, Exception> entry : fe.getExceptions().entrySet()) {
				responseModel.setResponseCode(entry.getKey());
				responseModel.setResponseMessage(entry.getValue().getMessage());
				if (logger.isInfoEnabled()) {
					logger.info("FormExceptions in Fetch Properties -- "+Util.errorToString(fe));
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (logger.isInfoEnabled()) {
				logger.info("Exception in fetchProperties -- "+Util.errorToString(e));
			}
			responseModel.setResponseCode(messageUtil.getBundle(PropertyListConstant.COMMON_ERROR_CODE));
			responseModel.setResponseMessage(messageUtil.getBundle(PropertyListConstant.COMMON_ERROR_MESSAGE));
		}

		Util.printLog(responseModel, PropertyListConstant.OUTGOING, "Fetch Properties", request);

		if (logger.isInfoEnabled()) {
			logger.info("fetchProperties -- END");
		}
		
		if (responseModel.getResponseCode().equals(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_CODE))) {
			return new ResponseEntity<>(responseModel, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping(value = "/room-details-by-oraRoomName", produces = "application/json")
	@ApiOperation(value = "Room Details By Ora Room Name", response = ResponseModel.class)
	//@ApiIgnore
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Please Try after Sometime!!!") })
	public ResponseEntity<ResponseModel> roomDetailsByOraRoomName(@RequestBody List<BookingVsRoomModel> bookingVsRoomModels) {

		if (logger.isInfoEnabled()) {
			logger.info("roomDetailsByOraRoomName -- START");
		}

		ResponseModel responseModel = new ResponseModel();
		Util.printLog(bookingVsRoomModels, PropertyListConstant.INCOMING, "Room Details By Ora Room Name", request);
		try {
			List<BookingVsRoomModel> bookingVsRoomModels2 = propertyService.roomDetailsByOraRoomName(bookingVsRoomModels);
			responseModel.setResponseBody(bookingVsRoomModels2);
			responseModel.setResponseCode(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_CODE));
			responseModel.setResponseMessage(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_MESSAGE));
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception in roomDetailsByOraRoomName -- "+Util.errorToString(e));
			}
			responseModel.setResponseCode(messageUtil.getBundle(PropertyListConstant.COMMON_ERROR_CODE));
			responseModel.setResponseMessage(messageUtil.getBundle(PropertyListConstant.COMMON_ERROR_MESSAGE));
		}

		Util.printLog(responseModel, PropertyListConstant.OUTGOING, "Room Details By Ora Room Name", request);

		if (logger.isInfoEnabled()) {
			logger.info("roomDetailsByOraRoomName -- END");
		}
		
		if (responseModel.getResponseCode().equals(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_CODE))) {
			return new ResponseEntity<>(responseModel, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(value = "/fetch-property-by-id", produces = "application/json")
	@ApiIgnore
	@ApiOperation(value = "Fetch Property By Id", response = ResponseModel.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Please Try after Sometime!!!") })
	public ResponseEntity<ResponseModel> fetchPropertyById(@RequestParam(value = "propertyId", required = true) String propertyId) {

		if (logger.isInfoEnabled()) {
			logger.info("fetchPropertyById -- START");
		}

		ResponseModel responseModel = new ResponseModel();
		Util.printLog(propertyId, PropertyListConstant.INCOMING, "Fetch Property By Id", request);
		try {
			responseModel.setResponseBody(propertyService.fetchPropertyById(propertyId));
			responseModel.setResponseCode(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_CODE));
			responseModel.setResponseMessage(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_MESSAGE));
			
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception in Fetch Property by Id -- "+Util.errorToString(e));
			}
			responseModel.setResponseCode(messageUtil.getBundle(PropertyListConstant.COMMON_ERROR_CODE));
			responseModel.setResponseMessage(messageUtil.getBundle(PropertyListConstant.COMMON_ERROR_MESSAGE));
		}
		
		Util.printLog(responseModel, PropertyListConstant.OUTGOING, "Fetch Property By Id", request);
		
		if (logger.isInfoEnabled()) {
			logger.info("fetchPropertyById -- END");
		}
		
		if (responseModel.getResponseCode().equals(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_CODE))) {
			return new ResponseEntity<>(responseModel, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(value = "/budgets", produces = "application/json")
	@ApiOperation(value = "Budgets", response = ResponseModel.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Please Try after Sometime!!!") })
	public ResponseEntity<ResponseModel> budgets() {

		if (logger.isInfoEnabled()) {
			logger.info("budgets -- START");
		}

		ResponseModel responseModel = new ResponseModel();
		Util.printLog(null, PropertyListConstant.INCOMING, "Budgets", request);
		try {
			responseModel.setResponseBody(propertyService.budgets());
			responseModel.setResponseCode(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_CODE));
			responseModel.setResponseMessage(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_MESSAGE));
			
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception in Budgets -- "+Util.errorToString(e));
			}
			responseModel.setResponseCode(messageUtil.getBundle(PropertyListConstant.COMMON_ERROR_CODE));
			responseModel.setResponseMessage(messageUtil.getBundle(PropertyListConstant.COMMON_ERROR_MESSAGE));
		}
		
		Util.printLog(responseModel, PropertyListConstant.OUTGOING, "Budgets", request);
		
		if (logger.isInfoEnabled()) {
			logger.info("budgets -- END");
		}
		
		if (responseModel.getResponseCode().equals(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_CODE))) {
			return new ResponseEntity<>(responseModel, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(value = "/ratings", produces = "application/json")
	@ApiOperation(value = "Ratings", response = ResponseModel.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Please Try after Sometime!!!") })
	public ResponseEntity<ResponseModel> ratings() {

		if (logger.isInfoEnabled()) {
			logger.info("fetchPropertyById -- START");
		}

		ResponseModel responseModel = new ResponseModel();
		Util.printLog(null, PropertyListConstant.INCOMING, "Ratings", request);
		try {
			responseModel.setResponseBody(propertyService.ratings());
			responseModel.setResponseCode(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_CODE));
			responseModel.setResponseMessage(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_MESSAGE));
			
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception in Ratings -- "+Util.errorToString(e));
			}
			responseModel.setResponseCode(messageUtil.getBundle(PropertyListConstant.COMMON_ERROR_CODE));
			responseModel.setResponseMessage(messageUtil.getBundle(PropertyListConstant.COMMON_ERROR_MESSAGE));
		}
		
		Util.printLog(responseModel, PropertyListConstant.OUTGOING, "Ratings", request);
		
		if (logger.isInfoEnabled()) {
			logger.info("ratings -- END");
		}
		
		if (responseModel.getResponseCode().equals(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_CODE))) {
			return new ResponseEntity<>(responseModel, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping(value = "/fetch-property-details", produces = "application/json")
	@ApiOperation(value = "Fetch Property Details", response = ResponseModel.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Please Try after Sometime!!!"),
			@ApiResponse(code = 320, message = "Session expires!!! Please Login to continue..."),
			@ApiResponse(code = 321, message = "Please give User Token"),
			@ApiResponse(code = 322, message = "Invalid user Token") })
	public ResponseEntity<ResponseModel> fetchPropertyDetails(@RequestBody FilterCiteriaModel filterCiteriaModel) {

		if (logger.isInfoEnabled()) {
			logger.info("fetchPropertyDetails -- START");
		}

		ResponseModel responseModel = new ResponseModel();
		Util.printLog(filterCiteriaModel, PropertyListConstant.INCOMING, "Fetch Property Details", request);
		try {
			long startTime = System.currentTimeMillis();
			PropertyModel propertyModel = propertyService.fetchPropertyDetails(filterCiteriaModel);
			long elapsedTimeNs = System.currentTimeMillis() - startTime;
			System.err.println("Total Time Taken ==>> "+(elapsedTimeNs/1000));
			responseModel.setResponseBody(propertyModel);
			responseModel.setResponseCode(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_CODE));
			responseModel.setResponseMessage(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_MESSAGE));
		} catch (FormExceptions fe) {

			for (Entry<String, Exception> entry : fe.getExceptions().entrySet()) {
				responseModel.setResponseCode(entry.getKey());
				responseModel.setResponseMessage(entry.getValue().getMessage());
				if (logger.isInfoEnabled()) {
					logger.info("FormExceptions in Fetch Property Details -- "+Util.errorToString(fe));
				}
				break;
			}
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception in fetchPropertyDetails -- "+Util.errorToString(e));
			}
			responseModel.setResponseCode(messageUtil.getBundle(PropertyListConstant.COMMON_ERROR_CODE));
			responseModel.setResponseMessage(messageUtil.getBundle(PropertyListConstant.COMMON_ERROR_MESSAGE));
		}

		Util.printLog(responseModel, PropertyListConstant.OUTGOING, "Fetch Property Details", request);

		if (logger.isInfoEnabled()) {
			logger.info("fetchPropertyDetails -- END");
		}
		
		if (responseModel.getResponseCode().equals(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_CODE))) {
			return new ResponseEntity<>(responseModel, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping(value = "/fetch-price-details", produces = "application/json")
	@ApiOperation(value = "Fetch Price Details", response = ResponseModel.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Please Try after Sometime!!!"),
			@ApiResponse(code = 320, message = "Session expires!!! Please Login to continue..."),
			@ApiResponse(code = 321, message = "Please give User Token"),
			@ApiResponse(code = 322, message = "Invalid user Token") })
	public ResponseEntity<ResponseModel> fetchPriceDetails(@RequestBody FilterCiteriaModel filterCiteriaModel) {

		if (logger.isInfoEnabled()) {
			logger.info("fetchPriceDetails -- START");
		}

		ResponseModel responseModel = new ResponseModel();
		Util.printLog(filterCiteriaModel, PropertyListConstant.INCOMING, "Fetch Price Details", request);
		try {
			long startTime = System.currentTimeMillis();
			PropertyModel propertyModel = propertyService.fetchPriceDetails(filterCiteriaModel);
			long elapsedTimeNs = System.currentTimeMillis() - startTime;
			System.err.println("Total Time Taken ==>> "+(elapsedTimeNs/1000));
			responseModel.setResponseBody(propertyModel);
			responseModel.setResponseCode(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_CODE));
			responseModel.setResponseMessage(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_MESSAGE));
		} catch (FormExceptions fe) {

			for (Entry<String, Exception> entry : fe.getExceptions().entrySet()) {
				responseModel.setResponseCode(entry.getKey());
				responseModel.setResponseMessage(entry.getValue().getMessage());
				if (logger.isInfoEnabled()) {
					logger.info("FormExceptions in Fetch Price Details -- "+Util.errorToString(fe));
				}
				break;
			}
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception in fetchPriceDetails -- "+Util.errorToString(e));
			}
			responseModel.setResponseCode(messageUtil.getBundle(PropertyListConstant.COMMON_ERROR_CODE));
			responseModel.setResponseMessage(messageUtil.getBundle(PropertyListConstant.COMMON_ERROR_MESSAGE));
		}

		Util.printLog(responseModel, PropertyListConstant.OUTGOING, "Fetch Price Details", request);

		if (logger.isInfoEnabled()) {
			logger.info("fetchPriceDetails -- END");
		}
		
		if (responseModel.getResponseCode().equals(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_CODE))) {
			return new ResponseEntity<>(responseModel, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
		}
	}
}