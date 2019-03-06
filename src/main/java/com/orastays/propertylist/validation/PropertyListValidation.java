package com.orastays.propertylist.validation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.orastays.propertylist.entity.PropertyEntity;
import com.orastays.propertylist.entity.PropertyTypeEntity;
import com.orastays.propertylist.entity.RoomEntity;
import com.orastays.propertylist.exceptions.FormExceptions;
import com.orastays.propertylist.helper.PropertyListConstant;
import com.orastays.propertylist.helper.Status;
import com.orastays.propertylist.helper.Util;
import com.orastays.propertylist.model.FilterCiteriaModel;
import com.orastays.propertylist.model.RoomModel;
import com.orastays.propertylist.model.user.UserModel;

@Component
public class PropertyListValidation extends AuthorizeUserValidation {

	private static final Logger logger = LogManager.getLogger(PropertyListValidation.class);
	
	public UserModel validateFetchProperties(FilterCiteriaModel filterCiteriaModel) throws FormExceptions {

		if (logger.isInfoEnabled()) {
			logger.info("validateFetchProperties -- Start");
		}

		UserModel userModel = null;
		if (!StringUtils.isBlank(filterCiteriaModel.getUserToken())) {
			userModel = getUserDetails(filterCiteriaModel.getUserToken());
		}
		
		Map<String, Exception> exceptions = new LinkedHashMap<>();
		
		// Validate Property Type
		if (StringUtils.isBlank(filterCiteriaModel.getPropertyTypeId())) {
			exceptions.put(messageUtil.getBundle("property.type.id.null.code"), new Exception(messageUtil.getBundle("property.type.id.null.message")));
		} else {
			if (!Util.isNumeric(filterCiteriaModel.getPropertyTypeId())) {
				exceptions.put(messageUtil.getBundle("property.type.id.invalid.code"), new Exception(messageUtil.getBundle("property.type.id.invalid.message")));
			} else {
				PropertyTypeEntity propertyTypeEntity = propertyTypeDAO.find(Long.parseLong(filterCiteriaModel.getPropertyTypeId()));
				if (Objects.isNull(propertyTypeEntity) && propertyTypeEntity.getStatus() != Status.ACTIVE.ordinal()) {
					exceptions.put(messageUtil.getBundle("property.type.id.invalid.code"), new Exception(messageUtil.getBundle("property.type.id.invalid.message")));
				}
			}
		}
		
		// Validate Latitude
		if (StringUtils.isBlank(filterCiteriaModel.getLatitude())) {
			exceptions.put(messageUtil.getBundle("latitude.null.code"), new Exception(messageUtil.getBundle("latitude.null.message")));
		} else {
			if (!Util.checkLatitude(filterCiteriaModel.getLatitude())) {
				exceptions.put(messageUtil.getBundle("latitude.invalid.code"), new Exception(messageUtil.getBundle("latitude.invalid.message")));
			}
		}

		// Validate Longitude
		if (StringUtils.isBlank(filterCiteriaModel.getLongitude())) {
			exceptions.put(messageUtil.getBundle("longitude.null.code"), new Exception(messageUtil.getBundle("longitude.null.message")));
		} else {
			if (!Util.checkLongitude(filterCiteriaModel.getLongitude())) {
				exceptions.put(messageUtil.getBundle("longitude.invalid.code"), new Exception(messageUtil.getBundle("longitude.invalid.message")));
			}
		}
		
		// Validate CheckIn Date
		if (StringUtils.isBlank(filterCiteriaModel.getCheckInDate())) {
			exceptions.put(messageUtil.getBundle("checkin.date.null.code"), new Exception(messageUtil.getBundle("checkin.date.null.message")));
		} else {
			if (!Util.checkOnlyDate(filterCiteriaModel.getCheckInDate())) {
				exceptions.put(messageUtil.getBundle("checkin.date.invalid.code"), new Exception(messageUtil.getBundle("checkin.date.invalid.message")));
			} else {
				if (Util.getMinuteDiffWithDate(filterCiteriaModel.getCheckInDate()) > 0) {
					exceptions.put(messageUtil.getBundle("checkin.date.invalid.code"), new Exception(messageUtil.getBundle("checkin.date.invalid.message")));
				}
			}
		}

		// Validate CheckOut Date
		if (StringUtils.isBlank(filterCiteriaModel.getCheckOutDate())) {
			exceptions.put(messageUtil.getBundle("checkout.date.null.code"), new Exception(messageUtil.getBundle("checkout.date.null.message")));
		} else {
			if (!Util.checkOnlyDate(filterCiteriaModel.getCheckOutDate())) {
				exceptions.put(messageUtil.getBundle("checkout.date.invalid.code"), new Exception(messageUtil.getBundle("checkout.date.invalid.message")));
			} else {
				if (Util.getMinuteDiffWithDate(filterCiteriaModel.getCheckOutDate()) > 0) {
					exceptions.put(messageUtil.getBundle("checkout.date.invalid.code"), new Exception(messageUtil.getBundle("checkout.date.invalid.message")));
				}
			}
		}
		
		// Validate Stay Type
		if (StringUtils.isBlank(filterCiteriaModel.getStayType())) {
			exceptions.put(messageUtil.getBundle("staytype.null.code"), new Exception(messageUtil.getBundle("staytype.null.message")));
		} else {
			if (StringUtils.equals(filterCiteriaModel.getStayType(), PropertyListConstant.SHARED) || StringUtils.equals(filterCiteriaModel.getStayType(), PropertyListConstant.PRIVATE)) {
				if (StringUtils.equals(filterCiteriaModel.getStayType(), PropertyListConstant.SHARED)) { // Customer Wants SHARED Rooms
					
					if(StringUtils.isBlank(filterCiteriaModel.getNoOfGuest())) {
						exceptions.put(messageUtil.getBundle("noofguest.null.code"), new Exception(messageUtil.getBundle("noofguest.null.message")));
					} else {
						if ((!Util.isNumeric(filterCiteriaModel.getNoOfGuest())) && (Integer.parseInt(filterCiteriaModel.getNoOfGuest()) > 0)) {
							exceptions.put(messageUtil.getBundle("noofguest.invalid.code"), new Exception(messageUtil.getBundle("noofguest.invalid.message")));
						}
					}
				} else { // Customer Wants PRIVATE Rooms
					
					// Validate Room Details
					if(CollectionUtils.isEmpty(filterCiteriaModel.getRoomModels())) {
						exceptions.put(messageUtil.getBundle("room.details.null.code"), new Exception(messageUtil.getBundle("room.details.null.message")));
					} else {
						int noOfGuest = 0;
						for(RoomModel roomModel : filterCiteriaModel.getRoomModels()) {
							if(Objects.isNull(roomModel)) {
								exceptions.put(messageUtil.getBundle("room.details.null.code"), new Exception(messageUtil.getBundle("room.details.null.message")));
							} else {
								
								if(StringUtils.isBlank(roomModel.getNoOfGuest())) {
									exceptions.put(messageUtil.getBundle("noofguest.null.code"), new Exception(messageUtil.getBundle("noofguest.null.message")));
								} else {
									if ((!Util.isNumeric(roomModel.getNoOfGuest())) || (Integer.parseInt(roomModel.getNoOfGuest()) < 0)) {
										exceptions.put(messageUtil.getBundle("noofguest.invalid.code"), new Exception(messageUtil.getBundle("noofguest.invalid.message")));
									} else {
										noOfGuest = noOfGuest + Integer.parseInt(roomModel.getNoOfGuest());
									}
								}
								
								if(!StringUtils.isBlank(roomModel.getNoOfChild())) {
									if ((!Util.isNumeric(roomModel.getNoOfChild())) || (Integer.parseInt(roomModel.getNoOfChild()) < 0)) {
										exceptions.put(messageUtil.getBundle("noofchild.invalid.code"), new Exception(messageUtil.getBundle("noofchild.invalid.message")));
									}
								}
							}
						}
						filterCiteriaModel.setNoOfGuest(String.valueOf(noOfGuest));
					}
				}
			} else {
				exceptions.put(messageUtil.getBundle("staytype.invalid.code"), new Exception(messageUtil.getBundle("staytype.invalid.message")));
			}
		}
		
		if (exceptions.size() > 0)
			throw new FormExceptions(exceptions);
		else {
			if(Util.getDayDiff(filterCiteriaModel.getCheckInDate(), filterCiteriaModel.getCheckOutDate()) <= 0) {
				exceptions.put(messageUtil.getBundle("checkout.date.lesser.code"), new Exception(messageUtil.getBundle("checkout.date.lesser.message")));
				throw new FormExceptions(exceptions);
			}
		}

		if (logger.isInfoEnabled()) {
			logger.info("validateFetchProperties -- End");
		}
		
		return userModel;
	}
	
