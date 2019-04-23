package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.sensoro.smartcity.adapter.model.DeviceCameraFacePicListModel;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ICameraDetailView;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceCameraFacePic;
import com.sensoro.smartcity.server.bean.DeviceCameraHistoryBean;
import com.sensoro.smartcity.server.response.DeviceCameraFacePicListRsp;
import com.sensoro.smartcity.server.response.DeviceCameraHistoryRsp;
import com.sensoro.smartcity.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CameraDetailPresenter extends BasePresenter<ICameraDetailView> {
    private Activity mActivity;
    private String cid;
    private String minId = null;
    private String yearMonthDate;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        Intent intent = mActivity.getIntent();
        String url = "http://wdquan-space.b0.upaiyun.com/VIDEO/2018/11/22/ae0645396048_hls_time10.m3u8";
        if (intent != null) {
            cid = intent.getStringExtra("cid");
            url = intent.getStringExtra("hls");
        }

        getView().initVideoOption(url);
        requestData(cid, Constants.DIRECTION_DOWN);
    }

    private void requestData(String cid, final int direction) {

        ArrayList<String> strings = new ArrayList<>();
        strings.add(cid);
        long currentTimeMillis = System.currentTimeMillis();
        String startTime = String.valueOf(currentTimeMillis - 24 * 60 * 60 * 30 * 1000L);
        String endTime = String.valueOf(currentTimeMillis);
        RetrofitServiceHelper.getInstance().getDeviceCameraFaceList(strings, null, 20,minId,startTime, endTime)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceCameraFacePicListRsp>(null) {
            @Override
            public void onCompleted(final DeviceCameraFacePicListRsp deviceCameraFacePicListRsp) {
                if (direction == Constants.DIRECTION_DOWN) {
                    yearMonthDate = null;
                    refreshData(deviceCameraFacePicListRsp,null);
                }else if(direction == Constants.DIRECTION_UP){
                    refreshData(deviceCameraFacePicListRsp,getView().getRvListData());
                }

            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {

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

    public void onCameraItemClick(int index) {
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
            }

            min = min / 1000;
            String beginTime = String.valueOf(min - 30);
            String endTime = String.valueOf(max/1000);

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
                        getView().dismissProgressDialog();
                    }
                }
            });

        }
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
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return;
            }
            String ymd = DateUtil.getDate(l);
            if (currentDate.equals(ymd)) {
                ymd = "今天";
            }else if(isYesterday(currentDate,ymd)){
                ymd = "昨天";
            }
            getView().setDateTime(ymd);
        }
    }

    public void doRefresh() {
        requestData(cid,Constants.DIRECTION_DOWN);
    }

    public void doLoadMore() {
        requestData(cid,Constants.DIRECTION_UP);
    }
}
