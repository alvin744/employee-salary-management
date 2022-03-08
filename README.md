## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Setup](#setup)
* [Unit Testing](#unit-testing)

## General info
This project is an employee salary management web service to facilitate management and analyse employees' salaries
	
## Technologies
Project is running with:
* Spring-boot version: 2.6.4
* Java version: 1.8.0_292
* Apache Maven version: 3.8.4
	
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