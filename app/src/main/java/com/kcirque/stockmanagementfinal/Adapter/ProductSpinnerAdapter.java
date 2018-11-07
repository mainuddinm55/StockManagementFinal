package com.kcirque.stockmanagementfinal.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kcirque.stockmanagementfinal.Database.Model.Product;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class ProductSpinnerAdapter extends BaseAdapter {

    private Context mContext;
    private List<Product> mProductList = new ArrayList<>();


    public ProductSpinnerAdapter(Context context, List<Product> productList) {
        this.mContext = context;
        this.mProductList = productList;
    }

    @Override
    public int getCount() {
        return mProductList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.spinner_drop_down_item, null, false);
        }

        Log.e(TAG, "getView: " + mProductList.size());
        TextView textView = convertView.findViewById(R.id.text_view_list_item);
        textView.setText(mProductList.get(position).getProductName());
        return convertView;
    }
}
