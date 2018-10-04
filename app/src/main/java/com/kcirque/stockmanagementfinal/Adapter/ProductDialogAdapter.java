package com.kcirque.stockmanagementfinal.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Adapter.ProductListAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Database.Model.Product;
import com.kcirque.stockmanagementfinal.Database.Model.Purchase;
import com.kcirque.stockmanagementfinal.Database.Model.StockHand;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class ProductDialogAdapter extends RecyclerView.Adapter<ProductDialogAdapter.ProductHolder> {
    private Context mContext;
    private List<Product> mProductList = new ArrayList<>();
    private DatabaseReference mPurchaseRef;
    private DatabaseReference mStockRef;
    private Purchase mPurchase = null;

    private RecyclerItemClickListener recyclerItemClickListener;
    private Purchase purchaseForClick;
    private int mQuantity;

    public void setRecyclerItemClickListener(RecyclerItemClickListener recyclerItemClickListener) {
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    public ProductDialogAdapter(Context context, List<Product> products) {
        mContext = context;
        mProductList = products;
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.spinner_drop_down_item, viewGroup, false);
        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductHolder productHolder, final int i) {

        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        mStockRef = mRootRef.child(Constant.STOCK_HAND_REF);
        mStockRef.keepSynced(true);
        final Product product = mProductList.get(i);
        Log.e(TAG, "run: " + mProductList.size());

        productHolder.nameTextView.setText(product.getProductName());
        mStockRef.child(String.valueOf(product.getProductId())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StockHand stockHand = dataSnapshot.getValue(StockHand.class);
                if (stockHand != null) {
                    mQuantity = stockHand.getPurchaseQuantity() - stockHand.getSellQuantity();
                    productHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            recyclerItemClickListener.onClick(v, i, mQuantity);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    public class ProductHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;

        public ProductHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_view_list_item);
        }
    }
}
