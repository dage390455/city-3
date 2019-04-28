package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.CameraPersonLocusActivity;
import com.sensoro.smartcity.adapter.model.DeviceCameraFacePicListModel;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ICameraDetailActivityView;
import com.sensoro.smartcity.model.CalendarDateModel;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceCameraFacePic;
import com.sensoro.smartcity.server.bean.DeviceCameraHistoryBean;
import com.sensoro.smartcity.server.response.DeviceCameraFacePicListRsp;
import com.sensoro.smartcity.server.response.DeviceCameraHistoryRsp;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.widget.popup.CalendarPopUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CameraDetailActivityPresenter extends BasePresenter<ICameraDetailActivityView> implements CalendarPopUtils.OnCalendarPopupCallbackListener {
    private Activity mActivity;
    private String cid;
    private String minId = null;
    private String yearMonthDate;
    private String url;
    private long startDateTime;
    private long endDateTime;
    private CalendarPopUtils mCalendarPopUtils;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        Intent intent = mActivity.getIntent();
        url = "http://wdquan-space.b0.upaiyun.com/VIDEO/2018/11/22/ae0645396048_hls_time10.m3u8";
        if (intent != null) {
            cid = intent.getStringExtra("cid");
            url = intent.getStringExtra("hls");
        }


        getView().initVideoOption(url);
        requestData(cid, Constants.DIRECTION_DOWN);
        mCalendarPopUtils = new CalendarPopUtils(mActivity);
        mCalendarPopUtils.setOnCalendarPopupCallbackListener(this);
    }

    private void getLastCoverImage(String lastCover) {
        Glide.with(mActivity).load(lastCover).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                BitmapDrawable bitmapDrawable = new BitmapDrawable(resource);
                getView().setImage(bitmapDrawable);
            }
        });
    }

    private void requestData(String cid, final int direction) {
        if(direction == Constants.DIRECTION_DOWN){
            minId = null;
            getView().initVideoOption(url);
        }
        ArrayList<String> strings = new ArrayList<>();
        strings.add(cid);
        long currentTimeMillis = System.currentTimeMillis();
        String startTime;
        String endTime;
        if (startDateTime == 0|| endDateTime == 0) {
            startTime = String.valueOf(currentTimeMillis - 24 * 60 * 60 * 30 * 1000L);
            endTime = String.valueOf(currentTimeMillis);
        }else{
            startTime = String.valueOf(startDateTime);
            endTime = String.valueOf(endDateTime);
        }
        RetrofitServiceHelper.getInstance().getDeviceCameraFaceList(strings, null, 20,minId,startTime, endTime)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceCameraFacePicListRsp>(null) {
            @Override
            public void onCompleted(final DeviceCameraFacePicListRsp deviceCameraFacePicListRsp) {
                if (direction == Constants.DIRECTION_DOWN) {
                    yearMonthDate = null;
                    getView().setLiveState(true);
                    getView().clearAdapterPreModel();
                    refreshData(deviceCameraFacePicListRsp,null);
                }else if(direction == Constants.DIRECTION_UP){
                    List<DeviceCameraFacePic> data = deviceCameraFacePicListRsp.getData();
                    if (data == null || data.size() <1) {
                        getView().onPullRefreshCompleteNoMoreData();
                        return;
                    }
                    refreshData(deviceCameraFacePicListRsp,getView().getRvListData());
                }

                getView().dismissProgressDialog();

            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
            }
        });
    }


    private void refreshData(DeviceCameraFacePicListRsp deviceCameraFacePicListRsp,List<DeviceCameraFacePicListModel> oldList) {
        ArrayList<DeviceCameraFacePicListModel> cameraLists = new ArrayList<>();

        if (oldList != null) {
            cameraLists.addAll(oldList);
        }
        List<DeviceCameraFacePic> data = deviceCameraFacePicListRsp.getData();
        if (data!= null && data.size() > 0) {
            minId = data.get(data.size()-1).getId();
            String currentDate = DateUtil.getDate(System.currentTimeMillis());
            for (DeviceCameraFacePic datum : data) {
                long l = 0;
                try {
                    l = Long.parseLong(datum.getCaptureTime());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    //时间错误，把这项数据丢弃
//                        time = mActivity.getString(R.s)
                    continue;
                }


                String ymd = DateUtil.getDate(l);
                if (currentDate.equals(ymd)) {
                    ymd = "今天";
                }else if(isYesterday(currentDate,ymd)){
                    ymd = "昨天";
                }
                if (yearMonthDate == null) {
                    yearMonthDate = ymd;
                    DeviceCameraFacePicListModel model = new DeviceCameraFacePicListModel();
                    model.time = ymd;
                    model.pics = null;
                    cameraLists.add(model);
                }else if(!yearMonthDate.equals(ymd)){
                    yearMonthDate = ymd;
                    DeviceCameraFacePicListModel model = new DeviceCameraFacePicListModel();
                    model.time = ymd;
                    model.pics = null;
                    cameraLists.add(model);
                }


                String time = DateUtil.getStrTime_ymd_hm(l);
                boolean isAdded = false;
                for (DeviceCameraFacePicListModel cameraList : cameraLists) {
                    if (cameraList.time.equals(time)) {
                        isAdded = true;
                        cameraList.pics.add(datum);
                        break;
                    }
                }
                if (!isAdded) {
                    DeviceCameraFacePicListModel model = new DeviceCameraFacePicListModel();
                    model.time = time;
                    model.pics.add(datum);
                    cameraLists.add(model);
                }
            }

            cameraLists.remove(0);

            if (isAttachedView()) {
                getView().onPullRefreshComplete();
                getView().updateCameraList(cameraLists);
                if (oldList == null) {
                    doDateTime(0);
                }
            }
        }

    }

    private boolean isYesterday(String currentDate, String ymd) {
        String[] currentSplit = currentDate.split("-");
        String[] ymdSplit = ymd.split("-");
        if (currentSplit.length == 3 && ymdSplit.length == 3) {
            try {
                int currentInt = Integer.parseInt(currentSplit[2]);
                int ymdInt = Integer.parseInt(ymdSplit[2]);

                return currentInt - ymdInt == 1 && currentSplit[1].equals(ymdSplit[1] )&& currentSplit[0].equals(ymdSplit[0]);

            } catch (NumberFormatException e) {
                e.printStackTrace();
                return false;
            }

        }

        return false;
    }

    @Override
    public void onDestroy() {
    }

    public void onCameraItemClick(final int index) {
        List<DeviceCameraFacePicListModel> rvListData = getView().getRvListData();
        if (rvListData != null) {
            getView().showProgressDialog();
            DeviceCameraFacePicListModel model = rvListData.get(index);
            Long min = 0L;
            Long max = 0L;
            if (model.pics != null && model.pics.size() > 0) {
                for (DeviceCameraFacePic pic : model.pics) {
                    String captureTime = pic.getCaptureTime();
                    try {
                        long capTime = Long.parseLong(captureTime);
                        if (min == 0) {
                            min = capTime;
                        }else{
                            min = Math.min(min,capTime);
                        }

                        max = Math.max(max,capTime);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                DeviceCameraFacePic deviceCameraFacePic = model.pics.get(0);

            }else{
                getView().toastShort("未获取到数据");
                return;
            }


            min = min / 1000;
            String beginTime = String.valueOf(min - 15);
            String endTime = String.valueOf(max/1000 + 15);

            RetrofitServiceHelper.getInstance().getDeviceCameraPlayHistoryAddress(cid, beginTime, endTime, null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceCameraHistoryRsp>(null) {
                @Override
                public void onCompleted(DeviceCameraHistoryRsp deviceCameraHistoryRsp) {
                    List<DeviceCameraHistoryBean> data = deviceCameraHistoryRsp.getData();
                    if (data != null && data.size() > 0) {
                        DeviceCameraHistoryBean deviceCameraHistoryBean = data.get(0);
                        String url1 = deviceCameraHistoryBean.getUrl();
                        if (isAttachedView()) {
                            getView().startPlayLogic(url1);
                        }

                    }

                    if (isAttachedView()) {
                        getView().dismissProgressDialog();
                    }
                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    if (isAttachedView()) {
                        getView().playError(index);
                        getView().dismissProgressDialog();
                    }
                }
            });

        }
    }

    public void doCalendar(LinearLayout root) {
        long temp_startTime = -1;
        long temp_endTime = -1;
        if (getView().isSelectedDateLayoutVisible()) {
            temp_startTime = startDateTime;
            temp_endTime = endDateTime;
        }
        mCalendarPopUtils.show(root, temp_startTime, temp_endTime);


    }

    public void doDateTime(int firstPosition) {
        DeviceCameraFacePicListModel model = getView().getItemData(firstPosition);
        if (model.pics == null) {
            getView().setDateTime(model.time);
        }else{
            String currentDate = DateUtil.getDate(System.currentTimeMillis());
            long l = 0;
            try {
                l = Long.parseLong(model.pics.get(0).getCaptureTime());
                String ymd = DateUtil.getDate(l);
                if (currentDate.equals(ymd)) {
                    ymd = "今天";
                }else if(isYesterday(currentDate,ymd)){
                    ymd = "昨天";
                }
                getView().setDateTime(ymd);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void doRefresh() {
        requestData(cid,Constants.DIRECTION_DOWN);
    }

    public void doLoadMore() {
        requestData(cid,Constants.DIRECTION_UP);
    }

    public void doLive() {
        getView().initVideoOption(url);
    }

    @Override
    public void onCalendarPopupCallback(CalendarDateModel calendarDateModel) {
        getView().setSelectedDateLayoutVisible(true);
        startDateTime = DateUtil.strToDate(calendarDateModel.startDate).getTime();
        endDateTime = DateUtil.strToDate(calendarDateModel.endDate).getTime();
        getView().setSelectedDateSearchText(DateUtil.getCalendarYearMothDayFormatDate(startDateTime) + " ~ " + DateUtil
                .getCalendarYearMothDayFormatDate(endDateTime));
        endDateTime += 1000 * 60 * 60 * 24;

        getView().showProgressDialog();
        requestData(cid,Constants.DIRECTION_DOWN);
    }

    public void doRequestData() {
        startDateTime = 0;
        endDateTime = 0;
        getView().showProgressDialog();
        requestData(cid,Constants.DIRECTION_DOWN);
    }

    public void doPersonLocus(int modelPosition, int avatarPosition) {
        DeviceCameraFacePicListModel model = getView().getItemData(modelPosition);
        DeviceCameraFacePic deviceCameraFacePic = model.pics.get(avatarPosition);
        String id = deviceCameraFacePic.getId();
        if (TextUtils.isEmpty(id)) {
            getView().toastShort(mActivity.getString(R.string.not_obtain_face_id));
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_PERSON_LOCUS_FACE_ID,deviceCameraFacePic.getId());
        intent.setClass(mActivity, CameraPersonLocusActivity.class);
        getView().startAC(intent);

    }
}
