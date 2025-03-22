package com.example.cleanorarest.service.Imp;

import com.example.cleanorarest.mapper.CustomerMapper;
import com.example.cleanorarest.model.customer.CustomerProfileRequest;
import com.example.cleanorarest.model.customer.CustomerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.example.cleanorarest.entity.Customer;
import com.example.cleanorarest.repository.CustomerRepository;
import com.example.cleanorarest.service.CustomerService;
import java.util.Optional;

@Service
@Slf4j
public class CustomerServiceImp implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImp(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }


    @Override
    public boolean ifCustomerCountMoreThan(int i) {
        return customerRepository.count() > i;
    }

    @Override
    public Customer getCustomerByEmail(String username) {
        return customerRepository.findByEmail(username).orElse(null);
    }

    @Override
    public CustomerResponse getCustomerResponseByEmail(String username) {
        Optional<Customer> customer = customerRepository.findByEmail(username);
        return customer.map(customerMapper::toResponse).orElse(null);
    }

    @Override
    public CustomerResponse updateCustomerFromCustomerRequest(CustomerProfileRequest customerRequest) {
        return null;
    }

    @Override
    public void save(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    public void deleteByEmail(String email) {
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(email);
        optionalCustomer.ifPresent(customer -> {
            customer.setDeleted(true);
            customerRepository.save(customer);
        });
    }
}
