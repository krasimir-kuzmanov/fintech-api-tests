package com.example.fintech.api.tests.security;

import com.example.fintech.api.client.AccountClient;
import com.example.fintech.api.client.AuthClient;
import com.example.fintech.api.client.TestSupportClient;
import com.example.fintech.api.model.request.LoginRequest;
import com.example.fintech.api.model.request.RegisterRequest;
import com.example.fintech.api.model.response.AuthResponse;
import com.example.fintech.api.testdata.TestDataFactory;
import com.example.fintech.api.testdata.TestConstants;
import com.example.fintech.api.tests.base.BaseTest;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

class ForbiddenAccessTests extends BaseTest {

  private final AccountClient accountClient = new AccountClient();
  private final AuthClient authClient = new AuthClient();
  private final TestSupportClient testSupportClient = new TestSupportClient();

  @Test
  void shouldReturn403WhenAccessingAnotherUsersAccount() {
    // given
    testSupportClient.reset();

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
    Response response = accountClient.getBalanceAuthenticated(bobAccountId, aliceToken);

    // then
    response.then()
        .statusCode(HttpStatus.SC_FORBIDDEN);
  }
}
