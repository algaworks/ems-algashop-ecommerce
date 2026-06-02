package com.algaworks.algashop.ecommerce.application.converter;

import com.algaworks.algashop.ecommerce.application.model.filter.SortOption;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

class StringToSortOptionConverterTest {

	private final StringToSortOptionConverter converter = new StringToSortOptionConverter();

	@Test
	void shouldConvertAddedAtDesc() {
		SortOption sortOption = converter.convert("ADDED_AT,DESC");

		assertThat(sortOption.getProperty()).isEqualTo(SortOption.Property.ADDED_AT);
		assertThat(sortOption.getDirection()).isEqualTo(Sort.Direction.DESC);
	}

	@Test
	void shouldConvertSalePriceAsc() {
		SortOption sortOption = converter.convert("SALE_PRICE,ASC");

		assertThat(sortOption.getProperty()).isEqualTo(SortOption.Property.SALE_PRICE);
		assertThat(sortOption.getDirection()).isEqualTo(Sort.Direction.ASC);
	}

	@Test
	void shouldConvertIgnoringWhitespaceAndCase() {
		SortOption sortOption = converter.convert(" sale_price , asc ");

		assertThat(sortOption.getProperty()).isEqualTo(SortOption.Property.SALE_PRICE);
		assertThat(sortOption.getDirection()).isEqualTo(Sort.Direction.ASC);
	}

	@Test
	void shouldReturnDefaultSortWhenValueIsInvalid() {
		assertThat(converter.convert("")).isEqualTo(SortOption.defaultSort());
		assertThat(converter.convert("UNKNOWN,DESC")).isEqualTo(SortOption.defaultSort());
		assertThat(converter.convert("ADDED_AT,UNKNOWN")).isEqualTo(SortOption.defaultSort());
		assertThat(converter.convert("ADDED_AT")).isEqualTo(SortOption.defaultSort());
		assertThat(converter.convert("ADDED_AT,DESC,EXTRA")).isEqualTo(SortOption.defaultSort());
	}
}
