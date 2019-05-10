package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IAlarmCameraLiveDetailActivityView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AlarmCameraLiveDetailActivityPresenter extends BasePresenter<IAlarmCameraLiveDetailActivityView> {
    private Activity mActivity;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;

        Intent intent = mActivity.getIntent();
        if (intent != null) {
            Serializable serializable = intent.getSerializableExtra(Constants.EXTRA_ALARM_CAMERAS);
            if (serializable instanceof ArrayList) {
                List<String> cameras = (List<String>) serializable;
                if (cameras.size() > 0) {
                    String url = cameras.get(0);
                    if (TextUtils.isEmpty(url)) {

                    }
                }

            }
        }

    }

    @Override
    public void onDestroy() {

    }
}
