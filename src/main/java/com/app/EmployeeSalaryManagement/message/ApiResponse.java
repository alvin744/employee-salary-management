package com.app.EmployeeSalaryManagement.message;

import org.springframework.stereotype.Component;

@Component
public class ApiResponse {
    Integer status;
    String message;
    Object results;

    public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

    public Integer getStatus() {
		return status;
	}

    public void setStatus(Integer status) {
		this.status = status;
	}

    public Object getResults() {
		return results;
	}

    public void setResults(Object results) {
		this.results = results;
	}
}
