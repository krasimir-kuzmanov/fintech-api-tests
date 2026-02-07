package com.example.fintech.api.client;

import com.example.fintech.api.model.request.FundAccountRequest;
import com.example.fintech.api.testdata.TestEndpoints;
import io.restassured.response.Response;

public class AccountClient extends BaseClient {

  public Response fund(String accountId, FundAccountRequest request, String token) {
    return authRequest(token)
        .pathParam("accountId", accountId)
        .body(request)
        .when()
        .post(TestEndpoints.ACCOUNT_FUND);
  }

  public Response getBalance(String accountId, String token) {
    return authRequest(token)
        .pathParam("accountId", accountId)
        .when()
        .get(TestEndpoints.ACCOUNT_BALANCE);
  }
}
