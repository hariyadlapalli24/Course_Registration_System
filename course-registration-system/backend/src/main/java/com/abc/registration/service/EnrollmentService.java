package com.abc.registration.service;

import com.abc.registration.dto.EnrollmentRequest;
import com.abc.registration.exception.ApiException;
import com.abc.registration.model.Course;
import com.abc.registration.model.Enrollment;
import com.abc.registration.model.User;
import com.abc.registration.repository.CourseRepository;
import com.abc.registration.repository.EnrollmentRepository;
import com.abc.registration.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                              UserRepository userRepository,
                              CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    public int enroll(EnrollmentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        List<Enrollment> newEnrollments = new ArrayList<>();

        for (Long courseId : request.getCourseIds()) {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found: " + courseId));

            if (!enrollmentRepository.existsByUserIdAndCourseId(user.getId(), course.getId())) {
                newEnrollments.add(new Enrollment(user, course));
            }
        }

        enrollmentRepository.saveAll(newEnrollments);
        return newEnrollments.size();
    }
}
