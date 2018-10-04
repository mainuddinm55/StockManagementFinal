package com.kcirque.stockmanagementfinal;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Category;
import com.kcirque.stockmanagementfinal.Database.Model.StockHand;
import com.kcirque.stockmanagementfinal.Fragment.ExpenseFragment;
import com.kcirque.stockmanagementfinal.Fragment.HomeFragment;
import com.kcirque.stockmanagementfinal.Fragment.ReminderFragment;
import com.kcirque.stockmanagementfinal.Fragment.SellerAddFragment;
import com.kcirque.stockmanagementfinal.Fragment.StockOutFragment;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentLoader {

    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawer;
    private DatabaseReference mRootRef;
    private DatabaseReference mStockRef;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private int mCount = 0;

    private String categoryName;
    private int categoryId = 1;

    private TextView reminderCounterTextView;
    private List<StockHand> mStockHandWarning = new ArrayList<>();
    private NavigationView mNavigationView;
    private DatabaseReference mCategoryRef;
    private SharedPref mSharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mSharedPref = new SharedPref(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        hideItem();
        reminderCounterTextView = (TextView) MenuItemCompat.getActionView(mNavigationView.getMenu().
                findItem(R.id.nav_reminder));
        mRootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        mStockRef = mRootRef.child(Constant.STOCK_HAND_REF);
        mCategoryRef = mRootRef.child(Constant.CATEGORY_REF);
        mStockRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCount = 0;
                mStockHandWarning.clear();
                for (DataSnapshot postData : dataSnapshot.getChildren()) {
                    StockHand stockHand = postData.getValue(StockHand.class);
                    int stock = stockHand.getPurchaseQuantity() - stockHand.getSellQuantity();
                    if (stock < 5) {
                        mStockHandWarning.add(stockHand);
                        mCount++;
                    }
                }

                if (mCount > 0) {
                    initializeCountDrawer(mCount);
                }

                Log.e(TAG, "onDataChange: " + mStockHandWarning.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        HomeFragment fragment = HomeFragment.getInstance();
        ft.replace(R.id.main_fragment_container,fragment,"homeFragment");
        ft.commit();



    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("homeFragment");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragment != null && fragment.isVisible()) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        displaySelectedScreen(id);
        return true;
    }

    public void displaySelectedScreen(int selectedMenuId) {
        Fragment fragment = null;
        switch (selectedMenuId) {
            case R.id.nav_category:
                showCategoryDialog();
                break;
            case R.id.nav_reminder:
                Log.e(TAG, "displaySelectedScreen: " + mStockHandWarning.size());
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.EXTRA_STOCK_WARNING, (Serializable) mStockHandWarning);
                fragment = ReminderFragment.getInstance();
                fragment.setArguments(bundle);
                break;
            case R.id.nav_seller:
                fragment = SellerAddFragment.getInstance();
                break;
            case R.id.nav_stock_out:
                fragment = StockOutFragment.getInstance();
                break;
            case R.id.nav_add_expense:
                fragment = ExpenseFragment.getInstance();
                break;
            case R.id.nav_logout:
                if (mUser != null) {
                    mAuth.signOut();
                } else {
                    mSharedPref.logOut();
                }
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
        }

        if (fragment != null) {
            loadFragment(fragment, true);
        }
        mDrawer.closeDrawers();
    }

    private void hideItem() {
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = mNavigationView.getMenu();
        if (mUser == null) {
            nav_Menu.findItem(R.id.nav_seller).setVisible(false);
        }
    }

    @Override
    public void loadFragment(Fragment fragment, boolean isBack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_fragment_container, fragment);
        if (isBack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    private void initializeCountDrawer(int count) {
        //Gravity property aligns the text
        reminderCounterTextView.setGravity(Gravity.CENTER_VERTICAL);
        reminderCounterTextView.setTextSize(20);
        reminderCounterTextView.setTypeface(null, Typeface.BOLD);
        reminderCounterTextView.setTextColor(getResources().getColor(android.R.color.black));
        reminderCounterTextView.setText("" + count);
    }

    private void showCategoryDialog() {
        mCategoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryId = (int) (dataSnapshot.getChildrenCount() + 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        AlertDialog.Builder categoryDialog = new AlertDialog.Builder(this);
        categoryDialog.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.add_category, null);
        categoryDialog.setView(view);
        final AlertDialog dialog = categoryDialog.create();
        final EditText categoryNameEditText = view.findViewById(R.id.category_name_edit_text);
        Button addCategoryBtn = view.findViewById(R.id.add_btn);
        Button cancelBtn = view.findViewById(R.id.cancel_btn);
        addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryNameEditText.getText().toString().trim().isEmpty()) {
                    categoryNameEditText.setError("Name is Required");
                    categoryNameEditText.requestFocus();
                    return;
                }

                categoryName = categoryNameEditText.getText().toString().trim();

                String key = mCategoryRef.push().getKey();
                Category category = new Category(categoryId, key, categoryName);

                mCategoryRef.push().setValue(category).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Category Added", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                });
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
