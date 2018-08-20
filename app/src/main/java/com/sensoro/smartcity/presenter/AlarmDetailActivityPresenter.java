package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageAlarmPhotoDetailActivity;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IAlarmDetailActivityView;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.server.response.DeviceAlarmItemRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.widget.popup.SensoroPopupAlarmView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AlarmDetailActivityPresenter extends BasePresenter<IAlarmDetailActivityView> implements Constants,
        SensoroPopupAlarmView.OnPopupCallbackListener {
    private final List<AlarmInfo.RecordInfo> mList = new ArrayList<>();
    private DeviceAlarmLogInfo deviceAlarmLogInfo;
    private boolean isReConfirm = false;

    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        deviceAlarmLogInfo = (DeviceAlarmLogInfo) mContext.getIntent().getSerializableExtra(EXTRA_ALARM_INFO);
        isReConfirm = mContext.getIntent().getBooleanExtra(EXTRA_ALARM_IS_RE_CONFIRM, false);
//        refreshData();
    }

    public void doBack() {
        EventData eventData = new EventData();
        eventData.code = EVENT_DATA_ALARM_DETAIL_RESULT;
        eventData.data = deviceAlarmLogInfo;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }

    public void refreshData() {
        //
        String deviceName = deviceAlarmLogInfo.getDeviceName();
        getView().setNameTextView(TextUtils.isEmpty(deviceName) ? deviceAlarmLogInfo.getDeviceSN() : deviceName);
        getView().setDateTextView(DateUtil.getFullParseDate(deviceAlarmLogInfo.getUpdatedTime()));

        getView().setDisplayStatus(deviceAlarmLogInfo.getDisplayStatus());
        getView().setSensoroIv(deviceAlarmLogInfo.getSensorType());
        AlarmInfo.RecordInfo[] recordInfoArray = deviceAlarmLogInfo.getRecords();
        if (recordInfoArray != null) {
            mList.clear();
            for (int i = recordInfoArray.length - 1; i >= 0; i--) {
                mList.add(recordInfoArray[i]);
            }
            for (AlarmInfo.RecordInfo recordInfo : recordInfoArray) {
                if (recordInfo.getType().equals("recovery")) {
                    getView().setStatusInfo("于" + DateUtil.getFullParseDate(recordInfo.getUpdatedTime()) + "恢复正常", R
                            .color.sensoro_normal, R.drawable.shape_status_normal);
                    break;
                } else {
                    getView().setStatusInfo(mContext.getResources().getString(R.string.alarming), R.color.sensoro_alarm,
                            R.drawable.shape_status_alarm);
                }
            }
        }
        getView().updateTimerShaftAdapter(mList);
    }

    public void showConfirmPopup() {
        getView().showConfirmPopup(isReConfirm);
    }

    @Override
    public void onPopupCallback(int statusResult, int statusType, int statusPlace, List<String> images, String remark) {
        getView().setUpdateButtonClickable(false);
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.doUpdatePhotosUrl(deviceAlarmLogInfo.get_id(), statusResult, statusType,
                statusPlace,
                remark, isReConfirm, images).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe
                        (new CityObserver<DeviceAlarmItemRsp>() {


                            @Override
                            public void onCompleted() {
                                getView().dismissProgressDialog();
                                getView().dismissConfirmPopup();
                            }

                            @Override
                            public void onNext(DeviceAlarmItemRsp deviceAlarmItemRsp) {
                                if (deviceAlarmItemRsp.getErrcode() == ResponseBase.CODE_SUCCESS) {
                                    getView().toastShort(mContext.getResources().getString(R.string
                                            .tips_commit_success));
                                    deviceAlarmLogInfo = deviceAlarmItemRsp.getData();
                                    refreshData();
                                } else {
                                    getView().toastShort(mContext.getResources().getString(R.string
                                            .tips_commit_failed));
                                }
                            }

                            @Override
                            public void onErrorMsg(int errorCode, String errorMsg) {
                                getView().dismissProgressDialog();
                                getView().dismissConfirmPopup();
                                getView().toastShort(errorMsg);
                                getView().setUpdateButtonClickable(true);
                            }
                        });
    }

    @Override
    public void onDestroy() {
        mList.clear();
    }

    public void clickPhotoItem(int position, List<String> images) {
        //
        ArrayList<ImageItem> items = new ArrayList<>();
        if (images != null && images.size() > 0) {
            for (String url : images) {
                ImageItem imageItem = new ImageItem();
                imageItem.path = url;
                imageItem.fromUrl = true;
                items.add(imageItem);
            }
            Intent intentPreview = new Intent(mContext, ImageAlarmPhotoDetailActivity.class);
            intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, items);
            intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
            intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
            getView().startAC(intentPreview);
        }

    }
}
