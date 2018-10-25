package com.kcirque.stockmanagementfinal.SQLiteDB.Model;

public class XLProfit {
    private int id;
    private int month;
    private int year;
    private double totalPurchase;
    private double stockHand;
    private double totalSales;
    private double totalExpense;
    private double totalEmpSalary;
    private double profitLoss;

    public static final String TABLE_NAME = "Profit";

    public static final String COL_ID = "id";
    public static final String COL_MONTH = "month";
    public static final String COL_YEAR = "year";
    public static final String COL_TOTAL_PURCHASE = "total_purchase";
    public static final String COL_STOCK_HAND = "total_stock_hand";
    public static final String COL_TOTAL_SALES = "total_sales";
    public static final String COL_TOTAL_EXPENSE = "total_expense";
    public static final String COL_TOTAL_SALARY = "total_emp_salary";
    public static final String COL_TOTAL_PROFIT = "profit";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + COL_ID + " INTEGER PRIMARY KEY,"
                    + COL_MONTH + " INTEGER,"
                    + COL_YEAR + " INTEGER,"
                    + COL_TOTAL_PURCHASE + " REAL,"
                    + COL_STOCK_HAND + " REAL,"
                    + COL_TOTAL_SALES + " REAL,"
                    + COL_TOTAL_EXPENSE + " REAL,"
                    + COL_TOTAL_SALARY + " REAL,"
                    + COL_TOTAL_PROFIT + " REAL"
                    + ");";


    public XLProfit() {
    }

    public XLProfit(int id, int month, int year, double totalPurchase, double stockHand, double totalSales, double totalExpense, double totalEmpSalary, double profitLoss) {
        this.id = id;
        this.month = month;
        this.year = year;
        this.totalPurchase = totalPurchase;
        this.stockHand = stockHand;
        this.totalSales = totalSales;
        this.totalExpense = totalExpense;
        this.totalEmpSalary = totalEmpSalary;
        this.profitLoss = profitLoss;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getTotalPurchase() {
        return totalPurchase;
    }

    public void setTotalPurchase(double totalPurchase) {
        this.totalPurchase = totalPurchase;
    }

    public double getStockHand() {
        return stockHand;
    }

    public void setStockHand(double stockHand) {
        this.stockHand = stockHand;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(double totalSales) {
        this.totalSales = totalSales;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(double totalExpense) {
        this.totalExpense = totalExpense;
    }

    public double getTotalEmpSalary() {
        return totalEmpSalary;
    }

    public void setTotalEmpSalary(double totalEmpSalary) {
        this.totalEmpSalary = totalEmpSalary;
    }

    public double getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(double profitLoss) {
        this.profitLoss = profitLoss;
    }
}
