package com.lugro;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import com.lugro.NotificationClient.NotificationRequest;
@AllArgsConstructor
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    
    public void sendNotification(NotificationRequest notificationRequest) {
        notificationRepository.save(
            Notification.builder()
            .toCustomerId(notificationRequest.toCustomerId())
            .toCustomerEmail(notificationRequest.toCustomerEmail())
            .sender("Luciano Guerrero")
            .message(notificationRequest.message())
            .sentAt(LocalDateTime.now())
            .build()
        );
    }
}
