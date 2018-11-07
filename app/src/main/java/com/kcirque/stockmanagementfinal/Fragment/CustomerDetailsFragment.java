package com.kcirque.stockmanagementfinal.Fragment;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Database.Model.Customer;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentCustomerDetailsBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerDetailsFragment extends Fragment {
    private FragmentCustomerDetailsBinding mBinding;
    private static CustomerDetailsFragment instance;

    private Context mContext;
    private FragmentLoader mFragmentLoader;

    public static synchronized CustomerDetailsFragment getInstance() {
        if (instance == null)
            instance = new CustomerDetailsFragment();
        return instance;
    }

    public CustomerDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_customer_details, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final Bundle bundle = getArguments();
        if (bundle != null) {
            final Customer customer = (Customer) bundle.getSerializable(Constant.EXTRA_CUSTOMER);
            mBinding.customerNameTextView.setText(customer.getCustomerName());
            mBinding.addressTextView.setText(customer.getAddress());
            mBinding.mobileTextView.setText(customer.getMobile());
            mBinding.emailTextView.setText(customer.getEmail());
            mBinding.accountTypeTextView.setText(customer.isMercantile() ? "Mercantile" : "Normal");
            mBinding.dueLayout.setVisibility(customer.getDue() > 0 ? View.VISIBLE : View.GONE);
            mBinding.dueTextView.setText(String.valueOf(customer.getDue()));
            Glide.with(mContext).load(customer.getImageUrl()).apply(RequestOptions.placeholderOf(R.drawable.ic_user))
                    .into(mBinding.customerImage);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                mBinding.updateCustomerBtn.setVisibility(View.VISIBLE);
            }
            mBinding.updateCustomerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomerAddFragment fragment = new CustomerAddFragment();
                    Bundle customerBundle = new Bundle();
                    customerBundle.putSerializable(Constant.EXTRA_CUSTOMER, customer);
                    fragment.setArguments(customerBundle);
                    mFragmentLoader.loadFragment(fragment, true, Constant.CUSTOMER_ADD_FRAGMENT_TAG);
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
