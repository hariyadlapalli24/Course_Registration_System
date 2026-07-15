package com.abc.registration.service;

import com.abc.registration.dto.LoginRequest;
import com.abc.registration.dto.LoginResponse;
import com.abc.registration.dto.RegisterRequest;
import com.abc.registration.dto.UserResponse;
import com.abc.registration.exception.ApiException;
import com.abc.registration.model.Role;
import com.abc.registration.model.User;
import com.abc.registration.repository.UserRepository;
import com.abc.registration.security.AuthToken;
import com.abc.registration.security.TokenStore;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenStore tokenStore;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenStore tokenStore) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenStore = tokenStore;
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "An account with this email already exists.");
        }
        if (userRepository.existsByRollNumber(request.getRollNumber())) {
            throw new ApiException(HttpStatus.CONFLICT, "An account with this roll number already exists.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setDepartment(request.getDepartment());
        user.setRollNumber(request.getRollNumber());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Always USER for public self-registration, regardless of anything
        // a client might try to send -- RegisterRequest has no role field.
        user.setRole(Role.USER);

        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = tokenStore.issueToken(new AuthToken(user.getId(), user.getEmail(), user.getRole()));
        return new LoginResponse(token, UserResponse.from(user));
    }

    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        return UserResponse.from(user);
    }
}
