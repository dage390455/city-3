package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.amap.api.maps.model.LatLng;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.DeviceInfo;
import com.sensoro.common.server.bean.InspectionTaskDeviceDetail;
import com.sensoro.common.server.bean.MalfunctionDataBean;
import com.sensoro.common.server.bean.MalfunctionListInfo;
import com.sensoro.common.server.response.MalfunctionCountRsp;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.MalfunctionHistoryActivity;
import com.sensoro.smartcity.activity.ScanActivity;
import com.sensoro.smartcity.imainviews.IMalfunctionDetailActivityView;
import com.sensoro.smartcity.util.CityAppUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MalfunctionDetailActivityPresenter extends BasePresenter<IMalfunctionDetailActivityView> implements IOnCreate {
    private Activity mActivity;
    private MalfunctionListInfo mMalfunctionInfo;
    private LatLng destPosition;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        onCreate();
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

        String deviceSN = mMalfunctionInfo.getDeviceSN();
        if (TextUtils.isEmpty(deviceSN)) {
            deviceSN = mActivity.getString(R.string.device_number) + mActivity.getString(R.string.unknown);
        } else {
            deviceSN = mActivity.getString(R.string.device_number) + deviceSN;
        }

        getView().setDeviceSn(deviceSN);

        long createdTime = mMalfunctionInfo.getCreatedTime();
        getView().setMalfunctionStatus(mMalfunctionInfo.getMalfunctionStatus(), DateUtil.getStrTimeToday(mActivity, createdTime, 1));
        List<MalfunctionListInfo.RecordsBean> records = mMalfunctionInfo.getRecords();
        Collections.sort(records, new Comparator<MalfunctionListInfo.RecordsBean>() {
            @Override
            public int compare(MalfunctionListInfo.RecordsBean o1, MalfunctionListInfo.RecordsBean o2) {
                long b = o2.getUpdatedTime() - o1.getUpdatedTime();
                if (b > 0) {
                    return 1;
                } else if (b < 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        String description = mActivity.getString(R.string.unknown_malfunction);
        Map<String, MalfunctionDataBean> malfunctionData = mMalfunctionInfo.getMalfunctionData();
        if (malfunctionData != null) {
            String malfunctionType = mMalfunctionInfo.getMalfunctionType();
            if (malfunctionType != null) {
                MalfunctionDataBean malfunctionDataBean = malfunctionData.get(malfunctionType);
                if (malfunctionDataBean != null) {
                    description = malfunctionDataBean.getDescription();
                }

            }
        }
        getView().updateRcContent(records, description);
        long current = System.currentTimeMillis();
        final StringBuffer stringBuffer = new StringBuffer();
        RetrofitServiceHelper.getInstance().getMalfunctionCount(current - 3600 * 24 * 180 * 1000L, current, null, mMalfunctionInfo.getDeviceSN()).subscribeOn(Schedulers.io())
                .flatMap(new Function<MalfunctionCountRsp, ObservableSource<ResponseResult<List<DeviceInfo>>>>() {
                    @Override
                    public ObservableSource<ResponseResult<List<DeviceInfo>>> apply(MalfunctionCountRsp malfunctionCountRsp) throws Exception {
                        stringBuffer.append(malfunctionCountRsp.getCount());
                        return RetrofitServiceHelper.getInstance().getDeviceDetailInfoList(mMalfunctionInfo.getDeviceSN(), null, 1);
                    }

                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<DeviceInfo>>>(this) {

            @Override
            public void onCompleted(ResponseResult<List<DeviceInfo>> deviceInfoListRsp) {
                List<DeviceInfo> data1 = deviceInfoListRsp.getData();
                if (data1 != null && data1.size() > 0) {
                    DeviceInfo deviceInfo = data1.get(0);
                    if (deviceInfo != null && mMalfunctionInfo.getDeviceSN().equals(deviceInfo.getSn())) {
                        getView().setMalfunctionDetailConfirmVisible(true);
                    }
                }
                String count = stringBuffer.toString();
                getView().setMalfunctionCount(count);
                getView().dismissProgressDialog();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                    getView().toastShort(errorMsg);
                } else if (errorCode == 4013101 || errorCode == 4000013) {
                    //查找新设备
                    getView().setMalfunctionDetailConfirmVisible(false);
                } else {
                    //TODO 控制逻辑
                    getView().toastShort(errorMsg);
                    getView().setMalfunctionDetailConfirmVisible(false);
                }
                String count = stringBuffer.toString();
                getView().setMalfunctionCount(count);
                getView().dismissProgressDialog();
            }
        });
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
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
            if (CityAppUtils.doNavigation(mActivity, destPosition)) {
                return;
            }
        }
        getView().toastShort(mActivity.getString(R.string.not_obtain_location_infomation));
    }

    public void doMalfunctionHistory() {
        Intent intent = new Intent(mActivity, MalfunctionHistoryActivity.class);
        intent.putExtra(Constants.EXTRA_SENSOR_SN, mMalfunctionInfo.getDeviceSN());
        getView().startAC(intent);
    }

    public void doChangeDevice() {
        String deviceSN = mMalfunctionInfo.getDeviceSN();
        InspectionTaskDeviceDetail inspectionTaskDeviceDetail = new InspectionTaskDeviceDetail();
        inspectionTaskDeviceDetail.setSn(deviceSN);
        //
        Intent intent = new Intent(mActivity, ScanActivity.class);
        intent.putExtra(Constants.EXTRA_SCAN_ORIGIN_TYPE, Constants.TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE);
        intent.putExtra(Constants.EXTRA_INSPECTION_DEPLOY_OLD_DEVICE_INFO, inspectionTaskDeviceDetail);
        getView().startAC(intent);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case Constants.EVENT_DATA_DEPLOY_RESULT_FINISH:
                getView().finishAc();
                break;
            case Constants.EVENT_DATA_DEPLOY_RESULT_CONTINUE:
                if (data instanceof Integer) {
                    int resultCode = (Integer) data;
                    //TODO 判断结果控制界面是否消失
                    if (resultCode == Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS) {
                        getView().finishAc();
                    }
                }
                break;
            case Constants.EVENT_DATA_SCAN_LOGIN_SUCCESS:
                getView().finishAc();
                break;
        }
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }
}
