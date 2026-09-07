package com.example.transfer.config;

import com.example.transfer.model.Account;
import com.example.transfer.repository.AccountRepository;
import org.springframework.context.annotation.*;
import org.springframework.boot.CommandLineRunner;

import java.math.BigDecimal;

@Configuration
public class DemoDataConfig {
    @Bean
    CommandLineRunner init(AccountRepository r) {
        return args -> {
            if (r.count() == 0) {
                Account a = new Account();
                a.setId(10001L);
                a.setName("Alice");
                a.setBalance(new BigDecimal("1000.00"));
                Account b = new Account();
                b.setId(10002L);
                b.setName("Bob");
                b.setBalance(new BigDecimal("1000.00"));
                r.save(a);
                r.save(b);
            }
        };
    }
}