	public UserModel validateFetchPropertyDetails(FilterCiteriaModel filterCiteriaModel) throws FormExceptions {

		if (logger.isInfoEnabled()) {
			logger.info("validateFetchPropertyDetails -- Start");
		}

		UserModel userModel = null;
		if (!StringUtils.isBlank(filterCiteriaModel.getUserToken())) {
			userModel = getUserDetails(filterCiteriaModel.getUserToken());
		}
		
		Map<String, Exception> exceptions = new LinkedHashMap<>();
		PropertyEntity propertyEntity = null;
		
		// Validate Property
		if (StringUtils.isBlank(filterCiteriaModel.getPropertyId())) {
			exceptions.put(messageUtil.getBundle("property.id.null.code"), new Exception(messageUtil.getBundle("property.id.null.message")));
		} else {
			if (!Util.isNumeric(filterCiteriaModel.getPropertyId())) {
				exceptions.put(messageUtil.getBundle("property.id.invalid.code"), new Exception(messageUtil.getBundle("property.id.invalid.message")));
			} else {
				propertyEntity = propertyDAO.find(Long.parseLong(filterCiteriaModel.getPropertyId()));
				if (Objects.isNull(propertyEntity) && propertyEntity.getStatus() != Status.ACTIVE.ordinal()) {
					exceptions.put(messageUtil.getBundle("property.id.invalid.code"), new Exception(messageUtil.getBundle("property.id.invalid.message")));
				}
			}
		}
		
		// Validate Property Type
		if (StringUtils.isBlank(filterCiteriaModel.getPropertyTypeId())) {
			exceptions.put(messageUtil.getBundle("property.type.id.null.code"), new Exception(messageUtil.getBundle("property.type.id.null.message")));
		} else {
			if (!Util.isNumeric(filterCiteriaModel.getPropertyTypeId())) {
				exceptions.put(messageUtil.getBundle("property.type.id.invalid.code"), new Exception(messageUtil.getBundle("property.type.id.invalid.message")));
			} else {
				PropertyTypeEntity propertyTypeEntity = propertyTypeDAO.find(Long.parseLong(filterCiteriaModel.getPropertyTypeId()));
				if (Objects.isNull(propertyTypeEntity) && propertyTypeEntity.getStatus() != Status.ACTIVE.ordinal()) {
					exceptions.put(messageUtil.getBundle("property.type.id.invalid.code"), new Exception(messageUtil.getBundle("property.type.id.invalid.message")));
				}
			}
		}
		
		// Validate Latitude
		if (StringUtils.isBlank(filterCiteriaModel.getLatitude())) {
			exceptions.put(messageUtil.getBundle("latitude.null.code"), new Exception(messageUtil.getBundle("latitude.null.message")));
		} else {
			if (!Util.checkLatitude(filterCiteriaModel.getLatitude())) {
				exceptions.put(messageUtil.getBundle("latitude.invalid.code"), new Exception(messageUtil.getBundle("latitude.invalid.message")));
			}
		}

		// Validate Longitude
		if (StringUtils.isBlank(filterCiteriaModel.getLongitude())) {
			exceptions.put(messageUtil.getBundle("longitude.null.code"), new Exception(messageUtil.getBundle("longitude.null.message")));
		} else {
			if (!Util.checkLongitude(filterCiteriaModel.getLongitude())) {
				exceptions.put(messageUtil.getBundle("longitude.invalid.code"), new Exception(messageUtil.getBundle("longitude.invalid.message")));
			}
		}
		
		// Validate CheckIn Date
		if (StringUtils.isBlank(filterCiteriaModel.getCheckInDate())) {
			exceptions.put(messageUtil.getBundle("checkin.date.null.code"), new Exception(messageUtil.getBundle("checkin.date.null.message")));
		} else {
			if (!Util.checkOnlyDate(filterCiteriaModel.getCheckInDate())) {
				exceptions.put(messageUtil.getBundle("checkin.date.invalid.code"), new Exception(messageUtil.getBundle("checkin.date.invalid.message")));
			} else {
				if (Util.getMinuteDiffWithDate(filterCiteriaModel.getCheckInDate()) > 0) {
					exceptions.put(messageUtil.getBundle("checkin.date.invalid.code"), new Exception(messageUtil.getBundle("checkin.date.invalid.message")));
				}
			}
		}

		// Validate CheckOut Date
		if (StringUtils.isBlank(filterCiteriaModel.getCheckOutDate())) {
			exceptions.put(messageUtil.getBundle("checkout.date.null.code"), new Exception(messageUtil.getBundle("checkout.date.null.message")));
		} else {
			if (!Util.checkOnlyDate(filterCiteriaModel.getCheckOutDate())) {
				exceptions.put(messageUtil.getBundle("checkout.date.invalid.code"), new Exception(messageUtil.getBundle("checkout.date.invalid.message")));
			} else {
				if (Util.getMinuteDiffWithDate(filterCiteriaModel.getCheckOutDate()) > 0) {
					exceptions.put(messageUtil.getBundle("checkout.date.invalid.code"), new Exception(messageUtil.getBundle("checkout.date.invalid.message")));
				}
			}
		}
		
		// Validate Stay Type
		if (StringUtils.isBlank(filterCiteriaModel.getStayType())) {
			exceptions.put(messageUtil.getBundle("staytype.null.code"), new Exception(messageUtil.getBundle("staytype.null.message")));
		} else {
			if (StringUtils.equals(filterCiteriaModel.getStayType(), PropertyListConstant.SHARED) || StringUtils.equals(filterCiteriaModel.getStayType(), PropertyListConstant.PRIVATE)) {
				if (StringUtils.equals(filterCiteriaModel.getStayType(), PropertyListConstant.SHARED)) { // Customer Wants SHARED Rooms
					
					if(StringUtils.isBlank(filterCiteriaModel.getNoOfGuest())) {
						exceptions.put(messageUtil.getBundle("noofguest.null.code"), new Exception(messageUtil.getBundle("noofguest.null.message")));
					} else {
						if ((!Util.isNumeric(filterCiteriaModel.getNoOfGuest())) && (Integer.parseInt(filterCiteriaModel.getNoOfGuest()) > 0)) {
							exceptions.put(messageUtil.getBundle("noofguest.invalid.code"), new Exception(messageUtil.getBundle("noofguest.invalid.message")));
						}
					}
				} else { // Customer Wants PRIVATE Rooms
					
					// Validate Room Details
					if(CollectionUtils.isEmpty(filterCiteriaModel.getRoomModels())) {
						exceptions.put(messageUtil.getBundle("room.details.null.code"), new Exception(messageUtil.getBundle("room.details.null.message")));
					} else {
						int noOfGuest = 0;
						for(RoomModel roomModel : filterCiteriaModel.getRoomModels()) {
							if(Objects.isNull(roomModel)) {
								exceptions.put(messageUtil.getBundle("room.details.null.code"), new Exception(messageUtil.getBundle("room.details.null.message")));
							} else {
								
								if(StringUtils.isBlank(roomModel.getNoOfGuest())) {
									exceptions.put(messageUtil.getBundle("noofguest.null.code"), new Exception(messageUtil.getBundle("noofguest.null.message")));
								} else {
									if ((!Util.isNumeric(roomModel.getNoOfGuest())) || (Integer.parseInt(roomModel.getNoOfGuest()) < 0)) {
										exceptions.put(messageUtil.getBundle("noofguest.invalid.code"), new Exception(messageUtil.getBundle("noofguest.invalid.message")));
									} else {
										noOfGuest = noOfGuest + Integer.parseInt(roomModel.getNoOfGuest());
									}
								}
								
								if(!StringUtils.isBlank(roomModel.getNoOfChild())) {
									if ((!Util.isNumeric(roomModel.getNoOfChild())) || (Integer.parseInt(roomModel.getNoOfChild()) < 0)) {
										exceptions.put(messageUtil.getBundle("noofchild.invalid.code"), new Exception(messageUtil.getBundle("noofchild.invalid.message")));
									}
								}
							}
						}
						filterCiteriaModel.setNoOfGuest(String.valueOf(noOfGuest));
					}
				}
			} else {
				exceptions.put(messageUtil.getBundle("staytype.invalid.code"), new Exception(messageUtil.getBundle("staytype.invalid.message")));
			}
		}
		
		if (exceptions.size() > 0)
			throw new FormExceptions(exceptions);
		else {
			if(Util.getDayDiff(filterCiteriaModel.getCheckInDate(), filterCiteriaModel.getCheckOutDate()) <= 0) {
				exceptions.put(messageUtil.getBundle("checkout.date.lesser.code"), new Exception(messageUtil.getBundle("checkout.date.lesser.message")));
				throw new FormExceptions(exceptions);
			}
		}

		if (logger.isInfoEnabled()) {
			logger.info("validateFetchPropertyDetails -- End");
		}
		
		return userModel;
	}

