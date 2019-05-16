package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.CameraPersonDetailActivity;
import com.sensoro.smartcity.activity.CameraPersonLocusActivity;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ICameraPersonAvatarHistoryActivityView;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.response.DeviceCameraPersonFaceRsp;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CameraPersonAvatarHistoryActivityPresenter extends BasePresenter<ICameraPersonAvatarHistoryActivityView> {
    private Activity mActivity;
    private int curPage = -1;
    private String faceId;
    private List<DeviceCameraPersonFaceRsp.DataBean> mData = new ArrayList<>();


    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        faceId = mActivity.getIntent().getStringExtra(Constants.EXTRA_PERSON_AVATAR_HISTORY_FACE_ID);
        String faceUrl = mActivity.getIntent().getStringExtra(Constants.EXTRA_CAMERA_PERSON_AVATAR_HISTORY_FACE_URL);

        loadAvatar(faceUrl);
        getView().loadTitleAvatar(faceUrl);

        requestData(faceId,Constants.DIRECTION_DOWN);

    }

    private void loadAvatar(String faceUrl) {

    }

    private void requestData(String faceId, final int direction) {
        if(direction == Constants.DIRECTION_DOWN){
            curPage = 0;
        }else{
            curPage++;
        }

        if(curPage < 0){
            curPage = 0;
        }

        //人脸记录从服务器可以获取到30天的数据，但是，视频记录，羚羊云方面只能保留7天的历史视频，所以这里只请求7天的人脸记录
        Long endTime = System.currentTimeMillis();
        Long startTime = endTime - 24*60*60*1000*7L;
        if (isAttachedView()) {
            getView().showProgressDialog();
        }
        RetrofitServiceHelper.getInstance().getDeviceCameraPersonFace(faceId, startTime, endTime, 85, curPage*20, 20, null)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<DeviceCameraPersonFaceRsp>(this) {
                    @Override
                    public void onCompleted(DeviceCameraPersonFaceRsp deviceCameraPersonFaceRsp) {
                        List<DeviceCameraPersonFaceRsp.DataBean> data = deviceCameraPersonFaceRsp.getData();

                        if (data != null && data.size() > 0) {
                            if(direction == Constants.DIRECTION_DOWN){
                                mData.clear();
                                mData.addAll(data);
                                if (isAttachedView()) {
                                    getView().updateData(mData);
                                    getView().onPullRefreshComplete();
                                }
                            }else{
                                mData.addAll(data);
                                if (isAttachedView()) {
                                    getView().updateData(mData);
                                    getView().onPullRefreshComplete();
                                }
                            }
                        }else{
                            if(direction == Constants.DIRECTION_UP){
                                curPage--;
                            }

                            if (isAttachedView()) {
                                getView().toastShort(mActivity.getString(R.string.no_more_data));
                            }
                        }

                        if (isAttachedView()) {
                            getView().onPullRefreshComplete();
                            getView().dismissProgressDialog();
                        }
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        if(direction == Constants.DIRECTION_UP){
                            curPage--;
                        }
                        if (isAttachedView()) {
                            getView().dismissProgressDialog();
                            getView().toastShort(errorMsg);
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {

    }

    public void doRefresh() {
        requestData(faceId,Constants.DIRECTION_DOWN);
    }

    public void doLoadMore() {
        requestData(faceId,Constants.DIRECTION_UP);
    }

    public void doItemClick(DeviceCameraPersonFaceRsp.DataBean dataBean) {
        Intent intent = new Intent(mActivity, CameraPersonDetailActivity.class);
        intent.putExtra(Constants.EXTRA_CAMERA_PERSON_DETAIL,dataBean);
        getView().startAC(intent);
    }

    public void doPersonLocus() {
        Intent intent = new Intent(mActivity, CameraPersonLocusActivity.class);
        intent.putExtra(Constants.EXTRA_PERSON_LOCUS_FACE_ID,faceId);
        getView().startAC(intent);
    }
}
