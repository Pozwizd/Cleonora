package com.example.cleanorarest.service;

import org.springframework.stereotype.Service;

import com.example.cleanorarest.entity.Customer;
import com.example.cleanorarest.model.customer.CustomerProfileRequest;
import com.example.cleanorarest.model.customer.CustomerResponse;

import jakarta.validation.Valid;


@Service
public interface CustomerService {
    boolean ifCustomerCountMoreThan(int i);

    Customer getCustomerByEmail(String username);

    CustomerResponse getCustomerResponseByEmail(String username);

    CustomerResponse updateCustomerFromCustomerRequest(@Valid CustomerProfileRequest customerRequest);

    void save(Customer customer);

    void deleteByEmail(String email);
}
