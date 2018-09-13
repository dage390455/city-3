package com.sensoro.smartcity.presenter;

import android.content.Context;
import android.content.Intent;

import com.sensoro.smartcity.activity.AlarmContactActivity;
import com.sensoro.smartcity.activity.DeployDeviceTagActivity;
import com.sensoro.smartcity.activity.NameAddressActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IDeployDeviceDetailActivityView;

public class DeployDeviceDetailActivityPresenter extends BasePresenter<IDeployDeviceDetailActivityView> {
    private Context mContext;

    @Override
    public void initData(Context context) {
        mContext = context;
    }

    @Override
    public void onDestroy() {

    }

    public void doNameAddress() {
        Intent intent = new Intent(mContext, NameAddressActivity.class);
        getView().startAC(intent);
    }

    public void doAlarmContact() {
        Intent intent = new Intent(mContext, AlarmContactActivity.class);
        getView().startAC(intent);
    }

    public void doTag() {
        Intent intent = new Intent(mContext, DeployDeviceTagActivity.class);
        getView().startAC(intent);
    }
}
