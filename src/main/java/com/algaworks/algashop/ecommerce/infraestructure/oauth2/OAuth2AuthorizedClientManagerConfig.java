package com.algaworks.algashop.ecommerce.infraestructure.oauth2;//package com.algaworks.algashop.order.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

import java.util.HashMap;

@Configuration
public class OAuth2AuthorizedClientManagerConfig {

  // TODO opcional, Bean com modo em memória já é configurado
  // deve conter todos flows usados
  @Bean
  public OAuth2AuthorizedClientManager authorizedClientManager(
          ClientRegistrationRepository clientRegistrationRepository,
          OAuth2AuthorizedClientRepository authorizedClientRepository) {
    var authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
            .refreshToken()
            .authorizationCode()
            .clientCredentials()
            .build();
    var authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
    return authorizedClientManager;
  }


  //todo alternativa
//  @Bean
//  public OAuth2AuthorizedClientManager authorizedClientManager(
//          ClientRegistrationRepository clientRegistrationRepository
//  ) {
//    OAuth2AuthorizedClientProvider authorizedClientProvider =
//            OAuth2AuthorizedClientProviderBuilder.builder()
//                    .clientCredentials()
//                    .password() // used for our usecase
//                    .refreshToken() // must be added to handle refresh tokens
//                    .build();
//
//    AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
//            new AuthorizedClientServiceOAuth2AuthorizedClientManager(
//                    clientRegistrationRepository,
//                    new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository)
//            );
//
//    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
//    authorizedClientManager.setContextAttributesMapper(oAuth2AuthorizeRequest -> {
//      var attributes = new HashMap<>(
//              new AuthorizedClientServiceOAuth2AuthorizedClientManager.DefaultContextAttributesMapper()
//                      .apply(oAuth2AuthorizeRequest)
//      );
//      attributes.put(OAuth2AuthorizationContext.USERNAME_ATTRIBUTE_NAME, username);
//      attributes.put(OAuth2AuthorizationContext.PASSWORD_ATTRIBUTE_NAME, password);
//      return attributes;
//    });
//
//    return authorizedClientManager;
//  }

}