package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutModel {
	private PersonalInfoModel shipping;
	private PersonalInfoModel billing;
	private PaymentInfo payment;
	private String customerId;

	//todo testar
//	@JsonAnySetter
//	@JsonAnyGetter
//	private Map<String, String> paymentInfo;

//	{
//		"paymentInfo": {
//			carId: "123",
//			tokenId: "123"
//		}
//	}

}
