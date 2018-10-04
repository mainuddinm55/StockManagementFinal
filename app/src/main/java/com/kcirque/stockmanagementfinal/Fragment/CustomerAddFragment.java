package com.kcirque.stockmanagementfinal.Fragment;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Database.Model.Customer;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.databinding.FragmentCustomerAddBinding;

import com.kcirque.stockmanagementfinal.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerAddFragment extends Fragment {

    private FragmentLoader mFragmentLoader;
    private static CustomerAddFragment INSTANCE;
    private FragmentCustomerAddBinding mBinding;

    private int mCustomerId = 1;

    private DatabaseReference mRootRef;
    private DatabaseReference mCustomerRef;
    private boolean mIsAdvancedPaid = false;

    public static synchronized CustomerAddFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CustomerAddFragment();
        }
        return INSTANCE;
    }


    public CustomerAddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_customer_add, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mRootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        mRootRef.keepSynced(true);
        mCustomerRef = mRootRef.child(Constant.CUSTOMER_REF);
        mCustomerRef.keepSynced(true);

        mCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCustomerId = (int) (dataSnapshot.getChildrenCount() + 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mBinding.addCustBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (mBinding.customerNameEdittext.getText().toString().isEmpty()) {
                            mBinding.customerNameEdittext.setError("Customer name required");
                            mBinding.customerNameEdittext.requestFocus();
                            return;
                        }
                        if (mBinding.customerMobileEdittext.getText().toString().isEmpty()) {
                            mBinding.customerMobileEdittext.setError("Customer mobile required");
                            mBinding.customerMobileEdittext.requestFocus();
                            return;
                        }
                        if (mIsAdvancedPaid) {
                            if (mBinding.advancePaidAmountEditText.getText().toString().trim().isEmpty()) {
                                mBinding.advancePaidAmountEditText.setError("Enter Amount");
                                mBinding.advancePaidAmountEditText.requestFocus();
                                return;
                            }
                        }

                        String key = mCustomerRef.push().getKey();
                        String name = mBinding.customerNameEdittext.getText().toString().trim();
                        String address = mBinding.customerAddressEdittext.getText().toString().trim();
                        String mobile = mBinding.customerMobileEdittext.getText().toString().trim();
                        String email = mBinding.customerEmailEdittext.getText().toString().trim();
                        double advancedPaid;
                        if (mIsAdvancedPaid) {
                            advancedPaid = Double.parseDouble(mBinding.advancePaidAmountEditText.getText().toString().trim());
                        } else {
                            advancedPaid = 0.00;
                        }

                        Customer customer = new Customer(key, mCustomerId, name, address, mobile, email, advancedPaid);

                        mCustomerRef.child(key).setValue(customer).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Snackbar.make(mBinding.rootView, "Customer Added", Snackbar.LENGTH_SHORT).show();
                                    mFragmentLoader.loadFragment(CustomerListFragment.getInstance(), true);
                                }
                            }
                        });
                    }
                }).start();


            }
        });


        mBinding.isPaidAdvanceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mIsAdvancedPaid = true;
                    mBinding.advancePaidAmountLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    mIsAdvancedPaid = false;
                    mBinding.advancePaidAmountLinearLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFragmentLoader = (FragmentLoader) context;
    }
}
