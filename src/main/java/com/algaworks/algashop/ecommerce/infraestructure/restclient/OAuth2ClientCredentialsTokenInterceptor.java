package com.algaworks.algashop.ecommerce.infraestructure.restclient;

import com.algaworks.algashop.ecommerce.infraestructure.oauth2.OAuth2ClientCredentialsManagerService;
import com.algaworks.algashop.ecommerce.infraestructure.security.AlgaShopSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

//https://github.com/spring-projects/spring-security/issues/13588
@RequiredArgsConstructor
@Component
public class OAuth2ClientCredentialsTokenInterceptor implements ClientHttpRequestInterceptor {
    private static final String clientRegistrationId = "backend";

//    private final OAuth2AuthorizedClientService authorizedClientService;
//    private final OAuth2AuthorizedClientManager manager;
//    private final ClientRegistration clientRegistration;
//    private final AlgaShopSecurityService algaShopSecurityService;

    private final AlgaShopSecurityService algaShopSecurityService;
    private final OAuth2ClientCredentialsManagerService auth2ClientCredentialsManagerService;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        OAuth2AccessToken accessToken = auth2ClientCredentialsManagerService.getAccessToken(clientRegistrationId);
        request.getHeaders().setBearerAuth(accessToken.getTokenValue());
        return execution.execute(request, body);
    }

//    @Override
//    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
//                ClientHttpRequestExecution execution) throws IOException {
//        //todo melhorar logica e testar
//        //apenas pega o token atual, que pode ter expirado
//        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(clientRegistrationId, clientRegistrationId);
//        //todo se vier null deve deslogar
//        if (authorizedClient == null) {
//            throw new RuntimeException("Erro cliente oauth2 não autenticado");
//        }
//
//        if (authorizedClient.getAccessToken().getExpiresAt() != null &&
//                OffsetDateTime.now().toInstant().isAfter(authorizedClient.getAccessToken().getExpiresAt())) {
//            //gera um novo token
//            OAuth2AuthorizeRequest oAuth2AuthorizeRequest = OAuth2AuthorizeRequest
//                    .withClientRegistrationId(clientRegistrationId)
//                    .principal(clientRegistrationId)
//                    .build();
//
//            OAuth2AuthorizedClient reAuthorizedClient = manager.authorize(oAuth2AuthorizeRequest);
//            //todo handler para -> org.springframework.web.client.HttpClientErrorException$Unauthorized: 401 : [no body] //
//            request.getHeaders().setBearerAuth(reAuthorizedClient.getAccessToken().getTokenValue());
//        } else {
//            request.getHeaders().setBearerAuth(authorizedClient.getAccessToken().getTokenValue());
//        }
//
////        //gera um novo token
////        authentication.ifPresent(oAuth2AuthenticationToken -> {
////            OAuth2AuthorizeRequest oAuth2AuthorizeRequest = OAuth2AuthorizeRequest
////                    .withClientRegistrationId(clientRegistrationId)
////                    .principal(authentication.get())
////                    .build();
////
////            OAuth2AuthorizedClient client2 = manager.authorize(oAuth2AuthorizeRequest);
////            request.getHeaders().setBearerAuth(client2.getAccessToken().getTokenValue());
////        });
//
//        return execution.execute(request, body);
//    }
}