	public UserModel validateFetchPriceDetails(FilterCiteriaModel filterCiteriaModel) throws FormExceptions {

		if (logger.isInfoEnabled()) {
			logger.info("validateFetchPriceDetails -- Start");
		}

		UserModel userModel = null;
		if (!StringUtils.isBlank(filterCiteriaModel.getUserToken())) {
			userModel = getUserDetails(filterCiteriaModel.getUserToken());
		}
		
		Map<String, Exception> exceptions = new LinkedHashMap<>();
		PropertyEntity propertyEntity = null;
		
		// Validate Property
		if (StringUtils.isBlank(filterCiteriaModel.getPropertyId())) {
			exceptions.put(messageUtil.getBundle("property.id.null.code"), new Exception(messageUtil.getBundle("property.id.null.message")));
		} else {
			if (!Util.isNumeric(filterCiteriaModel.getPropertyId())) {
				exceptions.put(messageUtil.getBundle("property.id.invalid.code"), new Exception(messageUtil.getBundle("property.id.invalid.message")));
			} else {
				propertyEntity = propertyDAO.find(Long.parseLong(filterCiteriaModel.getPropertyId()));
				if (Objects.isNull(propertyEntity) && propertyEntity.getStatus() != Status.ACTIVE.ordinal()) {
					exceptions.put(messageUtil.getBundle("property.id.invalid.code"), new Exception(messageUtil.getBundle("property.id.invalid.message")));
				}
			}
		}
		
		// Validate Property Type
		if (StringUtils.isBlank(filterCiteriaModel.getPropertyTypeId())) {
			exceptions.put(messageUtil.getBundle("property.type.id.null.code"), new Exception(messageUtil.getBundle("property.type.id.null.message")));
		} else {
			if (!Util.isNumeric(filterCiteriaModel.getPropertyTypeId())) {
				exceptions.put(messageUtil.getBundle("property.type.id.invalid.code"), new Exception(messageUtil.getBundle("property.type.id.invalid.message")));
			} else {
				PropertyTypeEntity propertyTypeEntity = propertyTypeDAO.find(Long.parseLong(filterCiteriaModel.getPropertyTypeId()));
				if (Objects.isNull(propertyTypeEntity) && propertyTypeEntity.getStatus() != Status.ACTIVE.ordinal()) {
					exceptions.put(messageUtil.getBundle("property.type.id.invalid.code"), new Exception(messageUtil.getBundle("property.type.id.invalid.message")));
				}
			}
		}
		
		// Validate Latitude
		if (StringUtils.isBlank(filterCiteriaModel.getLatitude())) {
			exceptions.put(messageUtil.getBundle("latitude.null.code"), new Exception(messageUtil.getBundle("latitude.null.message")));
		} else {
			if (!Util.checkLatitude(filterCiteriaModel.getLatitude())) {
				exceptions.put(messageUtil.getBundle("latitude.invalid.code"), new Exception(messageUtil.getBundle("latitude.invalid.message")));
			}
		}

		// Validate Longitude
		if (StringUtils.isBlank(filterCiteriaModel.getLongitude())) {
			exceptions.put(messageUtil.getBundle("longitude.null.code"), new Exception(messageUtil.getBundle("longitude.null.message")));
		} else {
			if (!Util.checkLongitude(filterCiteriaModel.getLongitude())) {
				exceptions.put(messageUtil.getBundle("longitude.invalid.code"), new Exception(messageUtil.getBundle("longitude.invalid.message")));
			}
		}
		
		// Validate CheckIn Date
		if (StringUtils.isBlank(filterCiteriaModel.getCheckInDate())) {
			exceptions.put(messageUtil.getBundle("checkin.date.null.code"), new Exception(messageUtil.getBundle("checkin.date.null.message")));
		} else {
			if (!Util.checkOnlyDate(filterCiteriaModel.getCheckInDate())) {
				exceptions.put(messageUtil.getBundle("checkin.date.invalid.code"), new Exception(messageUtil.getBundle("checkin.date.invalid.message")));
			} else {
				if (Util.getMinuteDiffWithDate(filterCiteriaModel.getCheckInDate()) > 0) {
					exceptions.put(messageUtil.getBundle("checkin.date.invalid.code"), new Exception(messageUtil.getBundle("checkin.date.invalid.message")));
				}
			}
		}

		// Validate CheckOut Date
		if (StringUtils.isBlank(filterCiteriaModel.getCheckOutDate())) {
			exceptions.put(messageUtil.getBundle("checkout.date.null.code"), new Exception(messageUtil.getBundle("checkout.date.null.message")));
		} else {
			if (!Util.checkOnlyDate(filterCiteriaModel.getCheckOutDate())) {
				exceptions.put(messageUtil.getBundle("checkout.date.invalid.code"), new Exception(messageUtil.getBundle("checkout.date.invalid.message")));
			} else {
				if (Util.getMinuteDiffWithDate(filterCiteriaModel.getCheckOutDate()) > 0) {
					exceptions.put(messageUtil.getBundle("checkout.date.invalid.code"), new Exception(messageUtil.getBundle("checkout.date.invalid.message")));
				}
			}
		}
		
		// Validate Stay Type
		if (StringUtils.isBlank(filterCiteriaModel.getStayType())) {
			exceptions.put(messageUtil.getBundle("staytype.null.code"), new Exception(messageUtil.getBundle("staytype.null.message")));
		} else {
			if (StringUtils.equals(filterCiteriaModel.getStayType(), PropertyListConstant.SHARED) || StringUtils.equals(filterCiteriaModel.getStayType(), PropertyListConstant.PRIVATE)) {
				// Validate Room Details
				if(CollectionUtils.isEmpty(filterCiteriaModel.getRoomModels())) {
					exceptions.put(messageUtil.getBundle("room.details.null.code"), new Exception(messageUtil.getBundle("room.details.null.message")));
				} else {
					int noOfGuest = 0;
					for(RoomModel roomModel : filterCiteriaModel.getRoomModels()) {
						if(Objects.isNull(roomModel)) {
							exceptions.put(messageUtil.getBundle("room.details.null.code"), new Exception(messageUtil.getBundle("room.details.null.message")));
						} else {
							
							if(StringUtils.isBlank(roomModel.getOraRoomName())) {
								exceptions.put(messageUtil.getBundle("ora.name.null.code"), new Exception(messageUtil.getBundle("ora.name.null.message")));
							} else {
								if(StringUtils.isBlank(roomModel.getNoOfGuest())) {
									exceptions.put(messageUtil.getBundle("noofguest.null.code"), new Exception(messageUtil.getBundle("noofguest.null.message")));
								} else {
									if ((!Util.isNumeric(roomModel.getNoOfGuest())) || (Integer.parseInt(roomModel.getNoOfGuest()) < 0)) {
										exceptions.put(messageUtil.getBundle("noofguest.invalid.code"), new Exception(messageUtil.getBundle("noofguest.invalid.message")));
									} else {
										noOfGuest = noOfGuest + Integer.parseInt(roomModel.getNoOfGuest());
									}
								}
								
								if(!StringUtils.isBlank(roomModel.getNoOfChild())) {
									if ((!Util.isNumeric(roomModel.getNoOfChild())) || (Integer.parseInt(roomModel.getNoOfChild()) < 0)) {
										exceptions.put(messageUtil.getBundle("noofchild.invalid.code"), new Exception(messageUtil.getBundle("noofchild.invalid.message")));
									}
								}
							}
						}
					}
					filterCiteriaModel.setNoOfGuest(String.valueOf(noOfGuest));
				}
			} else {
				exceptions.put(messageUtil.getBundle("staytype.invalid.code"), new Exception(messageUtil.getBundle("staytype.invalid.message")));
			}
		}
		
		if (exceptions.size() > 0)
			throw new FormExceptions(exceptions);
		else {
			if(Util.getDayDiff(filterCiteriaModel.getCheckInDate(), filterCiteriaModel.getCheckOutDate()) <= 0) {
				exceptions.put(messageUtil.getBundle("checkout.date.lesser.code"), new Exception(messageUtil.getBundle("checkout.date.lesser.message")));
				throw new FormExceptions(exceptions);
			} else {
				if(!checkRooms(filterCiteriaModel, propertyEntity)) {
					exceptions.put(messageUtil.getBundle("property.notavailable.code"), new Exception(messageUtil.getBundle("property.notavailable.message")));
					throw new FormExceptions(exceptions);
				}
			}
		}

		if (logger.isInfoEnabled()) {
			logger.info("validateFetchPriceDetails -- End");
		}
		
		return userModel;
	}
	
	private Boolean checkRooms(FilterCiteriaModel filterCiteriaModel, PropertyEntity propertyEntity) {
		
		if (logger.isInfoEnabled()) {
			logger.info("checkRooms -- END");
		}
		
		boolean flag = true;
		List<Boolean> count = new ArrayList<>();
		Map<String, RoomEntity> totalRooms = new ConcurrentHashMap<>();
		propertyEntity.getRoomEntities().parallelStream().forEach(roomEntity -> {
			if(roomEntity.getStatus() == Status.ACTIVE.ordinal()) { // Fetching only ACTIVE Rooms
				totalRooms.put(roomEntity.getOraRoomName(), roomEntity);
			}
		});
		
		for(RoomModel roomModel : filterCiteriaModel.getRoomModels()) {
			if(!totalRooms.containsKey(roomModel.getOraRoomName())) {
				count.add(false);
				break;
			}
		}
		
		if(count.contains(false)) {
			flag = false;
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("checkRooms -- END");
		}
		
		return flag;
	}
}