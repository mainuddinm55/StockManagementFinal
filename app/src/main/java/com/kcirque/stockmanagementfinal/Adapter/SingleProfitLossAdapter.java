package com.kcirque.stockmanagementfinal.Adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kcirque.stockmanagementfinal.R;


public class SingleProfitLossAdapter extends RecyclerView.Adapter<SingleProfitLossAdapter.SingleRowHolder> {
    private Context mContext;
    private String[] month = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    double[] data;
    private int index;

    public SingleProfitLossAdapter(Context context, double[] singleProfitLosses, int index) {
        this.mContext = context;
        this.data = singleProfitLosses;
        this.index = index;
    }

    @NonNull
    @Override
    public SingleRowHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.over_all_profit_loss_row_item, viewGroup, false);
        return new SingleRowHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(@NonNull SingleRowHolder singleRowHolder, int i) {
        if (i == 0) {
            singleRowHolder.titleTextView.setVisibility(View.VISIBLE);
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        switch (index) {
            case 0:
                singleRowHolder.nameTextView.setText(String.valueOf(Math.round(data[i])));
                singleRowHolder.titleTextView.setText("SL No");
                layoutParams = new LinearLayout.LayoutParams(180, LinearLayout.LayoutParams.WRAP_CONTENT);
                break;
            case 1:
                singleRowHolder.titleTextView.setText("Month");
                singleRowHolder.nameTextView.setText(month[i]);
                singleRowHolder.nameTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                layoutParams = new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT);
                break;
            case 2:
                singleRowHolder.titleTextView.setText("Total Purchase");
                singleRowHolder.nameTextView.setText(String.valueOf(data[i]));
                singleRowHolder.nameTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                layoutParams = new LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.WRAP_CONTENT);
                break;
            case 3:
                singleRowHolder.titleTextView.setText("Stock Hand");
                singleRowHolder.nameTextView.setText(String.valueOf(data[i]));
                singleRowHolder.nameTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                layoutParams = new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT);
                break;
            case 4:
                singleRowHolder.titleTextView.setText("Total Sales");
                singleRowHolder.nameTextView.setText(String.valueOf(data[i]));
                singleRowHolder.nameTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                layoutParams = new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT);
                break;
            case 5:
                singleRowHolder.titleTextView.setText("Total Expense");
                singleRowHolder.nameTextView.setText(String.valueOf(data[i]));
                singleRowHolder.nameTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                layoutParams = new LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.WRAP_CONTENT);
                break;
            case 6:
                singleRowHolder.titleTextView.setText("Emp Salary");
                singleRowHolder.nameTextView.setText(String.valueOf(data[i]));
                singleRowHolder.nameTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                layoutParams = new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT);
                break;
            case 7:
                singleRowHolder.titleTextView.setText("Profit/Loss");
                singleRowHolder.nameTextView.setText(String.valueOf(data[i]));
                singleRowHolder.nameTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                layoutParams = new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT);
                break;
        }
        singleRowHolder.linearLayout.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class SingleRowHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, titleTextView;
        LinearLayout linearLayout;

        public SingleRowHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            linearLayout = itemView.findViewById(R.id.linear_layout);
            titleTextView = itemView.findViewById(R.id.title_text_view);
        }
    }
}
