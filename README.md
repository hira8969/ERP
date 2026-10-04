# Centurion University - College ERP System

A complete College Enterprise Resource Planning (ERP) Web Application built with **Java (Jakarta EE 6.1)**, **Hibernate ORM 7**, **MySQL**, and **HTML5 / CSS3 / JavaScript**.

---

## 1. Project Overview & Features

This ERP system automates the complete academic and administrative workflow of a university campus with role-based access for **Super Admin**, **Admin**, **Faculty**, and **Students**.

### Key Modules:
1. **Authentication & Multi-Auth Security**:
   - **HTTP Basic Authentication**: Header `Authorization: Basic <base64>` for REST API clients and tools.
   - **Session & Cookie Auth**: Standard `JSESSIONID` session cookies and 7-day persistent `remember_me` tokens for browser users.
   - **Readable Password Storage**: Saved in normal readable text (`PasswordUtil.hash(...)` stores readable string) for transparent database inspection and easy college project demonstrations.
   - **Account Provisioning Workflow**: Admin & Super Admin create Faculty and Student accounts. Once created, Faculty and Students can log in using either their Email or Username/Roll Number.
2. **Student Lifecycle Management**:
   - Complete student directory with real-time search & department filtering.
   - Student profiles with admission numbers, roll numbers, guardian contacts, and status.
   - Auto-creates `STUDENT` login account upon student admission.
3. **Faculty & Staff Management**:
   - Complete directory of professors, lecturers, and HODs.
   - Educational qualifications, designations, and departmental allocations.
   - Auto-creates `FACULTY` login account upon faculty onboarding.
4. **User & Credentials Administration (`/users.html` & `/api/users`)**:
   - Dedicated portal for Super Admin & Admin to inspect user credentials, manage roles, and create accounts.
5. **Attendance Management**:
   - Faculty can mark daily class attendance (Present, Absent, Late).
   - Automated attendance rate calculation.
   - 75% minimum university examination eligibility check.
6. **Examinations & UGC 10-Point Results**:
   - Faculty marks recording.
   - Automatic letter grading (O, A+, A, B+, B, C, F) and grade point computation.
   - SGPA and academic division calculation (Distinction, First Class, Pass).
   - Print-ready semester grade sheets.
7. **Academic Structure & Curriculum**:
   - Departments, degree programs (B.Tech, BCA, MCA, MBA), and courses.
   - Course syllabus with credit distribution and passing standards.
8. **Class Schedule & Timetables**:
   - Day-wise weekly class schedule (Mon–Fri) with subject, faculty, and room numbers.
9. **Campus Notice Board**:
   - Official university circulars, exam dates, and announcements with role-specific targeting.

---

## 2. Technology Stack

- **Backend**: Java 21, Jakarta EE 6.1 (Servlets & Filters)
- **ORM & Persistence**: Hibernate 7.1.11.Final, Jakarta Persistence 3.2.0
- **Database**: MySQL 8.0+
- **Frontend**: Clean Vanilla HTML5, CSS3, JavaScript (Fetch API)
- **Build Tool**: Apache Maven

---

## 3. Database Setup

1. Open MySQL command line or MySQL Workbench:
   ```sql
   SOURCE database/schema.sql;
   ```
2. Verify connection settings in `Backend/src/main/resources/db.properties`:
   ```properties
   db.driver=com.mysql.cj.jdbc.Driver
   db.url=jdbc:mysql://localhost:3306/college_erp?useSSL=false&serverTimezone=UTC
   db.username=root
   db.password=Hira2006@
   ```

---

## 4. Default Demo Accounts

All accounts use the readable password: `Admin@123`

| Role | Username | Email | Password | Access Level |
|---|---|---|---|---|
| **Super Admin** | `superadmin` | `superadmin@gmail.com` | `Admin@123` | Full system access, all APIs & user management |
| **Admin** | `admin` | `admin@gmail.com` | `Admin@123` | Full management (creates students, faculty, users) |
| **Faculty** | `dr_rajesh` / `FAC001` | `rajesh@college.edu` | `Admin@123` | Marks attendance, enters results, views notices |
| **Faculty** | `prof_sunita` / `FAC002` | `sunita@college.edu` | `Admin@123` | Marks attendance, enters results, views notices |
| **Student** | `rahul_sharma` / `24CSE0101` | `rahul@gmail.com` | `Admin@123` | Checks attendance, grade sheets, timetable, notices |
| **Student** | `priya_patel` / `24CSE0102` | `priya@gmail.com` | `Admin@123` | Checks attendance, grade sheets, timetable, notices |

> **Note**: Users can log in using either their **Email** or their **Username / Employee Code / Roll Number**.

---

## 5. API Reference & Authentication

All API endpoints support:
1. **HTTP Session Cookie** (browser login via `/login` or `/api/auth/login`).
2. **HTTP Basic Authentication** (pass header: `Authorization: Basic <base64(user:pass)>`).

### Core Endpoints Table:

| Method | Endpoint | Allowed Roles | Description |
|---|---|---|---|
| `POST` | `/api/auth/login` | Public | JSON login with email/username + password |
| `GET` | `/api/auth/me` | Authenticated | Current user session status |
| `POST` | `/api/auth/logout` | Authenticated | Invalidate session |
| `GET` | `/api/dashboard/stats` | Any Auth | Role-customized statistics (Admin/Faculty/Student) |
| `GET`, `POST` | `/api/users` | Admin, Super Admin | List all users or create new user account |
| `PUT`, `DELETE` | `/api/users/{id}` | Admin, Super Admin | Update or delete user account |
| `GET`, `POST` | `/api/students` | GET: All, POST: Admin | List students or create student + user login |
| `PUT`, `DELETE` | `/api/students/{id}` | Admin, Super Admin | Update or delete student |
| `GET`, `POST` | `/api/faculty` | GET: All, POST: Admin | List faculty or create faculty + user login |
| `PUT`, `DELETE` | `/api/faculty/{id}` | Admin, Super Admin | Update or delete faculty |
| `GET` | `/api/departments` | Authenticated | List academic departments |
| `GET` | `/api/courses` | Authenticated | List degree programs and courses |
| `GET`, `POST` | `/api/subjects` | Admin, Faculty | List subjects or add new subject |
| `GET`, `POST` | `/api/attendance` | Admin, Faculty, Student (GET) | Query or record attendance |
| `GET`, `POST` | `/api/results` | Admin, Faculty, Student (GET) | Query grade reports or record exam marks |
| `GET`, `POST` | `/api/timetables` | Authenticated | Weekly class timetable schedule |
| `GET`, `POST` | `/api/notices` | Authenticated | Circulars & announcements |

### Sample cURL with Basic Auth:
```bash
# Authenticate as Admin with Basic Auth
curl -u admin:Admin@123 http://localhost:8080/Backend/api/dashboard/stats

# Fetch all users with clear readable passwords
curl -u admin:Admin@123 http://localhost:8080/Backend/api/users
```

---

## 6. How to Build & Run

1. **Build WAR file**:
   ```bash
   cd Backend
   mvn clean package
   ```
2. **Deploy**:
   - Generated WAR file: `Backend/target/Backend.war`.
   - Deploy to Apache Tomcat 10.1+.
3. **Open in Browser**:
   - URL: `http://localhost:8080/Backend/` or `http://localhost:8080/`
