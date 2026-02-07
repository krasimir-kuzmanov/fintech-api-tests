package com.example.fintech.api.tests;

import com.example.fintech.api.client.AccountClient;
import com.example.fintech.api.client.AuthClient;
import com.example.fintech.api.client.TestClient;
import com.example.fintech.api.client.TransactionClient;
import com.example.fintech.api.model.LoginRequest;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.example.fintech.api.tests.TestConstants.TRANSACTION_STATUS_SUCCESS;
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

    Response aliceLogin = authClient.login(new LoginRequest(alice.username(), "password"));
    Response bobLogin = authClient.login(new LoginRequest(bob.username(), "password"));

    String aliceToken = aliceLogin.then().statusCode(HttpStatus.SC_OK).extract().path("token");
    String bobToken = bobLogin.then().statusCode(HttpStatus.SC_OK).extract().path("token");

    accountClient.fundAuthenticated(alice.accountId(), new BigDecimal("100.00"), aliceToken)
        .then()
        .statusCode(HttpStatus.SC_OK);

    transactionClient.makePaymentAuthenticated(
            alice.accountId(),
            bob.accountId(),
            new BigDecimal("40.00"),
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

  private BigDecimal extractBalance(Response response) {
    Number balanceValue = response
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .path("balance");

    return new BigDecimal(balanceValue.toString());
  }
}
