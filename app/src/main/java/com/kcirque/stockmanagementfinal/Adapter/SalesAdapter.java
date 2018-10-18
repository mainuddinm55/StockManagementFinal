package com.kcirque.stockmanagementfinal.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kcirque.stockmanagementfinal.Database.Model.ProductSell;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

public class SalesAdapter extends RecyclerView.Adapter<DailyExpenseAdapter.ExpenseHolder> {
    private Context mContext;
    private List<ProductSell> mProductList = new ArrayList<>();

    public SalesAdapter(Context context, List<ProductSell> salesList) {
        this.mContext = context;
        this.mProductList = salesList;
    }


    @NonNull
    @Override
    public DailyExpenseAdapter.ExpenseHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.daily_cost_row_item, viewGroup, false);
        return new DailyExpenseAdapter.ExpenseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyExpenseAdapter.ExpenseHolder expenseHolder, int i) {
        expenseHolder.slNoTextView.setText(String.valueOf(i + 1));
        expenseHolder.expenseNameTextView.setText(mProductList.get(i).getProductName());
        expenseHolder.amountTextView.setText(String.valueOf(mProductList.get(i).getPrice() * mProductList.get(i).getQuantity()));
    }


    @Override
    public int getItemCount() {
        return mProductList.size();
    }
}
