package com.kcirque.stockmanagementfinal.Fragment;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Adapter.StockHandAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Product;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Database.Model.StockHand;
import com.kcirque.stockmanagementfinal.Activity.MainActivity;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentStockHandBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class StockHandFragment extends Fragment {

    private static StockHandFragment sInstance;
    private FragmentStockHandBinding mBinding;

    private static final String TAG = "Stock Management";
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private SharedPref mSharedPref;
    private DatabaseReference mAdminRef;
    private DatabaseReference mRootRef;
    private DatabaseReference mStockRef;
    private DatabaseReference mProductRef;

    private List<StockHand> mStockHandList = new ArrayList<>();
    private List<Product> mProductList = new ArrayList<>();
    private Context mContext;
    private int sellQuantity;

    public StockHandFragment() {
        // Required empty public constructor
    }

    public static synchronized StockHandFragment getInstance() {
        if (sInstance == null)
            sInstance = new StockHandFragment();
        return sInstance;
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
        mSharedPref = new SharedPref(mContext);
        Seller seller = mSharedPref.getSeller();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        getActivity().setTitle("Stock Hand");
        mRootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        if (mUser != null) {
            mAdminRef = mRootRef.child(mUser.getUid());
        } else {
            mAdminRef = mRootRef.child(seller.getAdminUid());
        }
        mStockRef = mAdminRef.child(Constant.STOCK_HAND_REF);
        mProductRef = mAdminRef.child(Constant.PRODUCT_REF);

        mBinding.totalLinearLayout.setVisibility(View.GONE);
        mBinding.progressBar.setVisibility(View.VISIBLE);
        mBinding.stockHandRecyclerView.setHasFixedSize(true);
        mBinding.stockHandRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        if (MainActivity.isNetworkAvailable(mContext)) {
            mStockRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    final StockHand stockHand = dataSnapshot.getValue(StockHand.class);
                    mStockHandList.add(stockHand);
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
            mProductRef.addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Product product = dataSnapshot.getValue(Product.class);
                    mProductList.add(product);
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
            mAdminRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (mStockHandList.size() > 0 && mProductList.size() > 0) {
                        mBinding.progressBar.setVisibility(View.GONE);
                        StockHandAdapter adapter = new StockHandAdapter(mContext, mStockHandList, mProductList);
                        mBinding.stockHandRecyclerView.setAdapter(adapter);
                        int totalPurchase = 0;
                        int totalSale = 0;
                        int totalStockHand = 0;
                        for (StockHand stockHand : mStockHandList) {
                            totalPurchase = totalPurchase + stockHand.getPurchaseQuantity();
                            totalSale = totalSale + stockHand.getSellQuantity();
                            totalStockHand = totalStockHand + (stockHand.getPurchaseQuantity() - stockHand.getSellQuantity());
                        }
                        mBinding.totalPurchaseTextView.setText(String.valueOf(totalPurchase));
                        mBinding.totalSaleTextView.setText(String.valueOf(totalSale));
                        mBinding.totalStockTextView.setText(String.valueOf(totalStockHand));
                        mBinding.totalTextTextView.setText(R.string.total_text);
                        mBinding.totalLinearLayout.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.progressBar.setVisibility(View.GONE);
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
