package com.algaworks.algashop.ecommerce.application.client;

import com.algaworks.algashop.ecommerce.application.model.client.*;
import com.algaworks.algashop.ecommerce.application.model.filter.OrderFilter;
import com.algaworks.algashop.ecommerce.application.properties.EcommerceProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class OrderClient {

	private final EcommerceProperties properties;
	private final RestClient userAuthenticatedRestClient;

	public OrderClient(EcommerceProperties properties,
					   @Qualifier("userAuthenticatedRestClient") RestClient userAuthenticatedRestClient) {
		this.properties = properties;
		this.userAuthenticatedRestClient = userAuthenticatedRestClient;
	}

	public OrderModelPage getOrders(OrderFilter orderFilter) {
		URI uri = UriComponentsBuilder.fromHttpUrl(properties.getApiUrl() + "/api/v1/orders")
				.queryParam("status", orderFilter.getStatus())
				.queryParam("orderedAtFrom", orderFilter.getOrderedAtFrom())
				.queryParam("orderedAtTo", orderFilter.getOrderedAtTo())
				.queryParam("totalValueFrom", orderFilter.getTotalValueFrom())
				.queryParam("totalValueTo", orderFilter.getTotalValueTo())
				.queryParam("paymentMethod", orderFilter.getPaymentMethod())
				.queryParam("code", orderFilter.getCode())
				.queryParam("size", orderFilter.getSize())
				.queryParam("page", orderFilter.getPage())
				.queryParam("direction", orderFilter.getDirection())
				.queryParam("sort", orderFilter.getSort())
				.build()
				.toUri();

		var responseEntity = userAuthenticatedRestClient.get()
				.uri(uri)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(OrderModelPage.class);

		return responseEntity.getBody();
	}

	public OrderModel getOrder(String orderCode) {
		var responseEntity = userAuthenticatedRestClient.get()
				.uri(URI.create(properties.getApiUrl() + "/api/v1/orders/"+orderCode))
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(OrderModel.class);

		return responseEntity.getBody();
	}
}
