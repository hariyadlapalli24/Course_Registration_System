package com.abc.registration.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class EnrollmentRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotEmpty(message = "Select at least one course")
    private List<Long> courseIds;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public List<Long> getCourseIds() { return courseIds; }
    public void setCourseIds(List<Long> courseIds) { this.courseIds = courseIds; }
}
