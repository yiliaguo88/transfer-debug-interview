package com.example.transfer.event;

import java.math.BigDecimal;

public record TransferCompletedEvent(Long transferId, String requestId, Long fromAccountId, Long toAccountId, BigDecimal amount) {
}