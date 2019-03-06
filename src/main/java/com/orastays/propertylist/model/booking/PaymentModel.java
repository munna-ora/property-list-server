package com.orastays.propertylist.model.booking;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
@JsonInclude(Include.NON_NULL)
public class PaymentModel {
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("paymentLink")
	private String paymentLink;
	
	@JsonProperty("reason")
	private String reason;
}
