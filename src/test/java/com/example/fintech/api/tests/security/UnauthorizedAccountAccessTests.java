package com.example.fintech.api.tests.security;

import com.example.fintech.api.client.AuthClient;
import com.example.fintech.api.model.request.FundAccountRequest;
import com.example.fintech.api.model.request.RegisterRequest;
import com.example.fintech.api.testdata.TestDataFactory;
import com.example.fintech.api.testdata.TestEndpoints;
import com.example.fintech.api.tests.base.BaseTest;
import io.restassured.response.Response;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;

class UnauthorizedAccountAccessTests extends BaseTest {

  private static final BigDecimal ACCOUNT_FUND_AMOUNT = new BigDecimal("100.00");

  private final AuthClient authClient = new AuthClient();

  @Test
  void shouldReturn401WhenFundingAccountWithoutToken() {
    // given
    RegisterRequest user = TestDataFactory.userWithPrefix("unauth");
    String accountId = authClient.register(user)
        .then()
        .extract()
        .path("id");

    FundAccountRequest request = new FundAccountRequest(ACCOUNT_FUND_AMOUNT);

    // when
    Response response = given()
        .contentType(ContentType.JSON)
        .pathParam("accountId", accountId)
        .body(request)
        .when()
        .post(TestEndpoints.ACCOUNT_FUND);

    // then
    response.then()
        .statusCode(HttpStatus.SC_UNAUTHORIZED);
  }

  @Test
  void shouldReturn401WhenGettingBalanceWithoutToken() {
    // given
    RegisterRequest user = TestDataFactory.userWithPrefix("unauth");
    String accountId = authClient.register(user)
        .then()
        .extract()
        .path("id");

    // when
    Response response = given()
        .pathParam("accountId", accountId)
        .when()
        .get(TestEndpoints.ACCOUNT_BALANCE);

    // then
    response.then()
        .statusCode(HttpStatus.SC_UNAUTHORIZED);
  }
}
