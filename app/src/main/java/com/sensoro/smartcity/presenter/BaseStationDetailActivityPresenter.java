package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.sensoro.smartcity.activity.FrequencyPointActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IBaseStationDetailActivityView;

public class BaseStationDetailActivityPresenter extends BasePresenter<IBaseStationDetailActivityView> {
    private Activity mContext;

    @Override
    public void initData(Context context) {

    }

    @Override
    public void onDestroy() {

    }


    public void switchCharType() {


    }

    public void imageClick() {


    }

    public void startNetWorkInfoActivity() {


    }

    public void startFrequencyPointActivity() {

        mContext.startActivity(new Intent(mContext, FrequencyPointActivity.class));

    }

    public void startSelfCheckActivity() {


    }


    public void doNavigation() {
//        List<Double> lonlat = mDeviceInfo.getLonlat();
//        if (lonlat.size() == 2) {
//            double v = lonlat.get(1);
//            double v1 = lonlat.get(0);
//            if (v == 0 || v1 == 0) {
//                getView().toastShort(mContext.getString(R.string.location_information_not_set));
//                return;
//            }
//        } else {
//            getView().toastShort(mContext.getString(R.string.location_information_not_set));
//            return;
//        }
//        Intent intent = new Intent();
//        if (AppUtils.isChineseLanguage()) {
//            intent.setClass(mContext, MonitorPointMapActivity.class);
//        } else {
//            intent.setClass(mContext, MonitorPointMapENActivity.class);
//        }
//        intent.putExtra(EXTRA_DEVICE_INFO, mDeviceInfo);
//        getView().startAC(intent);
    }
}