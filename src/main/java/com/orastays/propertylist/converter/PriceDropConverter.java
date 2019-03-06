package com.orastays.propertylist.converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.orastays.propertylist.entity.PriceDropEntity;
import com.orastays.propertylist.helper.Util;
import com.orastays.propertylist.model.PriceDropModel;

@Component
public class PriceDropConverter extends CommonConverter implements BaseConverter<PriceDropEntity, PriceDropModel> {

	private static final long serialVersionUID = 776600662250519034L;
	private static final Logger logger = LogManager.getLogger(PriceDropConverter.class);

	@Override
	public PriceDropEntity modelToEntity(PriceDropModel m) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PriceDropModel entityToModel(PriceDropEntity e) {
		
		if (logger.isInfoEnabled()) {
			logger.info("entityToModel -- START");
		}
		
		PriceDropModel priceDropModel = new PriceDropModel();
		priceDropModel = (PriceDropModel) Util.transform(modelMapper, e, priceDropModel);
		
		if (logger.isInfoEnabled()) {
			logger.info("entityToModel -- END");
		}
		
		return priceDropModel;
	}

	@Override
	public List<PriceDropModel> entityListToModelList(List<PriceDropEntity> es) {
		
		if (logger.isInfoEnabled()) {
			logger.info("entityListToModelList -- START");
		}
		
		List<PriceDropModel> priceDropModels = null;
		if(!CollectionUtils.isEmpty(es)) {
			priceDropModels = new ArrayList<>();
			for(PriceDropEntity priceDropEntity:es) {
				priceDropModels.add(entityToModel(priceDropEntity));
			}
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("entityListToModelList -- END");
		}
		
		return priceDropModels;
	}

}
