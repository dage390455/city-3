package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.imagepicker.ImagePicker;
import com.sensoro.common.imagepicker.ui.ImagePreviewDelActivity;
import com.sensoro.common.model.DeployAnalyzerModel;
import com.sensoro.common.model.DeviceNotificationBean;
import com.sensoro.common.model.ImageItem;
import com.sensoro.common.server.bean.DeployControlSettingData;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.common.utils.WidgetUtil;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.DeployMapActivity;
import com.sensoro.smartcity.activity.DeployMapENActivity;
import com.sensoro.smartcity.imainviews.IOfflineDeployTaskDetailActivityView;
import com.sensoro.smartcity.widget.dialog.DeployRecordContactDialogUtils;

import java.util.ArrayList;
import java.util.List;

public class OfflineDeployTaskDetailActivityPresenter extends BasePresenter<IOfflineDeployTaskDetailActivityView> {
    private Activity mActivity;
    private DeployAnalyzerModel deployAnalyzerModel;
    private List<DeviceNotificationBean> deviceNotificationBeans;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        deployAnalyzerModel = (DeployAnalyzerModel) mActivity.getIntent().getSerializableExtra(Constants.EXTRA_DEPLOY_ANALYZER_MODEL);
        refreshUI();
    }

    private void initDeployMapModel() {
        deployAnalyzerModel.mapSourceType = Constants.DEPLOY_MAP_SOURCE_TYPE_DEPLOY_RECORD;
        deployAnalyzerModel.deployType = Constants.TYPE_SCAN_DEPLOY_POINT_DISPLAY;
    }

    private void refreshUI() {
        if (deployAnalyzerModel != null) {
            getView().setSNTitle(mActivity.getString(R.string.device_number) + deployAnalyzerModel.sn);
            getView().setDeviceName(deployAnalyzerModel.nameAndAddress);
            getView().updateTagList(deployAnalyzerModel.tagList);
            //TODO 时间
            getView().setDeployTime(DateUtil.getStrTime_ymd_hm_ss(deployAnalyzerModel.updatedTime));
            String forceReason = deployAnalyzerModel.forceReason;
            String signalQuality = deployAnalyzerModel.currentSignalQuality;
            if (TextUtils.isEmpty(forceReason)) {
                getView().setForceDeployReason(null);
            } else {
                String forceReasonStr = null;
                switch (forceReason) {
                    case "lonlat":
                        forceReasonStr = mActivity.getString(R.string.deploy_check_record_reason_nearby) + mActivity.getString(R.string.deploy_check_record_force_tip);
                        break;
                    case "config":
                        forceReasonStr = mActivity.getString(R.string.deploy_check_record_reason_config) + mActivity.getString(R.string.deploy_check_record_force_tip);
                        break;
                    case "signalQuality":
                        if ("bad".equals(signalQuality)) {
                            forceReasonStr = mActivity.getString(R.string.deploy_check_record_reason_signal) + mActivity.getString(R.string.s_bad) + mActivity.getString(R.string.deploy_check_record_force_tip);
                        } else {
                            forceReasonStr = mActivity.getString(R.string.deploy_check_record_reason_signal) + mActivity.getString(R.string.s_none) + mActivity.getString(R.string.deploy_check_record_force_tip);
                        }
                        break;
                    case "status":
                        Integer status = deployAnalyzerModel.status;
                        if (status != null) {
                            switch (status) {
                                case Constants.SENSOR_STATUS_ALARM:
                                    forceReasonStr = mActivity.getString(R.string.deploy_check_record_reason_status) + mActivity.getString(R.string.status_alarm_true) + mActivity.getString(R.string.deploy_check_record_force_tip);
                                    break;
                                case Constants.SENSOR_STATUS_MALFUNCTION:
                                    forceReasonStr = mActivity.getString(R.string.deploy_check_record_reason_status) + mActivity.getString(R.string.status_malfunction) + mActivity.getString(R.string.deploy_check_record_force_tip);
                                    break;
                                case Constants.SENSOR_STATUS_NORMAL:
                                case Constants.SENSOR_STATUS_LOST:
                                case Constants.SENSOR_STATUS_INACTIVE:
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                }
                getView().setForceDeployReason(forceReasonStr);
            }
            //TODO 部署人员
//            String deployStaff = deployAnalyzerModel.getDeployStaff();
//            if (!TextUtils.isEmpty(deployStaff)) {
//                getView().setDeployRecordDetailDeployStaff(deployStaff);
//            }
            List<String> deployPics = deployAnalyzerModel.imgUrls;
            if (deployPics != null && deployPics.size() > 0) {
                ArrayList<ScenesData> list = new ArrayList<>();
                for (String url : deployPics) {
                    ScenesData scenesData = new ScenesData();
                    scenesData.url = url;
                    list.add(scenesData);
                }
                getView().updateDeployPic(list);
            }
            //TODO 处理多联系人
//            deviceNotificationBeans = WidgetUtil.handleDeviceNotifications(deployAnalyzerModel.deployContactModelList);
//            if (deviceNotificationBeans.size() > 0) {
//                DeviceNotificationBean deviceNotificationBean = deviceNotificationBeans.get(0);
//                String contact = deviceNotificationBean.getContact();
//                String content = deviceNotificationBean.getContent();
//                getView().setFirstContact(contact + "(" + content + ")");
//            } else {
//                ArrayList<DeviceNotificationBean> contacts = new ArrayList<>();
//                DeviceNotificationBean notification = mDeployRecordInfo.getNotification();
//                if (notification != null) {
//                    contacts.add(notification);
//                }
//                deviceNotificationBeans = WidgetUtil.handleDeviceNotifications(contacts);
//                if (deviceNotificationBeans.size() > 0) {
//                    DeviceNotificationBean deviceNotificationBean = deviceNotificationBeans.get(0);
//                    String contact = deviceNotificationBean.getContact();
//                    String content = deviceNotificationBean.getContent();
//                    getView().setFirstContact(contact + "(" + content + ")");
//                }
//            }
//            getView().setTotalContact(deviceNotificationBeans.size());
            if (deployAnalyzerModel.latLng.size() == 2) {
                getView().setPositionStatus(1);
            } else {
                getView().setPositionStatus(0);
            }
            getView().refreshSingle(signalQuality);
            String wxPhone = deployAnalyzerModel.weChatAccount;
            if (!TextUtils.isEmpty(wxPhone)) {
                getView().seDeployWeChat(wxPhone);
            }
            String deviceType = deployAnalyzerModel.deviceType;
            String deviceTypeName = WidgetUtil.getDeviceMainTypeName(deviceType);
            getView().setDeployDeviceRecordDeviceType(deviceTypeName);
            //
            boolean isFire = Constants.DEVICE_CONTROL_DEVICE_TYPES.contains(deviceType);
            getView().setDeployDetailDeploySettingVisible(isFire);
            if (isFire) {
                DeployControlSettingData deployControlSettingData = deployAnalyzerModel.settingData;
                //TODO 是否配置过电器火灾字段字段
//                if (mDeployRecordInfo.getConfig() != null) {
//                    DeployControlSettingData deployControlSettingData = mDeployRecordInfo.getConfig();
//                    if (deployControlSettingData != null) {
//                        Integer switchSpec = deployControlSettingData.getSwitchSpec();
//                        if (switchSpec != null) {
//                            getView().setDeployDeviceDetailDeploySetting(String.format(Locale.CHINA, "%sA", switchSpec));
//                        }
//                        //线材
//                        Integer material = deployControlSettingData.getWireMaterial();
//                        if (material != null) {
//                            switch (material) {
//                                case 0:
//                                    getView().setDeployDeviceRecordMaterial(mActivity.getString(R.string.cu));
//                                    break;
//                                case 1:
//                                    getView().setDeployDeviceRecordMaterial(mActivity.getString(R.string.al));
//                                    break;
//                            }
//                        }
//                        //线径
//                        Double diameter = deployControlSettingData.getWireDiameter();
//                        if (diameter != null) {
//                            getView().setDeployDeviceRecordDiameter(diameter + "mm²");
//                        }
//                    }
//                }
                final String[] values = {"-", "-"};
                if (deployControlSettingData != null) {
                    Integer switchSpec = deployControlSettingData.getSwitchSpec();
                    if (switchSpec != null) {
                        values[0] = switchSpec + "A";
                    }
                }
                if (hasNesConfigInfo(deployControlSettingData)) {
                    //新数据
                    Integer transformer = deployControlSettingData.getTransformer();
                    if (transformer != null) {
                        values[1] = transformer + "A";
                    }
                    getView().setDeployDetailConfigInfo(mActivity.getString(R.string.actual_overcurrent_threshold) + ":" + values[0], mActivity.getString(R.string.device_detail_config_trans) + ":" + values[1]);
                } else {
                    //传统数据
                    getView().setDeployDetailConfigInfo(mActivity.getString(R.string.actual_overcurrent_threshold) + ":" + values[0], null);
                }
                getView().setDeployDetailDeploySettingVisible(true);
            } else {
                getView().setDeployDetailDeploySettingVisible(false);
            }
        }
    }

    private boolean hasNesConfigInfo(DeployControlSettingData deployControlSettingData) {
        if (deployControlSettingData != null) {
            List<DeployControlSettingData.wireData> inputList = deployControlSettingData.getInput();
            List<DeployControlSettingData.wireData> outputList = deployControlSettingData.getOutput();
            return inputList != null && inputList.size() > 0 && outputList != null && outputList.size() > 0;
        }
        return false;

    }

    @Override
    public void onDestroy() {

    }

    public void doFixedPoint() {
        Intent intent = new Intent();
        initDeployMapModel();
        if (AppUtils.isChineseLanguage()) {
            intent.setClass(mActivity, DeployMapActivity.class);
        } else {
            intent.setClass(mActivity, DeployMapENActivity.class);
        }
        intent.putExtra(Constants.EXTRA_DEPLOY_ANALYZER_MODEL, deployAnalyzerModel);
        getView().startAC(intent);
    }

    public void toPhotoDetail(int position, List<ScenesData> images) {
        if (images.size() > 0) {
            ArrayList<ImageItem> items = new ArrayList<>();
            for (ScenesData scenesData : images) {
                ImageItem imageItem = new ImageItem();
                imageItem.isRecord = false;
                imageItem.fromUrl = true;
                imageItem.path = scenesData.url;
                items.add(imageItem);
            }
            Intent intentPreview = new Intent(mActivity, ImagePreviewDelActivity.class);
            intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, items);
            intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
            intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
            intentPreview.putExtra(Constants.EXTRA_JUST_DISPLAY_PIC, true);
            getView().startACForResult(intentPreview, Constants.REQUEST_CODE_PREVIEW);
        } else {
            getView().toastShort(mActivity.getString(R.string.no_photos_added));
        }
    }

    public void goConfigDetail() {
        DeployControlSettingData settingData = deployAnalyzerModel.settingData;
        if (hasNesConfigInfo(settingData)) {
            //新界面
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.EXTRA_DEPLOY_CONFIGURATION_SETTING_DATA, settingData);
            startActivity(ARouterConstants.ACTIVITY_DEPLOY_RECORD_CONFIG_THREE_PHASE_ELECT_ACTIVITY, bundle, mActivity);
        } else {
            Bundle bundle = new Bundle();
            if (settingData != null) {
                bundle.putSerializable(Constants.EXTRA_DEPLOY_CONFIGURATION_SETTING_DATA, settingData);
            }
            startActivity(ARouterConstants.ACTIVITY_DEPLOY_RECORD_CONFIG_COMMON_ELECT_ACTIVITY, bundle, mActivity);
            //旧界面
        }
    }

    public void showContactDialog() {
        if (deviceNotificationBeans != null) {
            if (deviceNotificationBeans.size() > 1) {
                DeployRecordContactDialogUtils deployRecordContactDialogUtils = new DeployRecordContactDialogUtils(mActivity);
                deployRecordContactDialogUtils.show(deviceNotificationBeans);
            } else {
                //暂时先不提示
//            getView().toastShort(mActivity.getString(R.string.no_find_contact_phone_number));
            }
        }

    }
}
