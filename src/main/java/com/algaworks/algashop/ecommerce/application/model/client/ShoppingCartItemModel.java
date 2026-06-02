package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartItemModel {
	private String id;
	private String productId;
	private String name;
	private BigDecimal price;
	private Integer quantity;
	private BigDecimal totalAmount;
	private Boolean available;
}
