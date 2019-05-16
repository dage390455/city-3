package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sensoro.smartcity.R;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ICameraPersonDetailActivityView;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.DeviceCameraHistoryBean;
import com.sensoro.common.server.response.DeviceCameraHistoryRsp;
import com.sensoro.common.server.response.DeviceCameraPersonFaceRsp;
import com.sensoro.common.utils.DateUtil;

import java.io.Serializable;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CameraPersonDetailActivityPresenter extends BasePresenter<ICameraPersonDetailActivityView> {
    private Activity mActivity;
    private DeviceCameraPersonFaceRsp.DataBean dataBean;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        Serializable extra = mActivity.getIntent().getSerializableExtra(Constants.EXTRA_CAMERA_PERSON_DETAIL);
        if (extra instanceof DeviceCameraPersonFaceRsp.DataBean) {
//            getView().initVideoOption(extra.get);
            dataBean = (DeviceCameraPersonFaceRsp.DataBean) extra;

            setTitle();

            setLastCover();
            requestVideo(dataBean);
        }else{
            getView().toastShort(mActivity.getString(R.string.tips_data_error));
        }
    }

    private void setLastCover() {
        Glide.with(mActivity).load(Constants.CAMERA_BASE_URL+dataBean.getSceneUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                .into(getView().getImageView());

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

                }else{
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

    }

    public void doRetry() {
        requestVideo(dataBean);
    }
}
