package com.kcirque.stockmanagementfinal.Fragment;


import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Activity.PurchaseDetailsActivity;
import com.kcirque.stockmanagementfinal.Adapter.PurchaseAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.DateConverter;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Purchase;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Activity.MainActivity;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentDailyPurchaseBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DailyPurchaseFragment extends Fragment {
    private FragmentDailyPurchaseBinding mBinding;
    public static DailyPurchaseFragment sInstance;
    private List<Purchase> mPurchaseList = new ArrayList<>();
    private DateConverter mDateConverter;

    private Context mContext;
    private double mTotalPurchase = 0;

    public DailyPurchaseFragment() {
        // Required empty public constructor
    }

    public static synchronized DailyPurchaseFragment getInstance() {
        if (sInstance == null) {
            sInstance = new DailyPurchaseFragment();
        }
        return sInstance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_daily_purchase, container, false);
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
            mBinding.purchaseListRecyclerView.setHasFixedSize(true);
            mBinding.purchaseListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mDateConverter = new DateConverter();
            DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
            DatabaseReference mAdminRef;
            if (user != null) {
                mAdminRef = mRootRef.child(user.getUid());
            } else {
                mAdminRef = mRootRef.child(seller.getAdminUid());
            }
            DatabaseReference purchaseRef = mAdminRef.child(Constant.PURCHASE_REF);

            purchaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mPurchaseList.clear();
                    mTotalPurchase = 0;
                    for (DataSnapshot postData : dataSnapshot.getChildren()) {
                        Purchase purchase = postData.getValue(Purchase.class);
                        if (purchase != null && mDateConverter.isToday(purchase.getPurchaseDate())) {
                            mPurchaseList.add(purchase);
                            mTotalPurchase = mTotalPurchase + purchase.getTotalPrice();

                        }
                    }

                    if (mPurchaseList.size() > 0) {
                        mBinding.progressBar.setVisibility(View.GONE);
                        PurchaseAdapter adapter = new PurchaseAdapter(mContext, mPurchaseList);
                        mBinding.purchaseListRecyclerView.setAdapter(adapter);
                        mBinding.totalLinearLayout.setVisibility(View.VISIBLE);
                        mBinding.totalAmountTextView.setText(String.valueOf(mTotalPurchase));
                        mBinding.totalTextTextView.setText(mContext.getResources().getString(R.string.total_text));
                        adapter.setItemClickListener(new RecyclerItemClickListener() {
                            @Override
                            public void onClick(View view, int position, Object object) {
                                Purchase purchase = (Purchase) object;
                                Intent intent = new Intent(mContext, PurchaseDetailsActivity.class);
                                intent.putExtra(Constant.EXTRA_PURCHASE, purchase);
                                startActivity(intent);
                            }
                        });
                    } else {
                        mBinding.progressBar.setVisibility(View.GONE);
                        mBinding.emptyPurchaseTextView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mBinding.progressBar.setVisibility(View.GONE);
                    mBinding.emptyPurchaseTextView.setText(databaseError.getMessage());
                    mBinding.emptyPurchaseTextView.setVisibility(View.VISIBLE);
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
