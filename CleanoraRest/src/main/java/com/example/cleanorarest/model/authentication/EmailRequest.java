package com.example.cleanorarest.model.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmailRequest {
    @NotEmpty(message = "Поле не может быть пустым")
    @Size(max=100, message = "Размер поля должен быть не более 50 символов")
    @Email(regexp = "[a-zA-Z0-9._-]+@[a-zA-Z0-9-]+\\.[a-z]{2,3}", message = "Неверный формат email")
    @Schema(example = "user@example.com")
    private String email;

}
