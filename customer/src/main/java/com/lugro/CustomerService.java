package com.lugro;
import org.springframework.stereotype.Service;
import com.lugro.NotificationClient.NotificationClient;
import com.lugro.NotificationClient.NotificationRequest;

import jakarta.validation.constraints.NotNull;

import com.lugro.Fraud.FraudCheckResponse;
import com.lugro.Fraud.FraudClient;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final FraudClient fraudClient;
    private final NotificationClient notificationClient;
    private final PasswordEncoder passwordEncoder;

    public CustomerResponse getCustomer(Long id) {
        return customerRepository.findById(id)
            .map(CustomerResponse::from)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    }

    public void registerCustomer(CustomerRegistrationRequest request) {
        if (customerRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }
        Customer customer = Customer.builder()
            .firstName(request.firstName())
            .lastName(request.lastName())
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .build();
        
        customerRepository.saveAndFlush(customer);
        @NotNull(message = "fraud check failed")
        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());

        if(fraudCheckResponse.isFraudster()){
            throw new IllegalStateException("fraudster");
        };
        customerRepository.save(customer);
        //todo: async
        notificationClient.sendNotification(
            new NotificationRequest(
                customer.getId(),
                customer.getEmail(),
                String.format("Hello %s, welcome to the system", customer.getFirstName())
            )
        );
    }
}
