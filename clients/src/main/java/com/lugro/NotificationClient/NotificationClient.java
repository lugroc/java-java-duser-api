package com.lugro.NotificationClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("notification")
public interface NotificationClient {
    @PostMapping("/api/v1/notifications")
    void sendNotification(NotificationRequest notificationRequest);
}
