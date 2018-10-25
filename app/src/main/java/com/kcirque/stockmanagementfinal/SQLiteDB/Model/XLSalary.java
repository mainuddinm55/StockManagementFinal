package com.kcirque.stockmanagementfinal.SQLiteDB.Model;

public class XLSalary {
    private int id;
    private String empKey;
    private String empName;
    private String month;
    private double amount;
    private String date;

    public static final String TABLE_NAME = "Salary";
    public static final String COL_ID = "id";
    public static final String COL_EMP_KEY = "emp_key";
    public static final String COL_EMP_NAME = "emp_name";
    public static final String COL_MONTH = "month";
    public static final String COL_AMOUNT = "amount";
    public static final String COL_DATE = "date";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + COL_ID + " INTEGER PRIMARY KEY ,"
                    + COL_EMP_KEY + " TEXT,"
                    + COL_EMP_NAME + " TEXT,"
                    + COL_MONTH + " TEXT,"
                    + COL_AMOUNT + " REAL,"
                    + COL_DATE + " TEXT"
                    + ");";

    public XLSalary() {
    }

    public XLSalary(int id, String empKey, String empName, String month, double amount, String date) {
        this.id = id;
        this.empKey = empKey;
        this.empName = empName;
        this.month = month;
        this.amount = amount;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
