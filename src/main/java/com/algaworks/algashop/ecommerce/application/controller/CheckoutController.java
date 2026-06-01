package com.algaworks.algashop.ecommerce.application.controller;

import com.algaworks.algashop.ecommerce.application.client.CheckoutClient;
import com.algaworks.algashop.ecommerce.application.client.CustomerRestClient;
import com.algaworks.algashop.ecommerce.application.model.client.*;
import com.algaworks.algashop.ecommerce.application.model.form.CheckoutForm;
import com.algaworks.algashop.ecommerce.application.model.page.CheckoutPageModel;
import com.algaworks.algashop.ecommerce.application.service.ShoppingCartService;
import com.algaworks.algashop.ecommerce.application.session.ShoppingCartSession;
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
	private final ShoppingCartSession shoppingCartSession;

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

		var inputBuilder = CheckoutModel.builder()
				.billing(checkoutForm.getBillingInfo());

		if (checkoutForm.isSendToDifferentAddress()) {
			inputBuilder.shipping(checkoutForm.getShippingInfo());
		} else {
			inputBuilder.shipping(checkoutForm.getBillingInfo());
		}

		inputBuilder.payment(
				PaymentInfo.builder()
						.method(checkoutForm.getPaymentMethod().toString())
						.creditCardId(checkoutForm.getCreditCardId())
						.build()
		);

		String customerId = userDetails.getAttribute("sub");

		inputBuilder.customerId(customerId);

		CheckoutResponseModel checkout = checkoutClient.checkout(shoppingCartSession.getCurrentShoppingCartId(), inputBuilder.build());

		return new ModelAndView("redirect:/my-account/orders/" + checkout.getOrderId());
	}

}