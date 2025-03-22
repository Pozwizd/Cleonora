package com.example.cleanorarest.service;


import com.example.cleanorarest.model.authentication.ChangePasswordRequest;
import com.example.cleanorarest.model.authentication.EmailRequest;

public interface PasswordResetTokenCustomerService {
    boolean validatePasswordResetToken(String token);
    void updatePassword(ChangePasswordRequest changePasswordRequest, String token);
    String createOrUpdatePasswordResetToken(EmailRequest emailRequest);
}
