package com.algaworks.algashop.ecommerce.application.controller;

import com.algaworks.algashop.ecommerce.application.client.CheckoutClient;
import com.algaworks.algashop.ecommerce.application.client.CustomerRestClient;
import com.algaworks.algashop.ecommerce.application.model.client.*;
import com.algaworks.algashop.ecommerce.application.model.form.CheckoutForm;
import com.algaworks.algashop.ecommerce.application.model.page.CheckoutPageModel;
import com.algaworks.algashop.ecommerce.application.service.ShoppingCartService;
import com.algaworks.algashop.ecommerce.application.util.FullNameParser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CheckoutController {

	private final ShoppingCartService shoppingCartService;
	private final CheckoutClient checkoutClient;
	private final CustomerRestClient customerManagementAPIClient;

	@GetMapping("/checkout")
	public ModelAndView checkout() {
		try {
			if (shoppingCartService.findCurrentShoppingCart().getTotalItems() < 1) {
				return new ModelAndView("redirect:/shopping-cart");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ModelAndView("redirect:/shopping-cart");
		}

		CustomerModel customerModel;

		try {
			customerModel = customerManagementAPIClient.getMyProfile();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return checkout(new CheckoutForm());
		}

		return checkout(CheckoutForm.of(customerModel));
	}

	private ModelAndView checkout(CheckoutForm checkoutForm) {
		var pageBuilder = CheckoutPageModel.builder();

		return pageBuilder
				.checkoutForm(checkoutForm)
				.build()
				.toModelAndView();
	}

	@PostMapping("/checkout")
	public ModelAndView doCheckout(@Valid @ModelAttribute("checkoutForm") CheckoutForm checkoutForm,
								   BindingResult bindingResult, @AuthenticationPrincipal OAuth2User userDetails) {
		if (shoppingCartService.findCurrentShoppingCart().getTotalItems() < 1) {
			return new ModelAndView("redirect:/shopping-cart");
		}

		if (bindingResult.hasErrors()) {
			return checkout(checkoutForm);
		}

		PersonalInfoModel shippingInfo = checkoutForm.isSendToDifferentAddress()
				? checkoutForm.getShippingInfo()
				: checkoutForm.getBillingInfo();

		var billingName = FullNameParser.split(checkoutForm.getBillingInfo().getFullName());
		var shippingName = FullNameParser.split(shippingInfo.getFullName());

		String email = userDetails.getAttribute("email");
		if (email == null || email.isBlank()) {
			email = customerManagementAPIClient.getMyProfile().getEmail();
		}

		CheckoutModel input = CheckoutModel.builder()
				.paymentMethod(checkoutForm.getPaymentMethod().name())
				.creditCardId(checkoutForm.getCreditCardId())
				.shipping(ShippingInputModel.builder()
						.recipient(RecipientModel.builder()
								.firstName(shippingName.firstName())
								.lastName(shippingName.lastName())
								.document(shippingInfo.getDocument())
								.phone(shippingInfo.getPhone())
								.build())
						.address(shippingInfo.getAddress())
						.build())
				.billing(BillingModel.builder()
						.firstName(billingName.firstName())
						.lastName(billingName.lastName())
						.document(checkoutForm.getBillingInfo().getDocument())
						.phone(checkoutForm.getBillingInfo().getPhone())
						.email(email)
						.address(checkoutForm.getBillingInfo().getAddress())
						.build())
				.build();

		OrderModel checkout = checkoutClient.checkout(input);

		return new ModelAndView("redirect:/my-account/orders/" + checkout.getId());
	}

}
