package com.algaworks.algashop.ecommerce.application.model.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerUpdateInput {

	@NotBlank
	private String fullName;

	@NotBlank
	private String phone;

	@Past
	@NotNull
	private LocalDate birthDate;

	@NotBlank
	private String document;

	@NotNull
	private boolean allowPromotionNotifications;

	@Valid
	@NotNull
	private AddressModel address;
}