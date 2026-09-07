package com.example.transfer.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long transferId;
    private Long accountId;
    private String type; // EMAIL, SMS, WEBHOOK
    private String status; // PENDING, SENT, FAILED
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private Integer retryCount = 0;
}
