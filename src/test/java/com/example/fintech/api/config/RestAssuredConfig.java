package com.example.fintech.api.config;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;

import java.io.InputStream;
import java.util.Properties;

public final class RestAssuredConfig {

  private static final String CONFIG_FILE = "application.properties";

  private static final String CONFIG_BASE_URL = "base.url";
  private static final String CONFIG_HTTP_TIMEOUT_MS = "http.timeout.ms";

  private static final int DEFAULT_TIMEOUT_MS = 10_000;

  private static boolean initialized = false;

  private RestAssuredConfig() {
    // utility class
  }

  public static synchronized void init() {
    if (initialized) {
      return;
    }

    Properties properties = loadProperties();

    RestAssured.baseURI = resolveBaseUrl(properties);
    applyTimeouts(properties);
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

    initialized = true;
  }

  private static Properties loadProperties() {
    Properties properties = new Properties();

    try (InputStream input =
             RestAssuredConfig.class
                 .getClassLoader()
                 .getResourceAsStream(CONFIG_FILE)) {
      if (input == null) {
        throw new RuntimeException("Could not find " + CONFIG_FILE + " on classpath");
      }
      properties.load(input);
    } catch (Exception e) {
      throw new RuntimeException("Failed to load test configuration", e);
    }

    return properties;
  }

  private static String resolveBaseUrl(Properties properties) {
    String baseUrl = properties.getProperty(CONFIG_BASE_URL);
    if (baseUrl == null || baseUrl.isBlank()) {
      throw new RuntimeException("Property '" + CONFIG_BASE_URL + "' is not defined");
    }

    return baseUrl;
  }

  private static void applyTimeouts(Properties properties) {
    int timeoutMs = parseTimeoutMs(properties.getProperty(CONFIG_HTTP_TIMEOUT_MS));

    RestAssured.config =
        io.restassured.config.RestAssuredConfig.config()
            .httpClient(
                HttpClientConfig.httpClientConfig()
                    .setParam("http.connection.timeout", timeoutMs)
                    .setParam("http.socket.timeout", timeoutMs));
  }

  private static int parseTimeoutMs(String value) {
    if (value == null || value.isBlank()) {
      return DEFAULT_TIMEOUT_MS;
    }
    try {
      return Integer.parseInt(value.trim());
    } catch (NumberFormatException e) {
      return DEFAULT_TIMEOUT_MS;
    }
  }
}
