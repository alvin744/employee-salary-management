package com.app.EmployeeSalaryManagement.controller;

import com.app.EmployeeSalaryManagement.helper.EmployeeHelper;
import com.app.EmployeeSalaryManagement.message.ApiResponse;
import com.app.EmployeeSalaryManagement.message.ResponseMessage;
import com.app.EmployeeSalaryManagement.model.Employee;
import com.app.EmployeeSalaryManagement.service.EmployeeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin("http://localhost:8080")
@Controller
public class EmployeeController {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/users/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        ApiResponse apiResponse = new ApiResponse();
        if (EmployeeHelper.hasCSVFormat(file)) {
            apiResponse = employeeService.save(file);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(apiResponse.getMessage()));
        } else {
            String message = "Bad input - Please upload a csv file!";
            apiResponse.setResults(null);
            apiResponse.setMessage(message);
            apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(new ResponseMessage(message));
        }
    }

    @GetMapping
    public ApiResponse sortedResult(@RequestParam(required = false, defaultValue = "0.0") Double minSalary,
                                    @RequestParam(required = false, defaultValue = "4000.0") Double maxSalary,
                                    @RequestParam(required = false, defaultValue = "0") Integer offset,
                                    @RequestParam(required = false, defaultValue = "0") Integer limit,
                                    @RequestParam(required = false, defaultValue = "") String filterByName,
                                    @RequestParam(required = false, defaultValue = "id,asc") String sort) {
        return employeeService.getAllEmployees(minSalary, maxSalary, offset, limit, filterByName, sort);
    }

    @GetMapping("/{id}")
    public ApiResponse getEmployeeById(@PathVariable(name = "id") String employeeId) {
        return employeeService.getEmployeeById(employeeId);
    }

    @PostMapping
    public ApiResponse addEmployee(@RequestBody Employee employee) {
        return employeeService.addEmployee(employee);
    }

    @PutMapping
    public ApiResponse updateEmployee(@RequestBody Employee employee) {
        return employeeService.updateEmployee(employee);
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteEmployee(@PathVariable(name = "id") String employeeId) {
        return employeeService.deleteEmployeeById(employeeId);
    }

    //API for the web
    @GetMapping("/index")
    public String showEmployeeList(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "index";
    }

    @GetMapping("/signup")
    public String showSignUpForm(Employee employees) {
        return "add-employee";
    }

    @PostMapping("/addemployee")
    public String addEmployee(@Validated Employee employees, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-employee";
        }
        
        employeeService.saveEmployee(employees);
        
        return "redirect:/index";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") String id, Model model) {

        model.addAttribute("employee", employeeService.getEmployee(id));

        return "update-employee";
    }

    @PostMapping("/update/{id}")
    public String updateEmployee(@PathVariable("id") String id, @Validated Employee employee, BindingResult result, Model model) {
        if (result.hasErrors()) {
            employee.setId(id);
            return "update-employee";
        }

        employeeService.saveEmployee(employee);
        
        return "redirect:/index";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable("id") String id, Model model) {
        Employee employee = employeeService.getEmployee(id);
        employeeService.deleteEmployee(employee);
        
        return "redirect:/index";
    }
}