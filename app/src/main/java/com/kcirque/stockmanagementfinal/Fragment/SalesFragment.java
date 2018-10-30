package com.kcirque.stockmanagementfinal.Fragment;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

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
import com.kcirque.stockmanagementfinal.Adapter.CustomerDialogAdapter;
import com.kcirque.stockmanagementfinal.Adapter.ProductSellAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.DateConverter;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Customer;
import com.kcirque.stockmanagementfinal.Database.Model.DateAmountSales;
import com.kcirque.stockmanagementfinal.Database.Model.ProductSell;
import com.kcirque.stockmanagementfinal.Database.Model.Profit;
import com.kcirque.stockmanagementfinal.Database.Model.Sales;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Database.Model.StockHand;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.MainActivity;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.SaleProductActivity;
import com.kcirque.stockmanagementfinal.databinding.FragmentSalesBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalesFragment extends Fragment {

    private static final String TAG = "SalesFragment";
    private static SalesFragment INSTANCE;
    public static final int GET_PRODUCT_REQUEST_CODE = 10;
    private FragmentSalesBinding mBinding;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private SharedPref mSharedPref;
    private DatabaseReference mAdminRef;
    private DatabaseReference mCustomerRef;
    private DatabaseReference mSalesRef;
    private DatabaseReference mStockRef;

    private List<Customer> mCustomerList = new ArrayList<>();
    private List<ProductSell> mProductSellList = new ArrayList<>();
    private CustomerDialogAdapter mAdapter;
    private ProductSellAdapter mProductSellAdapter;

    private DateConverter mDateConverter;
    private int mCustomerId;
    private String mCustomerName;
    private boolean mIsMercantileCustomer = false;
    private long mSalesDate;
    private double mSubTotal = 0.00;
    private double mTotal = 0.00;
    private double mDiscount = 0.00;
    private double mPaid = 0.00;
    private double mDue = 0.00;
    private Context mContext;
    private FragmentLoader mFragmentLoader;
    private String mCustomerKey;
    private double mTotalDue;
    private double mTotalStockOutAmount;
    private ProgressDialog progressDialog;

    public static synchronized SalesFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SalesFragment();
        }

        return INSTANCE;
    }

    public SalesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sales, container, false);
        mProductSellList.clear();
        mSubTotal = 0.00;
        mTotal = 0.00;
        mDiscount = 0.00;
        mPaid = 0.00;
        mDue = 0.00;
        return mBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onViewCreated: ");
        mSharedPref = new SharedPref(mContext);
        Seller seller = mSharedPref.getSeller();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        getActivity().setTitle("Sales Product");
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        if (mUser != null) {
            mAdminRef = mRootRef.child(mUser.getUid());
        } else {
            mAdminRef = mRootRef.child(seller.getAdminUid());
        }
        mCustomerRef = mAdminRef.child(Constant.CUSTOMER_REF);
        mSalesRef = mAdminRef.child(Constant.SALES_REF);
        mStockRef = mAdminRef.child(Constant.STOCK_HAND_REF);
        mDateConverter = new DateConverter();

        mSalesDate = mDateConverter.getCurrentDate();
        mBinding.salesDateTextView.setText(mDateConverter.getDateInString(mDateConverter.getCurrentDate()));
        mBinding.customerNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.isNetworkAvailable(mContext)) {
                    showCustomerDialog();
                } else {
                    Snackbar.make(mBinding.rootView, "No internet connection", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        mBinding.salesDateTextView.setOnClickListener(new View.OnClickListener() {
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
                        mSalesDate = mDateConverter.getDateInUnix(date);
                        mBinding.salesDateTextView.setText(date);
                    }
                }, mDateConverter.getYear(), mDateConverter.getMonth(), mDateConverter.getDay());

                mBinding.customerNameTextView.setError(null);
                mBinding.salesDateTextView.clearFocus();
                datePickerDialog.show();
            }
        });

        mBinding.addProductTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SaleProductActivity.class);
                startActivityForResult(intent, GET_PRODUCT_REQUEST_CODE);
            }
        });
        mBinding.salesProductList.setHasFixedSize(true);
        mBinding.salesProductList.setLayoutManager(new LinearLayoutManager(mContext));
        mProductSellAdapter = new ProductSellAdapter(mContext, mProductSellList);
        mBinding.salesProductList.setAdapter(mProductSellAdapter);

        mBinding.subTotalAmountTextView.setText(String.valueOf(mSubTotal));
        mBinding.totalAmountTextview.setText(String.valueOf(mTotal));
        mBinding.dueAmountTextview.setText(String.valueOf(mDue));

        mBinding.discountAmountTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    mDiscount = Double.parseDouble(s.toString());
                    mTotal = mSubTotal - mDiscount;
                    mDue = mTotal - mPaid;
                    mBinding.totalAmountTextview.setText(String.valueOf(mTotal));
                    mBinding.dueAmountTextview.setText(String.valueOf(mDue));
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
                if (!s.toString().isEmpty()) {
                    mPaid = Double.parseDouble(s.toString());
                    mDue = mTotal - mPaid;
                    mBinding.dueAmountTextview.setText(String.valueOf(mDue));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.sellProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mBinding.customerNameTextView.getText().toString().isEmpty()) {
                    mBinding.customerNameTextView.setError("Customer name required");
                    mBinding.customerNameTextView.requestFocus();
                    Snackbar.make(v, "Select Customer", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (mBinding.salesDateTextView.getText().toString().isEmpty()) {
                    mBinding.customerNameTextView.setError("Sales date required");
                    mBinding.salesDateTextView.requestFocus();
                    return;
                }
                if (mSalesDate < 0) {
                    mBinding.customerNameTextView.setError("Sales date required");
                    mBinding.salesDateTextView.requestFocus();
                    return;
                }
                if (mProductSellList.size() < 1) {
                    mBinding.addProductTextView.setError("Product required");
                    mBinding.addProductTextView.requestFocus();
                    Snackbar.make(v, "Please add Product", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (!mIsMercantileCustomer) {
                    if (mPaid != mTotal) {
                        mBinding.paidAmountEdittext.setError("Must be full payment");
                        mBinding.paidAmountEdittext.requestFocus();
                        return;
                    }
                }
                if (mIsMercantileCustomer && mPaid <= 0) {
                    AlertDialog.Builder warningDialog = new AlertDialog.Builder(mContext);
                    warningDialog.setTitle("Paid warning");
                    warningDialog.setMessage("do you want to sell without no payment");
                    warningDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (MainActivity.isNetworkAvailable(mContext)) {
                                sellProduct();
                            } else {
                                Snackbar.make(mBinding.rootView, "No internet connection", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                    warningDialog.setNegativeButton("No", null);
                    warningDialog.setNeutralButton("Cancel", null);
                    warningDialog.show();
                    return;
                }
                if (MainActivity.isNetworkAvailable(mContext)) {
                    sellProduct();
                } else {
                    Snackbar.make(mBinding.rootView, "No internet connection", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated: ");
    }


    private void showCustomerDialog() {
        AlertDialog.Builder customerDialog = new AlertDialog.Builder(mContext);
        View v = getLayoutInflater().inflate(R.layout.customer_dialog, null, false);
        customerDialog.setView(v);
        final RecyclerView customerRecyclerView = v.findViewById(R.id.customer_list_recycler_view);
        TextView titleTextView = v.findViewById(R.id.text_view_list_item);
        titleTextView.setText("Customers");
        customerRecyclerView.setHasFixedSize(true);
        customerRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        final AlertDialog dialog = customerDialog.create();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mCustomerRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mCustomerList.clear();
                        for (DataSnapshot postData : dataSnapshot.getChildren()) {
                            Customer customer = postData.getValue(Customer.class);
                            mCustomerList.add(customer);
                        }
                        if (mCustomerList.size() > 0) {
                            mAdapter = new CustomerDialogAdapter(mContext, mCustomerList);
                            customerRecyclerView.setAdapter(mAdapter);
                            mAdapter.setItemClickListener(new RecyclerItemClickListener() {
                                @Override
                                public void onClick(View view, int position, Object object) {
                                    mIsMercantileCustomer = mCustomerList.get(position).isMercantile();
                                    mTotalDue = mTotalDue + mCustomerList.get(position).getDue();
                                    mCustomerId = mCustomerList.get(position).getCustomerId();
                                    mCustomerKey = mCustomerList.get(position).getKey();
                                    mCustomerName = mCustomerList.get(position).getCustomerName();
                                    mBinding.customerNameTextView.setText(mCustomerName);
                                    mBinding.customerNameTextView.setError(null);
                                    mBinding.customerNameTextView.clearFocus();
                                    dialog.dismiss();
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }).start();


        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_PRODUCT_REQUEST_CODE && data != null) {
            ProductSell productSell = (ProductSell) data.getSerializableExtra(Constant.EXTRA_PRODUCT_SELL);
            double buyPrice = data.getDoubleExtra(Constant.EXTRA_BUY_PRICE, 0);
            /*if (mProductSellList.size() > 0) {
                for (ProductSell sell : mProductSellList){
                    if (sell.getProductId()==productSell.getProductId()){

                    }
                }
            }*/
            mSubTotal = mSubTotal + (productSell.getQuantity() * productSell.getPrice());
            mTotal = mSubTotal - mDiscount;
            mDue = mTotal - mPaid;
            mTotalStockOutAmount = mTotalStockOutAmount + buyPrice;
            mBinding.totalAmountTextview.setText(String.valueOf(mTotal));
            mProductSellList.add(productSell);
            mBinding.subTotalAmountTextView.setText(String.valueOf(mSubTotal));
            mBinding.dueAmountTextview.setText(String.valueOf(mDue));
            mBinding.addProductTextView.setError(null);
            mBinding.addProductTextView.clearFocus();
            mProductSellAdapter.notifyDataSetChanged();
        }
    }

    private void sellProduct() {
        showProgressDialog();
        String key = mSalesRef.push().getKey();
        Sales sales = new Sales(key, mCustomerId, mCustomerName, mSalesDate, mProductSellList, mSubTotal, mDiscount, mTotal, mPaid, mDue);
        mSalesRef.child(key).setValue(sales).addOnCompleteListener(new OnCompleteListener<Void>() {
            private int sellQuantity;

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (mDue > 0) {
                        mTotalDue = mTotalDue + mDue;
                        mCustomerRef.child(mCustomerKey).child("due").setValue(mTotalDue);
                        mCustomerRef.child(mCustomerKey).child("dueDate").setValue(mSalesDate);
                    }
                    for (final ProductSell productSell : mProductSellList) {
                        mStockRef.child(String.valueOf(productSell.getProductId())).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                StockHand stockHand = dataSnapshot.getValue(StockHand.class);
                                sellQuantity = stockHand.getSellQuantity();
                                mStockRef.child(String.valueOf(productSell.getProductId())).child("sellQuantity").setValue(sellQuantity + productSell.getQuantity());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    DateAmountSales dateAmountSales = new DateAmountSales(mSalesDate, mTotal, mTotalStockOutAmount);
                    DatabaseReference profitRef = mAdminRef.child(Constant.PROFIT_REF);
                    DatabaseReference salesRef = profitRef.child(Constant.SALES_REF);
                    salesRef.push().setValue(dateAmountSales).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Snackbar.make(mBinding.rootView, "Product Sell", Snackbar.LENGTH_SHORT).show();
                                dismissProgressDialog();
                                mFragmentLoader.loadFragment(HomeFragment.getInstance(), false, Constant.HOME_FRAGMENT_TAG);
                            }
                        }
                    });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(mBinding.rootView, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                dismissProgressDialog();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(TAG, "onAttach: ");
        mContext = context;
        mFragmentLoader = (FragmentLoader) context;
    }


    @Override
    public void onPause() {
        mBinding.paidAmountEdittext.setText(null);
        mBinding.dueAmountTextview.setText(null);
        super.onPause();
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Loading.....");
        progressDialog.setMessage("Please wait.....");
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }
}
