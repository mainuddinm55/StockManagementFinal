package com.kcirque.stockmanagementfinal.Fragment;


import android.app.DatePickerDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Adapter.SellerSpinnerAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.DateConverter;
import com.kcirque.stockmanagementfinal.Database.Model.Salary;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentSalaryAddBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalaryAddFragment extends Fragment {

    public static SalaryAddFragment sInstance;
    private FragmentSalaryAddBinding mBinding;
    private Context mContext;
    private FragmentLoader mFragmentLoader;
    private String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private List<Salary> mSalaryList = new ArrayList<>();
    private List<Seller> mSellerList = new ArrayList<>();
    private String mMonth;
    private String mSellerKey;
    private String mSellerName;
    private long mDate;
    private DateConverter mDateConverter;

    public SalaryAddFragment() {
        // Required empty public constructor
    }

    public static synchronized SalaryAddFragment getInstance() {
        if (sInstance == null) {
            sInstance = new SalaryAddFragment();
        }
        return sInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_salary_add, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mDateConverter = new DateConverter();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        getActivity().setTitle("Employee Salary");
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        DatabaseReference adminRef;
        rootRef.keepSynced(true);
        adminRef = rootRef.child(user.getUid());
        final DatabaseReference salaryRef = adminRef.child(Constant.SALARY_REF);
        DatabaseReference sellerRef = rootRef.child(Constant.SELLER_REF);
        salaryRef.keepSynced(true);
        mDate = mDateConverter.getCurrentDate();
        mBinding.dateTextView.setText(mDateConverter.getDateInString(mDateConverter.getCurrentDate()));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, months);
        mBinding.monthSpinner.setAdapter(adapter);
        sellerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mSellerList.clear();
                for (DataSnapshot postData : dataSnapshot.getChildren()) {
                    Seller sellerPerson = postData.getValue(Seller.class);
                    if (sellerPerson != null && sellerPerson.getAdminUid().equals(user.getUid())) {
                        mSellerList.add(sellerPerson);
                    }

                }
                if (mSellerList.size() > 0) {
                    SellerSpinnerAdapter adapter = new SellerSpinnerAdapter(mContext, mSellerList);
                    mBinding.sellerNameSpinner.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        salaryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postData : dataSnapshot.getChildren()) {
                    Salary salary = postData.getValue(Salary.class);
                    if (salary != null) {
                        mSalaryList.add(salary);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mBinding.monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMonth = months[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mBinding.sellerNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSellerKey = mSellerList.get(position).getKey();
                mSellerName = mSellerList.get(position).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mBinding.dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date;
                        if (month < 9) {
                            if (dayOfMonth < 9) {
                                date = "0" + dayOfMonth + "/" + "0" + (month + 1) + "/" + year;
                            } else {
                                date = dayOfMonth + "/" + "0" + (month + 1) + "/" + year;
                            }
                        } else {
                            if (dayOfMonth < 9) {
                                date = "0" + dayOfMonth + "/" + (month + 1) + "/" + year;
                            } else {
                                date = dayOfMonth + "/" + (month + 1) + "/" + year;
                            }

                        }
                        mDate = mDateConverter.getDateInUnix(date);
                        mBinding.dateTextView.setText(date);
                    }
                }, mDateConverter.getYear(), mDateConverter.getMonth(), mDateConverter.getDay());

                datePickerDialog.show();
            }
        });

        mBinding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                for (Salary salary : mSalaryList) {
                    if (salary.getEmpKey().equals(mSellerKey) && salary.getMonth().equals(mMonth)) {
                        Snackbar.make(v, "Salary Already added", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (mBinding.amountEditText.getText().toString().isEmpty() || Double.parseDouble(mBinding.amountEditText.getText().toString()) <= 0) {
                    mBinding.amountEditText.setError("Salary Amount Required");
                    mBinding.amountEditText.requestFocus();
                    return;
                }
                String key = salaryRef.push().getKey();
                double amount = Double.parseDouble(mBinding.amountEditText.getText().toString());
                Salary empSalary = new Salary(key, mSellerKey, mSellerName, mMonth, amount, mDate);
                salaryRef.child(key).setValue(empSalary).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(v, "Salary Added", Snackbar.LENGTH_SHORT).show();
                            mFragmentLoader.loadFragment(SalaryFragment.getInstance(), true, Constant.SALARY_FRAGMENT_TAG);
                        }
                    }
                });
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
