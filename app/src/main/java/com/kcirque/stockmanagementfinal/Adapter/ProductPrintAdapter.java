package com.kcirque.stockmanagementfinal.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kcirque.stockmanagementfinal.Database.Model.ProductSell;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

public class ProductPrintAdapter extends RecyclerView.Adapter<ProductPrintAdapter.ProductHolder> {
    List<ProductSell> mProductSellList = new ArrayList<>();

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext().getApplicationContext())
                .inflate(R.layout.print_product_layout, viewGroup, false);
        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder productHolder, int i) {
        ProductSell productSell = mProductSellList.get(i);
        productHolder.itemTextView.setText(productSell.getQuantity() + " : " + productSell.getProductName());
        productHolder.amountTextView.setText(String.valueOf(productSell.getPrice() * productSell.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return mProductSellList.size();
    }

    public void setProductSellList(List<ProductSell> productSellList) {
        this.mProductSellList = productSellList;
        notifyDataSetChanged();
    }

    class ProductHolder extends RecyclerView.ViewHolder {
        public TextView itemTextView, amountTextView;

        public ProductHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.item_text_view);
            amountTextView = itemView.findViewById(R.id.amount_text_view);
        }
    }
}
