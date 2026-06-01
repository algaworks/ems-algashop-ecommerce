package com.algaworks.algashop.ecommerce.application.model.form;

import com.algaworks.algashop.ecommerce.application.model.client.AddressModel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class NewCustomerForm {
	@NotBlank
	private String fullName;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String phone;

	@Past
	@NotNull
	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private LocalDate birthDate;

	@NotBlank
	private String document;

	@NotNull
	private boolean allowPromotionNotifications;

	@Valid
	@NotNull
	private AddressModel address;

}