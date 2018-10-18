package com.kcirque.stockmanagementfinal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirque.stockmanagementfinal.Adapter.MessageAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Chat;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.Service.SinchService;
import com.kcirque.stockmanagementfinal.databinding.ActivityChatBinding;
import com.sinch.android.rtc.calling.Call;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends BaseActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private ActivityChatBinding mBinding;
    private FirebaseUser mUser;
    private List<Chat> mChatList = new ArrayList<>();

    private ValueEventListener seenListener;
    private DatabaseReference chatRef;
    private MessageAdapter adapter;
    private String receiverId;
    private String callerName;
    private Seller seller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(ChatActivity.this, R.layout.activity_chat);

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setTitle("");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        mBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        final Seller seller = (Seller) intent.getSerializableExtra(Constant.EXTRA_SELLER);
        if (mUser != null) {
            mBinding.sellerNameTextView.setText(seller.getName());
        } else {
            mBinding.sellerNameTextView.setText("Admin");
        }

        mBinding.sellerProfileImageView.setImageResource(R.drawable.user);

        mBinding.messageRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mBinding.messageRecyclerView.setLayoutManager(linearLayoutManager);

        if (mUser != null) {
            readMessage(mUser.getUid(), seller.getKey());
        } else {
            readMessage(seller.getKey(), seller.getAdminUid());
        }

        mBinding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mBinding.messageEditText.getText().toString();
                if (!msg.equals("")) {
                    if (mUser != null) {
                        sendMessage(mUser.getUid(), seller.getKey(), msg);
                    } else {
                        sendMessage(seller.getKey(), seller.getAdminUid(), msg);
                    }

                    mBinding.messageEditText.setText("");
                } else {
                    Snackbar.make(v, "You can't send empty message", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        if (mUser != null) {
            seenMessage(seller.getKey());
            receiverId = seller.getKey();
            callerName = seller.getName();
        } else {
            seenMessage(seller.getAdminUid());
            receiverId = seller.getAdminUid();
            callerName = "Admin";
        }

        mBinding.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!checkPermission()) {
                        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO,
                                        Manifest.permission.ACCESS_NETWORK_STATE,
                                        Manifest.permission.READ_PHONE_STATE},
                                PERMISSION_REQUEST_CODE);
                    }
                }
                callUser();
            }
        });

    }

    private void callUser() {
        Call call = getSinchServiceInterface().callUserAudio(receiverId);
        String callId = call.getCallId();
        Intent callScreen = new Intent(ChatActivity.this, CallActivity.class);
        callScreen.putExtra(SinchService.CALL_ID, callId);
        callScreen.putExtra(SinchService.CALLER_NAME, callerName);
        startActivity(callScreen);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == RESULT_OK) {
            callUser();
        }
    }

    private boolean checkPermission() {
        //asking for permissions here
        if (ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                ) {
            return false;
        } else {
            return true;
        }

    }

    @Override
    public void onDestroy() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        super.onDestroy();
    }

    private void sendMessage(String sender, String receiver, String msg) {
        DatabaseReference adminRef;
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);

        SharedPref sharedPref = new SharedPref(this);
        Seller seller = sharedPref.getSeller();

        if (mUser != null) {
            adminRef = rootRef.child(mUser.getUid());
        } else {
            adminRef = rootRef.child(seller.getAdminUid());
        }
        DatabaseReference chatRef = adminRef.child(Constant.CHAT_REF);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("msg", msg);
        hashMap.put("isSeen", false);
        chatRef.push().setValue(hashMap);
    }

    private void readMessage(final String myId, final String userId) {

        DatabaseReference adminRef;
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);

        SharedPref sharedPref = new SharedPref(this);
        Seller seller = sharedPref.getSeller();

        if (mUser != null) {
            adminRef = rootRef.child(mUser.getUid());
        } else {
            adminRef = rootRef.child(seller.getAdminUid());
        }
        DatabaseReference chatRef = adminRef.child(Constant.CHAT_REF);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChatList.clear();
                for (DataSnapshot postData : dataSnapshot.getChildren()) {
                    Chat chat = postData.getValue(Chat.class);
                    if (chat.getSender().equals(myId) && chat.getReceiver().equals(userId) ||
                            chat.getSender().equals(userId) && chat.getReceiver().equals(myId)) {
                        mChatList.add(chat);
                    }
                }

                if (mChatList.size() > 0) {
                    adapter = new MessageAdapter(ChatActivity.this, mChatList);
                    mBinding.messageRecyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void seenMessage(final String userId) {
        final DatabaseReference adminRef;
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(Constant.STOCK_MGT_REF);

        SharedPref sharedPref = new SharedPref(this);
        seller = sharedPref.getSeller();

        if (mUser != null) {
            adminRef = rootRef.child(mUser.getUid());
        } else {
            adminRef = rootRef.child(seller.getAdminUid());
        }
        chatRef = adminRef.child(Constant.CHAT_REF);

        seenListener = chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postData : dataSnapshot.getChildren()) {
                    Chat chat = postData.getValue(Chat.class);

                    if (mUser != null) {
                        if (chat.getReceiver().equals(mUser.getUid()) && chat.getSender().equals(userId)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("isSeen", true);
                            postData.getRef().updateChildren(hashMap);
                        }
                    } else {
                        if (chat.getReceiver().equals(seller.getKey()) && chat.getSender().equals(userId)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("isSeen", true);
                            postData.getRef().updateChildren(hashMap);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    protected void onPause() {
        super.onPause();
        chatRef.removeEventListener(seenListener);
    }
}
