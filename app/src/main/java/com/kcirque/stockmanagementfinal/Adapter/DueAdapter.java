package com.kcirque.stockmanagementfinal.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kcirque.stockmanagementfinal.Database.Model.Customer;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

public class DueAdapter extends RecyclerView.Adapter<DueAdapter.DueHolder> {

    private Context mContext;
    private List<Customer> mCustomerList = new ArrayList<>();

    public DueAdapter(Context context, List<Customer> customers) {
        this.mContext = context;
        this.mCustomerList = customers;
    }

    @NonNull
    @Override
    public DueHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.due_row_item, viewGroup, false);
        return new DueHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DueHolder dueHolder, int i) {
        dueHolder.customerNameTextView.setText(mCustomerList.get(i).getCustomerName());
        dueHolder.dueAmountTextView.setText(String.valueOf(mCustomerList.get(i).getDue()));
    }

    @Override
    public int getItemCount() {
        return mCustomerList.size();
    }

    public class DueHolder extends RecyclerView.ViewHolder {

        TextView customerNameTextView, dueAmountTextView;

        private DueHolder(@NonNull View itemView) {
            super(itemView);
            customerNameTextView = itemView.findViewById(R.id.customer_name_text_view);
            dueAmountTextView = itemView.findViewById(R.id.due_amount_text_view);
        }
    }
}
