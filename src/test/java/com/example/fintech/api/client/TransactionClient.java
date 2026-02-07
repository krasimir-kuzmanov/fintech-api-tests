package com.example.fintech.api.client;

import com.example.fintech.api.model.request.PaymentRequest;
import io.restassured.response.Response;

public class TransactionClient extends BaseClient {

  private static final String PAYMENT_ENDPOINT = "/transaction/payment";
  private static final String TRANSACTIONS_ENDPOINT = "/transaction/{accountId}";

  public Response makePayment(PaymentRequest request) {
    return baseRequest()
        .body(request)
        .when()
        .post(PAYMENT_ENDPOINT);
  }

  public Response makePaymentAuthenticated(PaymentRequest request, String token) {
    return authRequest(token)
        .body(request)
        .when()
        .post(PAYMENT_ENDPOINT);
  }

  public Response getTransactions(String accountId) {
    return baseRequest()
        .pathParam("accountId", accountId)
        .when()
        .get(TRANSACTIONS_ENDPOINT);
  }

  public Response getTransactionsAuthenticated(String accountId, String token) {
    return authRequest(token)
        .pathParam("accountId", accountId)
        .when()
        .get(TRANSACTIONS_ENDPOINT);
  }
}