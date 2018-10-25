package com.kcirque.stockmanagementfinal.SQLiteDB.Model;

public class XLProduct {
    private int id;
    private String name;
    private String code;
    private int categoryId;
    private String desc;

    public static final String TABLE_NAME = "Product";
    public static final String COL_ID = "Product_id";
    public static final String COL_NAME = "Product_name";
    public static final String COL_CODE = "product_code";
    public static final String COL_CATEGORY_ID = "category_id";
    public static final String COL_DESC = "desc";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + COL_ID + " INTEGER PRIMARY KEY, "
                    + COL_NAME + " TEXT, "
                    + COL_CODE + " TEXT, "
                    + COL_CATEGORY_ID + " INTEGER, "
                    + COL_DESC + " TEXT"
                    + ");";


    public XLProduct() {
    }

    public XLProduct(int id, String name, String code, int categoryId, String desc) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.categoryId = categoryId;
        this.desc = desc;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
