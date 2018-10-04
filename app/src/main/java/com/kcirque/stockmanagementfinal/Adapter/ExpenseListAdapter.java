package com.kcirque.stockmanagementfinal.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.AdapterListUpdateCallback;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kcirque.stockmanagementfinal.Common.DateConverter;
import com.kcirque.stockmanagementfinal.Database.Model.Expense;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

public class ExpenseListAdapter extends RecyclerView.Adapter<ExpenseListAdapter.ExpenseHolder> {

    private Context mContext;
    private List<Expense> mExpenseList = new ArrayList<>();

    public ExpenseListAdapter(Context context, List<Expense> expenseList) {
        this.mContext = context;
        this.mExpenseList = expenseList;
    }

    @NonNull
    @Override
    public ExpenseHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.expense_row_item,viewGroup,false);
        return new ExpenseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseHolder expenseHolder, int i) {
        DateConverter converter = new DateConverter();
        expenseHolder.expenseNameTextView.setText(mExpenseList.get(i).getExpenseName());
        expenseHolder.ExpenseAmountTextView.setText(mExpenseList.get(i).getExpenseAmount()+" BDT");
        expenseHolder.expenseDateTextView.setText(converter.getDateInString(mExpenseList.get(i).getDate()));
    }

    @Override
    public int getItemCount() {
        return mExpenseList.size();
    }

    public class ExpenseHolder extends RecyclerView.ViewHolder{

        public TextView expenseNameTextView, expenseDateTextView, ExpenseAmountTextView;
        public ExpenseHolder(@NonNull View itemView) {
            super(itemView);
            expenseNameTextView = itemView.findViewById(R.id.expenseName);
            ExpenseAmountTextView = itemView.findViewById(R.id.amount);
            expenseDateTextView = itemView.findViewById(R.id.date);
        }
    }
}
