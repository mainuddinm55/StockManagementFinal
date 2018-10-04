package com.kcirque.stockmanagementfinal.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kcirque.stockmanagementfinal.Interface.ItemClickListener;
import com.kcirque.stockmanagementfinal.R;

public class HomeWidgetAdapter extends RecyclerView.Adapter<HomeWidgetAdapter.HomeWidgetHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ItemClickListener mItemClickListener;

    private String[] titles = {"Products", "Purchase\nEntry","Customers","Sales","Stock Hand","Profit Loss"};
    private int[] icons = {R.drawable.ic_product,R.drawable.ic_purchase,R.drawable.ic_customers,R.drawable.ic_sales,R.drawable.ic_stock,R.drawable.ic_reports};

    public HomeWidgetAdapter(Context context) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public HomeWidgetHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mLayoutInflater.inflate(R.layout.home_rv_item,viewGroup,false);
        return new HomeWidgetHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeWidgetHolder homeWidgetHolder, final int position) {
        homeWidgetHolder.homeWidgetImageView.setImageResource(icons[position]);
        homeWidgetHolder.homeWidgetTextView.setText(titles[position]);
        homeWidgetHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onClick(v,position,titles[position]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    public class HomeWidgetHolder extends RecyclerView.ViewHolder{

        public ImageView homeWidgetImageView;
        private TextView homeWidgetTextView;

        public HomeWidgetHolder(@NonNull View itemView) {
            super(itemView);
            homeWidgetImageView = itemView.findViewById(R.id.home_widget_imageview);
            homeWidgetTextView = itemView.findViewById(R.id.home_widget_title_textview);
        }
    }
}
