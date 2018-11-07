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
import com.kcirque.stockmanagementfinal.Adapter.SalesAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.DateConverter;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.ProductSell;
import com.kcirque.stockmanagementfinal.Database.Model.Sales;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Activity.MainActivity;
import com.kcirque.stockmanagementfinal.databinding.FragmentDailySalesBinding;

import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class DailySalesFragment extends Fragment {
    private FragmentDailySalesBinding mBinding;
    private static DailySalesFragment sInstance;
    private List<ProductSell> mProductSellList = new ArrayList<>();
    private DateConverter mDateConverter;

    private Context mContext;
    private double mTotalSales = 0;


    public DailySalesFragment() {
        // Required empty public constructor
    }

    public static synchronized DailySalesFragment getInstance() {
        if (sInstance == null)
            sInstance = new DailySalesFragment();
        return sInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_daily_sales, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(mContext);
        Seller seller = sharedPref.getSeller();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (MainActivity.isNetworkAvailable(mContext)) {
            mBinding.progressBar.setVisibility(View.VISIBLE);
            mBinding.salesListRecyclerView.setHasFixedSize(true);
            mBinding.salesListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mDateConverter = new DateConverter();
            DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
            DatabaseReference mAdminRef;
            if (user != null) {
                mAdminRef = mRootRef.child(user.getUid());
            } else {
                mAdminRef = mRootRef.child(seller.getAdminUid());
            }
            DatabaseReference salesRef = mAdminRef.child(Constant.SALES_REF);

            salesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mProductSellList.clear();
                    mTotalSales = 0;
                    for (DataSnapshot postData : dataSnapshot.getChildren()) {
                        Sales sales = postData.getValue(Sales.class);
                        if (sales != null && mDateConverter.isToday(sales.getSalesDate())) {
                            mProductSellList.addAll(sales.getSelectedProduct());
                            mTotalSales = mTotalSales + sales.getTotal();
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
                            SalesAdapter adapter = new SalesAdapter(mContext, productSellList);
                            mBinding.salesListRecyclerView.setAdapter(adapter);
                            mBinding.totalLinearLayout.setVisibility(View.VISIBLE);
                            mBinding.totalAmountTextView.setText(String.valueOf(mTotalSales));
                            mBinding.totalTextTextView.setText(mContext.getResources().getString(R.string.total_text));
                        } else {
                            mBinding.progressBar.setVisibility(View.GONE);
                            mBinding.emptySalesTextView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        mBinding.progressBar.setVisibility(View.GONE);
                        mBinding.emptySalesTextView.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mBinding.progressBar.setVisibility(View.GONE);
                    mBinding.emptySalesTextView.setText(databaseError.getMessage());
                    mBinding.emptySalesTextView.setVisibility(View.VISIBLE);
                }
            });
        } else {
            Snackbar.make(mBinding.rootView, "No internet connection", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
