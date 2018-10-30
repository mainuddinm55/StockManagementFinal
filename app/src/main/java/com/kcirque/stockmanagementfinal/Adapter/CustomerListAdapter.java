package com.kcirque.stockmanagementfinal.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Database.Model.Customer;
import com.kcirque.stockmanagementfinal.Database.Model.Sales;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerListAdapter extends RecyclerView.Adapter<CustomerListAdapter.CustomerHolder> {

    private static final String TAG = "Customer List";
    private Context mContext;
    private List<Customer> mCustomerList = new ArrayList<>();

    private RecyclerItemClickListener itemClickListener;

    public void setItemClickListener(RecyclerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CustomerListAdapter(Context context, List<Customer> customerList) {
        this.mContext = context;
        this.mCustomerList = customerList;
    }


    @NonNull
    @Override
    public CustomerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.customer_row_item, viewGroup, false);
        return new CustomerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomerHolder customerHolder, final int i) {

        customerHolder.debitTextTextView.setText(R.string.deposit_text);
        customerHolder.debitAmountTextView.setText(String.valueOf(mCustomerList.get(i).getDue()));
        customerHolder.emailTextView.setText(mCustomerList.get(i).getEmail());
        customerHolder.nameTextView.setText(mCustomerList.get(i).getCustomerName());
        customerHolder.mobileTextView.setText(mCustomerList.get(i).getMobile());
        customerHolder.imageView.setImageResource(R.drawable.ic_user);

        customerHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null && i != -1) {
                    itemClickListener.onClick(v, i, mCustomerList.get(i));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCustomerList.size();
    }

    public class CustomerHolder extends RecyclerView.ViewHolder {

        public CircleImageView imageView;
        public TextView nameTextView;
        public TextView mobileTextView;
        public TextView emailTextView;
        public TextView debitTextTextView;
        public TextView debitAmountTextView;

        public CustomerHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.customer_image_imageview);
            nameTextView = itemView.findViewById(R.id.customer_name_textview);
            mobileTextView = itemView.findViewById(R.id.customer_mobile_textview);
            emailTextView = itemView.findViewById(R.id.customer_email_textview);
            debitTextTextView = itemView.findViewById(R.id.debit_text_text_view);
            debitAmountTextView = itemView.findViewById(R.id.debit_amount_text_view);
        }
    }


}
