package com.lugro;

import com.lugro.Fraud.FraudCheckResponse;
import com.lugro.Fraud.FraudClient;
import com.lugro.NotificationClient.NotificationClient;
import com.lugro.NotificationClient.NotificationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private FraudClient fraudClient;
    @Mock private NotificationClient notificationClient;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private CustomerService customerService;

    @Test
    void shouldRegisterCustomer() {
        CustomerRegistrationRequest request = new CustomerRegistrationRequest("John", "Doe", "john@test.com", "secret");
        when(passwordEncoder.encode("secret")).thenReturn("encoded");
        when(customerRepository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        when(customerRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(fraudClient.isFraudster(any())).thenReturn(new FraudCheckResponse(false));

        customerService.registerCustomer(request);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository, times(1)).save(captor.capture());
        Customer saved = captor.getValue();
        assertThat(saved.getEmail()).isEqualTo("john@test.com");
        assertThat(saved.getPassword()).isEqualTo("encoded");
        verify(notificationClient).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void shouldThrowWhenFraudster() {
        CustomerRegistrationRequest request = new CustomerRegistrationRequest("Jane", "Doe", "jane@test.com", "secret");
        when(passwordEncoder.encode("secret")).thenReturn("encoded");
        when(customerRepository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        when(fraudClient.isFraudster(any())).thenReturn(new FraudCheckResponse(true));

        assertThatThrownBy(() -> customerService.registerCustomer(request))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("fraudster");

        verify(notificationClient, never()).sendNotification(any());
    }
}
