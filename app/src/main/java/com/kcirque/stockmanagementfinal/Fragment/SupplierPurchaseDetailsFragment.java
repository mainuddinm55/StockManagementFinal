package com.kcirque.stockmanagementfinal.Fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.DateConverter;
import com.kcirque.stockmanagementfinal.Database.Model.Purchase;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentSupplierPurchaseDetailsBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class SupplierPurchaseDetailsFragment extends Fragment {
    private FragmentSupplierPurchaseDetailsBinding mBinding;

    public SupplierPurchaseDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_supplier_purchase_details, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DateConverter dateConverter = new DateConverter();
        Bundle bundle = getArguments();
        if (bundle != null) {
            Purchase purchase = (Purchase) bundle.getSerializable(Constant.EXTRA_PURCHASE);
            if (purchase != null) {
                getActivity().setTitle(purchase.getProductName());
                mBinding.productNameTextView.setText(purchase.getProductName());
                mBinding.dateTextView.setText(dateConverter.getDateInString(purchase.getPurchaseDate()));
                mBinding.brandTextView.setText(purchase.getCompanyName());
                mBinding.supplierTextView.setText(purchase.getSupplierName());
                mBinding.qtyTextView.setText(String.valueOf(purchase.getQuantity()));
                mBinding.totalTextView.setText(String.valueOf(purchase.getTotalPrice()));
                mBinding.paidTextView.setText(String.valueOf(purchase.getPaidAmount()));
                mBinding.dueTextView.setText(String.valueOf(purchase.getDueAmount()));
            }
        }
    }
}
