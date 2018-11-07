package com.kcirque.stockmanagementfinal.Fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
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
import com.kcirque.stockmanagementfinal.Activity.MainActivity;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Database.Model.Supplier;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentSupplierAddBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class SupplierAddFragment extends Fragment {
    private FragmentSupplierAddBinding mBinding;
    private ProgressDialog mSpinner;
    private Context mContext;
    private FragmentLoader mFragmentLoader;

    public SupplierAddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_supplier_add, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Add Supplier");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        if (user != null) {
            DatabaseReference adminRef = rootRef.child(user.getUid());
            final DatabaseReference supplierRef = adminRef.child(Constant.SUPPLIER_REF);

            mBinding.addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isValid()) {
                        String name = mBinding.nameEditText.getText().toString();
                        String address = mBinding.addressEditText.getText().toString();
                        String email = mBinding.emailEditText.getText().toString();
                        String mobile = mBinding.mobileEditText.getText().toString();

                        if (MainActivity.isNetworkAvailable(mContext)) {
                            showSpinner();
                            String key = supplierRef.push().getKey();
                            Supplier supplier = new Supplier(key, name, address, email, mobile);
                            supplierRef.child(key).setValue(supplier).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        dismissSpinner();
                                        Snackbar.make(mBinding.rootView, "Supplier Added", Snackbar.LENGTH_SHORT).show();
                                        mFragmentLoader.loadFragment(new SupplierFragment(), true, Constant.SUPPLIER_FRAGMENT_TAG);
                                    } else {
                                        dismissSpinner();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dismissSpinner();
                                    Snackbar.make(mBinding.rootView, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Snackbar.make(mBinding.rootView, "No internet connection", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private boolean isValid() {
        if (TextUtils.isEmpty(mBinding.nameEditText.getText())) {
            mBinding.nameEditText.setError("Name required");
            mBinding.nameEditText.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(mBinding.mobileEditText.getText())) {
            mBinding.mobileEditText.setError("Mobile required");
            mBinding.mobileEditText.requestFocus();
            return false;
        } else if (!TextUtils.isEmpty(mBinding.emailEditText.getText()) && !android.util.Patterns.EMAIL_ADDRESS.matcher(mBinding.emailEditText.getText()).matches()) {
            mBinding.emailEditText.setError("Email invalid");
            mBinding.emailEditText.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(mContext);
        mSpinner.setTitle("Loading....");
        mSpinner.setMessage("Please wait....");
        mSpinner.show();
    }

    private void dismissSpinner() {
        if (mSpinner != null)
            mSpinner.dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentLoader = (FragmentLoader) context;
    }
}
