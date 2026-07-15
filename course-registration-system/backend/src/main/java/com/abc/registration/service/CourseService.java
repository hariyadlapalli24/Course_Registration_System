package com.abc.registration.service;

import com.abc.registration.dto.CourseResponse;
import com.abc.registration.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseResponse> listCourses() {
        return courseRepository.findAll().stream()
                .map(CourseResponse::from)
                .toList();
    }
}
