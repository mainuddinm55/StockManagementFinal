package com.kcirque.stockmanagementfinal.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kcirque.stockmanagementfinal.Database.Model.Purchase;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

public class PurchaseAdapter extends RecyclerView.Adapter<DailyExpenseAdapter.ExpenseHolder> {

    private Context mContext;
    private List<Purchase> mPurchaseList = new ArrayList<>();
    private RecyclerItemClickListener itemClickListener;

    public PurchaseAdapter(Context context, List<Purchase> purchaseList) {
        this.mContext = context;
        this.mPurchaseList = purchaseList;
    }

    @NonNull
    @Override
    public DailyExpenseAdapter.ExpenseHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.daily_cost_row_item, viewGroup, false);
        return new DailyExpenseAdapter.ExpenseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyExpenseAdapter.ExpenseHolder expenseHolder, final int i) {
        final Purchase purchase = mPurchaseList.get(i);
        expenseHolder.slNoTextView.setText(String.valueOf(i + 1));
        expenseHolder.expenseNameTextView.setText(purchase.getProductName());
        expenseHolder.amountTextView.setText(String.valueOf(purchase.getTotalPrice()));
        expenseHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onClick(v, i, purchase);
                }
            }
        });

    }

    public void setItemClickListener(RecyclerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return mPurchaseList.size();
    }
}
