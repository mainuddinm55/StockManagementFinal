package com.kcirque.stockmanagementfinal.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kcirque.stockmanagementfinal.Common.DateConverter;
import com.kcirque.stockmanagementfinal.Database.Model.Purchase;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class SupplierPurchaseAdapter extends RecyclerView.Adapter<SupplierPurchaseAdapter.PurchaseHolder> {
    private List<Purchase> purchaseList = new ArrayList<>();
    private RecyclerItemClickListener itemClickListener;

    @NonNull
    @Override
    public PurchaseHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.supplier_purchase_row_item, viewGroup, false);
        return new PurchaseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseHolder purchaseHolder, int i) {
        DateConverter dateConverter = new DateConverter();
        Purchase purchase = purchaseList.get(i);
        purchaseHolder.dateTextView.setText(dateConverter.getDateInString(purchase.getPurchaseDate()));
        purchaseHolder.productNameTextView.setText(purchase.getProductName());
        purchaseHolder.quantityTextView.setText(String.valueOf(purchase.getQuantity()));
        purchaseHolder.amountTextView.setText(String.valueOf(purchase.getTotalPrice()));
    }

    @Override
    public int getItemCount() {
        return purchaseList.size();
    }

    public void setPurchaseList(List<Purchase> purchaseList) {
        this.purchaseList = purchaseList;
        notifyDataSetChanged();
    }

    public Purchase getPurchaseAt(int position) {
        return purchaseList.get(position);
    }

    public void setItemClickListener(RecyclerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    class PurchaseHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, productNameTextView, quantityTextView, amountTextView;

        public PurchaseHolder(@NonNull final View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            productNameTextView = itemView.findViewById(R.id.name_text_view);
            quantityTextView = itemView.findViewById(R.id.quantity_text_view);
            amountTextView = itemView.findViewById(R.id.amount_text_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null && getAdapterPosition() != -1) {
                        itemClickListener.onClick(v, getAdapterPosition(), getPurchaseAt(getAdapterPosition()));
                    }
                }
            });
        }
    }
}
