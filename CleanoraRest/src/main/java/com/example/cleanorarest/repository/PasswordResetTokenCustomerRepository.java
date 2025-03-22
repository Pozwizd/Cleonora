package com.example.cleanorarest.repository;


import com.example.cleanorarest.entity.PasswordResetTokenCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenCustomerRepository extends JpaRepository<PasswordResetTokenCustomer, Long> {
    Optional<PasswordResetTokenCustomer> findByToken(String token);
}
