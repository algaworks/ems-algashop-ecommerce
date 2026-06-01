package com.algaworks.algashop.ecommerce.application.controller;

import com.algaworks.algashop.ecommerce.application.client.*;
import com.algaworks.algashop.ecommerce.application.exception.ErrorMessages;
import com.algaworks.algashop.ecommerce.application.model.client.CustomerInput;
import com.algaworks.algashop.ecommerce.application.model.filter.OrderFilter;
import com.algaworks.algashop.ecommerce.application.model.form.NewCustomerForm;
import com.algaworks.algashop.ecommerce.application.model.page.AlertMessage;
import com.algaworks.algashop.ecommerce.application.model.page.MyAccountPageModel;
import com.algaworks.algashop.ecommerce.application.model.page.NewAccountPageModel;
import com.algaworks.algashop.ecommerce.application.properties.EcommerceProperties;
import com.algaworks.algashop.ecommerce.infraestructure.security.AlgaShopSecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MyAccountController {

	private final CustomerRestClient customerManagementAPIClient;
	private final OrderClient orderClient;
	private final AlgaShopSecurityService algaShopSecurityService;

	@GetMapping("/my-account") //todo flash redirect attr
	public ModelAndView myAccount(NewCustomerForm newCustomerForm) {
		if (algaShopSecurityService.getAuthentication().isPresent()) {
			return existingAccount();
		}

		return newAccount(newCustomerForm);
	}

	private ModelAndView existingAccount() {
		var pageBuilder = MyAccountPageModel.builder();

		OrderFilter orderFilter = OrderFilter.builder()
				.size(3)
				.build();

		try {
			pageBuilder.ordersPage(orderClient.getOrders(orderFilter));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return pageBuilder.build().toModelAndView();
	}

	private ModelAndView newAccount(NewCustomerForm newCustomerForm) {
		var pageBuilder = NewAccountPageModel.builder();
		pageBuilder.newCustomerForm(newCustomerForm);
		return pageBuilder.build().toModelAndView();
	}

	@PostMapping("/my-account")
	public ModelAndView cadastro(@Valid @ModelAttribute("newCustomerForm") NewCustomerForm newCustomerForm,
								 BindingResult bindingResult,
								 RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return newAccount(newCustomerForm);
		}

		CustomerInput customerInput = CustomerInput.builder()
				.address(newCustomerForm.getAddress())
				.email(newCustomerForm.getEmail())
				.birthDate(newCustomerForm.getBirthDate())
				.document(newCustomerForm.getDocument())
				.fullName(newCustomerForm.getFullName())
				.phone(newCustomerForm.getPhone())
				.allowPromotionNotifications(newCustomerForm.isAllowPromotionNotifications())
				.build();

		try {
			customerManagementAPIClient.create(customerInput);
		} catch (Exception e) {
			log.error("Error when trying to create customer.",e);
			bindingResult.addError(new ObjectError("newCustomerForm", ErrorMessages.END_USER_GENERIC_ERROR_MESSAGE));
			redirectAttributes.addFlashAttribute("alertMessage", AlertMessage.danger(ErrorMessages.END_USER_GENERIC_ERROR_MESSAGE));
			return newAccount(newCustomerForm);
		}

		redirectAttributes.addFlashAttribute("alertMessage", AlertMessage.success("Success! Your access password will be sent via email."));
		return new ModelAndView("redirect:/my-account");
	}
}
