package com.example.fintech.api.client;

import com.example.fintech.api.testdata.TestEndpoints;
import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class TestSupportClient {

  public void reset() {
    given()
        .when()
        .post(TestEndpoints.TEST_RESET)
        .then()
        .statusCode(HttpStatus.SC_OK);
  }
}
