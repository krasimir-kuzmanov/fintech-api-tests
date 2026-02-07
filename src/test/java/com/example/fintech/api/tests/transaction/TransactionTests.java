package com.example.fintech.api.tests.transaction;

import com.example.fintech.api.client.AccountClient;
import com.example.fintech.api.client.TransactionClient;
import com.example.fintech.api.model.request.LoginRequest;
import com.example.fintech.api.model.request.FundAccountRequest;
import com.example.fintech.api.model.request.PaymentRequest;
import com.example.fintech.api.model.response.AuthResponse;
import com.example.fintech.api.model.response.TransactionResponse;
import com.example.fintech.api.tests.base.BaseTest;
import org.apache.http.HttpStatus;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.example.fintech.api.testdata.TestConstants.ERROR_CODE_INSUFFICIENT_FUNDS;
import static com.example.fintech.api.testdata.TestConstants.DEFAULT_PASSWORD;
import static com.example.fintech.api.testdata.TestConstants.TRANSACTION_STATUS_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

class TransactionTests extends BaseTest {

  private static final BigDecimal TRANSACTION_EXCESSIVE_AMOUNT = new BigDecimal("500.00");
  private static final BigDecimal TRANSACTION_INITIAL_BALANCE = new BigDecimal("100.00");
  private static final BigDecimal TRANSACTION_PAYMENT_AMOUNT = new BigDecimal("25.00");

  private final AccountClient accountClient = new AccountClient();
  private final TransactionClient transactionClient = new TransactionClient();

  @Test
  void shouldMakePaymentSuccessfully() {
    // given
    AccountPair accounts = registerAccounts();
    String token = loginAndGetToken(accounts.fromUsername());
    fundAccount(accounts.fromAccountId(), token);
    PaymentRequest request = new PaymentRequest(
        accounts.fromAccountId(),
        accounts.toAccountId(),
        TRANSACTION_PAYMENT_AMOUNT);

    // when
    TransactionResponse response = transactionClient.makePaymentAuthenticated(request, token)
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
    String token = loginAndGetToken(accounts.fromUsername());
    fundAccount(accounts.fromAccountId(), token);
    PaymentRequest request = new PaymentRequest(
        accounts.fromAccountId(),
        accounts.toAccountId(),
        TRANSACTION_EXCESSIVE_AMOUNT);

    // when
    Response response = transactionClient.makePaymentAuthenticated(request, token);

    // then
    response.then()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body("error", equalTo(ERROR_CODE_INSUFFICIENT_FUNDS));
  }

  @Test
  void shouldReturnTransactionHistory() {
    // given
    AccountPair accounts = registerAccounts();
    String token = loginAndGetToken(accounts.fromUsername());
    fundAccount(accounts.fromAccountId(), token);
    PaymentRequest firstPayment = new PaymentRequest(
        accounts.fromAccountId(),
        accounts.toAccountId(),
        new BigDecimal("30.00"));
    PaymentRequest secondPayment = new PaymentRequest(
        accounts.fromAccountId(),
        accounts.toAccountId(),
        new BigDecimal("20.00"));

    transactionClient.makePaymentAuthenticated(firstPayment, token)
        .then()
        .statusCode(HttpStatus.SC_OK);

    transactionClient.makePaymentAuthenticated(secondPayment, token)
        .then()
        .statusCode(HttpStatus.SC_OK);

    // when
    Response response = transactionClient.getTransactionsAuthenticated(accounts.fromAccountId(), token);

    // then
    response.then()
        .statusCode(HttpStatus.SC_OK)
        .body("size()", equalTo(2));
  }

  private AccountPair registerAccounts() {
    RegisteredUser fromUser = registerUser("payer");
    RegisteredUser toUser = registerUser("receiver");

    return new AccountPair(fromUser.username(), fromUser.accountId(), toUser.accountId());
  }

  private void fundAccount(String accountId, String token) {
    accountClient.fundAuthenticated(accountId, new FundAccountRequest(TRANSACTION_INITIAL_BALANCE), token)
        .then()
        .statusCode(HttpStatus.SC_OK);
  }

  private String loginAndGetToken(String username) {
    AuthResponse response = authClient.login(new LoginRequest(username, DEFAULT_PASSWORD))
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .as(AuthResponse.class);

    return response.token();
  }

  private record AccountPair(String fromUsername, String fromAccountId, String toAccountId) {
  }
}
