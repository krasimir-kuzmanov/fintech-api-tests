package com.example.fintech.api.tests;

import com.example.fintech.api.client.AuthClient;
import com.example.fintech.api.client.TestClient;
import com.example.fintech.api.model.RegisterRequest;
import com.example.fintech.api.config.RestAssuredConfig;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;

public abstract class BaseTest {

  protected final TestClient testClient = new TestClient();
  protected final AuthClient authClient = new AuthClient();

  @BeforeAll
  static void setup() {
    RestAssuredConfig.init();
  }

  @BeforeEach
  void resetState() {
    testClient.reset();
  }

  protected String registerAndGetAccountId(String usernamePrefix) {
    String username = usernamePrefix + "_" + UUID.randomUUID();
    Response response = authClient.register(
        new RegisterRequest(username, "password"));

    return response
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .path("id");
  }
}
