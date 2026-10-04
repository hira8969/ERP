package com.college.erp.dao;

import com.college.erp.entity.User;
import java.util.List;

public interface UserDAO {

    User findByEmail(String email);

    User findById(int userId);

    User findByUsername(String username);

    List<User> findAll();

    void save(User user);

    void update(User user);

    void delete(int userId);
}