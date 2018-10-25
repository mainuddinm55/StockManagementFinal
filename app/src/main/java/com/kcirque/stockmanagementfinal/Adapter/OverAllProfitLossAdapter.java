package com.kcirque.stockmanagementfinal.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.DateConverter;
import com.kcirque.stockmanagementfinal.Database.Model.DateAmountCost;
import com.kcirque.stockmanagementfinal.Database.Model.DateAmountPurchase;
import com.kcirque.stockmanagementfinal.Database.Model.DateAmountSalary;
import com.kcirque.stockmanagementfinal.Database.Model.DateAmountSales;
import com.kcirque.stockmanagementfinal.Database.Model.Profit;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

public class OverAllProfitLossAdapter extends RecyclerView.Adapter<OverAllProfitLossAdapter.ProfitLossHolder> implements Filterable {

    private Context mContext;
    private Profit mProfit;
    private Profit mFilteredProfit;
    private DateConverter mDateConverter;
    private List<double[]> profitList = new ArrayList<>(5);
    private double[] slNo = new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    private double[] month = new double[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};

    public OverAllProfitLossAdapter(Context context, Profit profit) {
        this.mContext = context;
        this.mProfit = profit;
        this.mFilteredProfit = profit;
    }

    @NonNull
    @Override
    public ProfitLossHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.over_all_profit_loss_layour_row, viewGroup, false);
        return new ProfitLossHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfitLossHolder profitLossHolder, int i) {
        mDateConverter = new DateConverter();
        profitLossHolder.singleRowRecyclerView.setHasFixedSize(true);
        profitLossHolder.singleRowRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        getMonthWiseList(mFilteredProfit);
        SingleProfitLossAdapter adapter = new SingleProfitLossAdapter(mContext, profitList.get(i), i);
        profitLossHolder.singleRowRecyclerView.setAdapter(adapter);

    }

    @Override
    public int getItemCount() {
        return 8;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String selectedYear = constraint.toString();
                List<DateAmountCost> tempCosts = new ArrayList<>();
                List<DateAmountSalary> tempSalaries = new ArrayList<>();
                List<DateAmountPurchase> tempPurchases = new ArrayList<>();
                List<DateAmountSales> tempSales = new ArrayList<>();
                if (selectedYear.isEmpty()) {
                    mFilteredProfit = mProfit;
                } else {
                    for (DateAmountCost cost : mProfit.getCostList()) {
                        String year = String.valueOf(mDateConverter.getYear(cost.getDate()));
                        if (year.equals(selectedYear)) {
                            tempCosts.add(cost);
                        }
                    }
                    for (DateAmountSalary salary : mProfit.getSalaryList()) {
                        String year = String.valueOf(mDateConverter.getYear(salary.getDate()));
                        if (year.equals(selectedYear)) {
                            tempSalaries.add(salary);
                        }
                    }
                    for (DateAmountPurchase purchase : mProfit.getPurchaseList()) {
                        String year = String.valueOf(mDateConverter.getYear(purchase.getDate()));
                        if (year.equals(selectedYear)) {
                            tempPurchases.add(purchase);
                        }
                    }
                    for (DateAmountSales dateAmountSales : mProfit.getSalesList()) {
                        String year = String.valueOf(mDateConverter.getYear(dateAmountSales.getDate()));
                        if (year.equals(selectedYear)) {
                            tempSales.add(dateAmountSales);
                        }
                    }
                    Profit profit = new Profit();
                    profit.setSalesList(tempSales);
                    profit.setPurchaseList(tempPurchases);
                    profit.setSalaryList(tempSalaries);
                    profit.setCostList(tempCosts);
                    mFilteredProfit = profit;
                }
                FilterResults results = new FilterResults();
                results.values = mFilteredProfit;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredProfit = (Profit) results.values;
                notifyDataSetChanged();
            }
        };
    }


    private void getMonthWiseList(Profit profit) {
        List<DateAmountSales> dateAmountSales = profit.getSalesList();
        List<DateAmountPurchase> dateAmountPurchases = profit.getPurchaseList();
        List<DateAmountCost> dateAmountCosts = profit.getCostList();
        List<DateAmountSalary> dateAmountSalaries = profit.getSalaryList();
        double[] totalSalesAmount = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        double[] totalPurchaseAmount = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        double[] totalCostAmount = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        double[] totalStockHandAmount = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        double[] totalStockOutAmount = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        double[] totalSalAmount = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        double[] totalProfitAmount = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        for (DateAmountCost cost : dateAmountCosts) {
            double amount;
            switch (mDateConverter.getMonth(cost.getDate())) {
                case 0:
                    amount = totalCostAmount[0];
                    amount = amount + cost.getAmount();
                    totalCostAmount[0] = amount;
                    break;
                case 1:
                    amount = totalCostAmount[1];
                    amount = amount + cost.getAmount();
                    totalCostAmount[1] = amount;
                    break;
                case 2:
                    amount = totalCostAmount[2];
                    amount = amount + cost.getAmount();
                    totalCostAmount[2] = amount;
                    break;
                case 3:
                    amount = totalCostAmount[11];
                    amount = amount + cost.getAmount();
                    totalCostAmount[3] = amount;
                    break;
                case 4:
                    amount = totalCostAmount[4];
                    amount = amount + cost.getAmount();
                    totalCostAmount[4] = amount;
                    break;
                case 5:
                    amount = totalCostAmount[5];
                    amount = amount + cost.getAmount();
                    totalCostAmount[5] = amount;
                    break;
                case 6:
                    amount = totalCostAmount[6];
                    amount = amount + cost.getAmount();
                    totalCostAmount[6] = amount;
                    break;
                case 7:
                    amount = totalCostAmount[7];
                    amount = amount + cost.getAmount();
                    totalCostAmount[7] = amount;
                    break;
                case 8:
                    amount = totalCostAmount[8];
                    amount = amount + cost.getAmount();
                    totalCostAmount[8] = amount;
                    break;
                case 9:
                    amount = totalCostAmount[9];
                    amount = amount + cost.getAmount();
                    totalCostAmount[9] = amount;
                    break;
                case 10:
                    amount = totalCostAmount[10];
                    amount = amount + cost.getAmount();
                    totalCostAmount[10] = amount;
                    break;
                case 11:
                    amount = totalCostAmount[11];
                    amount = amount + cost.getAmount();
                    totalCostAmount[11] = amount;
                    break;
            }
        }
        for (DateAmountSalary salary : dateAmountSalaries) {
            double amount;
            switch (mDateConverter.getMonth(salary.getDate())) {
                case 0:
                    amount = totalSalAmount[0];
                    amount = amount + salary.getAmount();
                    totalSalAmount[0] = amount;
                    break;
                case 1:
                    amount = totalSalAmount[1];
                    amount = amount + salary.getAmount();
                    totalSalAmount[1] = amount;
                    break;
                case 2:
                    amount = totalSalAmount[2];
                    amount = amount + salary.getAmount();
                    totalSalAmount[2] = amount;
                    break;
                case 3:
                    amount = totalSalAmount[3];
                    amount = amount + salary.getAmount();
                    totalSalAmount[3] = amount;
                    break;
                case 4:
                    amount = totalSalAmount[4];
                    amount = amount + salary.getAmount();
                    totalSalAmount[4] = amount;
                    break;
                case 5:
                    amount = totalSalAmount[5];
                    amount = amount + salary.getAmount();
                    totalSalAmount[5] = amount;
                    break;
                case 6:
                    amount = totalSalAmount[6];
                    amount = amount + salary.getAmount();
                    totalSalAmount[6] = amount;
                    break;
                case 7:
                    amount = totalSalAmount[7];
                    amount = amount + salary.getAmount();
                    totalSalAmount[7] = amount;
                    break;
                case 8:
                    amount = totalSalAmount[8];
                    amount = amount + salary.getAmount();
                    totalSalAmount[8] = amount;
                    break;
                case 9:
                    amount = totalSalAmount[9];
                    amount = amount + salary.getAmount();
                    totalSalAmount[9] = amount;
                    break;
                case 10:
                    amount = totalSalAmount[10];
                    amount = amount + salary.getAmount();
                    totalSalAmount[10] = amount;
                    break;
                case 11:
                    amount = totalSalAmount[11];
                    amount = amount + salary.getAmount();
                    totalSalAmount[11] = amount;
                    break;
            }
        }

        for (DateAmountSales sales : dateAmountSales) {
            double amount;
            double stockOutAmount;
            switch (mDateConverter.getMonth(sales.getDate())) {
                case 0:
                    amount = totalSalesAmount[0];
                    amount = amount + sales.getAmount();
                    totalSalesAmount[0] = amount;
                    stockOutAmount = totalStockOutAmount[0];
                    stockOutAmount = stockOutAmount + sales.getStockOutAmount();
                    totalStockOutAmount[0] = stockOutAmount;
                    break;
                case 1:
                    amount = totalSalesAmount[1];
                    amount = amount + sales.getAmount();
                    totalSalesAmount[1] = amount;
                    stockOutAmount = totalStockOutAmount[1];
                    stockOutAmount = stockOutAmount + sales.getStockOutAmount();
                    totalStockOutAmount[1] = stockOutAmount;
                    break;
                case 2:
                    amount = totalSalesAmount[2];
                    amount = amount + sales.getAmount();
                    totalSalesAmount[2] = amount;
                    stockOutAmount = totalStockOutAmount[2];
                    stockOutAmount = stockOutAmount + sales.getStockOutAmount();
                    totalStockOutAmount[2] = stockOutAmount;
                    break;
                case 3:
                    amount = totalSalesAmount[3];
                    amount = amount + sales.getAmount();
                    totalSalesAmount[3] = amount;
                    stockOutAmount = totalStockOutAmount[3];
                    stockOutAmount = stockOutAmount + sales.getStockOutAmount();
                    totalStockOutAmount[3] = stockOutAmount;
                    break;
                case 4:
                    amount = totalSalesAmount[4];
                    amount = amount + sales.getAmount();
                    totalSalesAmount[4] = amount;
                    stockOutAmount = totalStockOutAmount[4];
                    stockOutAmount = stockOutAmount + sales.getStockOutAmount();
                    totalStockOutAmount[4] = stockOutAmount;
                    break;
                case 5:
                    amount = totalSalesAmount[5];
                    amount = amount + sales.getAmount();
                    totalSalesAmount[5] = amount;
                    stockOutAmount = totalStockOutAmount[5];
                    stockOutAmount = stockOutAmount + sales.getStockOutAmount();
                    totalStockOutAmount[5] = stockOutAmount;
                    break;
                case 6:
                    amount = totalSalesAmount[6];
                    amount = amount + sales.getAmount();
                    totalSalesAmount[6] = amount;
                    stockOutAmount = totalStockOutAmount[6];
                    stockOutAmount = stockOutAmount + sales.getStockOutAmount();
                    totalStockOutAmount[6] = stockOutAmount;
                    break;
                case 7:
                    amount = totalSalesAmount[7];
                    amount = amount + sales.getAmount();
                    totalSalesAmount[7] = amount;
                    stockOutAmount = totalStockOutAmount[7];
                    stockOutAmount = stockOutAmount + sales.getStockOutAmount();
                    totalStockOutAmount[7] = stockOutAmount;
                    break;
                case 8:
                    amount = totalSalesAmount[8];
                    amount = amount + sales.getAmount();
                    totalSalesAmount[8] = amount;
                    stockOutAmount = totalStockOutAmount[8];
                    stockOutAmount = stockOutAmount + sales.getStockOutAmount();
                    totalStockOutAmount[8] = stockOutAmount;
                    break;
                case 9:
                    amount = totalSalesAmount[9];
                    amount = amount + sales.getAmount();
                    totalSalesAmount[9] = amount;
                    stockOutAmount = totalStockOutAmount[9];
                    stockOutAmount = stockOutAmount + sales.getStockOutAmount();
                    totalStockOutAmount[9] = stockOutAmount;
                    break;
                case 10:
                    amount = totalSalesAmount[10];
                    amount = amount + sales.getAmount();
                    totalSalesAmount[10] = amount;
                    stockOutAmount = totalStockOutAmount[10];
                    stockOutAmount = stockOutAmount + sales.getStockOutAmount();
                    totalStockOutAmount[10] = stockOutAmount;
                    break;
                case 11:
                    amount = totalSalesAmount[11];
                    amount = amount + sales.getAmount();
                    totalSalesAmount[11] = amount;
                    stockOutAmount = totalStockOutAmount[11];
                    stockOutAmount = stockOutAmount + sales.getStockOutAmount();
                    totalStockOutAmount[11] = stockOutAmount;
                    break;
            }
        }
        for (DateAmountPurchase purchase : dateAmountPurchases) {
            double amount;
            double stockHandAmount;
            double profitLossAmount;
            switch (mDateConverter.getMonth(purchase.getDate())) {
                case 0:
                    amount = totalPurchaseAmount[0];
                    amount = amount + purchase.getAmount();
                    totalPurchaseAmount[0] = amount;
                    stockHandAmount = amount - totalStockOutAmount[0];
                    totalStockHandAmount[0] = stockHandAmount;
                    profitLossAmount = (totalSalesAmount[0] + totalStockHandAmount[0]) - (totalPurchaseAmount[0] + totalCostAmount[0] + totalSalAmount[0]);
                    totalProfitAmount[0] = profitLossAmount;
                    break;
                case 1:
                    amount = totalPurchaseAmount[1];
                    amount = amount + purchase.getAmount();
                    totalPurchaseAmount[1] = amount;
                    stockHandAmount = amount - totalStockOutAmount[1];
                    totalStockHandAmount[1] = stockHandAmount;
                    profitLossAmount = (totalSalesAmount[1] + totalStockHandAmount[1]) - (totalPurchaseAmount[1] + totalCostAmount[1] + totalSalAmount[1]);
                    totalProfitAmount[1] = profitLossAmount;
                    break;
                case 2:
                    amount = totalPurchaseAmount[2];
                    amount = amount + purchase.getAmount();
                    totalPurchaseAmount[2] = amount;
                    stockHandAmount = amount - totalStockOutAmount[2];
                    totalStockHandAmount[2] = stockHandAmount;
                    profitLossAmount = (totalSalesAmount[2] + totalStockHandAmount[2]) - (totalPurchaseAmount[2] + totalCostAmount[2] + totalSalAmount[2]);
                    totalProfitAmount[0] = profitLossAmount;
                    break;
                case 3:
                    amount = totalPurchaseAmount[3];
                    amount = amount + purchase.getAmount();
                    totalPurchaseAmount[3] = amount;
                    stockHandAmount = amount - totalStockOutAmount[3];
                    totalStockHandAmount[3] = stockHandAmount;
                    profitLossAmount = (totalSalesAmount[3] + totalStockHandAmount[3]) - (totalPurchaseAmount[3] + totalCostAmount[3] + totalSalAmount[3]);
                    totalProfitAmount[3] = profitLossAmount;
                    break;
                case 4:
                    amount = totalPurchaseAmount[4];
                    amount = amount + purchase.getAmount();
                    totalPurchaseAmount[4] = amount;
                    stockHandAmount = amount - totalStockOutAmount[4];
                    totalStockHandAmount[4] = stockHandAmount;
                    profitLossAmount = (totalSalesAmount[4] + totalStockHandAmount[4]) - (totalPurchaseAmount[4] + totalCostAmount[4] + totalSalAmount[4]);
                    totalProfitAmount[4] = profitLossAmount;
                    break;
                case 5:
                    amount = totalPurchaseAmount[5];
                    amount = amount + purchase.getAmount();
                    totalPurchaseAmount[5] = amount;
                    stockHandAmount = amount - totalStockOutAmount[5];
                    totalStockHandAmount[5] = stockHandAmount;
                    profitLossAmount = (totalSalesAmount[5] + totalStockHandAmount[5]) - (totalPurchaseAmount[5] + totalCostAmount[5] + totalSalAmount[5]);
                    totalProfitAmount[5] = profitLossAmount;
                    break;
                case 6:
                    amount = totalPurchaseAmount[6];
                    amount = amount + purchase.getAmount();
                    totalPurchaseAmount[6] = amount;
                    stockHandAmount = amount - totalStockOutAmount[6];
                    totalStockHandAmount[6] = stockHandAmount;
                    profitLossAmount = (totalSalesAmount[6] + totalStockHandAmount[6]) - (totalPurchaseAmount[6] + totalCostAmount[6] + totalSalAmount[6]);
                    totalProfitAmount[6] = profitLossAmount;
                    break;
                case 7:
                    amount = totalPurchaseAmount[7];
                    amount = amount + purchase.getAmount();
                    totalPurchaseAmount[7] = amount;
                    stockHandAmount = amount - totalStockOutAmount[7];
                    totalStockHandAmount[7] = stockHandAmount;
                    profitLossAmount = (totalSalesAmount[7] + totalStockHandAmount[7]) - (totalPurchaseAmount[7] + totalCostAmount[7] + totalSalAmount[7]);
                    totalProfitAmount[7] = profitLossAmount;
                    break;
                case 8:
                    amount = totalPurchaseAmount[8];
                    amount = amount + purchase.getAmount();
                    totalPurchaseAmount[8] = amount;
                    stockHandAmount = amount - totalStockOutAmount[8];
                    totalStockHandAmount[8] = stockHandAmount;
                    profitLossAmount = (totalSalesAmount[8] + totalStockHandAmount[8]) - (totalPurchaseAmount[8] + totalCostAmount[8] + totalSalAmount[8]);
                    totalProfitAmount[8] = profitLossAmount;
                    break;
                case 9:
                    amount = totalPurchaseAmount[9];
                    amount = amount + purchase.getAmount();
                    totalPurchaseAmount[9] = amount;
                    stockHandAmount = amount - totalStockOutAmount[9];
                    totalStockHandAmount[9] = stockHandAmount;
                    profitLossAmount = (totalSalesAmount[9] + totalStockHandAmount[9]) - (totalPurchaseAmount[9] + totalCostAmount[9] + totalSalAmount[9]);
                    totalProfitAmount[9] = profitLossAmount;
                    break;
                case 10:
                    amount = totalPurchaseAmount[10];
                    amount = amount + purchase.getAmount();
                    totalPurchaseAmount[10] = amount;
                    stockHandAmount = amount - totalStockOutAmount[10];
                    totalStockHandAmount[10] = stockHandAmount;
                    profitLossAmount = (totalSalesAmount[10] + totalStockHandAmount[10]) - (totalPurchaseAmount[10] + totalCostAmount[10] + totalSalAmount[10]);
                    totalProfitAmount[10] = profitLossAmount;
                    break;
                case 11:
                    amount = totalPurchaseAmount[11];
                    amount = amount + purchase.getAmount();
                    totalPurchaseAmount[11] = amount;
                    stockHandAmount = amount - totalStockOutAmount[11];
                    totalStockHandAmount[11] = stockHandAmount;
                    profitLossAmount = (totalSalesAmount[11] + totalStockHandAmount[11]) - (totalPurchaseAmount[11] + totalCostAmount[11] + totalSalAmount[11]);
                    totalProfitAmount[11] = profitLossAmount;
                    break;
            }
        }
        profitList.add(Constant.TOTAL_SL_NO_INDEX, slNo);
        profitList.add(Constant.TOTAL_MONTH_INDEX, month);
        profitList.add(Constant.TOTAL_PURCHASE_INDEX, totalPurchaseAmount);
        profitList.add(Constant.TOTAL_STOCK_HAND_INDEX, totalStockHandAmount);
        profitList.add(Constant.TOTAL_SALES_INDEX, totalSalesAmount);
        profitList.add(Constant.TOTAL_COST_INDEX, totalCostAmount);
        profitList.add(Constant.TOTAL_SALARY_INDEX, totalSalAmount);
        profitList.add(Constant.TOTAL_PROFIT_INDEX, totalProfitAmount);


    }

    public class ProfitLossHolder extends RecyclerView.ViewHolder {

        RecyclerView singleRowRecyclerView;

        public ProfitLossHolder(@NonNull View itemView) {
            super(itemView);
            singleRowRecyclerView = itemView.findViewById(R.id.single_row_recycler_view);
        }
    }
}
