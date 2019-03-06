package com.orastays.propertylist.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.orastays.propertylist.entity.OfferEntity;
import com.orastays.propertylist.helper.Status;
import com.orastays.propertylist.helper.Util;
import com.orastays.propertylist.model.OfferModel;

@Component
public class OfferConverter extends CommonConverter implements BaseConverter<OfferEntity, OfferModel> {

	private static final long serialVersionUID = -6560684027344711160L;
	private static final Logger logger = LogManager.getLogger(OfferConverter.class);

	@Override
	public OfferEntity modelToEntity(OfferModel m) {

		if (logger.isInfoEnabled()) {
			logger.info("entityToModel -- START");
		}
		
		OfferEntity offerEntity = new OfferEntity();
		offerEntity = (OfferEntity) Util.transform(modelMapper, m, offerEntity);
		offerEntity.setStatus(Status.ACTIVE.ordinal());
		offerEntity.setCreatedDate(Util.getCurrentDateTime());
		
		if (logger.isInfoEnabled()) {
			logger.info("entityToModel -- END");
		}
		
		return offerEntity;
	}

	@Override
	public OfferModel entityToModel(OfferEntity e) {
		
		if (logger.isInfoEnabled()) {
			logger.info("entityToModel -- START");
		}
		
		OfferModel offerModel = null;
		
		if(Objects.nonNull(e)) {
			offerModel = new OfferModel();
			offerModel = (OfferModel) Util.transform(modelMapper, e, offerModel);
		}
		
		
		if (logger.isInfoEnabled()) {
			logger.info("entityToModel -- END");
		}
		
		return offerModel;
	}

	@Override
	public List<OfferModel> entityListToModelList(List<OfferEntity> es) {
		
		if (logger.isInfoEnabled()) {
			logger.info("entityListToModelList -- START");
		}
		
		List<OfferModel> offerModels = null;
		if(!CollectionUtils.isEmpty(es)) {
			offerModels = new ArrayList<>();
			for(OfferEntity offerEntity:es) {
				offerModels.add(entityToModel(offerEntity));
			}
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("entityListToModelList -- END");
		}
		
		return offerModels;
	}

}
