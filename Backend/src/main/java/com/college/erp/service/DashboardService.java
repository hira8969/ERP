package com.college.erp.service;

import java.util.Map;

public interface DashboardService {

    Map<String, Object> getAdminStatistics();

    Map<String, Object> getFacultyStatistics(int userId);

    Map<String, Object> getStudentStatistics(int userId);
}
