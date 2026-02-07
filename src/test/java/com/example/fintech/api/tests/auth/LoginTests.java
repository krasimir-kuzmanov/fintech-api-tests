package com.example.fintech.api.tests.auth;

import com.example.fintech.api.client.AuthClient;
import com.example.fintech.api.model.request.LoginRequest;
import com.example.fintech.api.model.request.RegisterRequest;
import com.example.fintech.api.tests.base.BaseTest;
import com.example.fintech.api.testdata.TestDataFactory;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import static com.example.fintech.api.testdata.TestConstants.DEFAULT_PASSWORD;
import static com.example.fintech.api.testdata.TestConstants.ERROR_CODE_INVALID_CREDENTIALS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class LoginTests extends BaseTest {

  private final AuthClient authClient = new AuthClient();

  @Test
  void shouldLoginSuccessfullyWithValidCredentials() {
    // given
    RegisterRequest registerRequest = TestDataFactory.userWithPrefix("alice");
    authClient.register(registerRequest);

    LoginRequest loginRequest = new LoginRequest(registerRequest.username(), DEFAULT_PASSWORD);

    // when
    Response response = authClient.login(loginRequest);

    // then
    response.then()
        .statusCode(HttpStatus.SC_OK)
        .body("token", notNullValue())
        .body("userId", notNullValue());
  }

  @Test
  void shouldRejectLoginWithIncorrectPassword() {
    // given
    RegisterRequest registerRequest = TestDataFactory.userWithPrefix("bob");
    authClient.register(registerRequest);

    LoginRequest loginRequest = new LoginRequest(registerRequest.username(), "wrong");

    // when
    Response response = authClient.login(loginRequest);

    // then
    response.then()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body("error", equalTo(ERROR_CODE_INVALID_CREDENTIALS));
  }
}
