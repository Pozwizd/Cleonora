package com.example.cleanorarest.model.admin;

import lombok.Data;

import java.io.Serializable;


@Data
public class AdminUserProfileResponse implements Serializable {
    Long id;
    String name;
    String surname;
    String avatar;
    String email;
    String phoneNumber;
    Boolean isActive;
    String role;
}
