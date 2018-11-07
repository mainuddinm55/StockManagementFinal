package com.kcirque.stockmanagementfinal.Common;

import android.content.Context;
import android.content.SharedPreferences;

import com.kcirque.stockmanagementfinal.Database.Model.Seller;

public class SharedPref {
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private static final String KEY = "key";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String UID = "adminUid";
    private static final String MOBILE = "mobile";
    private static final String STATUS = "status";

    public SharedPref(Context context) {
        this.mContext = context;
        mSharedPreferences = mContext.getSharedPreferences("Seller Status", Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public void putSeller(Seller seller) {
        mEditor.putString(KEY, seller.getKey());
        mEditor.putString(NAME, seller.getName());
        mEditor.putString(EMAIL, seller.getEmail());
        mEditor.putString(PASSWORD, seller.getPassword());
        mEditor.putString(UID, seller.getAdminUid());
        mEditor.putString(MOBILE, seller.getMobile());
        mEditor.putString(STATUS, seller.getStatus());
        mEditor.commit();
    }

    public void logOut() {
        mEditor.putString(KEY, null);
        mEditor.putString(NAME, null);
        mEditor.putString(EMAIL, null);
        mEditor.putString(PASSWORD, null);
        mEditor.putString(UID, null);
        mEditor.putString(MOBILE, null);
        mEditor.putString(STATUS, null);
        mEditor.commit();
    }

    public Seller getSeller() {
        String key = mSharedPreferences.getString(KEY, null);
        String name = mSharedPreferences.getString(NAME, null);
        String email = mSharedPreferences.getString(EMAIL, null);
        String pass = mSharedPreferences.getString(PASSWORD, null);
        String uid = mSharedPreferences.getString(UID, null);
        String mobile = mSharedPreferences.getString(MOBILE, null);
        String status = mSharedPreferences.getString(STATUS, null);
        if (key != null && name != null && email != null && pass != null && uid != null) {
            return new Seller(key, name, email, pass, uid, mobile, status);
        } else {
            return null;
        }
    }
}
