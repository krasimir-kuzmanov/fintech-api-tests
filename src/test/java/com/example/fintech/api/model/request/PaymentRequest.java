package com.example.fintech.api.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;

public record PaymentRequest(
    String fromAccountId,
    String toAccountId,
    @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal amount) {
}