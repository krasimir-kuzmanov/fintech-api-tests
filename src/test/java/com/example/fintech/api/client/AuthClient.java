package com.example.fintech.api.client;

import com.example.fintech.api.model.LoginRequest;
import com.example.fintech.api.model.RegisterRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthClient {

  private static final String REGISTER_ENDPOINT = "/auth/register";
  private static final String LOGIN_ENDPOINT = "/auth/login";

  public Response register(RegisterRequest request) {
    return given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post(REGISTER_ENDPOINT);
  }

  public Response login(LoginRequest request) {
    return given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post(LOGIN_ENDPOINT);
  }
}