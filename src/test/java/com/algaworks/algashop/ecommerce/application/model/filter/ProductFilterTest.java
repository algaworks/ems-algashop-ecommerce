package com.algaworks.algashop.ecommerce.application.model.filter;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;

class ProductFilterTest {

	@Test
	void shouldAddDefaultSortQueryParams() {
		ProductFilter productFilter = new ProductFilter();

		MultiValueMap<String, String> queryParams = productFilter.toQueryParams();

		assertThat(queryParams.getFirst("sortByProperty")).isEqualTo("addedAt");
		assertThat(queryParams.getFirst("sortDirection")).isEqualTo("DESC");
	}

	@Test
	void shouldAddSelectedSortQueryParams() {
		ProductFilter productFilter = ProductFilter.builder()
				.sort(SortOption.of(SortOption.Property.SALE_PRICE, Sort.Direction.ASC))
				.build();

		MultiValueMap<String, String> queryParams = productFilter.toQueryParams();

		assertThat(queryParams.getFirst("sortByProperty")).isEqualTo("salePrice");
		assertThat(queryParams.getFirst("sortDirection")).isEqualTo("ASC");
	}
}
