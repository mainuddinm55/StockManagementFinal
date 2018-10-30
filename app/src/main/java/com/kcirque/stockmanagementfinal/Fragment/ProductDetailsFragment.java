package com.kcirque.stockmanagementfinal.Fragment;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Product;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Database.Model.StockHand;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentProductDetailsBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductDetailsFragment extends Fragment {

    private FragmentProductDetailsBinding mBinding;
    private static ProductDetailsFragment sInstance;
    private Context mContext;

    public ProductDetailsFragment() {
        // Required empty public constructor
    }

    public static ProductDetailsFragment getInstance() {
        if (sInstance == null) {
            sInstance = new ProductDetailsFragment();
        }
        return sInstance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_details, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        SharedPref sharedPref = new SharedPref(mContext);
        Seller seller = sharedPref.getSeller();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        getActivity().setTitle("Stock Hand");
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        DatabaseReference adminRef;
        if (user != null) {
            adminRef = rootRef.child(user.getUid());
        } else {
            adminRef = rootRef.child(seller.getAdminUid());
        }
        DatabaseReference stockHandRef = adminRef.child(Constant.STOCK_HAND_REF);
        if (bundle != null) {

            Product product = (Product) bundle.getSerializable(Constant.EXTRA_PRODUCT);
            getActivity().setTitle(product.getProductName());
            Glide.with(mContext).load(product.getProductImageUrl())
                    .apply(RequestOptions.centerCropTransform())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .apply(RequestOptions.placeholderOf(R.drawable.placeholder))
                    .into(mBinding.productImageImageView);
            mBinding.productNameTextTextView.setText(mContext.getResources().getString(R.string.product_name_text));
            mBinding.productNameTextView.setText(product.getProductName());
            mBinding.companyNameTextTextView.setText(mContext.getResources().getString(R.string.product_company_name_text));
            mBinding.companyNameTextView.setText(product.getCompany());
            mBinding.productCodeTextTextView.setText(mContext.getResources().getString(R.string.product_code_text));
            mBinding.productCodeTextView.setText(product.getProductCode());
            mBinding.buyPriceTextTextView.setText(mContext.getResources().getString(R.string.purchase_buy_price_text));
            mBinding.buyPriceTextView.setText(String.valueOf(product.getBuyPrice()));
            mBinding.sellPriceTextTextView.setText(mContext.getResources().getString(R.string.purchase_sell_price_text));
            mBinding.sellPriceTextView.setText(String.valueOf(product.getSellPrice()));
            mBinding.productDescTextTextView.setText(mContext.getResources().getString(R.string.product_desc_text));
            mBinding.statusTextTextView.setText(mContext.getResources().getString(R.string.status_text));
            if (!product.getDescription().isEmpty()) {
                mBinding.productDescTextView.setText(product.getDescription());
            } else {
                mBinding.productDescTextView.setText("N/A");
            }
            stockHandRef.child(String.valueOf(product.getProductId())).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    StockHand stockHand = dataSnapshot.getValue(StockHand.class);
                    if (stockHand != null && (stockHand.getPurchaseQuantity() - stockHand.getSellQuantity()) > 0) {
                        mBinding.statusTextView.setText("Stock Available");
                    } else {
                        mBinding.statusTextView.setText("Stock Out");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
