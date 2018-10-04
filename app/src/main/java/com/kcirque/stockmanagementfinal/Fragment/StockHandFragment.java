package com.kcirque.stockmanagementfinal.Fragment;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Adapter.StockHandAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Database.Model.Product;
import com.kcirque.stockmanagementfinal.Database.Model.ProductSell;
import com.kcirque.stockmanagementfinal.Database.Model.Purchase;
import com.kcirque.stockmanagementfinal.Database.Model.Sales;
import com.kcirque.stockmanagementfinal.Database.Model.StockHand;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentStockHandBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class StockHandFragment extends Fragment {

    private FragmentStockHandBinding mBinding;
    private static StockHandFragment INSTANCE;

    private static final String TAG = "Stock Management";
    private DatabaseReference mRootRef;
    private DatabaseReference mPurchaseRef;
    private DatabaseReference mSalesRef;
    private DatabaseReference mStockRef;
    private DatabaseReference mProductRef;

    private List<StockHand> mStockHandList = new ArrayList<>();
    private List<Product> mProductList = new ArrayList<>();
    private Context mContext;

    public static synchronized StockHandFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StockHandFragment();
        }

        return INSTANCE;
    }

    public StockHandFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_stock_hand, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        mRootRef.keepSynced(true);
        mPurchaseRef = mRootRef.child(Constant.PURCHASE_REF);
        mPurchaseRef.keepSynced(true);
        mSalesRef = mRootRef.child(Constant.SALES_REF);
        mSalesRef.keepSynced(true);
        mStockRef = mRootRef.child(Constant.STOCK_HAND_REF);
        mStockRef.keepSynced(true);
        mProductRef = mRootRef.child(Constant.PRODUCT_REF);
        mProductRef.keepSynced(true);

        mBinding.totalLinearLayout.setVisibility(View.GONE);
        mBinding.progressBar.setVisibility(View.VISIBLE);
        mBinding.stockHandRecyclerView.setHasFixedSize(true);
        mBinding.stockHandRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        new Thread(new Runnable() {
            @Override
            public void run() {
                mPurchaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postData : dataSnapshot.getChildren()) {
                            int productId = Integer.parseInt(postData.getKey());
                            Log.e(TAG, "onDataChange: " + postData.getKey());
                            int purchaseQty = 0;
                            double buyPrice = 0;
                            for (DataSnapshot data : postData.getChildren()) {
                                Purchase purchase = data.getValue(Purchase.class);
                                Log.e(TAG, "Product Id " + purchase.getProductId() + " Purchase Quantity " + purchase.getQuantity());
                                productId = purchase.getProductId();
                                purchaseQty = purchaseQty + purchase.getQuantity();
                                buyPrice = purchase.getActualPrice();
                                //mStockHand.add(new StockHand(purchase.getProductId(), purchase.getQuantity(), 0));
                            }
                            StockHand stockHand = new StockHand(productId, purchaseQty, buyPrice, 0);
                            mStockRef.child(String.valueOf(stockHand.getProductId())).setValue(stockHand)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.e(TAG, "Add Stock Purchase Successful");
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mStockRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mStockHandList.clear();
                        for (DataSnapshot postData : dataSnapshot.getChildren()) {
                            final StockHand stockHand = postData.getValue(StockHand.class);
                            mStockHandList.add(stockHand);
                            mSalesRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Sales sales = null;
                                    for (DataSnapshot postData : dataSnapshot.getChildren()) {
                                        sales = postData.getValue(Sales.class);
                                        if (sales != null) {
                                            for (ProductSell productSell : sales.getSelectedProduct()) {
                                                if (productSell != null) {
                                                    if (stockHand.getProductId() == productSell.getProductId()) {
                                                        mStockRef.child(String.valueOf(stockHand.getProductId()))
                                                                .child("sellQuantity").setValue(productSell.getQuantity())
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Log.e(TAG, "Sell Quantity Added Success");
                                                                    }
                                                                });
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                        mProductRef.orderByChild("productId")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        mProductList.clear();
                                        for (DataSnapshot postData : dataSnapshot.getChildren()) {
                                            Product product = postData.getValue(Product.class);
                                            mProductList.add(product);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                        if (mStockHandList.size() > 0 && mProductList.size() > 0) {
                            mBinding.progressBar.setVisibility(View.GONE);
                            StockHandAdapter adapter = new StockHandAdapter(mContext, mStockHandList, mProductList);
                            int totalPurchase = 0;
                            int totalSale = 0;
                            int totalStockHand = 0;
                            for (StockHand stockHand : mStockHandList) {
                                totalPurchase = totalPurchase + stockHand.getPurchaseQuantity();
                                totalSale = totalSale + stockHand.getSellQuantity();
                                totalStockHand = totalStockHand + (stockHand.getPurchaseQuantity() - stockHand.getSellQuantity());
                            }
                            mBinding.stockHandRecyclerView.setAdapter(adapter);
                            mBinding.totalPurchaseTextView.setText(String.valueOf(totalPurchase));
                            mBinding.totalSaleTextView.setText(String.valueOf(totalSale));
                            mBinding.totalStockTextView.setText(String.valueOf(totalStockHand));
                            mBinding.totalTextTextView.setText(R.string.total_text);
                            mBinding.totalLinearLayout.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }).start();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
