package com.lugro.NotificationClient;

public record NotificationRequest(Long toCustomerId, String toCustomerEmail, String message) {
    
}
