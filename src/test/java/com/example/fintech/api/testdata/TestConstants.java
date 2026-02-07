package com.example.fintech.api.testdata;

import java.math.BigDecimal;

public final class TestConstants {

  public static final String ERROR_CODE_USER_ALREADY_EXISTS = "USER_ALREADY_EXISTS";
  public static final String ERROR_CODE_INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
  public static final String ERROR_CODE_INVALID_AMOUNT = "INVALID_AMOUNT";
  public static final String ERROR_CODE_INSUFFICIENT_FUNDS = "INSUFFICIENT_FUNDS";
  public static final String TRANSACTION_STATUS_SUCCESS = "SUCCESS";
  public static final String DEFAULT_PASSWORD = "password";

  public static final BigDecimal ACCOUNT_FUND_AMOUNT = new BigDecimal("100.50");
  public static final BigDecimal ACCOUNT_BALANCE_AFTER_FUND = new BigDecimal("100.50");
  public static final BigDecimal ACCOUNT_BALANCE_FUND_AMOUNT = new BigDecimal("75.25");
  public static final BigDecimal ACCOUNT_BALANCE_AFTER_TOPUP = new BigDecimal("75.25");
  public static final BigDecimal ACCOUNT_INVALID_FUND_AMOUNT = new BigDecimal("-10");

  public static final BigDecimal TRANSACTION_INITIAL_BALANCE = new BigDecimal("100.00");
  public static final BigDecimal TRANSACTION_PAYMENT_AMOUNT = new BigDecimal("25.00");
  public static final BigDecimal TRANSACTION_EXCESSIVE_AMOUNT = new BigDecimal("500.00");
  public static final BigDecimal TRANSACTION_FIRST_AMOUNT = new BigDecimal("30.00");
  public static final BigDecimal TRANSACTION_SECOND_AMOUNT = new BigDecimal("20.00");

  public static final BigDecimal E2E_FUND_AMOUNT = new BigDecimal("100.00");
  public static final BigDecimal E2E_PAYMENT_AMOUNT = new BigDecimal("40.00");
  public static final BigDecimal E2E_ALICE_BALANCE = new BigDecimal("60.00");
  public static final BigDecimal E2E_BOB_BALANCE = new BigDecimal("40.00");

  private TestConstants() {
  }
}
