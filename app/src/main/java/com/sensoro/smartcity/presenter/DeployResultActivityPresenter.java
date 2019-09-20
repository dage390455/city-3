package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.DeployContactModel;
import com.sensoro.common.model.DeployResultModel;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.bean.DeployControlSettingData;
import com.sensoro.common.server.bean.DeviceTypeStyles;
import com.sensoro.common.server.bean.MergeTypeStyles;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.common.utils.LogUtils;
import com.sensoro.smartcity.R;
import com.sensoro.common.constant.Constants;
import com.sensoro.smartcity.constant.CityConstants;
import com.sensoro.smartcity.imainviews.IDeployResultActivityView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.sensoro.smartcity.constant.CityConstants.DEVICE_STATUS_ARRAY;
import static com.sensoro.smartcity.constant.CityConstants.DEVICE_STATUS_COLOR_ARRAY;


public class DeployResultActivityPresenter extends BasePresenter<IDeployResultActivityView> {
    private Activity mContext;
    private DeployResultModel deployResultModel;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        deployResultModel = (DeployResultModel) mContext.getIntent().getSerializableExtra(Constants.EXTRA_DEPLOY_RESULT_MODEL);
        //
        try {
            LogUtils.loge("deployResultModel signal : " + deployResultModel.signal);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        switch (deployResultModel.resultCode) {
            case Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED:
                getView().setDeployResultRightButtonText(mContext.getString(R.string.modify_deploy_info));
                getView().setDeployResultRightButtonTextBackground(mContext.getResources().getDrawable(R.drawable.shape_bg_corner_f34_shadow));
                getView().setDeployResultLeftButtonText(mContext.getString(R.string.deploy_result_back_home));
                break;
            case Constants.DEPLOY_RESULT_MODEL_CODE_SCAN_FAILED:
                getView().setTitleText(mContext.getString(R.string.scan_code_failed));
                getView().setStateTextViewVisible(false);
                switch (deployResultModel.scanType) {
                    case Constants.TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
                        getView().setDeployResultRightButtonText(mContext.getString(R.string.continue_to_replace));
                        getView().setDeployResultLeftButtonText(mContext.getString(R.string.continue_inspection));
                        break;
                    case Constants.TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                        //TODO 失败是返回到哪
                        getView().setDeployResultRightButtonText(mContext.getString(R.string.continue_to_replace));
                        getView().setDeployResultLeftButtonText(mContext.getString(R.string.back));
                        break;
                    case Constants.TYPE_SCAN_SIGNAL_CHECK:
                        getView().setDeployResultRightButtonText(mContext.getString(R.string.rescan_code));
                        break;
                }
                break;
            case Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT:
                //失败
//                getView().setTitleText(mContext.getString(R.string.scan_code_failed));
                getView().setStateTextViewVisible(false);
                switch (deployResultModel.scanType) {
                    case Constants.TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:

                        getView().setDeployResultRightButtonText(mContext.getString(R.string.continue_to_replace));
                        getView().setDeployResultLeftButtonText(mContext.getString(R.string.continue_inspection));
                        break;
                    case Constants.TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                        //TODO 失败是返回到哪
                        getView().setDeployResultRightButtonText(mContext.getString(R.string.continue_to_replace));
                        getView().setDeployResultLeftButtonText(mContext.getString(R.string.back));
                        break;
                    case Constants.TYPE_SCAN_SIGNAL_CHECK:
                        getView().setDeployResultRightButtonText(mContext.getString(R.string.rescan_code));
                        getView().setTitleText(mContext.getString(R.string.scan_code_failed));
                        break;
                    case Constants.EVENT_DATA_SEARCH_NAMEPLATE:
                        getView().setDeployResultRightButtonText(mContext.getString(R.string.rescan_code));
                        getView().setTitleText(mContext.getString(R.string.scan_code_failed));
                        break;
                    case Constants.EVENT_DATA_ADD_SENSOR_FROM_DEPLOY://铭牌部署扫码关联传感器

                    case Constants.TYPE_SCAN_NAMEPLATE_ASSOCIATE_DEVICE:
                        getView().setDeployResultRightButtonText(mContext.getString(R.string.continue_associate));
                        getView().setTitleText(mContext.getString(R.string.scan_code_failed));
                        break;


                }
                break;
            case Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS:
                //成功
                switch (deployResultModel.scanType) {
                    case Constants.TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
                        getView().setDeployResultLeftButtonText(mContext.getString(R.string.continue_inspection));
                        getView().setDeployResultRightButtonVisible(false);
                        break;
                    case Constants.TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                        //TODO 成功是返回
                        getView().setDeployResultLeftButtonText(mContext.getString(R.string.back));
                        getView().setDeployResultRightButtonVisible(false);
                        break;
                    case Constants.TYPE_SCAN_SIGNAL_CHECK:
                        break;
                }
                break;
        }
        init();
    }

