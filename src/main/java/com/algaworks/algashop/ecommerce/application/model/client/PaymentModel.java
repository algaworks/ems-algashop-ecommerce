package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode
public class PaymentModel {

	private UUID id;

	private String orderCode;

	private String customerId;

	private OffsetDateTime createdAt;

	private OffsetDateTime paidAt;

	private OffsetDateTime refundedAt;

	private PersonalInfoModel billing;

	private BigDecimal totalAmount;

	private String paymentMethod;

	private String status;

	private PixInfoModel pixInfo;

	private String gatewayPaymentCode;

}
