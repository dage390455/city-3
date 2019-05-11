package com.sensoro.smartcity.presenter;

import android.content.Context;

import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IAlarmCameraVideoDetailActivityView;

public class AlarmCameraVideoDetailActivityPresenter extends BasePresenter<IAlarmCameraVideoDetailActivityView> {
    private Context mActivity;

    @Override
    public void initData(Context context) {
        mActivity = context;
    }

    @Override
    public void onDestroy() {

    }

    public void doRefresh() {

    }

    public void doItemClick(int position) {


    }

    public void regainGetCameraState(String sn) {

    }
}
