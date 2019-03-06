package com.orastays.propertylist.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.orastays.propertylist.entity.RoomVsOfferEntity;
import com.orastays.propertylist.helper.Status;
import com.orastays.propertylist.helper.Util;
import com.orastays.propertylist.model.RoomVsOfferModel;

@Component
public class RoomVsOfferConverter extends CommonConverter implements BaseConverter<RoomVsOfferEntity, RoomVsOfferModel> {

	private static final long serialVersionUID = -3679360384548776394L;
	private static final Logger logger = LogManager.getLogger(RoomVsOfferConverter.class);

	@Override
	public RoomVsOfferEntity modelToEntity(RoomVsOfferModel m) {

		if (logger.isInfoEnabled()) {
			logger.info("entityToModel -- START");
		}
		
		RoomVsOfferEntity roomVsOfferEntity = new RoomVsOfferEntity();
		roomVsOfferEntity = (RoomVsOfferEntity) Util.transform(modelMapper, m, roomVsOfferEntity);
		roomVsOfferEntity.setStatus(Status.ACTIVE.ordinal());
		roomVsOfferEntity.setCreatedDate(Util.getCurrentDateTime());
		
		if (logger.isInfoEnabled()) {
			logger.info("entityToModel -- END");
		}
		
		return roomVsOfferEntity;
	}

	@Override
	public RoomVsOfferModel entityToModel(RoomVsOfferEntity e) {
		
		if (logger.isInfoEnabled()) {
			logger.info("entityToModel -- START");
		}
		
		RoomVsOfferModel roomVsOfferModel = null;
		
		if(Objects.nonNull(e)) {
			roomVsOfferModel = new RoomVsOfferModel();
			roomVsOfferModel = (RoomVsOfferModel) Util.transform(modelMapper, e, roomVsOfferModel);
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("entityToModel -- END");
		}
		
		return roomVsOfferModel;
	}

	@Override
	public List<RoomVsOfferModel> entityListToModelList(List<RoomVsOfferEntity> es) {
		
		if (logger.isInfoEnabled()) {
			logger.info("entityListToModelList -- START");
		}
		
		List<RoomVsOfferModel> roomVsOfferModels = null;
		if(!CollectionUtils.isEmpty(es)) {
			roomVsOfferModels = new ArrayList<>();
			for(RoomVsOfferEntity roomVsOfferEntity:es) {
				roomVsOfferModels.add(entityToModel(roomVsOfferEntity));
			}
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("entityListToModelList -- END");
		}
		
		return roomVsOfferModels;
	}

}
