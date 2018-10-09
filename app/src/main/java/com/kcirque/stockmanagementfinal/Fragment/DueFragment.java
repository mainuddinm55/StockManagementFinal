package com.kcirque.stockmanagementfinal.Fragment;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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
import com.kcirque.stockmanagementfinal.Adapter.DueAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Customer;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentDueBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DueFragment extends Fragment {

    private FragmentDueBinding mBinding;
    private static DueFragment INSTANCE;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private SharedPref mSharedPref;
    private DatabaseReference mAdminRef;

    private DatabaseReference mCustomerRef;
    private List<Customer> mCustomerList = new ArrayList<>();
    private double mTotalDue = 0;
    private Context mContext;


    public DueFragment() {
        // Required empty public constructor
    }

    public static synchronized DueFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DueFragment();
        }

        return INSTANCE;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_due, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mSharedPref = new SharedPref(mContext);
        Seller seller = mSharedPref.getSeller();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        if (mUser != null) {
            mAdminRef = rootRef.child(mUser.getUid());
        } else {
            mAdminRef = rootRef.child(seller.getAdminUid());
        }
        mCustomerRef = mAdminRef.child(Constant.CUSTOMER_REF);

        mBinding.progressBar.setVisibility(View.VISIBLE);
        mBinding.dueListRecyclerView.setHasFixedSize(true);
        mBinding.dueListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCustomerList.clear();
                mTotalDue = 0;
                for (DataSnapshot postData : dataSnapshot.getChildren()) {
                    Customer customer = postData.getValue(Customer.class);
                    if (customer.getDue() > 0) {
                        mCustomerList.add(customer);
                        mTotalDue = mTotalDue + customer.getDue();
                    }
                }

                if (mCustomerList.size() > 0) {
                    mBinding.progressBar.setVisibility(View.GONE);
                    mBinding.dueLayout.setVisibility(View.VISIBLE);
                    DueAdapter adapter = new DueAdapter(mContext, mCustomerList);
                    mBinding.dueListRecyclerView.setAdapter(adapter);
                    mBinding.totalDueTextTextView.setText(mContext.getResources().getString(R.string.total_text));
                    mBinding.totalDueAmountTextView.setText(String.valueOf(mTotalDue));
                } else {
                    mBinding.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mBinding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
