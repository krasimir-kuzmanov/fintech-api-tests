package com.example.fintech.api.tests.transaction;

import com.example.fintech.api.client.AccountClient;
import com.example.fintech.api.client.TransactionClient;
import com.example.fintech.api.model.request.FundAccountRequest;
import com.example.fintech.api.model.request.PaymentRequest;
import com.example.fintech.api.model.request.RegisterRequest;
import com.example.fintech.api.model.response.BalanceResponse;
import com.example.fintech.api.testdata.TestDataFactory;
import com.example.fintech.api.testdata.TestEndpoints;
import com.example.fintech.api.tests.base.BaseTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static com.example.fintech.api.testdata.HttpConstants.AUTH_HEADER;
import static com.example.fintech.api.testdata.HttpConstants.BEARER_PREFIX;
import static com.example.fintech.api.testdata.TestConstants.ERROR_CODE_INVALID_AMOUNT;
import static com.example.fintech.api.testdata.TestConstants.ERROR_CODE_SAME_ACCOUNT_TRANSFER;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

class TransactionNegativeTests extends BaseTest {

  private static final String NON_NUMERIC_AMOUNT = "abc";
  private static final BigDecimal FUND_AMOUNT = new BigDecimal("100.00");
  private static final BigDecimal SAME_ACCOUNT_PAYMENT_AMOUNT = new BigDecimal("10.00");
  private static final BigDecimal ZERO_AMOUNT = BigDecimal.ZERO;

  private final AccountClient accountClient = new AccountClient();
  private final TransactionClient transactionClient = new TransactionClient();

  @Test
  void shouldReturn400WhenPaymentAmountIsZero() {
    // given
    RegisterRequest user = TestDataFactory.userWithPrefix("payer");
    String fromAccountId = authClient.register(user)
        .then()
        .extract()
        .path("id");

    String token = loginAndGetToken(user.username());

    // when
    Response response = given()
        .contentType(ContentType.JSON)
        .header(AUTH_HEADER, BEARER_PREFIX + token)
        .body(Map.of(
            "fromAccountId", fromAccountId,
            "toAccountId", fromAccountId,
            "amount", ZERO_AMOUNT.toPlainString()
        ))
        .when()
        .post(TestEndpoints.TRANSACTION_PAYMENT);

    // then
    response.then()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body("error", equalTo(ERROR_CODE_INVALID_AMOUNT));
  }

  @Test
  void shouldReturn400WhenPaymentAmountIsNonNumeric() {
    // given
    RegisterRequest user = TestDataFactory.userWithPrefix("payer");
    String fromAccountId = authClient.register(user)
        .then()
        .extract()
        .path("id");

    String token = loginAndGetToken(user.username());

    // when
    Response response = given()
        .contentType(ContentType.JSON)
        .header(AUTH_HEADER, BEARER_PREFIX + token)
        .body(Map.of(
            "fromAccountId", fromAccountId,
            "toAccountId", fromAccountId,
            "amount", NON_NUMERIC_AMOUNT
        ))
        .when()
        .post(TestEndpoints.TRANSACTION_PAYMENT);

    // then
    response.then()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body("error", equalTo(ERROR_CODE_INVALID_AMOUNT));
  }

  @Test
  void shouldReturn400WhenFromAndToAccountsAreTheSame() {
    // given
    RegisterRequest user = TestDataFactory.userWithPrefix("payer");
    String accountId = authClient.register(user)
        .then()
        .extract()
        .path("id");

    String token = loginAndGetToken(user.username());

    // when
    Response response = transactionClient.makePayment(
        new PaymentRequest(accountId, accountId, SAME_ACCOUNT_PAYMENT_AMOUNT),
        token);

    // then
    response.then()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body("error", equalTo(ERROR_CODE_SAME_ACCOUNT_TRANSFER));
  }

  @Test
  void shouldNotChangeBalanceOrHistoryWhenSameAccountTransferIsRejected() {
    // given
    RegisterRequest user = TestDataFactory.userWithPrefix("payer");
    String accountId = authClient.register(user)
        .then()
        .extract()
        .path("id");
    String token = loginAndGetToken(user.username());

    accountClient.fund(accountId, new FundAccountRequest(FUND_AMOUNT), token)
        .then()
        .statusCode(HttpStatus.SC_OK);

    // when
    transactionClient.makePayment(
            new PaymentRequest(accountId, accountId, SAME_ACCOUNT_PAYMENT_AMOUNT),
            token)
        .then()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body("error", equalTo(ERROR_CODE_SAME_ACCOUNT_TRANSFER));

    BalanceResponse balanceAfter = accountClient.getBalance(accountId, token)
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .as(BalanceResponse.class);

    Response historyAfter = transactionClient.getTransactions(accountId, token);

    // then
    assertThat(balanceAfter.balance()).isEqualByComparingTo(FUND_AMOUNT);
    historyAfter.then()
        .statusCode(HttpStatus.SC_OK)
        .body("size()", equalTo(0));
  }
}
