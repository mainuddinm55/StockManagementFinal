package com.kcirque.stockmanagementfinal.Fragment;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Adapter.StockWarningAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Database.Model.Product;
import com.kcirque.stockmanagementfinal.Database.Model.StockHand;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentReminderBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReminderFragment extends Fragment {

    private static ReminderFragment INSTANCE;

    private FragmentReminderBinding mBinding;

    private static final String TAG = "Reminder Hock";
    private DatabaseReference mProductRef;
    private List<Product> mProductList = new ArrayList<>();
    private Context mContext;
    private FragmentLoader mFragmentLoader;

    public ReminderFragment() {
        // Required empty public constructor
    }


    public static synchronized ReminderFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ReminderFragment();
        }

        return INSTANCE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reminder, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mBinding.stockWarningList.setHasFixedSize(true);
        mBinding.stockWarningList.setLayoutManager(new LinearLayoutManager(mContext));

        Bundle bundle = getArguments();

        if (bundle != null) {
            mBinding.progressBar.setVisibility(View.VISIBLE);
            final List<StockHand> stockHandList = (List<StockHand>) bundle.getSerializable(Constant.EXTRA_STOCK_WARNING);
            Log.e(TAG, "Stock Hand Warning List Size " + stockHandList.size());
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
            mProductRef = rootRef.child(Constant.PRODUCT_REF);
            mProductRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mProductList.clear();
                    for (DataSnapshot postData : dataSnapshot.getChildren()) {
                        Product product = postData.getValue(Product.class);
                        for (StockHand stockHand : stockHandList) {
                            if (product.getProductId() == stockHand.getProductId()) {
                                mProductList.add(product);
                            }
                        }
                    }

                    if (stockHandList.size() > 0 && mProductList.size() > 0) {
                        Log.e(TAG, "Array Size" + stockHandList.size() + " " + mProductList.size());
                        StockWarningAdapter adapter = new StockWarningAdapter(mContext, stockHandList, mProductList);
                        mBinding.stockWarningList.setAdapter(adapter);
                        mBinding.progressBar.setVisibility(View.GONE);
                        //stockHandList.clear();
                        adapter.setRecyclerItemClickListener(new RecyclerItemClickListener() {
                            @Override
                            public void onClick(View view, int position, Object object) {
                                Product product = (Product) object;
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constant.EXTRA_PURCHASE_PRODUCT, product);
                                PurchaseFragment fragment = PurchaseFragment.getInstance();
                                fragment.setArguments(bundle);
                                mFragmentLoader.loadFragment(fragment, true);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentLoader = (FragmentLoader) context;
    }
}
