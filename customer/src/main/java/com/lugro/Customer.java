package com.lugro;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Customer {
    @Id
    @SequenceGenerator(
        name = "customer_id_sequence", 
        sequenceName = "customer_id_sequence"
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE, 
        generator = "customer_id_sequence"
    )
    private Long id;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "Please provide an email address")
    private String email;
    @NotBlank
    private String password;
}
