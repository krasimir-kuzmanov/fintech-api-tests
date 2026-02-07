package com.example.fintech.api.tests.e2e;

import com.example.fintech.api.client.AccountClient;
import com.example.fintech.api.client.AuthClient;
import com.example.fintech.api.client.TransactionClient;
import com.example.fintech.api.model.request.FundAccountRequest;
import com.example.fintech.api.model.request.LoginRequest;
import com.example.fintech.api.model.request.PaymentRequest;
import com.example.fintech.api.model.response.AuthResponse;
import com.example.fintech.api.model.response.BalanceResponse;
import com.example.fintech.api.tests.base.BaseTest;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.example.fintech.api.testdata.TestConstants.DEFAULT_PASSWORD;
import static com.example.fintech.api.testdata.TestConstants.TRANSACTION_STATUS_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

class EndToEndAuthenticatedFlowTest extends BaseTest {

  private static final BigDecimal E2E_FUND_AMOUNT = new BigDecimal("100.00");
  private static final BigDecimal E2E_PAYMENT_AMOUNT = new BigDecimal("40.00");

  private final AccountClient accountClient = new AccountClient();
  private final AuthClient authClient = new AuthClient();
  private final TransactionClient transactionClient = new TransactionClient();

  @Test
  void shouldCompletePaymentFlow() {
    // given
    RegisteredUser alice = registerUser("alice");
    RegisteredUser bob = registerUser("bob");

    LoginRequest aliceLoginRequest = new LoginRequest(alice.username(), DEFAULT_PASSWORD);
    LoginRequest bobLoginRequest = new LoginRequest(bob.username(), DEFAULT_PASSWORD);

    // when
    AuthResponse aliceLogin = authClient.login(aliceLoginRequest)
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .as(AuthResponse.class);
    AuthResponse bobLogin = authClient.login(bobLoginRequest)
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .as(AuthResponse.class);

    String aliceToken = aliceLogin.token();
    String bobToken = bobLogin.token();

    FundAccountRequest fundRequest = new FundAccountRequest(E2E_FUND_AMOUNT);
    PaymentRequest paymentRequest = new PaymentRequest(
        alice.accountId(),
        bob.accountId(),
        E2E_PAYMENT_AMOUNT);

    Response fundingResponse = accountClient.fund(
        alice.accountId(),
        fundRequest,
        aliceToken);

    Response paymentResponse = transactionClient.makePayment(
        paymentRequest,
        aliceToken);

    Response aliceBalanceResponse = accountClient.getBalance(alice.accountId(), aliceToken);
    Response bobBalanceResponse = accountClient.getBalance(bob.accountId(), bobToken);

    Response transactionsResponse = transactionClient.getTransactions(alice.accountId(), aliceToken);

    // then
    fundingResponse.then()
        .statusCode(HttpStatus.SC_OK);

    paymentResponse.then()
        .statusCode(HttpStatus.SC_OK)
        .body("status", equalTo(TRANSACTION_STATUS_SUCCESS));

    BigDecimal aliceBalance = extractBalance(aliceBalanceResponse);
    BigDecimal bobBalance = extractBalance(bobBalanceResponse);

    BigDecimal expectedBalance = E2E_FUND_AMOUNT.subtract(E2E_PAYMENT_AMOUNT);

    assertThat(aliceBalance).isEqualByComparingTo(expectedBalance);
    assertThat(bobBalance).isEqualByComparingTo(E2E_PAYMENT_AMOUNT);

    transactionsResponse.then()
        .statusCode(HttpStatus.SC_OK)
        .body("$", hasSize(1));
  }

  private BigDecimal extractBalance(io.restassured.response.Response response) {
    BalanceResponse balanceResponse = response
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .as(BalanceResponse.class);

    return balanceResponse.balance();
  }
}