    private void init() {
        try {
            getView().setResultSettingVisible(CityConstants.DEVICE_CONTROL_DEVICE_TYPES.contains(deployResultModel.deviceType));
            switch (deployResultModel.resultCode) {
                case Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED:
                    //失败
                    setDeployResultFailedDetail();
                    break;
                case Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT:
                    //不在账户下
                    getView().setResultImageView(R.drawable.deploy_fail);
                    getView().setStateTextView(mContext.getString(R.string.failed));
                    getView().setDeployResultTvStateTextColor(R.color.c_f34a4a);
                    getView().setDeployResultDividerVisible(false);
                    if (!TextUtils.isEmpty(deployResultModel.sn)) {
                        getView().setSnTextView(deployResultModel.sn);
                    }
                    String text;
                    switch (deployResultModel.scanType) {
                        case Constants.TYPE_SCAN_SIGNAL_CHECK:
                            text = mContext.getString(R.string.device_exist_under_the_account);
                            break;
                        default:
                            text = mContext.getResources().getString(R.string
                                    .tips_deploy_not_exist);
                            break;
                    }
                    getView().setTipsTextView(text, R.color.c_252525);
                    break;
                case Constants.DEPLOY_RESULT_MODEL_CODE_SCAN_FAILED:
                    getView().setResultImageView(R.drawable.deploy_fail);
                    getView().setStateTextView(mContext.getString(R.string.failed));
                    getView().setDeployResultTvStateTextColor(R.color.c_f34a4a);
                    getView().setDeployResultDividerVisible(false);
                    if (!TextUtils.isEmpty(deployResultModel.sn)) {
                        getView().setSnTextView(deployResultModel.sn);
                    }
                    getView().setTipsTextView(deployResultModel.errorMsg, R.color.c_a6a6a6);
                    break;
                case Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS:
                    //成功
                    setDeployResultSuccessDetail();
                    break;
                default:
                    getView().setResultImageView(R.drawable.deploy_fail);
                    getView().setStateTextView(mContext.getString(R.string.failed));
                    getView().setDeployResultTvStateTextColor(R.color.c_f34a4a);
                    getView().setDeployResultDividerVisible(false);
                    if (!TextUtils.isEmpty(deployResultModel.sn)) {
                        getView().setSnTextView(deployResultModel.sn);
                    }
                    getView().setTipsTextView(mContext.getResources().getString(R.string.unknown_error), R.color.c_a6a6a6);
                    break;
            }


        } catch (Exception e) {
            e.printStackTrace();
            getView().setResultImageView(R.drawable.deploy_fail);
            getView().setStateTextView(mContext.getString(R.string.failed));
            getView().setDeployResultTvStateTextColor(R.color.c_f34a4a);
            getView().setDeployResultDividerVisible(false);
            if (!TextUtils.isEmpty(deployResultModel.sn)) {
                getView().setSnTextView(deployResultModel.sn);
            }
            getView().setTipsTextView(mContext.getResources().getString(R.string.tips_data_error), R.color.c_a6a6a6);
        }

    }

