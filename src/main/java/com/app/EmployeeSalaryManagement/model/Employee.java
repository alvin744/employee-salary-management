package com.app.EmployeeSalaryManagement.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * The type User.
 */
@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @Column(name = "id", unique = true)
    @NotBlank
    private String id;
    @Column(name = "login", unique = true)
    @NotBlank
    private String login;
    @Column(name = "name")
    @NotBlank
    private String name;
    @Column(name = "salary")
    @NotNull
    private Double salary;
    @Column(name = "startDate")
    @NotNull
    private String startDate;

    /**
     * Instantiates a new User.
     */
    public Employee() {
    }

    /**
     * Instantiates a new User.
     *
     * @param id        the id
     * @param login     the login
     * @param name      the name
     * @param salary    the salary
     * @param startDate the start date
     */
    public Employee(String id, String login, String name, Double salary, String startDate) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.salary = salary;
        this.startDate = startDate;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets login.
     *
     * @return the login
     */
    public String getLogin() {
        return login;
    }

    /**
     * Sets login.
     *
     * @param login the login
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets salary.
     *
     * @return the salary
     */
    public Double getSalary() {
        return salary;
    }

    /**
     * Sets salary.
     *
     * @param salary the salary
     */
    public void setSalary(Double salary) {
        this.salary = salary;
    }

    /**
     * Gets start date.
     *
     * @return the start date
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Sets start date.
     *
     * @param startDate the start date
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}