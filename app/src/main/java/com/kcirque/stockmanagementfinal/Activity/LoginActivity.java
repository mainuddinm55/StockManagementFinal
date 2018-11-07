package com.kcirque.stockmanagementfinal.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Login";
    private ActivityLoginBinding mBinding;
    private FirebaseAuth mAuth;
    private String[] mUserTypes = new String[]{"Admin", "Seller"};
    private String mUserType;
    private boolean mIsLogIn = false;
    private ProgressDialog mProgressDialog;
    private SharedPref mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mBinding = DataBindingUtil.setContentView(LoginActivity.this, R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mSharedPref = new SharedPref(getApplicationContext());


        mProgressDialog = new ProgressDialog(LoginActivity.this);
        mProgressDialog.setMessage("loading");
        mProgressDialog.setCancelable(false);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(LoginActivity.this, android.R.layout.simple_spinner_dropdown_item, mUserTypes);
        mBinding.userTypeSpinner.setAdapter(adapter);

        mBinding.userTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mUserType = (String) parent.getItemAtPosition(position);
                if (mBinding.userTypeSpinner.getSelectedView() != null) {
                    ((TextView) mBinding.userTypeSpinner.getSelectedView()).setTextColor(getResources().getColor(android.R.color.white));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mBinding.emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    mBinding.errorTextView.setText(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    mBinding.errorTextView.setText(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mBinding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValid()) {
                    mProgressDialog.show();
                    final String email = mBinding.emailEditText.getText().toString().trim();
                    final String pass = mBinding.passwordEditText.getText().toString().trim();
                    if (MainActivity.isNetworkAvailable(LoginActivity.this)) {
                        if (mUserType.equals(mUserTypes[0])) {

                            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        final DatabaseReference isSingIn = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF).child(task.getResult().getUser().getUid()).child(Constant.IS_LOGGED);
                                        isSingIn.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    Boolean isLoggedIn = (Boolean) dataSnapshot.getValue();
                                                    if (!isLoggedIn) {
                                                        isSingIn.setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                mProgressDialog.dismiss();
                                                                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                                finish();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                mAuth.signOut();
                                                            }
                                                        });
                                                    } else {
                                                        mProgressDialog.dismiss();
                                                        mAuth.signOut();
                                                        mBinding.errorTextView.setText("Already login another device");
                                                        mBinding.emailEditText.setText(null);
                                                        mBinding.passwordEditText.setText(null);
                                                    }
                                                } else {
                                                    isSingIn.setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            mProgressDialog.dismiss();
                                                            Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                            finish();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            mAuth.signOut();
                                                            isSingIn.setValue(false);
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    mProgressDialog.dismiss();
                                    mBinding.errorTextView.setText(getResources().getString(R.string.invalid_email_pass_text));
                                    mBinding.emailEditText.setText(null);
                                    mBinding.passwordEditText.setText(null);
                                    Log.e(TAG, "onFailure: " + e.getMessage());
                                }
                            });
                        } else if (mUserType.equals(mUserTypes[1])) {
                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);
                            DatabaseReference sellerRef = rootRef.child(Constant.SELLER_REF);
                            sellerRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot postData : dataSnapshot.getChildren()) {
                                        Seller seller = postData.getValue(Seller.class);
                                        if (seller != null) {
                                            String sellerEmail = seller.getEmail();
                                            String sellerPassword = seller.getPassword();
                                            String adminUid = seller.getAdminUid();
                                            if (email.equals(sellerEmail) && pass.equals(sellerPassword)) {
                                                mSharedPref.putSeller(seller);
                                                mIsLogIn = true;
                                                mProgressDialog.dismiss();
                                                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                                        .putExtra(Constant.ADMIN_UID, adminUid));
                                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                finish();
                                                return;
                                            }
                                        }
                                    }

                                    if (!mIsLogIn) {
                                        mProgressDialog.dismiss();
                                        mBinding.errorTextView.setText(getResources().getString(R.string.invalid_email_pass_text));
                                        mBinding.emailEditText.setText(null);
                                        mBinding.passwordEditText.setText(null);
                                        Log.e(TAG, "onFailure: " + getResources().getString(R.string.invalid_email_pass_text));
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "No internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private boolean isValid() {
        mBinding.emailLayout.setError(null);
        mBinding.passwordLayout.setError(null);
        if (mBinding.emailEditText.getText().toString().isEmpty()) {
            mBinding.emailLayout.setError(getResources().getString(R.string.email_required_text));
            return false;
        } else if (!mBinding.emailEditText.getText().toString().contains("@")) {
            mBinding.emailLayout.setError(getResources().getString(R.string.invalid_email_text));
            return false;
        } else if (mBinding.passwordEditText.getText().toString().isEmpty()) {
            mBinding.passwordLayout.setError(getResources().getString(R.string.password_required_text));
            return false;
        } else {
            mBinding.emailLayout.setError(null);
            mBinding.passwordLayout.setError(null);
            return true;
        }
    }
}
