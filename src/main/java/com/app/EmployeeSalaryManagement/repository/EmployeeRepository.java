package com.app.EmployeeSalaryManagement.repository;

import com.app.EmployeeSalaryManagement.model.Employee;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    List<Employee> findEmployeeBySalaryBetween(Double minSalary, Double maxSalary, Sort sort);

    Employee findEmployeeById(String id);

    Employee findEmployeeByLogin(String login);

    Employee findEmployeeByIdOrLogin(String id, String login);

}
