package com.example.cleanorarest.service.Imp;

import com.example.cleanorarest.repository.AdminUserRepository;
import com.example.cleanorarest.repository.CustomerRepository;
import com.example.cleanorarest.service.JwtService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import com.example.cleanorarest.entity.Customer;
import com.example.cleanorarest.model.authentication.AuthenticationRequest;

import com.example.cleanorarest.model.authentication.AuthenticationResponse;
import com.example.cleanorarest.model.authentication.RefreshToken;

/**
 * Юнит тесты для {@link AuthenticationServiceImpl}.
 */
import com.example.cleanorarest.model.customer.CustomerRequest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {


    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;


    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    /**
     * Тест для {@link AuthenticationServiceImpl#register(CustomerRequest)}.
     * Должен возвращать AuthenticationResponse при регистрации нового клиента.
     */
    @Test
    void register_NewCustomer_ReturnsAuthenticationResponse() {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setEmail("test@example.com");
        customerRequest.setPassword("password");
        customerRequest.setName("Test User");
        customerRequest.setPhoneNumber("1234567890");

        when(customerRepository.findByEmail(customerRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(customerRequest.getPassword())).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateAccessToken(any(Customer.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(Customer.class))).thenReturn("refreshToken");

        AuthenticationResponse response = authenticationService.register(customerRequest);

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());

        verify(customerRepository).findByEmail(customerRequest.getEmail());
        verify(passwordEncoder).encode(customerRequest.getPassword());
        verify(customerRepository).save(any(Customer.class));
        verify(jwtService).generateAccessToken(any(Customer.class));
        verify(jwtService).generateRefreshToken(any(Customer.class));
    }

    /**
     * Тест для {@link AuthenticationServiceImpl#register(CustomerRequest)}.
     * Должен возвращать AuthenticationResponse при регистрации существующего, удаленного клиента.
     */
     @Test
    void register_ExistingDeletedCustomer_ReturnsAuthenticationResponse() {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setEmail("test@example.com");
        customerRequest.setPassword("password");
        customerRequest.setName("Test User");
        customerRequest.setPhoneNumber("1234567890");

        Customer existingCustomer = new Customer();
        existingCustomer.setDeleted(true);
        existingCustomer.setEmail(customerRequest.getEmail());

        when(customerRepository.findByEmail(customerRequest.getEmail())).thenReturn(Optional.of(existingCustomer));
        when(passwordEncoder.encode(customerRequest.getPassword())).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateAccessToken(any(Customer.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(Customer.class))).thenReturn("refreshToken");

        AuthenticationResponse response = authenticationService.register(customerRequest);

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertFalse(existingCustomer.getDeleted());

        verify(customerRepository).findByEmail(customerRequest.getEmail());
        verify(passwordEncoder).encode(customerRequest.getPassword());
        verify(customerRepository).save(any(Customer.class));
        verify(jwtService).generateAccessToken(any(Customer.class));
        verify(jwtService).generateRefreshToken(any(Customer.class));
    }

    /**
     * Тест для {@link AuthenticationServiceImpl#authenticate(AuthenticationRequest)}.
     * Должен возвращать AuthenticationResponse при аутентификации с валидными учетными данными.
     */
    @Test
    void authenticate_ValidCredentials_ReturnsAuthenticationResponse() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("test@example.com");
        authenticationRequest.setPassword("password");

        Customer customer = new Customer();
        customer.setEmail("test@example.com");
        customer.setPassword("encodedPassword");

        when(customerRepository.findByEmail(authenticationRequest.getEmail())).thenReturn(Optional.of(customer));
        when(jwtService.generateAccessToken(any(Customer.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(Customer.class))).thenReturn("refreshToken");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);

        AuthenticationResponse response = authenticationService.authenticate(authenticationRequest);

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());

        verify(customerRepository).findByEmail(authenticationRequest.getEmail());
        verify(jwtService).generateAccessToken(any(Customer.class));
        verify(jwtService).generateRefreshToken(any(Customer.class));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    /**
     * Тест для {@link AuthenticationServiceImpl#authenticate(AuthenticationRequest)}.
     * Должен бросать EntityNotFoundException при аутентификации с невалидными учетными данными.
     */
    @Test
    void authenticate_InvalidCredentials_ThrowsEntityNotFoundException() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("test@example.com");
        authenticationRequest.setPassword("password");

        when(customerRepository.findByEmail(authenticationRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authenticationService.authenticate(authenticationRequest));

        verify(customerRepository).findByEmail(authenticationRequest.getEmail());
    }

    /**
     * Тест для {@link AuthenticationServiceImpl#refreshToken(RefreshToken)}.
     * Должен возвращать AuthenticationResponse при обновлении токена с валидным токеном.
     */
    @Test
    void refreshToken_ValidToken_ReturnsAuthenticationResponse() {
        RefreshToken refreshTokenRequest = new RefreshToken();
        refreshTokenRequest.setRefreshToken("validRefreshToken");

        Customer customer = new Customer();
        customer.setEmail("test@example.com");

        when(jwtService.extractCustomerEmail("validRefreshToken")).thenReturn("test@example.com");
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(customer));
        when(jwtService.isTokenValid("validRefreshToken", customer)).thenReturn(true);
        when(jwtService.generateAccessToken(any(Customer.class))).thenReturn("newAccessToken");

        AuthenticationResponse response = authenticationService.refreshToken(refreshTokenRequest);

        assertNotNull(response);
        assertEquals("newAccessToken", response.getAccessToken());
        assertEquals("validRefreshToken", response.getRefreshToken());

        verify(jwtService).extractCustomerEmail("validRefreshToken");
        verify(customerRepository).findByEmail("test@example.com");
        verify(jwtService).isTokenValid("validRefreshToken", customer);
        verify(jwtService).generateAccessToken(any(Customer.class));
    }

    /**
     * Тест для {@link AuthenticationServiceImpl#refreshToken(RefreshToken)}.
     * Должен возвращать null при обновлении токена с невалидным токеном.
     */
    @Test
    void refreshToken_InvalidToken_ReturnsNull() {
        RefreshToken refreshTokenRequest = new RefreshToken();
        refreshTokenRequest.setRefreshToken("invalidRefreshToken");

        Customer customer = new Customer();
        customer.setEmail("test@example.com");

        when(jwtService.extractCustomerEmail("invalidRefreshToken")).thenReturn("test@example.com");
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(customer));
        when(jwtService.isTokenValid("invalidRefreshToken", customer)).thenReturn(false);

        AuthenticationResponse response = authenticationService.refreshToken(refreshTokenRequest);

        assertNull(response);

        verify(jwtService).extractCustomerEmail("invalidRefreshToken");
        verify(customerRepository).findByEmail("test@example.com");
        verify(jwtService).isTokenValid("invalidRefreshToken", customer);
        verify(jwtService, never()).generateAccessToken(any(Customer.class));
    }

    /**
     * Тест для {@link AuthenticationServiceImpl#refreshToken(RefreshToken)}.
     * Должен возвращать null, если не удалось извлечь email.
     */
     @Test
    void refreshToken_EmailExtractionFails_ReturnsNull() {

        RefreshToken refreshTokenRequest = new RefreshToken();
        refreshTokenRequest.setRefreshToken("invalidRefreshToken");
        when(jwtService.extractCustomerEmail("invalidRefreshToken")).thenReturn(null);

        AuthenticationResponse response = authenticationService.refreshToken(refreshTokenRequest);

        assertNull(response);
        verify(jwtService).extractCustomerEmail("invalidRefreshToken");
        verifyNoInteractions(customerRepository);
    }
}
