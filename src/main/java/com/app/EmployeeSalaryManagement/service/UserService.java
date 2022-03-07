package com.app.EmployeeSalaryManagement.service;

import com.app.EmployeeSalaryManagement.helper.UserHelper;
import com.app.EmployeeSalaryManagement.message.ApiResponse;
import com.app.EmployeeSalaryManagement.model.User;
import com.app.EmployeeSalaryManagement.repository.UserRepository;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The type User service.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * Instantiates a new User service.
     *
     * @param userRepository the user repository
     */
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
            List<User> users = new ArrayList<>();
            HashMap<String, Object> objectHashMap = UserHelper.csvToRecords(file.getInputStream());
            JSONObject jsonObject = new JSONObject(objectHashMap);
            if (!jsonObject.getString("message").isEmpty()) {
                apiResponse.setMessage(jsonObject.getString("message"));
                apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                return apiResponse;
            }

            JSONArray jsonArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject userJsonObject = jsonArray.getJSONObject(i);
                User user = new User();
                user.setId(userJsonObject.getString("id"));
                user.setLogin(userJsonObject.getString("login"));
                user.setName(userJsonObject.getString("name"));
                user.setSalary(userJsonObject.getDouble("salary"));
                user.setStartDate(LocalDate.parse(userJsonObject.getString("startDate")));
                users.add(user);
                User existingUser = userRepository.findUserByIdOrLogin(user.getId(), user.getLogin());
                if(existingUser != null){
                    message = "Success but no data updated.";
                    status = HttpStatus.OK;
                } else {
                    message = "Data created or uploaded";
                    status = HttpStatus.CREATED;
                }
            }
            userRepository.saveAll(users);
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
     * Gets all users.
     *
     * @param minSalary the min salary
     * @param maxSalary the max salary
     * @param offset    the offset
     * @param limit     the limit
     * @return the all users
     */
    public ApiResponse getAllUsers(Double minSalary, Double maxSalary, Integer offset, Integer limit, String filterByName, String[] sort) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            List<Order> orders = new ArrayList<Order>();
            if (sort[0].contains(",")) {
              // will sort more than 2 fields
              // sortOrder="field, direction"
              for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Order(UserHelper.getSortDirection(_sort[1]), _sort[0]));
              }
            } else {
              // sort=[field, direction]
              orders.add(new Order(UserHelper.getSortDirection(sort[1]), sort[0]));
            } 
            List<User> usersList = userRepository.findUserBySalaryBetween(minSalary, maxSalary, Sort.by(orders));
            if (usersList.isEmpty()) {
                apiResponse.setMessage("No such employee");
                apiResponse.setResults(null);
            } else {
                if (offset > usersList.size()) {
                    apiResponse.setMessage("We don't have that much records in our database, Kindly change the offset value");
                    apiResponse.setResults(null);
                } else if (offset < 0){
                    apiResponse.setResults(null);
                        apiResponse.setMessage("Bad input - Enter the offset value 0 or more than 0");
                        apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                        return apiResponse;
                } else {
                    List<User> tempList = new ArrayList<>();
                    for (int i = offset; i <= usersList.size(); i++) {
                        try {
                            if(filterByName.equalsIgnoreCase("")){
                                tempList.add(usersList.get(i));
                            } else {
                                if(usersList.get(i).getName().toLowerCase().contains(filterByName.trim().toLowerCase())){
                                    tempList.add(usersList.get(i));
                                }
                            }

                        } catch (Exception e) {
                            break;
                        }
                    }
                    List<User> tempList2 = new ArrayList<>();
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
     * Gets user by id.
     *
     * @param userId the user id
     * @return the user by id
     */
    public ApiResponse getUserById(String userId) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            User user = userRepository.findUserById(userId);
            if (null != user) {
                apiResponse.setResults(user);
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
     * Add new User Service
     *
     * @param user the user
     * @return the api response
     */
    public ApiResponse addUser(User user) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            if (user.getId().equalsIgnoreCase("")) {
                apiResponse.setMessage("Invalid ID");
                apiResponse.setResults(null);
                apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            } else if (user.getLogin().equalsIgnoreCase("")) {
                apiResponse.setMessage("Invalid Login");
                apiResponse.setResults(null);
                apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            } else if (user.getName().equalsIgnoreCase("")) {
                apiResponse.setMessage("Invalid Name");
                apiResponse.setResults(null);
                apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            } else if (user.getSalary().longValue() < 0) {
                apiResponse.setMessage("Invalid Salary");
                apiResponse.setResults(null);
                apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            } else if (user.getStartDate() == null) {
                apiResponse.setMessage("Invalid Date");
                apiResponse.setResults(null);
                apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            } else {
                User existingUser = userRepository.findUserById(user.getId());
                if(existingUser != null){
                    apiResponse.setMessage("Employee ID already exists");
                    apiResponse.setResults(null);
                    apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                    return apiResponse;
                }
                existingUser = userRepository.findUserByLogin(user.getLogin());
                if(existingUser != null){
                    apiResponse.setMessage("Employee login not unique");
                    apiResponse.setResults(null);
                    apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                    return apiResponse;
                }
                userRepository.save(user);
                apiResponse.setMessage("Successfully created");
                apiResponse.setResults(user);
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
     * Update the User Service.
     *
     * @param user the user
     * @return the api response
     */
    public ApiResponse updateUser(User user) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            if (user.getLogin().equalsIgnoreCase("")) {
                apiResponse.setMessage("Invalid Login");
                apiResponse.setResults(null);
                apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            } else if (user.getName().equalsIgnoreCase("")) {
                apiResponse.setMessage("Invalid Name");
                apiResponse.setResults(null);
                apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            } else if (user.getSalary().longValue() < 0) {
                apiResponse.setMessage("Invalid Salary");
                apiResponse.setResults(null);
                apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            } else if (user.getStartDate() == null) {
                apiResponse.setMessage("Invalid Date");
                apiResponse.setResults(null);
                apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            } else {
                User existingUser = userRepository.findUserByLogin(user.getLogin());
                if(existingUser != null){
                    apiResponse.setMessage("Employee login not unique");
                    apiResponse.setResults(null);
                    apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                    return apiResponse;
                }
                existingUser = userRepository.findUserById(user.getId());
                if (null != existingUser) {
                    existingUser.setLogin(user.getLogin());
                    existingUser.setName(user.getName());
                    existingUser.setSalary(user.getSalary());
                    existingUser.setStartDate(user.getStartDate());
                    userRepository.save(existingUser);
                    apiResponse.setResults(existingUser);
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
     * Delete user by id api response.
     *
     * @param userId the user id
     * @return the api response
     */
    public ApiResponse deleteUserById(String userId) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            User user = userRepository.findUserById(userId);
            if (null != user) {
                userRepository.delete(user);
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
}
