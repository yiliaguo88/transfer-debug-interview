package com.example.transfer.api;

import java.math.BigDecimal;

public record TransferRequest(String requestId, Long fromAccountId, Long toAccountId, BigDecimal amount) {
}