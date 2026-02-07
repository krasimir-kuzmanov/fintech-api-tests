package com.example.fintech.api.client;

import com.example.fintech.api.model.request.FundAccountRequest;
import io.restassured.response.Response;

public class AccountClient extends BaseClient {

  private static final String FUND_ENDPOINT = "/account/{accountId}/fund";
  private static final String BALANCE_ENDPOINT = "/account/{accountId}";

  public Response fund(String accountId, FundAccountRequest request) {
    return baseRequest()
        .pathParam("accountId", accountId)
        .body(request)
        .when()
        .post(FUND_ENDPOINT);
  }

  public Response fundAuthenticated(String accountId, FundAccountRequest request, String token) {
    return authRequest(token)
        .pathParam("accountId", accountId)
        .body(request)
        .when()
        .post(FUND_ENDPOINT);
  }

  public Response getBalance(String accountId) {
    return baseRequest()
        .pathParam("accountId", accountId)
        .when()
        .get(BALANCE_ENDPOINT);
  }

  public Response getBalanceAuthenticated(String accountId, String token) {
    return authRequest(token)
        .pathParam("accountId", accountId)
        .when()
        .get(BALANCE_ENDPOINT);
  }
}