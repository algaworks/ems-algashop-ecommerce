package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CustomerModel {
	private String id;
	private String fullName;
	private String email;
	private String phone;
	private LocalDate birthDate;
	private String document;
	private boolean allowPromotionNotifications;
	private AddressModel address;
}