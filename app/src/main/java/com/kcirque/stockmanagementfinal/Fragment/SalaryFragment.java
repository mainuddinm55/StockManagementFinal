package com.kcirque.stockmanagementfinal.Fragment;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Adapter.SalaryAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Salary;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.Activity.MainActivity;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentSalaryBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalaryFragment extends Fragment {

    public static SalaryFragment sInstance;
    private FragmentSalaryBinding mBinding;
    private String[] months = {"Month", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private Context mContext;
    private String mMonth;
    private List<Salary> mSalaryList = new ArrayList<>();
    private SalaryAdapter mAdapter;
    private FragmentLoader mFragmentLoader;

    public SalaryFragment() {
        // Required empty public constructor
    }

    public static SalaryFragment getInstance() {
        if (sInstance == null) {
            sInstance = new SalaryFragment();
        }
        return sInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_salary, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(mContext);
        Seller seller = sharedPref.getSeller();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        getActivity().setTitle("Salary");
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        DatabaseReference adminRef;
        if (user != null) {
            adminRef = rootRef.child(user.getUid());
        } else {
            adminRef = rootRef.child(seller.getAdminUid());
        }
        DatabaseReference salaryRef = adminRef.child(Constant.SALARY_REF);
        mAdapter = new SalaryAdapter(mContext, mSalaryList);
        mBinding.progressBar.setVisibility(View.VISIBLE);
        mBinding.salaryListRecyclerView.setHasFixedSize(true);
        mBinding.salaryListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mBinding.salaryListRecyclerView.setAdapter(mAdapter);
        if (MainActivity.isNetworkAvailable(mContext)) {
            salaryRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mSalaryList.clear();
                    for (DataSnapshot postData : dataSnapshot.getChildren()) {
                        Salary salary = postData.getValue(Salary.class);
                        mSalaryList.add(salary);
                    }
                    if (mSalaryList.size() > 0) {
                        mBinding.progressBar.setVisibility(View.GONE);
                        mBinding.emptySalaryTextView.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mBinding.progressBar.setVisibility(View.GONE);
                        mBinding.emptySalaryTextView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            mBinding.progressBar.setVisibility(View.GONE);
            Snackbar.make(mBinding.rootView, "No internet connection", Snackbar.LENGTH_SHORT).show();
        }

        mAdapter.setItemClickListener(new RecyclerItemClickListener() {
            @Override
            public void onClick(View view, int position, Object object) {
                Salary salary = (Salary) object;
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.EXTRA_SALARY, salary);
                SalaryDetailsFragment fragment = new SalaryDetailsFragment();
                fragment.setArguments(bundle);
                mFragmentLoader.loadFragment(fragment, true, Constant.SALARY_DETAILS_FRAGMENT_TAG);
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, months);
        mBinding.monthSpinner.setAdapter(adapter);
        mBinding.monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMonth = (String) parent.getItemAtPosition(position);
                if (mSalaryList.size() > 0) {
                    mAdapter.getFilter().filter(mMonth);
                } else {
                    mBinding.progressBar.setVisibility(View.GONE);
                    mBinding.emptySalaryTextView.setVisibility(View.VISIBLE);
                }

                if (mBinding.monthSpinner.getSelectedView() != null) {
                    ((TextView) mBinding.monthSpinner.getSelectedView()).setTextColor(getResources().getColor(android.R.color.white));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mBinding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentLoader.loadFragment(SalaryAddFragment.getInstance(), true, Constant.SALARY_ADD_FRAGMENT_TAG);
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
