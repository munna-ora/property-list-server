package com.orastays.propertylist.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.orastays.propertylist.converter.AmenitiesConverter;
import com.orastays.propertylist.converter.CancellationSlabConverter;
import com.orastays.propertylist.converter.ConvenienceConverter;
import com.orastays.propertylist.converter.GstSlabConverter;
import com.orastays.propertylist.converter.OfferConverter;
import com.orastays.propertylist.converter.PriceDropConverter;
import com.orastays.propertylist.converter.PropertyConverter;
import com.orastays.propertylist.converter.PropertyTypeConverter;
import com.orastays.propertylist.converter.PropertyVsDescriptionConverter;
import com.orastays.propertylist.converter.PropertyVsDocumentConverter;
import com.orastays.propertylist.converter.PropertyVsGuestAccessConverter;
import com.orastays.propertylist.converter.PropertyVsImageConverter;
import com.orastays.propertylist.converter.PropertyVsNearbyConverter;
import com.orastays.propertylist.converter.PropertyVsPriceDropConverter;
import com.orastays.propertylist.converter.PropertyVsSpaceRuleConverter;
import com.orastays.propertylist.converter.PropertyVsSpecialExperienceConverter;
import com.orastays.propertylist.converter.RoomCategoryConverter;
import com.orastays.propertylist.converter.RoomConverter;
import com.orastays.propertylist.converter.RoomVsAmenitiesConverter;
import com.orastays.propertylist.converter.RoomVsCancellationConverter;
import com.orastays.propertylist.converter.RoomVsImageConverter;
import com.orastays.propertylist.converter.RoomVsMealConverter;
import com.orastays.propertylist.converter.RoomVsSpecialitiesConverter;
import com.orastays.propertylist.converter.SpaceRuleConverter;
import com.orastays.propertylist.converter.SpecialExperienceConverter;
import com.orastays.propertylist.converter.SpecialtiesConverter;
import com.orastays.propertylist.converter.StayTypeConverter;
import com.orastays.propertylist.converter.WishlistConverter;
import com.orastays.propertylist.dao.AmenitiesDAO;
import com.orastays.propertylist.dao.CancellationSlabDAO;
import com.orastays.propertylist.dao.ConvenienceDAO;
import com.orastays.propertylist.dao.GstSlabDAO;
import com.orastays.propertylist.dao.OfferDAO;
import com.orastays.propertylist.dao.PriceDropDAO;
import com.orastays.propertylist.dao.PropertyDAO;
import com.orastays.propertylist.dao.PropertyTypeDAO;
import com.orastays.propertylist.dao.PropertyVsDescriptionDAO;
import com.orastays.propertylist.dao.PropertyVsDocumentDAO;
import com.orastays.propertylist.dao.PropertyVsGuestAccessDAO;
import com.orastays.propertylist.dao.PropertyVsImageDAO;
import com.orastays.propertylist.dao.PropertyVsNearbyDAO;
import com.orastays.propertylist.dao.PropertyVsPriceDropDAO;
import com.orastays.propertylist.dao.PropertyVsSpaceRuleDAO;
import com.orastays.propertylist.dao.PropertyVsSpecialExperienceDAO;
import com.orastays.propertylist.dao.RoomCategoryDAO;
import com.orastays.propertylist.dao.RoomDAO;
import com.orastays.propertylist.dao.RoomVsAmenitiesDAO;
import com.orastays.propertylist.dao.RoomVsCancellationDAO;
import com.orastays.propertylist.dao.RoomVsImageDAO;
import com.orastays.propertylist.dao.RoomVsMealDAO;
import com.orastays.propertylist.dao.RoomVsSpecialitiesDAO;
import com.orastays.propertylist.dao.SpaceRuleDAO;
import com.orastays.propertylist.dao.SpecialExperienceDAO;
import com.orastays.propertylist.dao.SpecialtiesDAO;
import com.orastays.propertylist.dao.StayTypeDAO;
import com.orastays.propertylist.dao.WishlistDAO;
import com.orastays.propertylist.helper.MessageUtil;
import com.orastays.propertylist.helper.PropertyListHelper;
import com.orastays.propertylist.service.ConvenienceService;
import com.orastays.propertylist.service.GstSlabService;
import com.orastays.propertylist.service.PropertyListService;
import com.orastays.propertylist.validation.BookingValidation;
import com.orastays.propertylist.validation.HomeValidation;
import com.orastays.propertylist.validation.PropertyListValidation;
import com.orastays.propertylist.validation.WishlistValidation;

public abstract class BaseServiceImpl {

	@Value("${entitymanager.packagesToScan}")
	protected String entitymanagerPackagesToScan;

	@Autowired
	protected RestTemplate restTemplate;

	@Autowired
	protected MessageUtil messageUtil;

	@Autowired
	protected PropertyListValidation propertyListValidation;

	@Autowired
	protected HomeValidation homeValidation;

	@Autowired
	protected BookingValidation bookingValidation;
	
