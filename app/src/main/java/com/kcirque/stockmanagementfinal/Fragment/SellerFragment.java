package com.kcirque.stockmanagementfinal.Fragment;


import android.content.Context;
import android.content.Intent;
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
import com.kcirque.stockmanagementfinal.Adapter.SellerAdapter;
import com.kcirque.stockmanagementfinal.ChatActivity;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Chat;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.Interface.RecyclerItemClickListener;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentSellerBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SellerFragment extends Fragment {

    private static SellerFragment INSTANCE;
    private FragmentSellerBinding mBinding;
    private List<Seller> mSellerList = new ArrayList<>();

    private Context mContext;
    private SellerAdapter mAdapter;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private SharedPref mSharedPref;
    private Seller mSeller;

    private FragmentLoader mFragmentLoader;
    private DatabaseReference mAdminRef;
    private List<Chat> mChatList = new ArrayList<>();
    private DatabaseReference sellerRef;
    private DatabaseReference chatRef;

    public SellerFragment() {
        // Required empty public constructor
    }

    public static synchronized SellerFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SellerFragment();
        }

        return INSTANCE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_seller, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mBinding.progressBar.setVisibility(View.VISIBLE);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        if (mUser != null) {
            mAdminRef = rootRef.child(mUser.getUid());
        }
        sellerRef = rootRef.child(Constant.SELLER_REF);
        chatRef = mAdminRef.child(Constant.CHAT_REF);

        mBinding.sellerListRecyclerView.setHasFixedSize(true);
        mBinding.sellerListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mBinding.addSellerFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentLoader.loadFragment(SellerAddFragment.getInstance(), true, Constant.SELLER_ADD_FRAGMENT_TAG);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChatList.clear();
                for (DataSnapshot postData : dataSnapshot.getChildren()) {
                    Chat chat = postData.getValue(Chat.class);
                    if (mUser != null) {
                        if (chat.getReceiver().equals(mUser.getUid()) && !chat.isIsSeen()) {
                            mChatList.add(chat);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        sellerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mSellerList.clear();
                for (DataSnapshot postData : dataSnapshot.getChildren()) {
                    Seller seller = postData.getValue(Seller.class);
                    if (seller.getAdminUid().equals(mUser.getUid())) {
                        mSellerList.add(seller);
                    }

                }
                if (mSellerList.size() > 0) {
                    mBinding.progressBar.setVisibility(View.GONE);
                    mAdapter = new SellerAdapter(mContext, mSellerList, mChatList);
                    mBinding.sellerListRecyclerView.setAdapter(mAdapter);
                    mAdapter.setItemClickListener(new RecyclerItemClickListener() {
                        @Override
                        public void onClick(View view, int position, Object object) {
                            Intent intent = new Intent(mContext, ChatActivity.class);
                            Seller seller = (Seller) object;
                            intent.putExtra(Constant.EXTRA_SELLER, seller);
                            startActivity(intent);
                        }
                    });
                } else {
                    mBinding.progressBar.setVisibility(View.GONE);
                    mBinding.emptySellerTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mBinding.progressBar.setVisibility(View.GONE);
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
