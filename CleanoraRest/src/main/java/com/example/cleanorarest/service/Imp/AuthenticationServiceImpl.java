package com.example.cleanorarest.service.Imp;

import com.example.cleanorarest.model.customer.CustomerRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.cleanorarest.entity.Customer;
import com.example.cleanorarest.model.authentication.AuthenticationRequest;
import com.example.cleanorarest.model.authentication.AuthenticationResponse;
import com.example.cleanorarest.model.authentication.RefreshToken;
import com.example.cleanorarest.repository.CustomerRepository;
import com.example.cleanorarest.service.AuthenticationService;
import com.example.cleanorarest.service.JwtService;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


   @Override
    public AuthenticationResponse register(CustomerRequest customerRequest) {
        Optional<Customer> existingCustomer = customerRepository.findByEmail(customerRequest.getEmail());

        Customer customer;
        if (existingCustomer.isPresent() && existingCustomer.get().getDeleted()) {
            customer = existingCustomer.get();
            customer.setDeleted(false);
        } else {
            customer = new Customer();
            customer.setEmail(customerRequest.getEmail());
        }
       customer.setName(customerRequest.getName());
       customer.setPhoneNumber(customerRequest.getPhoneNumber());
       customer.setPassword(passwordEncoder.encode(customerRequest.getPassword()));

       customer = customerRepository.save(customer);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setAccessToken(jwtService.generateAccessToken(customer));
        authenticationResponse.setRefreshToken(jwtService.generateRefreshToken(customer));
        return authenticationResponse;
    }

   @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        Customer customer = customerRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow(EntityNotFoundException::new);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),authenticationRequest.getPassword()
                ));

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setAccessToken(jwtService.generateAccessToken(customer));
        authenticationResponse.setRefreshToken(jwtService.generateRefreshToken(customer));
        return authenticationResponse;
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshToken refreshToken) {
        String email = jwtService.extractCustomerEmail(refreshToken.getRefreshToken());
        if(email != null){
            Customer Customer = customerRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
            if(jwtService.isTokenValid(refreshToken.getRefreshToken(), Customer)){
                AuthenticationResponse authenticationResponse = new AuthenticationResponse();
                authenticationResponse.setAccessToken(jwtService.generateAccessToken(Customer));
                authenticationResponse.setRefreshToken(refreshToken.getRefreshToken());
                return authenticationResponse;
            }
        }
        return null;
    }
}
