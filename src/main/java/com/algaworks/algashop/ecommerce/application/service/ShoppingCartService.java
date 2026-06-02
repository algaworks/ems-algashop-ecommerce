package com.algaworks.algashop.ecommerce.application.service;

import com.algaworks.algashop.ecommerce.application.client.ShoppingCartClient;
import com.algaworks.algashop.ecommerce.application.model.client.ShoppingCartItemInput;
import com.algaworks.algashop.ecommerce.application.model.client.ShoppingCartModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;


@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartService {

	private final ShoppingCartClient shoppingCartClient;

	public ShoppingCartModel findCurrentShoppingCart() {
		try {
			return shoppingCartClient.getCurrentShoppingCart();
		} catch (HttpClientErrorException e) {
			log.warn("No current ShoppingCart exists.");
			return new ShoppingCartModel();
		}
	}

	public void addItem(ShoppingCartItemInput shoppingCartItemInput) {
		shoppingCartClient.addItem(shoppingCartItemInput);
	}

	public void removeItem(String itemId) {
		shoppingCartClient.removeItem(itemId);
	}
}
