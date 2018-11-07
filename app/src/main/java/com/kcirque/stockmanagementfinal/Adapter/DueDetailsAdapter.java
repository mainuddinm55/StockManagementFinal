package com.kcirque.stockmanagementfinal.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kcirque.stockmanagementfinal.Common.DateConverter;
import com.kcirque.stockmanagementfinal.Database.Model.Sales;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

public class DueDetailsAdapter extends RecyclerView.Adapter<DueDetailsAdapter.DueHolder> {
    private Context mContext;
    private List<Sales> mSalesList = new ArrayList<>();
    private RecyclerItemClickListener itemClickListener;

    public DueDetailsAdapter(Context context, List<Sales> sales) {
        this.mContext = context;
        this.mSalesList = sales;
    }

    @NonNull
    @Override
    public DueHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.due_details_row_item, viewGroup, false);
        return new DueHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DueHolder dueHolder, final int i) {
        DateConverter dateConverter = new DateConverter();
        final Sales sales = mSalesList.get(i);
        dueHolder.dateTextView.setText(dateConverter.getDateInString(sales.getSalesDate()));
        dueHolder.totalTextView.setText(String.valueOf(sales.getTotal()));
        dueHolder.paidTextView.setText(String.valueOf(sales.getPaid()));
        dueHolder.dueTextView.setText(String.valueOf(sales.getDue()));
        dueHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null)
                    itemClickListener.onClick(v, i, sales);
            }
        });
    }

    public void setItemClickListener(RecyclerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return mSalesList.size();
    }

    public static class DueHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, totalTextView, paidTextView, dueTextView;

        public DueHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            totalTextView = itemView.findViewById(R.id.total_text_view);
            paidTextView = itemView.findViewById(R.id.paid_text_view);
            dueTextView = itemView.findViewById(R.id.due_text_view);
        }
    }
}
