-- =======================================================
-- Centurion University College ERP Database Schema
-- Complete Production Database with Tables & Sample Data
-- =======================================================

CREATE DATABASE IF NOT EXISTS college_erp;
USE college_erp;

-- 1. Users Table (Authentication & Roles)
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

-- 2. Departments Table
CREATE TABLE IF NOT EXISTS departments (
    department_id INT AUTO_INCREMENT PRIMARY KEY,
    department_code VARCHAR(50) NOT NULL UNIQUE,
    department_name VARCHAR(150) NOT NULL,
    description VARCHAR(500) NULL,
    hod_faculty_id INT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- 3. Courses Table
CREATE TABLE IF NOT EXISTS courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(50) NOT NULL UNIQUE,
    course_name VARCHAR(150) NOT NULL,
    department_id INT NOT NULL,
    duration VARCHAR(50) NULL,
    total_semesters INT NOT NULL,
    description VARCHAR(500) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_course_dept FOREIGN KEY (department_id) REFERENCES departments(department_id)
);

-- 4. Semesters Table
CREATE TABLE IF NOT EXISTS semesters (
    semester_id INT AUTO_INCREMENT PRIMARY KEY,
    semester_number INT NOT NULL,
    semester_name VARCHAR(100) NOT NULL,
    course_id INT NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_sem_course FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

-- 5. Faculty Table
CREATE TABLE IF NOT EXISTS faculty (
    faculty_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    employee_number VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NULL,
    gender VARCHAR(20) NULL,
    date_of_birth DATE NULL,
    email VARCHAR(150) NULL UNIQUE,
    phone VARCHAR(20) NULL,
    department_id INT NOT NULL,
    designation VARCHAR(100) NULL,
    qualification VARCHAR(200) NULL,
    specialization VARCHAR(200) NULL,
    joining_date DATE NULL,
    address VARCHAR(255) NULL,
    city VARCHAR(100) NULL,
    state VARCHAR(100) NULL,
    pincode VARCHAR(10) NULL,
    profile_image VARCHAR(500) NULL,
    status VARCHAR(30) DEFAULT 'ACTIVE',
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    CONSTRAINT fk_faculty_dept FOREIGN KEY (department_id) REFERENCES departments(department_id)
);

-- 6. Students Table
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
    status VARCHAR(30) DEFAULT 'ACTIVE',
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    CONSTRAINT fk_student_dept FOREIGN KEY (department_id) REFERENCES departments(department_id),
    CONSTRAINT fk_student_course FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

-- 7. Subjects Table
CREATE TABLE IF NOT EXISTS subjects (
    subject_id INT AUTO_INCREMENT PRIMARY KEY,
    subject_code VARCHAR(50) NOT NULL UNIQUE,
    subject_name VARCHAR(150) NOT NULL,
    course_id INT NOT NULL,
    semester_id INT NOT NULL,
    faculty_id INT NULL,
    credits INT NOT NULL DEFAULT 4,
    max_marks INT NOT NULL DEFAULT 100,
    pass_marks INT NOT NULL DEFAULT 40,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_subject_course FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

-- 8. Attendance Table
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    subject_id INT NOT NULL,
    faculty_id INT NOT NULL,
    attendance_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL, -- PRESENT, ABSENT, LATE
    remarks VARCHAR(255) NULL,
    CONSTRAINT fk_att_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_att_subject FOREIGN KEY (subject_id) REFERENCES subjects(subject_id)
);

-- 9. Examinations Table
CREATE TABLE IF NOT EXISTS examinations (
    exam_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_name VARCHAR(150) NOT NULL,
    exam_type VARCHAR(50) NOT NULL,
    semester_id INT NOT NULL,
    subject_id INT NOT NULL,
    exam_date DATE NOT NULL,
    start_time VARCHAR(20) NOT NULL,
    end_time VARCHAR(20) NOT NULL,
    room_number VARCHAR(50) NULL,
    max_marks INT NOT NULL DEFAULT 100
);

-- 10. Results Table
CREATE TABLE IF NOT EXISTS results (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    examination_id INT NOT NULL,
    subject_id INT NOT NULL,
    marks_obtained DECIMAL(6,2) NOT NULL,
    grade VARCHAR(10) NULL,
    grade_point DECIMAL(4,2) NULL,
    result_status VARCHAR(30) NOT NULL, -- PASS, FAIL
    CONSTRAINT fk_res_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_res_subject FOREIGN KEY (subject_id) REFERENCES subjects(subject_id)
);

-- 11. Timetables Table
CREATE TABLE IF NOT EXISTS timetables (
    timetable_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    semester_id INT NOT NULL,
    subject_id INT NOT NULL,
    faculty_id INT NOT NULL,
    day_of_week VARCHAR(20) NOT NULL, -- Monday, Tuesday, etc.
    start_time VARCHAR(20) NOT NULL,
    end_time VARCHAR(20) NOT NULL,
    room_number VARCHAR(50) NULL,
    CONSTRAINT fk_tt_course FOREIGN KEY (course_id) REFERENCES courses(course_id),
    CONSTRAINT fk_tt_subject FOREIGN KEY (subject_id) REFERENCES subjects(subject_id)
);

-- 12. Notices Table
CREATE TABLE IF NOT EXISTS notices (
    notice_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    created_by INT NOT NULL,
    target_role VARCHAR(50) DEFAULT 'ALL', -- ALL, STUDENT, FACULTY
    publish_date DATETIME NOT NULL,
    expiry_date DATETIME NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- 13. Fees Table
CREATE TABLE IF NOT EXISTS fees (
    fee_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    semester_id INT NOT NULL,
    tuition_fee DECIMAL(12,2) DEFAULT 0.00,
    examination_fee DECIMAL(12,2) DEFAULT 0.00,
    library_fee DECIMAL(12,2) DEFAULT 0.00,
    hostel_fee DECIMAL(12,2) DEFAULT 0.00,
    other_fee DECIMAL(12,2) DEFAULT 0.00,
    total_amount DECIMAL(12,2) NOT NULL,
    paid_amount DECIMAL(12,2) DEFAULT 0.00,
    due_amount DECIMAL(12,2) NOT NULL,
    status VARCHAR(30) NOT NULL, -- PAID, PARTIAL, PENDING
    CONSTRAINT fk_fee_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- 14. Fee Payments Table
CREATE TABLE IF NOT EXISTS fee_payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    fee_id INT NOT NULL,
    student_id INT NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(100) UNIQUE NULL,
    payment_date DATE NOT NULL,
    receipt_number VARCHAR(100) UNIQUE NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at DATETIME NULL,
    CONSTRAINT fk_payment_fee FOREIGN KEY (fee_id) REFERENCES fees(fee_id) ON DELETE CASCADE
);

-- 15. Enrollments Table
CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    semester_id INT NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    enrollment_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    CONSTRAINT fk_enr_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_enr_course FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

-- =======================================================
-- INITIAL SEED DATA
-- Default password for all demo accounts: Admin@123
-- SHA-256 hash: e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7
-- =======================================================

-- Users (Super Admin, Admin, Faculty, Student)
-- All default passwords saved in clear, readable format: Admin@123
INSERT IGNORE INTO users (user_id, username, password, email, role, active, created_at, updated_at) VALUES
(1, 'superadmin', 'Admin@123', 'superadmin@gmail.com', 'SUPER_ADMIN', TRUE, NOW(), NOW()),
(2, 'admin', 'Admin@123', 'admin@gmail.com', 'ADMIN', TRUE, NOW(), NOW()),
(3, 'dr_rajesh', 'Admin@123', 'rajesh@college.edu', 'FACULTY', TRUE, NOW(), NOW()),
(4, 'prof_sunita', 'Admin@123', 'sunita@college.edu', 'FACULTY', TRUE, NOW(), NOW()),
(5, 'rahul_sharma', 'Admin@123', 'rahul@gmail.com', 'STUDENT', TRUE, NOW(), NOW()),
(6, 'priya_patel', 'Admin@123', 'priya@gmail.com', 'STUDENT', TRUE, NOW(), NOW());

-- Departments
INSERT IGNORE INTO departments (department_id, department_code, department_name, description, active) VALUES
(1, 'CSE', 'Computer Science and Engineering', 'Department of Computer Science, Software Engineering & AI', TRUE),
(2, 'ECE', 'Electronics & Communication', 'Department of Electronics, Embedded Systems & IoT', TRUE),
(3, 'MECH', 'Mechanical Engineering', 'Department of Mechanical & Robotics Engineering', TRUE),
(4, 'CIVIL', 'Civil Engineering', 'Department of Civil & Structural Engineering', TRUE),
(5, 'MGMT', 'Management Studies', 'Department of Business Administration & MBA', TRUE);

-- Courses
INSERT IGNORE INTO courses (course_id, course_code, course_name, department_id, duration, total_semesters, description, active) VALUES
(1, 'BTECH-CSE', 'B.Tech in Computer Science', 1, '4 Years', 8, 'Bachelor of Technology in Computer Science & Engineering', TRUE),
(2, 'BCA', 'Bachelor of Computer Applications', 1, '3 Years', 6, 'Undergraduate degree in computer applications & web development', TRUE),
(3, 'MCA', 'Master of Computer Applications', 1, '2 Years', 4, 'Postgraduate degree in software architecture & advanced computing', TRUE),
(4, 'BTECH-ECE', 'B.Tech in Electronics & Comm.', 2, '4 Years', 8, 'Undergraduate program in modern communication networks', TRUE),
(5, 'MBA', 'Master of Business Administration', 5, '2 Years', 4, 'Graduate management program in finance, marketing & analytics', TRUE);

-- Semesters
INSERT IGNORE INTO semesters (semester_id, semester_number, semester_name, course_id, academic_year, active) VALUES
(1, 1, 'Semester 1', 1, '2025-2026', TRUE),
(2, 2, 'Semester 2', 1, '2025-2026', TRUE),
(3, 3, 'Semester 3', 1, '2025-2026', TRUE),
(4, 4, 'Semester 4', 1, '2025-2026', TRUE),
(5, 5, 'Semester 5', 1, '2025-2026', TRUE),
(6, 6, 'Semester 6', 1, '2025-2026', TRUE);

-- Faculty Members
INSERT IGNORE INTO faculty (faculty_id, user_id, employee_number, first_name, last_name, gender, date_of_birth, email, phone, department_id, designation, qualification, specialization, joining_date, address, city, state, pincode, status, created_at, updated_at) VALUES
(1, 3, 'FAC001', 'Rajesh', 'Kumar', 'Male', '1982-05-14', 'rajesh@college.edu', '9876543210', 1, 'Associate Professor & HOD', 'Ph.D in Computer Science', 'Distributed Systems & Database Engineering', '2018-07-01', '45 Green Park Avenue', 'Bhubaneswar', 'Odisha', '751024', 'ACTIVE', NOW(), NOW()),
(2, 4, 'FAC002', 'Sunita', 'Verma', 'Female', '1987-11-20', 'sunita@college.edu', '9876543211', 1, 'Assistant Professor', 'M.Tech in Software Engineering', 'Data Structures & Algorithms', '2020-08-15', '12 Lake View Enclave', 'Bhubaneswar', 'Odisha', '751024', 'ACTIVE', NOW(), NOW()),
(3, NULL, 'FAC003', 'Anil', 'Mohanty', 'Male', '1979-03-25', 'anil@college.edu', '9876543212', 2, 'Professor', 'Ph.D in VLSI Design', 'Digital Signal Processing & Microprocessors', '2015-06-10', '88 Royal Palms', 'Cuttack', 'Odisha', '753001', 'ACTIVE', NOW(), NOW());

-- Students
INSERT IGNORE INTO students (student_id, user_id, admission_number, roll_number, first_name, last_name, gender, date_of_birth, email, phone, address, city, state, pincode, department_id, course_id, semester_id, admission_date, guardian_name, guardian_phone, blood_group, status, created_at, updated_at) VALUES
(1, 5, 'ADM2024001', '24CSE0101', 'Hiralal', 'Kumar', 'Male', '2004-03-12', 'hk@gmail.com', '7970836127', 'Flat 302, Sunrise Heights', 'Bhubaneswar', 'Odisha', '751010', 1, 1, 3, '2024-07-15', 'Mahesh Sharma', '9871122334', 'O+', 'ACTIVE', NOW(), NOW()),
(2, 6, 'ADM2024002', '24CSE0102', 'Priya', 'Patel', 'Female', '2004-08-22', 'priya@gmail.com', '9123456781', 'Plot 14, Silicon Hills', 'Bhubaneswar', 'Odisha', '751024', 1, 1, 3, '2024-07-16', 'Ramesh Patel', '9871122335', 'B+', 'ACTIVE', NOW(), NOW()),
(3, NULL, 'ADM2024003', '24CSE0103', 'Amit', 'Kumar', 'Male', '2003-12-05', 'amit.kumar@gmail.com', '9123456782', 'House 5, Forest Park', 'Cuttack', 'Odisha', '753002', 1, 1, 3, '2024-07-18', 'Suresh Kumar', '9871122336', 'A+', 'ACTIVE', NOW(), NOW()),
(4, NULL, 'ADM2024004', '24BCA0201', 'Sneha', 'Reddy', 'Female', '2005-01-19', 'sneha.reddy@gmail.com', '9123456783', 'Apartment 4B, Infocity Road', 'Bhubaneswar', 'Odisha', '751024', 1, 2, 2, '2024-08-01', 'Venkatesh Reddy', '9871122337', 'AB+', 'ACTIVE', NOW(), NOW());

-- Subjects
INSERT IGNORE INTO subjects (subject_id, subject_code, subject_name, course_id, semester_id, faculty_id, credits, max_marks, pass_marks, active) VALUES
(1, 'CS301', 'Data Structures & Algorithms', 1, 3, 2, 4, 100, 40, TRUE),
(2, 'CS302', 'Database Management Systems', 1, 3, 1, 4, 100, 40, TRUE),
(3, 'CS303', 'Object-Oriented Java Programming', 1, 3, 1, 4, 100, 40, TRUE),
(4, 'CS304', 'Computer Organization & Architecture', 1, 3, 3, 3, 100, 40, TRUE),
(5, 'CS305', 'Discrete Mathematics', 1, 3, 2, 3, 100, 40, TRUE);

-- Attendance Records
INSERT IGNORE INTO attendance (attendance_id, student_id, subject_id, faculty_id, attendance_date, status, remarks) VALUES
(1, 1, 1, 2, '2026-09-01', 'PRESENT', 'Attended lecture and answered questions'),
(2, 1, 1, 2, '2026-09-03', 'PRESENT', 'Lab session completed'),
(3, 1, 1, 2, '2026-09-05', 'ABSENT', 'Medical leave'),
(4, 1, 2, 1, '2026-09-02', 'PRESENT', 'Normal attendance'),
(5, 1, 2, 1, '2026-09-04', 'PRESENT', 'SQL Query practice lab'),
(6, 1, 3, 1, '2026-09-01', 'PRESENT', 'Servlet session lecture'),
(7, 1, 3, 1, '2026-09-03', 'PRESENT', 'Hibernate ORM lab'),
(8, 2, 1, 2, '2026-09-01', 'PRESENT', 'Active participation'),
(9, 2, 1, 2, '2026-09-03', 'PRESENT', 'Lab completed'),
(10, 2, 2, 1, '2026-09-02', 'PRESENT', 'Normal attendance'),
(11, 2, 3, 1, '2026-09-01', 'PRESENT', 'Present in Java class');

-- Examinations
INSERT IGNORE INTO examinations (exam_id, exam_name, exam_type, semester_id, subject_id, exam_date, start_time, end_time, room_number, max_marks) VALUES
(1, 'Mid-Semester Examination 2026', 'MIDTERM', 3, 1, '2026-09-20', '10:00 AM', '12:00 PM', 'Hall A-101', 50),
(2, 'Mid-Semester Examination 2026', 'MIDTERM', 3, 2, '2026-09-22', '10:00 AM', '12:00 PM', 'Hall A-102', 50),
(3, 'Mid-Semester Examination 2026', 'MIDTERM', 3, 3, '2026-09-24', '10:00 AM', '12:00 PM', 'Hall A-103', 50);

-- Results
INSERT IGNORE INTO results (result_id, student_id, examination_id, subject_id, marks_obtained, grade, grade_point, result_status) VALUES
(1, 1, 1, 1, 88.50, 'A+', 9.00, 'PASS'),
(2, 1, 2, 2, 92.00, 'O', 10.00, 'PASS'),
(3, 1, 3, 3, 95.00, 'O', 10.00, 'PASS'),
(4, 2, 1, 1, 84.00, 'A', 8.50, 'PASS'),
(5, 2, 2, 2, 89.00, 'A+', 9.00, 'PASS'),
(6, 2, 3, 3, 91.50, 'O', 10.00, 'PASS');

-- Timetables (Weekly Class Schedule)
INSERT IGNORE INTO timetables (timetable_id, course_id, semester_id, subject_id, faculty_id, day_of_week, start_time, end_time, room_number) VALUES
(1, 1, 3, 1, 2, 'Monday', '09:30 AM', '10:30 AM', 'Room 201'),
(2, 1, 3, 2, 1, 'Monday', '10:45 AM', '11:45 AM', 'Room 201'),
(3, 1, 3, 3, 1, 'Tuesday', '09:30 AM', '11:30 AM', 'Computer Lab 3'),
(4, 1, 3, 4, 3, 'Wednesday', '11:45 AM', '12:45 PM', 'Room 202'),
(5, 1, 3, 5, 2, 'Thursday', '02:00 PM', '03:00 PM', 'Room 201'),
(6, 1, 3, 1, 2, 'Friday', '10:00 AM', '12:00 PM', 'Data Structures Lab');

-- Campus Notices
INSERT IGNORE INTO notices (notice_id, title, content, created_by, target_role, publish_date, expiry_date, active) VALUES
(1, 'Mid-Semester Exam Schedule Announced', 'The Mid-Semester theory and practical examinations for B.Tech Semester 3 and BCA Semester 2 will commence from 20th September 2026. Hall tickets can be downloaded from the student portal.', 1, 'ALL', NOW(), '2026-10-31 23:59:59', TRUE),
(2, 'Annual Hackathon & Tech Fest: Centurion TechPulse 2026', 'Registration is now open for TechPulse 2026! Over 50 corporate sponsors and 5 lakh INR prize pool. Form your teams and register with your department coordinator by end of this week.', 1, 'STUDENT', NOW(), '2026-11-15 23:59:59', TRUE),
(3, 'Faculty Development Workshop on Cloud & AI Systems', 'A 3-day IEEE sponsored faculty development program on Distributed Cloud Infrastructure and Machine Learning will be hosted in Seminar Hall 1 next Monday.', 1, 'FACULTY', NOW(), '2026-10-15 23:59:59', TRUE),
(4, 'Library Extended Hours for Semester Preparation', 'The Central University Library will remain open until 10:00 PM starting this Monday to assist students preparing for upcoming examinations.', 1, 'ALL', NOW(), '2026-11-01 23:59:59', TRUE);

-- Fees
INSERT IGNORE INTO fees (fee_id, student_id, semester_id, tuition_fee, examination_fee, library_fee, hostel_fee, other_fee, total_amount, paid_amount, due_amount, status) VALUES
(1, 1, 3, 45000.00, 2500.00, 1500.00, 18000.00, 1000.00, 68000.00, 68000.00, 0.00, 'PAID'),
(2, 2, 3, 45000.00, 2500.00, 1500.00, 0.00, 1000.00, 50000.00, 30000.00, 20000.00, 'PARTIAL'),
(3, 3, 3, 45000.00, 2500.00, 1500.00, 18000.00, 1000.00, 68000.00, 0.00, 68000.00, 'PENDING');
