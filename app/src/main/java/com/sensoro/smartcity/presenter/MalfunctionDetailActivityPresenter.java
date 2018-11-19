package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.amap.api.maps.model.LatLng;
import com.sensoro.libbleserver.ble.utils.LogUtils;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.MalfunctionHistoryActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IMalfunctionDetailActivityView;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.MalfunctionListInfo;
import com.sensoro.smartcity.server.response.AlarmCountRsp;
import com.sensoro.smartcity.server.response.MalfunctionCountRsp;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.DateUtil;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MalfunctionDetailActivityPresenter extends BasePresenter<IMalfunctionDetailActivityView> {
    private Activity mActivity;
    private MalfunctionListInfo mMalfunctionInfo;
    private LatLng destPosition;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        mMalfunctionInfo = (MalfunctionListInfo) mActivity.getIntent().getSerializableExtra(Constants.EXTRA_MALFUNCTION_INFO);

        if (mMalfunctionInfo != null) {
            refreshUI();
        }

    }

    private void refreshUI() {
        getView().showProgressDialog();
        String deviceName = mMalfunctionInfo.getDeviceName();
        if (TextUtils.isEmpty(deviceName)) {
            deviceName = mMalfunctionInfo.getDeviceSN();
        }
        getView().setDeviceNameText(deviceName);

        long createdTime = mMalfunctionInfo.getCreatedTime();
        getView().setMalfunctionStatus(mMalfunctionInfo.getMalfunctionStatus(),DateUtil.getStrTimeToday(createdTime,1));
        List<MalfunctionListInfo.RecordsBean> records = mMalfunctionInfo.getRecords();
        getView().updateRcContent(records);
        long current = System.currentTimeMillis();

        RetrofitServiceHelper.INSTANCE.getMalfunctionCount(current - 3600 * 24 * 180 * 1000L, current, null, mMalfunctionInfo.getDeviceSN()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<MalfunctionCountRsp>(this) {
            @Override
            public void onCompleted(MalfunctionCountRsp alarmCountRsp) {
                int count = alarmCountRsp.getCount();
                getView().setMalfunctionCount(count + "");
                getView().dismissProgressDialog();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });

    }


    @Override
    public void onDestroy() {

    }

    public void doContactOwner() {
        String tempNumber = mMalfunctionInfo.getDeviceNotification().getContent();
        if (TextUtils.isEmpty(tempNumber)) {
            getView().toastShort(mActivity.getString(R.string.no_find_contact_phone_number));
        } else {
            AppUtils.diallPhone(tempNumber, mActivity);
        }
    }

    public void doNavigation() {
        List<Double> deviceLonlat = mMalfunctionInfo.getDeviceLonlat();
        if (deviceLonlat != null && deviceLonlat.size() > 1) {
            destPosition = new LatLng(deviceLonlat.get(1), deviceLonlat.get(0));
            if (AppUtils.doNavigation(mActivity, destPosition)) {
                return;
            }
        }
        //todo 改成字符串
        getView().toastShort("未获取到位置信息");
    }

    public void doMalfunctionHistory() {
        Intent intent = new Intent(mActivity, MalfunctionHistoryActivity.class);
        intent.putExtra(Constants.EXTRA_SENSOR_SN,mMalfunctionInfo.getDeviceSN());
        getView().startAC(intent);
    }
}
