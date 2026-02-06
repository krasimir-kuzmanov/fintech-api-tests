package com.example.fintech.api.client;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.math.BigDecimal;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class AccountClient {

  private static final String FUND_ENDPOINT = "/account/{accountId}/fund";
  private static final String BALANCE_ENDPOINT = "/account/{accountId}";

  public Response fund(String accountId, BigDecimal amount) {
    return given()
        .contentType(ContentType.JSON)
        .pathParam("accountId", accountId)
        .body(Map.of("amount", amount.toPlainString()))
        .when()
        .post(FUND_ENDPOINT);
  }

  public Response getBalance(String accountId) {
    return given()
        .pathParam("accountId", accountId)
        .when()
        .get(BALANCE_ENDPOINT);
  }
}
