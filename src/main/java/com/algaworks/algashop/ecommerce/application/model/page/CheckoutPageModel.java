package com.algaworks.algashop.ecommerce.application.model.page;

import com.algaworks.algashop.ecommerce.application.model.client.CategoryModel;
import com.algaworks.algashop.ecommerce.application.model.client.ShoppingCartModel;
import com.algaworks.algashop.ecommerce.application.model.form.CheckoutForm;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class CheckoutPageModel {

	@Builder.Default
	private CheckoutForm checkoutForm = new CheckoutForm();

	public ModelAndView toModelAndView() {
		ModelAndView modelAndView = new ModelAndView("checkout");

		modelAndView.addObject("checkoutForm", checkoutForm);

		return modelAndView;
	}
}
