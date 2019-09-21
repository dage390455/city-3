package com.sensoro.forestfire.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.AlarmCameraLiveBean;
import com.sensoro.common.server.bean.DeviceCameraDetailInfo;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.forestfire.R;
import com.sensoro.forestfire.imainviews.IAlarmForestFireCameraLiveDetailActivityView;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class AlarmForestFireCameraLiveDetailActivityPresenter extends BasePresenter<IAlarmForestFireCameraLiveDetailActivityView> {
    private Activity mActivity;
    private ArrayList<AlarmCameraLiveBean> mList = new ArrayList<>();

    private List<String> cameras;
    private int mItemClickPosition = 0;

    private String currentReTryClickSn;
    private ArrayList<String> urlList = new ArrayList<>();

    /**
     * 网络改变状态
     *
     * @param eventData
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        if (code == Constants.NetworkInfo) {
            int data = (int) eventData.data;

            switch (data) {

                case ConnectivityManager.TYPE_WIFI:
                    getView().getPlayView().setCityPlayState(-1);

//                    if (getView().getPlayView().getCurrentState() == CURRENT_STATE_PAUSE) {
                    //直播需要重新拉去
                    doLive();
//                    }
                    getView().setVerOrientationUtilEnable(true);


                    break;

                case ConnectivityManager.TYPE_MOBILE:

                    if (isAttachedView()) {
                        getView().setVerOrientationUtilEnable(false);

                        getView().getPlayView().setCityPlayState(2);
                        getView().getPlayView().getPlayAndRetryBtn().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getView().setVerOrientationUtilEnable(true);

                                GSYVideoManager.onResume(true);
                                getView().getPlayView().setCityPlayState(-1);
                                doLive();


                            }
                        });
                        getView().backFromWindowFull();

                    }

                    break;

                default:
                    if (isAttachedView()) {
                        getView().backFromWindowFull();
                        getView().getPlayView().setCityPlayState(1);
                        getView().setVerOrientationUtilEnable(false);

                    }


                    break;

            }
        } else if (code == Constants.VIDEO_START) {

            doLive();
//                    }
            getView().setVerOrientationUtilEnable(true);

        } else if (code == Constants.VIDEO_STOP) {


            getView().setVerOrientationUtilEnable(false);
            GSYVideoManager.onPause();
            getView().backFromWindowFull();


        }
    }

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;

        EventBus.getDefault().register(this);
        Intent intent = mActivity.getIntent();
        if (intent != null) {
            Serializable serializable = intent.getSerializableExtra(Constants.EXTRA_ALARM_CAMERAS);
            if (serializable instanceof ArrayList) {
                cameras = (List<String>) serializable;
                requestData(cameras, true);
            }
        }


    }

    private void requestData(List<String> cameras, boolean isShowProgress) {
        if (isShowProgress) {
            getView().showProgressDialog();

        }
        RetrofitServiceHelper.getInstance().getAlarmCamerasDetail(cameras).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<AlarmCameraLiveBean>>>(this) {

            @Override
            public void onCompleted(ResponseResult<List<AlarmCameraLiveBean>> alarmCameraLiveRsp) {
                List<AlarmCameraLiveBean> data = alarmCameraLiveRsp.getData();
                mList.clear();
                if (data != null && data.size() > 0) {
                    mList.addAll(data);
                    mItemClickPosition = 0;

                    if (!TextUtils.isEmpty(currentReTryClickSn)) {
                        for (int i = 0; i < mList.size(); i++) {
                            if (mList.get(i).getSn().equals(currentReTryClickSn)) {
                                mItemClickPosition = i;
                                break;
                            }
                        }
                    }


                    doLive();
                }

                if (isAttachedView()) {
                    getView().updateData(mList);
                    getView().onPullRefreshComplete();
                    getView().dismissProgressDialog();

                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {

                if (isAttachedView()) {
                    getView().dismissProgressDialog();
                    getView().toastShort(errorMsg);
                    getView().onPullRefreshComplete();

                }
            }
        });
    }

    public void doLive() {
        if (mList.size() > mItemClickPosition) {
            AlarmCameraLiveBean dataBean = mList.get(mItemClickPosition);
            if (dataBean == null) {
                getView().toastShort(mActivity.getString(R.string.unknown_error));
                return;
            }

            getLastCoverImage(dataBean.getLastCover());
            if (!TextUtils.isEmpty(dataBean.getDeviceStatus()) && "0".equals(dataBean.getDeviceStatus())) {
                getView().offlineType(dataBean.getHls(), dataBean.getSn());
            } else {

                urlList.clear();
                urlList.add(dataBean.getFlv());
                urlList.add(dataBean.getHls());
                getView().doPlayLive(urlList);

            }
        } else {
            getView().toastShort(mActivity.getString(R.string.unknown_error));
        }

    }

    @Override
    public void onDestroy() {
        mList.clear();
        mList = null;
        EventBus.getDefault().unregister(this);
    }

    public void doRefresh() {
        currentReTryClickSn = "";
        if (cameras == null) {
            getView().onPullRefreshComplete();
            return;
        }

        requestData(cameras, false);
    }

    public void doItemClick(int position) {
        mItemClickPosition = position;
        currentReTryClickSn = "";
        doLive();
    }


    public void regainGetCameraState(final String sn) {
        getView().showProgressDialog();
        RetrofitServiceHelper.getInstance().getDeviceCamera(sn).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<DeviceCameraDetailInfo>>(this) {
            @Override
            public void onCompleted(ResponseResult<DeviceCameraDetailInfo> deviceCameraDetailRsp) {
                DeviceCameraDetailInfo data = deviceCameraDetailRsp.getData();
                if (data != null) {
                    String hls = data.getHls();
                    String lastCover = data.getLastCover();

                    getLastCoverImage(lastCover);
                    String deviceStatus = data.getDeviceStatus();
                    if (!TextUtils.isEmpty(deviceStatus) && "0".equals(deviceStatus)) {
                        getView().offlineType(hls, sn);
                    } else {
                        doLive();
                        //更新列表信息

                        currentReTryClickSn = sn;
                        requestData(cameras, true);


                    }

                } else {
                    getView().toastShort(mActivity.getString(R.string.camera_info_get_failed));

                }
                getView().dismissProgressDialog();

            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    private void getLastCoverImage(String lastCover) {
        Glide.with(mActivity).asBitmap().load(lastCover).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                if (isAttachedView()) {
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(resource);
                    getView().setImage(bitmapDrawable);
                }
            }
        });
    }

}