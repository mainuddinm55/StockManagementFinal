package com.kcirque.stockmanagementfinal.Database.Model;

import java.io.Serializable;


public class Salary implements Serializable {


    String key;
    String empKey;
    String empName;
    String month;
    double amount;
    long date;

    public Salary() {
    }

    public Salary(String key, String empKey, String empName, String month, double amount, long date) {
        this.key = key;
        this.empKey = empKey;
        this.empName = empName;
        this.month = month;
        this.amount = amount;
        this.date = date;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEmpKey() {
        return empKey;
    }

    public void setEmpKey(String empKey) {
        this.empKey = empKey;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
