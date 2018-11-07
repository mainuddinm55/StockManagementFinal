package com.kcirque.stockmanagementfinal.Fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Activity.MainActivity;
import com.kcirque.stockmanagementfinal.Adapter.SupplierPurchaseAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Database.Model.Purchase;
import com.kcirque.stockmanagementfinal.Database.Model.Sales;
import com.kcirque.stockmanagementfinal.Database.Model.Supplier;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentSupplierDetailsBinding;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SupplierDetailsFragment extends Fragment {
    private static final String TAG = "SupplierDetailsFragment";
    private FragmentSupplierDetailsBinding mBinding;
    private Bundle mBundle;
    private List<Purchase> mPurchaseList = new ArrayList<>();
    private DatabaseReference mPurchaseRef;
    private Supplier mSupplier;
    private SupplierPurchaseAdapter mAdapter;
    private Context mContext;
    private FragmentLoader mFragmentLoader;
    private double mTotalPayable = 0;
    private double mPayAmount;
    private ProgressDialog progressDialog;

    public SupplierDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_supplier_details, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onViewCreated: ");
        mBundle = getArguments();
        if (mBundle != null) {
            mSupplier = (Supplier) mBundle.getSerializable(Constant.EXTRA_SUPPLIER);
            getActivity().setTitle(mSupplier.getName());
            mBinding.nameTextView.setText(mSupplier.getName());
            mBinding.mobileTextView.setText(mSupplier.getMobile());
            mBinding.emailTextView.setText(mSupplier.getEmail());
            mBinding.addressTextView.setText(mSupplier.getAddress());

            mBinding.progressBar.setVisibility(View.VISIBLE);
            mBinding.supplierPurchaseRecyclerView.setHasFixedSize(true);
            mBinding.supplierPurchaseRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mAdapter = new SupplierPurchaseAdapter();
            mBinding.supplierPurchaseRecyclerView.setAdapter(mAdapter);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
                DatabaseReference adminRef = rootRef.child(user.getUid());
                mPurchaseRef = adminRef.child(Constant.PURCHASE_REF);
                loadPurchase();
            }

            mBinding.purchaseFragmentLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBinding.purchaseFragmentLoad.setBackgroundColor(getResources().getColor(R.color.gray));
                    mBinding.dueFragmentLoad.setBackgroundColor(getResources().getColor(R.color.silverGray));
                    loadPurchase();
                }
            });

            mBinding.dueFragmentLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBinding.dueFragmentLoad.setBackgroundColor(getResources().getColor(R.color.gray));
                    mBinding.purchaseFragmentLoad.setBackgroundColor(getResources().getColor(R.color.silverGray));
                    loadDuePurchase();
                }
            });

            mAdapter.setItemClickListener(new RecyclerItemClickListener() {
                @Override
                public void onClick(View view, int position, Object object) {
                    SupplierPurchaseDetailsFragment fragment = new SupplierPurchaseDetailsFragment();
                    Bundle purchaseBundle = new Bundle();
                    Purchase purchase = (Purchase) object;
                    purchaseBundle.putSerializable(Constant.EXTRA_PURCHASE, purchase);
                    fragment.setArguments(purchaseBundle);
                    mFragmentLoader.loadFragment(fragment, true, Constant.SUPPLIER_PURCHASE_DETAILS_FRAGMENT_TAG);
                }
            });

        }
    }

    private void loadPurchase() {
        mPurchaseRef.orderByChild("supplierKey").equalTo(mSupplier.getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPurchaseList.clear();
                mTotalPayable = 0;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Purchase purchase = data.getValue(Purchase.class);
                    mPurchaseList.add(purchase);
                }
                if (mPurchaseList.size() > 0) {
                    mBinding.progressBar.setVisibility(View.GONE);
                    mAdapter.setPurchaseList(mPurchaseList);
                    for (Purchase purchase : mPurchaseList) {
                        mTotalPayable = mTotalPayable + purchase.getDueAmount();
                    }
                    mBinding.payableTextView.setText(String.valueOf(mTotalPayable));
                } else {
                    mBinding.progressBar.setVisibility(View.GONE);
                    mBinding.emptyPurchaseTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mBinding.progressBar.setVisibility(View.GONE);
                mBinding.emptyPurchaseTextView.setVisibility(View.VISIBLE);
                mBinding.emptyPurchaseTextView.setText(databaseError.getMessage());
            }
        });
    }

    private void loadDuePurchase() {
        mPurchaseRef.orderByChild("supplierKey").equalTo(mSupplier.getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPurchaseList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Purchase purchase = data.getValue(Purchase.class);
                    if (purchase.getDueAmount() > 0) {
                        mPurchaseList.add(purchase);
                    }
                }
                if (mPurchaseList.size() > 0) {
                    mBinding.progressBar.setVisibility(View.GONE);
                    mAdapter.setPurchaseList(mPurchaseList);
                } else {
                    mBinding.progressBar.setVisibility(View.GONE);
                    mBinding.emptyPurchaseTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mBinding.progressBar.setVisibility(View.GONE);
                mBinding.emptyPurchaseTextView.setVisibility(View.VISIBLE);
                mBinding.emptyPurchaseTextView.setText(databaseError.getMessage());
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentLoader = (FragmentLoader) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.due_payment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.payment_due_amount:
                showPaymentDialog();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    private void showPaymentDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("PAYMENT DUE AMOUNT");
        //dialog.setMessage("Change Username");
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View payView = inflater.inflate(R.layout.set_reminder_layout, null);
        final MaterialEditText payAmountEditText = payView.findViewById(R.id.reminder_count_edit_text);
        payAmountEditText.setHint("Enter amount");
        payAmountEditText.setFloatingLabelText("Amount");
        dialog.setView(payView);
        dialog.setPositiveButton("PAY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
                if (TextUtils.isEmpty(payAmountEditText.getText().toString())) {
                    Snackbar.make(mBinding.rootView, "Please enter a amount", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                mPayAmount = Double.parseDouble(payAmountEditText.getText().toString());
                if (MainActivity.isNetworkAvailable(mContext)) {
                    if (mTotalPayable < mPayAmount) {
                        Snackbar.make(mBinding.rootView, "Please enter a amount equal or below due", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    paidAmount();
                } else {
                    dialog.dismiss();
                    Snackbar.make(mBinding.rootView, "No internet connection", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void paidAmount() {
        showProgressDialog();
        for (Purchase purchase : mPurchaseList) {
            if (mPayAmount > 0) {
                if (purchase.getDueAmount() <= mPayAmount) {
                    mPayAmount = mPayAmount - purchase.getDueAmount();
                    double paid = purchase.getPaidAmount() + purchase.getDueAmount();
                    double due = purchase.getTotalPrice() - paid;
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("paidAmount", paid);
                    hashMap.put("dueAmount", due);

                    mPurchaseRef.child(purchase.getKey()).updateChildren(hashMap);

                } else if (purchase.getDueAmount() > mPayAmount) {
                    double paid = purchase.getPaidAmount() + mPayAmount;
                    double due = purchase.getTotalPrice() - paid;
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("paidAmount", paid);
                    hashMap.put("dueAmount", due);

                    mPurchaseRef.child(purchase.getKey()).updateChildren(hashMap);
                    mPayAmount = 0;
                }
            }
        }
        dismissProgressDialog();
        Toast.makeText(mContext, "Amount Paid", Toast.LENGTH_SHORT).show();
        mFragmentLoader.loadFragment(new SupplierFragment(), true, Constant.SUPPLIER_FRAGMENT_TAG);

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