	@Autowired
	protected PropertyListHelper propertyListHelper;

	@Autowired
	protected PropertyTypeDAO propertyTypeDAO;

	@Autowired
	protected PropertyTypeConverter propertyTypeConverter;

	@Autowired
	protected StayTypeConverter stayTypeConverter;

	@Autowired
	protected StayTypeDAO stayTypeDAO;

	@Autowired
	protected AmenitiesConverter amenitiesConverter;

	@Autowired
	protected AmenitiesDAO amenitiesDAO;

	@Autowired
	protected SpecialExperienceConverter specialExperienceConverter;

	@Autowired
	protected SpecialExperienceDAO specialExperienceDAO;

	@Autowired
	protected SpaceRuleConverter spaceRuleConverter;

	@Autowired
	protected SpaceRuleDAO spaceRuleDAO;

	@Autowired
	protected SpecialtiesConverter specialtiesConverter;

	@Autowired
	protected SpecialtiesDAO specialtiesDAO;

	@Autowired
	protected RoomCategoryConverter roomCategoryConverter;

	@Autowired
	protected RoomCategoryDAO roomCategoryDAO;

	@Autowired
	protected CancellationSlabConverter cancellationSlabConverter;

	@Autowired
	protected CancellationSlabDAO cancellationSlabDAO;

	@Autowired
	protected PriceDropDAO priceDropDAO;

	@Autowired
	protected PriceDropConverter priceDropConverter;

	@Autowired
	protected PropertyConverter propertyConverter;

	@Autowired
	protected PropertyDAO propertyDAO;

	@Autowired
	protected PropertyVsDescriptionConverter propertyVsDescriptionConverter;

	@Autowired
	protected PropertyVsDescriptionDAO propertyVsDescriptionDAO;

	@Autowired
	protected PropertyVsDocumentConverter propertyVsDocumentConverter;

	@Autowired
	protected PropertyVsDocumentDAO propertyVsDocumentDAO;

	@Autowired
	protected PropertyVsSpecialExperienceConverter pVsSpecialExperienceConverter;

	@Autowired
	protected PropertyVsSpecialExperienceDAO propertyVsSpecialExperienceDAO;

	@Autowired
	protected PropertyVsGuestAccessConverter propertyVsGuestAccessConverter;

	@Autowired
	protected PropertyVsGuestAccessDAO propertyVsGuestAccessDAO;

	@Autowired
	protected PropertyVsImageConverter propertyVsImageConverter;

	@Autowired
	protected PropertyVsImageDAO propertyVsImageDAO;

	@Autowired
	protected PropertyVsNearbyConverter propertyVsNearbyConverter;

	@Autowired
	protected PropertyVsNearbyDAO propertyVsNearbyDAO;

	@Autowired
	protected PropertyVsPriceDropConverter propertyVsPriceDropConverter;

	@Autowired
	protected PropertyVsPriceDropDAO propertyVsPriceDropDAO;

	@Autowired
	protected PropertyVsSpaceRuleConverter propertyVsSpaceRuleConverter;

	@Autowired
	protected PropertyVsSpaceRuleDAO propertyVsSpaceRuleDAO;

	@Autowired
	protected RoomConverter roomConverter;

	@Autowired
	protected RoomDAO roomDAO;

	@Autowired
	protected RoomVsAmenitiesDAO roomVsAmenitiesDAO;

	@Autowired
	protected RoomVsAmenitiesConverter roomVsAmenitiesConverter;

	@Autowired
	protected RoomVsImageDAO roomVsImageDAO;

	@Autowired
	protected RoomVsImageConverter roomVsImageConverter;

	@Autowired
	protected RoomVsSpecialitiesConverter roomVsSpecialitiesConverter;

	@Autowired
	protected RoomVsSpecialitiesDAO roomVsSpecialitiesDAO;

	@Autowired
	protected RoomVsMealConverter roomVsMealConverter;

	@Autowired
	protected OfferConverter offerConverter;

	@Autowired
	protected OfferDAO offerDAO;

	@Autowired
	protected RoomVsMealDAO roomVsMealDAO;

	@Autowired
	protected RoomVsCancellationConverter roomVsCancellationConverter;

	@Autowired
	protected RoomVsCancellationDAO roomVsCancellationDAO;

	@Autowired
	protected PropertyListService propertyListService;

	@Autowired
	protected ConvenienceService convenienceService;

	@Autowired
	protected GstSlabService gstSlabService;

	@Autowired
	protected GstSlabDAO gstSlabDAO;

	@Autowired
	protected GstSlabConverter gstSlabConverter;

	@Autowired
	protected ConvenienceDAO convenienceDAO;

	@Autowired
	protected ConvenienceConverter convenienceConverter;

	@Autowired
	protected WishlistDAO wishlistDAO;

	@Autowired
	protected WishlistConverter wishlistConverter;

	@Autowired
	protected WishlistValidation wishlistValidation;

}