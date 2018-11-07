package com.kcirque.stockmanagementfinal.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Adapter.ProductDialogAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Product;
import com.kcirque.stockmanagementfinal.Database.Model.ProductSell;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Database.Model.StockHand;
import com.kcirque.stockmanagementfinal.Fragment.SalesFragment;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.ActivitySaleProductBinding;

import java.util.ArrayList;
import java.util.List;

public class SaleProductActivity extends AppCompatActivity {

    private static final String TAG = "Sale ProductForRoom";
    private ActivitySaleProductBinding mBinding;

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private SharedPref mSharedPref;
    private DatabaseReference mAdminRef;
    private DatabaseReference mRootRef;
    private DatabaseReference mProductRef;

    private List<Product> mProductList = new ArrayList<>();
    private ProductDialogAdapter mAdapter;
    private int mProductId;
    private int mHasQuantity;
    private double mBuyPrice;
    private Product selectedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_product);
        mBinding = DataBindingUtil.setContentView(SaleProductActivity.this, R.layout.activity_sale_product);

        mBinding.toolbar.setTitle("Choose Product");
        setSupportActionBar(mBinding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        mSharedPref = new SharedPref(this);
        Seller seller = mSharedPref.getSeller();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mRootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        if (mUser != null) {
            mAdminRef = mRootRef.child(mUser.getUid());
        } else {
            mAdminRef = mRootRef.child(seller.getAdminUid());
        }
        mProductRef = mAdminRef.child(Constant.PRODUCT_REF);

        mBinding.productNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductDialog();
            }
        });

        mBinding.addBtn.setOnClickListener(new View.OnClickListener() {
            private int oldPriceQty;
            private ProductSell productSell;
            private ProductSell newPriceSell;
            private ProductSell oldPriceSell;

            @Override
            public void onClick(View v) {
                if (mBinding.productNameTextView.getText().toString().isEmpty()) {
                    mBinding.productNameTextView.setError("Select a Product");
                    mBinding.productNameTextView.requestFocus();
                    return;
                }
                if (mBinding.quantityEditText.getText().toString().isEmpty()) {
                    mBinding.quantityEditText.setError("Quantity Required");
                    mBinding.quantityEditText.requestFocus();
                    return;
                }
                String productName = mBinding.productNameTextView.getText().toString();
                double price = Double.parseDouble(mBinding.priceView.getText().toString());
                int qty = Integer.parseInt(mBinding.quantityEditText.getText().toString());
                if (qty > mHasQuantity) {
                    mBinding.quantityEditText.setError("Quantity must give under stock hand");
                    mBinding.quantityEditText.requestFocus();
                    return;
                }
                List<ProductSell> oldPriceProductList = (List<ProductSell>) getIntent().getSerializableExtra(Constant.EXTRA_OLD_PRICE_PRODUCT_LIST);

                if (oldPriceProductList != null && oldPriceProductList.size() > 0) {
                    for (ProductSell productSell : oldPriceProductList) {
                        if (productSell.getProductId() == selectedProduct.getProductId()) {
                            oldPriceQty = oldPriceQty + productSell.getQuantity();
                        }
                    }
                }
                if (oldPriceProductList != null && oldPriceProductList.size() > 0) {
                    if (selectedProduct.getOldPriceQuantity() > oldPriceQty) {
                        if (qty > (selectedProduct.getOldPriceQuantity() - oldPriceQty)) {
                            oldPriceSell = new ProductSell(mProductId, productName, selectedProduct.getOldPriceQuantity() - oldPriceQty, selectedProduct.getOldPrice());
                            newPriceSell = new ProductSell(mProductId, productName, qty - selectedProduct.getOldPriceQuantity() - oldPriceQty, price);
                        } else {
                            oldPriceSell = new ProductSell(mProductId, productName, qty, selectedProduct.getOldPrice());
                        }
                    } else {
                        newPriceSell = new ProductSell(mProductId, productName, qty, price);
                    }
                } else {
                    if (selectedProduct.getOldPriceQuantity() > 0) {
                        if (qty > selectedProduct.getOldPriceQuantity()) {
                            oldPriceSell = new ProductSell(mProductId, productName, selectedProduct.getOldPriceQuantity(), selectedProduct.getOldPrice());
                            newPriceSell = new ProductSell(mProductId, productName, qty - selectedProduct.getOldPriceQuantity(), price);
                        } else {
                            oldPriceSell = new ProductSell(mProductId, productName, qty, selectedProduct.getOldPrice());
                        }
                    } else {
                        newPriceSell = new ProductSell(mProductId, productName, qty, price);
                    }
                }
                Intent intent = new Intent(SaleProductActivity.this, MainActivity.class);

                if (oldPriceSell == null) {
                    intent.putExtra(Constant.EXTRA_NEW_PRICE_PRODUCT, newPriceSell);
                } else {
                    if (newPriceSell != null) {
                        intent.putExtra(Constant.EXTRA_OLD_PRICE_PRODUCT, oldPriceSell);
                        intent.putExtra(Constant.EXTRA_NEW_PRICE_PRODUCT, newPriceSell);
                    } else {
                        intent.putExtra(Constant.EXTRA_OLD_PRICE_PRODUCT, oldPriceSell);
                    }
                }
                intent.putExtra(Constant.EXTRA_BUY_PRICE, mBuyPrice);
                setResult(SalesFragment.GET_PRODUCT_REQUEST_CODE, intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProductDialog() {
        AlertDialog.Builder customerDialog = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.customer_dialog, null, false);
        customerDialog.setView(v);
        final RecyclerView customerRecyclerView = v.findViewById(R.id.customer_list_recycler_view);
        TextView titleTextView = v.findViewById(R.id.text_view_list_item);
        titleTextView.setText("Product");
        customerRecyclerView.setHasFixedSize(true);
        customerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final AlertDialog dialog = customerDialog.create();
        mProductRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProductList.clear();
                for (DataSnapshot postData : dataSnapshot.getChildren()) {
                    Product product = postData.getValue(Product.class);
                    if (product.getSellPrice() > 0) {
                        mProductList.add(product);
                    }
                }
                if (mProductList.size() > 0) {
                    Log.e(TAG, "onDataChange: " + mProductList.size());
                    mAdapter = new ProductDialogAdapter(SaleProductActivity.this, mProductList);
                    customerRecyclerView.setAdapter(mAdapter);
                    mAdapter.setRecyclerItemClickListener(new RecyclerItemClickListener() {
                        @Override
                        public void onClick(View view, int position, Object object) {
                            if (object != null) {
                                StockHand stockHand = (StockHand) object;
                                selectedProduct = mProductList.get(position);
                                mHasQuantity = stockHand.getPurchaseQuantity() - stockHand.getSellQuantity();
                                mBinding.productNameTextView.setText(mProductList.get(position).getProductName());
                                mBinding.priceView.setText(String.valueOf(mProductList.get(position).getSellPrice()));
                                mBuyPrice = stockHand.getBuyPrice();
                                mBinding.quantityEditText.setText("1");
                                mProductId = mProductList.get(position).getProductId();
                                mBinding.stockHandTextView.setText("Stock Has " + mHasQuantity);
                                dialog.dismiss();
                            } else {
                                mBinding.productNameTextView.setText(mProductList.get(position).getProductName());
                                mBinding.stockHandTextView.setText("Stock out");
                                dialog.dismiss();
                            }
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dialog.show();

    }
}
