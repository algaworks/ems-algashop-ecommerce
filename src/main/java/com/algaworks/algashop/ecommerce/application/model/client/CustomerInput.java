package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerInput {
	private String fullName;
	private String email;
	private String phone;
	private LocalDate birthDate;
	private String document;
	private boolean allowPromotionNotifications;
	private AddressModel address;

}