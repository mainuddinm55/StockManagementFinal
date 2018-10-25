package com.kcirque.stockmanagementfinal.Database.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Profit implements Serializable {
    private List<DateAmountSales> salesList = new ArrayList<>();
    private List<DateAmountPurchase> purchaseList = new ArrayList<>();
    private List<DateAmountCost> costList = new ArrayList<>();
    private List<DateAmountSalary> salaryList = new ArrayList<>();

    public Profit() {
    }

    public Profit(List<DateAmountSales> salesList, List<DateAmountPurchase> purchaseList, List<DateAmountCost> costList, List<DateAmountSalary> salaryList) {
        this.salesList = salesList;
        this.purchaseList = purchaseList;
        this.costList = costList;
        this.salaryList = salaryList;
    }

    public List<DateAmountSales> getSalesList() {
        return salesList;
    }

    public void setSalesList(List<DateAmountSales> salesList) {
        this.salesList = salesList;
    }

    public List<DateAmountPurchase> getPurchaseList() {
        return purchaseList;
    }

    public void setPurchaseList(List<DateAmountPurchase> purchaseList) {
        this.purchaseList = purchaseList;
    }

    public List<DateAmountCost> getCostList() {
        return costList;
    }

    public void setCostList(List<DateAmountCost> costList) {
        this.costList = costList;
    }

    public List<DateAmountSalary> getSalaryList() {
        return salaryList;
    }

    public void setSalaryList(List<DateAmountSalary> salaryList) {
        this.salaryList = salaryList;
    }
}
