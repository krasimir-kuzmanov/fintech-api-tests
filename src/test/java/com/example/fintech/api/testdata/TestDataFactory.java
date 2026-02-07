package com.example.fintech.api.testdata;

import com.example.fintech.api.model.request.RegisterRequest;
import com.example.fintech.api.testdata.TestConstants;

import java.util.UUID;

public final class TestDataFactory {

  public static RegisterRequest userWithPrefix(String prefix) {
    String username = prefix + "_" + UUID.randomUUID();
    return new RegisterRequest(username, TestConstants.DEFAULT_PASSWORD);
  }

}