    /**
     * 部署成功
     */
    private void setDeployResultSuccessDetail() {
        switch (deployResultModel.scanType) {
            //基站部署
            case Constants.TYPE_SCAN_DEPLOY_STATION:

                //铭牌部署
            case Constants.EVENT_DATA_ADD_SENSOR_FROM_DEPLOY://基站部署
                getView().setResultImageView(R.drawable.deploy_succeed);
                getView().setStateTextView(mContext.getString(R.string.success));
                getView().setDeployResultTvStateTextColor(R.color.c_1dbb99);
                switch (deployResultModel.scanType) {
                    case Constants.EVENT_DATA_ADD_SENSOR_FROM_DEPLOY:

                        getView().setTipsTextView(mContext.getResources().getString(R.string
                                .tips_deploy_nameplate_success), R.color.c_a6a6a6);
                        break;
                    //基站部署
                    case Constants.TYPE_SCAN_DEPLOY_STATION:
                        getView().setTipsTextView(mContext.getResources().getString(R.string
                                .tips_deploy_station_success), R.color.c_a6a6a6);

                        break;
                }


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
                //基站不展示状态
//                getView().setStatusTextView(mContext.getString(CityConstants.STATION_STATUS_ARRAY[deployResultModel.stationStatus + 1]),
//                        mContext.getResources().getColor(CityConstants.STATION_STATUS_COLOR_ARRAY[deployResultModel.stationStatus + 1]));
                if (deployResultModel.deployTime == null) {
                    getView().setUpdateTextView(DateUtil.getStrTimeToday(mContext, System.currentTimeMillis(), 0));
                } else {
                    getView().setUpdateTextView(DateUtil.getStrTimeToday(mContext, deployResultModel.deployTime, 0));
                }
//                if (deployResultModel.updateTime == -1 || deployResultModel.updateTime == 0) {
//                    getView().setUpdateTextViewVisible(false);
//                } else {
//                    getView().setUpdateTextView(DateUtil
//                            .getFullParseDatePoint(mContext, deployResultModel.updateTime));
//                }
                break;
            //设备部署/更换
            case Constants.TYPE_SCAN_DEPLOY_DEVICE:
            case Constants.TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case Constants.TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                //TODO 巡检设备更换
                getView().setResultImageView(R.drawable.deploy_succeed);
                getView().setStateTextView(mContext.getString(R.string.success));
                getView().setDeployResultTvStateTextColor(R.color.c_1dbb99);
                getView().setTipsTextView(mContext.getResources().getString(R.string.tips_deploy_success), R.color.c_a6a6a6);
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


                if (null != deployResultModel.deployContactModelList && deployResultModel.deployContactModelList.size() > 0) {

                    List<DeployContactModel> deployContactModelList = deployResultModel.deployContactModelList;

                    StringBuilder stringBuffer = new StringBuilder();

                    for (int i = 0; i < deployContactModelList.size(); i++) {
                        DeployContactModel model = deployContactModelList.get(i);
                        stringBuffer.append(model.name);
                        stringBuffer.append("(");
                        stringBuffer.append(model.phone);
                        stringBuffer.append(")");
                        if (i != deployContactModelList.size() - 1) {
                            stringBuffer.append("\n");
                        }

                    }
                    getView().setContactTextView(stringBuffer.toString());

                } else {

                    getView().setContactTextView(mContext.getString(R.string.no)
                            + "(" + mContext.getString(R.string.no) + ")");
                }


                getView().setWeChatTextView((TextUtils.isEmpty(deployResultModel.wxPhone) ?
                        mContext.getString(R.string.not_added) : deployResultModel.wxPhone));
                getView().refreshSignal(deployResultModel.updateTime, deployResultModel.signal);
                //去掉只认为正常的逻辑
//                if (deployResultModel.deviceStatus == 0 || deployResultModel.deviceStatus == 4) {
//                    getView().setStatusTextView(mContext.getString(DEVICE_STATUS_ARRAY[deployResultModel.deviceStatus]),
//                            mContext.getResources().getColor(DEVICE_STATUS_COLOR_ARRAY[deployResultModel.deviceStatus]));
//                } else {
//                    getView().setStatusTextView(mContext.getString(R.string.normal),
//                            mContext.getResources().getColor(R.color.c_1dbb99));
//                }
                try {
                    getView().setStatusTextView(mContext.getString(DEVICE_STATUS_ARRAY[deployResultModel.deviceStatus]),
                            mContext.getResources().getColor(DEVICE_STATUS_COLOR_ARRAY[deployResultModel.deviceStatus]));
                } catch (Exception e) {
                    e.printStackTrace();
                    getView().setStatusTextView(mContext.getString(R.string.normal),
                            mContext.getResources().getColor(R.color.c_1dbb99));
                }

                if (deployResultModel.deployTime == null) {
                    getView().setUpdateTextView(DateUtil.getStrTimeToday(mContext, System.currentTimeMillis(), 0));
                } else {
                    getView().setUpdateTextView(DateUtil.getStrTimeToday(mContext, deployResultModel.deployTime, 0));
                }
//                if (deployResultModel.updateTime == -1 || deployResultModel.updateTime == 0) {
//                    getView().setUpdateTextViewVisible(false);
//                } else {
//                    getView().setUpdateTextView(DateUtil
//                            .getFullParseDatePoint(mContext, deployResultModel.updateTime));
//                }
                if (CityConstants.DEVICE_CONTROL_DEVICE_TYPES.contains(deployResultModel.deviceType)) {
                    DeployControlSettingData settingData = deployResultModel.settingData;
                    if (settingData != null) {
                        getView().setDeployResultHasSetting(mContext.getString(R.string.had_setting));
                    } else {
                        getView().setDeployResultHasSetting(mContext.getString(R.string.not_setting));
                    }

                }
                checkMergeTypeConfigInfo();
                break;
            case Constants.TYPE_SCAN_DEPLOY_CAMERA:
                getView().setResultImageView(R.drawable.deploy_succeed);
                getView().setStateTextView(mContext.getString(R.string.success));
                getView().setDeployResultTvStateTextColor(R.color.c_1dbb99);
                getView().setTipsTextView("恭喜！摄像机点位部署成功", R.color.c_a6a6a6);
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
                //基站不展示状态
//                getView().setStatusTextView(mContext.getString(CityConstants.STATION_STATUS_ARRAY[deployResultModel.stationStatus + 1]),
//                        mContext.getResources().getColor(CityConstants.STATION_STATUS_COLOR_ARRAY[deployResultModel.stationStatus + 1]));
                if (deployResultModel.deployTime == null) {
                    getView().setUpdateTextView(DateUtil.getStrTimeToday(mContext, System.currentTimeMillis(), 0));
                } else {
                    getView().setUpdateTextView(DateUtil.getStrTimeToday(mContext, deployResultModel.deployTime, 0));
                }
                break;
            default:
                break;
        }
    }

