package com.example.cleanorarest.service;


import com.example.cleanorarest.model.customer.CustomerRequest;
import com.example.cleanorarest.model.authentication.AuthenticationRequest;
import com.example.cleanorarest.model.authentication.AuthenticationResponse;
import com.example.cleanorarest.model.authentication.RefreshToken;

public interface AuthenticationService {
    AuthenticationResponse register(CustomerRequest customerRequest);
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);
    AuthenticationResponse refreshToken(RefreshToken refreshToken);
}
