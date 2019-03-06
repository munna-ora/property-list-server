package com.orastays.propertylist.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class PropertyVsDocumentModel extends CommonModel {

	@JsonProperty("propertyVsDocumentId")
	private String propertyVsDocumentId;

	@JsonProperty("documentNumber")
	private String documentNumber;

	@JsonProperty("fileUrl")
	private String fileUrl;

	@JsonProperty("property")
	private PropertyModel propertyModel;

	@JsonProperty("document")
	private DocumentModel documentModel;
}
