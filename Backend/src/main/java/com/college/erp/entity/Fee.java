package com.college.erp.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "fees")
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fee_id")
    private int feeId;

    @Column(name = "student_id", nullable = false)
    private int studentId;

    @Column(name = "semester_id", nullable = false)
    private int semesterId;

    @Column(name = "tuition_fee", precision = 12, scale = 2)
    private BigDecimal tuitionFee;

    @Column(name = "examination_fee", precision = 12, scale = 2)
    private BigDecimal examinationFee;

    @Column(name = "library_fee", precision = 12, scale = 2)
    private BigDecimal libraryFee;

    @Column(name = "hostel_fee", precision = 12, scale = 2)
    private BigDecimal hostelFee;

    @Column(name = "other_fee", precision = 12, scale = 2)
    private BigDecimal otherFee;

    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "paid_amount", precision = 12, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "due_amount", precision = 12, scale = 2)
    private BigDecimal dueAmount;

    @Column(name = "status", nullable = false, length = 30)
    private String status;


    // =========================
    // Default Constructor
    // =========================

    public Fee() {
    }


    // =========================
    // Getters & Setters
    // =========================

    public int getFeeId() {
        return feeId;
    }

    public void setFeeId(int feeId) {
        this.feeId = feeId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public BigDecimal getTuitionFee() {
        return tuitionFee;
    }

    public void setTuitionFee(BigDecimal tuitionFee) {
        this.tuitionFee = tuitionFee;
    }

    public BigDecimal getExaminationFee() {
        return examinationFee;
    }

    public void setExaminationFee(BigDecimal examinationFee) {
        this.examinationFee = examinationFee;
    }

    public BigDecimal getLibraryFee() {
        return libraryFee;
    }

    public void setLibraryFee(BigDecimal libraryFee) {
        this.libraryFee = libraryFee;
    }

    public BigDecimal getHostelFee() {
        return hostelFee;
    }

    public void setHostelFee(BigDecimal hostelFee) {
        this.hostelFee = hostelFee;
    }

    public BigDecimal getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(BigDecimal otherFee) {
        this.otherFee = otherFee;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getDueAmount() {
        return dueAmount;
    }

    public void setDueAmount(BigDecimal dueAmount) {
        this.dueAmount = dueAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}