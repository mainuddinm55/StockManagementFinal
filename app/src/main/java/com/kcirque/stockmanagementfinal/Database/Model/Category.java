package com.kcirque.stockmanagementfinal.Database.Model;

public class Category {

    private int categoryId;
    private String key;
    private String categoryName;

    public Category() {
    }

    public Category(int categoryId, String key, String categoryName) {
        this.categoryId = categoryId;
        this.key = key;
        this.categoryName = categoryName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
