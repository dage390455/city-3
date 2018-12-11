package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.DeployMapActivity;
import com.sensoro.smartcity.activity.DeployMonitorSettingPhotoActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployRecordDetailActivityView;
import com.sensoro.smartcity.model.DeployAnalyzerModel;
import com.sensoro.smartcity.server.bean.DeployRecordInfo;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.imagepicker.bean.ImageItem;

import java.util.ArrayList;
import java.util.List;

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
            getView().setSNTitle(mDeployRecordInfo.getSn());
            getView().setDeviceName(mDeployRecordInfo.getDeviceName());
            getView().updateTagList(mDeployRecordInfo.getTags());
            getView().setDeployTime(DateUtil.getStrTime_ymd_hm_ss(mDeployRecordInfo.getCreatedTime()));
            getView().setPicCount(mActivity.getString(R.string.added) + mDeployRecordInfo.getDeployPics().size() + mActivity.getString(R.string.images));
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
            String deviceTypeName = WidgetUtil.getDeviceTypeName(deviceType);
            getView().setDeployDeviceRecordDeviceType(mActivity.getString(R.string.deploy_device_type) + deviceTypeName);
            boolean isFire = DEVICE_CONTROL_DEVICE_TYPES.contains(deviceType);
            getView().setDeployDetailDeploySettingVisible(isFire);
            if (isFire) {
                //TODO 是否配置过电器火灾字段字段
                getView().setDeployDeviceDetailDeploySetting(false);
            }

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
        intent.setClass(mActivity, DeployMapActivity.class);
        intent.putExtra(EXTRA_DEPLOY_ANALYZER_MODEL, deployAnalyzerModel);
        getView().startAC(intent);
    }
}
