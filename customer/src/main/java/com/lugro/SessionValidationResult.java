package com.lugro;

import java.time.LocalDateTime;

public record SessionValidationResult(Long customerId, LocalDateTime expiresAt) {
}