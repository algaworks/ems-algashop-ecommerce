package com.algaworks.algashop.ecommerce.infraestructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class EcommerceWebSecurityConfig {

	//todo rever
//https://github.com/spring-projects/spring-authorization-server/blob/main/samples/demo-client/src/main/java/sample/config/SecurityConfig.java
//https://docs.spring.io/spring-security/reference/servlet/oauth2/login/advanced.html

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
		http
			.authorizeHttpRequests(authorize -> authorize
//				.requestMatchers("/").permitAll()
				.requestMatchers("/checkout/**", "/buy-now/**").authenticated() //, "/my-account/**"
				.requestMatchers("/shopping-cart/add/**").authenticated() //, "/my-account/**"
				.requestMatchers("/my-account/orders/**").authenticated()
				.requestMatchers("/my-account/details/**").authenticated()
				.requestMatchers("/my-account/address", "/my-account/address/**").authenticated()
				.requestMatchers("/my-account/credit-cards", "/my-account/credit-cards/**").authenticated()
				.requestMatchers("/my-account/complete-your-profile", "/my-account/complete-your-profile/**").authenticated()
				.anyRequest().permitAll()
			)
//				.logout(l -> l.logoutSuccessUrl("/").logoutUrl("/logout"))
// todo validar se precisa https://docs.spring.io/spring-security/reference/reactive/oauth2/login/logout.html
//			.formLogin(f -> f.disable())
			.oauth2Login(o -> o.userInfoEndpoint(withDefaults())
				//https://github.com/spring-projects/spring-authorization-server/blob/main/samples/demo-client/src/main/java/sample/web/AuthorizationController.java
				.loginPage("/my-account") //http://algashop-ecommerce:9080/oauth2/authorization/oidc
			).logout(logout -> logout
						.logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository))
						.invalidateHttpSession(true)
						.clearAuthentication(true)
						.deleteCookies("JSESSIONID")
				);
		return http.build();
	}

	private LogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
		OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
				new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);


		// Define para onde o provedor deve redirecionar o usuário após o logout no servidor deles
		oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}?logout-success");

		return oidcLogoutSuccessHandler;
	}

//	@Bean
//	public ClientRegistrationRepository clientRegistrationRepository() {
//		return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
//	}

	@Bean
	public OAuth2AuthorizedClientRepository authorizedClientRepository() { //J
//		OAuth2AuthorizedClientService authorizedClientService
		return new HttpSessionOAuth2AuthorizedClientRepository();
//		return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService);
//		return new JdbcOAuth2AuthorizedClientService(jdbcOperations, authorizedClientService);
	}
}
