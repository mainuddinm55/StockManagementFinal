package com.kcirque.stockmanagementfinal.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ajts.androidmads.library.SQLiteToExcel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Common.BadgeDrawerArrowDrawable;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.DateConverter;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Category;
import com.kcirque.stockmanagementfinal.Database.Model.Chat;
import com.kcirque.stockmanagementfinal.Database.Model.Customer;
import com.kcirque.stockmanagementfinal.Database.Model.DateAmountCost;
import com.kcirque.stockmanagementfinal.Database.Model.DateAmountPurchase;
import com.kcirque.stockmanagementfinal.Database.Model.DateAmountSalary;
import com.kcirque.stockmanagementfinal.Database.Model.DateAmountSales;
import com.kcirque.stockmanagementfinal.Database.Model.Expense;
import com.kcirque.stockmanagementfinal.Database.Model.Product;
import com.kcirque.stockmanagementfinal.Database.Model.Purchase;
import com.kcirque.stockmanagementfinal.Database.Model.Salary;
import com.kcirque.stockmanagementfinal.Database.Model.Sales;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Database.Model.StockHand;
import com.kcirque.stockmanagementfinal.Fragment.DueFragment;
import com.kcirque.stockmanagementfinal.Fragment.ExpenseFragment;
import com.kcirque.stockmanagementfinal.Fragment.HomeFragment;
import com.kcirque.stockmanagementfinal.Fragment.MessageFragment;
import com.kcirque.stockmanagementfinal.Fragment.ReminderFragment;
import com.kcirque.stockmanagementfinal.Fragment.SalaryFragment;
import com.kcirque.stockmanagementfinal.Fragment.SellerFragment;
import com.kcirque.stockmanagementfinal.Fragment.SettingFragment;
import com.kcirque.stockmanagementfinal.Fragment.StockOutFragment;
import com.kcirque.stockmanagementfinal.Fragment.SupplierFragment;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.SQLiteDB.DatabaseHelper;
import com.kcirque.stockmanagementfinal.SQLiteDB.Model.XLCustomer;
import com.kcirque.stockmanagementfinal.SQLiteDB.Model.XLExpense;
import com.kcirque.stockmanagementfinal.SQLiteDB.Model.XLProduct;
import com.kcirque.stockmanagementfinal.SQLiteDB.Model.XLProfit;
import com.kcirque.stockmanagementfinal.SQLiteDB.Model.XLPurchase;
import com.kcirque.stockmanagementfinal.SQLiteDB.Model.XLSalary;
import com.kcirque.stockmanagementfinal.SQLiteDB.Model.XLSales;
import com.kcirque.stockmanagementfinal.SQLiteDB.Model.XLStockHand;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentLoader, RewardedVideoAdListener {

    private static final String TAG = "MainActivity";
    private static final int MESSAGE_READ_CODE = 10;
    private DrawerLayout mDrawer;
    private DatabaseReference mRootRef;
    private DatabaseReference mStockRef;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private int mCount = 0;

    private String categoryName;
    private int categoryId = 1;

    private TextView reminderCounterTextView;
    private TextView messageCounterTextView;
    private List<StockHand> mStockHandWarning = new ArrayList<>();
    private NavigationView mNavigationView;
    private DatabaseReference mCategoryRef;
    private SharedPref mSharedPref;

    private ActionBarDrawerToggle mToggle;
    private BadgeDrawerArrowDrawable mBadgeDrawable;
    private DatabaseReference mAdminRef;
    private DatabaseReference mChatRef;
    private Seller mSeller;
    private int mTotalMessage = 0;

    private View mHeaderView;
    private ValueEventListener listener;
    private int mReminderCount = 2;
    private ProgressDialog progressDialog;
    private String contactUs = "Mobile : +8801777-888661\nWeb: www.kcirqueit.com\nThank you";
    private RewardedVideoAd mRewardedVideoAd;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().build());
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, getResources().getString(R.string.APP_ID));

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mBadgeDrawable = new BadgeDrawerArrowDrawable(getSupportActionBar().getThemedContext());
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mHeaderView = mNavigationView.getHeaderView(0);
        final CircleImageView logoImageView = mHeaderView.findViewById(R.id.logoImageView);
        final TextView titleTextView = mHeaderView.findViewById(R.id.title_text_view);


        mNavigationView.setNavigationItemSelectedListener(this);

        mSharedPref = new SharedPref(this);
        mSeller = mSharedPref.getSeller();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        hideItem();
        reminderCounterTextView = (TextView) MenuItemCompat.getActionView(mNavigationView.getMenu().
                findItem(R.id.nav_reminder));
        messageCounterTextView = (TextView) MenuItemCompat.getActionView(mNavigationView.getMenu().
                findItem(R.id.nav_message));
        mRootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        if (mUser != null) {
            mAdminRef = mRootRef.child(mUser.getUid());
        } else {
            mAdminRef = mRootRef.child(mSeller.getAdminUid());
        }

        HomeFragment fragment = HomeFragment.getInstance();
        loadFragment(fragment, false, Constant.HOME_FRAGMENT_TAG);

        mStockRef = mAdminRef.child(Constant.STOCK_HAND_REF);
        mCategoryRef = mAdminRef.child(Constant.CATEGORY_REF);
        mChatRef = mAdminRef.child(Constant.CHAT_REF);
        final DatabaseReference contactUsRef = mRootRef.child(Constant.CONTACT_US_REF);

        final DatabaseReference registrationRef = mAdminRef.child(Constant.REGISTRATION_REF);

        registrationRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                contactUsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            contactUs = dataSnapshot.getValue(String.class);
                        } else {
                            contactUsRef.setValue(contactUs);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                if (dataSnapshot.exists()) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.registration_expired, null);
                    TextView noRegistrationTextView = view.findViewById(R.id.no_registration_text_view);
                    noRegistrationTextView.setText("Your registration was expired.\nPlease contact with us\n" + contactUs);
                    dialog.setView(view);
                    dialog.setCancelable(false);
                    AlertDialog alertDialog = dialog.create();
                    DateConverter dateConverter = new DateConverter();
                    long expiredDate = dataSnapshot.getValue(long.class);
                    if (dateConverter.getCurrentDate() >= expiredDate) {
                        alertDialog.show();
                    } else {
                        alertDialog.dismiss();
                    }
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.registration_expired, null);
                    TextView noRegistrationTextView = view.findViewById(R.id.no_registration_text_view);
                    noRegistrationTextView.setText("Not registration yer. \nPlease contact us\n" + contactUs);
                    dialog.setView(view);
                    dialog.setCancelable(false);
                    dialog.show();
                    registrationRef.setValue(0);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mAdminRef.child(Constant.COMPANY_NAME_REF).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String title = dataSnapshot.getValue(String.class);
                    titleTextView.setText(title);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mAdminRef.child(Constant.LOGO_URL_REF).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String ulr = dataSnapshot.getValue(String.class);
                Glide.with(getApplicationContext()).load(ulr)
                        .apply(RequestOptions.placeholderOf(R.mipmap.ic_header_logo_round))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .into(logoImageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mAdminRef.child(Constant.REMINDER_COUNT_REF).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String count = dataSnapshot.getValue(String.class);
                    mReminderCount = Integer.valueOf(count);
                    checkReminder();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        checkReminder();

        showRewardedVideo();

    }

    private void showRewardedVideo() {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    private void checkReminder() {
        mStockRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCount = 0;
                mStockHandWarning.clear();
                for (DataSnapshot postData : dataSnapshot.getChildren()) {
                    StockHand stockHand = postData.getValue(StockHand.class);
                    if (stockHand != null && stockHand.getSellQuantity() > 0) {
                        int stock = stockHand.getPurchaseQuantity() - stockHand.getSellQuantity();
                        if (stock < mReminderCount || stock == 0) {
                            mStockHandWarning.add(stockHand);
                            mCount++;
                        }
                    }
                }

                if (mCount > 0) {
                    mBadgeDrawable.setBackgroundColor(Color.RED);
                    mToggle.setDrawerArrowDrawable(mBadgeDrawable);
                    initializeCountDrawer();
                } else if (mTotalMessage <= 0) {
                    mBadgeDrawable.setBackgroundColor(Color.TRANSPARENT);
                    initializeCountDrawer();
                }

                Log.e(TAG, "onDataChange: " + mStockHandWarning.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initializeMessageCount() {
        if (mUser != null) {
            if (mTotalMessage > 0) {
                messageCounterTextView.setGravity(Gravity.CENTER_VERTICAL);
                messageCounterTextView.setTypeface(null, Typeface.BOLD);
                messageCounterTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                messageCounterTextView.setText("" + mTotalMessage);
                mBadgeDrawable.setBackgroundColor(Color.RED);
                mToggle.setDrawerArrowDrawable(mBadgeDrawable);
                initializeCountDrawer();
            } else if (mCount <= 0) {
                mBadgeDrawable.setBackgroundColor(Color.TRANSPARENT);
                messageCounterTextView.setText(null);
                initializeCountDrawer();
            }
        } else {
            if (mTotalMessage > 0) {
                messageCounterTextView.setGravity(Gravity.CENTER_VERTICAL);
                messageCounterTextView.setTypeface(null, Typeface.BOLD);
                messageCounterTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                messageCounterTextView.setText("" + mTotalMessage);
                mBadgeDrawable.setBackgroundColor(Color.RED);
                mToggle.setDrawerArrowDrawable(mBadgeDrawable);
                initializeCountDrawer();
            } else if (mCount <= 0) {
                mBadgeDrawable.setBackgroundColor(Color.TRANSPARENT);
                messageCounterTextView.setText(null);
                initializeCountDrawer();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Fragment homeFragment = getSupportFragmentManager().findFragmentByTag(Constant.HOME_FRAGMENT_TAG);
        Fragment productListFragment = getSupportFragmentManager().findFragmentByTag(Constant.PRODUCT_LIST_FRAGMENT_TAG);
        Fragment customerListFragment = getSupportFragmentManager().findFragmentByTag(Constant.CUSTOMER_LIST_FRAGMENT_TAG);
        Fragment sellerFragment = getSupportFragmentManager().findFragmentByTag(Constant.SELLER_FRAGMENT_TAG);
        Fragment salaryFragment = getSupportFragmentManager().findFragmentByTag(Constant.SALARY_FRAGMENT_TAG);
        Fragment dueFragment = getSupportFragmentManager().findFragmentByTag(Constant.DUE_FRAGMENT_TAG);
        Fragment supplierFragment = getSupportFragmentManager().findFragmentByTag(Constant.SUPPLIER_FRAGMENT_TAG);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (homeFragment != null && homeFragment.isVisible()) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (productListFragment != null && productListFragment.isVisible()) {
            loadFragment(HomeFragment.getInstance(), false, Constant.HOME_FRAGMENT_TAG);
        } else if (customerListFragment != null && customerListFragment.isVisible()) {
            loadFragment(HomeFragment.getInstance(), false, Constant.HOME_FRAGMENT_TAG);
        } else if (sellerFragment != null && sellerFragment.isVisible()) {
            loadFragment(HomeFragment.getInstance(), false, Constant.HOME_FRAGMENT_TAG);
        } else if (salaryFragment != null && salaryFragment.isVisible()) {
            loadFragment(HomeFragment.getInstance(), false, Constant.HOME_FRAGMENT_TAG);
        } else if (dueFragment != null && dueFragment.isVisible()) {
            loadFragment(HomeFragment.getInstance(), false, Constant.HOME_FRAGMENT_TAG);
        } else if (supplierFragment != null && supplierFragment.isVisible()) {
            loadFragment(HomeFragment.getInstance(), false, Constant.HOME_FRAGMENT_TAG);
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
        String tag = null;
        switch (selectedMenuId) {
            case R.id.nav_home:
                fragment = HomeFragment.getInstance();
                tag = Constant.HOME_FRAGMENT_TAG;
                break;
            case R.id.nav_category:
                showCategoryDialog();
                break;
            case R.id.nav_reminder:
                Log.e(TAG, "displaySelectedScreen: " + mStockHandWarning.size());
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.EXTRA_STOCK_WARNING, (Serializable) mStockHandWarning);
                fragment = ReminderFragment.getInstance();
                fragment.setArguments(bundle);
                tag = Constant.REMINDER_FRAGMENT_TAG;
                break;
            case R.id.nav_seller:
                fragment = SellerFragment.getInstance();
                tag = Constant.SELLER_FRAGMENT_TAG;
                break;
            case R.id.nav_supplier:
                fragment = new SupplierFragment();
                tag = Constant.SUPPLIER_FRAGMENT_TAG;
                break;
            case R.id.nav_salry:
                fragment = SalaryFragment.getInstance();
                tag = Constant.SALARY_FRAGMENT_TAG;
                break;
            case R.id.nav_message:
                openChatActivity();
                break;
            case R.id.nav_stock_out:
                fragment = StockOutFragment.getInstance();
                tag = Constant.STOCK_OUT_FRAGMENT_TAG;
                break;
            case R.id.nav_daily_report:
                startActivity(new Intent(MainActivity.this, DailyReportActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_due:
                fragment = DueFragment.getInstance();
                tag = Constant.DUE_FRAGMENT_TAG;
                break;
            case R.id.nav_add_expense:
                fragment = ExpenseFragment.getInstance();
                tag = Constant.EXPENSE_FRAGMENT_TAG;
                break;
            case R.id.nav_settings:
                fragment = SettingFragment.getInstance();
                tag = Constant.SETTING_FRAGMENT_TAG;
                break;
            case R.id.nav_export_to_xl:
                exportToXL();
                break;
            case R.id.nav_logout:
                if (mUser != null) {
                    final DatabaseReference isSingIn = mAdminRef.child(Constant.IS_LOGGED);
                    isSingIn.setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mAuth.signOut();
                        }
                    });
                } else {
                    mSharedPref.logOut();
                }
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.nav_help:
                startActivity(new Intent(this, HelpActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.nav_about:
                startActivity(new Intent(this, AboutActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }

        if (fragment != null) {
            loadFragment(fragment, true, tag);
        }
        mDrawer.closeDrawers();
    }

    private void openChatActivity() {
        if (mUser != null) {
            loadFragment(new MessageFragment(), true, Constant.MESSAGE_FRAGMENT_TAG);
        } else {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.putExtra(Constant.EXTRA_SELLER, mSeller);
            startActivityForResult(intent, MESSAGE_READ_CODE);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    private void exportToXL() {
        if (isNetworkAvailable(getApplicationContext())) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            } else {
                showProgressDialog();
                ExportXL exportXL = new ExportXL(this);
                Thread thread = new Thread(exportXL);
                thread.start();
            }
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideItem() {
        Menu nav_Menu = mNavigationView.getMenu();
        if (mUser == null) {
            nav_Menu.findItem(R.id.nav_category).setVisible(false);
            nav_Menu.findItem(R.id.nav_seller).setVisible(false);
            nav_Menu.findItem(R.id.nav_salry).setVisible(false);
            nav_Menu.findItem(R.id.nav_stock_out).setVisible(false);
            nav_Menu.findItem(R.id.nav_daily_report).setVisible(false);
            nav_Menu.findItem(R.id.nav_due).setVisible(false);
            nav_Menu.findItem(R.id.nav_add_expense).setVisible(false);
            nav_Menu.findItem(R.id.nav_settings).setVisible(false);
            nav_Menu.findItem(R.id.nav_export_to_xl).setVisible(false);
            nav_Menu.findItem(R.id.nav_supplier).setVisible(false);
        }
    }

    @Override
    public void loadFragment(Fragment fragment, boolean isBack, String tag) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.replace(R.id.main_fragment_container, fragment, tag);
        if (isBack) {
            ft.addToBackStack(null);
        }
        ft.commitAllowingStateLoss();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initializeCountDrawer() {
        //Gravity property aligns the text
        reminderCounterTextView.setGravity(Gravity.CENTER_VERTICAL);
        reminderCounterTextView.setTypeface(null, Typeface.BOLD);
        reminderCounterTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

        if (mCount > 0) {
            reminderCounterTextView.setText("" + mCount);
            mBadgeDrawable.setText((mCount + mTotalMessage) + "");
        } else if (mTotalMessage > 0) {
            mBadgeDrawable.setText((mCount + mTotalMessage) + "");
        } else {
            reminderCounterTextView.setText(null);
            mBadgeDrawable.setText((null));
        }
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
        categoryDialog.setTitle("ADD CATEGORY");
        View view = getLayoutInflater().inflate(R.layout.add_category, null);
        categoryDialog.setView(view);
        final MaterialEditText categoryNameEditText = view.findViewById(R.id.category_name_edit_text);
        categoryNameEditText.setHint("Category name");
        categoryNameEditText.setFloatingLabelText("Category name");
        categoryDialog.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if (categoryNameEditText.getText().toString().trim().isEmpty()) {
                    Snackbar.make(mDrawer, "Please Enter Category Name", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                categoryName = categoryNameEditText.getText().toString().trim();

                String key = mCategoryRef.push().getKey();
                Category category = new Category(categoryId, key, categoryName);

                if (isNetworkAvailable(getApplicationContext())) {
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
                            dialog.dismiss();
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        categoryDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        categoryDialog.show();
    }

    public void unreadMessage() {
        if (isNetworkAvailable(this)) {
            listener = mChatRef.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mTotalMessage = 0;
                    for (DataSnapshot postData : dataSnapshot.getChildren()) {
                        Chat chat = postData.getValue(Chat.class);
                        if (mUser != null) {
                            if (chat.getReceiver().equals(mUser.getUid()) && !chat.isIsSeen()) {
                                mTotalMessage++;
                            }
                        } else {
                            if (chat.getReceiver().equals(mSeller.getKey()) && chat.getSender().equals(mSeller.getAdminUid())
                                    && !chat.isIsSeen()) {
                                mTotalMessage++;
                            }
                        }
                    }

                    initializeMessageCount();


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    @Override
    protected void onPause() {
        mChatRef.removeEventListener(listener);
        mRewardedVideoAd.pause(this);
        super.onPause();

    }

    @Override
    protected void onResume() {
        unreadMessage();
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    protected void onRestart() {
        unreadMessage();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRewardedVideoAd.destroy(this);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        showRewardedVideo();
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(getResources().getString(R.string.REWARDED_VIDEO_AD_ID),
                new AdRequest.Builder().build());
    }

    private class ExportXL implements Runnable {
        private Context mContext;
        private int expenseId = 1;

        List<DateAmountPurchase> purchaseList = new ArrayList<>();
        List<DateAmountSalary> salaryList = new ArrayList<>();
        List<DateAmountCost> costList = new ArrayList<>();
        List<DateAmountSales> salesList = new ArrayList<>();

        HashSet<Integer> yearList = new HashSet<>();
        HashSet<Integer> monthList = new HashSet<>();


        private int purchaseId = 1;
        private int salaryId = 1;
        private int salesId = 1;
        private int stockHandId = 1;
        private int profitId = 1;

        boolean exporting = true;

        public ExportXL(Context context) {
            this.mContext = context;
        }

        @Override
        public void run() {
            final DateConverter dateConverter = new DateConverter();
            final DatabaseReference expenseRef = mAdminRef.child(Constant.EXPENSE_REF);
            final DatabaseReference customerRef = mAdminRef.child(Constant.CUSTOMER_REF);
            final DatabaseReference productRef = mAdminRef.child(Constant.PRODUCT_REF);
            final DatabaseReference profitRef = mAdminRef.child(Constant.PROFIT_REF);
            final DatabaseReference purchaseRef = mAdminRef.child(Constant.PURCHASE_REF);
            final DatabaseReference salaryRef = mAdminRef.child(Constant.SALARY_REF);
            final DatabaseReference salesRef = mAdminRef.child(Constant.SALES_REF);
            final DatabaseReference stockHandRef = mAdminRef.child(Constant.STOCK_HAND_REF);

            final DatabaseHelper database = new DatabaseHelper(mContext);
            database.deleteAllStockHand();
            database.deleteAllSales();
            database.deleteAllSalary();
            database.deleteAllPurchase();
            database.deleteAllProduct();
            database.deleteAllProfit();
            database.deleteAllExpense();
            database.deleteAllCustomer();

            final ChildEventListener expenseListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Expense expense = dataSnapshot.getValue(Expense.class);
                    if (expense != null) {
                        XLExpense xlExpense = new XLExpense(expenseId, expense.getExpenseName(), expense.getExpenseAmount(), expense.getComment(), dateConverter.getDateInString(expense.getDate()));
                        database.insertExpense(xlExpense);
                        expenseId++;
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled: " + databaseError.getMessage());
                }
            };
            final ChildEventListener customerListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Customer customer = dataSnapshot.getValue(Customer.class);
                    if (customer != null) {
                        String accountType = "Normal";
                        if (customer.isMercantile()) {
                            accountType = "Mercantile";
                        }
                        XLCustomer xlCustomer = new XLCustomer(customer.getCustomerId(), customer.getCustomerName(), customer.getAddress(), customer.getEmail(), customer.getMobile(), accountType, customer.getDue());
                        database.insertCustomer(xlCustomer);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            final ChildEventListener productListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Product product = dataSnapshot.getValue(Product.class);
                    if (product != null) {
                        XLProduct xlProduct = new XLProduct(product.getProductId(), product.getProductName(), product.getProductCode(), product.getProductCategoryId(), product.getDescription());
                        database.insertProduct(xlProduct);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            final ChildEventListener purchaseListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    Purchase purchase = dataSnapshot.getValue(Purchase.class);
                    if (purchase != null) {
                        XLPurchase xlPurchase = new XLPurchase(purchaseId, purchase.getProductId(), purchase.getCompanyName(), purchase.getActualPrice(), purchase.getSellingPrice(), purchase.getQuantity(), dateConverter.getDateInString(purchase.getPurchaseDate()), purchase.getTotalPrice(), purchase.getPaidAmount(), purchase.getDueAmount());
                        database.insertPurchase(xlPurchase);
                        purchaseId++;
                    }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            final ChildEventListener salaryListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Salary salary = dataSnapshot.getValue(Salary.class);
                    if (salary != null) {
                        XLSalary xlSalary = new XLSalary(salaryId, salary.getEmpKey(), salary.getEmpName(), salary.getMonth(), salary.getAmount(), dateConverter.getDateInString(salary.getDate()));
                        database.insertSalary(xlSalary);
                        salaryId++;
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            final ChildEventListener salesListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Sales sales = dataSnapshot.getValue(Sales.class);
                    if (sales != null) {
                        XLSales xlSales = new XLSales(salesId, sales.getCustomerId(), sales.getCustomerName(), dateConverter.getDateInString(sales.getSalesDate()), sales.getSubtotal(), sales.getDiscount(), sales.getTotal(), sales.getPaid(), sales.getDue());
                        database.insertSales(xlSales);
                        salesId++;
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            final ChildEventListener stockHandListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    StockHand stockHand = dataSnapshot.getValue(StockHand.class);
                    if (stockHand != null) {
                        XLStockHand xlStockHand = new XLStockHand(stockHandId, stockHand.getProductId(), stockHand.getPurchaseQuantity(), stockHand.getSellQuantity());
                        database.insertStockHand(xlStockHand);
                        stockHandId++;
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            final ChildEventListener profitListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    profitRef.child(Constant.COST_REF).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            DateAmountCost dateAmountCost = dataSnapshot.getValue(DateAmountCost.class);
                            yearList.add(dateConverter.getYear(dateAmountCost.getDate()));
                            monthList.add(dateConverter.getMonth(dateAmountCost.getDate()));
                            costList.add(dateAmountCost);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    profitRef.child(Constant.SALARY_REF).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            DateAmountSalary dateAmountSalary = dataSnapshot.getValue(DateAmountSalary.class);
                            yearList.add(dateConverter.getYear(dateAmountSalary.getDate()));
                            monthList.add(dateConverter.getMonth(dateAmountSalary.getDate()));
                            salaryList.add(dateAmountSalary);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    profitRef.child(Constant.PURCHASE_REF).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            DateAmountPurchase dateAmountPurchase = dataSnapshot.getValue(DateAmountPurchase.class);
                            yearList.add(dateConverter.getYear(dateAmountPurchase.getDate()));
                            monthList.add(dateConverter.getMonth(dateAmountPurchase.getDate()));
                            purchaseList.add(dateAmountPurchase);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    profitRef.child(Constant.SALES_REF).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            DateAmountSales dateAmountSales = dataSnapshot.getValue(DateAmountSales.class);
                            yearList.add(dateConverter.getYear(dateAmountSales.getDate()));
                            monthList.add(dateConverter.getMonth(dateAmountSales.getDate()));
                            salesList.add(dateAmountSales);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    profitRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (exporting) {
                                for (int year : yearList) {
                                    Log.e(TAG, "Year Size " + yearList.size());
                                    for (int month : monthList) {
                                        Log.e(TAG, "Month Size " + monthList.size());
                                        double totalPurchase = 0;
                                        double totalStockHand = 0;
                                        double totalStockOut = 0;
                                        double totalCost = 0;
                                        double totalSalary = 0;
                                        double totalSales = 0;
                                        double totalProfit = 0;

                                        for (DateAmountPurchase purchase : purchaseList) {
                                            if (month == dateConverter.getMonth(purchase.getDate()) && year == dateConverter.getYear(purchase.getDate())) {
                                                totalPurchase = totalPurchase + purchase.getAmount();
                                            }

                                        }
                                        for (DateAmountSalary salary : salaryList) {
                                            if (month == dateConverter.getMonth(salary.getDate()) && year == dateConverter.getYear(salary.getDate())) {
                                                totalSalary = totalSalary + salary.getAmount();
                                            }
                                        }
                                        for (DateAmountCost cost : costList) {
                                            if (month == dateConverter.getMonth(cost.getDate()) && year == dateConverter.getYear(cost.getDate())) {
                                                totalCost = totalCost + cost.getAmount();
                                            }
                                        }
                                        for (DateAmountSales sales : salesList) {
                                            if (month == dateConverter.getMonth(sales.getDate()) && year == dateConverter.getYear(sales.getDate())) {
                                                totalSales = totalSales + sales.getAmount();
                                                totalStockOut = totalStockOut + sales.getStockOutAmount();
                                            }
                                        }

                                        totalStockHand = totalPurchase - totalStockOut;
                                        totalProfit = (totalSales + totalStockHand) - (totalCost + totalPurchase + totalSalary);
                                        XLProfit xlProfit = new XLProfit(profitId, month, year, totalPurchase, totalStockHand, totalSales, totalCost, totalSalary, totalProfit);
                                        database.insertProfit(xlProfit);
                                        Log.e(TAG, "getXLProfit: " + profitId);
                                        profitId++;
                                    }
                                }
                                exporting = false;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            expenseRef.addChildEventListener(expenseListener);
            customerRef.addChildEventListener(customerListener);
            productRef.addChildEventListener(productListener);
            purchaseRef.addChildEventListener(purchaseListener);
            salaryRef.addChildEventListener(salaryListener);
            salesRef.addChildEventListener(salesListener);
            stockHandRef.addChildEventListener(stockHandListener);
            profitRef.addChildEventListener(profitListener);

            mAdminRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    SQLiteToExcel sqLiteToExcel = new SQLiteToExcel(mContext, DatabaseHelper.DATABASE_NAME);
                    sqLiteToExcel.exportAllTables("stock_management_system.xls", new SQLiteToExcel.ExportListener() {
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onCompleted(String filePath) {
                            dismissProgressDialog();
                            expenseRef.removeEventListener(expenseListener);
                            customerRef.removeEventListener(customerListener);
                            productRef.removeEventListener(productListener);
                            purchaseRef.removeEventListener(purchaseListener);
                            salaryRef.removeEventListener(salaryListener);
                            salesRef.removeEventListener(salesListener);
                            stockHandRef.removeEventListener(stockHandListener);
                            profitRef.removeEventListener(profitListener);
                            Toast.makeText(mContext, filePath, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Exception e) {
                            dismissProgressDialog();
                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }


    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Exporting.....");
        progressDialog.setMessage("Please wait.....");
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

}
