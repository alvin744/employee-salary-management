package com.app.EmployeeSalaryManagement.controller;

import com.app.EmployeeSalaryManagement.helper.UserHelper;
import com.app.EmployeeSalaryManagement.message.ApiResponse;
import com.app.EmployeeSalaryManagement.model.User;
import com.app.EmployeeSalaryManagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/upload")
    public ApiResponse uploadFile(@RequestParam("file") MultipartFile file) {
        if (UserHelper.hasCSVFormat(file)) {
            return userService.save(file);
        } else {
            ApiResponse apiResponse = new ApiResponse();
            String message = "Bad input - Please upload a csv file!";
            apiResponse.setResults(null);
            apiResponse.setMessage(message);
            apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return apiResponse;
        }
    }

    @GetMapping
    public ApiResponse sortedResult(@RequestParam(required = false, defaultValue = "0.0") Double minSalary,
                                    @RequestParam(required = false, defaultValue = "4000.0") Double maxSalary,
                                    @RequestParam(required = false, defaultValue = "0") Integer offset,
                                    @RequestParam(required = false, defaultValue = "0") Integer limit,
                                    @RequestParam(required = false, defaultValue = "") String filterByName,
                                    @RequestParam(required = false, defaultValue = "id,asc") String[] sort) {
        return userService.getAllUsers(minSalary, maxSalary, offset, limit, filterByName, sort);
    }

    @GetMapping("/{id}")
    public ApiResponse getUserById(@PathVariable(name = "id") String userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public ApiResponse addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping
    public ApiResponse updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteUser(@PathVariable(name = "id") String userId) {
        return userService.deleteUserById(userId);
    }
}