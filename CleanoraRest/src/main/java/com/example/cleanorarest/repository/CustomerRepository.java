package com.example.cleanorarest.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.cleanorarest.entity.Customer;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    @Query(value = "SELECT u FROM Customer u LEFT JOIN FETCH u.passwordResetTokenCustomer s WHERE u.email = :email")
    Optional<Customer> findWithPasswordResetTokenByEmail(@Param("email")String email);

    Page<Customer> findAll(Specification<Customer> specification, Pageable pageable);

    Optional<Customer> getCustomerByEmail(String email);
}
