package com.kcirque.stockmanagementfinal.Database.Model;

public class DateAmountSales {
    long date;
    double amount;
    double stockOutAmount;

    public DateAmountSales(long date, double amount, double stockOutAmount) {
        this.date = date;
        this.amount = amount;
        this.stockOutAmount = stockOutAmount;
    }

    public DateAmountSales() {

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

    public double getStockOutAmount() {
        return stockOutAmount;
    }

    public void setStockOutAmount(double stockOutAmount) {
        this.stockOutAmount = stockOutAmount;
    }
}
