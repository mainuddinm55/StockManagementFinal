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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.kcirque.stockmanagementfinal.Adapter.DueDetailsAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Customer;
import com.kcirque.stockmanagementfinal.Database.Model.Sales;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.Activity.MainActivity;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentDueDetailsBinding;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DueDetailsFragment extends Fragment {
    private static final String TAG = "DueDetailsFragment";
    private FragmentDueDetailsBinding mBinding;
    private static DueDetailsFragment sInstance;
    private Context mContext;
    private List<Sales> mSalesList = new ArrayList<>();
    private double mPayAmount;
    private DatabaseReference mSalesRef;
    private Customer dueCustomer;
    private DatabaseReference mCustomerRef;
    private ProgressDialog progressDialog;

    private FragmentLoader mFragmentLoader;

    public DueDetailsFragment() {
        // Required empty public constructor
    }

    public static synchronized DueDetailsFragment getInstance() {
        if (sInstance == null) {
            sInstance = new DueDetailsFragment();
        }
        return sInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_due_details, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SharedPref sharedPref = new SharedPref(mContext);
        Seller seller = sharedPref.getSeller();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        DatabaseReference adminRef;
        if (user != null) {
            adminRef = rootRef.child(user.getUid());
        } else {
            adminRef = rootRef.child(seller.getAdminUid());
        }
        mSalesRef = adminRef.child(Constant.SALES_REF);
        mCustomerRef = adminRef.child(Constant.CUSTOMER_REF);
        Bundle bundle = getArguments();


        if (bundle != null) {
            dueCustomer = (Customer) bundle.getSerializable(Constant.EXTRA_CUSTOMER);
            getActivity().setTitle(dueCustomer.getCustomerName());
            mBinding.dueListRecyclerView.setHasFixedSize(true);
            mBinding.dueListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mBinding.customerNameTextTextView.setText(mContext.getResources().getString(R.string.name_text));
            mBinding.customerNameTextView.setText(dueCustomer.getCustomerName());
            mBinding.customerAddressTextTextView.setText(mContext.getResources().getString(R.string.customer_address_text));
            mBinding.customerAddressTextView.setText(dueCustomer.getAddress());
            mBinding.customerMobileTextTextView.setText(mContext.getResources().getString(R.string.customer_mobile_text));
            mBinding.customerMobileTextView.setText(dueCustomer.getMobile());
            mBinding.emailTextTextView.setText(mContext.getResources().getString(R.string.email_text));
            mBinding.emailTextView.setText(dueCustomer.getEmail());
            mBinding.totalDueTextTextView.setText(mContext.getResources().getString(R.string.total_due_text));
            mBinding.totalDueTextView.setText(String.valueOf(dueCustomer.getDue()));

            if (MainActivity.isNetworkAvailable(mContext)) {
                mSalesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mSalesList.clear();
                        for (DataSnapshot postData : dataSnapshot.getChildren()) {
                            Sales sales = postData.getValue(Sales.class);
                            if (dueCustomer.getCustomerId() == sales.getCustomerId() && sales.getDue() > 0) {
                                mSalesList.add(sales);
                            }
                        }

                        if (mSalesList.size() > 0) {
                            DueDetailsAdapter adapter = new DueDetailsAdapter(mContext, mSalesList);
                            mBinding.dueListRecyclerView.setAdapter(adapter);
                            adapter.setItemClickListener(new RecyclerItemClickListener() {
                                @Override
                                public void onClick(View view, int position, Object object) {
                                    Sales sales = (Sales) object;
                                    Bundle salesBundle = new Bundle();
                                    salesBundle.putSerializable(Constant.EXTRA_SALES, sales);
                                    SalesDetailsFragment fragment = new SalesDetailsFragment();
                                    fragment.setArguments(salesBundle);
                                    mFragmentLoader.loadFragment(fragment, true, Constant.SALES_DETAILS_FRAGMENT_TAG);
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(mContext, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Snackbar.make(mBinding.rootView, "No internet connection", Snackbar.LENGTH_SHORT).show();
            }
        }
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
                    if (dueCustomer.getDue() < mPayAmount) {
                        Snackbar.make(mBinding.rootView, "Please enter a amount equal or below due", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    paymentDueAmount();
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

    private void paymentDueAmount() {
        showProgressDialog();
        double mDue = dueCustomer.getDue() - mPayAmount;
        for (Sales sales : mSalesList) {
            if (mPayAmount > 0) {
                if (sales.getDue() <= mPayAmount) {
                    mPayAmount = mPayAmount - sales.getDue();
                    double paid = sales.getPaid() + sales.getDue();
                    double due = sales.getTotal() - paid;
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("paid", paid);
                    hashMap.put("due", due);

                    mSalesRef.child(sales.getKey()).updateChildren(hashMap);

                } else if (sales.getDue() > mPayAmount) {
                    double paid = sales.getPaid() + mPayAmount;
                    double due = sales.getTotal() - paid;
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("paid", paid);
                    hashMap.put("due", due);

                    mSalesRef.child(sales.getKey()).updateChildren(hashMap);
                    mPayAmount = 0;
                }
            }
        }
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("due", mDue);
        mCustomerRef.child(dueCustomer.getKey()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dismissProgressDialog();
                    Snackbar.make(mBinding.rootView, "Due paid", Snackbar.LENGTH_SHORT).show();
                    mFragmentLoader.loadFragment(new DueFragment(), true, Constant.DUE_FRAGMENT_TAG);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dismissProgressDialog();
                Log.e(TAG, "onFailure: " + e.getMessage());
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentLoader = (FragmentLoader) context;
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
