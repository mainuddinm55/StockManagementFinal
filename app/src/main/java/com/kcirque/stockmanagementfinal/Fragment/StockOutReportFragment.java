package com.kcirque.stockmanagementfinal.Fragment;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Adapter.StockOutAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.DateConverter;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.ProductSell;
import com.kcirque.stockmanagementfinal.Database.Model.Sales;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.MainActivity;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentStockOutReportBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class StockOutReportFragment extends Fragment {

    private FragmentStockOutReportBinding mBinding;
    private static final String TAG = "Stock Out Report";
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private SharedPref mSharedPref;
    private DatabaseReference mAdminRef;
    private DatabaseReference mRootRef;
    private DatabaseReference mSalesRef;

    private List<Sales> mSalesList = new ArrayList<>();
    private List<ProductSell> mProductSellList = new ArrayList<>();

    private DateConverter mDateConverter;
    private int mStockType;
    private static StockOutReportFragment INSTANCE;
    private Context mContext;

    public StockOutReportFragment() {
        // Required empty public constructor
    }

    public static synchronized StockOutReportFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StockOutReportFragment();
        }

        return INSTANCE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_stock_out_report, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mSharedPref = new SharedPref(mContext);
        Seller seller = mSharedPref.getSeller();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        if (mUser != null) {
            mAdminRef = mRootRef.child(mUser.getUid());
        } else {
            mAdminRef = mRootRef.child(seller.getAdminUid());
        }
        mSalesRef = mAdminRef.child(Constant.SALES_REF);
        mDateConverter = new DateConverter();

        mBinding.stockOutRecyclerView.setHasFixedSize(true);
        mBinding.stockOutRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mBinding.progressBar.setVisibility(View.VISIBLE);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mStockType = bundle.getInt(Constant.EXTRA_STOCK_OUT_TYPE);
            switch (mStockType) {
                case StockOutFragment.TODAY_TYPE:
                    getActivity().setTitle("Today Sales Reports");
                    break;
                case StockOutFragment.DAY_7_TYPE:
                    getActivity().setTitle("7 Day's Sales Reports");
                    break;
                case StockOutFragment.WEEK_TYPE:
                    getActivity().setTitle("Last Week Sales Reports");
                    break;
                case StockOutFragment.DAY_30_TYPE:
                    getActivity().setTitle("30 Day's Sales Reports");
                    break;
                case StockOutFragment.MONTH_TYPE:
                    getActivity().setTitle("Last Month Sales Reports");
                    break;

            }
        }

        if (MainActivity.isNetworkAvailable(mContext)) {
            mSalesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mSalesList.clear();
                    mProductSellList.clear();
                    for (DataSnapshot postData : dataSnapshot.getChildren()) {
                        Sales sales = postData.getValue(Sales.class);
                        if (sales != null) {
                            long saleDate = sales.getSalesDate();
                            switch (mStockType) {
                                case StockOutFragment.TODAY_TYPE:
                                    boolean isToday = mDateConverter.isToday(saleDate);
                                    if (isToday) {
                                        List<ProductSell> productSellList = sales.getSelectedProduct();
                                        mProductSellList.addAll(productSellList);
                                    }
                                    Log.e(TAG, "Date " + mDateConverter.getDateInString(saleDate));
                                    Log.e(TAG, "Is Today " + mDateConverter.isToday(saleDate));
                                    break;
                                case StockOutFragment.DAY_7_TYPE:
                                    if (mDateConverter.getDayCount(saleDate) <= 7) {
                                        List<ProductSell> productSellList = sales.getSelectedProduct();
                                        mProductSellList.addAll(productSellList);
                                    }
                                    Log.e(TAG, "Date " + mDateConverter.getDateInString(saleDate));
                                    Log.e(TAG, "Day Count " + mDateConverter.getDayCount(saleDate));
                                    break;
                                case StockOutFragment.WEEK_TYPE:
                                    if (mDateConverter.isLastWeek(saleDate)) {
                                        List<ProductSell> productSellList = sales.getSelectedProduct();
                                        mProductSellList.addAll(productSellList);
                                    }

                                    Log.e(TAG, "Date " + mDateConverter.getDateInString(saleDate));
                                    Log.e(TAG, "This Week " + mDateConverter.isLastWeek(saleDate));
                                    break;
                                case StockOutFragment.DAY_30_TYPE:
                                    if (mDateConverter.getDayCount(saleDate) <= 30) {
                                        List<ProductSell> productSellList = sales.getSelectedProduct();
                                        mProductSellList.addAll(productSellList);
                                    }
                                    Log.e(TAG, "Date " + mDateConverter.getDateInString(saleDate));
                                    Log.e(TAG, "Day Count " + mDateConverter.getDayCount(saleDate));
                                    break;
                                case StockOutFragment.MONTH_TYPE:
                                    if (mDateConverter.isLastMonth(saleDate)) {
                                        List<ProductSell> productSellList = sales.getSelectedProduct();
                                        mProductSellList.addAll(productSellList);
                                    }
                                    Log.e(TAG, "Date " + mDateConverter.getDateInString(saleDate));
                                    Log.e(TAG, "Is Last Month " + mDateConverter.isLastMonth(saleDate));
                                    break;
                            }
                        }

                    }
                    if (mProductSellList.size() > 0) {
                        Log.e(TAG, "ProductForRoom List Size " + mProductSellList.size());
                        List<ProductSell> productSellList = new ArrayList<>();
                        for (int i = 0; i < mProductSellList.size(); i++) {
                            int productId = mProductSellList.get(i).getProductId();
                            int quantity = mProductSellList.get(i).getQuantity();
                            String name = mProductSellList.get(i).getProductName();
                            double price = mProductSellList.get(i).getPrice();
                            for (int j = 0; j < mProductSellList.size(); j++) {
                                if (i == j) {
                                    continue;
                                }
                                if (mProductSellList.get(i).getProductId() == mProductSellList.get(j).getProductId()) {
                                    quantity = quantity + mProductSellList.get(j).getQuantity();
                                    mProductSellList.remove(mProductSellList.get(j));
                                }
                            }
                            ProductSell productSell = new ProductSell(productId, name, quantity, price);
                            productSellList.add(productSell);
                        }

                        if (productSellList.size() > 0) {
                            mBinding.progressBar.setVisibility(View.GONE);
                            StockOutAdapter adapter = new StockOutAdapter(mContext, productSellList);
                            mBinding.stockOutRecyclerView.setAdapter(adapter);
                        }
                    } else {
                        mBinding.progressBar.setVisibility(View.GONE);
                        mBinding.emptyStockOutTextView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            mBinding.progressBar.setVisibility(View.GONE);
            Snackbar.make(mBinding.rootView, "No internet connection", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
