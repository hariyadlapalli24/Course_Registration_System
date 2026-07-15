package com.abc.registration.security;

import com.abc.registration.model.Role;

public class AuthToken {
    private final Long userId;
    private final String email;
    private final Role role;

    public AuthToken(Long userId, String email, Role role) {
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
}
