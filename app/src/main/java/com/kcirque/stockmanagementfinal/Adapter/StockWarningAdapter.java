package com.kcirque.stockmanagementfinal.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kcirque.stockmanagementfinal.Database.Model.Product;
import com.kcirque.stockmanagementfinal.Database.Model.StockHand;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

public class StockWarningAdapter extends RecyclerView.Adapter<StockWarningAdapter.StockWarningHolder> {

    private Context mContext;
    private List<StockHand> mStockHandList = new ArrayList<>();
    private List<Product> mProductList = new ArrayList<>();

    private RecyclerItemClickListener recyclerItemClickListener;

    public void setRecyclerItemClickListener(RecyclerItemClickListener recyclerItemClickListener) {
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    public StockWarningAdapter(Context context, List<StockHand> stockHands, List<Product> products) {
        this.mContext = context;
        this.mStockHandList = stockHands;
        this.mProductList = products;
    }

    @NonNull
    @Override
    public StockWarningHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.stock_out_warning_row_item, viewGroup, false);
        return new StockWarningHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final StockWarningHolder stockWarningHolder, final int i) {
        stockWarningHolder.idTextView.setText(String.valueOf(mStockHandList.get(i).getProductId()));
        stockWarningHolder.quantityTextView.setText(String.valueOf((mStockHandList.get(i).getPurchaseQuantity()) - mStockHandList.get(i).getSellQuantity()));
        if (mProductList.get(i).getProductId() == mStockHandList.get(i).getProductId()) {
            stockWarningHolder.nameTextView.setText(mProductList.get(i).getProductName());
        }

        stockWarningHolder.addPurchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerItemClickListener != null) {
                    recyclerItemClickListener.onClick(v, i, mProductList.get(i));
                } else {
                    Toast.makeText(mContext, "Seller can't Purchase!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mStockHandList.size();
    }

    public class StockWarningHolder extends RecyclerView.ViewHolder {

        public TextView idTextView, nameTextView, quantityTextView;
        public Button addPurchaseButton;

        public StockWarningHolder(@NonNull View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.product_id_textview);
            nameTextView = itemView.findViewById(R.id.product_name_textview);
            quantityTextView = itemView.findViewById(R.id.stock_hand_text_view);
            addPurchaseButton = itemView.findViewById(R.id.add_purchase_btn);
        }
    }
}
