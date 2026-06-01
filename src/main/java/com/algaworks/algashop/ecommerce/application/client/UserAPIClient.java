package com.algaworks.algashop.ecommerce.application.client;

import com.algaworks.algashop.ecommerce.application.model.client.*;
import com.algaworks.algashop.ecommerce.application.properties.EcommerceProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;

@Component
public class UserAPIClient {

	private final EcommerceProperties properties;
	private final RestClient userAuthenticatedRestClient;

	public UserAPIClient(EcommerceProperties properties,
						 @Qualifier("userAuthenticatedRestClient") RestClient userAuthenticatedRestClient) {
		this.properties = properties;
		this.userAuthenticatedRestClient = userAuthenticatedRestClient;
	}

	public void updateMyPassword(CustomerPasswordInput input) {
		userAuthenticatedRestClient.put()
				.uri(URI.create(properties.getAuthorizationServerUrl() + "/api/v1/users/me/password"))
				.body(input)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(Void.class);
	}
}
