package com.example.transfer.service;

import com.example.transfer.api.TransferRequest;
import com.example.transfer.event.TransferCompletedEvent;
import com.example.transfer.model.*;
import com.example.transfer.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;
    private final KafkaTemplate<String, TransferCompletedEvent> kafkaTemplate;
    private final NotificationService notificationService;

    public Transfer transfer(TransferRequest r) {
        return doTransfer(r);
    }

    @Transactional
    public Transfer doTransfer(TransferRequest r) {
        Account from = accountRepository.findById(r.fromAccountId()).orElseThrow();
        Account to = accountRepository.findById(r.toAccountId()).orElseThrow();
        if (from.getBalance().compareTo(r.amount()) < 0)
            throw new RuntimeException("Insufficient balance");
        from.setBalance(from.getBalance().subtract(r.amount()));
        to.setBalance(to.getBalance().add(r.amount()));
        accountRepository.save(from);
        accountRepository.save(to);
        Transfer t = new Transfer();
        t.setRequestId(r.requestId());
        t.setFromAccountId(from.getId());
        t.setToAccountId(to.getId());
        t.setAmount(r.amount());
        t.setStatus("SUCCESS");
        t.setCreatedAt(LocalDateTime.now());
        transferRepository.save(t);
        kafkaTemplate.send("transfer-completed", new TransferCompletedEvent(t.getId(), r.requestId(), from.getId(), to.getId(), r.amount()));
        notificationService.sendTransferNotification(t.getId(), from.getId(),
            String.format("Transfer of %.2f to account %d completed", r.amount(), to.getId()));
        if (ThreadLocalRandom.current().nextInt(10) == 1) {
            throw new RuntimeException("Simulated failure after Kafka send");
        }
        return t;
    }
}