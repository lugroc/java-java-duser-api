package com.lugro;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimitingFilter implements Filter {

    private static final int MAX_ATTEMPTS = 5;
    private static final int WINDOW_SECONDS = 900;

    private final Map<String, int[]> attempts = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (req.getRequestURI().contains("/api/v1/sessions/login") && "POST".equalsIgnoreCase(req.getMethod())) {
            String ip = getClientIP(req);
            if (isRateLimited(ip)) {
                res.setStatus(429);
                res.setContentType("application/json");
                res.getWriter().write("{\"error\":\"Too many login attempts. Try again later.\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private String getClientIP(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private boolean isRateLimited(String ip) {
        int now = (int) (Instant.now().getEpochSecond());
        int[] record = attempts.computeIfAbsent(ip, k -> new int[]{now, 1});

        synchronized (record) {
            if (now - record[0] > WINDOW_SECONDS) {
                record[0] = now;
                record[1] = 1;
                return false;
            }
            record[1]++;
            return record[1] > MAX_ATTEMPTS;
        }
    }
}
