package com.example.cleanorarest.service.Imp;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    @InjectMocks
    private JwtServiceImpl jwtService;

    @Mock
    private UserDetails userDetails;

    @Value("${jwt.secret.key}")
    private String secretKey = "mySecretKey";

    @Value("${jwt.token.access.expiration}")
    long accessExpiration = 86400000;

    @Value("${jwt.token.refresh.expiration}")
    long refreshExpiration = 604800000;




    @Test
    void testGenerateAccessToken() {
        jwtService.setSecretKey("zX1P4jRW7kF2yG9LhZ6V3mN8qX4aQ5bC0rT3nU7vW1yX8fL2mB5dH9eJ6sP0tA1X");
        jwtService.setAccessExpiration(86400000);
        jwtService.setRefreshExpiration(604800000);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        String token = jwtService.generateAccessToken(userDetails);
        assertNotNull(token);

        String email = jwtService.extractCustomerEmail(token);
        assertEquals("test@example.com", email);
    }

    @Test
    void testGenerateRefreshToken() {
        jwtService.setSecretKey("zX1P4jRW7kF2yG9LhZ6V3mN8qX4aQ5bC0rT3nU7vW1yX8fL2mB5dH9eJ6sP0tA1X");
        jwtService.setAccessExpiration(86400000);
        jwtService.setRefreshExpiration(604800000);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        String token = jwtService.generateRefreshToken(userDetails);
        assertNotNull(token);

        String email = jwtService.extractCustomerEmail(token);
        assertEquals("test@example.com", email);
    }

    @Test
    void testIsTokenValid() {
        jwtService.setSecretKey("zX1P4jRW7kF2yG9LhZ6V3mN8qX4aQ5bC0rT3nU7vW1yX8fL2mB5dH9eJ6sP0tA1X");
        jwtService.setAccessExpiration(86400000);
        jwtService.setRefreshExpiration(604800000);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        String token = jwtService.generateAccessToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void testIsTokenExpired() throws InterruptedException {
        jwtService.setSecretKey("zX1P4jRW7kF2yG9LhZ6V3mN8qX4aQ5bC0rT3nU7vW1yX8fL2mB5dH9eJ6sP0tA1X");

        jwtService.setAccessExpiration(1000);
        jwtService.setRefreshExpiration(1000);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        String token = jwtService.generateAccessToken(userDetails);

        Thread.sleep(2000);

        assertThrows(ExpiredJwtException.class, () -> {
            jwtService.isTokenValid(token, userDetails);
        });
    }




    @Test
    void testExtractCustomerEmail() {
        jwtService.setSecretKey("zX1P4jRW7kF2yG9LhZ6V3mN8qX4aQ5bC0rT3nU7vW1yX8fL2mB5dH9eJ6sP0tA1X");
        jwtService.setAccessExpiration(86400000);
        jwtService.setRefreshExpiration(604800000);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        String token = jwtService.generateAccessToken(userDetails);
        String email = jwtService.extractCustomerEmail(token);

        assertEquals("test@example.com", email);
    }
}
