package com.orastays.propertylist.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.orastays.propertylist.entity.RoomVsSpecialitiesEntity;
import com.orastays.propertylist.helper.Status;
import com.orastays.propertylist.helper.Util;
import com.orastays.propertylist.model.RoomVsSpecialitiesModel;

@Component
public class RoomVsSpecialitiesConverter extends CommonConverter
		implements BaseConverter<RoomVsSpecialitiesEntity, RoomVsSpecialitiesModel> {

	private static final long serialVersionUID = -74840772349731968L;
	
	private static final Logger logger = LogManager.getLogger(RoomVsSpecialitiesConverter.class);

	@Override
	public RoomVsSpecialitiesEntity modelToEntity(RoomVsSpecialitiesModel m) {
		
		if (logger.isInfoEnabled()) {
			logger.info("modelToEntity -- START");
		}

		RoomVsSpecialitiesEntity roomVsSpecialitiesEntity = new RoomVsSpecialitiesEntity();
		roomVsSpecialitiesEntity = (RoomVsSpecialitiesEntity) Util.transform(modelMapper, m, roomVsSpecialitiesEntity);
		roomVsSpecialitiesEntity.setStatus(Status.ACTIVE.ordinal());
		roomVsSpecialitiesEntity.setCreatedDate(Util.getCurrentDateTime());
		roomVsSpecialitiesEntity.setSpecialtiesEntity(specialtiesDAO.find(Long.valueOf(m.getSpecialtiesModel().getSpecialtiesId())));

		if (logger.isInfoEnabled()) {
			logger.info("modelToEntity -- END");
		}

		return roomVsSpecialitiesEntity;
	}

	@Override
	public RoomVsSpecialitiesModel entityToModel(RoomVsSpecialitiesEntity e) {
		
		if (logger.isInfoEnabled()) {
			logger.info("entityToModel -- START");
		}
		
		RoomVsSpecialitiesModel roomVsSpecialitiesModel = null;
		
		if(Objects.nonNull(e)) {
			roomVsSpecialitiesModel = new RoomVsSpecialitiesModel();
			roomVsSpecialitiesModel = (RoomVsSpecialitiesModel) Util.transform(modelMapper, e, roomVsSpecialitiesModel);
			roomVsSpecialitiesModel.setSpecialtiesModel(specialtiesConverter.entityToModel(e.getSpecialtiesEntity()));
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("entityToModel -- END");
		}
		
		return roomVsSpecialitiesModel;
	}

	@Override
	public List<RoomVsSpecialitiesModel> entityListToModelList(List<RoomVsSpecialitiesEntity> es) {
		
		if (logger.isInfoEnabled()) {
			logger.info("entityListToModelList -- START");
		}
		
		List<RoomVsSpecialitiesModel> roomVsSpecialitiesModels = null;
		if(!CollectionUtils.isEmpty(es)) {
			roomVsSpecialitiesModels = new ArrayList<>();
			for(RoomVsSpecialitiesEntity roomVsSpecialitiesEntity:es) {
				roomVsSpecialitiesModels.add(entityToModel(roomVsSpecialitiesEntity));
			}
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("entityListToModelList -- END");
		}
		
		return roomVsSpecialitiesModels;
	}
}
