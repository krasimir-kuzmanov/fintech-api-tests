package com.example.fintech.api.tests.auth;

import com.example.fintech.api.client.AuthClient;
import com.example.fintech.api.model.request.RegisterRequest;
import com.example.fintech.api.tests.base.BaseTest;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import static com.example.fintech.api.testdata.TestConstants.DEFAULT_PASSWORD;
import static com.example.fintech.api.testdata.TestConstants.ERROR_CODE_USER_ALREADY_EXISTS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class RegisterTests extends BaseTest {

  private final AuthClient authClient = new AuthClient();

  @Test
  void shouldRegisterUserSuccessfully() {
    // given
    RegisterRequest request = new RegisterRequest("john", DEFAULT_PASSWORD);

    // when
    Response response = authClient.register(request);

    // then
    response.then()
        .statusCode(HttpStatus.SC_OK)
        .body("username", equalTo("john"))
        .body("id", notNullValue());
  }

  @Test
  void shouldRejectDuplicateRegistration() {
    // given
    RegisterRequest request = new RegisterRequest("john", DEFAULT_PASSWORD);

    // when
    authClient.register(request)
        .then()
        .statusCode(HttpStatus.SC_OK);

    // then
    authClient.register(request)
        .then()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body("error", equalTo(ERROR_CODE_USER_ALREADY_EXISTS));
  }
}
