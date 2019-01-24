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
import com.sensoro.smartcity.server.bean.DeployControlSettingData;
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
                    case TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
                        getView().setDeployResultContinueText(mContext.getString(R.string.continue_to_replace));
                        getView().setDeployResultBackHomeText(mContext.getString(R.string.continue_inspection));
                        break;
                    case TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                        //TODO 失败是返回到哪
                        getView().setDeployResultContinueText(mContext.getString(R.string.continue_to_replace));
                        getView().setDeployResultBackHomeText(mContext.getString(R.string.back));
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
                    case TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
                        getView().setDeployResultBackHomeText(mContext.getString(R.string.continue_inspection));
                        getView().setDeployResultContinueVisible(false);
                        break;
                    case TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                        //TODO 成功是返回
                        getView().setDeployResultBackHomeText(mContext.getString(R.string.back));
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
            getView().setResultSettingVisible(DEVICE_CONTROL_DEVICE_TYPES.contains(deployResultModel.deviceType));
            switch (deployResultModel.resultCode) {
                case DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED:
                    //失败
                    setDeployResultFailedDetail();
                    break;
                case DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT:
                    //不在账户下
                    getView().setResultImageView(R.drawable.deploy_fail);
                    getView().setStateTextView(mContext.getString(R.string.failed));
                    getView().setDeployResultTvStateTextColor(R.color.c_f34a4a);
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
                    setDeployResultSuccessDetail();
                    break;
            }


        } catch (Exception e) {
            e.printStackTrace();
            getView().toastShort(mContext.getResources().getString(R.string.tips_data_error));
        }

    }

    private void setDeployResultSuccessDetail() {
        switch (deployResultModel.scanType) {
            //基站部署
            case TYPE_SCAN_DEPLOY_STATION:
                getView().setResultImageView(R.drawable.deploy_succeed);
                getView().setStateTextView(mContext.getString(R.string.success));
                getView().setDeployResultTvStateTextColor(R.color.c_29c093);
                getView().setTipsTextView(mContext.getResources().getString(R.string
                        .tips_deploy_station_success));
                if (!TextUtils.isEmpty(deployResultModel.sn)) {
                    getView().setSnTextView(deployResultModel.sn);
                }
                if (!TextUtils.isEmpty(deployResultModel.name)) {
                    getView().setNameTextView(deployResultModel.name);
                }
                if (!TextUtils.isEmpty(deployResultModel.address)) {
                    getView().setAddressTextView(deployResultModel.address);
                }

                getView().setContactAndSignalVisible(false);
                getView().setStatusTextView(mContext.getString(Constants.STATION_STATUS_ARRAY[deployResultModel.stationStatus + 1]));
                if (deployResultModel.updateTime == -1 || deployResultModel.updateTime == 0) {
                    getView().setUpdateTextViewVisible(false);
                } else {
                    getView().setUpdateTextView(DateUtil
                            .getFullParseDatePoint(mContext, deployResultModel.updateTime));
                }
                break;
            //设备部署/更换
            case TYPE_SCAN_DEPLOY_DEVICE:
            case TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                //TODO 巡检设备更换
                getView().setResultImageView(R.drawable.deploy_succeed);
                getView().setStateTextView(mContext.getString(R.string.success));
                getView().setDeployResultTvStateTextColor(R.color.c_29c093);
                getView().setTipsTextView(mContext.getResources().getString(R.string.tips_deploy_success));
                if (!TextUtils.isEmpty(deployResultModel.sn)) {
                    getView().setSnTextView(deployResultModel.sn);
                }
                if (!TextUtils.isEmpty(deployResultModel.name)) {
                    getView().setNameTextView(deployResultModel.name);
                }
                if (!TextUtils.isEmpty(deployResultModel.address)) {
                    getView().setAddressTextView(deployResultModel.address);
                }
                getView().setContactAndSignalVisible(true);
                getView().setContactTextView((TextUtils.isEmpty(deployResultModel.contact) ? mContext.getString(R.string.no)
                        : deployResultModel.contact) + "(" + (TextUtils.isEmpty
                        (deployResultModel.phone) ?
                        mContext.getString(R.string.no) : deployResultModel.phone) + ")");
                getView().setWeChatTextView((TextUtils.isEmpty(deployResultModel.wxPhone) ?
                        mContext.getString(R.string.not_added) : deployResultModel.wxPhone));
                getView().refreshSignal(deployResultModel.updateTime, deployResultModel.signal);

                getView().setStatusTextView(mContext.getString(Constants.DEVICE_STATUS_ARRAY[deployResultModel.deviceStatus]));
                if (deployResultModel.updateTime == -1 || deployResultModel.updateTime == 0) {
                    getView().setUpdateTextViewVisible(false);
                } else {
                    getView().setUpdateTextView(DateUtil
                            .getFullParseDatePoint(mContext, deployResultModel.updateTime));
                }
                if (DEVICE_CONTROL_DEVICE_TYPES.contains(deployResultModel.deviceType)) {
                    DeployControlSettingData settingData = deployResultModel.settingData;
                    if (settingData != null) {
                        getView().setDeployResultHasSetting(mContext.getString(R.string.had_setting));
                    } else {
                        getView().setDeployResultHasSetting(mContext.getString(R.string.not_setting));
                    }

                }
                break;
            default:
                break;
        }
    }

    private void setDeployResultFailedDetail() {
        switch (deployResultModel.scanType) {
            //基站部署
            case TYPE_SCAN_DEPLOY_STATION:
                getView().setResultImageView(R.drawable.deploy_fail);
                getView().setTipsTextView(mContext.getResources().getString(R.string
                        .tips_deploy_station_failed));
                getView().setStateTextView(mContext.getString(R.string.failed));
                getView().setDeployResultTvStateTextColor(R.color.c_f34a4a);
                if (!TextUtils.isEmpty(deployResultModel.sn)) {
                    getView().setSnTextView(deployResultModel.sn);
                }
                if (!TextUtils.isEmpty(deployResultModel.errorMsg)) {
                    getView().setDeployResultErrorInfo(mContext.getString(R.string.error) + "：" + deployResultModel.errorMsg);
                }
                if (!TextUtils.isEmpty(deployResultModel.name)) {
                    getView().setNameTextView(deployResultModel.name);
                }
                if (!TextUtils.isEmpty(deployResultModel.address)) {
                    getView().setAddressTextView(deployResultModel.address);
                }
                getView().setContactAndSignalVisible(false);
                getView().setStatusTextView(mContext.getString(Constants.STATION_STATUS_ARRAY[deployResultModel.stationStatus + 1]));
                if (deployResultModel.updateTime == -1 || deployResultModel.updateTime == 0) {
                    getView().setUpdateTextViewVisible(false);
                } else {
                    getView().setUpdateTextView(DateUtil
                            .getFullParseDatePoint(mContext, deployResultModel.updateTime));
                }
                break;
            //设备部署/更换
            case TYPE_SCAN_DEPLOY_DEVICE:
            case TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                getView().setResultImageView(R.drawable.deploy_fail);
                getView().setStateTextView(mContext.getString(R.string.failed));
                getView().setDeployResultTvStateTextColor(R.color.c_f34a4a);
                getView().setTipsTextView(mContext.getResources().getString(R.string.tips_deploy_failed));
                if (!TextUtils.isEmpty(deployResultModel.sn)) {
                    getView().setSnTextView(deployResultModel.sn);
                }
                if (!TextUtils.isEmpty(deployResultModel.errorMsg)) {
                    getView().setDeployResultErrorInfo(mContext.getString(R.string.error) + "：" + deployResultModel.errorMsg);
                }
                if (!TextUtils.isEmpty(deployResultModel.name)) {
                    getView().setNameTextView(deployResultModel.name);
                }
                if (!TextUtils.isEmpty(deployResultModel.address)) {
                    getView().setAddressTextView(deployResultModel.address);
                }
                getView().setContactAndSignalVisible(true);
                getView().setContactTextView((TextUtils.isEmpty(deployResultModel.contact) ? mContext.getString(R.string.no)
                        : deployResultModel.contact) + "(" + (TextUtils.isEmpty
                        (deployResultModel.phone) ?
                        mContext.getString(R.string.no) : deployResultModel.phone) + ")");
                getView().setWeChatTextView((TextUtils.isEmpty(deployResultModel.wxPhone) ?
                        mContext.getString(R.string.not_added) : deployResultModel.wxPhone));
                getView().setStatusTextView(mContext.getString(Constants.DEVICE_STATUS_ARRAY[deployResultModel.deviceStatus]));
                getView().refreshSignal(deployResultModel.updateTime, deployResultModel.signal);
                if (deployResultModel.updateTime == -1 || deployResultModel.updateTime == 0) {
                    getView().setUpdateTextViewVisible(false);
                } else {
                    getView().setUpdateTextView(DateUtil
                            .getFullParseDatePoint(mContext, deployResultModel.updateTime));
                }
                if (DEVICE_CONTROL_DEVICE_TYPES.contains(deployResultModel.deviceType)) {
                    DeployControlSettingData settingData = deployResultModel.settingData;
                    if (settingData != null) {
                        getView().setDeployResultHasSetting(mContext.getString(R.string.had_setting));
                    } else {
                        getView().setDeployResultHasSetting(mContext.getString(R.string.not_setting));
                    }

                }
                break;
            default:
                break;
        }
    }

    public void gotoContinue() {
        EventData eventData = new EventData();
        eventData.code = EVENT_DATA_DEPLOY_RESULT_CONTINUE;
        switch (deployResultModel.resultCode) {
            case DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS:
                if (deployResultModel.deviceInfo != null) {
                    eventData.data = deployResultModel.deviceInfo;
                }
                break;
            case DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED:
            case DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT:
                if ((deployResultModel.scanType == TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE || deployResultModel.scanType == TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE)) {
                    eventData.code = EVENT_DATA_DEPLOY_CHANGE_RESULT_CONTINUE;
                }
                break;
            default:
                break;
        }
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }

    public void backHome() {
        EventData eventData = new EventData();
        if (deployResultModel.scanType == TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE || deployResultModel.scanType == TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE) {
            //todo 部署失败，返回巡检
            eventData.code = EVENT_DATA_DEPLOY_RESULT_CONTINUE;
            eventData.data = deployResultModel.resultCode;
            EventBus.getDefault().post(eventData);
        } else {
            eventData.code = EVENT_DATA_DEPLOY_RESULT_FINISH;
        }
        switch (deployResultModel.resultCode) {
            case DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS:
                if (deployResultModel.deviceInfo != null) {
                    eventData.data = deployResultModel.deviceInfo;
                }
                break;
            case DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED:
            case DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT:
                break;
            default:
                break;
        }
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }

    @Override
    public void onDestroy() {

    }
}
