package com.algaworks.algashop.ecommerce.application.service;

import com.algaworks.algashop.ecommerce.application.client.ShoppingCartClient;
import com.algaworks.algashop.ecommerce.application.model.client.ShoppingCartInput;
import com.algaworks.algashop.ecommerce.application.model.client.ShoppingCartItemInput;
import com.algaworks.algashop.ecommerce.application.model.client.ShoppingCartModel;
import com.algaworks.algashop.ecommerce.application.session.ShoppingCartSession;
import com.algaworks.algashop.ecommerce.infraestructure.security.AlgaShopSecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;


@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartService {

	private final ShoppingCartClient shoppingCartClient;
	private final ShoppingCartSession shoppingCartSession;
	private final AlgaShopSecurityService algaShopSecurityService;

	public ShoppingCartModel findCurrentShoppingCart() {
		try {
			ShoppingCartModel shoppingCartModel = shoppingCartClient.getCurrentShoppingCart();
			shoppingCartSession.setCurrentShoppingCartId(shoppingCartModel.getId());
			return shoppingCartModel; //todo tratar erro
		} catch (HttpClientErrorException e) {
			log.warn("No current ShoppingCart exists, creating a new one.");
			ShoppingCartModel newShoppingCart = createNewShoppingCart();
			shoppingCartSession.setCurrentShoppingCartId(newShoppingCart.getId());
			return newShoppingCart;
		}
	}

	public String loadCurrentShoppingCartId() {
		if (shoppingCartSession.isEmpty()) {
			return findCurrentShoppingCart().getId();
		}
		return shoppingCartSession.getCurrentShoppingCartId();
	}

	private ShoppingCartModel createNewShoppingCart() {
		OAuth2AuthenticationToken authentication = algaShopSecurityService.getAuthentication()
				.orElseThrow(()-> new AccessDeniedException("User not authenticated."));

		return shoppingCartClient.create(new ShoppingCartInput(authentication.getPrincipal().getName()));
	}

	public void addItem(ShoppingCartItemInput shoppingCartItemInput) {
		shoppingCartClient.addItem(loadCurrentShoppingCartId(), shoppingCartItemInput);
	}

	public void removeItem(String productId) {
		shoppingCartClient.removeItem(loadCurrentShoppingCartId(), productId);
	}
}
