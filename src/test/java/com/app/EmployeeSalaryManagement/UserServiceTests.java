package com.app.EmployeeSalaryManagement;

import com.app.EmployeeSalaryManagement.model.User;
import com.app.EmployeeSalaryManagement.repository.UserRepository;
import com.app.EmployeeSalaryManagement.service.UserService;
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
 * The User service tests.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class UserServiceTests {
    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;

    /**
     * Gets all users test.
     */
    @Test
    void getAllUsersTest() {
        Double minSalary = 0.0;
        Double maxSalary = 4000.0;
        Integer offset = 0;
        Integer limit = 1;
        List<Order> orders = new ArrayList<Order>();
        orders.add(new Order(Sort.Direction.ASC, "ID"));
        String filterByName = "";
        String[] sort =  {"ID", "ASC"};
        when(userRepository.findUserBySalaryBetween(minSalary, maxSalary, Sort.by(orders))).thenReturn(Stream.of(new User("e0001", "hpotter", "Harry Potter", 1234.00, LocalDate.parse("2001-11-19"))).collect(Collectors.toList()));
        assertEquals(200, userService.getAllUsers(minSalary, maxSalary, offset, limit, filterByName, sort).getStatus());
    }

    /**
     * Gets users by id test.
     */
    @Test
    void getUsersByIdTest() {
        String id = "e0001";
        User user = new User("e0001", "hpotter", "Harry Potter", 1234.00, LocalDate.parse("2001-11-19"));
        when(userRepository.findUserById(id)).thenReturn(user);
        assertEquals(user, userService.getUserById(id).getResults());
    }

    /**
     * Save user test.
     */
    @Test
    void saveUserTest() {
        User user = new User("e0001", "hpotter", "Harry Potter", 1234.00, LocalDate.parse("2001-11-19"));
        when(userRepository.save(user)).thenReturn(user);
        assertEquals(user, userService.addUser(user).getResults());
    }

    /**
     * Delete user by id test.
     */
    @Test
    void deleteUserByIdTest() {
        String id = "e0001";
        User user = new User("e0001", "hpotter", "Harry Potter", 1234.00, LocalDate.parse("2001-11-19"));
        when(userRepository.getById(id)).thenReturn(user);
        assertEquals(200, userService.deleteUserById(user.getId()).getStatus());
    }

    /**
     * Update user test.
     */
    @Test
    void updateUserTest() {
        User oldUser = new User("e0001", "hpotter", "Harry Potter", 1234.00, LocalDate.parse("2001-11-19"));
        User updatedUser = new User("e0001", "hpotter", "Harry Potter Updated", 1234.00, LocalDate.parse("2001-11-19"));
        when(userRepository.findUserById(updatedUser.getId())).thenReturn(oldUser);
        assertEquals(201, userService.updateUser(updatedUser).getStatus());
    }

}
