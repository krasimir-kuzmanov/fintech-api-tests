package com.example.fintech.api.tests;

import com.example.fintech.api.client.AccountClient;
import com.example.fintech.api.client.TransactionClient;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.example.fintech.api.tests.TestConstants.ERROR_CODE_INSUFFICIENT_FUNDS;
import static com.example.fintech.api.tests.TestConstants.TRANSACTION_STATUS_SUCCESS;
import static org.hamcrest.Matchers.equalTo;

class TransactionTests extends BaseTest {

  private final AccountClient accountClient = new AccountClient();
  private final TransactionClient transactionClient = new TransactionClient();

  @Test
  void should_MakePayment_When_RequestIsValid() {
    AccountPair accounts = registerAccounts();
    fundAccount(accounts.fromAccountId(), new BigDecimal("100.00"));

    transactionClient.makePayment(
            accounts.fromAccountId(),
            accounts.toAccountId(),
            new BigDecimal("25.00"))
        .then()
        .statusCode(HttpStatus.SC_OK)
        .body("status", equalTo(TRANSACTION_STATUS_SUCCESS));
  }

  @Test
  void should_RejectPayment_When_InsufficientFunds() {
    AccountPair accounts = registerAccounts();
    fundAccount(accounts.fromAccountId(), new BigDecimal("100.00"));

    transactionClient.makePayment(
            accounts.fromAccountId(),
            accounts.toAccountId(),
            new BigDecimal("500.00"))
        .then()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body("error", equalTo(ERROR_CODE_INSUFFICIENT_FUNDS));
  }

  @Test
  void should_ReturnTransactions_When_AccountHasHistory() {
    AccountPair accounts = registerAccounts();
    fundAccount(accounts.fromAccountId(), new BigDecimal("100.00"));

    transactionClient.makePayment(
            accounts.fromAccountId(),
            accounts.toAccountId(),
            new BigDecimal("30.00"))
        .then()
        .statusCode(HttpStatus.SC_OK);

    transactionClient.makePayment(
            accounts.fromAccountId(),
            accounts.toAccountId(),
            new BigDecimal("20.00"))
        .then()
        .statusCode(HttpStatus.SC_OK);

    transactionClient.getTransactions(accounts.fromAccountId())
        .then()
        .statusCode(HttpStatus.SC_OK)
        .body("size()", equalTo(2));
  }

  private AccountPair registerAccounts() {
    String fromAccountId = registerAndGetAccountId("payer");
    String toAccountId = registerAndGetAccountId("receiver");

    return new AccountPair(fromAccountId, toAccountId);
  }

  private void fundAccount(String accountId, BigDecimal amount) {
    accountClient.fund(accountId, amount)
        .then()
        .statusCode(HttpStatus.SC_OK);
  }

  private record AccountPair(String fromAccountId, String toAccountId) {
  }
}
