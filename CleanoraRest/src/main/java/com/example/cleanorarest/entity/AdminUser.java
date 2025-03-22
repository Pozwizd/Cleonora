package com.example.cleanorarest.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Data
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String surname;

    private String avatar;

    private String email;

    private String phoneNumber;

    private String password;

    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    private AdminRole role = AdminRole.ADMIN;

    @OneToOne(mappedBy = "adminUser", cascade = CascadeType.ALL)
    private PasswordResetToken passwordResetToken;

}
