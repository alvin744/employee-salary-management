package com.app.EmployeeSalaryManagement;

import com.app.EmployeeSalaryManagement.model.Employee;
import com.app.EmployeeSalaryManagement.repository.EmployeeRepository;
import com.app.EmployeeSalaryManagement.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Sort;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * The Employee service tests.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class EmployeeServiceTests {
    @Autowired
    private EmployeeService userService;
    @MockBean
    private EmployeeRepository userRepository;

    /**
     * Gets all users test.
     */
    @Test
    void getAllEmployeesTest() {
        Double minSalary = 0.0;
        Double maxSalary = 4000.0;
        Integer offset = 0;
        Integer limit = 1;
        List<Order> orders = new ArrayList<Order>();
        orders.add(new Order(Sort.Direction.ASC, "ID"));
        String filterByName = "";
        String sort = "ID, ASC";
        when(userRepository.findEmployeeBySalaryBetween(minSalary, maxSalary, Sort.by(orders))).thenReturn(Stream.of(new Employee("e0001", "hpotter", "Harry Potter", 1234.00, ("2001-11-19"))).collect(Collectors.toList()));
        assertEquals(200, userService.getAllEmployees(minSalary, maxSalary, offset, limit, filterByName, sort).getStatus());
    }

    /**
     * Gets users by id test.
     */
    @Test
    void getEmployeesByIdTest() {
        String id = "e0001";
        Employee user = new Employee("e0001", "hpotter", "Harry Potter", 1234.00, ("2001-11-19"));
        when(userRepository.findEmployeeById(id)).thenReturn(user);
        assertEquals(user, userService.getEmployeeById(id).getResults());
    }

    /**
     * Save user test.
     */
    @Test
    void saveEmployeeTest() {
        Employee user = new Employee("e0001", "hpotter", "Harry Potter", 1234.00, ("2001-11-19"));
        when(userRepository.save(user)).thenReturn(user);
        assertEquals(user, userService.addEmployee(user).getResults());
    }

    /**
     * Delete user by id test.
     */
    @Test
    void deleteEmployeeByIdTest() {
        String id = "e0001";
        Employee user = new Employee("e0001", "hpotter", "Harry Potter", 1234.00, ("2001-11-19"));
        when(userRepository.getById(id)).thenReturn(user);
        assertEquals(200, userService.deleteEmployeeById(user.getId()).getStatus());
    }

    /**
     * Update user test.
     */
    @Test
    void updateEmployeeTest() {
        Employee oldEmployee = new Employee("e0001", "hpotter", "Harry Potter", 1234.00, ("2001-11-19"));
        Employee updatedEmployee = new Employee("e0001", "hpotter", "Harry Potter Updated", 1234.00, ("2001-11-19"));
        when(userRepository.findEmployeeById(updatedEmployee.getId())).thenReturn(oldEmployee);
        assertEquals(201, userService.updateEmployee(updatedEmployee).getStatus());
    }

}