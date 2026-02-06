package com.example.fintech.api.client;

import static io.restassured.RestAssured.given;

import org.apache.http.HttpStatus;

public class TestClient {

  private static final String RESET_ENDPOINT = "/test/reset";

  public void reset() {
    given()
        .when()
        .post(RESET_ENDPOINT)
        .then()
        .statusCode(HttpStatus.SC_OK);
  }
}
