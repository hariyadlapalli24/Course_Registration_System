package com.abc.registration.dto;

import com.abc.registration.model.Course;

public class CourseResponse {
    private Long id;
    private String courseCode;
    private String courseName;
    private String description;

    public static CourseResponse from(Course course) {
        CourseResponse dto = new CourseResponse();
        dto.id = course.getId();
        dto.courseCode = course.getCourseCode();
        dto.courseName = course.getCourseName();
        dto.description = course.getDescription();
        return dto;
    }

    public Long getId() { return id; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public String getDescription() { return description; }
}
