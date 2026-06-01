package com.algaworks.algashop.ecommerce.infraestructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

public class OAuth2AuthorizedClientServiceRedis implements OAuth2AuthorizedClientService {



	@Override
	public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {
		return null;
	}

	@Override
	public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {

	}

	@Override
	public void removeAuthorizedClient(String clientRegistrationId, String principalName) {

	}
}
