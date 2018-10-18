package com.kcirque.stockmanagementfinal.Fragment;


import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.kcirque.stockmanagementfinal.Adapter.DueDetailsAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Customer;
import com.kcirque.stockmanagementfinal.Database.Model.Salary;
import com.kcirque.stockmanagementfinal.Database.Model.Sales;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentDueDetailsBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DueDetailsFragment extends Fragment {

    private FragmentDueDetailsBinding mBinding;
    private static DueDetailsFragment sInstance;
    private Context mContext;
    private List<Sales> mSalesList = new ArrayList<>();

    public DueDetailsFragment() {
        // Required empty public constructor
    }

    public static synchronized DueDetailsFragment getInstance() {
        if (sInstance == null) {
            sInstance = new DueDetailsFragment();
        }
        return sInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_due_details, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SharedPref sharedPref = new SharedPref(mContext);
        Seller seller = sharedPref.getSeller();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        DatabaseReference adminRef;
        if (user != null) {
            adminRef = rootRef.child(user.getUid());
        } else {
            adminRef = rootRef.child(seller.getAdminUid());
        }
        DatabaseReference salesRef = adminRef.child(Constant.SALES_REF);
        Bundle bundle = getArguments();


        if (bundle != null) {
            final Customer dueCustomer = (Customer) bundle.getSerializable(Constant.EXTRA_CUSTOMER);
            getActivity().setTitle(dueCustomer.getCustomerName());
            mBinding.dueListRecyclerView.setHasFixedSize(true);
            mBinding.dueListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mBinding.customerNameTextTextView.setText(mContext.getResources().getString(R.string.name_text));
            mBinding.customerNameTextView.setText(dueCustomer.getCustomerName());
            mBinding.customerAddressTextTextView.setText(mContext.getResources().getString(R.string.customer_address_text));
            mBinding.customerAddressTextView.setText(dueCustomer.getAddress());
            mBinding.customerMobileTextTextView.setText(mContext.getResources().getString(R.string.customer_mobile_text));
            mBinding.customerMobileTextView.setText(dueCustomer.getMobile());
            mBinding.emailTextTextView.setText(mContext.getResources().getString(R.string.email_text));
            mBinding.emailTextView.setText(dueCustomer.getEmail());
            mBinding.totalDueTextTextView.setText(mContext.getResources().getString(R.string.total_due_text));
            mBinding.totalDueTextView.setText(String.valueOf(dueCustomer.getDue()));

            salesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mSalesList.clear();
                    for (DataSnapshot postData : dataSnapshot.getChildren()) {
                        Sales sales = postData.getValue(Sales.class);
                        if (dueCustomer.getCustomerId() == sales.getCustomerId() && sales.getDue() > 0) {
                            mSalesList.add(sales);
                        }
                    }

                    if (mSalesList.size() > 0) {
                        DueDetailsAdapter adapter = new DueDetailsAdapter(mContext, mSalesList);
                        mBinding.dueListRecyclerView.setAdapter(adapter);
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
    }
}
