package com.example.transfer.api;

import com.example.transfer.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/api/notifications/stats")
    public Map<String, Object> getNotificationStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("pending", notificationService.getPendingCount());
        stats.put("sent", notificationService.getSentCount());
        stats.put("failed", notificationService.getFailedCount());
        return stats;
    }
}
