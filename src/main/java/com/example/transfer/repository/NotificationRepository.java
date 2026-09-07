package com.example.transfer.repository;

import com.example.transfer.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByTransferIdOrderByCreatedAtDesc(Long transferId);
    List<Notification> findByStatus(String status);
    long countByStatus(String status);
}
