package com.lugro;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
public class SessionAuthFilter extends OncePerRequestFilter {

    private final SessionService sessionService;

    public SessionAuthFilter(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String sessionIdHeader = request.getHeader("X-Session-Id");

        if (sessionIdHeader != null) {
            try {
                UUID sessionId = UUID.fromString(sessionIdHeader);
                sessionService.validateSession(sessionId).ifPresent(customerId -> {
                    UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(customerId, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                });
            } catch (IllegalArgumentException ignored) {
            }
        }

        filterChain.doFilter(request, response);
    }
}
