package com.example.cleanorarest.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
public class PasswordResetTokenCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;


    private LocalDateTime expirationDate;

    private static final int EXPIRATION = 20;
    @Setter
    @OneToOne
    @JoinColumn(nullable = false, name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    public PasswordResetTokenCustomer() {
    }

    public PasswordResetTokenCustomer(String token, Customer customer) {
        this.token = token;
        this.expirationDate = calculateExpirationDate();
        this.customer = customer;
    }

    private LocalDateTime calculateExpirationDate() {
        return LocalDateTime.now().plusMinutes(EXPIRATION);
    }

    public void setExpirationDate() {
        this.expirationDate = LocalDateTime.now().plusMinutes(EXPIRATION);
    }

}
