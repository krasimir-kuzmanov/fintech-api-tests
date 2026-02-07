package com.example.fintech.api.client;

import com.example.fintech.api.model.request.PaymentRequest;
import com.example.fintech.api.testdata.TestEndpoints;
import io.restassured.response.Response;

public class TransactionClient extends BaseClient {

  public Response makePayment(PaymentRequest request, String token) {
    return authRequest(token)
        .body(request)
        .when()
        .post(TestEndpoints.TRANSACTION_PAYMENT);
  }

  public Response getTransactions(String accountId, String token) {
    return authRequest(token)
        .pathParam("accountId", accountId)
        .when()
        .get(TestEndpoints.TRANSACTION_HISTORY);
  }
}
