package com.kcirque.stockmanagementfinal.Fragment;


import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.DateConverter;
import com.kcirque.stockmanagementfinal.Database.Model.Salary;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentSalaryDetailsBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalaryDetailsFragment extends Fragment {
    private FragmentSalaryDetailsBinding mBinding;

    public SalaryDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_salary_details, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DateConverter dateConverter = new DateConverter();
        Bundle bundle = getArguments();
        if (bundle != null) {
            Salary salary = (Salary) bundle.getSerializable(Constant.EXTRA_SALARY);
            mBinding.nameTextView.setText(salary.getEmpName());
            mBinding.dateTextView.setText(dateConverter.getDateInString(salary.getDate()));
            mBinding.monthTextView.setText(salary.getMonth());
            mBinding.amountTextView.setText(String.valueOf(salary.getAmount()));
        }
    }
}
