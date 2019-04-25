package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;

import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IPersonLocusView;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitService;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.response.DeviceCameraPersonFaceRsp;

import java.sql.DataTruncation;
import java.util.Collections;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

public class PersonLocusPresenter extends BasePresenter<IPersonLocusView> {
    private Activity mActivity;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        String faceId = mActivity.getIntent().getStringExtra(Constants.EXTRA_PERSON_LOCUS_FACE_ID);

        requestData(faceId);

    }

    private void requestData(String faceId) {

        Long endTime = System.currentTimeMillis();
        Long startTime = endTime - 24*3600*100*30;
        if (isAttachedView()) {
            getView().showProgressDialog();
        }
        RetrofitServiceHelper.getInstance().getDeviceCameraPersonFace(faceId,startTime,endTime,85,0,100,null)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<DeviceCameraPersonFaceRsp>(this) {
                    @Override
                    public void onCompleted(DeviceCameraPersonFaceRsp deviceCameraPersonFaceRsp) {
                        List<DeviceCameraPersonFaceRsp.DataBean> data = deviceCameraPersonFaceRsp.getData();
                        if (data != null && data.size() > 0) {
                            Collections.reverse(data);
                            DeviceCameraPersonFaceRsp.DataBean dataBean = data.get(0);

                            if (isAttachedView()) {
                                LatLng latLng = new LatLng(dataBean.getLatitude(),dataBean.getLongitude());
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 15f, 0, 30));
                                getView().setMapCenter(cameraUpdate);
                                for (int i = 0; i < data.size(); i++) {
                                    LatLng latLn = new LatLng(data.get(i).getLatitude(), data.get(i).getLongitude());
                                    MarkerOptions markerOptions = new MarkerOptions()
                                                .position(latLn).icon(BitmapDescriptorFactory.fromResource(i == 0 ? R.drawable.person_locus_slected : R.drawable.person_locus_normal))
                                                .draggable(false);
                                    getView().addMarker(markerOptions);
                                }
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
                            getView().toastShort(errorMsg);
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {

    }
}
