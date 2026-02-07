package com.example.fintech.api.client;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public abstract class BaseClient {

  protected static final String AUTH_HEADER = "Authorization";
  protected static final String BEARER_PREFIX = "Bearer ";

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