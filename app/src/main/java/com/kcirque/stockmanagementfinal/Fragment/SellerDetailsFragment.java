package com.kcirque.stockmanagementfinal.Fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Activity.ChatActivity;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.Activity.MainActivity;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentSellerDetailsBinding;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class SellerDetailsFragment extends Fragment {
    private FragmentSellerDetailsBinding mBinding;
    private Context mContext;
    private FragmentLoader mFragmentLoader;
    private ProgressDialog mSpinner;
    private Seller mSeller;

    public SellerDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_seller_details, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        final Bundle bundle = getArguments();
        if (bundle != null) {
            mSeller = (Seller) bundle.getSerializable(Constant.EXTRA_SELLER);
            mBinding.sellerNameTextView.setText(mSeller.getName());
            mBinding.mobileTextView.setText(mSeller.getMobile());
            mBinding.emailTextView.setText(mSeller.getEmail());
            mBinding.statusTextView.setText(mSeller.getStatus());
            mBinding.passwordTextView.setText(mSeller.getPassword());
            Glide.with(mContext).load(mSeller.getImageUrl()).apply(RequestOptions.placeholderOf(R.drawable.ic_user))
                    .into(mBinding.sellerImage);
            mBinding.updateSellerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SellerAddFragment fragment = new SellerAddFragment();
                    fragment.setArguments(bundle);
                    mFragmentLoader.loadFragment(fragment, true, Constant.SELLER_ADD_FRAGMENT_TAG);
                }
            });
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
        inflater.inflate(R.menu.seller_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_password:
                changeSellerPassword();
                return true;
            case R.id.send_message:
                openChatActivity();
                return true;
            case R.id.delete_seller:
                deleteSeller();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteSeller() {
        if (MainActivity.isNetworkAvailable(mContext)) {
            showSpinner();
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
            DatabaseReference sellerRef = rootRef.child(Constant.SELLER_REF);
            sellerRef.child(mSeller.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        dismissSpinner();
                        Snackbar.make(mBinding.rootView, "Seller Deleted", Snackbar.LENGTH_SHORT).show();
                        mFragmentLoader.loadFragment(new SellerFragment(), true, Constant.HOME_FRAGMENT_TAG);
                    } else {
                        dismissSpinner();
                        Snackbar.make(mBinding.rootView, "Seller not Deleted", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Snackbar.make(mBinding.rootView, "No internet connection", Snackbar.LENGTH_SHORT).show();
        }

    }

    private void openChatActivity() {
        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.putExtra(Constant.EXTRA_SELLER, mSeller);
        mContext.startActivity(intent);
    }

    private void changeSellerPassword() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("CHANGE PASSWORD");
        //dialog.setMessage("Change Username");
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View usernameView = inflater.inflate(R.layout.change_password_layout, null);
        final MaterialEditText oldPasswordEditText = usernameView.findViewById(R.id.old_password_edit_text);
        final MaterialEditText newPasswordEditText = usernameView.findViewById(R.id.new_password_edit_text);
        final MaterialEditText confirmPasswordEditText = usernameView.findViewById(R.id.confirm_password_edit_text);
        dialog.setView(usernameView);
        dialog.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                //dialog.dismiss();

                if (TextUtils.isEmpty(oldPasswordEditText.getText().toString())) {
                    Snackbar.make(mBinding.rootView, "Please enter old password", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(newPasswordEditText.getText().toString())) {
                    Snackbar.make(mBinding.rootView, "Please enter new password", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmPasswordEditText.getText().toString())) {
                    Snackbar.make(mBinding.rootView, "Please enter confirm password", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (!newPasswordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
                    Snackbar.make(mBinding.rootView, "Please enter same password", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                showSpinner();
                final String oldPassword = oldPasswordEditText.getText().toString();
                final String newPassword = newPasswordEditText.getText().toString();
                if (MainActivity.isNetworkAvailable(mContext)) {
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
                    final DatabaseReference sellerRef = rootRef.child(Constant.SELLER_REF);
                    sellerRef.child(mSeller.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Seller seller = dataSnapshot.getValue(Seller.class);
                            if (oldPassword.equals(seller.getPassword())) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("password", newPassword);
                                sellerRef.child(mSeller.getKey()).updateChildren(hashMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        dismissSpinner();
                                        dialog.dismiss();
                                        Snackbar.make(mBinding.rootView, "Password Changed", Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                dismissSpinner();
                                Snackbar.make(mBinding.rootView, "Password does not matched", Snackbar.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentLoader = (FragmentLoader) context;
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(mContext);
        mSpinner.setTitle("Loading....");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }

    private void dismissSpinner() {
        if (mSpinner != null)
            mSpinner.dismiss();
    }
}
