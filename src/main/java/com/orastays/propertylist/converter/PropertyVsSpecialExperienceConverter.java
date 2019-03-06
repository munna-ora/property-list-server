package com.orastays.propertylist.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.orastays.propertylist.entity.PropertyVsSpecialExperienceEntity;
import com.orastays.propertylist.helper.Status;
import com.orastays.propertylist.helper.Util;
import com.orastays.propertylist.model.PropertyVsSpecialExperienceModel;

@Component
public class PropertyVsSpecialExperienceConverter extends CommonConverter
		implements BaseConverter<PropertyVsSpecialExperienceEntity, PropertyVsSpecialExperienceModel> {

	private static final long serialVersionUID = 4778058926083747281L;
	private static final Logger logger = LogManager.getLogger(PropertyVsSpecialExperienceConverter.class);

	@Override
	public PropertyVsSpecialExperienceEntity modelToEntity(PropertyVsSpecialExperienceModel m) {

		PropertyVsSpecialExperienceEntity specialExperienceEntity = new PropertyVsSpecialExperienceEntity();
		specialExperienceEntity = (PropertyVsSpecialExperienceEntity) Util.transform(modelMapper, m, specialExperienceEntity);
		specialExperienceEntity.setStatus(Status.ACTIVE.ordinal());
		specialExperienceEntity.setCreatedDate(Util.getCurrentDateTime());
		
		specialExperienceEntity.setSpecialExperienceEntity(specialExperienceDAO.find(Long.parseLong(m.getSpecialExperienceModel().getExperienceId())));
		
		return specialExperienceEntity;
	}

	@Override
	public PropertyVsSpecialExperienceModel entityToModel(PropertyVsSpecialExperienceEntity e) {
		
		if (logger.isInfoEnabled()) {
			logger.info("entityToModel -- START");
		}
		
		PropertyVsSpecialExperienceModel propertyVsSpecialExperienceModel = null;
		
		if(Objects.nonNull(e)) {
			
			propertyVsSpecialExperienceModel = new PropertyVsSpecialExperienceModel();
			propertyVsSpecialExperienceModel = (PropertyVsSpecialExperienceModel) Util.transform(modelMapper, e, propertyVsSpecialExperienceModel);
			propertyVsSpecialExperienceModel.setSpecialExperienceModel(specialExperienceConverter.entityToModel(e.getSpecialExperienceEntity()));
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("entityToModel -- END");
		}
		
		return propertyVsSpecialExperienceModel;
	}

	@Override
	public List<PropertyVsSpecialExperienceModel> entityListToModelList(List<PropertyVsSpecialExperienceEntity> es) {
		
		if (logger.isInfoEnabled()) {
			logger.info("entityListToModelList -- START");
		}
		
		List<PropertyVsSpecialExperienceModel> propertyVsSpecialExperienceModels = null;
		if(!CollectionUtils.isEmpty(es)) {
			propertyVsSpecialExperienceModels = new ArrayList<>();
			for(PropertyVsSpecialExperienceEntity propertyVsSpecialExperienceEntity:es) {
				propertyVsSpecialExperienceModels.add(entityToModel(propertyVsSpecialExperienceEntity));
			}
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("entityListToModelList -- END");
		}
		
		return propertyVsSpecialExperienceModels;
	}

}
