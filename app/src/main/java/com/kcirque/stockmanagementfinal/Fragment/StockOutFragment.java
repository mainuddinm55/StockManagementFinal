package com.kcirque.stockmanagementfinal.Fragment;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentStockOutBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class StockOutFragment extends Fragment {
    private FragmentStockOutBinding mBinding;

    public static final int TODAY_TYPE = 1;
    public static final int DAY_7_TYPE = 2;
    public static final int WEEK_TYPE = 3;
    public static final int DAY_30_TYPE = 4;
    public static final int MONTH_TYPE = 5;

    private int mType;

    private static StockOutFragment INSTANCE;
    private Context mContext;
    private FragmentLoader mFragmentLoader;

    public static synchronized StockOutFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StockOutFragment();
        }

        return INSTANCE;
    }

    public StockOutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_stock_out, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Sales Reports");
        String[] stockOutType = getResources().getStringArray(R.array.stock_out_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,R.layout.stock_type_list_item,stockOutType);
        mBinding.stockOutTypeListView.setAdapter(adapter);

        mBinding.stockOutTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        mType = TODAY_TYPE;
                        break;
                    case 1:
                        mType = DAY_7_TYPE;
                        break;
                    case 2:
                        mType = WEEK_TYPE;
                        break;
                    case 3:
                        mType = DAY_30_TYPE;
                        break;
                    case 4:
                        mType = MONTH_TYPE;
                        break;
                }

                Bundle bundle = new Bundle();
                bundle.putInt(Constant.EXTRA_STOCK_OUT_TYPE,mType);
                StockOutReportFragment fragment = StockOutReportFragment.getInstance();
                fragment.setArguments(bundle);
                mFragmentLoader.loadFragment(fragment,true,Constant.STOCK_OUT_REPORT_FRAGMENT_TAG);

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentLoader = (FragmentLoader) context;
    }
}
