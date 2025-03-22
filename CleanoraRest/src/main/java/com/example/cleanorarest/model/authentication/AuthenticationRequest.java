package com.example.cleanorarest.model.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthenticationRequest {
    @NotEmpty(message = "Поле не может быть пустым")
    @Schema(example = "customer@gmail.com")
    private String email;
    @NotEmpty(message = "Поле не может быть пустым")
    @Schema(example = "customer")
    private String password;

}
