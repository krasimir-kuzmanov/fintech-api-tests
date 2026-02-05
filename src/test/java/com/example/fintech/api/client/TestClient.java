package com.example.fintech.api.client;

import static io.restassured.RestAssured.given;

import com.example.fintech.api.model.CreateTestUserRequest;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;

public class TestClient {

  private static final String RESET_ENDPOINT = "/test/reset";
  private static final String TEST_USERS_ENDPOINT = "/test/users";

  public void reset() {
    given()
        .when()
        .post(RESET_ENDPOINT)
        .then()
        .statusCode(HttpStatus.SC_OK);
  }

  public void createTestUser(String username, String password, boolean overwrite) {
    CreateTestUserRequest request =
        new CreateTestUserRequest(username, password, overwrite);

    given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post(TEST_USERS_ENDPOINT)
        .then()
        .statusCode(HttpStatus.SC_OK);
  }
}
