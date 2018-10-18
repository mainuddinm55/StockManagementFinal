package com.kcirque.stockmanagementfinal.Fragment;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Adapter.DailyExpenseAdapter;
import com.kcirque.stockmanagementfinal.Adapter.ExpenseListAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.DateConverter;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Expense;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.MainActivity;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentDailyCostBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DailyCostFragment extends Fragment {
    private FragmentDailyCostBinding mBinding;
    public static DailyCostFragment INSTANCE;
    private List<Expense> mExpenseList = new ArrayList<>();
    private DatabaseReference mRootRef;
    private DateConverter mDateConverter;

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private SharedPref mSharedPref;
    private DatabaseReference mAdminRef;
    private double mTotalExpense = 0;

    private Context mContext;
    private FragmentLoader mFragmentLoader;


    public DailyCostFragment() {
        // Required empty public constructor
    }

    public static synchronized DailyCostFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DailyCostFragment();
        }
        return INSTANCE;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_daily_cost, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mSharedPref = new SharedPref(mContext);
        Seller seller = mSharedPref.getSeller();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mBinding.progressBar.setVisibility(View.VISIBLE);
        mBinding.expenseListRecyclerView.setHasFixedSize(true);
        mBinding.expenseListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mDateConverter = new DateConverter();
        mRootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        if (mUser != null) {
            mAdminRef = mRootRef.child(mUser.getUid());
        } else {
            mAdminRef = mRootRef.child(seller.getAdminUid());
        }
        DatabaseReference expenseRef = mAdminRef.child(Constant.EXPENSE_REF);
        expenseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mExpenseList.clear();
                mTotalExpense = 0;
                for (DataSnapshot postData : dataSnapshot.getChildren()) {
                    Expense expense = postData.getValue(Expense.class);
                    if (mDateConverter.isToday(expense.getDate())) {
                        mTotalExpense = mTotalExpense + expense.getExpenseAmount();
                        mExpenseList.add(expense);
                    }
                }
                if (mExpenseList.size() > 0) {
                    mBinding.progressBar.setVisibility(View.GONE);
                    DailyExpenseAdapter adapter = new DailyExpenseAdapter(mContext, mExpenseList);
                    mBinding.expenseListRecyclerView.setAdapter(adapter);
                    mBinding.totalLinearLayout.setVisibility(View.VISIBLE);
                    mBinding.totalAmountTextView.setText(String.valueOf(mTotalExpense));
                    mBinding.totalTextTextView.setText("Total Amount");
                    adapter.setRecyclerItemClickListener(new RecyclerItemClickListener() {
                        @Override
                        public void onClick(View view, int position, Object object) {
                            Expense expense = (Expense) object;
                            ExpenseDetailsFragment fragment = ExpenseDetailsFragment.getInstance();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Constant.EXTRA_EXPENSE, expense);
                            fragment.setArguments(bundle);
                            mFragmentLoader.loadFragment(fragment,true,Constant.EXPENSE_DETAILS_FRAGMENT_TAG);

                        }
                    });
                } else {
                    mBinding.emptyExpenseTextView.setVisibility(View.VISIBLE);
                    mBinding.progressBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentLoader = (FragmentLoader) new MainActivity();
    }
}
