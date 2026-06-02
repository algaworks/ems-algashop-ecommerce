package com.algaworks.algashop.ecommerce.application.controller;

import com.algaworks.algashop.ecommerce.application.client.CustomerRestClient;
import com.algaworks.algashop.ecommerce.application.client.UserAPIClient;
import com.algaworks.algashop.ecommerce.application.model.client.AddressModel;
import com.algaworks.algashop.ecommerce.application.model.client.CustomerInput;
import com.algaworks.algashop.ecommerce.application.model.client.CustomerModel;
import com.algaworks.algashop.ecommerce.application.model.client.CustomerUpdateInput;
import com.algaworks.algashop.ecommerce.application.model.form.EditCustomerForm;
import com.algaworks.algashop.ecommerce.application.model.page.AlertMessage;
import com.algaworks.algashop.ecommerce.application.util.FullNameParser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MyDataController {

	private final CustomerRestClient customerManagementClient;
	private final UserAPIClient userAPIClient;

	@GetMapping("/my-account/details")
	public ModelAndView getMyData(@ModelAttribute("alertMessage") AlertMessage alertMessage) {
		return getMyData(null, null, alertMessage, false);
	}

	private ModelAndView getMyData(EditCustomerForm customerForm, CustomerModel customer,
								   AlertMessage alertMessage, boolean customerProfileMissing) {
		ModelAndView modelAndView = new ModelAndView("myaccount-your-data");

		if (customerForm == null || customer == null) {
			try {
				customer = customerManagementClient.getMyProfile();
				customerForm = EditCustomerForm.of(customer);
			} catch (HttpClientErrorException.NotFound e) {
				customerProfileMissing = true;
				customerForm = customerForm == null ? new EditCustomerForm() : customerForm;
				if (customerForm.getAddress() == null) {
					customerForm.setAddress(new AddressModel());
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				modelAndView.addObject("alertMessage", AlertMessage.danger(
						"An unknown error occurred while trying to load your data. Please try again later."));
				return modelAndView;
			}
		}

		modelAndView.addObject("customer", customer);
		modelAndView.addObject("customerForm", customerForm);
		modelAndView.addObject("customerProfileMissing", customerProfileMissing);
		modelAndView.addObject("alertMessage", alertMessage);

		return modelAndView;
	}

	@PostMapping("/my-account/details")
	public ModelAndView editMyData(@Valid @ModelAttribute("customerForm") EditCustomerForm customerForm,
								   BindingResult bindingResult,
								   @AuthenticationPrincipal OAuth2User userDetails,
								   RedirectAttributes redirectAttributes) {
		CustomerModel customer = null;
		boolean profileMissing = false;

		try {
			customer = customerManagementClient.getMyProfile();
		} catch (HttpClientErrorException.NotFound e) {
			profileMissing = true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return getMyData(customerForm, null, AlertMessage.danger(
					"An unknown error occurred while trying to load your data. Please try again later."), false);
		}

		if (bindingResult.hasErrors()) {
			return getMyData(customerForm, customer, AlertMessage.danger("There are errors in the form!"), profileMissing);
		}

		FullNameParser.NameParts nameParts = FullNameParser.split(customerForm.getFullName());

		try {
			if (profileMissing) {
				String email = userDetails.getAttribute("email");
				if (email == null || email.isBlank()) {
					throw new AccessDeniedException("Authenticated user e-mail not found.");
				}
				CustomerInput input = CustomerInput.builder()
						.firstName(nameParts.firstName())
						.lastName(nameParts.lastName())
						.email(email)
						.phone(customerForm.getPhone())
						.document(customerForm.getDocument())
						.birthDate(customerForm.getBirthDate())
						.promotionNotificationsAllowed(customerForm.isAllowPromotionNotifications())
						.address(customerForm.getAddress())
						.build();
				customerManagementClient.createMyProfile(input);
			} else {
				CustomerUpdateInput input = CustomerUpdateInput.builder()
						.firstName(nameParts.firstName())
						.lastName(nameParts.lastName())
						.phone(customerForm.getPhone())
						.promotionNotificationsAllowed(customerForm.isAllowPromotionNotifications())
						.address(customerForm.getAddress())
						.build();
				customerManagementClient.updateMyProfile(input);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return getMyData(customerForm, customer, AlertMessage.danger(
					"An unknown error occurred while trying to edit your details. Please try again later."), profileMissing);
		}

		redirectAttributes.addFlashAttribute("alertMessage", AlertMessage.success("Your profile has been saved successfully!"));
		return new ModelAndView("redirect:/my-account/details");
	}

	@PostMapping("/my-account/details/password")
	public ModelAndView requestPasswordChange(RedirectAttributes redirectAttributes) {
		try {
			userAPIClient.requestMyPasswordChange();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return getMyData(null, null, AlertMessage.danger(
					"An unknown error occurred while trying to request your password change. Please try again later."), false);
		}

		redirectAttributes.addFlashAttribute("alertMessage",
				AlertMessage.success("Password change instructions were sent to your e-mail."));
		return new ModelAndView("redirect:/my-account/details");
	}
}
