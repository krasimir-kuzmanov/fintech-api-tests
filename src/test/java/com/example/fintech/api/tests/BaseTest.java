package com.example.fintech.api.tests;

import com.example.fintech.api.client.TestClient;
import com.example.fintech.api.config.RestAssuredConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTest {

  private final TestClient testClient = new TestClient();

  @BeforeAll
  static void setup() {
    RestAssuredConfig.init();
  }

  @BeforeEach
  void resetState() {
    testClient.reset();
  }

}