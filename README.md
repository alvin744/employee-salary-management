## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Setup](#setup)
* [Unit Testing](#unit-testing)
* [Postman API Collection](#postman-api-collection)
* [Sample Data](#sample-data)

## General info
This project is an employee salary management web service to facilitate management and analyse employees' salaries
	
## Technologies
Project is running with:
* Spring-boot version: 2.6.4
* Java version: 1.8.0_292
* Apache Maven version: 3.8.4
* H2 (embedded database)
* JUnit 5
	
## Setup
* Download the zip or clone the Git repository.
* Unzip the zip file (if you downloaded one)
* To run this project via command prompt:
    1) Open Command Prompt and Change directory (cd) to folder containing pom.xml
    2) Type this in command prompt
        ```
        $ mvn spring-boot:run
        ```
* To run this project via Eclipse or other IDE
    1) Open Eclipse/Other IDE
    2) File -> Import -> Existing Maven Project -> Navigate to the folder where you unzipped the zip
    3) Select the right project
    4) Choose the Spring Boot Application file (search for @EmployeeSalaryManagementApplication)
    5) Right Click on the file and Run as Java Application
    6) Application will be start up shortly

## Unit Testing
* Under /src/test/java/com/app/EmployeeSalaryManagement/
* Choose UserServiceTests.java
* Right click on the file and Run as Java Application
* Application will run test in sequence

## Postman API Collection
* There are 6 sample APIs in the postman collection
    ### User Story 1
    1) http://localhost:8080/users/upload 
        (POST Method)
        Provide CSV file in Body form-data
        | KEY        | Value         |
        | ---------- |:-------------:|
        | file       | employees.csv |

    ### User Story 2
    2) http://localhost:8080/users/
        (GET Method)
        Provide param with Key and Value Pair as follows:
        | KEY           | Value         |
        | ------------- |:-------------:|
        | minSalary     | 0             |  Value can be in any number
        | maxSalary     | 0             |  Value can be in any number
        | filterByName  |               |  Value can be any employee name (Wildcard match)
        | sort          | ID, ASC       |  Value can be any column name follow by a commas(,) then ASC or DESC
        | offset        | 0             |  Value can be in any number
        | limit         | 0             |  Value can be in any number

        Edit the value to filter for correct results
    ### User Story 3 
    
    #### Create User
    3) http://localhost:8080/users/ 
        (POST Method)
        Provide JSON Object in Body form-data
        ```
        {
        "id": "e0011",
        "login": "esmadmin123",
        "name": "system",
        "salary": 4000.0,
        "startDate": "2022-03-01"
        }
        ```

    #### Retrieve User
    4) http://localhost:8080/users/{id} 
        (GET Method)
        Replace {id} with an Employee ID e.g. http://localhost:8080/users/e0001
    
    #### Update User
    5) http://localhost:8080/users 
        (PUT Method)
        Provide JSON Object in Body form-data
        ```
        {
        "id": "e0011",
        "login": "esmadmin999",
        "name": "system9",
        "salary": 3400.0,
        "startDate": "2022-03-01"
        }
        ```
    #### Delete User
    6) http://localhost:8080/users/{id} 
        (DELETE Method)
        Replace {id} with an Employee ID e.g. http://localhost:8080/users/e0001

## Sample Data
* Refer for employees.csv for sample data

## Future Enhancement for thoughts
* Add spring boot security for authentication and access control
* Replace tomcat with undertow to improve performance and memory usage
* Create asynchronous threading to improve application performance

