package com.example.fintech.api.tests;

import com.example.fintech.api.client.AccountClient;
import com.example.fintech.api.client.AuthClient;
import com.example.fintech.api.model.RegisterRequest;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static com.example.fintech.api.tests.TestConstants.ERROR_CODE_INVALID_AMOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

class AccountTests extends BaseTest {

  private final AuthClient authClient = new AuthClient();
  private final AccountClient accountClient = new AccountClient();

  @Test
  void should_FundAccount_When_RequestIsValid() {
    String accountId = registerAndGetAccountId();

    Response response = accountClient.fund(accountId, new BigDecimal("100.50"));

    Float balanceValue = response
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .path("balance");

    assertThat(BigDecimal.valueOf(balanceValue))
        .isEqualByComparingTo(new BigDecimal("100.50"));
  }

  @Test
  void should_ReturnBalance_When_AccountHasFunds() {
    String accountId = registerAndGetAccountId();

    accountClient.fund(accountId, new BigDecimal("75.25"))
        .then()
        .statusCode(HttpStatus.SC_OK);

    Response response = accountClient.getBalance(accountId);

    Float balanceValue = response
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .path("balance");

    assertThat(BigDecimal.valueOf(balanceValue))
        .isEqualByComparingTo(new BigDecimal("75.25"));
  }

  @Test
  void should_RejectFunding_When_AmountIsNegative() {
    String accountId = registerAndGetAccountId();

    accountClient.fund(accountId, new BigDecimal("-10"))
        .then()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body("error", equalTo(ERROR_CODE_INVALID_AMOUNT));
  }

  private String registerAndGetAccountId() {
    String username = "account_user_" + UUID.randomUUID();

    Response response = authClient.register(
        new RegisterRequest(username, "password"));

    return response
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .path("id");
  }
}
