package com.algaworks.algashop.ecommerce.application.model.filter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@Getter
@EqualsAndHashCode
public class SortOption {
	private static final Property DEFAULT_PROPERTY = Property.ADDED_AT;
	private static final Sort.Direction DEFAULT_DIRECTION = Sort.Direction.DESC;

	private final Property property;
	private final Sort.Direction direction;

	public SortOption(Property property, Sort.Direction direction) {
		this.property = property == null ? DEFAULT_PROPERTY : property;
		this.direction = direction == null ? DEFAULT_DIRECTION : direction;
	}

	public static SortOption of(Property property, Sort.Direction direction) {
		return new SortOption(property, direction);
	}

	public static SortOption defaultSort() {
		return new SortOption(DEFAULT_PROPERTY, DEFAULT_DIRECTION);
	}

	public static SortOption from(String value) {
		if (value == null || value.isBlank()) {
			return defaultSort();
		}

		String[] tokens = value.split(",", -1);
		if (tokens.length != 2) {
			return defaultSort();
		}

		Optional<Property> property = Property.from(tokens[0]);
		Optional<Sort.Direction> direction = parseDirection(tokens[1]);

		if (property.isEmpty() || direction.isEmpty()) {
			return defaultSort();
		}

		return new SortOption(property.get(), direction.get());
	}

	public static SortOption from(Sort.Order order) {
		if (order == null) {
			return defaultSort();
		}

		return Property.from(order.getProperty())
				.map(property -> new SortOption(property, order.getDirection()))
				.orElseGet(SortOption::defaultSort);
	}

	public String getPropertyName() {
		return this.property.getPropertyName();
	}

	@Override
	public String toString() {
		return this.property.name() + "," + this.direction.name();
	}

	private static Optional<Sort.Direction> parseDirection(String value) {
		if (value == null || value.isBlank()) {
			return Optional.empty();
		}

		try {
			return Optional.of(Sort.Direction.valueOf(value.trim().toUpperCase()));
		} catch (IllegalArgumentException e) {
			return Optional.empty();
		}
	}

	@Getter
	@RequiredArgsConstructor
	public enum Property {
		ADDED_AT("addedAt"),
		SALE_PRICE("salePrice");

		private final String propertyName;

		static Optional<Property> from(String value) {
			if (value == null || value.isBlank()) {
				return Optional.empty();
			}

			String trimmedValue = value.trim();
			for (Property property : values()) {
				if (property.name().equalsIgnoreCase(trimmedValue)
						|| property.getPropertyName().equalsIgnoreCase(trimmedValue)) {
					return Optional.of(property);
				}
			}

			return Optional.empty();
		}
	}
}
