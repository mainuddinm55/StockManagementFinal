package com.kcirque.stockmanagementfinal.Fragment;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Database.Model.Expense;
import com.kcirque.stockmanagementfinal.Database.Model.Purchase;
import com.kcirque.stockmanagementfinal.Database.Model.Sales;
import com.kcirque.stockmanagementfinal.Database.Model.StockHand;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentProfitLossBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfitLossFragment extends Fragment {

    private static ProfitLossFragment INSTANCE;

    public static final int DAY_7_TYPE = 1;
    public static final int WEEK_TYPE = 2;
    public static final int DAY_30_TYPE = 3;
    public static final int MONTH_TYPE = 4;
    public static final int YEAR_TYPE = 5;

    private int mType;

    private Context mContext;
    private FragmentLoader mFragmentLoader;

    private static final String TAG = "Profit Loss Fragment";
    private FragmentProfitLossBinding mBinding;

    public static synchronized ProfitLossFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProfitLossFragment();
        }

        return INSTANCE;
    }

    public ProfitLossFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profit_loss, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String[] profit_loss_type = getResources().getStringArray(R.array.profit_loss_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,R.layout.stock_type_list_item,profit_loss_type);
        mBinding.profitLossTypeListView.setAdapter(adapter);

        mBinding.profitLossTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        mType = DAY_7_TYPE;
                        break;
                    case 1:
                        mType = WEEK_TYPE;
                        break;
                    case 2:
                        mType = DAY_30_TYPE;
                        break;
                    case 3:
                        mType = MONTH_TYPE;
                        break;
                    case 4:
                        mType = YEAR_TYPE;
                        break;
                }

                Bundle bundle = new Bundle();
                bundle.putInt(Constant.EXTRA_PROFIT_LOSS_TYPE,mType);
                ProfitLossReportFragment fragment = ProfitLossReportFragment.getInstance();
                fragment.setArguments(bundle);
                mFragmentLoader.loadFragment(fragment,true);

            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentLoader = (FragmentLoader)context;
    }
}
