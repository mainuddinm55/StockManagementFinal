package com.kcirque.stockmanagementfinal.Fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.kcirque.stockmanagementfinal.Adapter.CustomerListAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Customer;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.MainActivity;
import com.kcirque.stockmanagementfinal.databinding.FragmentCustomerListBinding;

import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerListFragment extends Fragment {

    private static CustomerListFragment INSTANCE;
    private FragmentCustomerListBinding mBinding;

    private DatabaseReference mRootRef;
    private DatabaseReference mCustomerRef;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private SharedPref mSharedPref;
    private DatabaseReference mAdminRef;

    private List<Customer> mCustomerList = new ArrayList<>();
    private Context mContext;
    private FragmentLoader mFragmentLoader;

    public static synchronized CustomerListFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CustomerListFragment();
        }

        return INSTANCE;
    }


    public CustomerListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_customer_list, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if (MainActivity.isNetworkAvailable(getContext())) {
            mSharedPref = new SharedPref(mContext);
            Seller seller = mSharedPref.getSeller();
            mAuth = FirebaseAuth.getInstance();
            mUser = mAuth.getCurrentUser();
            mRootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
            getActivity().setTitle("Customer List");
            if (mUser != null) {
                mAdminRef = mRootRef.child(mUser.getUid());
            } else {
                mAdminRef = mRootRef.child(seller.getAdminUid());
            }
            mCustomerRef = mAdminRef.child(Constant.CUSTOMER_REF);
            mCustomerRef.keepSynced(true);

            mBinding.customerListRecyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            mBinding.customerListRecyclerView.setLayoutManager(layoutManager);


            mBinding.progressBar.setVisibility(View.VISIBLE);
            mCustomerRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mCustomerList.clear();
                    for (DataSnapshot postData : dataSnapshot.getChildren()) {
                        Customer customer = postData.getValue(Customer.class);
                        mCustomerList.add(customer);
                    }
                    if (mCustomerList.size() > 0) {
                        mBinding.progressBar.setVisibility(View.GONE);
                        CustomerListAdapter adapter = new CustomerListAdapter(mContext, mCustomerList);
                        mBinding.customerListRecyclerView.setAdapter(adapter);
                        adapter.setItemClickListener(new RecyclerItemClickListener() {
                            @Override
                            public void onClick(View view, int position, Object object) {
                                Customer customer = (Customer) object;
                                CustomerDetailsFragment fragment = CustomerDetailsFragment.getInstance();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constant.EXTRA_CUSTOMER, customer);
                                fragment.setArguments(bundle);
                                mFragmentLoader.loadFragment(fragment, true, Constant.CUSTOMER_DETAILS_TAG);
                            }
                        });
                    } else {
                        mBinding.progressBar.setVisibility(View.GONE);
                        mBinding.emptyCustomerTextview.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            mBinding.addCustomerFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFragmentLoader.loadFragment(CustomerAddFragment.getInstance(), true, Constant.CUSTOMER_ADD_FRAGMENT_TAG);
                }
            });
        } else {
            Toast.makeText(mContext, "No internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentLoader = (FragmentLoader) context;
    }
}
