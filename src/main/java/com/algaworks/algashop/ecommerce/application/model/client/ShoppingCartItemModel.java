package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartItemModel {
	private UUID productId;
	private String name;
	private String slug;
	private BigDecimal price;
	private Integer quantity;
	private BigDecimal totalAmount;
	private Boolean inStock;
	private ImageModel mainImage;
}
