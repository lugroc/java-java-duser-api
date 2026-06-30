package com.lugro;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public UUID login(String email, String password) {
        Customer customer = customerRepository.findAll().stream()
            .filter(c -> c.getEmail().equals(email) && passwordEncoder.matches(password, c.getPassword()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        Session session = Session.builder()
            .id(UUID.randomUUID())
            .customerId(customer.getId())
            .createdAt(LocalDateTime.now())
            .lastAccessedAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusHours(24))
            .active(true)
            .build();

        sessionRepository.save(session);
        return session.getId();
    }

    public Optional<SessionValidationResult> validateSession(UUID sessionId) {
        return sessionRepository.findByIdAndActiveTrue(sessionId)
            .filter(s -> s.getExpiresAt().isAfter(LocalDateTime.now()))
            .map(s -> {
                s.setLastAccessedAt(LocalDateTime.now());
                sessionRepository.save(s);
                return new SessionValidationResult(s.getCustomerId(), s.getExpiresAt());
            });
    }

    public void logout(UUID sessionId) {
        sessionRepository.findById(sessionId).ifPresent(session -> {
            session.setActive(false);
            sessionRepository.save(session);
        });
    }
}
