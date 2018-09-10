package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageAlarmPhotoDetailActivity;
import com.sensoro.smartcity.activity.VideoPlayActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IAlertLogActivityView;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.util.DateUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AlertLogActivityPresenter extends BasePresenter<IAlertLogActivityView> implements Constants {
    private final List<AlarmInfo.RecordInfo> mList = new ArrayList<>();
    private DeviceAlarmLogInfo deviceAlarmLogInfo;
    private boolean isReConfirm = false;
    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        deviceAlarmLogInfo = (DeviceAlarmLogInfo) mContext.getIntent().getSerializableExtra(EXTRA_ALARM_INFO);
        isReConfirm = mContext.getIntent().getBooleanExtra(EXTRA_ALARM_IS_RE_CONFIRM, false);
        refreshData();
    }

    public void refreshData() {
        //
        String deviceName = deviceAlarmLogInfo.getDeviceName();
        getView().setDeviceNameTextView(TextUtils.isEmpty(deviceName) ? deviceAlarmLogInfo.getDeviceSN() : deviceName);
        getView().setCurrentAlarmState(deviceAlarmLogInfo.getDisplayStatus(), DateUtil.getFullParseDate(deviceAlarmLogInfo.getUpdatedTime()));
        //TODO 半年累计报警次数
        getView().setAlarmCount(deviceAlarmLogInfo.getDisplayStatus() + 10 + "");
//        getView().setDisplayStatus(deviceAlarmLogInfo.getDisplayStatus());
//        getView().setSensoroIv(deviceAlarmLogInfo.getSensorType());
        AlarmInfo.RecordInfo[] recordInfoArray = deviceAlarmLogInfo.getRecords();
        if (recordInfoArray != null) {
            mList.clear();
            for (int i = recordInfoArray.length - 1; i >= 0; i--) {
                mList.add(recordInfoArray[i]);
            }
            for (AlarmInfo.RecordInfo recordInfo : recordInfoArray) {
                if (recordInfo.getType().equals("recovery")) {
//                    getView().setStatusInfo("于" + DateUtil.getFullParseDate(recordInfo.getUpdatedTime()) + "恢复正常", R
//                            .color.sensoro_normal, R.drawable.shape_status_normal);
//                    break;
                } else {
//                    getView().setStatusInfo(mContext.getResources().getString(R.string.alarming), R.color.sensoro_alarm,
//                            R.drawable.shape_status_alarm);
                }
            }
        }
        getView().updateAlertLogContentAdapter(mList);
    }

    public void clickPhotoItem(int position, List<ScenesData> scenesDataList) {
        //
        ArrayList<ImageItem> items = new ArrayList<>();
        if (scenesDataList != null && scenesDataList.size() > 0) {
            for (ScenesData scenesData : scenesDataList) {
                ImageItem imageItem = new ImageItem();
                imageItem.fromUrl = true;
                if ("video".equals(scenesData.type)) {
                    imageItem.isRecord = true;
                    imageItem.recordPath = scenesData.url;
                    imageItem.path = scenesData.thumbUrl;
                } else {
                    imageItem.path = scenesData.url;
                    imageItem.isRecord = false;
                }
                items.add(imageItem);
            }
            ImageItem imageItem = items.get(position);
            if (imageItem.isRecord) {
                Intent intent = new Intent();
                intent.setClass(mContext, VideoPlayActivity.class);
                intent.putExtra("path_record", (Serializable) imageItem);
                intent.putExtra("video_del", true);
                getView().startAC(intent);
            } else {
                //
                Intent intentPreview = new Intent(mContext, ImageAlarmPhotoDetailActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, items);
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                getView().startAC(intentPreview);
            }

        }

    }

    @Override
    public void onDestroy() {

    }

    public void doContactOwner() {

    }

    public void doNavigation() {

    }
}
