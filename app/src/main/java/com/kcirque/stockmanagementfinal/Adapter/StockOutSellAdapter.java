package com.kcirque.stockmanagementfinal.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Database.Model.ProductSell;
import com.kcirque.stockmanagementfinal.Database.Model.Sales;
import com.kcirque.stockmanagementfinal.Fragment.SalesDetailsFragment;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.MainActivity;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

public class StockOutSellAdapter extends RecyclerView.Adapter<StockOutSellAdapter.StockOutSellHolder> {
    private List<Sales> salesList = new ArrayList<>();
    private Context context;

    private RecyclerItemClickListener itemClickListener;

    public StockOutSellAdapter(Context context, List<Sales> salesList) {
        this.context = context;
        this.salesList = salesList;
    }

    @NonNull
    @Override
    public StockOutSellHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext().getApplicationContext())
                .inflate(R.layout.sales_report_layout_row, viewGroup, false);
        return new StockOutSellHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockOutSellHolder stockOutSellHolder, final int i) {
        List<ProductSell> productSells = salesList.get(i).getSelectedProduct();
        stockOutSellHolder.recyclerView.setHasFixedSize(true);
        stockOutSellHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        StockOutAdapter adapter = new StockOutAdapter(context, productSells);
        stockOutSellHolder.recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(new RecyclerItemClickListener() {
            @Override
            public void onClick(View view, int position, Object object) {
                if (itemClickListener != null) {
                    itemClickListener.onClick(view, i, salesList.get(i));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return salesList.size();
    }

    public void setItemClickListener(RecyclerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class StockOutSellHolder extends RecyclerView.ViewHolder {
        private RecyclerView recyclerView;

        public StockOutSellHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.sales_report_layout);
        }
    }
}
