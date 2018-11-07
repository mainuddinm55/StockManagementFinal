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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.Activity.MainActivity;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentSellerAddBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SellerAddFragment extends Fragment {

    private static final int TAKE_PHOTO_REQUEST_CODE = 30;
    private static SellerAddFragment INSTANCE;
    private static final String TAG = "Add Seller";
    private FragmentSellerAddBinding mBinding;
    private String mSellerName;
    private String mSellerEmail;
    private String mSellerPassword;
    private String mSellerMobile;
    private Context mContext;
    private FragmentLoader mFragmentLoader;
    private ProgressDialog progressDialog;
    private String mStatus = "Active";
    private Seller mSeller;
    private View.OnClickListener changeImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, TAKE_PHOTO_REQUEST_CODE);
        }
    };
    private Uri mImageUri;
    private DatabaseReference sellerRef;
    private Bundle bundle;

    public static synchronized SellerAddFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SellerAddFragment();
        }

        return INSTANCE;
    }

    public SellerAddFragment() {
        // Required empty public constructor
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_seller_add, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            bundle = getArguments();
            if (bundle != null) {
                mSeller = (Seller) bundle.getSerializable(Constant.EXTRA_SELLER);
                getActivity().setTitle("Update Seller");
                mBinding.sellerNameEditText.setText(mSeller.getName());
                mBinding.sellerMobileEditText.setText(mSeller.getMobile());
                mBinding.sellerEmailEditText.setText(mSeller.getEmail());
                mBinding.passwordLayout.setVisibility(View.GONE);
                mBinding.confirmPasswordLayout.setVisibility(View.GONE);
                Glide.with(mContext).load(mSeller.getImageUrl()).apply(RequestOptions.placeholderOf(R.drawable.ic_user))
                        .into(mBinding.sellerImage);
                mBinding.statusRadioGroup.check(mSeller.getStatus().equals("Active") ? R.id.active_radio_btn : R.id.deactivate_radio_btn);
                mBinding.addSellerBtn.setText("Update");
            } else {
                getActivity().setTitle("Add a Seller");

            }
            mBinding.statusRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton = view.findViewById(checkedId);
                    mStatus = radioButton.getText().toString();
                }
            });
            mBinding.addSellerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSeller != null && mSeller.getKey() != null) {
                        updateSeller();
                    } else {
                        addNewSeller();
                    }
                }
            });
            mBinding.changeImageBtn.setOnClickListener(changeImageClickListener);
            mBinding.sellerImage.setOnClickListener(changeImageClickListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
    }

    private void updateSeller() {
        if (mBinding.sellerNameEditText.getText().toString().isEmpty()) {
            mBinding.sellerNameEditText.setError(getResources().getString(R.string.name_required));
            mBinding.sellerNameEditText.requestFocus();
            return;
        }
        if (mBinding.sellerEmailEditText.getText().toString().isEmpty()) {
            mBinding.sellerEmailEditText.setError(getResources().getString(R.string.name_required));
            mBinding.sellerEmailEditText.requestFocus();
            return;
        }
        if (!mBinding.sellerEmailEditText.getText().toString().contains("@")) {
            mBinding.sellerEmailEditText.setError(getResources().getString(R.string.invalid_email_text));
            mBinding.sellerEmailEditText.requestFocus();
            return;
        }

        showProgressDialog();
        mSellerName = mBinding.sellerNameEditText.getText().toString();
        mSellerEmail = mBinding.sellerEmailEditText.getText().toString();
        mSellerMobile = mBinding.sellerMobileEditText.getText().toString();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", mSellerName);
        hashMap.put("email", mSellerEmail);
        hashMap.put("mobile", mSellerMobile);
        hashMap.put("status", mStatus);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        DatabaseReference sellerRef = rootRef.child(Constant.SELLER_REF);
        if (MainActivity.isNetworkAvailable(mContext)) {
            sellerRef.child(mSeller.getKey()).updateChildren(hashMap,
                    new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            dismissProgressDialog();
                            Toast.makeText(mContext, "Seller Updated", Toast.LENGTH_SHORT).show();
                            if (mImageUri != null) {
                                new UploadTask(mSeller.getKey()).doInBackground(mImageUri);
                            }
                            mFragmentLoader.loadFragment(SellerFragment.getInstance(), true, Constant.SELLER_FRAGMENT_TAG);
                        }
                    }
            );
        } else {
            dismissProgressDialog();
            Snackbar.make(mBinding.rootView, "No internet connection", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void addNewSeller() {
        if (isValid()) {
            String password = mBinding.sellerPasswordEditText.getText().toString();
            String confirmPassword = mBinding.confirmPasswordEditText.getText().toString();
            if (password.equals(confirmPassword)) {
                mSellerPassword = password;
            } else {
                mBinding.confirmPasswordEditText.setError("Password does not match");
                mBinding.sellerPasswordEditText.setError("Password does not match");
                return;
            }
            showProgressDialog();
            mSellerName = mBinding.sellerNameEditText.getText().toString();
            mSellerEmail = mBinding.sellerEmailEditText.getText().toString();
            mSellerMobile = mBinding.sellerMobileEditText.getText().toString();
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
            sellerRef = rootRef.child(Constant.SELLER_REF);
            final String key = sellerRef.push().getKey();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uId = user.getUid();

            Seller seller = new Seller(key, mSellerName, mSellerEmail, mSellerPassword, uId, mSellerMobile, mStatus);

            if (MainActivity.isNetworkAvailable(mContext)) {
                sellerRef.child(key).setValue(seller).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dismissProgressDialog();
                            Toast.makeText(mContext, "Seller Added", Toast.LENGTH_SHORT).show();
                            if (mImageUri != null) {
                                new UploadTask(key).doInBackground(mImageUri);
                            }
                            mFragmentLoader.loadFragment(SellerFragment.getInstance(), true, Constant.SELLER_FRAGMENT_TAG);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dismissProgressDialog();
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                });
            }
        } else {
            dismissProgressDialog();
            Snackbar.make(mBinding.rootView, "No internet connection", Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean isValid() {
        if (mBinding.sellerNameEditText.getText().toString().isEmpty()) {
            mBinding.sellerNameEditText.setError(getResources().getString(R.string.name_required));
            return false;
        } else if (mBinding.sellerEmailEditText.getText().toString().isEmpty()) {
            mBinding.sellerEmailEditText.setError(getResources().getString(R.string.email_required_text));
            return false;
        } else if (!mBinding.sellerEmailEditText.getText().toString().contains("@")) {
            mBinding.sellerEmailEditText.setError(getResources().getString(R.string.invalid_email_text));
            return false;
        } else if (mBinding.sellerPasswordEditText.getText().toString().isEmpty()) {
            mBinding.sellerPasswordEditText.setError(getResources().getString(R.string.password_required_text));
            return false;
        } else if (mBinding.confirmPasswordEditText.getText().toString().isEmpty()) {
            mBinding.confirmPasswordEditText.setError(getResources().getString(R.string.password_required_text));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentLoader = (FragmentLoader) context;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            Glide.with(mContext).load(mImageUri).apply(RequestOptions.placeholderOf(R.drawable.ic_user))
                    .into(mBinding.sellerImage);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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

    private String getFileExtension(Uri uri) {
        ContentResolver cR = mContext.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    class UploadTask extends AsyncTask<Uri, Void, Void> {
        private final String sellerKey;

        public UploadTask(String sellerKey) {
            this.sellerKey = sellerKey;
        }

        @Override
        protected Void doInBackground(Uri... uris) {
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference(Constant.SELLER_REF);
            String fileName = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
            StorageReference fileReference = mStorageRef.child(fileName + "." + getFileExtension(uris[0]));
            fileReference.putFile(uris[0]).addOnSuccessListener(new OnSuccessListener<com.google.firebase.storage.UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(com.google.firebase.storage.UploadTask.TaskSnapshot taskSnapshot) {
                    String imageUrl = taskSnapshot.getDownloadUrl().toString();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("imageUrl", imageUrl);
                    sellerRef.child(sellerKey).updateChildren(hashMap);
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
}
