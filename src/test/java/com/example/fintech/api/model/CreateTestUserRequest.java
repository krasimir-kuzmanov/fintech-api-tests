package com.example.fintech.api.model;

public record CreateTestUserRequest(String username, String password, Boolean overwrite) {
}
