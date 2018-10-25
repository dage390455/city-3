package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.amap.api.maps.model.LatLng;
import com.lzy.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.activity.DeployMapActivity;
import com.sensoro.smartcity.activity.DeployMonitorSettingPhotoActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployRecordDetailActivityView;
import com.sensoro.smartcity.model.DeployMapModel;
import com.sensoro.smartcity.server.bean.DeployRecordInfo;
import com.sensoro.smartcity.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class DeployRecordDetailActivityPresenter extends BasePresenter<IDeployRecordDetailActivityView>
implements Constants {
    private Activity mActivity;
    private DeployRecordInfo mDeployRecordInfo;
    private DeployMapModel mDeployMapModel;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        mDeployRecordInfo = (DeployRecordInfo) mActivity.getIntent().getSerializableExtra(EXTRA_DEPLOY_RECORD_DETAIL);

        refreshUI();
    }

    private void initDeployMapModel() {
        List<Double> lonlat = mDeployRecordInfo.getLonlat();
        mDeployMapModel = new DeployMapModel();
        mDeployMapModel.deployType = TYPE_SCAN_DEPLOY_POINT_DISPLAY;
        mDeployMapModel.latLng = new LatLng(lonlat.get(1),lonlat.get(0));
        mDeployMapModel.signal = mDeployRecordInfo.getSignalQuality();

    }

    private void refreshUI() {
        if (mDeployRecordInfo != null) {
            getView().setInclueTitle(mDeployRecordInfo.getSn());
            getView().setDeviceName(mDeployRecordInfo.getDeviceName());
            getView().updateTagList(mDeployRecordInfo.getTags());
            getView().setDeployTime(DateUtil.getStrTime_ymd_hm(mDeployRecordInfo.getCreatedTime()));
            getView().setPicCount("已添加"+mDeployRecordInfo.getDeployPics().size()+"张照片");
            ArrayList<DeployRecordInfo.NotificationBean> contacts = new ArrayList<>();
            if(mDeployRecordInfo.getNotification()!=null){
                contacts.add(mDeployRecordInfo.getNotification());
            }
            getView().updateContactList(contacts);
            if(mDeployRecordInfo.getLonlat() != null){
                getView().setPositionStatus(1);
            }else{
                getView().setPositionStatus(0);
            }
            getView().refreshSingle(mDeployRecordInfo.getSignalQuality());

        }
    }

    @Override
    public void onDestroy() {

    }

    public void doDeployPic() {
        List<String> deployPics = mDeployRecordInfo.getDeployPics();
        if(deployPics.size()>0){
            ArrayList<ImageItem> items = new ArrayList<>();
            for (String deployPic : deployPics) {
                ImageItem imageItem = new ImageItem();
                imageItem.isRecord = false;
                imageItem.fromUrl = true;
                imageItem.path = deployPic;
                items.add(imageItem);
            }
            Intent intent = new Intent(mActivity, DeployMonitorSettingPhotoActivity.class);
            intent.putExtra(EXTRA_JUST_DISPLAY_PIC,true);
            intent.putExtra(EXTRA_DEPLOY_TO_PHOTO,items);
            getView().startAC(intent);
        }else{
            getView().toastShort("未添加照片");
        }
    }

    public void doFixedPoint() {
        Intent intent = new Intent();
        initDeployMapModel();
        intent.setClass(mActivity, DeployMapActivity.class);
        intent.putExtra(EXTRA_DEPLOY_TO_MAP, mDeployMapModel);
        intent.putExtra(EXTRA_DEPLOY_DISPLAY_MAP,true);
        getView().startAC(intent);
    }
}
