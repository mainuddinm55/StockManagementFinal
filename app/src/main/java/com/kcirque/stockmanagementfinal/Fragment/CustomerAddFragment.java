package com.kcirque.stockmanagementfinal.Fragment;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Customer;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.Activity.MainActivity;
import com.kcirque.stockmanagementfinal.databinding.FragmentCustomerAddBinding;

import com.kcirque.stockmanagementfinal.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerAddFragment extends Fragment {
    private static final String TAG = "CustomerAddFragment";
    private static final int TAKE_PHOTO_REQUEST_CODE = 20;

    private FragmentLoader mFragmentLoader;
    private static CustomerAddFragment INSTANCE;
    private FragmentCustomerAddBinding mBinding;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private SharedPref mSharedPref;
    private DatabaseReference mAdminRef;
    private int mCustomerId = 1;

    private DatabaseReference mRootRef;
    private DatabaseReference mCustomerRef;
    private boolean mIsMercantile = false;
    private Context mContext;
    private ProgressDialog progressDialog;
    private Customer mCustomer;
    private Uri mImageUri;

    public static synchronized CustomerAddFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CustomerAddFragment();
        }
        return INSTANCE;
    }


    public CustomerAddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_customer_add, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        mSharedPref = new SharedPref(mContext);
        Seller seller = mSharedPref.getSeller();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        if (mUser != null) {
            mAdminRef = mRootRef.child(mUser.getUid());
        } else {
            mAdminRef = mRootRef.child(seller.getAdminUid());
        }
        mCustomerRef = mAdminRef.child(Constant.CUSTOMER_REF);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mCustomer = (Customer) bundle.getSerializable(Constant.EXTRA_CUSTOMER);
            getActivity().setTitle("Update Customer");
            mCustomerId = mCustomer.getCustomerId();
            mBinding.accountTypeRg.check(mCustomer.isMercantile() ? R.id.mercantile_account_rb : R.id.normal_account_rb);
            mBinding.customerNameEdittext.setText(mCustomer.getCustomerName());
            mBinding.customerAddressEdittext.setText(mCustomer.getAddress());
            mBinding.customerMobileEdittext.setText(mCustomer.getMobile());
            mBinding.customerEmailEdittext.setText(mCustomer.getEmail());
            mBinding.addCustBtn.setText("Update");
            Glide.with(mContext).load(mCustomer.getImageUrl()).apply(RequestOptions.placeholderOf(R.drawable.ic_user))
                    .into(mBinding.customerImage);
        } else {
            getActivity().setTitle("Add a Customer");
            mCustomerRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mCustomerId = (int) (dataSnapshot.getChildrenCount() + 1);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


        mBinding.accountTypeRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = view.findViewById(checkedId);
                if (radioButton.getText().toString().equals("Normal")) {
                    mIsMercantile = false;
                } else if (radioButton.getText().toString().equals("Mercantile")) {
                    mIsMercantile = true;
                }
            }
        });

        mBinding.addCustBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBinding.customerNameEdittext.getText().toString().isEmpty()) {
                    mBinding.customerNameEdittext.setError("Customer name required");
                    mBinding.customerNameEdittext.requestFocus();
                    return;
                }
                if (mBinding.customerMobileEdittext.getText().toString().isEmpty()) {
                    mBinding.customerMobileEdittext.setError("Customer mobile required");
                    mBinding.customerMobileEdittext.requestFocus();
                    return;
                }
                showProgressDialog();
                if (mCustomer != null && mCustomer.getKey() != null) {
                    updateCustomer();
                } else {
                    addNewCustomer();
                }

            }
        });

        mBinding.customerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, TAKE_PHOTO_REQUEST_CODE);
            }
        });

    }

    private void updateCustomer() {
        String name = mBinding.customerNameEdittext.getText().toString().trim();
        String address = mBinding.customerAddressEdittext.getText().toString().trim();
        String mobile = mBinding.customerMobileEdittext.getText().toString().trim();
        String email = mBinding.customerEmailEdittext.getText().toString().trim();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("customerName", name);
        hashMap.put("address", address);
        hashMap.put("mobile", mobile);
        hashMap.put("email", email);
        hashMap.put("mercantile", mIsMercantile);
        if (mCustomer.isMercantile() && !mIsMercantile && mCustomer.getDue() > 0) {
            dismissProgressDialog();
            Snackbar.make(mBinding.rootView, "Please paid due before switch account", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (MainActivity.isNetworkAvailable(mContext)) {
            mCustomerRef.child(mCustomer.getKey()).updateChildren(hashMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    dismissProgressDialog();
                    Snackbar.make(mBinding.rootView, "Customer Added", Snackbar.LENGTH_SHORT).show();
                    if (mImageUri != null) {
                        new UploadTask(mCustomer.getKey()).doInBackground(mImageUri);
                    }
                    mFragmentLoader.loadFragment(CustomerListFragment.getInstance(), true, Constant.CUSTOMER_LIST_FRAGMENT_TAG);
                }
            });
        } else {
            Snackbar.make(mBinding.rootView, "No internet connection", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void addNewCustomer() {
        final String key = mCustomerRef.push().getKey();
        String name = mBinding.customerNameEdittext.getText().toString().trim();
        String address = mBinding.customerAddressEdittext.getText().toString().trim();
        String mobile = mBinding.customerMobileEdittext.getText().toString().trim();
        String email = mBinding.customerEmailEdittext.getText().toString().trim();

        Customer customer = new Customer(key, mCustomerId, name, address, mobile, email, 0, mIsMercantile);

        if (MainActivity.isNetworkAvailable(mContext)) {
            mCustomerRef.child(key).setValue(customer).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        dismissProgressDialog();
                        Snackbar.make(mBinding.rootView, "Customer Added", Snackbar.LENGTH_SHORT).show();
                        if (mImageUri != null) {
                            new UploadTask(key).doInBackground(mImageUri);
                        }
                        mFragmentLoader.loadFragment(CustomerListFragment.getInstance(), true, Constant.CUSTOMER_LIST_FRAGMENT_TAG);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dismissProgressDialog();
                    Snackbar.make(mBinding.rootView, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            });
        } else {
            Snackbar.make(mBinding.rootView, "No internet connection", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            Glide.with(mContext).load(mImageUri).apply(RequestOptions.placeholderOf(R.drawable.ic_user))
                    .into(mBinding.customerImage);
        }
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

    class UploadTask extends AsyncTask<Uri, Void, Void> {
        private final String sellerKey;

        public UploadTask(String sellerKey) {
            this.sellerKey = sellerKey;
        }

        @Override
        protected Void doInBackground(Uri... uris) {
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference(Constant.CUSTOMER_REF);
            String fileName = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
            StorageReference fileReference = mStorageRef.child(fileName + "." + getFileExtension(uris[0]));
            fileReference.putFile(uris[0]).addOnSuccessListener(new OnSuccessListener<com.google.firebase.storage.UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(com.google.firebase.storage.UploadTask.TaskSnapshot taskSnapshot) {
                    String imageUrl = taskSnapshot.getDownloadUrl().toString();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("imageUrl", imageUrl);
                    mCustomerRef.child(sellerKey).updateChildren(hashMap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: " + e.getMessage());
                }
            });

            return null;
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = mContext.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
