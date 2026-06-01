package com.algaworks.algashop.ecommerce.application.controller;

import com.algaworks.algashop.ecommerce.application.client.CustomerRestClient;
import com.algaworks.algashop.ecommerce.application.client.UserAPIClient;
import com.algaworks.algashop.ecommerce.application.model.client.*;
import com.algaworks.algashop.ecommerce.application.model.form.EditCustomerForm;
import com.algaworks.algashop.ecommerce.application.model.page.AlertMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
		return getMyData(null,null, alertMessage);
	}

	private ModelAndView getMyData(EditCustomerForm customerForm, CustomerModel customer, AlertMessage alertMessage) {
		ModelAndView modelAndView = new ModelAndView("myaccount-your-data");

		if (customerForm == null || customer == null) {
			try {
				customer = customerManagementClient.getMyProfile();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				modelAndView.addObject("alertMessage", AlertMessage.danger(
						"An unknown error occurred while trying to load your data. Please try again later."));
				return modelAndView;
			}

			customerForm = EditCustomerForm.of(customer);
		}

		modelAndView.addObject("customer", customer);
		modelAndView.addObject("customerForm", customerForm);
		modelAndView.addObject("alertMessage", alertMessage);

		return modelAndView;
	}

	@PostMapping("/my-account/details")
	public ModelAndView editMyData(@Valid @ModelAttribute("customerForm") EditCustomerForm customerForm,
								   BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		CustomerModel customer;
		try {
			customer = customerManagementClient.getMyProfile();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return getMyData(customerForm, null, AlertMessage.danger(
					"An unknown error occurred while trying to load your data. Please try again later."));
		}

		if (bindingResult.hasErrors()) {
			return getMyData(customerForm, customer, AlertMessage.danger("There are errors in the form!"));
		}

		CustomerUpdateInput input = CustomerUpdateInput.builder()
				.document(customerForm.getDocument())
				.fullName(customerForm.getFullName())
				.phone(customerForm.getPhone())
				.birthDate(customerForm.getBirthDate())
				.allowPromotionNotifications(customerForm.isAllowPromotionNotifications())
				.address(customer.getAddress())
				.build();

		try {
			customerManagementClient.updateMyProfile(input);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return getMyData(customerForm, customer, AlertMessage.danger(
					"An unknown error occurred while trying to edit your details. Please try again later."));
		}

		redirectAttributes.addFlashAttribute("alertMessage", AlertMessage.success("Your profile has been edited successfully!"));

		return new ModelAndView("redirect:/my-account/details");
	}

	@PostMapping("/my-account/details/email")
	public ModelAndView editMyEmail(@Valid CustomerEmailInput input,
									BindingResult bindingResult,
									RedirectAttributes redirectAttributes) {
		CustomerModel customer;
		try {
			customer = customerManagementClient.getMyProfile();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return getMyData(null, null, AlertMessage.danger(
					"An unknown error occurred while trying to load your data. Please try again later."));
		}

		if (bindingResult.hasErrors()) {
			return getMyData(null, customer, AlertMessage.danger("There are errors in the form!"));
		}

		try {
			customerManagementClient.updateMyEmail(input);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return getMyData(null, null, AlertMessage.danger(
					"An unknown error occurred while trying to edit your details. Please try again later."));
		}

		redirectAttributes.addFlashAttribute("alertMessage", AlertMessage.success("Your profile has been edited successfully!"));

		return new ModelAndView("redirect:/my-account/details");
	}

	@PostMapping("/my-account/details/password")
	public ModelAndView changeMyPassword(@Valid CustomerPasswordInput input,
									BindingResult bindingResult,
									RedirectAttributes redirectAttributes) {
		CustomerModel customer;
		try {
			customer = customerManagementClient.getMyProfile();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return getMyData(null, null, AlertMessage.danger(
					"An unknown error occurred while trying to load your data. Please try again later."));
		}

		if (bindingResult.hasErrors()) {
			return getMyData(null, customer, AlertMessage.danger("There are errors in the form!"));
		}

		try {
			userAPIClient.updateMyPassword(input);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return getMyData(null, null, AlertMessage.danger(
					"An unknown error occurred while trying to edit your details. Please try again later."));
		}

		redirectAttributes.addFlashAttribute("alertMessage", AlertMessage.success("Your password has been edited successfully!"));

		return new ModelAndView("redirect:/my-account/details");
	}
}