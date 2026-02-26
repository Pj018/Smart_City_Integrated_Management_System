package com.example.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder.Default
    private int points = 0;

    @Builder.Default
    private boolean active = true;

    @Column(name = "profile_photo")
    private String profilePhoto;

    @Column(name = "email_notifications")
    @Builder.Default
    private Boolean emailNotifications = true;

    @Column(name = "sms_notifications")
    @Builder.Default
    private Boolean smsNotifications = false;

    public Boolean getEmailNotifications() {
        return emailNotifications != null ? emailNotifications : true;
    }

    public Boolean getSmsNotifications() {
        return smsNotifications != null ? smsNotifications : false;
    }

    @Column(name = "theme_preference")
    @Builder.Default
    private String themePreference = "light";

    @Column(name = "language_preference")
    @Builder.Default
    private String languagePreference = "en";

    public String getThemePreference() {
        return themePreference != null ? themePreference : "light";
    }

    public String getLanguagePreference() {
        return languagePreference != null ? languagePreference : "en";
    }
}
