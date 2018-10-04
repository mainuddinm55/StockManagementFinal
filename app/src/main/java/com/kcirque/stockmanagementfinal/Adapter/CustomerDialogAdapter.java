package com.kcirque.stockmanagementfinal.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Database.Model.Customer;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

public class CustomerDialogAdapter extends RecyclerView.Adapter<CustomerDialogAdapter.CustomerHolder> {

    private static final String TAG = "Customer List";
    private Context mContext;
    private List<Customer> mCustomerList = new ArrayList<>();
    private DatabaseReference mSalesRef;

    private RecyclerItemClickListener itemClickListener;

    public void setItemClickListener(RecyclerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CustomerDialogAdapter(Context context, List<Customer> customerList) {
        this.mContext = context;
        this.mCustomerList = customerList;
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        mSalesRef = rootRef.child(Constant.SALES_REF);
    }

    @NonNull
    @Override
    public CustomerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.spinner_drop_down_item, viewGroup, false);
        return new CustomerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerHolder customerHolder, final int i) {
        customerHolder.nameTextView.setText(mCustomerList.get(i).getCustomerName());
        customerHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(v, i, mCustomerList.get(i));
            }

        });
    }

    @Override
    public int getItemCount() {
        return mCustomerList.size();
    }

    public class CustomerHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;

        public CustomerHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_view_list_item);
        }
    }
}
