package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderModel {
	private String id;
	private String customerId;
	private PersonalInfoModel shipping;
	private PersonalInfoModel billing;
	private Integer totalItems;
	private BigDecimal totalAmount;
	private List<OrderItemModel> items = new ArrayList<>();
	private OffsetDateTime placedAt;
	private OffsetDateTime canceledAt;
	private OffsetDateTime paidAt;
	private String status;
	private String paymentMethod;
}
