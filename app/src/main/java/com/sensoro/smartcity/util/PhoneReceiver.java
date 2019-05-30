package com.sensoro.smartcity.util;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.sensoro.common.model.EventData;
import com.sensoro.smartcity.constant.Constants;

import org.greenrobot.eventbus.EventBus;

public class PhoneReceiver extends BroadcastReceiver {

    private String mAction;


    @Override
    public void onReceive(Context context, Intent intent) {
        mAction = intent.getAction();
        if (Intent.ACTION_NEW_OUTGOING_CALL.equals(mAction)) {
            //去电
//            EventBus.getDefault().post(new EventData(Constants.VIDEO_STOP));
        } else {
            //来电
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            manager.listen(stateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private final PhoneStateListener stateListener = new PhoneStateListener() {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {

                case TelephonyManager.CALL_STATE_IDLE:
                    EventBus.getDefault().post(new EventData(Constants.VIDEO_START));

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //接听
                case TelephonyManager.CALL_STATE_RINGING:

                    EventBus.getDefault().post(new EventData(Constants.VIDEO_STOP));

                    break;

                default:
                    break;
            }
        }

        ;
    };
}