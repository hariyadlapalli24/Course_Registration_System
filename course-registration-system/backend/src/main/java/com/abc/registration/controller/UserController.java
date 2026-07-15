package com.abc.registration.controller;

import com.abc.registration.dto.LoginRequest;
import com.abc.registration.dto.LoginResponse;
import com.abc.registration.dto.RegisterRequest;
import com.abc.registration.dto.UserResponse;
import com.abc.registration.exception.ApiException;
import com.abc.registration.security.AuthToken;
import com.abc.registration.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id, Authentication authentication) {
        AuthToken caller = (AuthToken) authentication.getPrincipal();

        boolean isSelf = caller.getUserId().equals(id);
        boolean isAdmin = caller.getRole().name().equals("ADMIN");

        if (!isSelf && !isAdmin) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You may only view your own profile.");
        }

        return ResponseEntity.ok(userService.getUser(id));
    }
}
