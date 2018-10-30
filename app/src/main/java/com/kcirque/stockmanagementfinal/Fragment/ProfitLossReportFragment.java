package com.kcirque.stockmanagementfinal.Fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.DateConverter;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.DateAmountCost;
import com.kcirque.stockmanagementfinal.Database.Model.DateAmountPurchase;
import com.kcirque.stockmanagementfinal.Database.Model.DateAmountSalary;
import com.kcirque.stockmanagementfinal.Database.Model.DateAmountSales;
import com.kcirque.stockmanagementfinal.Database.Model.Expense;
import com.kcirque.stockmanagementfinal.Database.Model.ProductSell;
import com.kcirque.stockmanagementfinal.Database.Model.Profit;
import com.kcirque.stockmanagementfinal.Database.Model.Purchase;
import com.kcirque.stockmanagementfinal.Database.Model.Salary;
import com.kcirque.stockmanagementfinal.Database.Model.Sales;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Database.Model.StockHand;
import com.kcirque.stockmanagementfinal.MainActivity;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentProfitLossReportBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfitLossReportFragment extends Fragment {
    private static ProfitLossReportFragment INSTANCE;

    private static final String TAG = "Profit Activity";
    private FragmentProfitLossReportBinding mBinding;

    private DateConverter mDateConverter;

    private double mTotalPurchase = 0;
    private double mTotalSales = 0;
    private double mTotalStockHand = 0;
    private double mTotalStockOut = 0;
    private double mTotalProfit = 0;
    private double mTotalCost = 0;
    private int mProfitType;

    private double mTotalSalary = 0;

    public static synchronized ProfitLossReportFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProfitLossReportFragment();
        }

        return INSTANCE;
    }


    public ProfitLossReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profit_loss_report, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(getContext());
        Seller seller = sharedPref.getSeller();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        mDateConverter = new DateConverter();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        DatabaseReference adminRef;
        if (user != null) {
            adminRef = rootRef.child(user.getUid());
        } else {
            adminRef = rootRef.child(seller.getAdminUid());
        }
        mBinding.linearLayout.setVisibility(View.GONE);
        mBinding.progressBar.setVisibility(View.VISIBLE);
        DatabaseReference profitRef = adminRef.child(Constant.PROFIT_REF);
        DatabaseReference salesRef = profitRef.child(Constant.SALES_REF);
        DatabaseReference purchaseRef = profitRef.child(Constant.PURCHASE_REF);
        DatabaseReference costRef = profitRef.child(Constant.COST_REF);
        final DatabaseReference salaryRef = profitRef.child(Constant.SALES_REF);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mProfitType = bundle.getInt(Constant.EXTRA_PROFIT_LOSS_TYPE);

            if (MainActivity.isNetworkAvailable(getContext())) {
                purchaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mTotalPurchase = 0;
                        mTotalStockHand = 0;
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            DateAmountPurchase purchase = data.getValue(DateAmountPurchase.class);
                            switch (mProfitType) {
                                case ProfitLossFragment.DAY_7_TYPE:
                                    if (mDateConverter.getDayCount(purchase.getDate()) <= 7) {
                                        mTotalPurchase = mTotalPurchase + purchase.getAmount();
                                    }
                                    break;
                                case ProfitLossFragment.WEEK_TYPE:
                                    if (mDateConverter.isLastWeek(purchase.getDate())) {
                                        mTotalPurchase = mTotalPurchase + purchase.getAmount();
                                    }
                                    break;
                                case ProfitLossFragment.DAY_30_TYPE:
                                    if (mDateConverter.getDayCount(purchase.getDate()) <= 30) {
                                        mTotalPurchase = mTotalPurchase + purchase.getAmount();
                                    }
                                    break;
                                case ProfitLossFragment.MONTH_TYPE:
                                    if (mDateConverter.isLastMonth(purchase.getDate())) {
                                        mTotalPurchase = mTotalPurchase + purchase.getAmount();
                                    }
                                    break;
                                case ProfitLossFragment.YEAR_TYPE:
                                    if (mDateConverter.isLastYear(purchase.getDate())) {
                                        mTotalPurchase = mTotalPurchase + purchase.getAmount();
                                    }
                                    break;
                            }
                        }
                        mBinding.totalPurchaseTextView.setText(String.valueOf(mTotalPurchase));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                costRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mTotalCost = 0;
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            DateAmountCost cost = data.getValue(DateAmountCost.class);
                            switch (mProfitType) {
                                case ProfitLossFragment.DAY_7_TYPE:
                                    if (mDateConverter.getDayCount(cost.getDate()) <= 7) {
                                        mTotalCost = mTotalCost + cost.getAmount();
                                    }
                                    break;
                                case ProfitLossFragment.WEEK_TYPE:
                                    if (mDateConverter.isLastWeek(cost.getDate())) {
                                        mTotalCost = mTotalCost + cost.getAmount();
                                    }
                                    break;
                                case ProfitLossFragment.DAY_30_TYPE:
                                    if (mDateConverter.getDayCount(cost.getDate()) <= 30) {
                                        mTotalCost = mTotalCost + cost.getAmount();
                                    }
                                    break;
                                case ProfitLossFragment.MONTH_TYPE:
                                    if (mDateConverter.isLastMonth(cost.getDate())) {
                                        mTotalCost = mTotalCost + cost.getAmount();
                                    }
                                    break;
                                case ProfitLossFragment.YEAR_TYPE:
                                    if (mDateConverter.isLastYear(cost.getDate())) {
                                        mTotalCost = mTotalCost + cost.getAmount();
                                    }
                                    break;
                            }
                        }
                        mBinding.totalCostTextView.setText(String.valueOf(mTotalCost));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                salaryRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mTotalSalary = 0;
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            DateAmountSalary salary = data.getValue(DateAmountSalary.class);
                            switch (mProfitType) {
                                case ProfitLossFragment.DAY_7_TYPE:
                                    if (mDateConverter.getDayCount(salary.getDate()) <= 7) {
                                        mTotalSalary = mTotalSalary + salary.getAmount();
                                    }
                                    break;
                                case ProfitLossFragment.WEEK_TYPE:
                                    if (mDateConverter.isLastWeek(salary.getDate())) {
                                        mTotalSalary = mTotalSalary + salary.getAmount();
                                    }
                                    break;
                                case ProfitLossFragment.DAY_30_TYPE:
                                    if (mDateConverter.getDayCount(salary.getDate()) <= 30) {
                                        mTotalSalary = mTotalSalary + salary.getAmount();
                                    }
                                    break;
                                case ProfitLossFragment.MONTH_TYPE:
                                    if (mDateConverter.isLastMonth(salary.getDate())) {
                                        mTotalSalary = mTotalSalary + salary.getAmount();
                                    }
                                    break;
                                case ProfitLossFragment.YEAR_TYPE:
                                    if (mDateConverter.isLastYear(salary.getDate())) {
                                        mTotalSalary = mTotalSalary + salary.getAmount();
                                    }
                                    break;
                            }
                        }
                        mBinding.totalEmpSalaryTextView.setText(String.valueOf(mTotalSalary));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                salesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mTotalSales = 0;
                        mTotalStockOut = 0;
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            DateAmountSales sales = data.getValue(DateAmountSales.class);
                            switch (mProfitType) {
                                case ProfitLossFragment.DAY_7_TYPE:
                                    if (mDateConverter.getDayCount(sales.getDate()) <= 7) {
                                        mTotalSales = mTotalSales + sales.getAmount();
                                        mTotalStockOut = mTotalStockOut + sales.getStockOutAmount();
                                    }
                                    break;
                                case ProfitLossFragment.WEEK_TYPE:
                                    if (mDateConverter.isLastWeek(sales.getDate())) {
                                        mTotalSales = mTotalSales + sales.getAmount();
                                        mTotalStockOut = mTotalStockOut + sales.getStockOutAmount();
                                    }
                                    break;
                                case ProfitLossFragment.DAY_30_TYPE:
                                    if (mDateConverter.getDayCount(sales.getDate()) <= 30) {
                                        mTotalSales = mTotalSales + sales.getAmount();
                                        mTotalStockOut = mTotalStockOut + sales.getStockOutAmount();
                                    }
                                    break;
                                case ProfitLossFragment.MONTH_TYPE:
                                    if (mDateConverter.isLastMonth(sales.getDate())) {
                                        mTotalSales = mTotalSales + sales.getAmount();
                                        mTotalStockOut = mTotalStockOut + sales.getStockOutAmount();
                                    }
                                    break;
                                case ProfitLossFragment.YEAR_TYPE:
                                    if (mDateConverter.isLastYear(sales.getDate())) {
                                        mTotalSales = mTotalSales + sales.getAmount();
                                        mTotalStockOut = mTotalStockOut + sales.getStockOutAmount();
                                    }
                                    break;
                            }
                        }
                        mBinding.totalSellAmountTextView.setText(String.valueOf(mTotalSales));
                        mTotalStockHand = mTotalPurchase - mTotalStockOut;
                        mBinding.totalStockTextView.setText(String.valueOf(mTotalStockHand));
                        mTotalProfit = ((mTotalSales + mTotalStockHand) - mTotalPurchase) - mTotalCost;
                        if (mProfitType == ProfitLossFragment.MONTH_TYPE || mProfitType == ProfitLossFragment.YEAR_TYPE) {
                            mBinding.empSalLinearLayout.setVisibility(View.VISIBLE);
                            mTotalProfit = mTotalProfit - mTotalSalary;
                        }
                        mBinding.totalSellAmountTextView.setText(String.valueOf(mTotalSales));
                        mBinding.totalStockTextView.setText(String.valueOf(mTotalStockHand));
                        if (mTotalProfit >= 0) {
                            mBinding.linearLayout.setVisibility(View.VISIBLE);
                            mBinding.progressBar.setVisibility(View.GONE);
                            mBinding.totalProfitTextView.setText(String.valueOf(mTotalProfit));
                        } else {
                            mBinding.linearLayout.setVisibility(View.VISIBLE);
                            mBinding.progressBar.setVisibility(View.GONE);
                            mBinding.profitTextView.setText("Total Loss");
                            mBinding.totalProfitTextView.setText(String.valueOf(mTotalProfit));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                mBinding.progressBar.setVisibility(View.GONE);
                Snackbar.make(mBinding.rootView, "No internet connection", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

}

