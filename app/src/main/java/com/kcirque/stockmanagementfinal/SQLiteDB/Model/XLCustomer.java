package com.kcirque.stockmanagementfinal.SQLiteDB.Model;

public class XLCustomer {
    private int id;
    private String customerName;
    private String address;
    private String email;
    private String mobile;
    private String accountType;
    private double due;

    public static final String TABLE_NAME = "Customer";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_ADDRESS = "address";
    public static final String COL_EMAIL = "email";
    public static final String COL_MOBILE = "mobile";
    public static final String COL_ACCOUNT_TYPE = "account_type";
    public static final String COL_DUE = "due";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + COL_ID + " INTEGER PRIMARY KEY,"
                    + COL_NAME + " TEXT,"
                    + COL_ADDRESS + " TEXT,"
                    + COL_EMAIL + " TEXT,"
                    + COL_MOBILE + " TEXT,"
                    + COL_ACCOUNT_TYPE + " TEXT,"
                    + COL_DUE + " REAL"
                    + ");";

    public XLCustomer() {
    }

    public XLCustomer(int id, String customerName, String address, String email, String mobile, String accountType, double due) {
        this.id = id;
        this.customerName = customerName;
        this.address = address;
        this.email = email;
        this.mobile = mobile;
        this.accountType = accountType;
        this.due = due;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public double getDue() {
        return due;
    }

    public void setDue(double due) {
        this.due = due;
    }
}
