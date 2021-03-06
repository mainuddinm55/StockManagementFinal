package com.kcirque.stockmanagementfinal.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kcirque.stockmanagementfinal.Database.Model.Category;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class CategorySpinnerAdapter extends BaseAdapter {

    private Context mContext;
    private List<Category> mCategoryList = new ArrayList<>();


    public CategorySpinnerAdapter(Context context, List<Category> categoryList) {
        this.mContext = context;
        this.mCategoryList = categoryList;
    }

    @Override
    public int getCount() {
        return mCategoryList.size();
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

        Log.e(TAG, "getView: " + mCategoryList.size());
        TextView textView = convertView.findViewById(R.id.text_view_list_item);
        textView.setText(mCategoryList.get(position).getCategoryName());
        return convertView;
    }
}
