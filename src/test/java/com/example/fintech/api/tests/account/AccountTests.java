package com.example.fintech.api.tests.account;

import com.example.fintech.api.client.AccountClient;
import com.example.fintech.api.model.request.FundAccountRequest;
import com.example.fintech.api.model.response.BalanceResponse;
import com.example.fintech.api.tests.base.BaseTest;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import static com.example.fintech.api.testdata.TestConstants.ACCOUNT_BALANCE_AFTER_FUND;
import static com.example.fintech.api.testdata.TestConstants.ACCOUNT_BALANCE_AFTER_TOPUP;
import static com.example.fintech.api.testdata.TestConstants.ACCOUNT_BALANCE_FUND_AMOUNT;
import static com.example.fintech.api.testdata.TestConstants.ACCOUNT_FUND_AMOUNT;
import static com.example.fintech.api.testdata.TestConstants.ACCOUNT_INVALID_FUND_AMOUNT;
import static com.example.fintech.api.testdata.TestConstants.ERROR_CODE_INVALID_AMOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

class AccountTests extends BaseTest {

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
        .isEqualByComparingTo(ACCOUNT_BALANCE_AFTER_FUND);
  }

  @Test
  void shouldReturnBalanceForExistingAccount() {
    // given
    String accountId = registerAndGetAccountId("account_user");
    FundAccountRequest request = new FundAccountRequest(ACCOUNT_BALANCE_FUND_AMOUNT);

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
        .isEqualByComparingTo(ACCOUNT_BALANCE_AFTER_TOPUP);
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
