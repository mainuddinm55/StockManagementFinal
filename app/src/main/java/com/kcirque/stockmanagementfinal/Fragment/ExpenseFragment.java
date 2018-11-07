package com.kcirque.stockmanagementfinal.Fragment;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.kcirque.stockmanagementfinal.Database.Model.DateAmountCost;
import com.kcirque.stockmanagementfinal.Database.Model.Expense;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.Activity.MainActivity;
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
    private FragmentLoader mFragmentLoader;

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
    private ProgressDialog progressDialog;

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
        getActivity().setTitle("Expenses");

        if (MainActivity.isNetworkAvailable(mContext)) {
            mBinding.progressBar.setVisibility(View.VISIBLE);
            mBinding.expenseListRecyclerView.setHasFixedSize(true);
            mBinding.expenseListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mDateConverter = new DateConverter();
            mExpenseDate = mDateConverter.getCurrentDate();
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
                    for (DataSnapshot postData : dataSnapshot.getChildren()) {
                        Expense expense = postData.getValue(Expense.class);
                        mExpenseList.add(expense);
                    }
                    if (mExpenseList.size() > 0) {
                        mBinding.progressBar.setVisibility(View.GONE);
                        ExpenseListAdapter adapter = new ExpenseListAdapter(mContext, mExpenseList);
                        mBinding.expenseListRecyclerView.setAdapter(adapter);
                        adapter.setRecyclerItemClickListener(new RecyclerItemClickListener() {
                            @Override
                            public void onClick(View view, int position, Object object) {
                                Expense expense = (Expense) object;
                                ExpenseDetailsFragment fragment = ExpenseDetailsFragment.getInstance();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constant.EXTRA_EXPENSE, expense);
                                fragment.setArguments(bundle);
                                mFragmentLoader.loadFragment(fragment, true, Constant.EXPENSE_DETAILS_FRAGMENT_TAG);
                            }
                        });
                    } else {
                        mBinding.emptyExpenseTextView.setVisibility(View.VISIBLE);
                        mBinding.progressBar.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mBinding.progressBar.setVisibility(View.GONE);
                    mBinding.emptyExpenseTextView.setText(databaseError.getMessage());
                    mBinding.emptyExpenseTextView.setVisibility(View.VISIBLE);
                }
            });
        } else {
            Snackbar.make(mBinding.rootView, "No internet connection", Snackbar.LENGTH_SHORT).show();
        }
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
        mFragmentLoader = (FragmentLoader) context;
    }

    private void newExpenseDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        final View view = LayoutInflater.from(mContext).inflate(R.layout.add_new_expense, null);
        dialog.setTitle("ADD EXPENSE");
        dialog.setView(view);
        final EditText expenseNameET = view.findViewById(R.id.expense_name_edit_text);
        final EditText expenseAmountET = view.findViewById(R.id.amount_edit_text);
        final EditText expenseCommentET = view.findViewById(R.id.comment_edit_text);
        final TextView expenseDateTextView = view.findViewById(R.id.date_text_view);
        expenseDateTextView.setText(mDateConverter.getDateInString(mDateConverter.getCurrentDate()));
        //final AlertDialog alertDialog = dialog.create();
        expenseDateTextView.setOnClickListener(new View.OnClickListener() {
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
                        expenseDateTextView.setText(date);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        dialog.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if (expenseNameET.getText().toString().isEmpty()) {
                    Snackbar.make(mBinding.rootView, "Please Enter ExpenseForRoom Name", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (expenseAmountET.getText().toString().isEmpty()) {
                    Snackbar.make(mBinding.rootView, "Please Enter ExpenseForRoom Amount", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (expenseDateTextView.getText().toString().isEmpty()) {
                    Snackbar.make(mBinding.rootView, "Please Enter ExpenseForRoom Date", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                showProgressDialog();
                String expenseName = expenseNameET.getText().toString().trim();
                final double expenseAmount = Double.parseDouble(expenseAmountET.getText().toString().trim());
                String comment = expenseCommentET.getText().toString().trim();
                DatabaseReference expenseRef = mAdminRef.child(Constant.EXPENSE_REF);
                String expenseKey = expenseRef.push().getKey();

                Expense expense = new Expense(expenseKey, expenseName, expenseAmount, comment, mExpenseDate);

                if (MainActivity.isNetworkAvailable(mContext)) {
                    expenseRef.child(expenseKey).setValue(expense).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                DateAmountCost profit = new DateAmountCost(mExpenseDate, expenseAmount);
                                DatabaseReference profitRef = mAdminRef.child(Constant.PROFIT_REF);
                                DatabaseReference costRef = profitRef.child(Constant.COST_REF);
                                costRef.push().setValue(profit).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(mContext, "ExpenseForRoom Added", Toast.LENGTH_SHORT).show();
                                        mBinding.emptyExpenseTextView.setVisibility(View.GONE);
                                        dismissProgressDialog();
                                        dialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dismissProgressDialog();
                                        dialog.dismiss();
                                    }
                                });

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
                            dismissProgressDialog();
                            //alertDialog.dismiss();
                        }
                    });
                } else {
                    dismissProgressDialog();
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
