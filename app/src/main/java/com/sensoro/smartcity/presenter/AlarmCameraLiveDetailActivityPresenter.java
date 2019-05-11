package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IAlarmCameraLiveDetailActivityView;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceCameraDetailInfo;
import com.sensoro.smartcity.server.response.AlarmCameraLiveRsp;
import com.sensoro.smartcity.server.response.DeviceCameraDetailRsp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AlarmCameraLiveDetailActivityPresenter extends BasePresenter<IAlarmCameraLiveDetailActivityView> {
    private Activity mActivity;
    private ArrayList<AlarmCameraLiveRsp.DataBean> mList = new ArrayList<>();
    private List<String> cameras;
    private int mItemClickPosition = 0;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;

        Intent intent = mActivity.getIntent();
        if (intent != null) {
            Serializable serializable = intent.getSerializableExtra(Constants.EXTRA_ALARM_CAMERAS);
            if (serializable instanceof ArrayList) {
                cameras = (List<String>) serializable;
                requestData(cameras,true);
            }
        }



    }

    private void requestData(List<String> cameras, boolean isShowProgress) {
        if (isShowProgress) {
            getView().showProgressDialog();

        }
        RetrofitServiceHelper.getInstance().getAlarmCamerasDetail(cameras).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<AlarmCameraLiveRsp>(this){

            @Override
            public void onCompleted(AlarmCameraLiveRsp alarmCameraLiveRsp) {
                List<AlarmCameraLiveRsp.DataBean> data = alarmCameraLiveRsp.getData();
                mList.clear();
                if (data != null && data.size() > 0) {
                    mList.addAll(data);
                    mItemClickPosition = 0;
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
            AlarmCameraLiveRsp.DataBean dataBean = mList.get(mItemClickPosition);
            if (dataBean == null) {
                getView().toastShort(mActivity.getString(R.string.unknown_error));
                return;
            }

            getLastCoverImage(dataBean.getLastCover());
            if (!TextUtils.isEmpty(dataBean.getDeviceStatus()) && "0".equals(dataBean.getDeviceStatus())) {
                getView().offlineType(dataBean.getHls(),dataBean.getSn());
            } else {
                getView().doPlayLive(dataBean.getHls());

            }
        }else{
            getView().toastShort(mActivity.getString(R.string.unknown_error));
        }

    }
    @Override
    public void onDestroy() {
        mList.clear();
        mList = null;
    }

    public void doRefresh() {
        if (cameras == null) {
            getView().onPullRefreshComplete();
            return;
        }

        requestData(cameras, false);
    }

    public void doItemClick(int position) {
        mItemClickPosition = position;
        doLive();
    }



    public void regainGetCameraState(final String sn) {
        getView().showProgressDialog();
        RetrofitServiceHelper.getInstance().getDeviceCamera(sn).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceCameraDetailRsp>(this) {
            @Override
            public void onCompleted(DeviceCameraDetailRsp deviceCameraDetailRsp) {
                DeviceCameraDetailInfo data = deviceCameraDetailRsp.getData();
                if (data != null) {
                    String hls = data.getHls();
                    String lastCover = data.getLastCover();

                    getLastCoverImage(lastCover);
                    String deviceStatus = data.getDeviceStatus();
                    if (!TextUtils.isEmpty(deviceStatus) && "0".equals(deviceStatus)) {
                        getView().offlineType(hls,sn);
                    } else {
                        doLive();
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
        Glide.with(mActivity).load(lastCover).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                BitmapDrawable bitmapDrawable = new BitmapDrawable(resource);
                getView().setImage(bitmapDrawable);
            }
        });
    }
}
