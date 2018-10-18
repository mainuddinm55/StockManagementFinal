package com.kcirque.stockmanagementfinal.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kcirque.stockmanagementfinal.Common.DateConverter;
import com.kcirque.stockmanagementfinal.Database.Model.Customer;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

public class DueAdapter extends RecyclerView.Adapter<DueAdapter.DueHolder> implements Filterable {

    private Context mContext;
    private List<Customer> mCustomerList = new ArrayList<>();
    private List<Customer> mFilteredList = new ArrayList<>();
    private DateConverter mDateConverter;
    private double mTotalDue = 0;
    private RecyclerItemClickListener mRecyclerItemClickListener;


    public DueAdapter(Context context, List<Customer> customers) {
        this.mContext = context;
        this.mCustomerList = customers;
        this.mFilteredList = customers;
    }

    public void setRecyclerItemClickListener(RecyclerItemClickListener recyclerItemClickListener) {
        this.mRecyclerItemClickListener = recyclerItemClickListener;
    }

    @NonNull
    @Override
    public DueHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.due_row_item, viewGroup, false);
        return new DueHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DueHolder dueHolder, final int i) {

        mDateConverter = new DateConverter();
        dueHolder.customerNameTextView.setText(mFilteredList.get(i).getCustomerName());
        dueHolder.dueAmountTextView.setText(String.valueOf(mFilteredList.get(i).getDue()));
        dueHolder.dueDateTextView.setText(mDateConverter.getDateInString(mFilteredList.get(i).getDueDate()));
        if (i == (mFilteredList.size() - 1)) {
            mTotalDue = 0;
            for (Customer customer : mFilteredList) {
                mTotalDue = mTotalDue + customer.getDue();
            }
            dueHolder.totalTextTextView.setText(mContext.getResources().getString(R.string.total_text));
            dueHolder.totalAmountTextView.setText(String.valueOf(mTotalDue));
            dueHolder.linearLayout.setVisibility(View.VISIBLE);
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerItemClickListener.onClick(v, i, mCustomerList.get(i));
            }
        };

        dueHolder.customerNameTextView.setOnClickListener(onClickListener);
        dueHolder.dueDateTextView.setOnClickListener(onClickListener);
        dueHolder.dueAmountTextView.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                mDateConverter = new DateConverter();
                String query = charSequence.toString();
                List<Customer> tempList = new ArrayList<>();
                if (query.isEmpty()) {
                    mFilteredList = mCustomerList;

                } else {
                    for (Customer p : mCustomerList) {
                        if (p.getCustomerName().toLowerCase().contains(query.toLowerCase()) ||
                                p.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                                mDateConverter.getDateInString(p.getDueDate()).contains(query)) {
                            tempList.add(p);
                        }
                    }
                    mFilteredList = tempList;
                }
                FilterResults results = new FilterResults();
                results.values = mFilteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (List<Customer>) filterResults.values;
                notifyDataSetChanged();//refresh recyclerview items
            }
        };
    }

    public class DueHolder extends RecyclerView.ViewHolder {

        TextView customerNameTextView, dueAmountTextView, dueDateTextView;
        TextView totalTextTextView, totalAmountTextView;
        LinearLayout linearLayout;

        private DueHolder(@NonNull View itemView) {
            super(itemView);
            customerNameTextView = itemView.findViewById(R.id.customer_name_text_view);
            dueAmountTextView = itemView.findViewById(R.id.due_amount_text_view);
            dueDateTextView = itemView.findViewById(R.id.due_date_text_view);
            totalTextTextView = itemView.findViewById(R.id.total_due_text_text_view);
            totalAmountTextView = itemView.findViewById(R.id.total_due_amount_text_view);
            linearLayout = itemView.findViewById(R.id.due_layout);
        }
    }


}
