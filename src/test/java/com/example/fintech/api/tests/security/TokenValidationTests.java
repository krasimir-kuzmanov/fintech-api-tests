package com.example.fintech.api.tests.security;

import com.example.fintech.api.client.AuthClient;
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

import static com.example.fintech.api.testdata.HttpConstants.AUTH_HEADER;
import static com.example.fintech.api.testdata.HttpConstants.BEARER_PREFIX;
import static io.restassured.RestAssured.given;

class TokenValidationTests extends BaseTest {

  private static final String INVALID_TOKEN = "invalid-token";
  private static final String GARBAGE_AUTH_VALUE = "something-not-bearer";
  private static final String BLANK_BEARER_VALUE = "Bearer   ";

  private final AuthClient authClient = new AuthClient();

  @Test
  void shouldReturn401WhenTokenIsInvalid() {
    // given
    RegisterRequest user = TestDataFactory.userWithPrefix("token");
    String accountId = authClient.register(user)
        .then()
        .extract()
        .path("id");

    // when
    Response response = given()
        .contentType(ContentType.JSON)
        .header(AUTH_HEADER, BEARER_PREFIX + INVALID_TOKEN)
        .pathParam("accountId", accountId)
        .when()
        .get(TestEndpoints.ACCOUNT_BALANCE);

    // then
    response.then()
        .statusCode(HttpStatus.SC_UNAUTHORIZED);
  }

  @Test
  void shouldReturn401WhenBearerPrefixIsMissing() {
    // given
    RegisterRequest user = TestDataFactory.userWithPrefix("token");
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
        .header(AUTH_HEADER, login.token())
        .pathParam("accountId", accountId)
        .when()
        .get(TestEndpoints.ACCOUNT_BALANCE);

    // then
    response.then()
        .statusCode(HttpStatus.SC_UNAUTHORIZED);
  }

  @Test
  void shouldReturn401WhenAuthorizationHeaderIsGarbage() {
    // given
    RegisterRequest user = TestDataFactory.userWithPrefix("token");
    String accountId = authClient.register(user)
        .then()
        .extract()
        .path("id");

    // when
    Response response = given()
        .contentType(ContentType.JSON)
        .header(AUTH_HEADER, GARBAGE_AUTH_VALUE)
        .pathParam("accountId", accountId)
        .when()
        .get(TestEndpoints.ACCOUNT_BALANCE);

    // then
    response.then()
        .statusCode(HttpStatus.SC_UNAUTHORIZED);
  }

  @Test
  void shouldReturn401WhenBearerTokenValueIsBlank() {
    // given
    RegisterRequest user = TestDataFactory.userWithPrefix("token");
    String accountId = authClient.register(user)
        .then()
        .extract()
        .path("id");

    // when
    Response response = given()
        .contentType(ContentType.JSON)
        .header(AUTH_HEADER, BLANK_BEARER_VALUE)
        .pathParam("accountId", accountId)
        .when()
        .get(TestEndpoints.ACCOUNT_BALANCE);

    // then
    response.then()
        .statusCode(HttpStatus.SC_UNAUTHORIZED);
  }
}
