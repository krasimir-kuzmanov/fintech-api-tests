package com.example.fintech.api.tests.account;

import com.example.fintech.api.client.AccountClient;
import com.example.fintech.api.model.request.FundAccountRequest;
import com.example.fintech.api.model.response.BalanceResponse;
import com.example.fintech.api.tests.BaseTest;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.example.fintech.api.testdata.TestConstants.ERROR_CODE_INVALID_AMOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

class AccountTests extends BaseTest {

  private final AccountClient accountClient = new AccountClient();

  @Test
  void should_FundAccount_When_RequestIsValid() {
    String accountId = registerAndGetAccountId("account_user");

    Response response = accountClient.fund(accountId, new FundAccountRequest(new BigDecimal("100.50")));

    BalanceResponse balanceResponse = response
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .as(BalanceResponse.class);

    assertThat(balanceResponse.balance())
        .isEqualByComparingTo(new BigDecimal("100.50"));
  }

  @Test
  void should_ReturnBalance_When_AccountHasFunds() {
    String accountId = registerAndGetAccountId("account_user");

    accountClient.fund(accountId, new FundAccountRequest(new BigDecimal("75.25")))
        .then()
        .statusCode(HttpStatus.SC_OK);

    Response response = accountClient.getBalance(accountId);

    BalanceResponse balanceResponse = response
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .as(BalanceResponse.class);

    assertThat(balanceResponse.balance())
        .isEqualByComparingTo(new BigDecimal("75.25"));
  }

  @Test
  void should_RejectFunding_When_AmountIsNegative() {
    String accountId = registerAndGetAccountId("account_user");

    accountClient.fund(accountId, new FundAccountRequest(new BigDecimal("-10")))
        .then()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body("error", equalTo(ERROR_CODE_INVALID_AMOUNT));
  }

}