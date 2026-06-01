package com.algaworks.algashop.ecommerce.application.client;

import com.algaworks.algashop.ecommerce.application.model.client.*;
import com.algaworks.algashop.ecommerce.application.properties.EcommerceProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;

@Component
public class ShoppingCartClient {

	private final EcommerceProperties properties;
	private final RestClient restClient;

	public ShoppingCartClient(EcommerceProperties properties,
				  @Qualifier("userAuthenticatedRestClient") RestClient restClient) {
		this.properties = properties;
		this.restClient = restClient;
	}

	public ShoppingCartModel getCurrentShoppingCart() {
		return restClient.get()
				.uri(URI.create(properties.getApiUrl() + "/api/v1/shopping-carts/current"))
				.accept(MediaType.APPLICATION_JSON)
				.header("Cache-Control", "no-cache")
				.retrieve()
				.toEntity(ShoppingCartModel.class).getBody();
	}

	public ShoppingCartModel getShoppingCart(String shoppingCartId) {
		return restClient.get()
				.uri(URI.create(properties.getApiUrl() + "/api/v1/shopping-carts/"+shoppingCartId))
				.accept(MediaType.APPLICATION_JSON)
				.header("Cache-Control", "no-cache")
				.retrieve()
				.toEntity(ShoppingCartModel.class).getBody();
	}

	public ShoppingCartItemListModel getShoppingCartItems(String shoppingCartId) {
		return restClient.get()
				.uri(URI.create(properties.getApiUrl() + "/api/v1/shopping-carts/"+shoppingCartId+"/items"))
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(ShoppingCartItemListModel.class).getBody();
	}

	public void addItem(String shoppingCartId, ShoppingCartItemInput input) {
		restClient.post()
				.uri(URI.create(properties.getApiUrl() + "/api/v1/shopping-carts/"+shoppingCartId+"/items"))
				.body(input)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve().toBodilessEntity();
	}

	public void removeItem(String shoppingCartId, String productId) {
		restClient.delete()
				.uri(URI.create(properties.getApiUrl() + "/api/v1/shopping-carts/"+shoppingCartId+"/items/" + productId))
				.accept(MediaType.APPLICATION_JSON)
				.retrieve().toBodilessEntity();
	}

	public ShoppingCartModel create(ShoppingCartInput input) {
		return restClient.post()
				.uri(URI.create(properties.getApiUrl() + "/api/v1/shopping-carts"))
				.body(input)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(ShoppingCartModel.class)
				.getBody();
	}
}
