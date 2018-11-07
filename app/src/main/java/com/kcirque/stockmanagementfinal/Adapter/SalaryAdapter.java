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

import com.kcirque.stockmanagementfinal.Database.Model.Salary;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

public class SalaryAdapter extends RecyclerView.Adapter<SalaryAdapter.SalaryHolder> implements Filterable {

    private Context mContext;
    private List<Salary> mSalaryList = new ArrayList<>();
    private List<Salary> mFilteredList = new ArrayList<>();

    private RecyclerItemClickListener itemClickListener;

    public SalaryAdapter(Context context, List<Salary> salaries) {
        this.mContext = context;
        this.mSalaryList = salaries;
        this.mFilteredList = salaries;
    }

    @NonNull
    @Override
    public SalaryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.salary_row_item, viewGroup, false);
        return new SalaryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalaryHolder salaryHolder, final int i) {

        final Salary salary = mFilteredList.get(i);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onClick(v, i, salary);
                }
            }
        };
        salaryHolder.slNoTextView.setText(String.valueOf(i + 1));
        salaryHolder.sellerNameTextView.setText(salary.getEmpName());
        salaryHolder.monthTextView.setText(salary.getMonth());
        salaryHolder.amountTextView.setText(String.valueOf(salary.getAmount()));
        if (i == (mFilteredList.size() - 1)) {
            double totalSalary = 0;
            for (Salary salary1 : mFilteredList) {
                totalSalary = totalSalary + salary1.getAmount();
            }
            salaryHolder.linearLayout.setVisibility(View.VISIBLE);
            salaryHolder.totalTextTextView.setText("Total Salary");
            salaryHolder.totalSalaryTextView.setText(String.valueOf(totalSalary));

            salaryHolder.slNoTextView.setOnClickListener(clickListener);
            salaryHolder.sellerNameTextView.setOnClickListener(clickListener);
            salaryHolder.monthTextView.setOnClickListener(clickListener);
            salaryHolder.amountTextView.setOnClickListener(clickListener);

        }
    }

    public void setItemClickListener(RecyclerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString();
                List<Salary> tempList = new ArrayList<>();
                if (query.isEmpty() || query.toLowerCase().equals("month")) {
                    mFilteredList = mSalaryList;
                } else {
                    for (Salary salary : mSalaryList) {
                        if (salary.getMonth().toLowerCase().equals(query.toLowerCase())) {
                            tempList.add(salary);
                        }
                    }
                    mFilteredList = tempList;
                }

                FilterResults results = new FilterResults();
                results.values = mFilteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredList = (List<Salary>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class SalaryHolder extends RecyclerView.ViewHolder {
        TextView totalTextTextView, totalSalaryTextView;
        LinearLayout linearLayout;
        TextView slNoTextView, sellerNameTextView, monthTextView, amountTextView;

        public SalaryHolder(@NonNull View itemView) {
            super(itemView);
            slNoTextView = itemView.findViewById(R.id.sl_no_text_view);
            sellerNameTextView = itemView.findViewById(R.id.seller_name_text_view);
            monthTextView = itemView.findViewById(R.id.month_text_view);
            amountTextView = itemView.findViewById(R.id.salary_text_view);
            totalTextTextView = itemView.findViewById(R.id.total_text_text_view);
            totalSalaryTextView = itemView.findViewById(R.id.total_amount_text_view);
            linearLayout = itemView.findViewById(R.id.total_linear_layout);
        }
    }
}
