package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ShoppingCartModel {
	private String id;
	private String customerId;
	private Integer totalItems = 0;
	private BigDecimal totalAmount = BigDecimal.ZERO;
	private List<ShoppingCartItemModel> items = new ArrayList<>();
}
