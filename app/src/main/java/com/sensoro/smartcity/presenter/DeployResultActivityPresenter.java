package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployResultActivityView;
import com.sensoro.smartcity.model.DeployResultModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.util.DateUtil;

import org.greenrobot.eventbus.EventBus;

public class DeployResultActivityPresenter extends BasePresenter<IDeployResultActivityView> implements Constants {
    private Activity mContext;
    private DeployResultModel deployResultModel;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        deployResultModel = (DeployResultModel) mContext.getIntent().getSerializableExtra(EXTRA_DEPLOY_RESULT_MODEL);
        //
        switch (deployResultModel.resultCode) {
            case DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED:
            case DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT:
                //失败
                switch (deployResultModel.scanType) {
                    case TYPE_SCAN_DEPLOY_DEVICE_CHANGE:
                        getView().setDeployResultContinueText(mContext.getString(R.string.continue_to_replace));
                        getView().setDeployResultBackHomeText(mContext.getString(R.string.continue_inspection));
                        break;
                    case TYPE_SCAN_SIGNAL_CHECK:
                        getView().setStateTextViewVisible(false);
                        getView().setDeployResultContinueText(mContext.getString(R.string.rescan_code));
                        getView().setArrowsLeftVisible(true);
                        getView().setTitleText(mContext.getString(R.string.scan_code_failed));
                        break;
                }
                break;
            case DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS:
                //成功
                switch (deployResultModel.scanType) {
                    case TYPE_SCAN_DEPLOY_DEVICE_CHANGE:
                        getView().setDeployResultBackHomeText(mContext.getString(R.string.continue_inspection));
                        getView().setDeployResultContinueVisible(false);
                        break;
                    case TYPE_SCAN_SIGNAL_CHECK:
                        break;
                }
                break;
        }
        init();
    }

    private void init() {
        try {
            switch (deployResultModel.resultCode) {
                case DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED:
                    //失败
                    getView().setResultImageView(R.drawable.deploy_fail);
                    getView().setStateTextView(mContext.getString(R.string.failed));
                    if (!TextUtils.isEmpty(deployResultModel.sn)) {
                        getView().setSnTextView(deployResultModel.sn);
                    }
                    if (!TextUtils.isEmpty(deployResultModel.errorMsg)) {
                        getView().setTipsTextView(mContext.getResources().getString(R.string
                                .tips_deploy_station_failed));
                        getView().setDeployResultErrorInfo(mContext.getString(R.string.error) + "：" + deployResultModel.errorMsg);
                    }
                    break;
                case DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT:
                    //不在账户下
                    getView().setResultImageView(R.drawable.deploy_fail);
                    getView().setStateTextView(mContext.getString(R.string.failed));
                    if (!TextUtils.isEmpty(deployResultModel.sn)) {
                        getView().setSnTextView(deployResultModel.sn);
                    }

                    String text;
                    switch (deployResultModel.scanType) {
                        case TYPE_SCAN_SIGNAL_CHECK:
                            text = mContext.getString(R.string.device_exist_under_the_account);
                            break;
                        default:
                            text = mContext.getResources().getString(R.string
                                    .tips_deploy_not_exist);
                            break;
                    }
                    getView().setTipsTextView(text);
                    break;
                case DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS:
                    //成功
                    DeviceInfo deviceInfo = deployResultModel.deviceInfo;
                    String sn = deviceInfo.getSn().toUpperCase();
                    String name = deviceInfo.getName();
                    long updatedTime = deviceInfo.getUpdatedTime();
                    switch (deployResultModel.scanType) {
                        case TYPE_SCAN_DEPLOY_STATION:
                            //基站部署
                            deployStation(sn, name, deployResultModel.address, updatedTime);
                            break;
                        case TYPE_SCAN_DEPLOY_DEVICE:
                            //设备部署
                            deployDevice(sn, name, deployResultModel.address, updatedTime);
                            break;
                        case TYPE_SCAN_LOGIN:
                            break;
                        case TYPE_SCAN_DEPLOY_DEVICE_CHANGE:
                            //TODO 巡检设备更换
                            deployDevice(sn, name, deployResultModel.address, updatedTime);
                            break;
                        case TYPE_SCAN_INSPECTION:
                            //TODO 扫描巡检设备
                            break;
                        default:
                            break;
                    }
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            getView().toastShort(mContext.getResources().getString(R.string.tips_data_error));
        }

    }

    private void deployDevice(String sn, String name, String address, long updatedTime) {
        if (!TextUtils.isEmpty(address)) {
            getView().setAddressTextView(address);
        }
        getView().setContactAndSignalVisible(true);
        getView().setContactTextView((TextUtils.isEmpty(deployResultModel.contact) ? mContext.getString(R.string.no)
                : deployResultModel.contact) + "(" + (TextUtils.isEmpty
                (deployResultModel.phone) ?
                mContext.getString(R.string.no) : deployResultModel.phone) + ")");
        getView().refreshSignal(updatedTime, deployResultModel.deviceInfo.getSignal());
        getView().setResultImageView(R.drawable.deploy_succeed);
        getView().setStateTextView(mContext.getString(R.string.success));
        getView().setTipsTextView(mContext.getResources().getString(R.string.tips_deploy_success));
        getView().setSnTextView(sn);
        getView().setNameTextView(name);
        getView().setStatusTextView(mContext.getString(Constants.DEVICE_STATUS_ARRAY[deployResultModel.deviceInfo.getStatus()]));
//                // 修改长传时间
//                String lastUpdatedTime = deviceInfo.getLastUpdatedTime();
        if (updatedTime == -1 || updatedTime == 0) {
            getView().setUpdateTextViewVisible(false);
        } else {
            getView().setUpdateTextView(DateUtil
                    .getFullParseDatePoint(updatedTime));
        }
    }

    private void deployStation(String sn, String name, String address, long updatedTime) {
        if (!TextUtils.isEmpty(address)) {
            getView().setAddressTextView(address);
        }
        getView().setContactAndSignalVisible(false);
        getView().setResultImageView(R.drawable.deploy_succeed);
        getView().setStateTextView(mContext.getString(R.string.success));
        getView().setTipsTextView(mContext.getResources().getString(R.string
                .tips_deploy_station_success));
        getView().setSnTextView(sn);
        getView().setNameTextView(name);
        getView().setStatusTextView(mContext.getString(Constants.STATION_STATUS_ARRAY[deployResultModel.deviceInfo.getStatus() + 1]));
        if (updatedTime == -1 || updatedTime == 0) {
            getView().setUpdateTextViewVisible(false);
        } else {
            getView().setUpdateTextView(DateUtil.getFullParseDatePoint(updatedTime));
        }
    }

    public void gotoContinue() {
        EventData eventData = new EventData();
        if (deployResultModel.scanType == TYPE_SCAN_DEPLOY_DEVICE_CHANGE && deployResultModel.resultCode != DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS) {
            eventData.code = EVENT_DATA_DEPLOY_CHANGE_RESULT_CONTINUE;
            EventBus.getDefault().post(eventData);
            getView().finishAc();
            return;
        }
        eventData.code = EVENT_DATA_DEPLOY_RESULT_CONTINUE;
        if (deployResultModel.resultCode == DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS && deployResultModel.deviceInfo != null) {
            eventData.data = deployResultModel.deviceInfo;
        }
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }

    public void backHome() {
        EventData eventData = new EventData();
        if (deployResultModel.scanType == TYPE_SCAN_DEPLOY_DEVICE_CHANGE) {
            //todo 部署失败，返回巡检
            eventData.code = EVENT_DATA_DEPLOY_RESULT_CONTINUE;
            EventBus.getDefault().post(eventData);
            getView().finishAc();
        } else {
            eventData.code = EVENT_DATA_DEPLOY_RESULT_FINISH;
        }

        if (deployResultModel.resultCode == DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS && deployResultModel.deviceInfo != null) {
            eventData.data = deployResultModel.deviceInfo;
        }
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }

    @Override
    public void onDestroy() {

    }
}
