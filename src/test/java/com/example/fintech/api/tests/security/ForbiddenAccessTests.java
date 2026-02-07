package com.example.fintech.api.tests.security;

import com.example.fintech.api.client.AccountClient;
import com.example.fintech.api.client.AuthClient;
import com.example.fintech.api.client.TransactionClient;
import com.example.fintech.api.model.request.LoginRequest;
import com.example.fintech.api.model.request.PaymentRequest;
import com.example.fintech.api.model.request.RegisterRequest;
import com.example.fintech.api.model.response.AuthResponse;
import com.example.fintech.api.testdata.TestDataFactory;
import com.example.fintech.api.testdata.TestConstants;
import com.example.fintech.api.tests.base.BaseTest;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class ForbiddenAccessTests extends BaseTest {

  private static final BigDecimal UNAUTHORIZED_PAYMENT_AMOUNT = new BigDecimal("10.00");

  private final AccountClient accountClient = new AccountClient();
  private final AuthClient authClient = new AuthClient();
  private final TransactionClient transactionClient = new TransactionClient();

  @Test
  void shouldReturn403WhenAccessingAnotherUsersAccount() {
    // given
    RegisterRequest alice = TestDataFactory.userWithPrefix("alice");
    RegisterRequest bob = TestDataFactory.userWithPrefix("bob");

    authClient.register(alice);
    Response bobRegisterResponse = authClient.register(bob);

    String bobAccountId = bobRegisterResponse
        .then()
        .extract()
        .path("id");

    AuthResponse aliceLogin = authClient.login(
            new LoginRequest(alice.username(), TestConstants.DEFAULT_PASSWORD))
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .as(AuthResponse.class);

    String aliceToken = aliceLogin.token();

    // when
    Response response = accountClient.getBalance(bobAccountId, aliceToken);

    // then
    response.then()
        .statusCode(HttpStatus.SC_FORBIDDEN);
  }

  @Test
  void shouldReturn403WhenPayingFromAccountNotOwnedByUser() {
    // given
    RegisterRequest alice = TestDataFactory.userWithPrefix("alice");
    RegisterRequest bob = TestDataFactory.userWithPrefix("bob");

    authClient.register(alice);
    Response bobRegisterResponse = authClient.register(bob);

    String bobAccountId = bobRegisterResponse
        .then()
        .extract()
        .path("id");

    AuthResponse aliceLogin = authClient.login(
            new LoginRequest(alice.username(), TestConstants.DEFAULT_PASSWORD))
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .as(AuthResponse.class);

    String aliceToken = aliceLogin.token();

    PaymentRequest request = new PaymentRequest(
        bobAccountId,
        bobAccountId,
        UNAUTHORIZED_PAYMENT_AMOUNT);

    // when
    Response response = transactionClient.makePayment(request, aliceToken);

    // then
    response.then()
        .statusCode(HttpStatus.SC_FORBIDDEN);
  }
}
