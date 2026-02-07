package com.example.fintech.api.tests.auth;

import com.example.fintech.api.client.AuthClient;
import com.example.fintech.api.model.request.RegisterRequest;
import com.example.fintech.api.tests.BaseTest;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import static com.example.fintech.api.testdata.TestConstants.ERROR_CODE_USER_ALREADY_EXISTS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class RegisterTests extends BaseTest {

  private final AuthClient authClient = new AuthClient();

  @Test
  void should_RegisterUser_When_RequestIsValid() {
    Response response = authClient.register(
        new RegisterRequest("john", "password"));

    response.then()
        .statusCode(HttpStatus.SC_OK)
        .body("username", equalTo("john"))
        .body("id", notNullValue());
  }

  @Test
  void should_NotAllowDuplicateRegistration_When_UsernameExists() {
    RegisterRequest request = new RegisterRequest("john", "password");

    authClient.register(request)
        .then()
        .statusCode(HttpStatus.SC_OK);

    authClient.register(request)
        .then()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body("error", equalTo(ERROR_CODE_USER_ALREADY_EXISTS));
  }
}
