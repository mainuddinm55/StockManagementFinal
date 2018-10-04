package com.kcirque.stockmanagementfinal.Database.Model;

public class Customer {

    private String key;
    private int customerId;
    private String customerName;
    private String address;
    private String mobile;
    private String email;
    private double deposit;

    public Customer(String key, int customerId, String customerName, String address, String mobile, String email, double deposit) {
        this.key = key;
        this.customerId = customerId;
        this.customerName = customerName;
        this.address = address;
        this.mobile = mobile;
        this.email = email;
        this.deposit = deposit;
    }

    public Customer() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getDeposit() {

        return deposit;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }
}
