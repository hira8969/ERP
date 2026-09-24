College-ERP/
│
├── Backend/
│   ├── pom.xml
│   │
│   └── src/
│       └── main/
│           │
│           ├── java/
│           │   └── com/
│           │       └── college/
│           │           └── erp/
│           │               │
│           │               ├── controller/
│           │               │   ├── LoginServlet.java
│           │               │   ├── LogoutServlet.java
│           │               │   │
│           │               │   ├── student/
│           │               │   │   ├── StudentDashboardServlet.java
│           │               │   │   ├── StudentProfileServlet.java
│           │               │   │   ├── StudentAttendanceServlet.java
│           │               │   │   └── StudentResultServlet.java
│           │               │   │
│           │               │   ├── admin/
│           │               │   │   ├── AdminDashboardServlet.java
│           │               │   │   ├── ManageStudentServlet.java
│           │               │   │   ├── ManageFacultyServlet.java
│           │               │   │   └── ManageCourseServlet.java
│           │               │   │
│           │               │   └── faculty/
│           │               │       ├── FacultyDashboardServlet.java
│           │               │       ├── MarkAttendanceServlet.java
│           │               │       └── UploadResultServlet.java
│           │               │
│           │               ├── service/
│           │               │   ├── AuthService.java
│           │               │   ├── StudentService.java
│           │               │   ├── FacultyService.java
│           │               │   ├── CourseService.java
│           │               │   ├── AttendanceService.java
│           │               │   └── ResultService.java
│           │               │
│           │               ├── dao/
│           │               │   ├── UserDAO.java
│           │               │   ├── StudentDAO.java
│           │               │   ├── FacultyDAO.java
│           │               │   ├── CourseDAO.java
│           │               │   ├── AttendanceDAO.java
│           │               │   └── ResultDAO.java
│           │               │
│           │               ├── dao/
│           │               │   └── impl/
│           │               │       ├── UserDAOImpl.java
│           │               │       ├── StudentDAOImpl.java
│           │               │       ├── FacultyDAOImpl.java
│           │               │       └── CourseDAOImpl.java
│           │               │
│           │               ├── model/
│           │               │   ├── User.java
│           │               │   ├── Student.java
│           │               │   ├── Faculty.java
│           │               │   ├── Course.java
│           │               │   ├── Attendance.java
│           │               │   └── Result.java
│           │               │
│           │               ├── dto/
│           │               │   ├── LoginRequest.java
│           │               │   ├── StudentDTO.java
│           │               │   └── DashboardDTO.java
│           │               │
│           │               ├── util/
│           │               │   ├── DBConnection.java
│           │               │   ├── PasswordUtil.java
│           │               │   ├── ValidationUtil.java
│           │               │   └── AppConstants.java
│           │               │
│           │               ├── filter/
│           │               │   ├── AuthenticationFilter.java
│           │               │   └── AuthorizationFilter.java
│           │               │
│           │               ├── exception/
│           │               │   ├── DAOException.java
│           │               │   └── ServiceException.java
│           │               │
│           │               └── listener/
│           │                   └── AppContextListener.java
│           │
│           ├── resources/
│           │   ├── db.properties
│           │   └── log4j2.xml
│           │
│           └── webapp/
│               │
│               ├── WEB-INF/
│               │   ├── web.xml
│               │   │
│               │   └── views/
│               │       │
│               │       ├── auth/
│               │       │   ├── login.jsp
│               │       │   └── forgot-password.jsp
│               │       │
│               │       ├── student/
│               │       │   ├── dashboard.jsp
│               │       │   ├── profile.jsp
│               │       │   ├── attendance.jsp
│               │       │   ├── results.jsp
│               │       │   └── timetable.jsp
│               │       │
│               │       ├── admin/
│               │       │   ├── dashboard.jsp
│               │       │   ├── students.jsp
│               │       │   ├── faculty.jsp
│               │       │   ├── courses.jsp
│               │       │   └── reports.jsp
│               │       │
│               │       ├── faculty/
│               │       │   ├── dashboard.jsp
│               │       │   ├── attendance.jsp
│               │       │   └── results.jsp
│               │       │
│               │       └── error/
│               │           ├── 404.jsp
│               │           ├── 500.jsp
│               │           └── access-denied.jsp
│               │
│               ├── assets/
│               │   ├── css/
│               │   │   ├── global.css
│               │   │   ├── auth.css
│               │   │   ├── dashboard.css
│               │   │   └── responsive.css
│               │   │
│               │   ├── js/
│               │   │   ├── app.js
│               │   │   ├── validation.js
│               │   │   ├── student.js
│               │   │   └── admin.js
│               │   │
│               │   └── images/
│               │       ├── logo.png
│               │       └── icons/
│               │
│               └── index.jsp
│
├── database/
│   ├── schema.sql
│   ├── seed.sql
│   └── procedures.sql
│
├── docs/
│   ├── SRS.md
│   ├── ER-Diagram.png
│   ├── Architecture.png
│   └── API-Documentation.md
│
├── .gitignore
├── README.md
└── LICENSE