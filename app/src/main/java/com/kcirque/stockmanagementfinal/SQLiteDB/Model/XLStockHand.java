package com.kcirque.stockmanagementfinal.SQLiteDB.Model;

public class XLStockHand {
    private int id;
    private int productId;
    private int totalPurchase;
    private int totalSell;

    public static final String TABLE_NAME = "StockHand";
    public static final String COL_ID = "id";
    public static final String COL_PRODUCT_ID = "product_id";
    public static final String COL_TOTAL_PURCHASE = "total_purchase_qty";
    public static final String COL_TOTAL_SELL = "total_sell_qty";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + COL_ID + " INTEGER PRIMARY KEY,"
                    + COL_PRODUCT_ID + " INTEGER,"
                    + COL_TOTAL_PURCHASE + " INTEGER,"
                    + COL_TOTAL_SELL + " INTEGER"
                    + ");";

    public XLStockHand() {
    }

    public XLStockHand(int id, int productId, int totalPurchase, int totalSell) {
        this.id = id;
        this.productId = productId;
        this.totalPurchase = totalPurchase;
        this.totalSell = totalSell;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getTotalPurchase() {
        return totalPurchase;
    }

    public void setTotalPurchase(int totalPurchase) {
        this.totalPurchase = totalPurchase;
    }

    public int getTotalSell() {
        return totalSell;
    }

    public void setTotalSell(int totalSell) {
        this.totalSell = totalSell;
    }
}
