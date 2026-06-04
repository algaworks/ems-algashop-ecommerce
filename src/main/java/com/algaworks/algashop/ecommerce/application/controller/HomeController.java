package com.algaworks.algashop.ecommerce.application.controller;

import com.algaworks.algashop.ecommerce.application.client.CategoryClient;
import com.algaworks.algashop.ecommerce.application.client.ProductClient;
import com.algaworks.algashop.ecommerce.application.client.ShoppingCartClient;
import com.algaworks.algashop.ecommerce.application.model.client.CategoryModel;
import com.algaworks.algashop.ecommerce.application.model.client.ShoppingCartModel;
import com.algaworks.algashop.ecommerce.application.model.filter.ProductFilter;
import com.algaworks.algashop.ecommerce.application.model.page.HomePageModel;
import com.algaworks.algashop.ecommerce.application.model.client.PageModel;
import com.algaworks.algashop.ecommerce.application.model.client.ProductModel;
import com.algaworks.algashop.ecommerce.infraestructure.security.AlgaShopSecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

	private final ProductClient productClient;
	private final CategoryClient categoryClient;

	@GetMapping("/")
	public ModelAndView index() {
		var pageBuilder = HomePageModel.builder();

		try {
			PageModel<ProductModel> products = productClient.findAll(new ProductFilter());
			pageBuilder.products(products.getContent());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		try {
			PageModel<CategoryModel> categories = categoryClient.findAll();
			List<CategoryModel> topCategories = categories.getContent();
			if (categories.getTotalElements() > 3) {
				topCategories = categories.getContent().subList(0,3);
			}
			pageBuilder.topCategories(topCategories);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return pageBuilder.build().toModelAndView();
	}

}