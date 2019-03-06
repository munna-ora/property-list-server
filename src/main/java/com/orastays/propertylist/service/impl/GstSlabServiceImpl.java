package com.orastays.propertylist.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.orastays.propertylist.entity.GstSlabEntity;
import com.orastays.propertylist.exceptions.FormExceptions;
import com.orastays.propertylist.helper.Status;
import com.orastays.propertylist.helper.Util;
import com.orastays.propertylist.model.GstSlabModel;
import com.orastays.propertylist.service.GstSlabService;

@Service
@Transactional
public class GstSlabServiceImpl extends BaseServiceImpl implements GstSlabService {
	
	private static final Logger logger = LogManager.getLogger(GstSlabServiceImpl.class);

	@Override
	public GstSlabEntity getActiveGstEntity(Double amount) {
		
		if (logger.isInfoEnabled()) {
			logger.info("getActiveGstEntity -- START");
		}

		List<GstSlabEntity> gstSlabEntities = null;
		GstSlabEntity gstSlabEntityFetched = null;
		try {
			Map<String, String> innerMap1 = new LinkedHashMap<>();
			innerMap1.put("status", String.valueOf(Status.ACTIVE.ordinal()));
			
			Map<String, Map<String, String>> outerMap1 = new LinkedHashMap<>();
			outerMap1.put("eq", innerMap1);

			Map<String, Map<String, Map<String, String>>> alliasMap = new LinkedHashMap<>();
			alliasMap.put(entitymanagerPackagesToScan + ".GstSlabEntity", outerMap1);

			gstSlabEntities = gstSlabDAO.fetchListBySubCiteria(alliasMap);
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception in getActiveGstEntity -- " + Util.errorToString(e));
			}
		}

		for (GstSlabEntity gstSlabEntity : gstSlabEntities) {
			if (Double.parseDouble(gstSlabEntity.getFromAmount()) <= amount
					&& Double.parseDouble(gstSlabEntity.getToAmount()) >= amount) {
				
				gstSlabEntityFetched = gstSlabEntity;
				break;
			}
		}

		if (logger.isInfoEnabled()) {
			logger.info("getActiveGstEntity -- END");
		}

		return gstSlabEntityFetched;

	}

	@Override
	public GstSlabModel getActiveGstModel(Double amount) throws FormExceptions {
		
		if (logger.isInfoEnabled()) {
			logger.info("getActiveGstModel -- START");
		}
		
		GstSlabModel gstSlabModel = gstSlabConverter.entityToModel(getActiveGstEntity(amount));
		
		if (logger.isInfoEnabled()) {
			logger.info("getActiveGstModel -- END");
		}
		
		return gstSlabModel;
	}
}