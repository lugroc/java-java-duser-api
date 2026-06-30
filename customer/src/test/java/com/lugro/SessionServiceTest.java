package com.lugro;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock private SessionRepository sessionRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private SessionService sessionService;

    @Test
    void shouldLoginWithValidCredentials() {
        Customer customer = Customer.builder().id(1L).email("a@b.com").password("encoded").build();
        when(customerRepository.findAll()).thenReturn(List.of(customer));
        when(passwordEncoder.matches("pass", "encoded")).thenReturn(true);
        when(sessionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UUID sessionId = sessionService.login("a@b.com", "pass");

        assertThat(sessionId).isNotNull();
        verify(sessionRepository).save(any());
    }

    @Test
    void shouldThrowOnInvalidCredentials() {
        when(customerRepository.findAll()).thenReturn(List.of());
        assertThatThrownBy(() -> sessionService.login("a@b.com", "wrong"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid email or password");
    }

    @Test
    void shouldValidateActiveSession() {
        UUID id = UUID.randomUUID();
        Session session = Session.builder().id(id).customerId(1L).expiresAt(LocalDateTime.now().plusHours(1)).active(true).build();
        when(sessionRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.of(session));

        Optional<Long> result = sessionService.validateSession(id);

        assertThat(result).contains(1L);
        verify(sessionRepository).save(session);
    }

    @Test
    void shouldRejectExpiredSession() {
        UUID id = UUID.randomUUID();
        Session session = Session.builder().id(id).expiresAt(LocalDateTime.now().minusHours(1)).active(true).build();
        when(sessionRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.of(session));

        Optional<Long> result = sessionService.validateSession(id);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldDeactivateOnLogout() {
        UUID id = UUID.randomUUID();
        Session session = Session.builder().id(id).active(true).build();
        when(sessionRepository.findById(id)).thenReturn(Optional.of(session));

        sessionService.logout(id);

        assertThat(session.isActive()).isFalse();
        verify(sessionRepository).save(session);
    }
}
