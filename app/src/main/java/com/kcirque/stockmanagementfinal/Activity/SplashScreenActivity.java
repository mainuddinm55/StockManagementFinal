package com.kcirque.stockmanagementfinal.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.R;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";
    private SharedPref mSharedPref;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView imageView = findViewById(R.id.imageView);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        imageView.startAnimation(animation);

        mAuth = FirebaseAuth.getInstance();

        mSharedPref = new SharedPref(getApplicationContext());
        final Seller seller = mSharedPref.getSeller();
        final FirebaseUser user = mAuth.getCurrentUser();


        Thread timer = new Thread() {

            @Override
            public void run() {

                try {
                    sleep(3000);
                    if (user != null) {
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else if (seller != null) {
                        Log.e(TAG, "onCreate: " + seller.getAdminUid() + seller.getName());
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();

                    } else {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                    super.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        };

        timer.start();
    }


}
