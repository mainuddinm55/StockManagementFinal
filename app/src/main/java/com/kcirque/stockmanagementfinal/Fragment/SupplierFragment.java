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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Adapter.SupplierAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Database.Model.Supplier;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentSupplierBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SupplierFragment extends Fragment {

    private FragmentSupplierBinding mBinding;

    private Context mContext;
    private FragmentLoader mFragmentLoader;

    private List<Supplier> mSupplierList = new ArrayList<>();

    public SupplierFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_supplier, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Supplier List");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        if (user != null) {
            DatabaseReference adminRef = rootRef.child(user.getUid());
            final DatabaseReference supplierRef = adminRef.child(Constant.SUPPLIER_REF);
            mBinding.supplierRecyclerView.setHasFixedSize(true);
            mBinding.supplierRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            final SupplierAdapter adapter = new SupplierAdapter();
            mBinding.supplierRecyclerView.setAdapter(adapter);
            supplierRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mSupplierList.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Supplier supplier = data.getValue(Supplier.class);
                        mSupplierList.add(supplier);
                    }
                    if (mSupplierList.size() > 0) {
                        adapter.setSupplierList(mSupplierList);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            adapter.setRecyclerItemClickListener(new RecyclerItemClickListener() {
                @Override
                public void onClick(View view, int position, Object object) {
                    Supplier supplier = (Supplier) object;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constant.EXTRA_SUPPLIER, supplier);
                    SupplierDetailsFragment fragment = new SupplierDetailsFragment();
                    fragment.setArguments(bundle);
                    mFragmentLoader.loadFragment(fragment, true, Constant.SUPPLIER_DETAILS_FRAGMENT_TAG);
                }
            });
        }
        mBinding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentLoader.loadFragment(new SupplierAddFragment(), true, Constant.SUPPLIER_ADD_FRAGMENT_TAG);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        this.mFragmentLoader = (FragmentLoader) context;
    }
}
