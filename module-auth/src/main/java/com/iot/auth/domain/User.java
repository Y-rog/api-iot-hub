package com.iot.auth.domain;

import java.util.UUID;

public class User {

    private final UUID id;
    private final String email;
    private final String hashedPassword;
    private final String role;

    public User(UUID id, String email, String hashedPassword, String role) {
        this.id = id;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.role = role;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getHashedPassword() { return hashedPassword; }
    public String getRole() { return role; }
}