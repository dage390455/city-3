package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.DeployMapActivity;
import com.sensoro.smartcity.activity.DeployMapENActivity;
import com.sensoro.smartcity.activity.DeployMonitorSettingPhotoActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployRecordDetailActivityView;
import com.sensoro.smartcity.model.DeployAnalyzerModel;
import com.sensoro.smartcity.server.bean.DeployControlSettingData;
import com.sensoro.smartcity.server.bean.DeployRecordInfo;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.smartcity.widget.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.widget.imagepicker.ui.ImagePreviewDelActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeployRecordDetailActivityPresenter extends BasePresenter<IDeployRecordDetailActivityView>
        implements Constants {
    private Activity mActivity;
    private DeployRecordInfo mDeployRecordInfo;
    private DeployAnalyzerModel deployAnalyzerModel;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        mDeployRecordInfo = (DeployRecordInfo) mActivity.getIntent().getSerializableExtra(EXTRA_DEPLOY_RECORD_DETAIL);
        refreshUI();
    }

    private void initDeployMapModel() {
        List<Double> lonlat = mDeployRecordInfo.getLonlat();
        deployAnalyzerModel = new DeployAnalyzerModel();
        deployAnalyzerModel.mapSourceType = DEPLOY_MAP_SOURCE_TYPE_DEPLOY_RECORD;
        deployAnalyzerModel.deployType = TYPE_SCAN_DEPLOY_POINT_DISPLAY;
        if (lonlat != null) {
            deployAnalyzerModel.latLng.clear();
            deployAnalyzerModel.latLng.addAll(lonlat);
        }
        deployAnalyzerModel.signal = mDeployRecordInfo.getSignalQuality();

    }

    private void refreshUI() {
        if (mDeployRecordInfo != null) {
            getView().setSNTitle(mActivity.getString(R.string.device_number) + mDeployRecordInfo.getSn());
            getView().setDeviceName(mDeployRecordInfo.getDeviceName());
            getView().updateTagList(mDeployRecordInfo.getTags());
            getView().setDeployTime(DateUtil.getStrTime_ymd_hm_ss(mDeployRecordInfo.getCreatedTime()));
            getView().setForceDeployReason("测试强制部署原因");
            List<String> deployPics = mDeployRecordInfo.getDeployPics();
            if (deployPics != null && deployPics.size() > 0) {
                ArrayList<ScenesData> list = new ArrayList<>();
                for (String url : deployPics) {
                    ScenesData scenesData = new ScenesData();
                    scenesData.url = url;
                    list.add(scenesData);
                }
                getView().updateDeployPic(list);
            }
            ArrayList<DeployRecordInfo.NotificationBean> contacts = new ArrayList<>();
            if (mDeployRecordInfo.getNotification() != null) {
                contacts.add(mDeployRecordInfo.getNotification());
            }
            getView().updateContactList(contacts);
            if (mDeployRecordInfo.getLonlat() != null) {
                getView().setPositionStatus(1);
            } else {
                getView().setPositionStatus(0);
            }
            getView().refreshSingle(mDeployRecordInfo.getSignalQuality());
            String wxPhone = mDeployRecordInfo.getWxPhone();
            if (!TextUtils.isEmpty(wxPhone)) {
                getView().seDeployWeChat(wxPhone);
            }
            String deviceType = mDeployRecordInfo.getDeviceType();
            String deviceTypeName = WidgetUtil.getDeviceMainTypeName(deviceType);
            getView().setDeployDeviceRecordDeviceType(deviceTypeName);
            boolean isFire = DEVICE_CONTROL_DEVICE_TYPES.contains(deviceType);
            getView().setDeployDetailDeploySettingVisible(isFire);
            if (isFire) {
                //TODO 是否配置过电器火灾字段字段
                if (mDeployRecordInfo.getConfig() != null) {
                    DeployControlSettingData deployControlSettingData = mDeployRecordInfo.getConfig();
                    if (deployControlSettingData != null) {
                        //线径的判断，暂时不需要了
//                        if (deployControlSettingData.getWireDiameter() != null) {
//                            String formatDouble = WidgetUtil.getFormatDouble(deployControlSettingData.getWireDiameter(), 2);
////                            getView().setDeployDeviceDetailDeploySetting(mActivity.getString(R.string.had_setting_detail) + deployControlSettingData.getSwitchSpec() + "A" + " " + mActivity.getString(R.string.diameter) + ":" + formatDouble + "m㎡");
//
//
//                        } else {
//                            getView().setDeployDeviceDetailDeploySetting(mActivity.getString(R.string.had_setting_detail) + deployControlSettingData.getSwitchSpec() + "A");
//                        }
                        Integer switchSpec = deployControlSettingData.getSwitchSpec();
                        if (switchSpec != null) {
                            getView().setDeployDeviceDetailDeploySetting(String.format(Locale.CHINA,"%sA", switchSpec));
                            return;
                        }

                        //线材
                        Integer material = deployControlSettingData.getWireMaterial();
                        if (material != null) {
                            switch (material) {
                                case 0:
                                    getView().setDeployDeviceRecordMaterial(mActivity.getString(R.string.cu));
                                    break;
                                case 1:
                                    getView().setDeployDeviceRecordMaterial(mActivity.getString(R.string.al));
                                    break;
                            }
                        }

                        //线径
                        Double diameter = deployControlSettingData.getWireDiameter();
                        if (diameter != null) {
                            getView().setDeployDeviceRecordDiameter(diameter+"mm");
                        }
                    }
                }
            }
            getView().setDeployDeviceDetailDeploySetting(null);

        }

    }


    @Override
    public void onDestroy() {

    }

    public void doDeployPic() {
        List<String> deployPics = mDeployRecordInfo.getDeployPics();
        if (deployPics.size() > 0) {
            ArrayList<ImageItem> items = new ArrayList<>();
            for (String deployPic : deployPics) {
                ImageItem imageItem = new ImageItem();
                imageItem.isRecord = false;
                imageItem.fromUrl = true;
                imageItem.path = deployPic;
                items.add(imageItem);
            }
            Intent intent = new Intent(mActivity, DeployMonitorSettingPhotoActivity.class);
            intent.putExtra(EXTRA_JUST_DISPLAY_PIC, true);
            intent.putExtra(EXTRA_DEPLOY_TO_PHOTO, items);
            getView().startAC(intent);
        } else {
            getView().toastShort(mActivity.getString(R.string.no_photos_added));
        }
    }

    public void doFixedPoint() {
        Intent intent = new Intent();
        initDeployMapModel();
        if (AppUtils.isChineseLanguage()) {
            intent.setClass(mActivity, DeployMapActivity.class);
        } else {
            intent.setClass(mActivity, DeployMapENActivity.class);
        }
        intent.putExtra(EXTRA_DEPLOY_ANALYZER_MODEL, deployAnalyzerModel);
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
            intentPreview.putExtra(EXTRA_JUST_DISPLAY_PIC, true);
            getView().startACForResult(intentPreview, REQUEST_CODE_PREVIEW);
        } else {
            getView().toastShort(mActivity.getString(R.string.no_photos_added));
        }
    }
}
