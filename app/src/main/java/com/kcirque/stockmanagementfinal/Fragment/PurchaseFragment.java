package com.kcirque.stockmanagementfinal.Fragment;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Adapter.AutoCompleteProductAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.DateConverter;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Product;
import com.kcirque.stockmanagementfinal.Database.Model.Purchase;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Database.Model.StockHand;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.MainActivity;
import com.kcirque.stockmanagementfinal.databinding.FragmentPurchaseBinding;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PurchaseFragment extends Fragment {

    private static PurchaseFragment INSTANCE;

    private FragmentPurchaseBinding mBinding;

    private static final String TAG = "Spinner Item Selected";

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private SharedPref mSharedPref;
    private DatabaseReference mAdminRef;
    private DatabaseReference mProductRef;
    private DatabaseReference mPurchaseRef;
    private DatabaseReference mStockRef;

    private List<Product> mProductList = new ArrayList<>();

    private int mProductId;
    private int mQuantity;
    private double mBuyPrice;
    private long mPurchaseDate;
    private double mTotalAmount;
    private double mDueAmount;

    private Calendar mCalender = Calendar.getInstance();
    private int mYear = mCalender.get(Calendar.YEAR);
    private int mMonth = mCalender.get(Calendar.MONTH);
    private int mDay = mCalender.get(Calendar.DAY_OF_MONTH);

    private DateConverter mDateConverter;
    private Context mContext;
    private FragmentLoader mFragmentLoader;
    private String mProductKey;

    public static synchronized PurchaseFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PurchaseFragment();
        }

        return INSTANCE;
    }

    public PurchaseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_purchase, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mSharedPref = new SharedPref(mContext);
        Seller seller = mSharedPref.getSeller();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        mRootRef.keepSynced(true);
        if (mUser != null) {
            mAdminRef = mRootRef.child(mUser.getUid());
        } else {
            mAdminRef = mRootRef.child(seller.getAdminUid());
        }
        mProductRef = mAdminRef.child(Constant.PRODUCT_REF);
        mProductRef.keepSynced(true);
        mPurchaseRef = mAdminRef.child(Constant.PURCHASE_REF);
        mPurchaseRef.keepSynced(true);
        mStockRef = mAdminRef.child(Constant.STOCK_HAND_REF);


        mDateConverter = new DateConverter();

        Bundle bundle = getArguments();
        if (bundle != null) {
            Product product = (Product) bundle.getSerializable(Constant.EXTRA_PURCHASE_PRODUCT);
            mBinding.productNameAct.setText(product.getProductName());
            mProductId = product.getProductId();

        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                mProductRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mProductList.clear();
                        for (DataSnapshot postData : dataSnapshot.getChildren()) {
                            Product product = postData.getValue(Product.class);
                            mProductList.add(product);
                        }

                        if (mProductList.size() > 0) {
                            AutoCompleteProductAdapter aptAdapter = new AutoCompleteProductAdapter(mContext, mProductList);
                            mBinding.productNameAct.setAdapter(aptAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }).start();

        mBinding.productNameAct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mProductId = ((Product) parent.getItemAtPosition(position)).getProductId();
                mBinding.productNameAct.setText(((Product) parent.getItemAtPosition(position)).getProductName());
                Log.e(TAG, "onItemClick: " + ((Product) parent.getItemAtPosition(position)).getProductName());
            }
        });

        mBinding.quantityEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    mQuantity = Integer.parseInt(s.toString());
                    mTotalAmount = mQuantity * mBuyPrice;
                    mBinding.totalAmountTextview.setText(String.valueOf(mTotalAmount));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.buyPriceEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!s.toString().isEmpty()) {
                        mBuyPrice = Double.parseDouble(String.valueOf(s));
                        mTotalAmount = mQuantity * mBuyPrice;
                        mBinding.totalAmountTextview.setText(String.valueOf(mTotalAmount));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.paidAmountEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!s.equals("")) {
                        mDueAmount = mTotalAmount - Double.parseDouble(String.valueOf(s));
                        mBinding.dueAmountTextview.setText(String.valueOf(mDueAmount));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.purchaseDateEdittext.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    mBinding.purchaseDateEdittext.setText(current);
                    mBinding.purchaseDateEdittext.setSelection(sel < current.length() ? sel : current.length());
                    mPurchaseDate = mDateConverter.getDateInUnix(current);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.purchaseDateEdittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date;

                        if (month < 9) {
                            if (dayOfMonth < 9) {
                                date = "0" + dayOfMonth + "/" + "0" + (month + 1) + "/" + year;
                            } else {
                                date = dayOfMonth + "/" + "0" + (month + 1) + "/" + year;
                            }
                        } else {
                            if (dayOfMonth < 9) {
                                date = "0" + dayOfMonth + "/" + (month + 1) + "/" + year;
                            } else {
                                date = dayOfMonth + "/" + (month + 1) + "/" + year;
                            }

                        }
                        mPurchaseDate = mDateConverter.getDateInUnix(date);
                        mBinding.purchaseDateEdittext.setText(date);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        mBinding.productNameAct.setValidator(new AutoCompleteTextView.Validator() {
            @Override
            public boolean isValid(CharSequence text) {
                for (Product product : mProductList) {
                    if (product.getProductName().toLowerCase().equals(text.toString().toLowerCase())) {
                        mProductKey = product.getKey();
                        return true;
                    } else
                        return false;
                }
                return false;
            }

            @Override
            public CharSequence fixText(CharSequence invalidText) {
                mBinding.productNameAct.setError("Enter Valid Product Name");
                mBinding.productNameAct.requestFocus();
                return "";
            }
        });

        mBinding.addPurchaseBtn.setOnClickListener(new View.OnClickListener() {
            private int quantity;

            @Override
            public void onClick(View v) {

                if (mBinding.productNameAct.getText().toString().length() < 1) {
                    mBinding.productNameAct.setError("Product Name Required");
                    mBinding.productNameAct.requestFocus();
                    return;
                } else if (!mBinding.productNameAct.getValidator().isValid(mBinding.productNameAct.getText())) {
                    mBinding.productNameAct.setError("this product not found. add this product before purchase");
                    mBinding.productNameAct.requestFocus();
                    return;
                }

                if (mBinding.companyNameEdittext.getText().toString().isEmpty()) {
                    mBinding.companyNameEdittext.setError("Company name required");
                    mBinding.companyNameEdittext.requestFocus();
                    return;
                }
                if (mBinding.quantityEdittext.getText().toString().length() < 1) {
                    mBinding.quantityEdittext.setError("Quantity required");
                    mBinding.quantityEdittext.requestFocus();
                    return;
                }
                if (mBinding.buyPriceEdittext.getText().toString().length() < 1) {
                    mBinding.buyPriceEdittext.setError("Buy price required");
                    mBinding.buyPriceEdittext.requestFocus();
                    return;
                }
                if (mBinding.sellPriceEdittext.getText().toString().length() < 1) {
                    mBinding.sellPriceEdittext.setError("Sell price required");
                    mBinding.sellPriceEdittext.requestFocus();
                    return;
                }
                if (mBinding.purchaseDateEdittext.getText().toString().length() < 1) {
                    mBinding.purchaseDateEdittext.setError("Purchase date required");
                    mBinding.purchaseDateEdittext.requestFocus();
                    return;
                }
                if (mBinding.paidAmountEdittext.getText().toString().length() < 1) {
                    mBinding.paidAmountEdittext.setError("Paid amount required");
                    mBinding.paidAmountEdittext.requestFocus();
                    return;
                }
                String companyName = mBinding.companyNameEdittext.getText().toString().trim();
                double sellPrice = Double.parseDouble(mBinding.sellPriceEdittext.getText().toString());
                double paidAmt = Double.parseDouble(mBinding.paidAmountEdittext.getText().toString());
                String key = mPurchaseRef.push().getKey();
                final Purchase purchase = new Purchase(key, mProductId, companyName, mBuyPrice, sellPrice, mQuantity, mPurchaseDate, mTotalAmount, paidAmt, mDueAmount);
                mPurchaseRef.child(String.valueOf(mProductId)).child(key).setValue(purchase).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mStockRef.child(String.valueOf(mProductId)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    StockHand stockHand = dataSnapshot.getValue(StockHand.class);
                                    quantity = stockHand.getPurchaseQuantity();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            Constant.SELECTED_PRODUCT_ID = mProductId;
                            mProductRef.child(mProductKey).child("buyPrice").setValue(purchase.getActualPrice());
                            mProductRef.child(mProductKey).child("sellPrice").setValue(purchase.getSellingPrice());
                            mProductRef.child(mProductKey).child("company").setValue(purchase.getCompanyName());
                            Snackbar.make(mBinding.rootView, "Purchase Added", Snackbar.LENGTH_SHORT).show();
                            mStockRef.child(String.valueOf(mProductId)).child("buyPrice").setValue(purchase.getActualPrice());
                            mStockRef.child(String.valueOf(mProductId)).child("purchaseQuantity").setValue(quantity+mQuantity);
                            mFragmentLoader.loadFragment(HomeFragment.getInstance(), false,Constant.HOME_FRAGMENT_TAG);
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentLoader = (FragmentLoader) context;
    }
}
