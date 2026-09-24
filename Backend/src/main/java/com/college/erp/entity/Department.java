package com.college.erp.entity;

public class Department {

    private int departmentId;
    private String departmentCode;
    private String departmentName;
    private String description;
    private int hodFacultyId;
    private boolean active;

    public Department() {
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getHodFacultyId() {
        return hodFacultyId;
    }

    public void setHodFacultyId(int hodFacultyId) {
        this.hodFacultyId = hodFacultyId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}