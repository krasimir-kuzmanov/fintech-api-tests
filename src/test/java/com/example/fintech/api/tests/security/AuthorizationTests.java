package com.example.fintech.api.tests.security;

import com.example.fintech.api.client.AuthClient;
import com.example.fintech.api.model.request.FundAccountRequest;
import com.example.fintech.api.model.request.LoginRequest;
import com.example.fintech.api.model.request.RegisterRequest;
import com.example.fintech.api.model.response.AuthResponse;
import com.example.fintech.api.testdata.TestConstants;
import com.example.fintech.api.testdata.TestDataFactory;
import com.example.fintech.api.testdata.TestEndpoints;
import com.example.fintech.api.tests.base.BaseTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.example.fintech.api.testdata.HttpConstants.AUTH_HEADER;
import static com.example.fintech.api.testdata.HttpConstants.BEARER_PREFIX;
import static io.restassured.RestAssured.given;

class AuthorizationTests extends BaseTest {

  private static final BigDecimal FUND_AMOUNT = new BigDecimal("10.00");

  private final AuthClient authClient = new AuthClient();

  @Test
  void shouldReturn403WhenFundingAnotherUsersAccount() {
    // given
    RegisterRequest alice = TestDataFactory.userWithPrefix("alice");
    RegisterRequest bob = TestDataFactory.userWithPrefix("bob");

    authClient.register(alice);
    Response bobRegister = authClient.register(bob);

    String bobAccountId = bobRegister
        .then()
        .extract()
        .path("id");

    AuthResponse aliceLogin = authClient.login(new LoginRequest(alice.username(), TestConstants.DEFAULT_PASSWORD))
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .as(AuthResponse.class);

    FundAccountRequest request = new FundAccountRequest(FUND_AMOUNT);

    // when
    Response response = given()
        .contentType(ContentType.JSON)
        .header(AUTH_HEADER, BEARER_PREFIX + aliceLogin.token())
        .pathParam("accountId", bobAccountId)
        .body(request)
        .when()
        .post(TestEndpoints.ACCOUNT_FUND);

    // then
    response.then()
        .statusCode(HttpStatus.SC_FORBIDDEN);
  }

  @Test
  void shouldReturn403WhenGettingAnotherUsersTransactions() {
    // given
    RegisterRequest alice = TestDataFactory.userWithPrefix("alice");
    RegisterRequest bob = TestDataFactory.userWithPrefix("bob");

    authClient.register(alice);
    Response bobRegister = authClient.register(bob);

    String bobAccountId = bobRegister
        .then()
        .extract()
        .path("id");

    AuthResponse aliceLogin = authClient.login(new LoginRequest(alice.username(), TestConstants.DEFAULT_PASSWORD))
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .as(AuthResponse.class);

    // when
    Response response = given()
        .header(AUTH_HEADER, BEARER_PREFIX + aliceLogin.token())
        .pathParam("accountId", bobAccountId)
        .when()
        .get(TestEndpoints.TRANSACTION_HISTORY);

    // then
    response.then()
        .statusCode(HttpStatus.SC_FORBIDDEN);
  }
}
