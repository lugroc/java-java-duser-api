package com.lugro;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.validation.constraints.AssertTrue;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @AssertTrue(message = "email already exists")
    public boolean existsByEmail(String email);
}