package com.example.fintech.api.tests.transaction;

import com.example.fintech.api.client.AccountClient;
import com.example.fintech.api.client.TransactionClient;
import com.example.fintech.api.model.request.FundAccountRequest;
import com.example.fintech.api.model.request.PaymentRequest;
import com.example.fintech.api.model.response.TransactionResponse;
import com.example.fintech.api.tests.base.BaseTest;
import org.apache.http.HttpStatus;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.example.fintech.api.testdata.TestConstants.ERROR_CODE_INSUFFICIENT_FUNDS;
import static com.example.fintech.api.testdata.TestConstants.TRANSACTION_EXCESSIVE_AMOUNT;
import static com.example.fintech.api.testdata.TestConstants.TRANSACTION_FIRST_AMOUNT;
import static com.example.fintech.api.testdata.TestConstants.TRANSACTION_INITIAL_BALANCE;
import static com.example.fintech.api.testdata.TestConstants.TRANSACTION_PAYMENT_AMOUNT;
import static com.example.fintech.api.testdata.TestConstants.TRANSACTION_SECOND_AMOUNT;
import static com.example.fintech.api.testdata.TestConstants.TRANSACTION_STATUS_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

class TransactionTests extends BaseTest {

  private final AccountClient accountClient = new AccountClient();
  private final TransactionClient transactionClient = new TransactionClient();

  @Test
  void shouldMakePaymentSuccessfully() {
    // given
    AccountPair accounts = registerAccounts();
    fundAccount(accounts.fromAccountId(), TRANSACTION_INITIAL_BALANCE);
    PaymentRequest request = new PaymentRequest(
        accounts.fromAccountId(),
        accounts.toAccountId(),
        TRANSACTION_PAYMENT_AMOUNT);

    // when
    TransactionResponse response = transactionClient.makePayment(request)
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .as(TransactionResponse.class);

    // then
    assertThat(response.status()).isEqualTo(TRANSACTION_STATUS_SUCCESS);
  }

  @Test
  void shouldRejectPaymentWithInsufficientFunds() {
    // given
    AccountPair accounts = registerAccounts();
    fundAccount(accounts.fromAccountId(), TRANSACTION_INITIAL_BALANCE);
    PaymentRequest request = new PaymentRequest(
        accounts.fromAccountId(),
        accounts.toAccountId(),
        TRANSACTION_EXCESSIVE_AMOUNT);

    // when
    Response response = transactionClient.makePayment(request);

    // then
    response.then()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body("error", equalTo(ERROR_CODE_INSUFFICIENT_FUNDS));
  }

  @Test
  void shouldReturnTransactionHistory() {
    // given
    AccountPair accounts = registerAccounts();
    fundAccount(accounts.fromAccountId(), TRANSACTION_INITIAL_BALANCE);
    PaymentRequest firstPayment = new PaymentRequest(
        accounts.fromAccountId(),
        accounts.toAccountId(),
        TRANSACTION_FIRST_AMOUNT);
    PaymentRequest secondPayment = new PaymentRequest(
        accounts.fromAccountId(),
        accounts.toAccountId(),
        TRANSACTION_SECOND_AMOUNT);

    transactionClient.makePayment(firstPayment)
        .then()
        .statusCode(HttpStatus.SC_OK);

    transactionClient.makePayment(secondPayment)
        .then()
        .statusCode(HttpStatus.SC_OK);

    // when
    Response response = transactionClient.getTransactions(accounts.fromAccountId());

    // then
    response.then()
        .statusCode(HttpStatus.SC_OK)
        .body("size()", equalTo(2));
  }

  private AccountPair registerAccounts() {
    String fromAccountId = registerAndGetAccountId("payer");
    String toAccountId = registerAndGetAccountId("receiver");

    return new AccountPair(fromAccountId, toAccountId);
  }

  private void fundAccount(String accountId, BigDecimal amount) {
    accountClient.fund(accountId, new FundAccountRequest(amount))
        .then()
        .statusCode(HttpStatus.SC_OK);
  }

  private record AccountPair(String fromAccountId, String toAccountId) {
  }
}
