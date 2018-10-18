package com.kcirque.stockmanagementfinal.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kcirque.stockmanagementfinal.Database.Model.Expense;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

public class DailyExpenseAdapter extends RecyclerView.Adapter<DailyExpenseAdapter.ExpenseHolder> {

    private Context mContext;
    private List<Expense> mExpenseList = new ArrayList<>();

    private RecyclerItemClickListener mRecyclerItemClickListener;

    public void setRecyclerItemClickListener(RecyclerItemClickListener recyclerItemClickListener) {
        this.mRecyclerItemClickListener = recyclerItemClickListener;
    }

    public DailyExpenseAdapter(Context context, List<Expense> expenseList) {
        this.mContext = context;
        this.mExpenseList = expenseList;
    }

    @NonNull
    @Override
    public ExpenseHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.daily_cost_row_item, viewGroup, false);
        return new ExpenseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseHolder expenseHolder, final int i) {
        expenseHolder.slNoTextView.setText(String.valueOf((i + 1)));
        expenseHolder.expenseNameTextView.setText(mExpenseList.get(i).getExpenseName());
        expenseHolder.amountTextView.setText(String.valueOf(mExpenseList.get(i).getExpenseAmount()));
        expenseHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerItemClickListener.onClick(v, i, mExpenseList.get(i));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mExpenseList.size();
    }

    static class ExpenseHolder extends RecyclerView.ViewHolder {

        TextView slNoTextView, expenseNameTextView, amountTextView;


        public ExpenseHolder(@NonNull View itemView) {
            super(itemView);
            slNoTextView = itemView.findViewById(R.id.sl_no_text_view);
            expenseNameTextView = itemView.findViewById(R.id.expense_name_text_view);
            amountTextView = itemView.findViewById(R.id.amount_text_view);

        }
    }
}
