package com.kcirque.stockmanagementfinal.Fragment;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentSellerAddBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class SellerAddFragment extends Fragment {

    private static SellerAddFragment INSTANCE;
    private static final String TAG = "Add Seller";
    private FragmentSellerAddBinding mBinding;
    private String mSellerName;
    private String mSellerEmail;
    private String mSellerPassword;
    private String mSellerMobile;
    private Context mContext;
    private FragmentLoader mFragmentLoader;

    public static synchronized SellerAddFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SellerAddFragment();
        }

        return INSTANCE;
    }

    public SellerAddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_seller_add, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mBinding.addSellerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isValid()) {
                        String password = mBinding.sellerPasswordEditText.getText().toString();
                        String confirmPassword = mBinding.confirmPasswordEditText.getText().toString();
                        if (password.equals(confirmPassword)) {
                            mSellerPassword = password;
                        } else {
                            mBinding.confirmPasswordEditText.setError("Password does not match");
                            mBinding.sellerPasswordEditText.setError("Password does not match");
                            return;
                        }
                        mSellerName = mBinding.sellerNameEditText.getText().toString();
                        mSellerEmail = mBinding.sellerEmailEditText.getText().toString();
                        mSellerMobile = mBinding.sellerMobileEditText.getText().toString();
                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
                        DatabaseReference sellerRef = rootRef.child(Constant.SELLER_REF);
                        String key = sellerRef.push().getKey();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String uId = user.getUid();

                        Seller seller = new Seller(key, mSellerName, mSellerEmail, mSellerPassword, uId, mSellerMobile);

                        sellerRef.child(key).setValue(seller).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(mContext, "Seller Added", Toast.LENGTH_SHORT).show();
                                    mFragmentLoader.loadFragment(SellerFragment.getInstance(), true, Constant.SELLER_FRAGMENT_TAG);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: " + e.getMessage());
                            }
                        });
                    }
                }
            });
        }
    }

    private boolean isValid() {
        if (mBinding.sellerNameEditText.getText().toString().isEmpty()) {
            mBinding.sellerNameEditText.setError(getResources().getString(R.string.name_required));
            return false;
        } else if (mBinding.sellerEmailEditText.getText().toString().isEmpty()) {
            mBinding.sellerEmailEditText.setError(getResources().getString(R.string.email_required_text));
            return false;
        } else if (!mBinding.sellerEmailEditText.getText().toString().contains("@")) {
            mBinding.sellerEmailEditText.setError(getResources().getString(R.string.invalid_email_text));
            return false;
        } else if (mBinding.sellerPasswordEditText.getText().toString().isEmpty()) {
            mBinding.sellerPasswordEditText.setError(getResources().getString(R.string.password_required_text));
            return false;
        } else if (mBinding.confirmPasswordEditText.getText().toString().isEmpty()) {
            mBinding.confirmPasswordEditText.setError(getResources().getString(R.string.password_required_text));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentLoader = (FragmentLoader) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.sellerNameEditText.setText(null);
        mBinding.sellerEmailEditText.setText(null);
        mBinding.confirmPasswordEditText.setText(null);
        mBinding.sellerPasswordEditText.setText(null);
        mBinding.sellerMobileEditText.setText(null);
    }
}
