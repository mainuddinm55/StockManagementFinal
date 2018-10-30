package com.kcirque.stockmanagementfinal.Fragment;


import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.MainActivity;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentSellerAddBinding;

import java.util.HashMap;

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
    private ProgressDialog progressDialog;
    private String mStatus = "Active";
    private Seller mSeller;

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
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                mSeller = (Seller) bundle.getSerializable(Constant.EXTRA_SELLER);
                getActivity().setTitle("Update Seller");
                mBinding.sellerNameEditText.setText(mSeller.getName());
                mBinding.sellerMobileEditText.setText(mSeller.getMobile());
                mBinding.sellerEmailEditText.setText(mSeller.getEmail());
                mBinding.passwordLayout.setVisibility(View.GONE);
                mBinding.confirmPasswordLayout.setVisibility(View.GONE);
                mBinding.statusRadioGroup.check(mSeller.getStatus().equals("Active") ? R.id.active_radio_btn : R.id.deactivate_radio_btn);
                mBinding.addSellerBtn.setText("Update");
            } else {
                getActivity().setTitle("Add a Seller");
            }
            mBinding.statusRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton = view.findViewById(checkedId);
                    mStatus = radioButton.getText().toString();
                }
            });
            mBinding.addSellerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSeller != null && mSeller.getKey() != null) {
                        updateSeller();
                    } else {
                        addNewSeller();
                    }
                }
            });
        }
    }

    private void updateSeller() {
        if (mBinding.sellerNameEditText.getText().toString().isEmpty()) {
            mBinding.sellerNameEditText.setError(getResources().getString(R.string.name_required));
            mBinding.sellerNameEditText.requestFocus();
            return;
        }
        if (mBinding.sellerEmailEditText.getText().toString().isEmpty()) {
            mBinding.sellerEmailEditText.setError(getResources().getString(R.string.name_required));
            mBinding.sellerEmailEditText.requestFocus();
            return;
        }
        if (!mBinding.sellerEmailEditText.getText().toString().contains("@")) {
            mBinding.sellerEmailEditText.setError(getResources().getString(R.string.invalid_email_text));
            mBinding.sellerEmailEditText.requestFocus();
            return;
        }

        showProgressDialog();
        mSellerName = mBinding.sellerNameEditText.getText().toString();
        mSellerEmail = mBinding.sellerEmailEditText.getText().toString();
        mSellerMobile = mBinding.sellerMobileEditText.getText().toString();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", mSellerName);
        hashMap.put("email", mSellerEmail);
        hashMap.put("mobile", mSellerMobile);
        hashMap.put("status", mStatus);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        DatabaseReference sellerRef = rootRef.child(Constant.SELLER_REF);
        if (MainActivity.isNetworkAvailable(mContext)) {
            sellerRef.child(mSeller.getKey()).updateChildren(hashMap,
                    new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            dismissProgressDialog();
                            Toast.makeText(mContext, "Seller Updated", Toast.LENGTH_SHORT).show();
                            mFragmentLoader.loadFragment(SellerFragment.getInstance(), true, Constant.SELLER_FRAGMENT_TAG);
                        }
                    }
            );
        } else {
            dismissProgressDialog();
            Snackbar.make(mBinding.rootView, "No internet connection", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void addNewSeller() {
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
            showProgressDialog();
            mSellerName = mBinding.sellerNameEditText.getText().toString();
            mSellerEmail = mBinding.sellerEmailEditText.getText().toString();
            mSellerMobile = mBinding.sellerMobileEditText.getText().toString();
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
            DatabaseReference sellerRef = rootRef.child(Constant.SELLER_REF);
            String key = sellerRef.push().getKey();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uId = user.getUid();

            Seller seller = new Seller(key, mSellerName, mSellerEmail, mSellerPassword, uId, mSellerMobile, mStatus);

            if (MainActivity.isNetworkAvailable(mContext)) {
                sellerRef.child(key).setValue(seller).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dismissProgressDialog();
                            Toast.makeText(mContext, "Seller Added", Toast.LENGTH_SHORT).show();
                            mFragmentLoader.loadFragment(SellerFragment.getInstance(), true, Constant.SELLER_FRAGMENT_TAG);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dismissProgressDialog();
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                });
            }
        } else {
            dismissProgressDialog();
            Snackbar.make(mBinding.rootView, "No internet connection", Snackbar.LENGTH_SHORT).show();
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
    public void onPause() {
        super.onPause();
        mBinding.sellerNameEditText.setText(null);
        mBinding.sellerEmailEditText.setText(null);
        mBinding.confirmPasswordEditText.setText(null);
        mBinding.sellerPasswordEditText.setText(null);
        mBinding.sellerMobileEditText.setText(null);
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Loading.....");
        progressDialog.setMessage("Please wait.....");
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }
}
