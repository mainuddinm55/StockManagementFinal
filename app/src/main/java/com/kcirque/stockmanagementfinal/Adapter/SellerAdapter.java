package com.kcirque.stockmanagementfinal.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Interface.ItemClickListener;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerAdapter extends RecyclerView.Adapter<SellerAdapter.SellerHolder> {
    private Context mContext;
    private List<Seller> mSellerList = new ArrayList<>();

    RecyclerItemClickListener itemClickListener;

    public void setItemClickListener(RecyclerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public SellerAdapter(Context context, List<Seller> sellers) {
        mContext = context;
        mSellerList = sellers;
    }

    @NonNull
    @Override
    public SellerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.seller_row_item, viewGroup, false);
        return new SellerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerHolder sellerHolder, final int i) {
        sellerHolder.sellerNameTextView.setText(mSellerList.get(i).getName());
        sellerHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, i, mSellerList.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSellerList.size();
    }

    class SellerHolder extends RecyclerView.ViewHolder {

        public CircleImageView sellerImageView;
        public TextView sellerNameTextView;

        public SellerHolder(@NonNull View itemView) {
            super(itemView);
            sellerImageView = itemView.findViewById(R.id.seller_profile_image_view);
            sellerNameTextView = itemView.findViewById(R.id.seller_name_text_view);
        }
    }

}
