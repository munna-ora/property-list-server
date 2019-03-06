package com.orastays.propertylist.dao;

import org.springframework.stereotype.Repository;

import com.orastays.propertylist.entity.ContactPurposeEntity;

@Repository
public class ContactPurposeDAO extends GenericDAO<ContactPurposeEntity, Long> {

	private static final long serialVersionUID = -244559235960090533L;

	public ContactPurposeDAO() {
		super(ContactPurposeEntity.class);
	}
}
