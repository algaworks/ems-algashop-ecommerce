package com.algaworks.algashop.ecommerce.infraestructure.restclient;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {
	//protected resources with
	//https://github.com/spring-projects/spring-security/issues/13588

	//timeout
	//https://stackoverflow.com/questions/78084176/how-to-set-connect-read-timeout-in-the-springs-restclient
	//https://docs.spring.io/spring-framework/reference/integration/rest-clients.html

	@Bean
	@Primary
	RestClient restClient(RestClient.Builder builder,
				OAuth2ClientCredentialsTokenInterceptor oAuth2ClientCredentialsTokenInterceptor) {
		return builder
				.requestFactory(fastTimeoutRequestFactory())
				.requestInterceptor(oAuth2ClientCredentialsTokenInterceptor)
				.build();
	}
	
	@Bean
	@Qualifier("userAuthenticatedRestClient")
	RestClient userAuthenticatedRestClient(RestClient.Builder builder, 
			   OAuth2UserTokenInterceptor oAuth2UserTokenInterceptor) {
		return builder
				.requestFactory(fastTimeoutRequestFactory())
				.requestInterceptor(oAuth2UserTokenInterceptor)
				.build();
	}

	private ClientHttpRequestFactory fastTimeoutRequestFactory() {
		ClientHttpRequestFactorySettings requestFactorySettings = new ClientHttpRequestFactorySettings(
				Duration.ofMinutes(1),
				Duration.ofMinutes(1),
				(SslBundle) null);
		return ClientHttpRequestFactories.get(requestFactorySettings);
	}
}
