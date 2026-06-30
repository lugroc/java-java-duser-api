package com.lugro;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    private final SessionAuthFilter sessionAuthFilter;
    private final LoginRateLimitingFilter loginRateLimitingFilter;

    public SecurityConfig(SessionAuthFilter sessionAuthFilter, LoginRateLimitingFilter loginRateLimitingFilter) {
        this.sessionAuthFilter = sessionAuthFilter;
        this.loginRateLimitingFilter = loginRateLimitingFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/error").permitAll()
                .requestMatchers("/api/v1/customers/**").permitAll()
                .requestMatchers("/api/v1/sessions/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(loginRateLimitingFilter, SessionAuthFilter.class)
            .addFilterBefore(sessionAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
// rebuild trigger 20260630001500
