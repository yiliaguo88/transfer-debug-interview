package com.example.transfer.service;

import com.example.transfer.event.TransferCompletedEvent;
import com.example.transfer.model.Ledger;
import com.example.transfer.repository.LedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LedgerService {
    private final LedgerRepository repo;

    @Transactional
    public void createLedger(TransferCompletedEvent e) {
        Ledger d = new Ledger();
        d.setTransferId(e.transferId());
        d.setRequestId(e.requestId());
        d.setAccountId(e.fromAccountId());
        d.setAmount(e.amount().negate());
        d.setType("DEBIT");
        d.setCreatedAt(LocalDateTime.now());
        repo.save(d);
        Ledger c = new Ledger();
        c.setTransferId(e.transferId());
        c.setRequestId(e.requestId());
        c.setAccountId(e.toAccountId());
        c.setAmount(e.amount());
        c.setType("CREDIT");
        c.setCreatedAt(LocalDateTime.now());
        repo.save(c);
    }
}