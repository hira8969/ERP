package com.college.erp.dao;

import com.college.erp.entity.User;

public interface UserDAO {

    User findByEmail(String email);

    void save(User user);
}