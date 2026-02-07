package com.example.fintech.api.tests.security;

import com.example.fintech.api.client.AccountClient;
import com.example.fintech.api.client.AuthClient;
import com.example.fintech.api.client.TestSupportClient;
import com.example.fintech.api.model.request.FundAccountRequest;
import com.example.fintech.api.model.request.RegisterRequest;
import com.example.fintech.api.testdata.TestDataFactory;
import com.example.fintech.api.tests.base.BaseTest;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class UnauthorizedAccountAccessTests extends BaseTest {

  private static final BigDecimal ACCOUNT_FUND_AMOUNT = new BigDecimal("100.00");

  private final AccountClient accountClient = new AccountClient();
  private final AuthClient authClient = new AuthClient();
  private final TestSupportClient testSupportClient = new TestSupportClient();

  @Test
  void shouldReturn401WhenFundingAccountWithoutToken() {
    // given
    testSupportClient.reset();

    RegisterRequest user = TestDataFactory.userWithPrefix("unauth");
    String accountId = authClient.register(user)
        .then()
        .extract()
        .path("id");

    FundAccountRequest request = new FundAccountRequest(ACCOUNT_FUND_AMOUNT);

    // when
    Response response = accountClient.fund(accountId, request);

    // then
    response.then()
        .statusCode(HttpStatus.SC_UNAUTHORIZED);
  }

  @Test
  void shouldReturn401WhenGettingBalanceWithoutToken() {
    // given
    testSupportClient.reset();

    RegisterRequest user = TestDataFactory.userWithPrefix("unauth");
    String accountId = authClient.register(user)
        .then()
        .extract()
        .path("id");

    // when
    Response response = accountClient.getBalance(accountId);

    // then
    response.then()
        .statusCode(HttpStatus.SC_UNAUTHORIZED);
  }
}
