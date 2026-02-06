package com.example.fintech.api.client;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.math.BigDecimal;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class TransactionClient {

  private static final String PAYMENT_ENDPOINT = "/transaction/payment";
  private static final String TRANSACTIONS_ENDPOINT = "/transaction/{accountId}";

  public Response makePayment(String fromAccountId, String toAccountId, BigDecimal amount) {
    return given()
        .contentType(ContentType.JSON)
        .body(Map.of(
            "fromAccountId", fromAccountId,
            "toAccountId", toAccountId,
            "amount", amount.toPlainString()
        ))
        .when()
        .post(PAYMENT_ENDPOINT);
  }

  public Response getTransactions(String accountId) {
    return given()
        .pathParam("accountId", accountId)
        .when()
        .get(TRANSACTIONS_ENDPOINT);
  }
}