package com.example.cleanorarest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import com.example.cleanorarest.model.authentication.AuthenticationRequest;
import com.example.cleanorarest.model.authentication.AuthenticationResponse;
import com.example.cleanorarest.model.authentication.ChangePasswordRequest;
import com.example.cleanorarest.model.authentication.EmailRequest;
import com.example.cleanorarest.model.authentication.PasswordResetTokenResponse;
import com.example.cleanorarest.model.authentication.RefreshToken;
import com.example.cleanorarest.model.customer.CustomerRequest;
import com.example.cleanorarest.service.AuthenticationService;
import com.example.cleanorarest.service.CustomerService;
import com.example.cleanorarest.service.MailService;
import com.example.cleanorarest.service.PasswordResetTokenCustomerService;

import jakarta.servlet.http.HttpServletRequest;


@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private PasswordResetTokenCustomerService passwordResetTokenCustomerService;

    @Mock
    private CustomerService customerService;

    @Mock
    private MailService mailService;

    @InjectMocks
    private AuthenticationController authenticationController;


    @Test
    void registerCustomer_ValidInput_ReturnsCreated() {
        CustomerRequest customerRequest = new CustomerRequest();
        AuthenticationResponse expectedResponse = new AuthenticationResponse();
        when(authenticationService.register(customerRequest)).thenReturn(expectedResponse);

        ResponseEntity<AuthenticationResponse> response = authenticationController.registerCustomer(customerRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }


    @Test
    void login_ValidInput_ReturnsOk() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        AuthenticationResponse expectedResponse = new AuthenticationResponse();
        when(authenticationService.authenticate(authenticationRequest)).thenReturn(expectedResponse);

        ResponseEntity<AuthenticationResponse> response = authenticationController.login(authenticationRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }


    @Test
    void refreshToken_ValidInput_ReturnsOk() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRefreshToken("validRefreshToken");
        AuthenticationResponse expectedResponse = new AuthenticationResponse();
        when(authenticationService.refreshToken(refreshToken)).thenReturn(expectedResponse);

        ResponseEntity<?> response = authenticationController.refreshToken(refreshToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }


    @Test
    void refreshToken_InvalidInput_ReturnsUnauthorized() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRefreshToken("");

        ResponseEntity<?> response = authenticationController.refreshToken(refreshToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }


    @Test
    void refreshToken_CustomerNotFound_ReturnsNotFound() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRefreshToken("validRefreshToken");
        when(authenticationService.refreshToken(refreshToken)).thenReturn(null);

        ResponseEntity<?> response = authenticationController.refreshToken(refreshToken);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void forgotPassword_ValidInput_ReturnsOk() {
        EmailRequest emailRequest = new EmailRequest();
        String token = "testToken";
        HttpServletRequest httpRequest = new MockHttpServletRequest();
        when(passwordResetTokenCustomerService.createOrUpdatePasswordResetToken(emailRequest)).thenReturn(token);

        ResponseEntity<?> response = authenticationController.forgotPassword(httpRequest, emailRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(token, ((PasswordResetTokenResponse) response.getBody()).getPasswordResetToken());
    }

    @Test
    void changePassword_ValidInput_ReturnsOk() {
        String token = "validToken";
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        when(passwordResetTokenCustomerService.validatePasswordResetToken(token)).thenReturn(true);

        ResponseEntity<?> response = authenticationController.changePassword(token, changePasswordRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void changePassword_InvalidToken_ReturnsBadRequest() {
        String token = "invalidToken";
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        when(passwordResetTokenCustomerService.validatePasswordResetToken(token)).thenReturn(false);

        ResponseEntity<?> response = authenticationController.changePassword(token, changePasswordRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
