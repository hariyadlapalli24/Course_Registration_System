package com.abc.registration.controller;

import com.abc.registration.dto.ApiMessageResponse;
import com.abc.registration.dto.EnrollmentRequest;
import com.abc.registration.exception.ApiException;
import com.abc.registration.security.AuthToken;
import com.abc.registration.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public ResponseEntity<ApiMessageResponse> enroll(@Valid @RequestBody EnrollmentRequest request,
                                                      Authentication authentication) {
        AuthToken caller = (AuthToken) authentication.getPrincipal();

        boolean isSelf = caller.getUserId().equals(request.getUserId());
        boolean isAdmin = caller.getRole().name().equals("ADMIN");

        if (!isSelf && !isAdmin) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You may only enroll yourself in courses.");
        }

        int count = enrollmentService.enroll(request);
        return ResponseEntity.ok(new ApiMessageResponse(
                count > 0 ? "Registration successful." : "You were already enrolled in the selected course(s)."));
    }
}
