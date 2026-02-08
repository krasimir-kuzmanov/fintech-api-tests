package com.example.fintech.api.tests.account;

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
import java.util.Map;

import static com.example.fintech.api.testdata.HttpConstants.AUTH_HEADER;
import static com.example.fintech.api.testdata.HttpConstants.BEARER_PREFIX;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class AccountNegativeTests extends BaseTest {

  private static final String NON_NUMERIC_AMOUNT = "abc";
  private static final String NUMERIC_STRING_AMOUNT = "100.00";
  private static final BigDecimal ZERO_AMOUNT = BigDecimal.ZERO;

  private final AuthClient authClient = new AuthClient();

  @Test
  void shouldReturn400WhenFundingWithZeroAmount() {
    // given
    RegisterRequest user = TestDataFactory.userWithPrefix("account");
    String accountId = authClient.register(user)
        .then()
        .extract()
        .path("id");

    AuthResponse login = authClient.login(new LoginRequest(user.username(), TestConstants.DEFAULT_PASSWORD))
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .as(AuthResponse.class);

    FundAccountRequest request = new FundAccountRequest(ZERO_AMOUNT);

    // when
    Response response = given()
        .contentType(ContentType.JSON)
        .header(AUTH_HEADER, BEARER_PREFIX + login.token())
        .pathParam("accountId", accountId)
        .body(request)
        .when()
        .post(TestEndpoints.ACCOUNT_FUND);

    // then
    response.then()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body("error", equalTo(TestConstants.ERROR_CODE_INVALID_AMOUNT));
  }

  @Test
  void shouldReturn400WhenFundingWithNonNumericAmount() {
    // given
    RegisterRequest user = TestDataFactory.userWithPrefix("account");
    String accountId = authClient.register(user)
        .then()
        .extract()
        .path("id");

    AuthResponse login = authClient.login(new LoginRequest(user.username(), TestConstants.DEFAULT_PASSWORD))
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .as(AuthResponse.class);

    // when
    Response response = given()
        .contentType(ContentType.JSON)
        .header(AUTH_HEADER, BEARER_PREFIX + login.token())
        .pathParam("accountId", accountId)
        .body(Map.of("amount", NON_NUMERIC_AMOUNT))
        .when()
        .post(TestEndpoints.ACCOUNT_FUND);

    // then
    response.then()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body("error", equalTo(TestConstants.ERROR_CODE_INVALID_AMOUNT));
  }

  @Test
  void shouldReturn400WhenFundingWithStringNumericAmount() {
    // given
    RegisterRequest user = TestDataFactory.userWithPrefix("account");
    String accountId = authClient.register(user)
        .then()
        .extract()
        .path("id");

    AuthResponse login = authClient.login(new LoginRequest(user.username(), TestConstants.DEFAULT_PASSWORD))
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .as(AuthResponse.class);

    // when
    Response response = given()
        .contentType(ContentType.JSON)
        .header(AUTH_HEADER, BEARER_PREFIX + login.token())
        .pathParam("accountId", accountId)
        .body(Map.of("amount", NUMERIC_STRING_AMOUNT))
        .when()
        .post(TestEndpoints.ACCOUNT_FUND);

    // then
    response.then()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body("error", equalTo(TestConstants.ERROR_CODE_INVALID_AMOUNT));
  }
}
