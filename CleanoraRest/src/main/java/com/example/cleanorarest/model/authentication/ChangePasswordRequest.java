package com.example.cleanorarest.model.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import com.example.cleanorarest.validators.customer.confirmPassword.PasswordMatching;

@Setter
@Getter
@PasswordMatching(
        newPassword = "newPassword",
        confirmNewPassword = "confirmNewPassword",
        message = "Пароли должны быть одинаковыми"
)
public class ChangePasswordRequest {
    @NotEmpty(message = "Поле не может быть пустым")
    @Size(max=100, message = "Размер поля должен быть не более 100 символов")
    @Pattern.List({
            @Pattern(regexp = ".{8,}", message = "Пароль должен содержать хотя бы одну цифру, одну заглавную букву, один спецсимвол ,./? и быть длиной более 8 символов"),
            @Pattern(regexp = ".*\\d+.*", message = "Пароль должен содержать хотя бы одну цифру, одну заглавную букву, один спецсимвол ,./? и быть длиной более 8 символов"),
            @Pattern(regexp = ".*[A-Z]+.*", message = "Пароль должен содержать хотя бы одну цифру, одну заглавную букву, один спецсимвол ,./? и быть длиной более 8 символов")
    })
    @Schema(example = "admin")
    private String newPassword;

    @NotEmpty(message = "Поле не может быть пустым")
    @Size(max=100, message = "Размер поля должен быть не более 100 символов")
    @Schema(example = "admin")
    private String confirmNewPassword;


}