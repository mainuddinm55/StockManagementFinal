package com.kcirque.stockmanagementfinal.Database.Model;

public class Seller {
    private String key;
    private String name;
    private String email;
    private String password;
    private String adminUid;
    private String mobile;

    public Seller(String key, String name, String email, String password, String adminUid, String mobile) {
        this.key = key;
        this.name = name;
        this.email = email;
        this.password = password;
        this.adminUid = adminUid;
        this.mobile = mobile;
    }

    public Seller() {

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdminUid() {
        return adminUid;
    }

    public void setAdminUid(String adminUid) {
        this.adminUid = adminUid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
