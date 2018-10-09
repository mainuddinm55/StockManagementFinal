package com.kcirque.stockmanagementfinal.Fragment;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Adapter.ProductListAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Product;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductListFragment extends Fragment {

    private static ProductListFragment INSTANCE;

    private RecyclerView mProductListRecyclerView;
    private FloatingActionButton mAddProductFab;
    private TextView mEmptyProductTextView;
    private ProgressBar mProgressBar;

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private SharedPref mSharedPref;
    private DatabaseReference mAdminRef;
    private DatabaseReference mRootRef;
    private DatabaseReference mProductRef;

    private List<Product> mProductList = new ArrayList<>();
    private Context mContext;
    private FragmentLoader mFragmentLoader;

    public static synchronized ProductListFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProductListFragment();
        }

        return INSTANCE;
    }

    public ProductListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mSharedPref = new SharedPref(mContext);
        Seller seller = mSharedPref.getSeller();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mProductListRecyclerView = view.findViewById(R.id.product_list_recycler_view);
        mAddProductFab = view.findViewById(R.id.add_product_fab);
        mEmptyProductTextView = view.findViewById(R.id.empty_product_textview);
        mProgressBar = view.findViewById(R.id.progress_bar);

        mProductListRecyclerView.setHasFixedSize(true);
        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mProductListRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        }
        else{
            mProductListRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        }

        mRootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        mRootRef.keepSynced(true);
        if (mUser != null) {
            mAdminRef = mRootRef.child(mUser.getUid());
        } else {
            mAdminRef = mRootRef.child(seller.getAdminUid());
        }
        mProductRef = mAdminRef.child(Constant.PRODUCT_REF);
        mProductRef.keepSynced(true);
        mProgressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mProductRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mProductList.clear();
                        for (DataSnapshot postData : dataSnapshot.getChildren()) {
                            Product product = postData.getValue(Product.class);
                            mProductList.add(product);
                        }
                        if (mProductList.size() > 0) {
                            mEmptyProductTextView.setVisibility(View.GONE);
                            mProgressBar.setVisibility(View.GONE);
                            ProductListAdapter adapter = new ProductListAdapter(mContext, mProductList);
                            mProductListRecyclerView.setAdapter(adapter);
                        } else {
                            mProgressBar.setVisibility(View.GONE);
                            mEmptyProductTextView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }).start();

        mAddProductFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentLoader.loadFragment(ProductAddFragment.getInstance(), true,Constant.PRODUCT_ADD_FRAGMENT_TAG);
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
