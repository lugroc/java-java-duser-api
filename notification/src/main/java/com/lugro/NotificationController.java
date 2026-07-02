package com.lugro;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lugro.NotificationClient.NotificationRequest;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@RestController
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;
    @PostMapping("/api/v1/notifications")
    public void sendNotification(@RequestBody NotificationRequest notificationRequest) {
        log.info("Sending notification {}", notificationRequest);
        notificationService.sendNotification(notificationRequest);
    }
}
