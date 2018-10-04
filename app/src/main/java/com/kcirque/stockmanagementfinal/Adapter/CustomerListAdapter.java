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
    private boolean mIsDialog;
    private DatabaseReference mSalesRef;

    private RecyclerItemClickListener itemClickListener;

    private double mDue;

    public void setItemClickListener(RecyclerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CustomerListAdapter(Context context, List<Customer> customerList) {
        this.mContext = context;
        this.mCustomerList = customerList;
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        mSalesRef = rootRef.child(Constant.SALES_REF);
    }


    @NonNull
    @Override
    public CustomerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.customer_row_item, viewGroup, false);
        return new CustomerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomerHolder customerHolder, final int i) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mSalesRef.orderByChild("customerId").equalTo(mCustomerList.get(i).getCustomerId())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                List<Sales> salesList = new ArrayList<>();
                                salesList.clear();
                                for (DataSnapshot postData : dataSnapshot.getChildren()) {
                                    Sales sales = postData.getValue(Sales.class);
                                    salesList.add(sales);
                                    Log.e(TAG, "Customer Sales List " + sales.getCustomerName());
                                }
                                if (salesList.size() > 0) {
                                    for (Sales sales : salesList) {
                                        mDue = mDue + sales.getDue();
                                    }
                                    if (mDue > mCustomerList.get(i).getDeposit()) {
                                        customerHolder.debitTextTextView.setText(mContext.getResources().getString(R.string.due_text));
                                        customerHolder.debitAmountTextView.setText(String.valueOf((mDue - mCustomerList.get(i).getDeposit())) + "BDT");
                                    } else {
                                        customerHolder.debitTextTextView.setText(mContext.getResources().getString(R.string.deposit_text));
                                        customerHolder.debitAmountTextView.setText(String.valueOf(mCustomerList.get(i).getDeposit()) + " BDT");
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                customerHolder.debitTextTextView.setText(R.string.deposit_text);
                customerHolder.emailTextView.setText(mCustomerList.get(i).getEmail());
                customerHolder.nameTextView.setText(mCustomerList.get(i).getCustomerName());
                customerHolder.mobileTextView.setText(mCustomerList.get(i).getMobile());
            }
        }).start();
        customerHolder.imageView.setImageResource(R.drawable.ic_user);
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
