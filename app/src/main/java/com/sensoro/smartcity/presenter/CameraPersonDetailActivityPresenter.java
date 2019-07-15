package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.DeviceCameraHistoryBean;
import com.sensoro.common.server.response.DeviceCameraHistoryRsp;
import com.sensoro.common.server.response.DeviceCameraPersonFaceRsp;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.imainviews.ICameraPersonDetailActivityView;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.shuyu.gsyvideoplayer.video.base.GSYVideoView.CURRENT_STATE_PAUSE;

public class CameraPersonDetailActivityPresenter extends BasePresenter<ICameraPersonDetailActivityView> {
    private Activity mActivity;
    private DeviceCameraPersonFaceRsp.DataBean dataBean;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        EventBus.getDefault().register(this);
        Serializable extra = mActivity.getIntent().getSerializableExtra(Constants.EXTRA_CAMERA_PERSON_DETAIL);
        if (extra instanceof DeviceCameraPersonFaceRsp.DataBean) {
//            getView().initVideoOption(extra.get);
            dataBean = (DeviceCameraPersonFaceRsp.DataBean) extra;

            setTitle();

            setLastCover();
            requestVideo(dataBean);
        } else {
            getView().toastShort(mActivity.getString(R.string.tips_data_error));
        }
    }

    private void setLastCover() {
        Glide.with(mActivity).load(Constants.CAMERA_BASE_URL + dataBean.getSceneUrl())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                //缓存全尺寸
                .into(getView().getImageView());

    }

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
                    getView().setVerOrientationUtil(true);

                    if (getView().getPlayView().getCurrentState() == CURRENT_STATE_PAUSE) {
                        getView().getPlayView().clickCityStartIcon();

                        GSYVideoManager.onResume();

                    } else {
                        doRetry();

                    }

                    break;

                case ConnectivityManager.TYPE_MOBILE:
                    getView().setVerOrientationUtil(false);

                    if (isAttachedView()) {
                        getView().getPlayView().setCityPlayState(2);

                        getView().getPlayView().getPlayAndRetryBtn().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                getView().getPlayView().setCityPlayState(-1);
                                if (getView().getPlayView().getCurrentState() == CURRENT_STATE_PAUSE) {
                                    getView().getPlayView().clickCityStartIcon();
                                    getView().setVerOrientationUtil(true);

                                }
                                GSYVideoManager.onResume(true);


                            }
                        });

                        getView().backFromWindowFull();

                    }

                    break;

                default:
                    if (isAttachedView()) {


                        getView().backFromWindowFull();
                        getView().getPlayView().setCityPlayState(1);
                        getView().setVerOrientationUtil(false);
                    }
                    break;


            }
        } else if (code == Constants.VIDEO_START) {

            getView().setVerOrientationUtil(true);

            if (getView().getPlayView().getCurrentState() == CURRENT_STATE_PAUSE) {
                getView().getPlayView().clickCityStartIcon();

                GSYVideoManager.onResume();

            } else {
                doRetry();

            }

        } else if (code == Constants.VIDEO_STOP) {

            getView().setVerOrientationUtil(false);
            GSYVideoManager.onPause();

            getView().backFromWindowFull();



        }
    }

    private void setTitle() {
        try {
            long l = Long.parseLong(dataBean.getCaptureTime());
            String time = DateUtil.getStrTime_ymd_hm_ss(l);
            getView().setTitle(time);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            getView().setTitle(mActivity.getString(R.string.person_avatar_video));
        }
    }

    private void requestVideo(DeviceCameraPersonFaceRsp.DataBean dataBean) {
        getView().showProgressDialog();
        long time;
        try {
            time = Long.parseLong(dataBean.getCaptureTime());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            getView().toastShort(mActivity.getString(R.string.time_parse_error));
            return;
        }

        time = time / 1000;
        String beginTime = String.valueOf(time - 15);
        String endTime = String.valueOf(time + 15);

        RetrofitServiceHelper.getInstance().getDeviceCameraPlayHistoryAddress(dataBean.getCid(), beginTime, endTime, null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceCameraHistoryRsp>(null) {
            @Override
            public void onCompleted(DeviceCameraHistoryRsp deviceCameraHistoryRsp) {
                List<DeviceCameraHistoryBean> data = deviceCameraHistoryRsp.getData();
                if (data != null && data.size() > 0) {
                    DeviceCameraHistoryBean deviceCameraHistoryBean = data.get(0);
                    String url1 = deviceCameraHistoryBean.getUrl();
                    if (isAttachedView()) {
                        getView().startPlayLogic(url1);
                    }

                } else {
                    if (isAttachedView()) {
                        getView().toastShort(mActivity.getString(R.string.obtain_video_fail));
                    }
                }

                if (isAttachedView()) {
                    getView().dismissProgressDialog();
                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                if (isAttachedView()) {
                    getView().playError(errorMsg);
                    getView().dismissProgressDialog();
                }
            }
        });


    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    public void doRetry() {
        requestVideo(dataBean);
    }
}
