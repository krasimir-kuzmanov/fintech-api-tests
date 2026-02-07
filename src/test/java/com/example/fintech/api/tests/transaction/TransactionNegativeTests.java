package com.example.fintech.api.tests.transaction;

import com.example.fintech.api.client.AuthClient;
import com.example.fintech.api.client.TestSupportClient;
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

class TransactionNegativeTests extends BaseTest {

  private static final String NON_NUMERIC_AMOUNT = "abc";
  private static final BigDecimal ZERO_AMOUNT = BigDecimal.ZERO;

  private final TestSupportClient testSupportClient = new TestSupportClient();
  private final AuthClient authClient = new AuthClient();

  @Test
  void shouldReturn400WhenPaymentAmountIsZero() {
    // given
    testSupportClient.reset();

    RegisterRequest user = TestDataFactory.userWithPrefix("payer");
    String fromAccountId = authClient.register(user)
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
        .body("error", equalTo(TestConstants.ERROR_CODE_INVALID_AMOUNT));
  }

  @Test
  void shouldReturn400WhenPaymentAmountIsNonNumeric() {
    // given
    testSupportClient.reset();

    RegisterRequest user = TestDataFactory.userWithPrefix("payer");
    String fromAccountId = authClient.register(user)
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
        .body("error", equalTo(TestConstants.ERROR_CODE_INVALID_AMOUNT));
  }
}
