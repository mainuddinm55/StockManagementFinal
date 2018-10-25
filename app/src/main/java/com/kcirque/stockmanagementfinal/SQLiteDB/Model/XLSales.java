package com.kcirque.stockmanagementfinal.SQLiteDB.Model;

public class XLSales {
    private int id;
    private int customerId;
    private String customerName;
    private String date;
    private double subTotal;
    private double discount;
    private double total;
    private double paid;
    private double due;

    public static final String TABLE_NAME = "Sales";
    public static final String COL_ID = "id";
    public static final String COL_CUSTOMER_ID = "customer_id";
    public static final String COL_CUSTOMER_NAME = "customer_name";
    public static final String COL_DATE = "date";
    public static final String COL_SUB_TOTAL = "sub_total";
    public static final String COL_DISCOUNT = "discount";
    public static final String COL_TOTAL = "total";
    public static final String COL_PAID = "paid";
    public static final String COL_DUE = "due";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + COL_ID + " INTEGER PRIMARY KEY,"
                    + COL_CUSTOMER_ID + " INTEGER, "
                    + COL_CUSTOMER_NAME + " TEXT,"
                    + COL_DATE + " TEXT,"
                    + COL_SUB_TOTAL + " REAL,"
                    + COL_DISCOUNT + " REAL,"
                    + COL_TOTAL + " REAL,"
                    + COL_PAID + " REAL,"
                    + COL_DUE + " REAL"
                    + ");";

    public XLSales() {
    }

    public XLSales(int id, int customerId, String customerName, String date, double subTotal, double discount, double total, double paid, double due) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.date = date;
        this.subTotal = subTotal;
        this.discount = discount;
        this.total = total;
        this.paid = paid;
        this.due = due;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getPaid() {
        return paid;
    }

    public void setPaid(double paid) {
        this.paid = paid;
    }

    public double getDue() {
        return due;
    }

    public void setDue(double due) {
        this.due = due;
    }
}
