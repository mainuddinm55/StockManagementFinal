package com.kcirque.stockmanagementfinal.Database.Model;

public class Purchase {

    private String key;
    private int productId;
    private String productName;
    private String companyName;
    private double actualPrice;
    private double sellingPrice;
    private int quantity;
    private long purchaseDate;
    private double totalPrice;
    private double paidAmount;
    private double dueAmount;

    public Purchase(String key, int productId, String productName, String companyName, double actualPrice, double sellingPrice, int quantity, long purchaseDate, double totalPrice, double paidAmount, double dueAmount) {
        this.key = key;
        this.productId = productId;
        this.productName = productName;
        this.companyName = companyName;
        this.actualPrice = actualPrice;
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
        this.purchaseDate = purchaseDate;
        this.totalPrice = totalPrice;
        this.paidAmount = paidAmount;
        this.dueAmount = dueAmount;
    }

    public Purchase() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(double actualPrice) {
        this.actualPrice = actualPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(long purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getDueAmount() {
        return dueAmount;
    }

    public void setDueAmount(double dueAmount) {
        this.dueAmount = dueAmount;
    }
}
