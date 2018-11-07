package com.kcirque.stockmanagementfinal.Fragment;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Adapter.DueAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Customer;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.Activity.MainActivity;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentDueBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DueFragment extends Fragment implements SearchView.OnQueryTextListener {

    private FragmentDueBinding mBinding;
    private static DueFragment INSTANCE;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private SharedPref mSharedPref;
    private DatabaseReference mAdminRef;

    private DatabaseReference mCustomerRef;
    private List<Customer> mCustomerList = new ArrayList<>();
    private double mTotalDue = 0;
    private Context mContext;
    private DueAdapter mAdapter;
    private FragmentLoader mFragmentLoader;


    public DueFragment() {
        // Required empty public constructor
    }

    public static synchronized DueFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DueFragment();
        }

        return INSTANCE;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_due, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mSharedPref = new SharedPref(mContext);
        Seller seller = mSharedPref.getSeller();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        getActivity().setTitle("Total Due");

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        if (mUser != null) {
            mAdminRef = rootRef.child(mUser.getUid());
        } else {
            mAdminRef = rootRef.child(seller.getAdminUid());
        }
        mCustomerRef = mAdminRef.child(Constant.CUSTOMER_REF);

        if (MainActivity.isNetworkAvailable(mContext)) {
            mBinding.progressBar.setVisibility(View.VISIBLE);
            mBinding.dueListRecyclerView.setHasFixedSize(true);
            mBinding.dueListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mCustomerRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mCustomerList.clear();
                    mTotalDue = 0;
                    for (DataSnapshot postData : dataSnapshot.getChildren()) {
                        Customer customer = postData.getValue(Customer.class);
                        if (customer.getDue() > 0) {
                            mCustomerList.add(customer);
                            mTotalDue = mTotalDue + customer.getDue();
                        }
                    }

                    if (mCustomerList.size() > 0) {
                        mBinding.progressBar.setVisibility(View.GONE);
                        mAdapter = new DueAdapter(mContext, mCustomerList);
                        mBinding.dueListRecyclerView.setAdapter(mAdapter);
                        mAdapter.setRecyclerItemClickListener(new RecyclerItemClickListener() {
                            @Override
                            public void onClick(View view, int position, Object object) {
                                DueDetailsFragment fragment = DueDetailsFragment.getInstance();
                                Bundle bundle = new Bundle();
                                Customer customer = (Customer) object;
                                bundle.putSerializable(Constant.EXTRA_CUSTOMER, customer);
                                fragment.setArguments(bundle);
                                mFragmentLoader.loadFragment(fragment, true, Constant.DUE_DETAILS_FRAGMENT);
                            }
                        });

                    } else {
                        mBinding.emptyDueTextView.setVisibility(View.VISIBLE);
                        mBinding.progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mBinding.progressBar.setVisibility(View.GONE);
                    mBinding.emptyDueTextView.setVisibility(View.VISIBLE);
                    mBinding.emptyDueTextView.setText(databaseError.getMessage());
                }
            });
        } else {
            Snackbar.make(mBinding.rootView, "No internet connection", Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.due_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setIconified(false);
        searchView.setQueryHint("Customer name or date");
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentLoader = (FragmentLoader) context;
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        mAdapter.getFilter().filter(s);
        return true;
    }
}
