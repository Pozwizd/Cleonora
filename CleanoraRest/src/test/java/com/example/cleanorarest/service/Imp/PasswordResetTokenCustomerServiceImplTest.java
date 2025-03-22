package com.example.cleanorarest.service.Imp;

import com.example.cleanorarest.entity.Customer;
import com.example.cleanorarest.entity.PasswordResetTokenCustomer;
import com.example.cleanorarest.model.authentication.ChangePasswordRequest;
import com.example.cleanorarest.model.authentication.EmailRequest;
import com.example.cleanorarest.repository.CustomerRepository;
import com.example.cleanorarest.repository.PasswordResetTokenCustomerRepository;
import com.example.cleanorarest.service.PasswordResetTokenCustomerService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenCustomerServiceImplTest {

    @Mock
    private PasswordResetTokenCustomerRepository passwordResetTokenCustomerRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetTokenCustomerServiceImpl passwordResetTokenCustomerServiceImpl;

    /**
     * Test for {@link PasswordResetTokenCustomerService#validatePasswordResetToken(String)}.
     * Should return true for a valid token.
     */
    @Test
    void validatePasswordResetToken_ValidToken_ReturnsTrue() {
        String token = "validToken";
        PasswordResetTokenCustomer passwordResetToken = new PasswordResetTokenCustomer();
        passwordResetToken.setToken(token);
        passwordResetToken.setExpirationDate(LocalDateTime.now().plusHours(1));

        when(passwordResetTokenCustomerRepository.findByToken(token)).thenReturn(Optional.of(passwordResetToken));

        boolean isValid = passwordResetTokenCustomerServiceImpl.validatePasswordResetToken(token);

        assertTrue(isValid);
        verify(passwordResetTokenCustomerRepository).findByToken(token);
    }

    /**
     * Test for {@link PasswordResetTokenCustomerService#validatePasswordResetToken(String)}.
     * Should return false for an expired token.
     */
    @Test
    void validatePasswordResetToken_ExpiredToken_ReturnsFalse() {
        // Arrange
        String token = "expiredToken";
        PasswordResetTokenCustomer passwordResetToken = new PasswordResetTokenCustomer();
        passwordResetToken.setToken(token);
        passwordResetToken.setExpirationDate(LocalDateTime.now().minusHours(1));

        when(passwordResetTokenCustomerRepository.findByToken(token)).thenReturn(Optional.of(passwordResetToken));

        boolean isValid = passwordResetTokenCustomerServiceImpl.validatePasswordResetToken(token);

        assertFalse(isValid);
        verify(passwordResetTokenCustomerRepository).findByToken(token);
    }

    /**
     * Test for {@link PasswordResetTokenCustomerService#validatePasswordResetToken(String)}.
     * Should return false when the token is not found.
     */
    @Test
    void validatePasswordResetToken_TokenNotFound_ReturnsFalse() {
        String token = "nonExistentToken";

        when(passwordResetTokenCustomerRepository.findByToken(token)).thenReturn(Optional.empty());

        boolean isValid = passwordResetTokenCustomerServiceImpl.validatePasswordResetToken(token);

        assertFalse(isValid);
        verify(passwordResetTokenCustomerRepository).findByToken(token);
    }

    /**
     * Test for {@link PasswordResetTokenCustomerService#updatePassword(ChangePasswordRequest, String)}.
     * Should update the password for a valid token.
     */
    @Test
    void updatePassword_ValidToken_UpdatesPassword() {
        String token = "validToken";
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setNewPassword("newPassword");

        Customer customer = new Customer();
        customer.setPassword("oldPassword");
        PasswordResetTokenCustomer passwordResetTokenCustomer = new PasswordResetTokenCustomer();
        passwordResetTokenCustomer.setToken(token);
        passwordResetTokenCustomer.setCustomer(customer);

        when(passwordResetTokenCustomerRepository.findByToken(token)).thenReturn(Optional.of(passwordResetTokenCustomer));
        when(passwordEncoder.encode(changePasswordRequest.getNewPassword())).thenReturn("encodedNewPassword");

        passwordResetTokenCustomerServiceImpl.updatePassword(changePasswordRequest, token);

        assertEquals("encodedNewPassword", customer.getPassword());
        verify(passwordResetTokenCustomerRepository).findByToken(token);
        verify(passwordEncoder).encode(changePasswordRequest.getNewPassword());
        verify(passwordResetTokenCustomerRepository).save(passwordResetTokenCustomer);
    }

    /**
     * Test for {@link PasswordResetTokenCustomerService#updatePassword(ChangePasswordRequest, String)}.
     * Should throw EntityNotFoundException when the token is not found.
     */
    @Test
    void updatePassword_TokenNotFound_ThrowsException() {
        String token = "nonExistentToken";
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setNewPassword("newPassword");

        when(passwordResetTokenCustomerRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> passwordResetTokenCustomerServiceImpl.updatePassword(changePasswordRequest, token));
        verify(passwordResetTokenCustomerRepository).findByToken(token);
    }

    /**
     * Test for {@link PasswordResetTokenCustomerService#createOrUpdatePasswordResetToken(EmailRequest)}.
     * Should throw EntityNotFoundException when the customer is not found.
     */
    @Test
    void createOrUpdatePasswordResetToken_CustomerNotFound_ThrowsException() {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmail("nonExistent@example.com");

        when(customerRepository.findWithPasswordResetTokenByEmail(emailRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> passwordResetTokenCustomerServiceImpl.createOrUpdatePasswordResetToken(emailRequest));
        verify(customerRepository).findWithPasswordResetTokenByEmail(emailRequest.getEmail());
    }

    /**
     * Test for {@link PasswordResetTokenCustomerService#createOrUpdatePasswordResetToken(EmailRequest)}.
     * Should create a new token when the customer does not have an existing token.
     */
    @Test
    void createOrUpdatePasswordResetToken_NewToken_CreatesToken() {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmail("test@example.com");

        Customer customer = new Customer();
        customer.setEmail(emailRequest.getEmail());

        when(customerRepository.findWithPasswordResetTokenByEmail(emailRequest.getEmail())).thenReturn(Optional.of(customer));
        when(passwordResetTokenCustomerRepository.save(any(PasswordResetTokenCustomer.class))).thenAnswer(invocation -> {
            PasswordResetTokenCustomer token = invocation.getArgument(0);
            token.setId(1L);
            return token;
        });

        String token = passwordResetTokenCustomerServiceImpl.createOrUpdatePasswordResetToken(emailRequest);

        assertNotNull(token);
        verify(customerRepository).findWithPasswordResetTokenByEmail(emailRequest.getEmail());
        verify(passwordResetTokenCustomerRepository).save(any(PasswordResetTokenCustomer.class));
    }

    /**
     * Test for {@link PasswordResetTokenCustomerService#createOrUpdatePasswordResetToken(EmailRequest)}.
     * Should update the existing token when the customer already has a token.
     */
    @Test
    void createOrUpdatePasswordResetToken_ExistingToken_UpdatesToken() {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmail("test@example.com");

        Customer customer = new Customer();
        customer.setEmail(emailRequest.getEmail());
        PasswordResetTokenCustomer existingToken = new PasswordResetTokenCustomer();
        existingToken.setToken("oldToken");
        existingToken.setCustomer(customer);
        customer.setPasswordResetTokenCustomer(existingToken);

        when(customerRepository.findWithPasswordResetTokenByEmail(emailRequest.getEmail())).thenReturn(Optional.of(customer));

        String newToken = passwordResetTokenCustomerServiceImpl.createOrUpdatePasswordResetToken(emailRequest);

        assertNotNull(newToken);
        assertNotEquals("oldToken", newToken);
        verify(customerRepository).findWithPasswordResetTokenByEmail(emailRequest.getEmail());
        verify(customerRepository).save(customer);
    }
}
