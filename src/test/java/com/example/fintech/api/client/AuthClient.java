package com.example.fintech.api.client;

import com.example.fintech.api.model.request.LoginRequest;
import com.example.fintech.api.model.request.RegisterRequest;
import io.restassured.response.Response;

public class AuthClient extends BaseClient {

  private static final String LOGIN_ENDPOINT = "/auth/login";
  private static final String REGISTER_ENDPOINT = "/auth/register";

  public Response register(RegisterRequest request) {
    return baseRequest()
        .body(request)
        .when()
        .post(REGISTER_ENDPOINT);
  }

  public Response login(LoginRequest request) {
    return baseRequest()
        .body(request)
        .when()
        .post(LOGIN_ENDPOINT);
  }
}