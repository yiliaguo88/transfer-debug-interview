package com.example.transfer.api;

import com.example.transfer.model.Account;
import com.example.transfer.model.Transfer;
import com.example.transfer.repository.*;
import com.example.transfer.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService service;
    private final AccountRepository accounts;
    private final TransferRepository transfers;
    private final LedgerRepository ledgers;

    @PostMapping("/transfers")
    public Transfer transfer(@RequestBody TransferRequest r) {
        return service.transfer(r);
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of("accounts", accounts.findAll(), "transferCount", transfers.count(), "ledgerCount", ledgers.count());
    }

    @PostMapping("/reset")
    @Transactional
    public Map<String, Object> reset() {
        ledgers.deleteAll();
        transfers.deleteAll();
        accounts.deleteAll();

        Account alice = new Account();
        alice.setId(10001L);
        alice.setName("Alice");
        alice.setBalance(new BigDecimal("1000.00"));

        Account bob = new Account();
        bob.setId(10002L);
        bob.setName("Bob");
        bob.setBalance(new BigDecimal("1000.00"));

        accounts.save(alice);
        accounts.save(bob);

        return Map.of("status", "reset-ok", "accounts", accounts.findAll());
    }

    @GetMapping("/transfers/history")
    public List<Map<String, Object>> getTransferHistory() {
        List<Transfer> transferList = transfers.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Transfer t : transferList) {
            Account from = accounts.findById(t.getFromAccountId()).orElse(null);
            Account to = accounts.findById(t.getToAccountId()).orElse(null);

            Map<String, Object> item = new HashMap<>();
            item.put("id", t.getId());
            item.put("requestId", t.getRequestId());
            item.put("fromAccount", from != null ? from.getName() : "Unknown");
            item.put("toAccount", to != null ? to.getName() : "Unknown");
            item.put("amount", t.getAmount());
            item.put("status", t.getStatus());
            item.put("createdAt", t.getCreatedAt());

            result.add(item);
        }

        return result;
    }
}