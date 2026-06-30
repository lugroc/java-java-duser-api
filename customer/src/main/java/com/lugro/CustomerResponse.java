package com.lugro;

public record CustomerResponse(Long id, String firstName, String lastName, String email) {
    static CustomerResponse from(Customer c) {
        return new CustomerResponse(c.getId(), c.getFirstName(), c.getLastName(), c.getEmail());
    }
}
