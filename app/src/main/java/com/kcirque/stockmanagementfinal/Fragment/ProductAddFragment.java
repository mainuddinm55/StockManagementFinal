package com.kcirque.stockmanagementfinal.Fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

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
import com.kcirque.stockmanagementfinal.Adapter.CategorySpinnerAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.DataBinderMapperImpl;
import com.kcirque.stockmanagementfinal.Database.Model.Category;
import com.kcirque.stockmanagementfinal.Database.Model.Product;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Interface.FragmentLoader;
import com.kcirque.stockmanagementfinal.MainActivity;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.Service.ImageUploadService;
import com.kcirque.stockmanagementfinal.databinding.FragmentProductAddBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductAddFragment extends Fragment {

    private static ProductAddFragment INSTANCE;
    FragmentProductAddBinding mBinding;

    private static final String TAG = "StockManagement";
    private static final int TAKE_PHOTO_REQUEST_CODE = 10;
    private List<Category> mCategoryList = new ArrayList<>();
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private SharedPref mSharedPref;
    private DatabaseReference mAdminRef;
    private DatabaseReference mRootRef;
    private DatabaseReference mCategoryRef;
    private DatabaseReference mProductRef;

    private FragmentLoader mFragmentLoader;

    private int mCategoryId;
    private int mProductId = 1;
    private String mKey;

    private Uri mImageUri = null;

    View.OnClickListener takePhoto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, TAKE_PHOTO_REQUEST_CODE);
        }
    };
    private Context mContext;

    public static synchronized ProductAddFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProductAddFragment();
        }

        return INSTANCE;
    }

    public ProductAddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_add, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onViewCreated: ");
        mSharedPref = new SharedPref(mContext);
        Seller seller = mSharedPref.getSeller();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
        mRootRef.keepSynced(true);
        getActivity().setTitle("Add a Product");
        if (mUser != null) {
            mAdminRef = mRootRef.child(mUser.getUid());
        } else {
            mAdminRef = mRootRef.child(seller.getAdminUid());
        }
        mCategoryRef = mAdminRef.child(Constant.CATEGORY_REF);
        mCategoryRef.keepSynced(true);
        mProductRef = mAdminRef.child(Constant.PRODUCT_REF);
        mProductRef.keepSynced(true);

        mProductRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mProductId = (int) dataSnapshot.getChildrenCount() + 1;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {

                mCategoryRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mCategoryList.clear();
                        for (DataSnapshot postData : dataSnapshot.getChildren()) {
                            Category category = postData.getValue(Category.class);
                            mCategoryList.add(category);
                        }
                        if (mCategoryList.size() > 0) {
                            CategorySpinnerAdapter adapter = new CategorySpinnerAdapter(mContext, mCategoryList);
                            mBinding.categorySpinner.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }).start();

        mBinding.productImageLayout.setOnClickListener(takePhoto);
        mBinding.productImageButton.setOnClickListener(takePhoto);
        mBinding.productImageText.setOnClickListener(takePhoto);

        mBinding.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCategoryId = mCategoryList.get(position).getCategoryId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mBinding.addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinding.productNameEdittext.getText().toString().trim().isEmpty()) {
                    mBinding.productNameEdittext.setError("Product Name Required");
                    mBinding.productNameEdittext.requestFocus();
                    return;
                }
                if (mBinding.productCodeEdittext.getText().toString().trim().isEmpty()) {
                    mBinding.productCodeEdittext.setError("Product Code Required");
                    mBinding.productCodeEdittext.requestFocus();
                    return;
                }
                if (mImageUri != null) {
                    addProduct();
                    String fileName = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
                    Intent intent = new Intent(mContext, ImageUploadService.class);
                    intent.putExtra(ImageUploadService.EXTRA_FILE_URI, mImageUri);
                    intent.putExtra(ImageUploadService.EXTRA_REF_KEY, mKey);
                    intent.putExtra(ImageUploadService.EXTRA_FILE_NAME, fileName);
                    mContext.startService(intent);
                    //Toast.makeText(AddProductActivity.this, "upload", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onClick: Image Found");
                } else {
                    Log.e(TAG, "onClick: No Image Found");
                    showNoImageWarningDialog();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: " );
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: " );
        mBinding.productCodeEdittext.setText(null);
        mBinding.productNameEdittext.setText(null);
        mBinding.productDescEdittext.setText(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: " );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: " );
    }

    private void showNoImageWarningDialog() {
        AlertDialog.Builder warningDialog = new AlertDialog.Builder(mContext);
        warningDialog.setTitle("Skip Image");
        warningDialog.setMessage("Do you want to skip image");
        warningDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addProduct();
            }
        });
        warningDialog.setNegativeButton("No", null);
        warningDialog.setNeutralButton("Cancel", null);

        warningDialog.show();
    }

    private void addProduct() {

        mKey = mProductRef.push().getKey();
        String productName = mBinding.productNameEdittext.getText().toString();
        String productCode = mBinding.productCodeEdittext.getText().toString();
        String desc = mBinding.productDescEdittext.getText().toString();

        Product product = new Product(mKey, mProductId, productName, productCode, mCategoryId, desc, "");

        mProductRef.child(mKey).setValue(product)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(mBinding.rootView, "Product Added", Snackbar.LENGTH_SHORT).show();
                            //Toast.makeText(AddProductActivity.this, "Product Added", Toast.LENGTH_SHORT).show();
                            mFragmentLoader.loadFragment(ProductListFragment.getInstance(), true, Constant.PRODUCT_LIST_FRAGMENT_TAG);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                });
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
            mBinding.productImageLayout.setVisibility(View.GONE);
            mBinding.productImageview.setVisibility(View.VISIBLE);
            mBinding.productImageview.setImageURI(mImageUri);
        }
    }
}
