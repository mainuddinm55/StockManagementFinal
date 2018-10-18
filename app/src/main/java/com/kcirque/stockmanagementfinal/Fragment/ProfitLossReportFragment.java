package com.kcirque.stockmanagementfinal.Fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.kcirque.stockmanagementfinal.Database.Model.Expense;
import com.kcirque.stockmanagementfinal.Database.Model.ProductSell;
import com.kcirque.stockmanagementfinal.Database.Model.Purchase;
import com.kcirque.stockmanagementfinal.Database.Model.Salary;
import com.kcirque.stockmanagementfinal.Database.Model.Sales;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Database.Model.StockHand;
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

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private SharedPref mSharedPref;
    private DatabaseReference mAdminRef;
    private DatabaseReference mPurchaseRef;
    private DatabaseReference mSalesRef;
    private DatabaseReference mExpenseRef;
    private DateConverter mDateConverter;

    private double mTotalPurchase = 0;
    private double mTotalSales = 0;
    private double mTotalStock = 0;
    private double mTotalProfit = 0;
    private double mTotalCost = 0;
    private int mProfitType;
    private List<StockHand> mStockList = new ArrayList<>();
    private List<ProductSell> mProductSell = new ArrayList<>();
    private StockHand stockHand = null;
    private DatabaseReference mSalaryRef;
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
        mSharedPref = new SharedPref(getContext());
        Seller seller = mSharedPref.getSeller();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDateConverter = new DateConverter();
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        if (mUser != null) {
            mAdminRef = mRootRef.child(mUser.getUid());
        } else {
            mAdminRef = mRootRef.child(seller.getAdminUid());
        }
        mPurchaseRef = mAdminRef.child(Constant.PURCHASE_REF);
        mSalesRef = mAdminRef.child(Constant.SALES_REF);
        mSalaryRef = mAdminRef.child(Constant.SALARY_REF);
        mExpenseRef = mAdminRef.child(Constant.EXPENSE_REF);
        mBinding.linearLayout.setVisibility(View.GONE);
        mBinding.progressBar.setVisibility(View.VISIBLE);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mProfitType = bundle.getInt(Constant.EXTRA_PROFIT_LOSS_TYPE);
            switch (mProfitType) {
                case ProfitLossFragment.DAY_7_TYPE:
                    getActivity().setTitle("7 Day's Profit Loss");
                    break;
                case ProfitLossFragment.WEEK_TYPE:
                    getActivity().setTitle("last Week Profit Loss");
                    break;
                case ProfitLossFragment.DAY_30_TYPE:
                    getActivity().setTitle("30 Day's Profit Loss");
                    break;
                case ProfitLossFragment.MONTH_TYPE:
                    getActivity().setTitle("Last Month Profit Loss");
                    break;
                case ProfitLossFragment.YEAR_TYPE:
                    getActivity().setTitle("Last Year Profit Loss");
                    break;

            }
        }
        new Thread(new Runnable() {
            private int productId = 0;
            private int totalPurchaseQuantity = 0;
            private double buyPrice;
            private int sellQuantity;

            @Override
            public void run() {
                mPurchaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mTotalPurchase = 0;
                        mStockList.clear();
                        for (DataSnapshot postData : dataSnapshot.getChildren()) {
                            productId = 0;
                            totalPurchaseQuantity = 0;
                            buyPrice = 0;
                            sellQuantity = 0;
                            for (DataSnapshot data : postData.getChildren()) {
                                Purchase purchase = data.getValue(Purchase.class);
                                long purchaseDate = purchase.getPurchaseDate();
                                switch (mProfitType) {
                                    case ProfitLossFragment.DAY_7_TYPE:
                                        if (mDateConverter.getDayCount(purchaseDate) <= 7) {
                                            mTotalPurchase = mTotalPurchase + purchase.getTotalPrice();
                                            getStockHand(purchase);
                                        }
                                        break;
                                    case ProfitLossFragment.WEEK_TYPE:
                                        if (mDateConverter.isLastWeek(purchaseDate)) {
                                            mTotalPurchase = mTotalPurchase + purchase.getTotalPrice();
                                            getStockHand(purchase);
                                        }
                                        break;
                                    case ProfitLossFragment.DAY_30_TYPE:
                                        if (mDateConverter.getDayCount(purchaseDate) <= 30) {
                                            mTotalPurchase = mTotalPurchase + purchase.getTotalPrice();
                                            getStockHand(purchase);
                                        }
                                        break;
                                    case ProfitLossFragment.MONTH_TYPE:
                                        if (mDateConverter.isLastMonth(purchaseDate)) {
                                            mTotalPurchase = mTotalPurchase + purchase.getTotalPrice();
                                            getStockHand(purchase);
                                        }
                                        break;
                                    case ProfitLossFragment.YEAR_TYPE:
                                        if (mDateConverter.isLastYear(purchaseDate)) {
                                            mTotalPurchase = mTotalPurchase + purchase.getTotalPrice();
                                            getStockHand(purchase);
                                        }
                                        break;
                                }
                            }
                        }
                        mBinding.totalPurchaseTextView.setText(String.valueOf(mTotalPurchase));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mExpenseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mTotalCost = 0;
                        for (DataSnapshot postData : dataSnapshot.getChildren()) {
                            Expense expense = postData.getValue(Expense.class);
                            switch (mProfitType) {
                                case ProfitLossFragment.DAY_7_TYPE:
                                    if (mDateConverter.getDayCount(expense.getDate()) <= 7) {
                                        mTotalCost = mTotalCost + expense.getExpenseAmount();
                                    }
                                    break;
                                case ProfitLossFragment.WEEK_TYPE:
                                    if (mDateConverter.isLastWeek(expense.getDate())) {
                                        mTotalCost = mTotalCost + expense.getExpenseAmount();
                                    }
                                    break;
                                case ProfitLossFragment.DAY_30_TYPE:
                                    if (mDateConverter.getDayCount(expense.getDate()) <= 30) {
                                        mTotalCost = mTotalCost + expense.getExpenseAmount();
                                    }
                                    break;
                                case ProfitLossFragment.MONTH_TYPE:
                                    if (mDateConverter.isLastMonth(expense.getDate())) {
                                        mTotalCost = mTotalCost + expense.getExpenseAmount();
                                    }
                                    break;
                                case ProfitLossFragment.YEAR_TYPE:
                                    if (mDateConverter.isLastYear(expense.getDate())) {
                                        mTotalCost = mTotalCost + expense.getExpenseAmount();
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

                mSalaryRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mTotalSalary = 0;
                        for (DataSnapshot postData : dataSnapshot.getChildren()) {
                            Salary salary = postData.getValue(Salary.class);
                            if (salary != null) {
                                switch (mProfitType) {
                                    case ProfitLossFragment.MONTH_TYPE:
                                        if (mDateConverter.isLastMonth(salary.getDate())) {
                                            mTotalSalary = mTotalSalary + salary.getAmount();
                                        }
                                        break;
                                }
                            }
                        }

                        mBinding.totalEmpSalaryTextView.setText(String.valueOf(mTotalSalary));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mSalesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mTotalSales = 0;
                        mProductSell.clear();
                        for (DataSnapshot postData : dataSnapshot.getChildren()) {
                            Sales sales = postData.getValue(Sales.class);
                            switch (mProfitType) {
                                case ProfitLossFragment.DAY_7_TYPE:
                                    if (mDateConverter.getDayCount(sales.getSalesDate()) <= 7) {
                                        mProductSell.addAll(sales.getSelectedProduct());
                                        mTotalSales = mTotalSales + sales.getTotal();
                                    }
                                    break;
                                case ProfitLossFragment.WEEK_TYPE:
                                    if (mDateConverter.isLastWeek(sales.getSalesDate())) {
                                        mProductSell.addAll(sales.getSelectedProduct());
                                        mTotalSales = mTotalSales + sales.getTotal();
                                    }
                                    break;
                                case ProfitLossFragment.DAY_30_TYPE:
                                    if (mDateConverter.getDayCount(sales.getSalesDate()) <= 30) {
                                        mProductSell.addAll(sales.getSelectedProduct());
                                        mTotalSales = mTotalSales + sales.getTotal();
                                    }
                                    break;
                                case ProfitLossFragment.MONTH_TYPE:
                                    if (mDateConverter.isLastMonth(sales.getSalesDate())) {
                                        mProductSell.addAll(sales.getSelectedProduct());
                                        mTotalSales = mTotalSales + sales.getTotal();
                                    }
                                    break;
                                case ProfitLossFragment.YEAR_TYPE:
                                    if (mDateConverter.isLastYear(sales.getSalesDate())) {
                                        mProductSell.addAll(sales.getSelectedProduct());
                                        mTotalSales = mTotalSales + sales.getTotal();
                                    }
                                    break;
                            }

                        }

                        if (mStockList.size() > 0) {
                            if (mProductSell.size() > 0) {
                                mTotalStock = 0;
                                for (ProductSell productSell : mProductSell) {
                                    for (StockHand stockHand : mStockList) {
                                        if (productSell.getProductId() == stockHand.getProductId()) {
                                            stockHand.setSellQuantity(productSell.getQuantity());
                                        }
                                    }
                                }
                            }
                            for (StockHand stockHand : mStockList) {
                                int stockQty = stockHand.getPurchaseQuantity() - stockHand.getSellQuantity();
                                double totalStock = stockHand.getBuyPrice() * stockQty;
                                mTotalStock = mTotalStock + totalStock;
                                Log.e(TAG, "Stock " + stockHand.getProductId() + " = " + mTotalStock);
                            }

                            mBinding.totalStockTextView.setText(String.valueOf(mTotalStock));
                        }

                        mTotalProfit = ((mTotalSales + mTotalStock) - mTotalPurchase) - mTotalCost;
                        if (mProfitType == ProfitLossFragment.MONTH_TYPE) {
                            mBinding.empSalLinearLayout.setVisibility(View.VISIBLE);
                            mTotalProfit = mTotalProfit - mTotalSalary;
                        }
                        mBinding.totalSellAmountTextView.setText(String.valueOf(mTotalSales));
                        mBinding.totalStockTextView.setText(String.valueOf(mTotalStock));
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

                        mTotalStock = 0;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            void getStockHand(Purchase purchase) {
                if (productId == purchase.getProductId()) {
                    mStockList.remove(stockHand);
                    totalPurchaseQuantity = totalPurchaseQuantity + purchase.getQuantity();
                    buyPrice = purchase.getActualPrice();
                    stockHand = new StockHand(productId, totalPurchaseQuantity, buyPrice, sellQuantity);

                } else {
                    productId = purchase.getProductId();
                    totalPurchaseQuantity = totalPurchaseQuantity + purchase.getQuantity();
                    buyPrice = purchase.getActualPrice();
                    stockHand = new StockHand(productId, totalPurchaseQuantity, buyPrice, sellQuantity);
                }

                mStockList.add(stockHand);

            }
        }).start();
    }
}
