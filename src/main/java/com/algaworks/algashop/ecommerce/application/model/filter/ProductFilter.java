package com.algaworks.algashop.ecommerce.application.model.filter;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilter {
	private String term;
	private String categoryId;
	private String[] categoriesId;
	private Boolean hasDiscount;
	private BigDecimal priceFrom;
	private BigDecimal priceTo;

	public List<String> getCategoriesIdAsList() {
		if (categoriesId == null) {
			return Collections.emptyList();
		}

		return Arrays.asList(categoriesId);
	}

	@Builder.Default
	private int size = 6;

	@Builder.Default
	private int page = 0;

	@Builder.Default
	private SortType sort = SortType.ADDED_AT;

	@Builder.Default
	private Sort.Direction sortDirection = Sort.Direction.DESC;

	public MultiValueMap<String, String> toQueryParams() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

		if (this.getCategoriesId() != null ) {
			for (String id : this.getCategoriesId()) {
				params.add("categoriesId", id);
			}
		}

		if (StringUtils.isNotBlank(this.getTerm())) {
			params.add("term", this.getTerm());
		}

		if (this.getHasDiscount() != null) {
			params.add("hasDiscount", this.getHasDiscount().toString());
		}

		if (this.getPriceFrom() != null) {
			params.add("priceFrom", this.getPriceFrom().toString());
		}

		if (this.getPriceTo() != null) {
			params.add("priceTo", this.getPriceTo().toString());
		}

		if (this.getSort() != null) {
			params.add("sortByProperty", this.getSort().name());
		}

		params.add("sortDirection", this.getSortDirection().name());

		params.add("size", Integer.valueOf(this.getSize()).toString());
		params.add("page", Integer.valueOf(this.getPage()).toString());

		return params;
	}

	@Getter
	@AllArgsConstructor
	enum SortType {
		ADDED_AT("addedAt"),
		SALE_PRICE("salePrice");

		private final String propertyName;
	}

	public static ProductFilter of(Pageable pageable) {
		ProductFilter.ProductFilterBuilder builder = ProductFilter.builder()
				.page(pageable.getPageNumber())
				.size(pageable.getPageSize());

		Iterator<Sort.Order> iterator = pageable.getSort().stream().iterator();

		//Apenas a primeira ordenação é considerada
		if (iterator.hasNext()) {
			Sort.Order order = iterator.next();
			builder.sort(SortType.valueOf(order.getProperty()));
			builder.sortDirection(order.getDirection());
		}

		return builder.build();
	}
}
