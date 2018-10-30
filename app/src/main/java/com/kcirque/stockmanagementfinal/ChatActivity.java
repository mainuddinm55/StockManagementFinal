package com.kcirque.stockmanagementfinal;

import android.content.ContentResolver;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

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
import com.kcirque.stockmanagementfinal.Adapter.MessageAdapter;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Chat;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.databinding.ActivityChatBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private static final int PHOTO_PICKER_REQUEST_CODE = 2;
    private ActivityChatBinding mBinding;
    private FirebaseUser mUser;
    private List<Chat> mChatList = new ArrayList<>();

    private ValueEventListener seenListener;
    private DatabaseReference chatRef;
    private MessageAdapter adapter;
    private Seller sellerPref;
    private Seller sellerIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(ChatActivity.this, R.layout.activity_chat);

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setTitle("");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        SharedPref sharedPref = new SharedPref(this);
        sellerPref = sharedPref.getSeller();

        mBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        sellerIntent = (Seller) intent.getSerializableExtra(Constant.EXTRA_SELLER);
        if (mUser != null) {
            mBinding.sellerNameTextView.setText(sellerIntent.getName());
        } else {
            mBinding.sellerNameTextView.setText("Admin");
        }

        mBinding.sellerProfileImageView.setImageResource(R.drawable.user);

        mBinding.messageRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mBinding.messageRecyclerView.setLayoutManager(linearLayoutManager);

        if (mUser != null) {
            readMessage(mUser.getUid(), sellerIntent.getKey());
        } else {
            readMessage(sellerPref.getKey(), sellerPref.getAdminUid());
        }

        mBinding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mBinding.messageEditText.getText().toString();
                if (!msg.equals("")) {
                    if (mUser != null) {
                        sendMessage(mUser.getUid(), sellerIntent.getKey(), msg);
                    } else {
                        sendMessage(sellerPref.getKey(), sellerPref.getAdminUid(), msg);
                    }

                    mBinding.messageEditText.setText("");
                } else {
                    Snackbar.make(v, "You can't send empty message", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        mBinding.picBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), PHOTO_PICKER_REQUEST_CODE);
            }
        });

        if (mUser != null) {
            seenMessage(sellerIntent.getKey());
        } else {
            seenMessage(sellerIntent.getAdminUid());
        }

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

        Chat chat = new Chat(sender, receiver, msg, null, false);
        /*HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("msg", msg);
        hashMap.put("isSeen", false);*/
        chatRef.push().setValue(chat);
    }

    private void sentPhoto(String sender, String receiver, String imageUrl) {
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

        Chat chat = new Chat(sender, receiver, null, imageUrl, false);

        chatRef.push().setValue(chat);
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

        if (mUser != null) {
            adminRef = rootRef.child(mUser.getUid());
        } else {
            adminRef = rootRef.child(sellerPref.getAdminUid());
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
                        if (chat.getReceiver().equals(sellerPref.getKey()) && chat.getSender().equals(userId)) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            if (MainActivity.isNetworkAvailable(this)) {
                new BitmapUploadTask(data.getData()).execute();
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class ImageUploadTask extends AsyncTask<Void, Void, Void> {
        private final StorageReference fileReference;
        private Uri imageUri;
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());

        public ImageUploadTask(Uri imageUri) {
            this.imageUri = imageUri;
            StorageReference chatImageRef = FirebaseStorage.getInstance().getReference(Constant.CHAT_IMAGE_REF);
            fileReference = chatImageRef.child(fileName
                    + "." + getFileExtension(imageUri));
        }

        private String getFileExtension(Uri uri) {
            ContentResolver cR = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cR.getType(uri));
        }

        @Override
        protected Void doInBackground(Void... voids) {
            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (mUser != null) {
                        sentPhoto(mUser.getUid(), sellerIntent.getKey(), taskSnapshot.getDownloadUrl().toString());
                    } else {
                        sentPhoto(sellerPref.getKey(), sellerPref.getAdminUid(), taskSnapshot.getDownloadUrl().toString());
                    }
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

    public class BitmapUploadTask extends AsyncTask<Void, Void, Void> {
        private final StorageReference fileReference;
        private Uri imageUri;
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());

        public BitmapUploadTask(Uri imageUri) {
            this.imageUri = imageUri;
            StorageReference chatImageRef = FirebaseStorage.getInstance().getReference(Constant.CHAT_IMAGE_REF);
            fileReference = chatImageRef.child(fileName
                    + "." + getFileExtension(imageUri));
        }

        private String getFileExtension(Uri uri) {
            ContentResolver cR = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cR.getType(uri));
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = fileReference.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if (mUser != null) {
                            sentPhoto(mUser.getUid(), sellerIntent.getKey(), taskSnapshot.getDownloadUrl().toString());
                        } else {
                            sentPhoto(sellerPref.getKey(), sellerPref.getAdminUid(), taskSnapshot.getDownloadUrl().toString());
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
