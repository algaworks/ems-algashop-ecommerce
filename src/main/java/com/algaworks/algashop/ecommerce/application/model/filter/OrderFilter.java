package com.algaworks.algashop.ecommerce.application.model.filter;

import com.algaworks.algashop.ecommerce.application.model.client.OrderStatus;
import com.algaworks.algashop.ecommerce.application.model.form.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Iterator;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderFilter {
	private OrderStatus status;
	private OffsetDateTime orderedAtFrom;
	private OffsetDateTime orderedAtTo;
	private BigDecimal totalValueFrom;
	private BigDecimal totalValueTo;
	private PaymentMethod paymentMethod;
	private String code;

	private int size;
	private int page;

	@Builder.Default
	@JsonIgnore
	private String sortProperty = "orderedAt";

	@Builder.Default
	@JsonIgnore
	private Sort.Direction direction = Sort.Direction.DESC;

	@JsonProperty
	public String getSort() {
		return sortProperty + "," + direction;
	}

	public static OrderFilter of(Pageable pageable) {
		OrderFilterBuilder builder = OrderFilter.builder()
				.page(pageable.getPageNumber())
				.size(pageable.getPageSize());

		Iterator<Sort.Order> iterator = pageable.getSort().stream().iterator();

		//Apenas a primeira ordenação é considerada
		if (iterator.hasNext()) {
			Sort.Order order = iterator.next();
			builder.sortProperty(order.getProperty());
			builder.direction(order.getDirection());
		}

		return builder.build();
	}
}