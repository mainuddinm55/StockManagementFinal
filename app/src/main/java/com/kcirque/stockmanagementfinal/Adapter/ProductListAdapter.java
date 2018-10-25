package com.kcirque.stockmanagementfinal.Adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Database.Model.Product;
import com.kcirque.stockmanagementfinal.Database.Model.Purchase;
import com.kcirque.stockmanagementfinal.Database.Model.StockHand;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductListHolder> {

    private Context mContext;
    private List<Product> mProductList = new ArrayList<>();

    private RecyclerItemClickListener recyclerItemClickListener;

    public void setRecyclerItemClickListener(RecyclerItemClickListener recyclerItemClickListener) {
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    public ProductListAdapter(Context context, List<Product> productList) {
        this.mContext = context;
        this.mProductList = productList;
    }

    @NonNull
    @Override
    public ProductListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.product_row_item, viewGroup, false);
        return new ProductListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductListHolder productListHolder, final int i) {
        final Product product = mProductList.get(i);
        Log.e(TAG, "onBindViewHolder: ProductForRoom Id " + product.getProductId());

        Log.e(TAG, "onBindViewHolder: " + mProductList.size());
        if (product.getCompany() != null) {
            productListHolder.productCompanyTextView.setText("Brand : " + mProductList.get(i).getCompany());
        }
        if (product.getSellPrice() > 0) {
            productListHolder.productPriceTextView.setText("Price : " + String.valueOf(mProductList.get(i).getSellPrice()) + " BDT");
        } else {
            productListHolder.productPriceTextView.setText("Not purchase yet");
        }
        Glide.with(mContext).load(product.getProductImageUrl())
                .apply(RequestOptions.placeholderOf(R.drawable.placeholder))
                .into(productListHolder.productImageImageView);
        productListHolder.productNameTextView.setText("ProductForRoom : " + product.getProductName());
        productListHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerItemClickListener.onClick(v, i, product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    public class ProductListHolder extends RecyclerView.ViewHolder {
        public ImageView productImageImageView;
        public TextView productNameTextView;
        public TextView productCompanyTextView;
        public TextView productPriceTextView;

        public ProductListHolder(@NonNull View itemView) {
            super(itemView);
            productImageImageView = itemView.findViewById(R.id.product_imageview);
            productNameTextView = itemView.findViewById(R.id.product_name_textview);
            productCompanyTextView = itemView.findViewById(R.id.company_name_textview);
            productPriceTextView = itemView.findViewById(R.id.product_price_textview);
        }
    }

}
