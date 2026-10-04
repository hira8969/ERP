package com.college.erp.dao;

import com.college.erp.entity.Fee;
import java.util.List;

public interface FeeDAO {

    List<Fee> getFeesByStudent(int studentId);

    List<Fee> getAllFees();

    Fee getFeeById(int feeId);

    void saveFee(Fee fee);

    void updateFee(Fee fee);
}
