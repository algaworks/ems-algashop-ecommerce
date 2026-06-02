package com.algaworks.algashop.ecommerce.application.controller;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class MyDataTemplateTest {

	@Test
	void shouldNotBindAddressFieldsOnMyDataTemplate() throws Exception {
		String template = Files.readString(Path.of("src/main/resources/templates/myaccount-your-data.html"));

		assertThat(template).doesNotContain("address.");
	}
}
