package com.lugro;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/v1/sessions")
@AllArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        UUID sessionId = sessionService.login(request.email(), request.password());
        log.info("session created: {}", sessionId);
        return ResponseEntity.ok(Map.of("sessionId", sessionId.toString()));
    }

    @GetMapping("/validate/{sessionId}")
    public ResponseEntity<Map<String, Object>> validate(@PathVariable UUID sessionId) {
        return sessionService.validateSession(sessionId)
            .map(result -> ResponseEntity.ok(Map.<String, Object>of(
                "valid", true, "customerId", result.customerId(), "expiresAt", result.expiresAt().toString())))
            .orElse(ResponseEntity.ok(Map.of("valid", false)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody Map<String, String> body) {
        UUID sessionId = UUID.fromString(body.get("sessionId"));
        sessionService.logout(sessionId);
        log.info("session invalidated: {}", sessionId);
        return ResponseEntity.ok().build();
    }
}
