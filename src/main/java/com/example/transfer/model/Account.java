package com.example.transfer.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "account")
public class Account {
    @Id
    private Long id;
    private String name;
    private BigDecimal balance;
}