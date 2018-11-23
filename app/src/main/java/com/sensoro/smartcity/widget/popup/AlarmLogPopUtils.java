package com.sensoro.smartcity.widget.popup;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.VideoPlayActivity;
import com.sensoro.smartcity.adapter.AlertLogRcContentAdapter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.server.response.AlarmCountRsp;
import com.sensoro.smartcity.server.response.DeviceAlarmItemRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.toast.SensoroToast;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.smartcity.widget.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.widget.imagepicker.ui.ImageAlarmPhotoDetailActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AlarmLogPopUtils implements AlarmPopUtils.OnPopupCallbackListener,
        AlertLogRcContentAdapter.OnPhotoClickListener, Constants {

    private final FixHeightBottomSheetDialog mAlarmLogDialog;
    private final Activity mActivity;
    @BindView(R.id.ac_alert_log_tv_name)
    TextView acAlertLogTvName;
    @BindView(R.id.ac_alert_tv_time)
    TextView acAlertTvTime;
    @BindView(R.id.ac_alert_imv_alert_icon)
    ImageView acAlertImvAlertIcon;
    @BindView(R.id.ac_alert_tv_alert_time)
    TextView acAlertTvAlertTime;
    @BindView(R.id.ac_alert_tv_alert_time_text)
    TextView acAlertTvAlertTimeText;
    @BindView(R.id.ac_alert_ll_alert_time)
    LinearLayout acAlertLlAlertTime;
    @BindView(R.id.ac_alert_imv_alert_count_icon)
    ImageView acAlertImvAlertCountIcon;
    @BindView(R.id.ac_alert_tv_alert_count)
    TextView acAlertTvAlertCount;
    @BindView(R.id.ac_alert_tv_alert_count_text)
    TextView acAlertTvAlertCountText;
    @BindView(R.id.ac_alert_ll_alert_count)
    LinearLayout acAlertLlAlertCount;
    @BindView(R.id.ac_alert_ll_card)
    LinearLayout acAlertLlCard;
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
    private AlarmPopUtils mAlarmPopUtils;
    private List<AlarmInfo.RecordInfo> mList = new ArrayList<>();
    private AlertLogRcContentAdapter alertLogRcContentAdapter;
    private DeviceAlarmLogInfo mDeviceAlarmLogInfo;
    private LatLng destPosition;
    private boolean isReConfirm = false;
    private ProgressUtils mProgressUtils;

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

    public void show() {
        if (mAlarmLogDialog != null) {
            mAlarmLogDialog.show();
        }
    }

    public void refreshData(DeviceAlarmLogInfo deviceAlarmLogInfo) {
        mDeviceAlarmLogInfo = deviceAlarmLogInfo;

        String name = mDeviceAlarmLogInfo.getDeviceName();
        acAlertLogTvName.setText(TextUtils.isEmpty(name) ? mDeviceAlarmLogInfo.getDeviceSN() : name);

        acAlertTvAlertTime.setText(DateUtil.getStrTimeToday(mActivity,mDeviceAlarmLogInfo.getUpdatedTime(), 1));

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
        mProgressUtils.showProgress();
        RetrofitServiceHelper.INSTANCE.getAlarmCount(current - 3600 * 24 * 180 * 1000L, current, null, mDeviceAlarmLogInfo.getDeviceSN()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<AlarmCountRsp>() {
            @Override
            public void onCompleted(AlarmCountRsp alarmCountRsp) {
                int count = alarmCountRsp.getCount();
                acAlertTvAlertCount.setText(count + "");
                mProgressUtils.dismissProgress();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                SensoroToast.INSTANCE.makeText(errorMsg, Toast.LENGTH_SHORT).show();
                mProgressUtils.dismissProgress();
            }
        });
        updateAlertLogContentAdapter(mList);
    }

    private void updateAlertLogContentAdapter(List<AlarmInfo.RecordInfo> recordInfoList) {
        alertLogRcContentAdapter.setData(recordInfoList);
        alertLogRcContentAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.alarm_log_close, R.id.ac_alert_tv_contact_owner, R.id.ac_alert_tv_quick_navigation, R.id.ac_alert_tv_alert_confirm})
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
        }
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
                            LogUtils.loge("单独联系人：" + number);
                            tempNumber = number;
                            break outer;

                        } else if ("group".equals(source)) {
                            LogUtils.loge("分组联系人：" + number);
                            tempNumber = number;
                            break;
                        } else if ("notification".equals(source)) {
                            LogUtils.loge("账户联系人：" + number);
                            tempNumber = number;
                            break;
                        }

                    }

                }
            }
        }
        if (TextUtils.isEmpty(tempNumber)) {
            SensoroToast.INSTANCE.makeText(mActivity.getString(R.string.no_find_contact_phone_number), Toast.LENGTH_SHORT).show();
        } else {
            AppUtils.diallPhone(tempNumber, mActivity);
        }
    }

    private void doNavigation() {
        double[] deviceLonlat = mDeviceAlarmLogInfo.getDeviceLonlat();
        if (deviceLonlat != null && deviceLonlat.length > 1) {
            destPosition = new LatLng(deviceLonlat[1], deviceLonlat[0]);
            if (AppUtils.doNavigation(mActivity, destPosition)) {
                return;
            }
        }
        SensoroToast.INSTANCE.makeText(mActivity.getString(R.string.not_obtain_location_infomation), Toast.LENGTH_SHORT).show();
    }

    private void doConfirm() {
        mAlarmPopUtils = new AlarmPopUtils(mActivity);
        mAlarmPopUtils.setOnPopupCallbackListener(this);
        mAlarmPopUtils.show();
    }

    @Override
    public void onPopupCallback(int statusResult, int statusType, int statusPlace, List<ScenesData> scenesDataList, String remark) {
        mAlarmPopUtils.setUpdateButtonClickable(false);
        mProgressUtils.showProgress();
        RetrofitServiceHelper.INSTANCE.doUpdatePhotosUrl(mDeviceAlarmLogInfo.get_id(), statusResult, statusType,
                statusPlace,
                remark, isReConfirm, scenesDataList).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe
                        (new CityObserver<DeviceAlarmItemRsp>() {


                            @Override
                            public void onCompleted(DeviceAlarmItemRsp deviceAlarmItemRsp) {
                                if (deviceAlarmItemRsp.getErrcode() == ResponseBase.CODE_SUCCESS) {
                                    SensoroToast.INSTANCE.makeText(mActivity.getResources().
                                            getString(R.string.tips_commit_success), Toast.LENGTH_SHORT).show();
                                    mDeviceAlarmLogInfo = deviceAlarmItemRsp.getData();
                                    refreshData(mDeviceAlarmLogInfo);
                                } else {
                                    SensoroToast.INSTANCE.makeText(mActivity.getResources().
                                            getString(R.string.tips_commit_failed), Toast.LENGTH_SHORT).show();
                                }
                                mProgressUtils.dismissProgress();
                                mAlarmPopUtils.dismiss();
                            }

                            @Override
                            public void onErrorMsg(int errorCode, String errorMsg) {
                                mProgressUtils.dismissProgress();
                                mAlarmPopUtils.dismiss();
                                mAlarmPopUtils.setUpdateButtonClickable(true);
                            }
                        });
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

    public interface DialogDisplayStatusListener {
        void onDialogShow();
    }
}
