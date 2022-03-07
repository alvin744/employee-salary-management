package com.app.EmployeeSalaryManagement.repository;

import com.app.EmployeeSalaryManagement.model.User;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    List<User> findUserBySalaryBetween(Double minSalary, Double maxSalary, Sort sort);

    User findUserById(String id);

    User findUserByLogin(String login);

    User findUserByIdOrLogin(String id, String login);

}
