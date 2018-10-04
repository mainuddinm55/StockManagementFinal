package com.kcirque.stockmanagementfinal.Database;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseOfflineSync extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
