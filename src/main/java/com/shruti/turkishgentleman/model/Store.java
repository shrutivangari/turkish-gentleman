package com.shruti.turkishgentleman.model;

public class Store {

    public String getEmployeeId() {
        return employeeId;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getDepartment() {
        return department;
    }

    private String employeeId;
    private String zipCode;
    private String storeId;
    private String department;

    public Store(String employeeId, String zipCode, String storeId, String department) {
        this.employeeId = employeeId;
        this.zipCode = zipCode;
        this.storeId = storeId;
        this.department = department;
    }
}
