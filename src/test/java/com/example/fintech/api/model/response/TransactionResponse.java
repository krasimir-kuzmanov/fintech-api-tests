package com.example.fintech.api.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TransactionResponse(String transactionId, String status, BigDecimal amount) {
}