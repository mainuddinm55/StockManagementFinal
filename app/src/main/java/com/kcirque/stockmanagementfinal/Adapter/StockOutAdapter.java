package com.kcirque.stockmanagementfinal.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kcirque.stockmanagementfinal.Database.Model.ProductSell;
import com.kcirque.stockmanagementfinal.Database.Model.Sales;
import com.kcirque.stockmanagementfinal.Interface.ItemClickListener;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

public class StockOutAdapter extends RecyclerView.Adapter<StockOutAdapter.StockOutHolder> {

    private Context mContext;
    private List<ProductSell> mProductSell = new ArrayList<>();
    private RecyclerItemClickListener itemClickListener;

    public StockOutAdapter(Context context, List<ProductSell> productSells) {
        this.mContext = context;
        this.mProductSell = productSells;
    }

    @NonNull
    @Override
    public StockOutHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.stock_out_row_item, viewGroup, false);
        return new StockOutHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockOutHolder stockOutHolder, final int i) {

        double amount = mProductSell.get(i).getPrice() * mProductSell.get(i).getQuantity();
        stockOutHolder.idTextView.setText(String.valueOf(mProductSell.get(i).getProductId()));
        stockOutHolder.nameTextView.setText(String.valueOf(mProductSell.get(i).getProductName()));
        stockOutHolder.quantityTextView.setText(String.valueOf(mProductSell.get(i).getQuantity()));
        stockOutHolder.amountTextView.setText(String.valueOf(amount + " BDT"));

        stockOutHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null && i != -1) {
                    itemClickListener.onClick(v, i, mProductSell.get(i));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mProductSell.size();
    }

    public void setItemClickListener(RecyclerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    class StockOutHolder extends RecyclerView.ViewHolder {
        TextView idTextView;
        TextView nameTextView;
        TextView quantityTextView;
        TextView amountTextView;

        StockOutHolder(@NonNull View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.product_id_text_view);
            nameTextView = itemView.findViewById(R.id.product_name_text_view);
            quantityTextView = itemView.findViewById(R.id.sell_qty_text_view);
            amountTextView = itemView.findViewById(R.id.total_sell_amount_text_view);
        }
    }
}
