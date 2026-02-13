package com.example.fintech.api.tests.base;

import com.example.fintech.api.client.AuthClient;
import com.example.fintech.api.model.request.LoginRequest;
import com.example.fintech.api.config.RestAssuredConfig;
import com.example.fintech.api.model.request.RegisterRequest;
import com.example.fintech.api.model.response.AuthResponse;
import com.example.fintech.api.testdata.TestDataFactory;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import static com.example.fintech.api.testdata.TestConstants.DEFAULT_PASSWORD;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {

  protected final AuthClient authClient = new AuthClient();

  @BeforeAll
  void setup() {
    RestAssuredConfig.init();
  }

  protected RegisteredUser registerUser(String usernamePrefix) {
    RegisterRequest request = TestDataFactory.userWithPrefix(usernamePrefix);
    Response response = authClient.register(request);

    String accountId = response
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .path("id");

    return new RegisteredUser(request.username(), accountId);
  }

  protected String loginAndGetToken(String username) {
    AuthResponse response = authClient.login(new LoginRequest(username, DEFAULT_PASSWORD))
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .as(AuthResponse.class);

    return response.token();
  }

  protected record RegisteredUser(String username, String accountId) {
  }
}
