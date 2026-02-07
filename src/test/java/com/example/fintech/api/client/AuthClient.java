package com.example.fintech.api.client;

import com.example.fintech.api.model.request.LoginRequest;
import com.example.fintech.api.model.request.RegisterRequest;
import com.example.fintech.api.testdata.TestEndpoints;
import io.restassured.response.Response;

public class AuthClient extends BaseClient {

  public Response register(RegisterRequest request) {
    return baseRequest()
        .body(request)
        .when()
        .post(TestEndpoints.AUTH_REGISTER);
  }

  public Response login(LoginRequest request) {
    return baseRequest()
        .body(request)
        .when()
        .post(TestEndpoints.AUTH_LOGIN);
  }
}
