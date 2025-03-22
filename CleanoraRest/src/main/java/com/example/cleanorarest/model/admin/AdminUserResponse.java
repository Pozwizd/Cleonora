package com.example.cleanorarest.model.admin;

import lombok.Data;

@Data
public class AdminUserResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private Boolean isActive;
    private String role;
}
