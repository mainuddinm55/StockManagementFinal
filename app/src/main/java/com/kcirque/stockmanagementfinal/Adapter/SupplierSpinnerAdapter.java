package com.kcirque.stockmanagementfinal.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kcirque.stockmanagementfinal.Database.Model.Supplier;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class SupplierSpinnerAdapter extends BaseAdapter {
    private Context mContext;
    private List<Supplier> mSupplierList = new ArrayList<>();


    public SupplierSpinnerAdapter(Context context, List<Supplier> suppliers) {
        this.mContext = context;
        this.mSupplierList = suppliers;
    }

    @Override
    public int getCount() {
        return mSupplierList.size();
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

        Log.e(TAG, "getView: " + mSupplierList.size());
        TextView textView = convertView.findViewById(R.id.text_view_list_item);
        textView.setText(mSupplierList.get(position).getName());
        return convertView;
    }
}
