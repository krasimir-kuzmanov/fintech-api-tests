package com.example.fintech.api.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;

public record FundAccountRequest(
    @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal amount
) {
}