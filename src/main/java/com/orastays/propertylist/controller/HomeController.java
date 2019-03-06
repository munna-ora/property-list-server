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
import com.orastays.propertylist.model.OfferModel;
import com.orastays.propertylist.model.PriceCalculatorModel;
import com.orastays.propertylist.model.PropertyListViewModel;
import com.orastays.propertylist.model.ResponseModel;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
@Api(value = "Landing Page", tags = "Landing Page")
public class HomeController extends BaseController {

	private static final Logger logger = LogManager.getLogger(HomeController.class);
	
	@GetMapping(value = "/fetch-properties-by-type", produces = "application/json")
	@ApiOperation(value = "Fetch Properties By Type", response = ResponseModel.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Please Try after Sometime!!!"),
			@ApiResponse(code = 1908, message = "Please select property type id"),
			@ApiResponse(code = 1909, message = "Invalid property type id") })
	public ResponseEntity<ResponseModel> fetchPropertyByType(@RequestParam(value = "propertyTypeId", required = true) String propertyTypeId,
			@RequestParam(value = "userToken", required = false) String userToken) {

		if (logger.isInfoEnabled()) {
			logger.info("fetchPropertyByType -- START");
		}

		ResponseModel responseModel = new ResponseModel();
		Util.printLog(propertyTypeId, PropertyListConstant.INCOMING, "Fetch Properties By Type", request);
		try {
			long startTime = System.currentTimeMillis();
			List<PropertyListViewModel> propertyListViewModels = homeService.fetchPropertyByType(propertyTypeId, userToken);
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
					logger.info("FormExceptions in Fetch Properties By Type -- "+Util.errorToString(fe));
				}
				break;
			}
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception in fetchPropertyByType -- "+Util.errorToString(e));
			}
			responseModel.setResponseCode(messageUtil.getBundle(PropertyListConstant.COMMON_ERROR_CODE));
			responseModel.setResponseMessage(messageUtil.getBundle(PropertyListConstant.COMMON_ERROR_MESSAGE));
		}

		Util.printLog(responseModel, PropertyListConstant.OUTGOING, "Fetch Properties By Type", request);

		if (logger.isInfoEnabled()) {
			logger.info("fetchPropertyByType -- END");
		}
		
		if (responseModel.getResponseCode().equals(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_CODE))) {
			return new ResponseEntity<>(responseModel, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping(value = "/price-calculator", produces = "application/json")
	@ApiOperation(value = "Price Calculator", response = ResponseModel.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Please Try after Sometime!!!"),
			@ApiResponse(code = 1908, message = "Please select property type id"),
			@ApiResponse(code = 1909, message = "Invalid property type id") })
	public ResponseEntity<ResponseModel> priceCalculator(@RequestBody PriceCalculatorModel priceCalculatorModel) {

		if (logger.isInfoEnabled()) {
			logger.info("priceCalculator -- START");
		}

		ResponseModel responseModel = new ResponseModel();
		Util.printLog(priceCalculatorModel, PropertyListConstant.INCOMING, "Price Calculator", request);
		try {
			responseModel.setResponseBody(homeService.priceCalculator(priceCalculatorModel));
			responseModel.setResponseCode(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_CODE));
			responseModel.setResponseMessage(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_MESSAGE));
		} catch (FormExceptions fe) {

			for (Entry<String, Exception> entry : fe.getExceptions().entrySet()) {
				responseModel.setResponseCode(entry.getKey());
				responseModel.setResponseMessage(entry.getValue().getMessage());
				if (logger.isInfoEnabled()) {
					logger.info("FormExceptions in Price Calculator -- "+Util.errorToString(fe));
				}
				break;
			}
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception in priceCalculator -- "+Util.errorToString(e));
			}
			responseModel.setResponseCode(messageUtil.getBundle(PropertyListConstant.COMMON_ERROR_CODE));
			responseModel.setResponseMessage(messageUtil.getBundle(PropertyListConstant.COMMON_ERROR_MESSAGE));
		}

		Util.printLog(responseModel, PropertyListConstant.OUTGOING, "Price Calculator", request);

		if (logger.isInfoEnabled()) {
			logger.info("priceCalculator -- END");
		}
		
		if (responseModel.getResponseCode().equals(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_CODE))) {
			return new ResponseEntity<>(responseModel, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping(value = "/fetch-offer", produces = "application/json")
	@ApiOperation(value = "Fetch Offer", response = ResponseModel.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 201, message = "Please Try after Sometime!!!") })
	public ResponseEntity<ResponseModel> fetchOffer() {

		if (logger.isInfoEnabled()) {
			logger.info("fetchOffer -- START");
		}

		ResponseModel responseModel = new ResponseModel();
		Util.printLog(responseModel, PropertyListConstant.INCOMING, "Fetch Offer", request);
		try {
		
			List<OfferModel> propertyTypeModels = homeService.fetchOffer();
			responseModel.setResponseBody(propertyTypeModels);
			responseModel.setResponseCode(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_CODE));
			responseModel.setResponseMessage(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_MESSAGE));
			
		} catch (FormExceptions fe) {

			for (Entry<String, Exception> entry : fe.getExceptions().entrySet()) {
				responseModel.setResponseCode(entry.getKey());
				responseModel.setResponseMessage(entry.getValue().getMessage());
				if (logger.isInfoEnabled()) {
					logger.info("FormExceptions in Fetch Offer -- "+Util.errorToString(fe));
				}
				break;
			}
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception in Fetch Offer -- "+Util.errorToString(e));
			}
			responseModel.setResponseCode(messageUtil.getBundle(PropertyListConstant.COMMON_ERROR_CODE));
			responseModel.setResponseMessage(messageUtil.getBundle(PropertyListConstant.COMMON_ERROR_MESSAGE));
		}
		
		Util.printLog(responseModel, PropertyListConstant.OUTGOING, "Fetch Offer", request);
		
		if (logger.isInfoEnabled()) {
			logger.info("fetchOffer -- END");
		}
		
		if (responseModel.getResponseCode().equals(messageUtil.getBundle(PropertyListConstant.COMMON_SUCCESS_CODE))) {
			return new ResponseEntity<>(responseModel, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
		}
	}
}