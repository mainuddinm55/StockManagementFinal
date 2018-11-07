package com.kcirque.stockmanagementfinal.Database.Model;

import com.google.android.gms.common.data.SingleRefDataBufferIterator;

import java.io.Serializable;

public class Product implements Serializable {

    private String key;
    private int productId;
    private String productName;
    private String productCode;
    private int productCategoryId;
    private String description;
    private String productImageUrl;
    private double buyPrice;
    private double sellPrice;
    private String company;
    private double oldPrice;
    private int oldPriceQuantity;

    public Product(String key, int productId, String productName, String productCode, int productCategoryId, String description, String productImageUrl) {
        this.key = key;
        this.productId = productId;
        this.productName = productName;
        this.productCode = productCode;
        this.productCategoryId = productCategoryId;
        this.description = description;
        this.productImageUrl = productImageUrl;
    }

    public Product() {
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(int productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public int getOldPriceQuantity() {
        return oldPriceQuantity;
    }

    public void setOldPriceQuantity(int oldPriceQuantity) {
        this.oldPriceQuantity = oldPriceQuantity;
    }

}
