package com.kcirque.stockmanagementfinal.Fragment;


import android.app.DatePickerDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.kcirque.stockmanagementfinal.Adapter.ExpenseListAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.DateConverter;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Expense;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentExpenseBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExpenseFragment extends Fragment {

    private Context mContext;
    private FragmentExpenseBinding mBinding;
    private static ExpenseFragment INSTANCE;
    private List<Expense> mExpenseList = new ArrayList<>();
    private long mExpenseDate;
    private DatabaseReference mRootRef;
    private DateConverter mDateConverter;
    Calendar calendar = Calendar.getInstance();
    int mYear = calendar.get(Calendar.YEAR);
    int mMonth = calendar.get(Calendar.MONTH);
    int mDay = calendar.get(Calendar.DAY_OF_MONTH);

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private SharedPref mSharedPref;
    private DatabaseReference mAdminRef;

    public ExpenseFragment() {
        // Required empty public constructor
    }

    public static synchronized ExpenseFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExpenseFragment();
        }

        return INSTANCE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_expense, container, false);
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
                for (DataSnapshot postData : dataSnapshot.getChildren()){
                    Expense expense = postData.getValue(Expense.class);
                    mExpenseList.add(expense);
                }
                if (mExpenseList.size()>0){
                    mBinding.progressBar.setVisibility(View.GONE);
                    ExpenseListAdapter adapter = new ExpenseListAdapter(mContext,mExpenseList);
                    mBinding.expenseListRecyclerView.setAdapter(adapter);
                } else {
                    mBinding.emptyExpenseTextView.setVisibility(View.VISIBLE);
                    mBinding.progressBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mBinding.addExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newExpenseDialog();
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void newExpenseDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext()).inflate(R.layout.add_new_expense, null);
        dialog.setView(view);
        dialog.setCancelable(false);
        final EditText expenseNameET = view.findViewById(R.id.expenseNameET);
        final EditText expenseAmountET = view.findViewById(R.id.expenseAmountET);
        final EditText expenseCommentET = view.findViewById(R.id.expenseCommentET);
        final TextView expenseDateET = view.findViewById(R.id.expenseDateTV);
        Button saveBtn = view.findViewById(R.id.saveBtn);
        Button cancelBtn = view.findViewById(R.id.cancelBtn);
        final AlertDialog alertDialog = dialog.create();
        expenseDateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date;
                        if (month < 9) {
                            date = dayOfMonth + "/" + "0" + (month + 1) + "/" + year;
                        } else {
                            date = dayOfMonth + "/" + (month + 1) + "/" + year;
                        }
                        mExpenseDate = mDateConverter.getDateInUnix(date);
                        expenseDateET.setText(date);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expenseNameET.getText().toString().isEmpty()) {
                    expenseNameET.setError("Expense Name Required");
                    expenseNameET.requestFocus();
                    return;
                }
                if (expenseAmountET.getText().toString().isEmpty()) {
                    expenseAmountET.setError("Amount Required");
                    expenseAmountET.requestFocus();
                    return;
                }
                if (expenseDateET.getText().toString().isEmpty()) {
                    expenseDateET.setError("Date Required");
                    expenseDateET.requestFocus();
                    return;
                }

                String expenseName = expenseNameET.getText().toString().trim();
                double expenseAmount = Double.parseDouble(expenseAmountET.getText().toString().trim());
                String comment = expenseCommentET.getText().toString().trim();
                DatabaseReference expenseRef = mAdminRef.child(Constant.EXPENSE_REF);
                String expenseKey = expenseRef.push().getKey();

                Expense expense = new Expense(expenseKey, expenseName, expenseAmount, comment, mExpenseDate);

                expenseRef.child(expenseKey).setValue(expense).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, "Expense Added", Toast.LENGTH_SHORT).show();
                            mBinding.emptyExpenseTextView.setVisibility(View.GONE);
                            alertDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                });

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private Context mContext() {
        return mContext;
    }
}
