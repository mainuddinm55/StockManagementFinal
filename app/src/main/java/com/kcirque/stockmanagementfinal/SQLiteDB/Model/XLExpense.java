package com.kcirque.stockmanagementfinal.SQLiteDB.Model;

public class XLExpense {
    private int id;
    private String name;
    private double amount;
    private String comment;
    private String date;

    public static final String TABLE_NAME = "Expense";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_AMOUNT = "amount";
    public static final String COL_COMMENT = "comment";
    public static final String COL_DATE = "date";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ( "
                    + COL_ID + " INTEGER PRIMARY KEY, "
                    + COL_NAME + " TEXT, "
                    + COL_AMOUNT + " REAL, "
                    + COL_COMMENT + " TEXT, "
                    + COL_DATE + " TEXT " +
                    ");";

    public XLExpense() {
    }

    public XLExpense(int id, String name, double amount, String comment, String date) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.comment = comment;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
