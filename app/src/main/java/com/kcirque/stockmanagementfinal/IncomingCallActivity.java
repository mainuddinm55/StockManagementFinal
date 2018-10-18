package com.kcirque.stockmanagementfinal;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kcirque.stockmanagementfinal.Common.AudioPlayer;
import com.kcirque.stockmanagementfinal.Service.SinchService;
import com.kcirque.stockmanagementfinal.databinding.ActivityIncomingCallBinding;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;
import com.sinch.android.rtc.video.VideoCallListener;

import java.util.List;

public class IncomingCallActivity extends BaseActivity {
    private ActivityIncomingCallBinding mBinding;
    static final String TAG = IncomingCallActivity.class.getSimpleName();
    private String mCallId;
    private String mCallerName;
    private AudioPlayer mAudioPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(IncomingCallActivity.this, R.layout.activity_incoming_call);

        mAudioPlayer = new AudioPlayer(this);
        mAudioPlayer.playRingtone();
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
        mCallerName = getIntent().getStringExtra(SinchService.CALLER_NAME);

        mBinding.answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerClicked();
            }
        });

        mBinding.declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineClicked();
            }
        });

    }

    @Override
    protected void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());
            mBinding.remoteUser.setText(mCallId);

        } else {
            Log.e(TAG, "Started with invalid callId, aborting");
            finish();
        }
    }

    private void answerClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.answer();
            Intent intent = new Intent(this, CallActivity.class);
            intent.putExtra(SinchService.CALL_ID, mCallId);
            intent.putExtra(SinchService.CALLER_NAME,mCallerName);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            finish();
        }
    }

    private void declineClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended, cause: " + cause.toString());
            mAudioPlayer.stopRingtone();
            finish();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

    }
}
