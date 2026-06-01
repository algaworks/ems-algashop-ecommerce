package com.algaworks.algashop.ecommerce.application.session;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serial;
import java.io.Serializable;

@Component
@SessionScope
@Data
public class ShoppingCartSession implements Serializable {

	@Serial
	private static final long serialVersionUID = -239042403689110522L;

	private String currentShoppingCartId;

	public boolean isEmpty() {
		return StringUtils.isBlank(currentShoppingCartId);
	}
}
