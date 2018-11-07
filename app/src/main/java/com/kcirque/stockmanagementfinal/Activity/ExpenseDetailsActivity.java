package com.kcirque.stockmanagementfinal.Activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.DateConverter;
import com.kcirque.stockmanagementfinal.Database.Model.Expense;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.ActivityExpenseDetailsBinding;

public class ExpenseDetailsActivity extends AppCompatActivity {
    private ActivityExpenseDetailsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_expense_details);
        setSupportActionBar(mBinding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Expense Details");
        }
        Intent intent = getIntent();
        if (intent != null) {
            Expense expense = (Expense) intent.getSerializableExtra(Constant.EXTRA_EXPENSE);

            DateConverter dateConverter = new DateConverter();

            mBinding.expenseNameTextTextView.setText(getResources().getString(R.string.expense_name_text));
            mBinding.expenseNameTextView.setText(expense.getExpenseName());
            mBinding.dateTextTextView.setText(getResources().getString(R.string.date_text));
            mBinding.dateTextView.setText(dateConverter.getDateInString(expense.getDate()));
            mBinding.amountTextTextView.setText(getResources().getString(R.string.amount_text));
            mBinding.amountTextView.setText(String.valueOf(expense.getExpenseAmount()));
            mBinding.commentTextTextView.setText(getResources().getString(R.string.comment_text));
            if (!expense.getComment().isEmpty()) {
                mBinding.commentTextView.setText(expense.getComment());
            } else {
                mBinding.commentTextView.setText("N/A");
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
