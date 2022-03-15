package com.app.EmployeeSalaryManagement.service;

import com.app.EmployeeSalaryManagement.helper.EmployeeHelper;
import com.app.EmployeeSalaryManagement.message.ApiResponse;
import com.app.EmployeeSalaryManagement.model.Employee;
import com.app.EmployeeSalaryManagement.repository.EmployeeRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The type Employee service.
 */
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    /**
     * Instantiates a new Employee service.
     *
     * @param employeeRepository the employee repository
     */
    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Save Employee through CSV file.
     *
     * @param file the file
     * @return the response entity
     * @throws ParseException
     */
    @Transactional
    public ApiResponse save(MultipartFile file) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            String message = "";
            HttpStatus status = null;
            List<Employee> employees = new ArrayList<>();
            HashMap<String, Object> objectHashMap = EmployeeHelper.csvToRecords(file.getInputStream());
            JSONObject jsonObject = new JSONObject(objectHashMap);
            if (!jsonObject.getString("message").isEmpty()) {
                apiResponse.setMessage(jsonObject.getString("message"));
                apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                return apiResponse;
            }

            JSONArray jsonArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject employeeJsonObject = jsonArray.getJSONObject(i);
                Employee employee = new Employee();
                employee.setId(employeeJsonObject.getString("id"));
                employee.setLogin(employeeJsonObject.getString("login"));
                employee.setName(employeeJsonObject.getString("name"));
                employee.setSalary(employeeJsonObject.getDouble("salary"));
                employee.setStartDate(employeeJsonObject.getString("startDate"));
                employees.add(employee);
                Employee existingEmployee = employeeRepository.findEmployeeByIdOrLogin(employee.getId(), employee.getLogin());
                if(existingEmployee != null){
                    message = "Success but no data updated.";
                    status = HttpStatus.OK;
                } else {
                    message = "Data created or uploaded";
                    status = HttpStatus.CREATED;
                }
            }
            employeeRepository.saveAll(employees);
            apiResponse.setMessage(message);
            apiResponse.setStatus(status.value());
            return apiResponse;
        } catch (IOException e) {
            apiResponse.setResults(null);
            apiResponse.setMessage(e.getMessage());
            apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return apiResponse;
        }
    }

    /**
     * Gets all employees.
     *
     * @param minSalary the min salary
     * @param maxSalary the max salary
     * @param offset    the offset
     * @param limit     the limit
     * @return the all employees
     */
    public ApiResponse getAllEmployees(Double minSalary, Double maxSalary, Integer offset, Integer limit, String filterByName, String sort) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            List<Order> orders = new ArrayList<Order>();
            String[] _sort = sort.split(",");
            orders.add(new Order(EmployeeHelper.getSortDirection(_sort[1]), _sort[0]));
            
            List<Employee> employeesList = employeeRepository.findEmployeeBySalaryBetween(minSalary, maxSalary, Sort.by(orders));
            if (employeesList.isEmpty()) {
                apiResponse.setMessage("No such employee");
                apiResponse.setResults(null);
            } else {
                if (offset > employeesList.size()) {
                    apiResponse.setMessage("We don't have that much records in our database, Kindly change the offset value");
                    apiResponse.setResults(null);
                } else if (offset < 0){
                    apiResponse.setResults(null);
                        apiResponse.setMessage("Bad input - Enter the offset value 0 or more than 0");
                        apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                        return apiResponse;
                } else {
                    List<Employee> tempList = new ArrayList<>();
                    for (int i = offset; i <= employeesList.size(); i++) {
                        try {
                            if(filterByName.equalsIgnoreCase("")){
                                tempList.add(employeesList.get(i));
                            } else {
                                if(employeesList.get(i).getName().toLowerCase().contains(filterByName.trim().toLowerCase())){
                                    tempList.add(employeesList.get(i));
                                }
                            }

                        } catch (Exception e) {
                            break;
                        }
                    }
                    List<Employee> tempList2 = new ArrayList<>();
                    if (limit > 0) {
                        for (int i = 0; i < limit; i++) {
                            tempList2.add(tempList.get(i));
                        }
                        apiResponse.setMessage("Success but no data updated");
                        apiResponse.setResults(tempList2);
                    } else if (limit == 0) {
                        apiResponse.setMessage("Success but no data updated");
                        apiResponse.setResults(tempList);
                    } else {
                        apiResponse.setResults(null);
                        apiResponse.setMessage("Bad input - Enter the limit value 0 or more than 0");
                        apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                        return apiResponse;
                    }
                }
                apiResponse.setStatus(HttpStatus.OK.value());
                return apiResponse;
            }
            apiResponse.setStatus(HttpStatus.OK.value());
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setResults(null);
            apiResponse.setMessage(e.getMessage());
            apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return apiResponse;
        }
    }

    /**
     * Gets employee by id.
     *
     * @param employeeId the employee id
     * @return the employee by id
     */
    public ApiResponse getEmployeeById(String employeeId) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            Employee employee = employeeRepository.findEmployeeById(employeeId);
            if (null != employee) {
                apiResponse.setResults(employee);
                apiResponse.setMessage("Success but no data updated");
                apiResponse.setStatus(HttpStatus.OK.value());
            } else {
                apiResponse.setResults(null);
                apiResponse.setMessage("No such employee");
                apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            }
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setResults(null);
            apiResponse.setMessage(e.getMessage());
            apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return apiResponse;
        }
    }

    /**
     * Add new Employee Service
     *
     * @param employee the employee
     * @return the api response
     */
    public ApiResponse addEmployee(Employee employee) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            if (employee.getId().equalsIgnoreCase("")) {
                apiResponse.setMessage("Invalid ID");
                apiResponse.setResults(null);
                apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            } else if (employee.getLogin().equalsIgnoreCase("")) {
                apiResponse.setMessage("Invalid Login");
                apiResponse.setResults(null);
                apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            } else if (employee.getName().equalsIgnoreCase("")) {
                apiResponse.setMessage("Invalid Name");
                apiResponse.setResults(null);
                apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            } else if (employee.getSalary().longValue() < 0) {
                apiResponse.setMessage("Invalid Salary");
                apiResponse.setResults(null);
                apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            } else if (employee.getStartDate() == null) {
                apiResponse.setMessage("Invalid Date");
                apiResponse.setResults(null);
                apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            } else {
                Employee existingEmployee = employeeRepository.findEmployeeById(employee.getId());
                if(existingEmployee != null){
                    apiResponse.setMessage("Employee ID already exists");
                    apiResponse.setResults(null);
                    apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                    return apiResponse;
                }
                existingEmployee = employeeRepository.findEmployeeByLogin(employee.getLogin());
                if(existingEmployee != null){
                    apiResponse.setMessage("Employee login not unique");
                    apiResponse.setResults(null);
                    apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                    return apiResponse;
                }
                employeeRepository.save(employee);
                apiResponse.setMessage("Successfully created");
                apiResponse.setResults(employee);
                apiResponse.setStatus(HttpStatus.CREATED.value());
            }
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setResults(null);
            apiResponse.setMessage(e.getMessage());
            apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return apiResponse;
        }
    }

    /**
     * Update the Employee Service.
     *
     * @param employee the employee
     * @return the api response
     */
    public ApiResponse updateEmployee(Employee employee) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            if (employee.getLogin().equalsIgnoreCase("")) {
                apiResponse.setMessage("Invalid Login");
                apiResponse.setResults(null);
                apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            } else if (employee.getName().equalsIgnoreCase("")) {
                apiResponse.setMessage("Invalid Name");
                apiResponse.setResults(null);
                apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            } else if (employee.getSalary().longValue() < 0) {
                apiResponse.setMessage("Invalid Salary");
                apiResponse.setResults(null);
                apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            } else if (employee.getStartDate() == null) {
                apiResponse.setMessage("Invalid Date");
                apiResponse.setResults(null);
                apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            } else {
                Employee existingEmployee = employeeRepository.findEmployeeByLogin(employee.getLogin());
                if(existingEmployee != null){
                    apiResponse.setMessage("Employee login not unique");
                    apiResponse.setResults(null);
                    apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                    return apiResponse;
                }
                existingEmployee = employeeRepository.findEmployeeById(employee.getId());
                if (null != existingEmployee) {
                    existingEmployee.setLogin(employee.getLogin());
                    existingEmployee.setName(employee.getName());
                    existingEmployee.setSalary(employee.getSalary());
                    existingEmployee.setStartDate(employee.getStartDate());
                    employeeRepository.save(existingEmployee);
                    apiResponse.setResults(existingEmployee);
                    apiResponse.setStatus(HttpStatus.CREATED.value());
                    apiResponse.setMessage("Successfully Updated");
                } else {
                    apiResponse.setResults(null);
                    apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                    apiResponse.setMessage("No such employee");
                }
            }
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setResults(null);
            apiResponse.setMessage(e.getMessage());
            apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return apiResponse;
        }
    }

    /**
     * Delete employee by id api response.
     *
     * @param employeeId the employee id
     * @return the api response
     */
    public ApiResponse deleteEmployeeById(String employeeId) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            Employee employee = employeeRepository.findEmployeeById(employeeId);
            if (null != employee) {
                employeeRepository.delete(employee);
                apiResponse.setMessage("Successfully deleted");
            } else {
                apiResponse.setMessage("No such employee");
            }
            apiResponse.setResults(null);
            apiResponse.setStatus(HttpStatus.OK.value());
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setResults(null);
            apiResponse.setMessage(e.getMessage());
            apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return apiResponse;
        }
    }

    public List<Employee> getAllEmployees() {

        return employeeRepository.findAll();
    }

    public void saveEmployee(Employee employee) {

        try {
            employeeRepository.save(employee);
        } catch (Exception e){
            e.printStackTrace();
        }
        
    }

    public Employee getEmployee(String id) {
    
        return employeeRepository.findEmployeeById(id);
    }

    public void deleteEmployee(Employee employee) {

        employeeRepository.delete(employee);
    }
}