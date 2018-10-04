package com.kcirque.stockmanagementfinal.Adapter;

import android.content.Context;
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

public class ProductSellAdapter extends RecyclerView.Adapter<ProductSellAdapter.ProductSellHolder>{
    private Context mContext;
    private List<ProductSell> mProductList = new ArrayList<>();

    public ProductSellAdapter(Context context, List<ProductSell> productSellList) {
        this.mContext = context;
        this.mProductList = productSellList;
    }

    @NonNull
    @Override
    public ProductSellHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.product_sell_row_item,viewGroup,false);
        return new ProductSellHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductSellHolder productSellHolder, int i) {
        ProductSell productSell = mProductList.get(i);
        productSellHolder.productNameTextView.setText(productSell.getProductName());
        productSellHolder.unitPriceTextView.setText(String.valueOf(productSell.getPrice()));
        productSellHolder.quantityTextView.setText(String.valueOf(productSell.getQuantity()));
        productSellHolder.amountTextView.setText(String.valueOf((productSell.getPrice()*productSell.getQuantity())));
    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    public class ProductSellHolder extends RecyclerView.ViewHolder {
        public TextView productNameTextView;
        public TextView unitPriceTextView;
        public TextView quantityTextView;
        public TextView amountTextView;
        public ProductSellHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.product_name_textview);
            unitPriceTextView = itemView.findViewById(R.id.product_price_textview);
            quantityTextView = itemView.findViewById(R.id.quantity_textview);
            amountTextView = itemView.findViewById(R.id.amount_textviw);
        }
    }
}
