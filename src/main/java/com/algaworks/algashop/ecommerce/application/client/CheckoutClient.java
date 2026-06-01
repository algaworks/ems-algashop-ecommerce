package com.algaworks.algashop.ecommerce.application.client;

import com.algaworks.algashop.ecommerce.application.model.client.CheckoutModel;
import com.algaworks.algashop.ecommerce.application.model.client.CheckoutResponseModel;
import com.algaworks.algashop.ecommerce.application.properties.EcommerceProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;

@Component
public class CheckoutClient {

	private final EcommerceProperties properties;
	private final RestClient userAuthenticatedRestClient;

	public CheckoutClient(EcommerceProperties properties,
						  @Qualifier("userAuthenticatedRestClient") RestClient userAuthenticatedRestClient) {
		this.properties = properties;
		this.userAuthenticatedRestClient = userAuthenticatedRestClient;
	}

	public CheckoutResponseModel checkout(String shoppingCartId, CheckoutModel checkoutInput) {
		ResponseEntity<CheckoutResponseModel> responseEntity = userAuthenticatedRestClient.post()
				.uri(URI.create(properties.getApiUrl() + "/api/v1/shopping-carts/"+shoppingCartId+"/checkout"))
				.body(checkoutInput)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(CheckoutResponseModel.class);

		return responseEntity.getBody();
	}

}
