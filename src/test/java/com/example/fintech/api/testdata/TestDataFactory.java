package com.example.fintech.api.testdata;

import com.example.fintech.api.model.request.RegisterRequest;

import java.util.UUID;

import static com.example.fintech.api.testdata.TestConstants.DEFAULT_PASSWORD;

public final class TestDataFactory {

  public static RegisterRequest userWithPrefix(String prefix) {
    String username = prefix + "_" + UUID.randomUUID();
    return new RegisterRequest(username, DEFAULT_PASSWORD);
  }

}
