package com.sensoro.smartcity.widget.popup;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.model.LatLng;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.ImageItem;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.AlarmInfo;
import com.sensoro.common.server.bean.DeviceAlarmLogInfo;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.common.server.response.AlarmCloudVideoRsp;
import com.sensoro.common.server.response.AlarmCountRsp;
import com.sensoro.common.server.response.DeviceAlarmItemRsp;
import com.sensoro.common.server.response.ResponseBase;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.AlarmCameraLiveDetailActivity;
import com.sensoro.smartcity.activity.AlarmCameraVideoDetailActivity;
import com.sensoro.smartcity.activity.VideoPlayActivity;
import com.sensoro.smartcity.adapter.AlertLogRcContentAdapter;
import com.sensoro.smartcity.analyzer.AlarmPopupConfigAnalyzer;
import com.sensoro.smartcity.model.AlarmPopupModel;
import com.sensoro.smartcity.util.CityAppUtils;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.smartcity.widget.imagepicker.ui.ImageAlarmPhotoDetailActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AlarmLogPopUtils implements AlarmPopUtils.OnPopupCallbackListener,
        AlertLogRcContentAdapter.OnPhotoClickListener, Constants {

    private final FixHeightBottomSheetDialog mAlarmLogDialog;
    private final Activity mActivity;
    @BindView(R.id.ac_alert_log_tv_name)
    TextView acAlertLogTvName;
    @BindView(R.id.ac_alert_tv_sn)
    TextView acAlertTvSn;
    @BindView(R.id.iv_alarm_time_ac_alert)
    ImageView acAlertImvAlertIcon;
    @BindView(R.id.ac_alert_tv_alert_time)
    TextView acAlertTvAlertTime;
    @BindView(R.id.ac_alert_tv_alert_time_text)
    TextView acAlertTvAlertTimeText;
    @BindView(R.id.iv_alarm_count_ac_alert)
    ImageView acAlertImvAlertCountIcon;
    @BindView(R.id.ac_alert_tv_alert_count)
    TextView acAlertTvAlertCount;
    @BindView(R.id.ac_alert_tv_alert_count_text)
    TextView acAlertTvAlertCountText;
    @BindView(R.id.ac_alert_rc_content)
    RecyclerView acAlertRcContent;
    @BindView(R.id.ac_alert_tv_contact_owner)
    TextView acAlertTvContactOwner;
    @BindView(R.id.ac_alert_tv_quick_navigation)
    TextView acAlertTvQuickNavigation;
    @BindView(R.id.ac_alert_tv_alert_confirm)
    TextView acAlertTvAlertConfirm;
    @BindView(R.id.ac_alert_ll_bottom)
    LinearLayout acAlertLlBottom;
    @BindView(R.id.alarm_log_close)
    ImageView alarmLogClose;
    @BindView(R.id.tv_live_camera_count_ac_alert)
    TextView tvLiveCameraCountAcAlert;
    @BindView(R.id.ll_camera_live_ac_alert)
    LinearLayout llCameraLiveAcAlert;
    @BindView(R.id.tv_video_camera_count_ac_alert)
    TextView tvVideoCameraCountAcAlert;
    @BindView(R.id.ll_camera_video_ac_alert)
    LinearLayout llCameraVideoAcAlert;
    private AlarmPopUtils mAlarmPopUtils;
    private List<AlarmInfo.RecordInfo> mList = new ArrayList<>();
    private AlertLogRcContentAdapter alertLogRcContentAdapter;
    private DeviceAlarmLogInfo mDeviceAlarmLogInfo;
    private LatLng destPosition;
    private boolean isReConfirm = false;
    private ProgressUtils mProgressUtils;
    private AlarmCloudVideoRsp.DataBean mVideoBean;
    private AlarmPopupModel mAlarmPopupModel;

    public AlarmLogPopUtils(Activity activity) {
        mActivity = activity;
        mAlarmLogDialog = new FixHeightBottomSheetDialog(activity);
        View view = View.inflate(activity, R.layout.item_pop_alert_log, null);
        ButterKnife.bind(this, view);
        initRcContent();
        mAlarmLogDialog.setContentView(view);
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
    }

    public AlarmLogPopUtils(Activity activity, final DialogDisplayStatusListener listener) {
        mActivity = activity;
        mAlarmLogDialog = new FixHeightBottomSheetDialog(activity);
        View view = View.inflate(activity, R.layout.item_pop_alert_log, null);
        ButterKnife.bind(this, view);
        initRcContent();
        mAlarmLogDialog.setContentView(view);
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        mAlarmLogDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                listener.onDialogShow();
            }
        });
    }

    private void initRcContent() {
        alertLogRcContentAdapter = new AlertLogRcContentAdapter(mActivity);
        alertLogRcContentAdapter.setOnPhotoClickListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        acAlertRcContent.setLayoutManager(manager);
        acAlertRcContent.setAdapter(alertLogRcContentAdapter);

        int androiodScreenHeight = AppUtils.getAndroiodScreenHeight(mActivity);
        if (androiodScreenHeight == -1) {
            androiodScreenHeight = AppUtils.dp2px(mActivity, 220);
        }
        ViewGroup.LayoutParams layoutParams = acAlertRcContent.getLayoutParams();
        layoutParams.height = (int) (androiodScreenHeight * 0.46);
        acAlertRcContent.setLayoutParams(layoutParams);
    }

    public void show(AlarmPopupModel alarmPopupModel) {
        this.mAlarmPopupModel = alarmPopupModel;
        if (mAlarmLogDialog != null) {
            mAlarmLogDialog.show();
        }
    }

    public void refreshData(DeviceAlarmLogInfo deviceAlarmLogInfo) {
        mDeviceAlarmLogInfo = deviceAlarmLogInfo;

        String name = mDeviceAlarmLogInfo.getDeviceName();
        acAlertLogTvName.setText(TextUtils.isEmpty(name) ? mDeviceAlarmLogInfo.getDeviceSN() : name);

        String deviceSN = deviceAlarmLogInfo.getDeviceSN();
        if (TextUtils.isEmpty(deviceSN)) {
            deviceSN = mActivity.getString(R.string.device_number) + mActivity.getString(R.string.unknown);
        } else {
            deviceSN = mActivity.getString(R.string.device_number) + deviceSN;
        }
        acAlertTvSn.setText(deviceSN);

        if (PreferencesHelper.getInstance().getUserData().hasDeviceCameraList) {
            List<String> cameras = deviceAlarmLogInfo.getCameras();
            if (cameras != null && cameras.size() > 0) {
                llCameraLiveAcAlert.setVisibility(View.VISIBLE);
                tvLiveCameraCountAcAlert.setText(
                        String.format(Locale.ROOT, "%s%d%s", mActivity.getString(R.string.relation_camera)
                                , cameras.size(), mActivity.getString(R.string.upload_photo_dialog_append_title3)));
            } else {
                llCameraLiveAcAlert.setVisibility(View.GONE);
            }

        } else {
            llCameraVideoAcAlert.setVisibility(View.GONE);
        }


        acAlertTvAlertTime.setText(DateUtil.getStrTimeToday(mActivity, mDeviceAlarmLogInfo.getCreatedTime(), 1));

        acAlertTvAlertCount.setText(mDeviceAlarmLogInfo.getDisplayStatus() + 10 + "");

        int displayStatus = mDeviceAlarmLogInfo.getDisplayStatus();
        if (displayStatus == DISPLAY_STATUS_CONFIRM) {
            isReConfirm = true;
            acAlertTvAlertConfirm.setText(mActivity.getString(R.string.alarm_log_alarm_warn_confirm));
            acAlertTvAlertConfirm.setTextColor(mActivity.getResources().getColor(R.color.white));
            acAlertTvAlertConfirm.setBackgroundResource(R.drawable.shape_btn_corner_29c_bg_4dp);
        } else {
            isReConfirm = false;
            acAlertTvAlertConfirm.setText(mActivity.getString(R.string.confirming_again));
            acAlertTvAlertConfirm.setTextColor(mActivity.getResources().getColor(R.color.c_252525));
            acAlertTvAlertConfirm.setBackgroundResource(R.drawable.shape_bg_solid_fa_stroke_df_corner_4dp);
        }

        initRcContentData();

    }


    private void initRcContentData() {
        AlarmInfo.RecordInfo[] recordInfoArray = mDeviceAlarmLogInfo.getRecords();
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
        long current = System.currentTimeMillis();

        getCloudVideo();

        RetrofitServiceHelper.getInstance()
                .getAlarmCount(current - 3600 * 24 * 180 * 1000L, current, null,
                        mDeviceAlarmLogInfo.getDeviceSN())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<AlarmCountRsp>(null) {
                    @Override
                    public void onCompleted(AlarmCountRsp alarmCountRsp) {
                        int count = alarmCountRsp.getCount();
                        acAlertTvAlertCount.setText(count + "");
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        SensoroToast.getInstance().makeText(errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
        updateAlertLogContentAdapter(mList);
    }

    private void getCloudVideo() {
        if (!PreferencesHelper.getInstance().getUserData().hasDeviceCameraList) {
            llCameraVideoAcAlert.setVisibility(View.GONE);
            return;
        }
        String[] eventIds = {mDeviceAlarmLogInfo.get_id()};
        RetrofitServiceHelper.getInstance().getCloudVideo(eventIds)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<AlarmCloudVideoRsp>(null) {
                    @Override
                    public void onCompleted(AlarmCloudVideoRsp response) {
                        List<AlarmCloudVideoRsp.DataBean> data = response.getData();
                        if (data != null && data.size() > 0) {
                            mVideoBean = data.get(0);
                            List<AlarmCloudVideoRsp.DataBean.MediasBean> mMedias = mVideoBean.getMedias();
                            if (mMedias != null && mMedias.size() > 0) {
                                tvVideoCameraCountAcAlert.setText(String.format(Locale.ROOT, "%s%d%s",
                                        mActivity.getString(R.string.alarm_camera_video),
                                        mMedias.size(), mActivity.getString(R.string.video_unit_duan)));
                                llCameraVideoAcAlert.setVisibility(View.VISIBLE);
                            } else {
                                llCameraVideoAcAlert.setVisibility(View.GONE);
                            }
                        } else {
                            llCameraVideoAcAlert.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        llCameraVideoAcAlert.setVisibility(View.GONE);
                    }
                });
    }

    private void updateAlertLogContentAdapter(List<AlarmInfo.RecordInfo> recordInfoList) {
        alertLogRcContentAdapter.setData(recordInfoList);
        alertLogRcContentAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.alarm_log_close, R.id.ac_alert_tv_contact_owner, R.id.ac_alert_tv_quick_navigation,
            R.id.ac_alert_tv_alert_confirm, R.id.ll_camera_video_ac_alert, R.id.ll_camera_live_ac_alert})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.alarm_log_close:
                mAlarmLogDialog.dismiss();
                break;
            case R.id.ac_alert_tv_contact_owner:
                doContactOwner();
                break;
            case R.id.ac_alert_tv_quick_navigation:
                doNavigation();
                break;
            case R.id.ac_alert_tv_alert_confirm:
                doConfirm();
                break;
            case R.id.ll_camera_video_ac_alert:
                doVideo();
                break;
            case R.id.ll_camera_live_ac_alert:
                doLive();
                break;
        }
    }

    private void doLive() {
        Intent intent = new Intent(mActivity, AlarmCameraLiveDetailActivity.class);
        ArrayList<String> cameras = new ArrayList<>(mDeviceAlarmLogInfo.getCameras());
        intent.putExtra(Constants.EXTRA_ALARM_CAMERAS, cameras);
        mActivity.startActivity(intent);
    }

    private void doVideo() {
        Intent intent = new Intent(mActivity, AlarmCameraVideoDetailActivity.class);
        intent.putExtra(Constants.EXTRA_ALARM_CAMERA_VIDEO, mVideoBean);
        mActivity.startActivity(intent);
    }

    public void doContactOwner() {
        String tempNumber = null;
        outer:
        for (AlarmInfo.RecordInfo recordInfo : mList) {
            String type = recordInfo.getType();
            if ("sendVoice".equals(type)) {
                AlarmInfo.RecordInfo.Event[] phoneList = recordInfo.getPhoneList();
                for (AlarmInfo.RecordInfo.Event event : phoneList) {
                    String source = event.getSource();
                    String number = event.getNumber();
                    if (!TextUtils.isEmpty(number)) {
                        if ("attach".equals(source)) {
                            try {
                                LogUtils.loge("单独联系人：" + number);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                            tempNumber = number;
                            break outer;

                        } else if ("group".equals(source)) {
                            try {
                                LogUtils.loge("分组联系人：" + number);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                            tempNumber = number;
                            break;
                        } else if ("notification".equals(source)) {
                            try {
                                LogUtils.loge("账户联系人：" + number);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                            tempNumber = number;
                            break;
                        }

                    }

                }
            }
        }
        if (TextUtils.isEmpty(tempNumber)) {
            SensoroToast.getInstance().makeText(mActivity.getString(R.string.no_find_contact_phone_number), Toast.LENGTH_SHORT).show();
        } else {
            AppUtils.diallPhone(tempNumber, mActivity);
        }
    }

    private void doNavigation() {
        double[] deviceLonlat = mDeviceAlarmLogInfo.getDeviceLonlat();
        if (deviceLonlat != null && deviceLonlat.length > 1) {
            destPosition = new LatLng(deviceLonlat[1], deviceLonlat[0]);
            if (CityAppUtils.doNavigation(mActivity, destPosition)) {
                return;
            }
        }
        SensoroToast.getInstance().makeText(mActivity.getString(R.string.not_obtain_location_infomation), Toast.LENGTH_SHORT).show();
    }

    private void doConfirm() {
        mAlarmPopUtils = new AlarmPopUtils(mActivity);
        mAlarmPopUtils.setOnPopupCallbackListener(this);
        mAlarmPopUtils.show(mAlarmPopupModel);
    }


    @Override
    public void onPhotoItemClick(int position, List<ScenesData> scenesDataList) {
        ArrayList<ImageItem> items = new ArrayList<>();
        if (scenesDataList != null && scenesDataList.size() > 0) {
            for (ScenesData scenesData : scenesDataList) {
                ImageItem imageItem = new ImageItem();
                imageItem.fromUrl = true;
                if ("video".equals(scenesData.type)) {
                    imageItem.isRecord = true;
                    imageItem.thumbPath = scenesData.thumbUrl;
                    imageItem.path = scenesData.url;
                } else {
                    imageItem.path = scenesData.url;
                    imageItem.isRecord = false;
                }
                items.add(imageItem);
            }
            ImageItem imageItem = items.get(position);
            if (imageItem.isRecord) {
                Intent intent = new Intent();
                intent.setClass(mActivity, VideoPlayActivity.class);
                intent.putExtra("path_record", (Serializable) imageItem);
                intent.putExtra("video_del", true);
                mActivity.startActivity(intent);
            } else {
                //
                Intent intentPreview = new Intent(mActivity, ImageAlarmPhotoDetailActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, items);
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                mActivity.startActivity(intentPreview);
            }

        }
    }

    @Override
    public void onPopupCallback(AlarmPopupModel alarmPopupModel, List<ScenesData> scenesDataList) {
        mAlarmPopUtils.setUpdateButtonClickable(false);
        mProgressUtils.showProgress();
        Map<String, Integer> alarmPopupServerData = AlarmPopupConfigAnalyzer.createAlarmPopupServerData(alarmPopupModel);
        RetrofitServiceHelper.getInstance().doUpdatePhotosUrl(mDeviceAlarmLogInfo.get_id(), alarmPopupServerData, alarmPopupModel.securityRisksList,
                alarmPopupModel.mRemark, false, scenesDataList).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<DeviceAlarmItemRsp>(null) {

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        if (mAlarmLogDialog != null && mAlarmPopUtils != null) {
                            mProgressUtils.dismissProgress();
                            mAlarmPopUtils.dismiss();
                            mAlarmPopUtils.setUpdateButtonClickable(true);
                        }
                    }

                    @Override
                    public void onCompleted(DeviceAlarmItemRsp deviceAlarmItemRsp) {
                        if (mProgressUtils != null && mAlarmLogDialog != null) {
                            if (deviceAlarmItemRsp.getErrcode() == ResponseBase.CODE_SUCCESS) {

                                SensoroToast.getInstance().makeText(mActivity.getResources().
                                        getString(R.string.tips_commit_success), Toast.LENGTH_SHORT).show();
                                mDeviceAlarmLogInfo = deviceAlarmItemRsp.getData();
                                refreshData(mDeviceAlarmLogInfo);
                            } else {
                                SensoroToast.getInstance().makeText(mActivity.getResources().
                                        getString(R.string.tips_commit_failed), Toast.LENGTH_SHORT).show();
                            }
                            mProgressUtils.dismissProgress();
                            mAlarmPopUtils.dismiss();
                        }
                    }
                });
    }

    public interface DialogDisplayStatusListener {
        void onDialogShow();
    }
}
