package com.example.transfer.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Ledger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long transferId;
    private String requestId;  // 预埋字段：用于幂等性检查，但未加唯一约束
    private Long accountId;
    private BigDecimal amount;
    private String type;
    private LocalDateTime createdAt;
}