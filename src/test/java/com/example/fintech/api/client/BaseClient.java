package com.example.fintech.api.client;

import static com.example.fintech.api.testdata.HttpConstants.AUTH_HEADER;
import static com.example.fintech.api.testdata.HttpConstants.BEARER_PREFIX;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public abstract class BaseClient {

  protected RequestSpecification baseRequest() {
    return given()
        .contentType(ContentType.JSON);
  }

  protected RequestSpecification authRequest(String token) {
    return given()
        .contentType(ContentType.JSON)
        .header(AUTH_HEADER, BEARER_PREFIX + token);
  }
}
