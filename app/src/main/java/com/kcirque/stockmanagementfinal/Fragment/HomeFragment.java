package com.kcirque.stockmanagementfinal.Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kcirque.stockmanagementfinal.Adapter.HomeWidgetAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.DateConverter;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.StockHand;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.Interface.ItemClickListener;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private Context mContext;
    private static final String TAG = "Stock Management";
    private RecyclerView mHomeWidgetRecyclerView;
    FragmentLoader mFragmentLoader;


    private static HomeFragment INSTANCE;

    public static synchronized HomeFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HomeFragment();
        }

        return INSTANCE;
    }

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mHomeWidgetRecyclerView = view.findViewById(R.id.home_recycler_view);
        mHomeWidgetRecyclerView.setHasFixedSize(true);
        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mHomeWidgetRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        }
        else{
            mHomeWidgetRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        }

        HomeWidgetAdapter adapter = new HomeWidgetAdapter(mContext);
        mHomeWidgetRecyclerView.setAdapter(adapter);

        adapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, String title) {
                switch (position) {
                    case 0:
                        mFragmentLoader.loadFragment(ProductListFragment.getInstance(),true,Constant.PRODUCT_LIST_FRAGMENT_TAG);
                        break;
                    case 1:
                        mFragmentLoader.loadFragment(PurchaseFragment.getInstance(),true, Constant.PURCHASE_FRAGMENT_TAG);
                        break;
                    case 2:
                        mFragmentLoader.loadFragment(CustomerListFragment.getInstance(),true,Constant.CUSTOMER_LIST_FRAGMENT_TAG);
                        break;
                    case 3:
                        mFragmentLoader.loadFragment(SalesFragment.getInstance(),true,Constant.SALES_FRAGMENT_TAG);
                        break;
                    case 4:
                        mFragmentLoader.loadFragment(StockHandFragment.getInstance(),true,Constant.STOCK_HAND_FRAGMENT_TAG);
                        break;
                    case 5:
                        mFragmentLoader.loadFragment(ProfitLossFragment.getInstance(),true,Constant.PROFIT_LOSS_FRAGMENT_TAG);
                        break;

                }
            }
        });


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentLoader = (FragmentLoader) mContext;
    }


}
