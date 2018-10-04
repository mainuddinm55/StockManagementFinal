package com.kcirque.stockmanagementfinal.Database.Model;

import java.io.Serializable;

public class StockHand implements Serializable {
    private int productId;
    private int purchaseQuantity;
    private double buyPrice;
    private int sellQuantity;

    public StockHand() {
    }

    public StockHand(int productId, int purchaseQuantity, double buyPrice, int sellQuantity) {
        this.productId = productId;
        this.purchaseQuantity = purchaseQuantity;
        this.buyPrice = buyPrice;
        this.sellQuantity = sellQuantity;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getPurchaseQuantity() {
        return purchaseQuantity;
    }

    public void setPurchaseQuantity(int purchaseQuantity) {
        this.purchaseQuantity = purchaseQuantity;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public int getSellQuantity() {
        return sellQuantity;
    }

    public void setSellQuantity(int sellQuantity) {
        this.sellQuantity = sellQuantity;
    }
}
