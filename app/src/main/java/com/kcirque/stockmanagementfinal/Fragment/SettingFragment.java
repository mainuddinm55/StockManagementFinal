package com.kcirque.stockmanagementfinal.Fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.FragmentSettingBinding;
import com.rengwuxian.materialedittext.MaterialEditText;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {
    private static final String TAG = "SettingFragment";
    private static final int TAKE_PHOTO_REQUEST_CODE = 20;
    private FragmentSettingBinding mBinding;
    private static SettingFragment INSTANCE;
    private Context mContext;
    private String[] settings = {"Username", "Company Name", "Change Password", "Set Reminder"};
    private FirebaseUser mUser;
    private Seller mSeller;

    private DatabaseReference mAdminRef;
    private FirebaseAuth mAuth;
    private ProgressDialog mSpinner;
    private DatabaseReference mRootRef;
    private Uri mImageUri = null;

    public SettingFragment() {
        // Required empty public constructor
    }

    public synchronized static SettingFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SettingFragment();
        }
        return INSTANCE;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Settings");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        SharedPref sharedPref = new SharedPref(mContext);
        mSeller = sharedPref.getSeller();
        mRootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);

        if (mUser != null) {
            mAdminRef = mRootRef.child(mUser.getUid());
            if (!mUser.getDisplayName().isEmpty()) {
                mBinding.userNameTextView.setText(mUser.getDisplayName());
            } else {
                mBinding.userNameTextView.setText("N/A");
                mBinding.userNameTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_edit), null);
                mBinding.userNameTextView.setCompoundDrawablePadding(10);
                mBinding.userNameTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showChangeUsernameDialog();
                    }
                });
            }

            mBinding.companyNameTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_edit), null);
            mBinding.companyNameTextView.setCompoundDrawablePadding(10);
            mBinding.companyNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showChangeCompanyNameDialog();
                }
            });

            mBinding.changePasswordTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_edit), null);
            mBinding.changePasswordTextView.setCompoundDrawablePadding(10);
            mBinding.changePasswordTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showChangePasswordDialog();
                }
            });

            mBinding.setReminderTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_edit), null);
            mBinding.setReminderTextView.setCompoundDrawablePadding(10);
            mBinding.setReminderTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSetReminderDialog();
                }
            });

            View.OnClickListener changeLogoClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeLogo();
                }
            };
            mBinding.changeLogoBtn.setOnClickListener(changeLogoClickListener);
            mBinding.logoImageView.setOnClickListener(changeLogoClickListener);

        } else {
            mAdminRef = mRootRef.child(mSeller.getAdminUid());
            mBinding.userNameTextView.setText(mSeller.getName());
            mBinding.changePasswordTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_edit), null);
            mBinding.changePasswordTextView.setCompoundDrawablePadding(10);
            mBinding.changePasswordTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showChangePasswordDialog();
                }
            });
        }

        mAdminRef.child(Constant.COMPANY_NAME_REF).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String companyName = dataSnapshot.getValue(String.class);
                mBinding.companyNameTextView.setText(companyName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
        mAdminRef.child(Constant.REMINDER_COUNT_REF).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String reminderCount = dataSnapshot.getValue(String.class);
                if (reminderCount != null)
                    mBinding.setReminderTextView.setText("Reminder Set : " + reminderCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    private void changeLogo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, TAKE_PHOTO_REQUEST_CODE);
    }

    private void showChangePasswordDialog() {
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
                if (newPasswordEditText.getText().toString().length() < 6) {
                    Snackbar.make(mBinding.rootView, "Password should be at least 6 characters", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (confirmPasswordEditText.getText().toString().length() < 6) {
                    Snackbar.make(mBinding.rootView, "Password should be at least 6 characters", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (!newPasswordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
                    Snackbar.make(mBinding.rootView, "Password does not match", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                showSpinner();
                final String oldPassword = oldPasswordEditText.getText().toString();
                final String newPassword = newPasswordEditText.getText().toString();
                if (mUser != null) {
                    AuthCredential authCredential = EmailAuthProvider.getCredential(mUser.getEmail(), oldPassword);
                    mAuth.getCurrentUser().reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Snackbar.make(mBinding.rootView, "Password Changed", Snackbar.LENGTH_SHORT).show();
                                            dismissSpinner();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "onFailure: " + e.getMessage());
                                        dismissSpinner();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(mBinding.rootView, "Old password does not match", Snackbar.LENGTH_SHORT).show();
                            dismissSpinner();
                        }
                    });

                } else {
                    final DatabaseReference sellerRef = mRootRef.child(Constant.SELLER_REF);
                    sellerRef.child(mSeller.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Seller seller = dataSnapshot.getValue(Seller.class);
                            if (seller != null && seller.getPassword().equals(oldPassword)) {
                                sellerRef.child(mSeller.getKey()).child("password").setValue(newPassword)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Snackbar.make(mBinding.rootView, "Password Changed", Snackbar.LENGTH_SHORT).show();
                                                    dismissSpinner();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "onFailure: " + e.getMessage());
                                                dismissSpinner();
                                            }
                                        });
                            } else {
                                Snackbar.make(mBinding.rootView, "Old password does not match", Snackbar.LENGTH_SHORT).show();
                                dismissSpinner();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
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

    private void showSetReminderDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("REMINDER COUNT");
        //dialog.setMessage("Change Username");
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View usernameView = inflater.inflate(R.layout.set_reminder_layout, null);
        final MaterialEditText reminderCountEditText = usernameView.findViewById(R.id.reminder_count_edit_text);
        dialog.setView(usernameView);
        dialog.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
                if (TextUtils.isEmpty(reminderCountEditText.getText().toString())) {
                    Snackbar.make(mBinding.rootView, "Please enter a number", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                final String reminderCount = reminderCountEditText.getText().toString();
                mAdminRef.child(Constant.REMINDER_COUNT_REF).setValue(reminderCount)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mBinding.setReminderTextView.setText("Reminder Set : " + reminderCount);
                                    Snackbar.make(mBinding.rootView, "Reminder Count Set", Snackbar.LENGTH_SHORT).show();
                                    dialog.dismiss();
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
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showChangeCompanyNameDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("COMPANY NAME");
        //dialog.setMessage("Change Username");
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View usernameView = inflater.inflate(R.layout.company_name_change_layout, null);
        final MaterialEditText companyNameEditText = usernameView.findViewById(R.id.company_name_edit_text);
        dialog.setView(usernameView);
        dialog.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
                if (TextUtils.isEmpty(companyNameEditText.getText().toString())) {
                    Snackbar.make(mBinding.rootView, "Please enter company name", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                final String companyName = companyNameEditText.getText().toString();
                mAdminRef.child(Constant.COMPANY_NAME_REF).setValue(companyName)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mBinding.companyNameTextView.setText(companyName);
                                    Snackbar.make(mBinding.rootView, "Company Name Changed", Snackbar.LENGTH_SHORT).show();
                                    dialog.dismiss();
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
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showChangeUsernameDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("USERNAME");
        //dialog.setMessage("Change Username");
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View usernameView = inflater.inflate(R.layout.username_change_layout, null);
        final MaterialEditText usernameEditText = usernameView.findViewById(R.id.user_name_edit_text);
        dialog.setView(usernameView);
        dialog.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
                if (TextUtils.isEmpty(usernameEditText.getText().toString())) {
                    Snackbar.make(mBinding.rootView, "Please enter username", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                final String username = usernameEditText.getText().toString();
                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                        .setDisplayName(username).build();
                mUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(mBinding.rootView, "Username Changed", Snackbar.LENGTH_SHORT).show();
                            mBinding.userNameTextView.setText(username);
                            dialog.dismiss();
                        }
                    }
                });

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            mBinding.logoImageView.setImageURI(mImageUri);
            upload();
        }
    }

    private void upload() {
        showSpinner();
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference(Constant.LOGO_REF);
        final StorageReference fileReference = mStorageRef.child(mImageUri.getPath() + "." + getFileExtension(mImageUri));
        fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String logoUrl = taskSnapshot.getDownloadUrl().toString();
                mAdminRef.child(Constant.LOGO_URL_REF).setValue(logoUrl)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Snackbar.make(mBinding.rootView, "Logo Upload Success", Snackbar.LENGTH_SHORT).show();
                                    dismissSpinner();
                                }
                            }
                        });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e.getMessage());
                dismissSpinner();
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = mContext.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
