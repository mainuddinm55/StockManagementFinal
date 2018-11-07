package com.kcirque.stockmanagementfinal.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kcirque.stockmanagementfinal.Database.Model.Supplier;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.SupplierHolder> {
    private List<Supplier> supplierList = new ArrayList<>();
    private RecyclerItemClickListener recyclerItemClickListener;

    @NonNull
    @Override
    public SupplierHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.supplier_row_item, viewGroup, false);
        return new SupplierHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplierHolder supplierHolder, int i) {
        supplierHolder.nameTextView.setText(supplierList.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return supplierList.size();
    }

    public void setSupplierList(List<Supplier> supplierList) {
        this.supplierList = supplierList;
        notifyDataSetChanged();
    }

    public void setRecyclerItemClickListener(RecyclerItemClickListener recyclerItemClickListener) {
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    class SupplierHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        public SupplierHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerItemClickListener != null && getAdapterPosition() != -1) {
                        recyclerItemClickListener.onClick(v, getAdapterPosition(), supplierList.get(getAdapterPosition()));
                    }
                }
            });
        }
    }
}
