package com.orastays.propertylist.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.orastays.propertylist.entity.PropertyEntity;
import com.orastays.propertylist.helper.MessageUtil;
import com.orastays.propertylist.helper.Util;
import com.orastays.propertylist.model.FilterCiteriaModel;

@Repository
public class PropertyDAO extends GenericDAO<PropertyEntity, Long>{

	private static final Logger logger = LogManager.getLogger(PropertyDAO.class);
	private static final long serialVersionUID = -7671486148453498082L;
	
	@Autowired
	protected transient MessageUtil messageUtil;

	public PropertyDAO() {
		super(PropertyEntity.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<PropertyEntity> selectByRadius(FilterCiteriaModel filterCiteriaModel) {
		 
		if (logger.isInfoEnabled()) {
			logger.info("selectByRadius -- START");
		}
		
		List<PropertyEntity> propertyEntities = null;
		 Session session = null;
		 try {
			 session = this.sessionFactory.openSession();
			 Query query = session.createSQLQuery("SELECT property_id, ( 6371 * acos( cos( radians("+filterCiteriaModel.getLatitude()+") ) * cos( radians( latitude ) ) * cos( radians( longitude ) - radians("+filterCiteriaModel.getLongitude()+") ) + sin( radians("+filterCiteriaModel.getLatitude()+") ) * sin( radians( latitude ) ) ) ) "
			 		+ "AS distance FROM master_property HAVING distance < "+messageUtil.getBundle("radius")+" ORDER BY distance");  
			 
			 List<Object[]> output = null;
			 output = query.list();
			 if(Objects.nonNull(output)) {
				 
				 propertyEntities = new ArrayList<>();
				 for (Object[] object : output) {
					 
					 PropertyEntity propertyEntity = new PropertyEntity();
					 propertyEntity.setPropertyId(Long.valueOf(String.valueOf(object[0])));
					 propertyEntities.add(propertyEntity);
				 }
			 }
			 
		     
		 } catch (HibernateException e) {
			 if (logger.isInfoEnabled()) {
					logger.info("Exception in selectByRadius -- "+Util.errorToString(e));
				}
		 } finally {
			if(Objects.nonNull(session)) {
				session.close();
			}
		}
		 
		 if (logger.isInfoEnabled()) {
				logger.info("selectByRadius -- END");
		 }
		 
		 return propertyEntities;
	}
}
