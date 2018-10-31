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
import android.widget.Toast;

import com.kcirque.stockmanagementfinal.Adapter.ProductSellAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.DateConverter;
import com.kcirque.stockmanagementfinal.Database.Model.Sales;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentSalesDetailsBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalesDetailsFragment extends Fragment {
    private FragmentSalesDetailsBinding mBinding;
    private Context mContext;

    public SalesDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sales_details, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DateConverter dateConverter = new DateConverter();
        Bundle bundle = getArguments();
        if (bundle != null) {
            Sales sales = (Sales) bundle.getSerializable(Constant.EXTRA_SALES);
            if (sales != null) {
                getActivity().setTitle("Sales Details");
                mBinding.salesProductList.setHasFixedSize(true);
                mBinding.salesProductList.setLayoutManager(new LinearLayoutManager(mContext));
                ProductSellAdapter adapter = new ProductSellAdapter(mContext, sales.getSelectedProduct());
                mBinding.salesProductList.setAdapter(adapter);
                mBinding.customerNameTextView.setText(sales.getCustomerName());
                mBinding.totalAmountTextView.setText(String.valueOf(sales.getTotal()));
                mBinding.paidAmountTextView.setText(String.valueOf(sales.getPaid()));
                mBinding.dueAmountTextview.setText(String.valueOf(sales.getDue()));
                mBinding.salesDateTextView.setText(dateConverter.getDateInString(sales.getSalesDate()));
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}

