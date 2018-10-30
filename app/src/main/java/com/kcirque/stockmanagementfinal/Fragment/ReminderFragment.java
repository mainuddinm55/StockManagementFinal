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
import com.kcirque.stockmanagementfinal.Adapter.StockWarningAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Product;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Database.Model.StockHand;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.MainActivity;
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
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private SharedPref mSharedPref;
    private DatabaseReference mAdminRef;
    private DatabaseReference mProductRef;
    private List<Product> mProductList = new ArrayList<>();
    private Context mContext;
    private FragmentLoader mFragmentLoader;
    private PurchaseFragment fragment;
    private Bundle mBundle;

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
        mBundle = null;
        fragment = PurchaseFragment.getInstance();
        fragment.setArguments(mBundle);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mSharedPref = new SharedPref(mContext);
        Seller seller = mSharedPref.getSeller();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        getActivity().setTitle("Reminder");

        mBinding.stockWarningList.setHasFixedSize(true);
        mBinding.stockWarningList.setLayoutManager(new LinearLayoutManager(mContext));

        Bundle bundle = getArguments();

        if (bundle != null) {
            mBinding.progressBar.setVisibility(View.VISIBLE);
            final List<StockHand> stockHandList = (List<StockHand>) bundle.getSerializable(Constant.EXTRA_STOCK_WARNING);
            Log.e(TAG, "Stock Hand Warning List Size " + stockHandList.size());
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
            if (mUser != null) {
                mAdminRef = rootRef.child(mUser.getUid());
            } else {
                mAdminRef = rootRef.child(seller.getAdminUid());
            }
            mProductRef = mAdminRef.child(Constant.PRODUCT_REF);
            if (MainActivity.isNetworkAvailable(mContext)) {
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
                                    mBundle = new Bundle();
                                    mBundle.putSerializable(Constant.EXTRA_PURCHASE_PRODUCT, product);
                                    fragment.setArguments(mBundle);
                                    mFragmentLoader.loadFragment(fragment, true, Constant.PURCHASE_FRAGMENT_TAG);
                                }
                            });
                        } else {
                            mBinding.progressBar.setVisibility(View.GONE);
                            mBinding.emptyReminderTextView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                Snackbar.make(mBinding.rootView, "No internet connection", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentLoader = (FragmentLoader) context;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
    }

}
