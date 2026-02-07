package com.example.fintech.api.tests.account;

import com.example.fintech.api.client.AccountClient;
import com.example.fintech.api.model.request.FundAccountRequest;
import com.example.fintech.api.model.response.BalanceResponse;
import com.example.fintech.api.tests.base.BaseTest;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.example.fintech.api.testdata.TestConstants.ERROR_CODE_INVALID_AMOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

class AccountTests extends BaseTest {

  private static final BigDecimal ACCOUNT_FUND_AMOUNT = new BigDecimal("100.50");
  private static final BigDecimal ACCOUNT_INVALID_FUND_AMOUNT = new BigDecimal("-10.00");

  private final AccountClient accountClient = new AccountClient();

  @Test
  void shouldFundAccountSuccessfully() {
    // given
    String accountId = registerAndGetAccountId("account_user");
    FundAccountRequest request = new FundAccountRequest(ACCOUNT_FUND_AMOUNT);

    // when
    Response response = accountClient.fund(accountId, request);

    // then
    BalanceResponse balanceResponse = response
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .as(BalanceResponse.class);

    assertThat(balanceResponse.balance())
        .isEqualByComparingTo(ACCOUNT_FUND_AMOUNT);
  }

  @Test
  void shouldReturnBalanceForExistingAccount() {
    // given
    String accountId = registerAndGetAccountId("account_user");
    FundAccountRequest request = new FundAccountRequest(new BigDecimal("75.25"));

    accountClient.fund(accountId, request)
        .then()
        .statusCode(HttpStatus.SC_OK);

    // when
    Response response = accountClient.getBalance(accountId);

    // then
    BalanceResponse balanceResponse = response
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .as(BalanceResponse.class);

    assertThat(balanceResponse.balance())
        .isEqualByComparingTo(new BigDecimal("75.25"));
  }

  @Test
  void shouldRejectFundingWithNegativeAmount() {
    // given
    String accountId = registerAndGetAccountId("account_user");
    FundAccountRequest request = new FundAccountRequest(ACCOUNT_INVALID_FUND_AMOUNT);

    // when
    Response response = accountClient.fund(accountId, request);

    // then
    response.then()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body("error", equalTo(ERROR_CODE_INVALID_AMOUNT));
  }

}
