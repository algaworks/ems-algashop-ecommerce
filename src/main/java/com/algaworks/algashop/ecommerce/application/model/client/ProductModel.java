package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class ProductModel {
	private String id;
	private OffsetDateTime createdAt;
	private String name;
	private String brand;
	private String slug;
	private boolean enabled;

	private BigDecimal regularPrice;
	private BigDecimal salePrice;

	private Boolean hasDiscount;
	private Boolean inStock;

	private Integer discountPercentageRounded;
	private String description;

	private CategoryModel category;
	private ImageModel mainImage;

	//para tela
	public boolean getOnStock() {
		return inStock;
	}
}
