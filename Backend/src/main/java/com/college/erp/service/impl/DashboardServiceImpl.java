package com.college.erp.service.impl;

import com.college.erp.dao.*;
import com.college.erp.dao.impl.*;
import com.college.erp.entity.Faculty;
import com.college.erp.entity.Notice;
import com.college.erp.entity.Student;
import com.college.erp.service.AttendanceService;
import com.college.erp.service.DashboardService;
import com.college.erp.service.ResultService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardServiceImpl implements DashboardService {

    private final StudentDAO studentDAO = new StudentDAOImpl();
    private final FacultyDAO facultyDAO = new FacultyDAOImpl();
    private final CourseDAO courseDAO = new CourseDAOImpl();
    private final DepartmentDAO departmentDAO = new DepartmentDAOImpl();
    private final SubjectDAO subjectDAO = new SubjectDAOImpl();
    private final NoticeDAO noticeDAO = new NoticeDAOImpl();
    private final AttendanceService attendanceService = new AttendanceServiceImpl();
    private final ResultService resultService = new ResultServiceImpl();

    @Override
    public Map<String, Object> getAdminStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", studentDAO.getAllStudents().size());
        stats.put("totalFaculty", facultyDAO.getAllFaculty().size());
        stats.put("totalCourses", courseDAO.findAll().size());
        stats.put("totalDepartments", departmentDAO.findAll().size());
        stats.put("totalSubjects", subjectDAO.getAllSubjects().size());
        stats.put("recentNotices", noticeDAO.getAllNotices().size());
        return stats;
    }

    @Override
    public Map<String, Object> getFacultyStatistics(int userId) {
        Map<String, Object> stats = new HashMap<>();
        Faculty faculty = facultyDAO.getFacultyByUserId(userId);
        int facultyId = (faculty != null) ? faculty.getFacultyId() : 1;
        int deptId = (faculty != null) ? faculty.getDepartmentId() : 1;

        stats.put("facultyName", (faculty != null) ? (faculty.getFirstName() + " " + (faculty.getLastName() != null ? faculty.getLastName() : "")) : "Faculty Member");
        stats.put("designation", (faculty != null) ? faculty.getDesignation() : "Professor");
        stats.put("mySubjectsCount", subjectDAO.getSubjectsByFaculty(facultyId).size());
        stats.put("departmentStudentsCount", studentDAO.getAllStudents().stream().filter(s -> s.getDepartmentId() == deptId).count());
        stats.put("recentNotices", noticeDAO.getNoticesByRole("FACULTY").size());
        return stats;
    }

    @Override
    public Map<String, Object> getStudentStatistics(int userId) {
        Map<String, Object> stats = new HashMap<>();
        List<Student> students = studentDAO.getAllStudents();
        Student currentStudent = students.stream()
                .filter(s -> s.getUserId() == userId)
                .findFirst()
                .orElse(students.isEmpty() ? null : students.get(0));

        if (currentStudent != null) {
            AttendanceService.AttendanceSummary attSummary = attendanceService.getStudentAttendanceSummary(currentStudent.getStudentId());
            ResultService.StudentGradeReport gradeReport = resultService.getStudentGradeReport(currentStudent.getStudentId());

            stats.put("studentId", currentStudent.getStudentId());
            stats.put("studentName", currentStudent.getFirstName() + " " + (currentStudent.getLastName() != null ? currentStudent.getLastName() : ""));
            stats.put("rollNumber", currentStudent.getRollNumber() != null ? currentStudent.getRollNumber() : "");
            stats.put("admissionNumber", currentStudent.getAdmissionNumber() != null ? currentStudent.getAdmissionNumber() : "");
            stats.put("attendancePercentage", attSummary.getPercentage());
            stats.put("attendanceEligible", attSummary.isEligibleForExam());
            stats.put("totalClasses", attSummary.getTotalClasses());
            stats.put("attendedClasses", attSummary.getPresentClasses());
            stats.put("sgpa", gradeReport.getSgpa());
            stats.put("overallStatus", gradeReport.getOverallStatus());
            stats.put("completedExams", gradeReport.getResults().size());

            com.college.erp.entity.Course course = (currentStudent.getCourseId() > 0) ? courseDAO.findById(currentStudent.getCourseId()) : null;
            com.college.erp.entity.Department dept = (currentStudent.getDepartmentId() > 0) ? departmentDAO.findById(currentStudent.getDepartmentId()) : null;

            stats.put("courseId", currentStudent.getCourseId());
            stats.put("semesterId", currentStudent.getSemesterId());
            stats.put("courseName", course != null ? course.getCourseName() : "Enrolled Academic Course");
            stats.put("courseCode", course != null ? course.getCourseCode() : "N/A");
            stats.put("departmentName", dept != null ? dept.getDepartmentName() : "Academic Department");

            List<com.college.erp.entity.Subject> subjects = subjectDAO.getSubjectsByCourseAndSemester(currentStudent.getCourseId(), currentStudent.getSemesterId());
            if (subjects == null || subjects.isEmpty()) {
                subjects = subjectDAO.getAllSubjects().stream()
                        .filter(s -> s.getCourseId() == currentStudent.getCourseId())
                        .toList();
            }
            if (subjects.isEmpty()) {
                subjects = subjectDAO.getAllSubjects();
            }

            java.util.List<Map<String, Object>> subjectList = new java.util.ArrayList<>();
            java.util.Set<String> distinctFaculty = new java.util.HashSet<>();

            for (com.college.erp.entity.Subject sub : subjects) {
                Map<String, Object> subMap = new HashMap<>();
                subMap.put("subjectId", sub.getSubjectId());
                subMap.put("subjectCode", sub.getSubjectCode());
                subMap.put("subjectName", sub.getSubjectName());
                subMap.put("credits", sub.getCredits());
                subMap.put("maxMarks", sub.getMaxMarks());
                subMap.put("passMarks", sub.getPassMarks());
                subMap.put("facultyId", sub.getFacultyId());

                Faculty fac = (sub.getFacultyId() > 0) ? facultyDAO.getFacultyById(sub.getFacultyId()) : null;
                if (fac != null) {
                    String fn = fac.getFirstName() + (fac.getLastName() != null && !fac.getLastName().isBlank() ? " " + fac.getLastName() : "");
                    subMap.put("facultyName", fn.trim());
                    subMap.put("facultyEmail", fac.getEmail() != null ? fac.getEmail() : "academic@college.edu");
                    subMap.put("facultyPhone", fac.getPhone() != null ? fac.getPhone() : "");
                    subMap.put("designation", fac.getDesignation() != null ? fac.getDesignation() : "Professor");
                    subMap.put("employeeNumber", fac.getEmployeeNumber() != null ? fac.getEmployeeNumber() : "");
                    distinctFaculty.add(fn.trim());
                } else {
                    subMap.put("facultyName", "Department Faculty");
                    subMap.put("facultyEmail", "academic@college.edu");
                    subMap.put("facultyPhone", "N/A");
                    subMap.put("designation", "Course Instructor");
                    subMap.put("employeeNumber", "FAC-TBA");
                    distinctFaculty.add("Department Faculty");
                }
                subjectList.add(subMap);
            }

            stats.put("courseSubjects", subjectList);
            stats.put("totalSubjects", subjectList.size());
            stats.put("totalCourseFaculty", distinctFaculty.size());
        } else {
            stats.put("studentName", "Student");
            stats.put("attendancePercentage", 0.0);
            stats.put("attendanceEligible", true);
            stats.put("sgpa", 0.0);
            stats.put("overallStatus", "N/A");
            stats.put("courseSubjects", java.util.Collections.emptyList());
            stats.put("totalSubjects", 0);
            stats.put("totalCourseFaculty", 0);
        }
        stats.put("recentNotices", noticeDAO.getNoticesByRole("STUDENT").size());
        return stats;
    }
}
