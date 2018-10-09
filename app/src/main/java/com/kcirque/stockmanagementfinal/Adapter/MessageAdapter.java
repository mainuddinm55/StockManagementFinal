package com.kcirque.stockmanagementfinal.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kcirque.stockmanagementfinal.Common.SharedPref;
import com.kcirque.stockmanagementfinal.Database.Model.Chat;
import com.kcirque.stockmanagementfinal.Database.Model.Seller;
import com.kcirque.stockmanagementfinal.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    private FirebaseUser mUser;
    private SharedPref mSharedPref;

    public static final int MESSAGE_TYPE_LEFT = 1;
    public static final int MESSAGE_TYPE_RIGHT = 2;

    private Context mContext;
    private List<Chat> mChatList = new ArrayList<>();

    public MessageAdapter(Context context, List<Chat> chatList) {
        this.mContext = context;
        this.mChatList = chatList;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        if (i == MESSAGE_TYPE_LEFT) {
            View view = layoutInflater.inflate(R.layout.chat_item_left, viewGroup, false);
            return new MessageHolder(view);
        } else {
            View view = layoutInflater.inflate(R.layout.chat_item_right, viewGroup, false);
            return new MessageHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder messageHolder, int i) {
        messageHolder.messageTextView.setText(mChatList.get(i).getMsg());
        messageHolder.profileImageView.setImageResource(R.drawable.ic_user);

        if (i == (mChatList.size() - 1)) {
            if (mChatList.get(i).isIsSeen()) {
                messageHolder.msgSeenTextView.setText("seen");
            } else {
                messageHolder.msgSeenTextView.setText("delivered");
            }
        } else {
            messageHolder.msgSeenTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder {

        public CircleImageView profileImageView;
        public TextView messageTextView, msgSeenTextView;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profile_image_view);
            messageTextView = itemView.findViewById(R.id.show_message);
            msgSeenTextView = itemView.findViewById(R.id.msg_seen_text_view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mSharedPref = new SharedPref(mContext);
        Seller seller = mSharedPref.getSeller();

        if (mUser != null) {
            if (mChatList.get(position).getSender().equals(mUser.getUid())) {
                return MESSAGE_TYPE_RIGHT;
            } else {
                return MESSAGE_TYPE_LEFT;
            }
        } else if (seller != null) {
            if (mChatList.get(position).getSender().equals(seller.getKey())) {
                return MESSAGE_TYPE_RIGHT;
            } else {
                return MESSAGE_TYPE_LEFT;
            }
        } else {
            return -1;
        }

    }
}
