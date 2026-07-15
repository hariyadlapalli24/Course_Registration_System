package com.abc.registration.controller;

import com.abc.registration.dto.ApiMessageResponse;
import com.abc.registration.dto.RegisterRequest;
import com.abc.registration.dto.UserResponse;
import com.abc.registration.dto.UserUpdateRequest;
import com.abc.registration.exception.ApiException;
import com.abc.registration.model.Enrollment;
import com.abc.registration.model.Role;
import com.abc.registration.model.User;
import com.abc.registration.repository.EnrollmentRepository;
import com.abc.registration.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Everything under /api/admin/** is locked to ROLE_ADMIN by SecurityConfig's
 * authorizeHttpRequests rule. A regular USER token -- even a tampered
 * client -- is rejected with 403 before any method here ever runs.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserRepository userRepository,
                            EnrollmentRepository enrollmentRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/enrollments")
    public List<Map<String, Object>> allEnrollments() {
        return enrollmentRepository.findAll().stream()
                .map(e -> Map.<String, Object>of(
                        "enrollmentId", e.getId(),
                        "studentName", e.getUser().getName(),
                        "rollNumber", e.getUser().getRollNumber(),
                        "courseCode", e.getCourse().getCourseCode(),
                        "courseName", e.getCourse().getCourseName(),
                        "enrolledAt", e.getEnrolledAt().toString()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/users")
    public List<UserResponse> allUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    /**
     * The only API path that can create an ADMIN account. It lives behind
     * /api/admin/** so only an already-authenticated admin can call it --
     * there is no public UI or unauthenticated route that leads here.
     */
    @PostMapping("/admins")
    public ResponseEntity<UserResponse> createAdmin(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "An account with this email already exists.");
        }

        User admin = new User();
        admin.setName(request.getName());
        admin.setDepartment(request.getDepartment());
        admin.setRollNumber(request.getRollNumber());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setRole(Role.ADMIN);

        User saved = userRepository.save(admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(saved));
    }

    /**
     * Edit a user's profile fields (name, department, roll number, email).
     * Password and role are intentionally not editable through this endpoint.
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                     @Valid @RequestBody UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new ApiException(HttpStatus.CONFLICT, "Another account already uses this email.");
        }
        if (userRepository.existsByRollNumberAndIdNot(request.getRollNumber(), id)) {
            throw new ApiException(HttpStatus.CONFLICT, "Another account already uses this roll number.");
        }

        user.setName(request.getName());
        user.setDepartment(request.getDepartment());
        user.setRollNumber(request.getRollNumber());
        user.setEmail(request.getEmail());

        User saved = userRepository.save(user);
        return ResponseEntity.ok(UserResponse.from(saved));
    }

    /**
     * Delete a user. Their enrollments are removed first since enrollments
     * carry a NOT NULL foreign key to users (see schema.sql) and would
     * otherwise block the delete.
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiMessageResponse> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "User not found");
        }

        List<Enrollment> userEnrollments = enrollmentRepository.findByUserId(id);
        enrollmentRepository.deleteAll(userEnrollments);
        userRepository.deleteById(id);

        return ResponseEntity.ok(new ApiMessageResponse("User deleted."));
    }

    /**
     * Remove a single enrollment (i.e. drop a user from one course) without
     * touching the user's account or the course itself.
     */
    @DeleteMapping("/enrollments/{id}")
    public ResponseEntity<ApiMessageResponse> deleteEnrollment(@PathVariable Long id) {
        if (!enrollmentRepository.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Enrollment not found");
        }
        enrollmentRepository.deleteById(id);
        return ResponseEntity.ok(new ApiMessageResponse("Enrollment removed."));
    }
}
