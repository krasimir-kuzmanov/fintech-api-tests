package com.example.fintech.api.tests;

import com.example.fintech.api.model.CreateTestUserRequest;
import com.example.fintech.api.model.RegisterRequest;
import io.restassured.http.ContentType;
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
        .statusCode(200)
        .body("username", equalTo("john_doe"))
        .body("id", notNullValue());
  }

  @Test
  void should_NotAllowDuplicateRegistration_When_UsernameExists() {
    RegisterRequest request =
        new RegisterRequest("john", "password");

    given()
        .contentType(ContentType.JSON)
        .body(new CreateTestUserRequest("john", "password", true))
        .when()
        .post("/test/users")
        .then()
        .statusCode(200);

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/auth/register")
        .then()
        .statusCode(400)
        .body("error", equalTo("USER_ALREADY_EXISTS"));
  }
}
