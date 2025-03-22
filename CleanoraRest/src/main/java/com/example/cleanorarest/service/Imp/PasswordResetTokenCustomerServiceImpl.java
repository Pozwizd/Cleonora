package com.example.cleanorarest.service.Imp;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.cleanorarest.entity.Customer;
import com.example.cleanorarest.entity.PasswordResetTokenCustomer;
import com.example.cleanorarest.model.authentication.ChangePasswordRequest;
import com.example.cleanorarest.model.authentication.EmailRequest;
import com.example.cleanorarest.repository.CustomerRepository;
import com.example.cleanorarest.repository.PasswordResetTokenCustomerRepository;
import com.example.cleanorarest.service.PasswordResetTokenCustomerService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class PasswordResetTokenCustomerServiceImpl implements PasswordResetTokenCustomerService {
    private final PasswordResetTokenCustomerRepository passwordResetTokenCustomerRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetTokenCustomerServiceImpl(PasswordResetTokenCustomerRepository passwordResetTokenCustomerRepository, CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.passwordResetTokenCustomerRepository = passwordResetTokenCustomerRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean validatePasswordResetToken(String token) {
        log.info("validatePasswordResetToken() - Finding password reset token and validating it");
        Optional<PasswordResetTokenCustomer> passwordResetToken = passwordResetTokenCustomerRepository.findByToken(token);
        boolean isValid = passwordResetToken.isPresent() && !passwordResetToken.get().getExpirationDate().isBefore(LocalDateTime.now());
        log.info("validatePasswordResetToken() - Password reset token was found and validated");
        return isValid;
    }


    @Override
    public void updatePassword(ChangePasswordRequest changePasswordRequest, String token) {
        log.info("updatePassword() - Updating password");
        PasswordResetTokenCustomer passwordResetTokenCustomer = passwordResetTokenCustomerRepository.findByToken(token).orElseThrow(()-> new EntityNotFoundException("Password reset token not found"));
        passwordResetTokenCustomer.getCustomer().setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        passwordResetTokenCustomerRepository.save(passwordResetTokenCustomer);
        log.info("updatePassword() - Password was updated");
    }

    @Override
    public String createOrUpdatePasswordResetToken(EmailRequest emailRequest) {
        Customer customer = customerRepository.findWithPasswordResetTokenByEmail(emailRequest.getEmail()).orElseThrow(()-> new EntityNotFoundException("User was not found by email "+emailRequest.getEmail()));
        String token = UUID.randomUUID().toString();
        if(customer.getPasswordResetTokenCustomer() != null){
            customer.getPasswordResetTokenCustomer().setToken(token);
            customer.getPasswordResetTokenCustomer().setExpirationDate();
            customerRepository.save(customer);
        } else {
            PasswordResetTokenCustomer passwordResetTokenCustomer = new PasswordResetTokenCustomer(token, customer);
            passwordResetTokenCustomerRepository.save(passwordResetTokenCustomer);
        }
        return token;
    }
}
