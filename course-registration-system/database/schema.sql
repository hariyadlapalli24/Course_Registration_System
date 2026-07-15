-- Reference schema for the Course Registration System.
--
-- You do NOT need to run this by hand: with spring.jpa.hibernate.ddl-auto=update
-- in application.properties, Spring Boot/Hibernate creates and updates these
-- tables automatically from the @Entity classes on startup, and
-- config/DataSeeder.java inserts the sample courses and bootstrap admin.
--
-- This file is provided so you can inspect the resulting structure, or run
-- it manually if you'd rather manage the schema yourself (in which case set
-- spring.jpa.hibernate.ddl-auto=validate or none).

CREATE DATABASE IF NOT EXISTS course_registration;
USE course_registration;

CREATE TABLE IF NOT EXISTS users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    department    VARCHAR(255) NOT NULL,
    roll_number   VARCHAR(100) NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'USER'
);

CREATE TABLE IF NOT EXISTS courses (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_code   VARCHAR(50)  NOT NULL UNIQUE,
    course_name   VARCHAR(255) NOT NULL,
    description   VARCHAR(1000)
);

CREATE TABLE IF NOT EXISTS enrollments (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    course_id     BIGINT NOT NULL,
    enrolled_at   DATETIME NOT NULL,
    CONSTRAINT fk_enrollment_user   FOREIGN KEY (user_id)   REFERENCES users(id),
    CONSTRAINT fk_enrollment_course FOREIGN KEY (course_id) REFERENCES courses(id),
    CONSTRAINT uq_user_course UNIQUE (user_id, course_id)
);

-- Sample course catalog (this also gets auto-seeded by DataSeeder.java on
-- first boot, so only run these INSERTs if you're managing the schema by
-- hand and skipped the seeder).
INSERT INTO courses (course_code, course_name, description) VALUES
    ('CS101', 'Introduction to Computer Science', 'Fundamentals of programming, algorithms, and problem solving.'),
    ('CS204', 'Data Structures & Algorithms', 'Core data structures, complexity analysis, and algorithm design.'),
    ('CS310', 'Database Management Systems', 'Relational design, SQL, normalization, and transactions.'),
    ('CS415', 'Cloud Computing', 'Distributed systems, virtualization, and cloud service models.'),
    ('MA201', 'Discrete Mathematics', 'Logic, set theory, combinatorics, and graph theory.'),
    ('EN110', 'Technical Communication', 'Writing and presenting technical material clearly.');
