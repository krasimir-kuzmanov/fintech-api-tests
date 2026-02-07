package com.example.fintech.api.config;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;

public final class RestAssuredConfig {

  private static boolean initialized = false;

  private RestAssuredConfig() {
    // utility class
  }

  public static synchronized void init() {
    if (initialized) {
      return;
    }

    RestAssured.baseURI = TestConfig.baseUrl();
    applyTimeouts();
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

    initialized = true;
  }

  private static void applyTimeouts() {
    int timeoutMs = TestConfig.httpTimeoutMs();

    RestAssured.config =
        io.restassured.config.RestAssuredConfig.config()
            .httpClient(
                HttpClientConfig.httpClientConfig()
                    .setParam("http.connection.timeout", timeoutMs)
                    .setParam("http.socket.timeout", timeoutMs));
  }
}