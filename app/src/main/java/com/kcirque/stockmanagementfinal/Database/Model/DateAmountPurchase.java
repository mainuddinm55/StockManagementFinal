package com.kcirque.stockmanagementfinal.Database.Model;

public class DateAmountPurchase {
    long date;
    double amount;

    public DateAmountPurchase(long date, double amount) {
        this.date = date;
        this.amount = amount;
    }

    public DateAmountPurchase() {
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
