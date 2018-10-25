package com.kcirque.stockmanagementfinal.Fragment;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Adapter.OverAllProfitLossAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.DateConverter;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.DateAmountCost;
import com.kcirque.stockmanagementfinal.Database.Model.DateAmountPurchase;
import com.kcirque.stockmanagementfinal.Database.Model.DateAmountSalary;
import com.kcirque.stockmanagementfinal.Database.Model.DateAmountSales;
import com.kcirque.stockmanagementfinal.Database.Model.Profit;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentOverAllProfitLossBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OverAllProfitLossFragment extends Fragment {

    private static final String TAG = "OverAllProfitLossFragme";

    private FragmentOverAllProfitLossBinding mBinding;
    private static OverAllProfitLossFragment sInstance;

    private Profit mProfit = new Profit();
    List<DateAmountSales> sales = new ArrayList<>();
    List<DateAmountSalary> salaries = new ArrayList<>();
    List<DateAmountPurchase> purchases = new ArrayList<>();
    private HashSet<String> yearSpinnerList = new HashSet<>();
    private Spinner yearSpinner;
    private Context mContext;

    private DateConverter mDateConverter;
    private int date;

    public OverAllProfitLossFragment() {
        // Required empty public constructor
    }

    public static synchronized OverAllProfitLossFragment getInstance() {
        if (sInstance == null)
            sInstance = new OverAllProfitLossFragment();
        return sInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_over_all_profit_loss, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mBinding.progressBar.setVisibility(View.VISIBLE);
        setHasOptionsMenu(true);
        SharedPref sharedPref = new SharedPref(getContext());
        Seller seller = sharedPref.getSeller();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        mDateConverter = new DateConverter();
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        DatabaseReference adminRef;
        if (currentUser != null) {
            adminRef = mRootRef.child(currentUser.getUid());
        } else {
            adminRef = mRootRef.child(seller.getAdminUid());
        }

        mBinding.overAllProfitLossRecyclerView.setHasFixedSize(true);
        mBinding.overAllProfitLossRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        DatabaseReference profitRef = adminRef.child(Constant.PROFIT_REF);
        profitRef.child(Constant.SALES_REF).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sales.clear();
                date = 0;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    DateAmountSales dateAmountSales = data.getValue(DateAmountSales.class);
                    yearSpinnerList.add(String.valueOf(mDateConverter.getYear(dateAmountSales.getDate())));
                    sales.add(dateAmountSales);
                }
                mProfit.setSalesList(sales);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        profitRef.child(Constant.PURCHASE_REF).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                purchases.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    DateAmountPurchase purchase = data.getValue(DateAmountPurchase.class);
                    yearSpinnerList.add(String.valueOf(mDateConverter.getYear(purchase.getDate())));
                    purchases.add(purchase);
                }
                mProfit.setPurchaseList(purchases);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        profitRef.child(Constant.SALARY_REF).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                salaries.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    DateAmountSalary salary = data.getValue(DateAmountSalary.class);
                    yearSpinnerList.add(String.valueOf(mDateConverter.getYear(salary.getDate())));
                    salaries.add(salary);
                }
                mProfit.setSalaryList(salaries);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        profitRef.child(Constant.COST_REF).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<DateAmountCost> costs = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    DateAmountCost cost = data.getValue(DateAmountCost.class);
                    yearSpinnerList.add(String.valueOf(mDateConverter.getYear(cost.getDate())));
                    costs.add(cost);
                }
                mProfit.setCostList(costs);
                final OverAllProfitLossAdapter allProfitLossAdapter = new OverAllProfitLossAdapter(mContext, mProfit);
                mBinding.overAllProfitLossRecyclerView.setAdapter(allProfitLossAdapter);
                Log.e(TAG, "onDataChange: " + yearSpinnerList.size());
                if (yearSpinnerList.size() > 0) {
                    final List<String> years = new ArrayList<>(yearSpinnerList);
                    Log.e(TAG, "onDataChange: " + years.size());
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, years);
                    yearSpinner.setAdapter(adapter);

                    yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (yearSpinner.getSelectedView() != null) {
                                ((TextView) yearSpinner.getSelectedView()).setTextColor(getResources().getColor(android.R.color.white));
                            }
                            String year = years.get(position);
                            allProfitLossAdapter.getFilter().filter(year);
                            mBinding.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            mBinding.progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mBinding.progressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.over_all_profit_menu, menu);
        MenuItem yearMenuItem = menu.findItem(R.id.action_year_spinner);
        yearSpinner = (Spinner) yearMenuItem.getActionView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
