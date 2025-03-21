package com.example.cleanorarest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String token;
    @Getter
    private LocalDateTime expirationDate;
    private static final int EXPIRATION = 1;
    @OneToOne
    @JoinColumn(nullable = false, name = "admin_user_id", referencedColumnName = "id")
    private AdminUser adminUser;


    private LocalDateTime calculateExpirationDate() {
        return LocalDateTime.now().plusMinutes(EXPIRATION);
    }

    public void setExpirationDate() {
        this.expirationDate = LocalDateTime.now().plusMinutes(EXPIRATION);
    }

    public PasswordResetToken(String token, AdminUser adminUser) {
        this.token = token;
        this.expirationDate = calculateExpirationDate();
        this.adminUser = adminUser;
    }

}
