package com.kcirque.stockmanagementfinal.SQLiteDB.Model;

public class XLPurchase {
    private int id;
    private int productIdl;
    private String company;
    private double buyPrice;
    private double sellPrice;
    private int quantity;
    private String date;
    private double total;
    private double paid;
    private double due;

    public static final String TABLE_NAME = "Purchase";
    public static final String COL_ID = "id";
    public static final String COL_PRODUCT_ID = "product_id";
    public static final String COL_COMPANY = "company";
    public static final String COL_SELL_PRICE = "sell_price";
    public static final String COL_BUY_PRICE = "buy_price";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_DATE = "date";
    public static final String COL_TOTAL = "total";
    public static final String COL_PAID = "paid";
    public static final String COL_DUE = "due";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + COL_ID + " INTEGER PRIMARY KEY,"
                    + COL_PRODUCT_ID + " INTEGER,"
                    + COL_COMPANY + " TEXT,"
                    + COL_BUY_PRICE + " REAL,"
                    + COL_SELL_PRICE + " REAL,"
                    + COL_QUANTITY + " INTEGER,"
                    + COL_DATE + " TEXT,"
                    + COL_TOTAL + " REAL,"
                    + COL_PAID + " REAL,"
                    + COL_DUE + " REAL"
                    + ");";


    public XLPurchase() {
    }

    public XLPurchase(int id, int productIdl, String company, double buyPrice, double sellPrice, int quantity, String date, double total, double paid, double due) {
        this.id = id;
        this.productIdl = productIdl;
        this.company = company;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.quantity = quantity;
        this.date = date;
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

    public int getProductIdl() {
        return productIdl;
    }

    public void setProductIdl(int productIdl) {
        this.productIdl = productIdl;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
