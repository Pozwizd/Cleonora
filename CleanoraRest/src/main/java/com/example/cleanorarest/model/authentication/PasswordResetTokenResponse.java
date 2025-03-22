package com.example.cleanorarest.model.authentication;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasswordResetTokenResponse {
    private String passwordResetToken;

    public PasswordResetTokenResponse(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

}
