package com.kcirque.stockmanagementfinal.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.Database.Model.Product;
import com.kcirque.stockmanagementfinal.Database.Model.StockHand;

import java.util.ArrayList;
import java.util.List;

public class StockHandAdapter extends RecyclerView.Adapter<StockHandAdapter.StockHolder> {

    private Context mContext;
    private List<StockHand> mStockList = new ArrayList<>();
    private List<Product> mProductList = new ArrayList<>();


    public StockHandAdapter(Context context, List<StockHand> stockHands, List<Product> products) {
        this.mContext = context;
        this.mStockList = stockHands;
        this.mProductList = products;
    }

    @NonNull
    @Override
    public StockHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.stock_hand_row_item, viewGroup, false);
        return new StockHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final StockHolder stockHolder, int i) {
        StockHand stockHand = mStockList.get(i);
        stockHolder.idTextView.setText(String.valueOf(stockHand.getProductId()));
        stockHolder.nameTextView.setText(mProductList.get(i).getProductName());
        stockHolder.totalPurchaseTextView.setText(String.valueOf(stockHand.getPurchaseQuantity()));
        stockHolder.totalSaleTextView.setText(String.valueOf(stockHand.getSellQuantity()));
        stockHolder.stockHandTextView.setText(String.valueOf(stockHand.getPurchaseQuantity() - stockHand.getSellQuantity()));


    }

    @Override
    public int getItemCount() {
        return mStockList.size();
    }

    public class StockHolder extends RecyclerView.ViewHolder {

        public TextView idTextView;
        public TextView nameTextView;
        public TextView totalPurchaseTextView;
        public TextView totalSaleTextView;
        public TextView stockHandTextView;

        public StockHolder(@NonNull View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.product_id_textview);
            nameTextView = itemView.findViewById(R.id.product_name_textview);
            totalPurchaseTextView = itemView.findViewById(R.id.purchase_qty_text_view);
            totalSaleTextView = itemView.findViewById(R.id.sell_quantity_text_view);
            stockHandTextView = itemView.findViewById(R.id.stock_hand_text_view);
        }
    }
}
