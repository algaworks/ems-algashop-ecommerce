package com.algaworks.algashop.ecommerce.application.controller;

import com.algaworks.algashop.ecommerce.application.client.CreditCardClient;
import com.algaworks.algashop.ecommerce.application.model.client.CreditCardModel;
import com.algaworks.algashop.ecommerce.application.model.client.TokenizedCreditCardInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyCreditCardControllerTest {

	@Mock
	private CreditCardClient creditCardClient;

	@Test
	void shouldRenderCreditCardsPage() {
		MyCreditCardController controller = new MyCreditCardController(creditCardClient);
		CreditCardModel creditCard = creditCard("card-1");

		when(creditCardClient.findAll()).thenReturn(List.of(creditCard));

		ModelAndView modelAndView = controller.creditCards();

		assertThat(modelAndView.getViewName()).isEqualTo("myaccount-credit-cards");
		assertThat(modelAndView.getModel().get("creditCards")).isEqualTo(List.of(creditCard));
	}

	@Test
	void shouldReturnCreditCardsAsJson() {
		MyCreditCardController controller = new MyCreditCardController(creditCardClient);
		CreditCardModel creditCard = creditCard("card-1");

		when(creditCardClient.findAll()).thenReturn(List.of(creditCard));

		ResponseEntity<List<CreditCardModel>> responseEntity = controller.list();

		assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(responseEntity.getBody()).isEqualTo(List.of(creditCard));
	}

	@Test
	void shouldRegisterCreditCard() {
		MyCreditCardController controller = new MyCreditCardController(creditCardClient);
		CreditCardModel creditCard = creditCard("card-1");
		ArgumentCaptor<TokenizedCreditCardInput> inputCaptor = ArgumentCaptor.forClass(TokenizedCreditCardInput.class);

		when(creditCardClient.register(any(TokenizedCreditCardInput.class))).thenReturn(creditCard);

		TokenizedCreditCardInput input = new TokenizedCreditCardInput("tokenized-card");
		ResponseEntity<CreditCardModel> responseEntity = controller.register(input, bindingResult(input));

		assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(responseEntity.getBody()).isEqualTo(creditCard);
		verify(creditCardClient).register(inputCaptor.capture());
		assertThat(inputCaptor.getValue().getTokenizedCard()).isEqualTo("tokenized-card");
	}

	@Test
	void shouldReturnBadRequestWhenRegisterFails() {
		MyCreditCardController controller = new MyCreditCardController(creditCardClient);

		when(creditCardClient.register(any(TokenizedCreditCardInput.class))).thenThrow(new RuntimeException("provider error"));

		TokenizedCreditCardInput input = new TokenizedCreditCardInput("tokenized-card");
		ResponseEntity<CreditCardModel> responseEntity = controller.register(input, bindingResult(input));

		assertThat(responseEntity.getStatusCode().is4xxClientError()).isTrue();
		assertThat(responseEntity.getBody()).isNull();
	}

	@Test
	void shouldRemoveCreditCard() {
		MyCreditCardController controller = new MyCreditCardController(creditCardClient);

		ResponseEntity<Void> responseEntity = controller.remove("card-1");

		assertThat(responseEntity.getStatusCode().value()).isEqualTo(204);
		verify(creditCardClient).deleteById("card-1");
	}

	@Test
	void shouldReturnBadRequestWhenRemoveFails() {
		MyCreditCardController controller = new MyCreditCardController(creditCardClient);

		doThrow(new RuntimeException("provider error")).when(creditCardClient).deleteById("card-1");

		ResponseEntity<Void> responseEntity = controller.remove("card-1");

		assertThat(responseEntity.getStatusCode().is4xxClientError()).isTrue();
	}

	private CreditCardModel creditCard(String id) {
		CreditCardModel creditCard = new CreditCardModel();
		creditCard.setId(id);
		creditCard.setBrand("Visa");
		creditCard.setLastNumbers("1234");
		creditCard.setExpMonth(12);
		creditCard.setExpYear(2028);
		return creditCard;
	}

	private BeanPropertyBindingResult bindingResult(TokenizedCreditCardInput input) {
		return new BeanPropertyBindingResult(input, "tokenizedCreditCardInput");
	}
}
