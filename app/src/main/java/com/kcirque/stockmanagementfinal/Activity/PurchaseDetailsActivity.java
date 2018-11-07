package com.kcirque.stockmanagementfinal.Activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.DateConverter;
import com.kcirque.stockmanagementfinal.Database.Model.Purchase;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.ActivityPurchaseDetailsBinding;

public class PurchaseDetailsActivity extends AppCompatActivity {

    private ActivityPurchaseDetailsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_purchase_details);

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setTitle("Purchase Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DateConverter dateConverter = new DateConverter();
        Intent intent = getIntent();
        if (intent != null) {
            Purchase purchase = (Purchase) intent.getSerializableExtra(Constant.EXTRA_PURCHASE);
            mBinding.supplierTextView.setText(purchase.getSupplierName());
            mBinding.dateTextView.setText(dateConverter.getDateInString(purchase.getPurchaseDate()));
            mBinding.productTextView.setText(purchase.getProductName());
            mBinding.companyTextView.setText(purchase.getCompanyName());
            mBinding.quantityTextView.setText(String.valueOf(purchase.getQuantity()));
            mBinding.buyPriceTextView.setText(String.valueOf(purchase.getActualPrice()));
            mBinding.sellPriceTextView.setText(String.valueOf(purchase.getSellingPrice()));
            mBinding.totalTextView.setText(String.valueOf(purchase.getTotalPrice()));
            mBinding.paidTextView.setText(String.valueOf(purchase.getPaidAmount()));
            mBinding.dueTextView.setText(String.valueOf(purchase.getDueAmount()));
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
