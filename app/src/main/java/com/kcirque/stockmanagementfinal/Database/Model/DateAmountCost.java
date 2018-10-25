package com.kcirque.stockmanagementfinal.Database.Model;

public class DateAmountCost {
    long date;
    double amount;

    public DateAmountCost(long date, double amount) {
        this.date = date;
        this.amount = amount;
    }

    public DateAmountCost() {
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
}
