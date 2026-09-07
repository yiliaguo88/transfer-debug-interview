package com.example.transfer.service;

import com.example.transfer.model.Notification;
import com.example.transfer.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void sendTransferNotification(Long transferId, Long accountId, String message) {
        Notification notification = new Notification();
        notification.setTransferId(transferId);
        notification.setAccountId(accountId);
        notification.setType("SMS");
        notification.setMessage(message);
        notification.setStatus("PENDING");
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        executor.submit(() -> {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1000));


                notification.setStatus("SENT");
                notification.setSentAt(LocalDateTime.now());
                notificationRepository.save(notification);
                log.info("Notification sent for transfer {}", transferId);

            } catch (Exception e) {
                notification.setStatus("FAILED");
                notification.setRetryCount(notification.getRetryCount() + 1);
                notificationRepository.save(notification);
                log.error("Failed to send notification for transfer {}", transferId, e);
            }
        });
    }

    public long getPendingCount() {
        return notificationRepository.countByStatus("PENDING");
    }

    public long getSentCount() {
        return notificationRepository.countByStatus("SENT");
    }

    public long getFailedCount() {
        return notificationRepository.countByStatus("FAILED");
    }
}
