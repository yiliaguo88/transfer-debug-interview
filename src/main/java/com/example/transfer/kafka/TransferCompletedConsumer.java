package com.example.transfer.kafka;

import com.example.transfer.event.TransferCompletedEvent;
import com.example.transfer.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransferCompletedConsumer {
    private final LedgerService ledgerService;

    @KafkaListener(topics = "transfer-completed", groupId = "ledger-service")
    public void consume(TransferCompletedEvent e) {
        ledgerService.createLedger(e);
        if (Math.random() < 0.3) {
            throw new RuntimeException("Simulated consumer failure");
        }
    }
}