package com.kcirque.stockmanagementfinal.Database.Model;

public class DateAmountSalary {
    long date;
    double amount;
    int month;

    public DateAmountSalary(long date, double amount, int month) {
        this.date = date;
        this.amount = amount;
        this.month = month;
    }

    public DateAmountSalary() {

    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
