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

import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.DateConverter;
import com.kcirque.stockmanagementfinal.Database.Model.Expense;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentExpenseDetailsBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExpenseDetailsFragment extends Fragment {
    private static ExpenseDetailsFragment sInstance;
    private FragmentExpenseDetailsBinding mBinding;

    private Context mContext;

    public ExpenseDetailsFragment() {
        // Required empty public constructor
    }

    public static synchronized ExpenseDetailsFragment getInstance() {
        if (sInstance == null)
            sInstance = new ExpenseDetailsFragment();
        return sInstance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_expense_details, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        getActivity().setTitle("ExpenseForRoom");
        if (bundle != null) {
            Expense expense = (Expense) bundle.getSerializable(Constant.EXTRA_EXPENSE);

            DateConverter dateConverter = new DateConverter();

            mBinding.expenseNameTextTextView.setText(mContext.getResources().getString(R.string.expense_name_text));
            mBinding.expenseNameTextView.setText(expense.getExpenseName());
            mBinding.dateTextTextView.setText(mContext.getResources().getString(R.string.date_text));
            mBinding.dateTextView.setText(dateConverter.getDateInString(expense.getDate()));
            mBinding.amountTextTextView.setText(mContext.getResources().getString(R.string.amount_text));
            mBinding.amountTextView.setText(String.valueOf(expense.getExpenseAmount()));
            mBinding.commentTextTextView.setText(mContext.getResources().getString(R.string.comment_text));
            if (!expense.getComment().isEmpty()) {
                mBinding.commentTextView.setText(expense.getComment());
            } else {
                mBinding.commentTextView.setText("N/A");
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
