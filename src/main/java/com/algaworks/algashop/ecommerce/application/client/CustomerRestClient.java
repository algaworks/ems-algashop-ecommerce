package com.algaworks.algashop.ecommerce.application.client;

import com.algaworks.algashop.ecommerce.application.model.client.*;
import com.algaworks.algashop.ecommerce.application.properties.EcommerceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.URI;

@Component
@Slf4j
public class CustomerRestClient {

	private final EcommerceProperties properties;
	private final RestClient userAuthenticatedRestClient;

	public CustomerRestClient(EcommerceProperties properties,
							  @Qualifier("userAuthenticatedRestClient") RestClient userAuthenticatedRestClient) {
		this.properties = properties;
		this.userAuthenticatedRestClient = userAuthenticatedRestClient;
	}

	public CustomerModel createMyProfile(CustomerInput customerInput) {
		try {
			log.info("Create customer:\n" + customerInput.toString() + "\n");
			ResponseEntity<CustomerModel> responseEntity = userAuthenticatedRestClient.post()
					.uri(URI.create(properties.getApiUrl() + "/api/v1/customers/me"))
					.body(customerInput)
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
					.retrieve()
					.toEntity(CustomerModel.class);

			return responseEntity.getBody();
		} catch (RestClientResponseException e) {
			log.error("Error when tried to create Customer:\n" + e.getResponseBodyAsString() + "\n");
			throw e; //todo integration gateway 502 exception
		}
	}

	public CustomerModel getMyProfile() {
		ResponseEntity<CustomerModel> responseEntity = userAuthenticatedRestClient.get()
				.uri(URI.create(properties.getApiUrl() + "/api/v1/customers/me"))
				.accept(MediaType.APPLICATION_JSON)
				.header("Cache-Control", "no-cache")
				.retrieve()
				.toEntity(CustomerModel.class);

		return responseEntity.getBody();
	}

	public CustomerModel updateMyProfile(CustomerUpdateInput customerInput) {
		ResponseEntity<CustomerModel> responseEntity = userAuthenticatedRestClient.put()
				.uri(URI.create(properties.getApiUrl() + "/api/v1/customers/me"))
				.body(customerInput)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(CustomerModel.class);

		return responseEntity.getBody();
	}
}
