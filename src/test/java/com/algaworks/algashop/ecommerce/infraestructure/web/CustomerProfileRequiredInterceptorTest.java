package com.algaworks.algashop.ecommerce.infraestructure.web;

import com.algaworks.algashop.ecommerce.application.client.CustomerRestClient;
import com.algaworks.algashop.ecommerce.application.model.client.CustomerModel;
import com.algaworks.algashop.ecommerce.infraestructure.security.AlgaShopSecurityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerProfileRequiredInterceptorTest {

	@Mock
	private AlgaShopSecurityService algaShopSecurityService;

	@Mock
	private CustomerRestClient customerRestClient;

	@Test
	void shouldRedirectAuthenticatedUserWithoutCustomerProfileToNewCustomerProfile() throws Exception {
		CustomerProfileRequiredInterceptor interceptor = new CustomerProfileRequiredInterceptor(
				algaShopSecurityService, customerRestClient);
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/my-account");
		MockHttpServletResponse response = new MockHttpServletResponse();

		when(algaShopSecurityService.isAuthenticated()).thenReturn(true);
		when(customerRestClient.getMyProfile()).thenThrow(notFound());

		boolean proceed = interceptor.preHandle(request, response, new Object());

		assertThat(proceed).isFalse();
		assertThat(response.getRedirectedUrl()).isEqualTo("/my-account/complete-your-profile");
	}

	@Test
	void shouldRedirectAuthenticatedUserWithCustomerProfileAwayFromNewCustomerProfile() throws Exception {
		CustomerProfileRequiredInterceptor interceptor = new CustomerProfileRequiredInterceptor(
				algaShopSecurityService, customerRestClient);
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/my-account/complete-your-profile");
		MockHttpServletResponse response = new MockHttpServletResponse();

		when(algaShopSecurityService.isAuthenticated()).thenReturn(true);
		when(customerRestClient.getMyProfile()).thenReturn(new CustomerModel());

		boolean proceed = interceptor.preHandle(request, response, new Object());

		assertThat(proceed).isFalse();
		assertThat(response.getRedirectedUrl()).isEqualTo("/my-account");
	}

	@Test
	void shouldAllowAuthenticatedUserWithoutCustomerProfileOnNewCustomerProfile() throws Exception {
		CustomerProfileRequiredInterceptor interceptor = new CustomerProfileRequiredInterceptor(
				algaShopSecurityService, customerRestClient);
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/my-account/complete-your-profile");
		MockHttpServletResponse response = new MockHttpServletResponse();

		when(algaShopSecurityService.isAuthenticated()).thenReturn(true);
		when(customerRestClient.getMyProfile()).thenThrow(notFound());

		boolean proceed = interceptor.preHandle(request, response, new Object());

		assertThat(proceed).isTrue();
		assertThat(response.getRedirectedUrl()).isNull();
	}

	private HttpClientErrorException notFound() {
		return HttpClientErrorException.NotFound.create(
				HttpStatus.NOT_FOUND,
				"Not Found",
				HttpHeaders.EMPTY,
				new byte[0],
				StandardCharsets.UTF_8
		);
	}
}
