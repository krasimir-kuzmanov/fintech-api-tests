package com.example.fintech.api.config;

import java.io.InputStream;
import java.util.Properties;

public final class TestConfig {

  private static final String CONFIG_FILE = "application.properties";
  private static final String CONFIG_BASE_URL = "base.url";
  private static final String CONFIG_HTTP_TIMEOUT_MS = "http.timeout.ms";

  private static final int DEFAULT_TIMEOUT_MS = 10_000;

  private static final Properties PROPERTIES = loadProperties();

  private TestConfig() {
    // utility class
  }

  public static String baseUrl() {
    String baseUrl = PROPERTIES.getProperty(CONFIG_BASE_URL);

    if (baseUrl == null || baseUrl.isBlank()) {
      throw new RuntimeException("Property '" + CONFIG_BASE_URL + "' is not defined");
    }

    return baseUrl;
  }

  public static int httpTimeoutMs() {
    String value = PROPERTIES.getProperty(CONFIG_HTTP_TIMEOUT_MS);

    if (value == null || value.isBlank()) {
      return DEFAULT_TIMEOUT_MS;
    }

    try {
      return Integer.parseInt(value.trim());
    } catch (NumberFormatException e) {
      return DEFAULT_TIMEOUT_MS;
    }
  }

  private static Properties loadProperties() {
    Properties properties = new Properties();

    try (InputStream input =
             TestConfig.class
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
}