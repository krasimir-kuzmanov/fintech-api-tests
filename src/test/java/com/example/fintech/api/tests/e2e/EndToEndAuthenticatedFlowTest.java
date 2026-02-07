package com.example.fintech.api.tests.e2e;

import com.example.fintech.api.client.AccountClient;
import com.example.fintech.api.client.AuthClient;
import com.example.fintech.api.client.TestClient;
import com.example.fintech.api.client.TransactionClient;
import com.example.fintech.api.model.request.FundAccountRequest;
import com.example.fintech.api.model.request.LoginRequest;
import com.example.fintech.api.model.request.PaymentRequest;
import com.example.fintech.api.model.response.AuthResponse;
import com.example.fintech.api.model.response.BalanceResponse;
import com.example.fintech.api.tests.BaseTest;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.example.fintech.api.testdata.TestConstants.TRANSACTION_STATUS_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

class EndToEndAuthenticatedFlowTest extends BaseTest {

  private final TestClient testClient = new TestClient();
  private final AuthClient authClient = new AuthClient();
  private final AccountClient accountClient = new AccountClient();
  private final TransactionClient transactionClient = new TransactionClient();

  @Test
  void should_CompleteAuthenticatedFlow_When_PaymentIsValid() {
    testClient.reset();

    RegisteredUser alice = registerUser("alice");
    RegisteredUser bob = registerUser("bob");

    AuthResponse aliceLogin = authClient.login(new LoginRequest(alice.username(), "password"))
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .as(AuthResponse.class);
    AuthResponse bobLogin = authClient.login(new LoginRequest(bob.username(), "password"))
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .as(AuthResponse.class);

    String aliceToken = aliceLogin.token();
    String bobToken = bobLogin.token();

    accountClient.fundAuthenticated(
            alice.accountId(),
            new FundAccountRequest(new BigDecimal("100.00")),
            aliceToken)
        .then()
        .statusCode(HttpStatus.SC_OK);

    transactionClient.makePaymentAuthenticated(
            new PaymentRequest(
                alice.accountId(),
                bob.accountId(),
                new BigDecimal("40.00")),
            aliceToken)
        .then()
        .statusCode(HttpStatus.SC_OK)
        .body("status", equalTo(TRANSACTION_STATUS_SUCCESS));

    BigDecimal aliceBalance = extractBalance(
        accountClient.getBalanceAuthenticated(alice.accountId(), aliceToken));
    BigDecimal bobBalance = extractBalance(
        accountClient.getBalanceAuthenticated(bob.accountId(), bobToken));

    assertThat(aliceBalance).isEqualByComparingTo(new BigDecimal("60.00"));
    assertThat(bobBalance).isEqualByComparingTo(new BigDecimal("40.00"));

    transactionClient.getTransactionsAuthenticated(alice.accountId(), aliceToken)
        .then()
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