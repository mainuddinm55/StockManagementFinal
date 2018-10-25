package com.kcirque.stockmanagementfinal.SQLiteDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kcirque.stockmanagementfinal.SQLiteDB.Model.XLCustomer;
import com.kcirque.stockmanagementfinal.SQLiteDB.Model.XLExpense;
import com.kcirque.stockmanagementfinal.SQLiteDB.Model.XLProduct;
import com.kcirque.stockmanagementfinal.SQLiteDB.Model.XLProfit;
import com.kcirque.stockmanagementfinal.SQLiteDB.Model.XLPurchase;
import com.kcirque.stockmanagementfinal.SQLiteDB.Model.XLSalary;
import com.kcirque.stockmanagementfinal.SQLiteDB.Model.XLSales;
import com.kcirque.stockmanagementfinal.SQLiteDB.Model.XLStockHand;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "stock_management_system";
    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(XLCustomer.CREATE_TABLE);
        db.execSQL(XLExpense.CREATE_TABLE);
        db.execSQL(XLProduct.CREATE_TABLE);
        db.execSQL(XLProfit.CREATE_TABLE);
        db.execSQL(XLPurchase.CREATE_TABLE);
        db.execSQL(XLSalary.CREATE_TABLE);
        db.execSQL(XLSales.CREATE_TABLE);
        db.execSQL(XLStockHand.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertExpense(XLExpense expense) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(XLExpense.COL_ID, expense.getId());
        contentValues.put(XLExpense.COL_NAME, expense.getName());
        contentValues.put(XLExpense.COL_AMOUNT, expense.getAmount());
        contentValues.put(XLExpense.COL_COMMENT, expense.getComment());
        contentValues.put(XLExpense.COL_DATE, expense.getDate());

        long id = database.insert(XLExpense.TABLE_NAME, null, contentValues);

        database.close();
        return id;
    }

    public long deleteAllExpense() {
        SQLiteDatabase database = this.getWritableDatabase();
        long id = database.delete(XLExpense.TABLE_NAME, "1", null);
        database.close();
        return id;
    }

    public long insertCustomer(XLCustomer customer) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(XLCustomer.COL_ID, customer.getId());
        contentValues.put(XLCustomer.COL_NAME, customer.getCustomerName());
        contentValues.put(XLCustomer.COL_ADDRESS, customer.getAddress());
        contentValues.put(XLCustomer.COL_EMAIL, customer.getEmail());
        contentValues.put(XLCustomer.COL_MOBILE, customer.getMobile());
        contentValues.put(XLCustomer.COL_ACCOUNT_TYPE, customer.getAccountType());
        contentValues.put(XLCustomer.COL_DUE, customer.getDue());

        long id = database.insert(XLCustomer.TABLE_NAME, null, contentValues);

        database.close();
        return id;
    }

    public long deleteAllCustomer() {
        SQLiteDatabase database = this.getWritableDatabase();
        long id = database.delete(XLCustomer.TABLE_NAME, "1", null);
        database.close();
        return id;
    }

    public long insertProduct(XLProduct product) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(XLProduct.COL_ID, product.getId());
        contentValues.put(XLProduct.COL_NAME, product.getName());
        contentValues.put(XLProduct.COL_CODE, product.getCode());
        contentValues.put(XLProduct.COL_CATEGORY_ID, product.getCategoryId());
        contentValues.put(XLProduct.COL_DESC, product.getDesc());
        long id = database.insert(XLProduct.TABLE_NAME, null, contentValues);
        database.close();
        return id;
    }

    public long deleteAllProduct() {
        SQLiteDatabase database = this.getWritableDatabase();
        long id = database.delete(XLProduct.TABLE_NAME, "1", null);
        database.close();
        return id;
    }

    public long insertProfit(XLProfit profit) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(XLProfit.COL_ID, profit.getId());
        contentValues.put(XLProfit.COL_MONTH, profit.getMonth());
        contentValues.put(XLProfit.COL_YEAR, profit.getYear());
        contentValues.put(XLProfit.COL_TOTAL_PURCHASE, profit.getTotalPurchase());
        contentValues.put(XLProfit.COL_STOCK_HAND, profit.getStockHand());
        contentValues.put(XLProfit.COL_TOTAL_SALES, profit.getTotalSales());
        contentValues.put(XLProfit.COL_TOTAL_EXPENSE, profit.getTotalExpense());
        contentValues.put(XLProfit.COL_TOTAL_SALARY, profit.getTotalEmpSalary());
        contentValues.put(XLProfit.COL_TOTAL_PROFIT, profit.getProfitLoss());

        long id = database.insert(XLProfit.TABLE_NAME, null, contentValues);
        database.close();
        return id;
    }

    public long deleteAllProfit() {
        SQLiteDatabase database = this.getWritableDatabase();
        long id = database.delete(XLProfit.TABLE_NAME, "1", null);
        database.close();
        return id;
    }

    public long insertPurchase(XLPurchase purchase) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(XLPurchase.COL_ID, purchase.getId());
        contentValues.put(XLPurchase.COL_PRODUCT_ID, purchase.getProductIdl());
        contentValues.put(XLPurchase.COL_COMPANY, purchase.getCompany());
        contentValues.put(XLPurchase.COL_BUY_PRICE, purchase.getBuyPrice());
        contentValues.put(XLPurchase.COL_SELL_PRICE, purchase.getSellPrice());
        contentValues.put(XLPurchase.COL_QUANTITY, purchase.getQuantity());
        contentValues.put(XLPurchase.COL_DATE, purchase.getDate());
        contentValues.put(XLPurchase.COL_TOTAL, purchase.getTotal());
        contentValues.put(XLPurchase.COL_PAID, purchase.getPaid());
        contentValues.put(XLPurchase.COL_DUE, purchase.getDue());

        long id = database.insert(XLPurchase.TABLE_NAME, null, contentValues);
        database.close();
        return id;
    }

    public long deleteAllPurchase() {
        SQLiteDatabase database = this.getWritableDatabase();
        long id = database.delete(XLPurchase.TABLE_NAME, "1", null);
        database.close();
        return id;
    }

    public long insertSalary(XLSalary salary) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(XLSalary.COL_ID, salary.getId());
        contentValues.put(XLSalary.COL_EMP_KEY, salary.getEmpKey());
        contentValues.put(XLSalary.COL_EMP_NAME, salary.getEmpName());
        contentValues.put(XLSalary.COL_MONTH, salary.getMonth());
        contentValues.put(XLSalary.COL_AMOUNT, salary.getAmount());
        contentValues.put(XLSalary.COL_DATE, salary.getDate());

        long id = database.insert(XLSalary.TABLE_NAME, null, contentValues);
        database.close();
        return id;
    }

    public long deleteAllSalary() {
        SQLiteDatabase database = this.getWritableDatabase();
        long id = database.delete(XLSalary.TABLE_NAME, "1", null);
        database.close();
        return id;
    }

    public long insertSales(XLSales sales) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(XLSales.COL_ID, sales.getId());
        contentValues.put(XLSales.COL_CUSTOMER_ID, sales.getCustomerId());
        contentValues.put(XLSales.COL_CUSTOMER_NAME, sales.getCustomerName());
        contentValues.put(XLSales.COL_DATE, sales.getDate());
        contentValues.put(XLSales.COL_SUB_TOTAL, sales.getSubTotal());
        contentValues.put(XLSales.COL_DISCOUNT, sales.getDiscount());
        contentValues.put(XLSales.COL_TOTAL, sales.getTotal());
        contentValues.put(XLSales.COL_PAID, sales.getPaid());
        contentValues.put(XLSales.COL_DUE, sales.getDue());

        long id = database.insert(XLSales.TABLE_NAME, null, contentValues);
        database.close();
        return id;
    }

    public long deleteAllSales() {
        SQLiteDatabase database = this.getWritableDatabase();
        long id = database.delete(XLSales.TABLE_NAME, "1", null);
        database.close();
        return id;
    }

    public long insertStockHand(XLStockHand stockHand) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(XLStockHand.COL_ID, stockHand.getId());
        contentValues.put(XLStockHand.COL_PRODUCT_ID, stockHand.getProductId());
        contentValues.put(XLStockHand.COL_TOTAL_PURCHASE, stockHand.getTotalPurchase());
        contentValues.put(XLStockHand.COL_TOTAL_SELL, stockHand.getTotalSell());

        long id = database.insert(XLStockHand.TABLE_NAME, null, contentValues);
        database.close();
        return id;
    }

    public long deleteAllStockHand() {
        SQLiteDatabase database = this.getWritableDatabase();
        long id = database.delete(XLStockHand.TABLE_NAME, "1", null);
        database.close();
        return id;
    }
}
