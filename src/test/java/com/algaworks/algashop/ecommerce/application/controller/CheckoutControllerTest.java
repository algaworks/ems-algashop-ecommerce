package com.algaworks.algashop.ecommerce.application.controller;

import com.algaworks.algashop.ecommerce.application.client.CheckoutClient;
import com.algaworks.algashop.ecommerce.application.client.CreditCardClient;
import com.algaworks.algashop.ecommerce.application.client.CustomerRestClient;
import com.algaworks.algashop.ecommerce.application.model.client.AddressModel;
import com.algaworks.algashop.ecommerce.application.model.client.CheckoutModel;
import com.algaworks.algashop.ecommerce.application.model.client.CreditCardModel;
import com.algaworks.algashop.ecommerce.application.model.client.CustomerModel;
import com.algaworks.algashop.ecommerce.application.model.client.OrderModel;
import com.algaworks.algashop.ecommerce.application.model.client.PersonalInfoModel;
import com.algaworks.algashop.ecommerce.application.model.client.ShoppingCartModel;
import com.algaworks.algashop.ecommerce.application.model.form.CheckoutForm;
import com.algaworks.algashop.ecommerce.application.model.form.PaymentMethod;
import com.algaworks.algashop.ecommerce.application.model.page.AlertMessage;
import com.algaworks.algashop.ecommerce.application.service.ShoppingCartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckoutControllerTest {

	@Mock
	private ShoppingCartService shoppingCartService;

	@Mock
	private CheckoutClient checkoutClient;

	@Mock
	private CreditCardClient creditCardClient;

	@Mock
	private CustomerRestClient customerRestClient;

	@Test
	void shouldLoadCreditCardsAndPreserveCustomerDataOnCheckoutPage() {
		CheckoutController controller = controller();
		CreditCardModel creditCard = creditCard("card-1");

		when(shoppingCartService.findCurrentShoppingCart()).thenReturn(shoppingCart());
		when(customerRestClient.getMyProfile()).thenReturn(customer());
		when(creditCardClient.findAll()).thenReturn(List.of(creditCard));

		ModelAndView modelAndView = controller.checkout();

		assertThat(modelAndView.getViewName()).isEqualTo("checkout");
		CheckoutForm form = (CheckoutForm) modelAndView.getModel().get("checkoutForm");
		assertThat(form.getShippingInfo().getFullName()).isEqualTo("Alex Silva");
		assertThat(form.getShippingInfo().getAddress().getZipCode()).isEqualTo("12345");
		assertThat(form.getBillingInfo().getFullName()).isNull();
		assertThat(form.getBillingInfo().getAddress().getZipCode()).isNull();
		assertThat(modelAndView.getModel().get("creditCards")).isEqualTo(List.of(creditCard));
	}

	@Test
	void shouldCheckoutWithSavedCreditCard() {
		CheckoutController controller = controller();
		CheckoutForm form = checkoutForm(PaymentMethod.CREDIT_CARD);
		form.setCreditCardId("card-1");
		ArgumentCaptor<CheckoutModel> inputCaptor = ArgumentCaptor.forClass(CheckoutModel.class);

		when(shoppingCartService.findCurrentShoppingCart()).thenReturn(shoppingCart());
		when(creditCardClient.findById("card-1")).thenReturn(creditCard("card-1"));
		when(checkoutClient.checkout(any(CheckoutModel.class))).thenReturn(order("order-1"));

		ModelAndView modelAndView = controller.doCheckout(form, bindingResult(form), oauth2User());

		assertThat(modelAndView.getViewName()).isEqualTo("redirect:/my-account/orders/order-1");
		verify(creditCardClient).findById("card-1");
		verify(checkoutClient).checkout(inputCaptor.capture());
		assertThat(inputCaptor.getValue().getCreditCardId()).isEqualTo("card-1");
		assertThat(inputCaptor.getValue().getPaymentMethod()).isEqualTo("CREDIT_CARD");
	}

	@Test
	void shouldCopyShippingInfoToBillingWhenBillingAddressIsNotDifferent() {
		CheckoutController controller = controller();
		CheckoutForm form = checkoutForm(PaymentMethod.GATEWAY_BALANCE);
		form.setShippingInfo(personalInfo("Shipping Person", "111-22-333", "111-222-3333", address("10000", "Shipping Street")));
		form.setBillingInfo(personalInfo("Billing Person", "999-88-777", "999-888-7777", address("90000", "Billing Street")));
		ArgumentCaptor<CheckoutModel> inputCaptor = ArgumentCaptor.forClass(CheckoutModel.class);

		when(shoppingCartService.findCurrentShoppingCart()).thenReturn(shoppingCart());
		when(checkoutClient.checkout(any(CheckoutModel.class))).thenReturn(order("order-1"));

		ModelAndView modelAndView = controller.doCheckout(form, bindingResult(form), oauth2User());

		assertThat(modelAndView.getViewName()).isEqualTo("redirect:/my-account/orders/order-1");
		verify(checkoutClient).checkout(inputCaptor.capture());
		CheckoutModel input = inputCaptor.getValue();
		assertThat(input.getShipping().getRecipient().getFirstName()).isEqualTo("Shipping");
		assertThat(input.getShipping().getRecipient().getLastName()).isEqualTo("Person");
		assertThat(input.getBilling().getFirstName()).isEqualTo("Shipping");
		assertThat(input.getBilling().getLastName()).isEqualTo("Person");
		assertThat(input.getBilling().getDocument()).isEqualTo("111-22-333");
		assertThat(input.getBilling().getPhone()).isEqualTo("111-222-3333");
		assertThat(input.getBilling().getAddress().getStreet()).isEqualTo("Shipping Street");
	}

	@Test
	void shouldUseBillingInfoWhenBillingAddressIsDifferent() {
		CheckoutController controller = controller();
		CheckoutForm form = checkoutForm(PaymentMethod.GATEWAY_BALANCE);
		form.setBillToDifferentAddress(true);
		form.setShippingInfo(personalInfo("Shipping Person", "111-22-333", "111-222-3333", address("10000", "Shipping Street")));
		form.setBillingInfo(personalInfo("Billing Person", "999-88-777", "999-888-7777", address("90000", "Billing Street")));
		ArgumentCaptor<CheckoutModel> inputCaptor = ArgumentCaptor.forClass(CheckoutModel.class);

		when(shoppingCartService.findCurrentShoppingCart()).thenReturn(shoppingCart());
		when(checkoutClient.checkout(any(CheckoutModel.class))).thenReturn(order("order-1"));

		ModelAndView modelAndView = controller.doCheckout(form, bindingResult(form), oauth2User());

		assertThat(modelAndView.getViewName()).isEqualTo("redirect:/my-account/orders/order-1");
		verify(checkoutClient).checkout(inputCaptor.capture());
		CheckoutModel input = inputCaptor.getValue();
		assertThat(input.getShipping().getRecipient().getFirstName()).isEqualTo("Shipping");
		assertThat(input.getShipping().getAddress().getStreet()).isEqualTo("Shipping Street");
		assertThat(input.getBilling().getFirstName()).isEqualTo("Billing");
		assertThat(input.getBilling().getLastName()).isEqualTo("Person");
		assertThat(input.getBilling().getDocument()).isEqualTo("999-88-777");
		assertThat(input.getBilling().getPhone()).isEqualTo("999-888-7777");
		assertThat(input.getBilling().getAddress().getStreet()).isEqualTo("Billing Street");
	}

	@Test
	void shouldReturnCheckoutWithAlertWhenCreditCardHasNoIdAndNoToken() {
		CheckoutController controller = controller();
		CheckoutForm form = checkoutForm(PaymentMethod.CREDIT_CARD);

		when(shoppingCartService.findCurrentShoppingCart()).thenReturn(shoppingCart());

		ModelAndView modelAndView = controller.doCheckout(form, bindingResult(form), oauth2User());

		assertThat(modelAndView.getViewName()).isEqualTo("checkout");
		AlertMessage alertMessage = (AlertMessage) modelAndView.getModel().get("alertMessage");
		assertThat(alertMessage.getType()).isEqualTo(AlertMessage.Type.DANGER);
		verify(creditCardClient, never()).register(any());
		verify(checkoutClient, never()).checkout(any(CheckoutModel.class));
	}

	@Test
	void shouldReturnCheckoutWithAlertWhenCreditCardValidationFails() {
		CheckoutController controller = controller();
		CheckoutForm form = checkoutForm(PaymentMethod.CREDIT_CARD);
		form.setCreditCardId("card-1");

		when(shoppingCartService.findCurrentShoppingCart()).thenReturn(shoppingCart());
		when(creditCardClient.findById("card-1")).thenThrow(new RuntimeException("not found"));

		ModelAndView modelAndView = controller.doCheckout(form, bindingResult(form), oauth2User());

		assertThat(modelAndView.getViewName()).isEqualTo("checkout");
		AlertMessage alertMessage = (AlertMessage) modelAndView.getModel().get("alertMessage");
		assertThat(alertMessage.getType()).isEqualTo(AlertMessage.Type.DANGER);
		verify(creditCardClient, never()).register(any());
		verify(checkoutClient, never()).checkout(any(CheckoutModel.class));
	}

	private CheckoutController controller() {
		return new CheckoutController(shoppingCartService, checkoutClient, creditCardClient, customerRestClient);
	}

	private BeanPropertyBindingResult bindingResult(CheckoutForm form) {
		return new BeanPropertyBindingResult(form, "checkoutForm");
	}

	private ShoppingCartModel shoppingCart() {
		ShoppingCartModel shoppingCart = new ShoppingCartModel();
		shoppingCart.setTotalItems(1);
		return shoppingCart;
	}

	private CustomerModel customer() {
		CustomerModel customer = new CustomerModel();
		customer.setFirstName("Alex");
		customer.setLastName("Silva");
		customer.setEmail("alex@example.com");
		customer.setPhone("123-456-7890");
		customer.setDocument("123-45-678");
		customer.setAddress(address());
		return customer;
	}

	private CheckoutForm checkoutForm(PaymentMethod paymentMethod) {
		return CheckoutForm.builder()
				.paymentMethod(paymentMethod)
				.billingInfo(personalInfo("Alex Silva"))
				.shippingInfo(personalInfo("Alex Silva"))
				.build();
	}

	private PersonalInfoModel personalInfo(String fullName) {
		return personalInfo(fullName, "123-45-678", "123-456-7890", address());
	}

	private PersonalInfoModel personalInfo(String fullName, String document, String phone, AddressModel address) {
		return PersonalInfoModel.builder()
				.fullName(fullName)
				.document(document)
				.phone(phone)
				.address(address)
				.build();
	}

	private AddressModel address() {
		return address("12345", "Main Street");
	}

	private AddressModel address(String zipCode, String street) {
		return AddressModel.builder()
				.zipCode(zipCode)
				.street(street)
				.number("100")
				.complement("Apt 10")
				.neighborhood("Downtown")
				.city("Austin")
				.state("TX")
				.build();
	}

	private CreditCardModel creditCard(String id) {
		CreditCardModel creditCard = new CreditCardModel();
		creditCard.setId(id);
		creditCard.setBrand("Visa");
		creditCard.setLastNumbers("1234");
		creditCard.setExpMonth(12);
		creditCard.setExpYear(2028);
		return creditCard;
	}

	private OrderModel order(String id) {
		OrderModel order = new OrderModel();
		order.setId(id);
		return order;
	}

	private OAuth2User oauth2User() {
		return new DefaultOAuth2User(
				List.of(new SimpleGrantedAuthority("ROLE_USER")),
				Map.of("email", "alex@example.com", "name", "Alex Silva"),
				"email"
		);
	}
}
