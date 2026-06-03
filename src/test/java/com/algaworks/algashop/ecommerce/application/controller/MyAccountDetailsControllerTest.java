package com.algaworks.algashop.ecommerce.application.controller;

import com.algaworks.algashop.ecommerce.application.client.CustomerRestClient;
import com.algaworks.algashop.ecommerce.application.client.UserAPIClient;
import com.algaworks.algashop.ecommerce.application.model.client.AddressModel;
import com.algaworks.algashop.ecommerce.application.model.client.CustomerModel;
import com.algaworks.algashop.ecommerce.application.model.client.CustomerUpdateInput;
import com.algaworks.algashop.ecommerce.application.model.form.EditCustomerForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyAccountDetailsControllerTest {

	@Mock
	private CustomerRestClient customerRestClient;

	@Mock
	private UserAPIClient userAPIClient;

	@Test
	void shouldPreserveExistingAddressWhenUpdatingPersonalData() {
		MyAccountDetailsController controller = new MyAccountDetailsController(customerRestClient, userAPIClient);
		CustomerModel customer = customer();
		EditCustomerForm form = EditCustomerForm.builder()
				.fullName("Alex Updated")
				.phone("999-888-7777")
				.birthDate(LocalDate.of(1990, 1, 1))
				.document("123-45-678")
				.allowPromotionNotifications(false)
				.build();
		BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(form, "customerForm");
		ArgumentCaptor<CustomerUpdateInput> inputCaptor = ArgumentCaptor.forClass(CustomerUpdateInput.class);

		when(customerRestClient.getMyProfile()).thenReturn(customer);
		when(customerRestClient.updateMyProfile(any(CustomerUpdateInput.class))).thenReturn(customer);

		ModelAndView modelAndView = controller.editMyData(form, bindingResult, new RedirectAttributesModelMap());

		assertThat(modelAndView.getViewName()).isEqualTo("redirect:/my-account/details");
		verify(customerRestClient).updateMyProfile(inputCaptor.capture());
		CustomerUpdateInput input = inputCaptor.getValue();
		assertThat(input.getFirstName()).isEqualTo("Alex");
		assertThat(input.getLastName()).isEqualTo("Updated");
		assertThat(input.getPhone()).isEqualTo("999-888-7777");
		assertThat(input.getAddress()).isSameAs(customer.getAddress());
		assertThat(input.getAddress().getZipCode()).isEqualTo("12345");
	}

	private CustomerModel customer() {
		CustomerModel customer = new CustomerModel();
		customer.setFirstName("Alex");
		customer.setLastName("Silva");
		customer.setEmail("alex@example.com");
		customer.setPhone("123-456-7890");
		customer.setBirthDate(LocalDate.of(1990, 1, 1));
		customer.setDocument("123-45-678");
		customer.setPromotionNotificationsAllowed(true);
		customer.setAddress(AddressModel.builder()
				.zipCode("12345")
				.street("Main Street")
				.number("100")
				.complement("Apt 10")
				.neighborhood("Downtown")
				.city("Austin")
				.state("TX")
				.build());
		return customer;
	}
}