    /**
     * 部署失败
     */
    private void setDeployResultFailedDetail() {
        switch (deployResultModel.scanType) {


            //铭牌部署失败
            case Constants.EVENT_DATA_ADD_SENSOR_FROM_DEPLOY:
                getView().setResultImageView(R.drawable.deploy_fail);
                getView().setTipsTextView(mContext.getResources().getString(R.string
                        .tips_deploy_station_failed), R.color.c_a6a6a6);
                getView().setStateTextView(mContext.getString(R.string.failed));
                getView().setDeployResultTvStateTextColor(R.color.c_f34a4a);
                if (!TextUtils.isEmpty(deployResultModel.sn)) {
                    getView().setSnTextView(deployResultModel.sn);
                }
                if (!TextUtils.isEmpty(deployResultModel.errorMsg)) {
                    getView().setTipsTextView(mContext.getString(R.string.reason) + "：" + deployResultModel.errorMsg, R.color.c_a6a6a6);
                }
                if (!TextUtils.isEmpty(deployResultModel.name)) {
                    getView().setNameTextView(deployResultModel.name);
                }
                if (!TextUtils.isEmpty(deployResultModel.address)) {
                    getView().setAddressTextView(deployResultModel.address);
                }
                getView().setContactAndSignalVisible(false);


                break;


            //基站部署
            case Constants.TYPE_SCAN_DEPLOY_STATION:
                getView().setResultImageView(R.drawable.deploy_fail);
                getView().setTipsTextView(mContext.getResources().getString(R.string
                        .tips_deploy_station_failed), R.color.c_a6a6a6);
                getView().setStateTextView(mContext.getString(R.string.failed));
                getView().setDeployResultTvStateTextColor(R.color.c_f34a4a);
                if (!TextUtils.isEmpty(deployResultModel.sn)) {
                    getView().setSnTextView(deployResultModel.sn);
                }
                if (!TextUtils.isEmpty(deployResultModel.errorMsg)) {
                    getView().setTipsTextView(mContext.getString(R.string.reason) + "：" + deployResultModel.errorMsg, R.color.c_a6a6a6);
                }
                if (!TextUtils.isEmpty(deployResultModel.name)) {
                    getView().setNameTextView(deployResultModel.name);
                }
                if (!TextUtils.isEmpty(deployResultModel.address)) {
                    getView().setAddressTextView(deployResultModel.address);
                }
                getView().setContactAndSignalVisible(false);
                // 基站不展示状态
//                getView().setStatusTextView(mContext.getString(CityConstants.STATION_STATUS_ARRAY[deployResultModel.stationStatus + 1]),
//                        mContext.getResources().getColor(CityConstants.STATION_STATUS_COLOR_ARRAY[deployResultModel.stationStatus + 1]));
                if (deployResultModel.deployTime == null) {
                    getView().setUpdateTextView(DateUtil.getStrTimeToday(mContext, System.currentTimeMillis(), 0));
                } else {
                    getView().setUpdateTextView(DateUtil.getStrTimeToday(mContext, deployResultModel.deployTime, 0));
                }
//                if (deployResultModel.updateTime == -1 || deployResultModel.updateTime == 0) {
//                    getView().setUpdateTextViewVisible(false);
//                } else {
//                    getView().setUpdateTextView(DateUtil
//                            .getFullParseDatePoint(mContext, deployResultModel.updateTime));
//                }
                break;
            //设备部署/更换
            case Constants.TYPE_SCAN_DEPLOY_DEVICE:
            case Constants.TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case Constants.TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                getView().setResultImageView(R.drawable.deploy_fail);
                getView().setStateTextView(mContext.getString(R.string.failed));
                getView().setDeployResultTvStateTextColor(R.color.c_f34a4a);
                getView().setTipsTextView(mContext.getResources().getString(R.string.tips_deploy_failed), R.color.c_a6a6a6);
                if (!TextUtils.isEmpty(deployResultModel.sn)) {
                    getView().setSnTextView(deployResultModel.sn);
                }
                if (!TextUtils.isEmpty(deployResultModel.errorMsg)) {
                    getView().setTipsTextView(mContext.getString(R.string.reason) + "：" + deployResultModel.errorMsg, R.color.c_a6a6a6);
                }
                if (!TextUtils.isEmpty(deployResultModel.name)) {
                    getView().setNameTextView(deployResultModel.name);
                }
                if (!TextUtils.isEmpty(deployResultModel.address)) {
                    getView().setAddressTextView(deployResultModel.address);
                }
                getView().setContactAndSignalVisible(true);


                if (null != deployResultModel.deployContactModelList && deployResultModel.deployContactModelList.size() > 0) {

                    List<DeployContactModel> deployContactModelList = deployResultModel.deployContactModelList;

                    StringBuilder stringBuffer = new StringBuilder();

                    for (int i = 0; i < deployContactModelList.size(); i++) {
                        DeployContactModel model = deployContactModelList.get(i);
                        stringBuffer.append(model.name);
                        stringBuffer.append("(");
                        stringBuffer.append(model.phone);
                        stringBuffer.append(")");
                        if (i != deployContactModelList.size() - 1) {
                            stringBuffer.append("\n");
                        }
                    }
                    getView().setContactTextView(stringBuffer.toString());

//                    MyLoaLat.logd("----"+stringBuffer.toString());
                } else {

                    getView().setContactTextView(mContext.getString(R.string.no)
                            + "(" + mContext.getString(R.string.no) + ")");
                }
//                getView().setContactTextView((TextUtils.isEmpty(deployResultModel.contact) ? mContext.getString(R.string.no)
//                        : deployResultModel.contact) + "(" + (TextUtils.isEmpty
//                        (deployResultModel.phone) ?
//                        mContext.getString(R.string.no) : deployResultModel.phone) + ")");
                getView().setWeChatTextView((TextUtils.isEmpty(deployResultModel.wxPhone) ?
                        mContext.getString(R.string.not_added) : deployResultModel.wxPhone));
//                if (deployResultModel.deviceStatus == 0 || deployResultModel.deviceStatus == 4) {
//                    getView().setStatusTextView(mContext.getString(DEVICE_STATUS_ARRAY[deployResultModel.deviceStatus]),
//                            mContext.getResources().getColor(DEVICE_STATUS_COLOR_ARRAY[deployResultModel.deviceStatus]));
//                } else {
//                    getView().setStatusTextView(mContext.getString(R.string.normal),
//                            mContext.getResources().getColor(R.color.c_1dbb99));
//                }
                try {
                    getView().setStatusTextView(mContext.getString(DEVICE_STATUS_ARRAY[deployResultModel.deviceStatus]),
                            mContext.getResources().getColor(DEVICE_STATUS_COLOR_ARRAY[deployResultModel.deviceStatus]));
                } catch (Exception e) {
                    e.printStackTrace();
                    getView().setStatusTextView(mContext.getString(R.string.normal),
                            mContext.getResources().getColor(R.color.c_1dbb99));
                }
                getView().refreshSignal(deployResultModel.updateTime, deployResultModel.signal);
                //TODO 当前的部署时间需要更换
                if (deployResultModel.deployTime == null) {
                    getView().setUpdateTextView(DateUtil.getStrTimeToday(mContext, System.currentTimeMillis(), 0));
                } else {
                    getView().setUpdateTextView(DateUtil.getStrTimeToday(mContext, deployResultModel.deployTime, 0));
                }
//                if (deployResultModel.updateTime == -1 || deployResultModel.updateTime == 0) {
//                    getView().setUpdateTextViewVisible(false);
//                } else {
//                    getView().setUpdateTextView(DateUtil
//                            .getFullParseDatePoint(mContext, deployResultModel.updateTime));
//                }
                if (CityConstants.DEVICE_CONTROL_DEVICE_TYPES.contains(deployResultModel.deviceType)) {
                    DeployControlSettingData settingData = deployResultModel.settingData;
                    if (settingData != null) {
                        getView().setDeployResultHasSetting(mContext.getString(R.string.had_setting));
                    } else {
                        getView().setDeployResultHasSetting(mContext.getString(R.string.not_setting));
                    }

                }
                break;
            case Constants.TYPE_SCAN_DEPLOY_CAMERA:
                getView().setResultImageView(R.drawable.deploy_fail);
                getView().setTipsTextView("很遗憾! 摄像头部署失败", R.color.c_a6a6a6);
                getView().setStateTextView(mContext.getString(R.string.failed));
                getView().setDeployResultTvStateTextColor(R.color.c_f34a4a);
                if (!TextUtils.isEmpty(deployResultModel.sn)) {
                    getView().setSnTextView(deployResultModel.sn);
                }
                if (!TextUtils.isEmpty(deployResultModel.errorMsg)) {
                    getView().setTipsTextView(mContext.getString(R.string.reason) + "：" + deployResultModel.errorMsg, R.color.c_a6a6a6);
                }
                if (!TextUtils.isEmpty(deployResultModel.name)) {
                    getView().setNameTextView(deployResultModel.name);
                }
                if (!TextUtils.isEmpty(deployResultModel.address)) {
                    getView().setAddressTextView(deployResultModel.address);
                }
                getView().setContactAndSignalVisible(false);
                if (deployResultModel.deployTime == null) {
                    getView().setUpdateTextView(DateUtil.getStrTimeToday(mContext, System.currentTimeMillis(), 0));
                } else {
                    getView().setUpdateTextView(DateUtil.getStrTimeToday(mContext, deployResultModel.deployTime, 0));
                }
                break;
            default:
                break;
        }
    }

