package com.abc.registration.config;

import com.abc.registration.model.Course;
import com.abc.registration.model.Role;
import com.abc.registration.model.User;
import com.abc.registration.repository.CourseRepository;
import com.abc.registration.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(CourseRepository courseRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedCourses();
        seedAdmin();
    }

    private void seedCourses() {
        if (courseRepository.count() > 0) {
            return;
        }

        courseRepository.save(new Course("CS101", "Introduction to Computer Science",
                "Fundamentals of programming, algorithms, and problem solving."));
        courseRepository.save(new Course("CS204", "Data Structures & Algorithms",
                "Core data structures, complexity analysis, and algorithm design."));
        courseRepository.save(new Course("CS310", "Database Management Systems",
                "Relational design, SQL, normalization, and transactions."));
        courseRepository.save(new Course("CS415", "Cloud Computing",
                "Distributed systems, virtualization, and cloud service models."));
        courseRepository.save(new Course("MA201", "Discrete Mathematics",
                "Logic, set theory, combinatorics, and graph theory."));
        courseRepository.save(new Course("EN110", "Technical Communication",
                "Writing and presenting technical material clearly."));
    }

    private void seedAdmin() {
        // This is the ONLY way an admin account exists in a fresh database --
        // there is no public UI or API path that lets a client choose
        // role=ADMIN for itself. Change this password immediately after
        // first login in any real deployment, and create further admins
        // through the protected POST /api/admin/admins endpoint.
        if (userRepository.existsByEmail("admin@abcuniversity.edu")) {
            return;
        }

        User admin = new User();
        admin.setName("System Administrator");
        admin.setDepartment("Administration");
        admin.setRollNumber("ADMIN-0001");
        admin.setEmail("admin@abcuniversity.edu");
        admin.setPassword(passwordEncoder.encode("ChangeMe123!"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
    }
}
