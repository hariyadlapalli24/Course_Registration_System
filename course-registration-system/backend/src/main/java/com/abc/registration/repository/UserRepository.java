package com.abc.registration.repository;

import com.abc.registration.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByRollNumber(String rollNumber);

    // Used when editing a user: makes sure the new email/roll number isn't
    // already taken by a *different* user (the user's own row shouldn't
    // trigger a conflict against itself).
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByRollNumberAndIdNot(String rollNumber, Long id);
}