    /**
     * 检查配置参数是否符合要求
     */
    private void checkMergeTypeConfigInfo() {
        DeviceTypeStyles configDeviceType = PreferencesHelper.getInstance().getConfigDeviceType(deployResultModel.deviceType);
        if (configDeviceType != null) {
            String mergeType = configDeviceType.getMergeType();
            if (!TextUtils.isEmpty(mergeType)) {
                MergeTypeStyles configMergeType = PreferencesHelper.getInstance().getConfigMergeType(mergeType);
                if (configMergeType != null) {
                    //有configMergeType认为设备配置信息满足要求
                    return;
                }
            }
        }
        //发送同更新当前的
        EventData eventData = new EventData();
        eventData.code = Constants.EVENT_DATA_CHECK_MERGE_TYPE_CONFIG_DATA;
        EventBus.getDefault().post(eventData);
    }

    public void doRightButton() {
        if(!isAttachedView())
            return;

        EventData eventData = new EventData();
        eventData.code = Constants.EVENT_DATA_DEPLOY_RESULT_CONTINUE;
        switch (deployResultModel.resultCode) {
            case Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS:
                if (deployResultModel.deviceInfo != null) {
                    eventData.data = deployResultModel.deviceInfo;
                }
                break;
            case Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED:
            case Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT:
                if ((deployResultModel.scanType == Constants.TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE || deployResultModel.scanType == Constants.TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE)) {
                    eventData.code = Constants.EVENT_DATA_DEPLOY_CHANGE_RESULT_CONTINUE;
                } else if (deployResultModel.scanType == Constants.TYPE_SCAN_DEPLOY_DEVICE) {
                    //直接返回上一个界面
                    getView().finishAc();
                    return;
                }
                break;

            default:
                break;
        }
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }

    public void doLeftButton() {
        EventData eventData = new EventData();
        if (deployResultModel.scanType == Constants.TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE || deployResultModel.scanType == Constants.TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE) {
            //todo 部署失败，返回巡检
            eventData.code = Constants.EVENT_DATA_DEPLOY_RESULT_CONTINUE;
            eventData.data = deployResultModel.resultCode;
            EventBus.getDefault().post(eventData);
        } else {
            eventData.code = Constants.EVENT_DATA_DEPLOY_RESULT_FINISH;
        }
        switch (deployResultModel.resultCode) {
            case Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS:
                if (deployResultModel.deviceInfo != null) {
                    eventData.data = deployResultModel.deviceInfo;
                }
                break;
            case Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED:
            case Constants.DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT:
            case Constants.EVENT_DATA_ADD_SENSOR_FROM_DEPLOY://铭牌部署扫码关联传感器

            case Constants.TYPE_SCAN_NAMEPLATE_ASSOCIATE_DEVICE:
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
