package com.kcirque.stockmanagementfinal.Database.Model;

import java.util.ArrayList;
import java.util.List;

public class Sales {

    private String key;
    private int customerId;
    private String customerName;
    private long salesDate;
    private List<ProductSell> selectedProduct = new ArrayList<>();
    private double subtotal;
    private double discount;
    private double total;
    private double paid;
    private double due;

    public Sales() {
    }

    public Sales(String key, int customerId, String customerName, long salesDate, List<ProductSell> selectedProduct, double subtotal, double discount, double total, double paid, double due) {
        this.key = key;
        this.customerId = customerId;
        this.customerName = customerName;
        this.salesDate = salesDate;
        this.selectedProduct = selectedProduct;
        this.subtotal = subtotal;
        this.discount = discount;
        this.total = total;
        this.paid = paid;
        this.due = due;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public long getSalesDate() {
        return salesDate;
    }

    public void setSalesDate(long salesDate) {
        this.salesDate = salesDate;
    }

    public List<ProductSell> getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(List<ProductSell> selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
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
