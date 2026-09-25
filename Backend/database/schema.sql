CREATE DATABASE IF NOT EXISTS college_erp;
USE college_erp;

CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NULL,
    updated_at DATETIME NULL
);

INSERT IGNORE INTO users (username, password, email, role, active, created_at, updated_at)
VALUES ('admin', 'e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7',
    'admin@gmail.com', 'ADMIN', TRUE, NOW(), NOW());

CREATE TABLE IF NOT EXISTS departments (
    department_id INT AUTO_INCREMENT PRIMARY KEY,
    department_code VARCHAR(50) NOT NULL UNIQUE,
    department_name VARCHAR(150) NOT NULL,
    description VARCHAR(500) NULL,
    hod_faculty_id INT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(50) NOT NULL UNIQUE,
    course_name VARCHAR(150) NOT NULL,
    department_id INT NOT NULL,
    duration VARCHAR(50) NULL,
    total_semesters INT NOT NULL,
    description VARCHAR(500) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_course_department FOREIGN KEY (department_id)
        REFERENCES departments(department_id)
);

CREATE TABLE IF NOT EXISTS students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL,
    admission_number VARCHAR(50) NOT NULL UNIQUE,
    roll_number VARCHAR(50) NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NULL,
    gender VARCHAR(20) NULL,
    date_of_birth DATE NULL,
    email VARCHAR(150) NULL UNIQUE,
    phone VARCHAR(20) NULL,
    address VARCHAR(255) NULL,
    city VARCHAR(100) NULL,
    state VARCHAR(100) NULL,
    pincode VARCHAR(10) NULL,
    department_id INT NULL,
    course_id INT NULL,
    semester_id INT NULL,
    admission_date DATE NULL,
    guardian_name VARCHAR(150) NULL,
    guardian_phone VARCHAR(20) NULL,
    blood_group VARCHAR(10) NULL,
    profile_image VARCHAR(500) NULL,
    status VARCHAR(30) NULL,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    CONSTRAINT fk_student_department FOREIGN KEY (department_id) REFERENCES departments(department_id),
    CONSTRAINT fk_student_course FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

INSERT IGNORE INTO departments (department_code, department_name, active)
VALUES ('CSE', 'Computer Science and Engineering', TRUE);

INSERT IGNORE INTO courses (course_code, course_name, department_id, duration, total_semesters, active)
SELECT 'BCA', 'Bachelor of Computer Applications', department_id, '3 Years', 6, TRUE
FROM departments WHERE department_code = 'CSE';