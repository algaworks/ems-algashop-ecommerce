package com.algaworks.algashop.ecommerce.application.converter;

import com.algaworks.algashop.ecommerce.application.model.filter.SortOption;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToSortConverter implements Converter<String, SortOption> {

	@Override
	public SortOption convert(String source) {
		return SortOption.from(source);
	}
}
