package com.example.fintech.api.tests;

import com.example.fintech.api.model.RegisterRequest;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class RegisterTests extends BaseTest {

  @Test
  void should_RegisterUser_When_RequestIsValid() {
    RegisterRequest request =
        new RegisterRequest("john_doe", "password123");

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/auth/register")
        .then()
        .statusCode(HttpStatus.SC_OK)
        .body("username", equalTo("john_doe"))
        .body("id", notNullValue());
  }

  @Test
  void should_NotAllowDuplicateRegistration_When_UsernameExists() {
    RegisterRequest request =
        new RegisterRequest("john", "password");

    testClient.createTestUser("john", "password", true);

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/auth/register")
        .then()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body("error", equalTo("USER_ALREADY_EXISTS"));
  }
}
