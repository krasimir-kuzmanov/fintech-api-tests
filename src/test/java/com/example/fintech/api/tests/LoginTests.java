package com.example.fintech.api.tests;

import com.example.fintech.api.client.AuthClient;
import com.example.fintech.api.model.LoginRequest;
import com.example.fintech.api.model.RegisterRequest;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import static com.example.fintech.api.tests.TestConstants.ERROR_CODE_INVALID_CREDENTIALS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class LoginTests extends BaseTest {

  private final AuthClient authClient = new AuthClient();

  @Test
  void should_LoginSuccessfully_When_CredentialsAreValid() {
    authClient.register(
        new RegisterRequest("alice", "secret"));

    Response response = authClient.login(
        new LoginRequest("alice", "secret"));

    response.then()
        .statusCode(HttpStatus.SC_OK)
        .body("token", notNullValue())
        .body("userId", notNullValue());
  }

  @Test
  void should_RejectLogin_When_PasswordIsIncorrect() {
    authClient.register(
        new RegisterRequest("bob", "correct"));

    Response response = authClient.login(
        new LoginRequest("bob", "wrong"));

    response.then()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body("error", equalTo(ERROR_CODE_INVALID_CREDENTIALS));
  }
}
