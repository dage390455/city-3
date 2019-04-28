package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.model.DeviceCameraFacePicListModel;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IPersonLocusView;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceCameraFacePic;
import com.sensoro.smartcity.server.bean.DeviceCameraHistoryBean;
import com.sensoro.smartcity.server.response.DeviceCameraHistoryRsp;
import com.sensoro.smartcity.server.response.DeviceCameraPersonFaceRsp;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.ImageFactory;
import com.sensoro.smartcity.widget.GlideRoundTransform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PersonLocusPresenter extends BasePresenter<IPersonLocusView> {
    private Activity mActivity;
    private int index = 0;
    private int size;
    private List<DeviceCameraPersonFaceRsp.DataBean> data;
    private int index1;
    private DeviceCameraPersonFaceRsp.DataBean preBean;
    private ArrayList<LatLng> displayLinePoints = new ArrayList<>();
    private MarkerOptions avatarMarkerOptions;
    private DeviceCameraPersonFaceRsp.DataBean playBean;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        String faceId = mActivity.getIntent().getStringExtra(Constants.EXTRA_PERSON_LOCUS_FACE_ID);
        size = AppUtils.dp2px(mActivity, 24);
        requestData(faceId);

    }

    private void requestData(String faceId) {

        Long endTime = System.currentTimeMillis();
        Long startTime = endTime - 24*60*60*1000*7L;
        if (isAttachedView()) {
            getView().showProgressDialog();
        }
        RetrofitServiceHelper.getInstance().getDeviceCameraPersonFace(faceId, startTime, endTime, 85, 0, 100, null)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<DeviceCameraPersonFaceRsp>(this) {
                    @Override
                    public void onCompleted(DeviceCameraPersonFaceRsp deviceCameraPersonFaceRsp) {
                        data = deviceCameraPersonFaceRsp.getData();

                        if (data != null && data.size() > 0) {
                            Collections.reverse(data);
                            List<LatLng> linePoints = new ArrayList<>();

                            if (isAttachedView()) {
                                for (int i = 0; i < data.size(); i++) {
                                    DeviceCameraPersonFaceRsp.DataBean dataFace = data.get(i);
                                    LatLng latLn = new LatLng(dataFace.getLatitude(), dataFace.getLongitude());

                                    linePoints.add(latLn);

                                    addNormalMarker(latLn,i);
                                }


                                DeviceCameraPersonFaceRsp.DataBean dataBean = data.get(0);
                                preBean = dataBean;

                                final LatLng latLng = new LatLng(dataBean.getLatitude(), dataBean.getLongitude());
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 18f, 0, 30));
                                getView().setMapCenter(cameraUpdate);


                                loadAvatar(dataBean, latLng,-1);
                                displayLinePoints.add(latLng);


                                PolylineOptions polylineOptions = new PolylineOptions();
                                polylineOptions.addAll(linePoints)
                                .width(AppUtils.dp2px(mActivity,4))
                                .color(Color.parseColor("#D8D8D8"));

                                if (isAttachedView()) {
                                    try {
                                        long l = Long.parseLong(dataBean.getCaptureTime());
                                        getView().setTimeText(DateUtil.getMothDayHourMinuteFormatDate(l));
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                    getView().addPolyLine(polylineOptions,false);
                                    checkLeftRightStatus();
                                    getView().initSeekBar(data.size()-1);
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

    private void addNormalMarker(LatLng latLn, int tag) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLn)
                .anchor(0.5f,0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.person_locus_normal))
                .draggable(false);

        getView().addMarker(markerOptions,tag);
    }

    private void loadAvatar(final DeviceCameraPersonFaceRsp.DataBean dataBean, final LatLng latLng, final int tag) {
        Glide.with(mActivity)
                .load(Constants.CAMERA_BASE_URL+dataBean.getFaceUrl())
                .asBitmap()
                .thumbnail(0.1f)
                .override(size,size)
                .transform(new GlideRoundTransform(mActivity))
                .error(R.drawable.deploy_pic_placeholder)           //设置错误图片
                .placeholder(R.drawable.ic_default_cround_image)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (avatarMarkerOptions == null) {
                            avatarMarkerOptions = new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(resource))
                                    .anchor(0.5f,1)
                                    .draggable(false).title("dd").snippet("ddd");
                            if (isAttachedView()) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getView().addMarker(avatarMarkerOptions, tag);
                                    }
                                });

                            }
                        }else{
//                            avatarMarkerOptions.position(latLng).icon(BitmapDescriptorFactory.fromBitmap(resource));

                            getView().refreshMap(latLng,resource);
                        }
                        playBean = dataBean;


                    }
                });

        getView().clearIMv();
        Glide.with(mActivity)
                .load(Constants.CAMERA_BASE_URL+dataBean.getFaceUrl())
                .asBitmap()
                .thumbnail(0.1f)
                .override(size,size)
                .transform(new GlideRoundTransform(mActivity))
                .error(R.drawable.deploy_pic_placeholder)           //设置错误图片
                .placeholder(R.drawable.ic_default_cround_image)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(getView().getIMv());
    }


    @Override
    public void onDestroy() {

    }

    public void doMoveLeft() {
        index--;
        if (index >= 0 && data.size() > index) {
            DeviceCameraPersonFaceRsp.DataBean bean = data.get(index);
            LatLng latLng = new LatLng(bean.getLatitude(), bean.getLongitude());
//            getView().removeAvatarMarker();
            getView().setMapCenter(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 18f, 0, 30)));
            loadAvatar(bean,latLng,-1);

            displayLinePoints.remove(displayLinePoints.size()-1);
            getView().clearDisplayLine();
            if (displayLinePoints.size()>1) {
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.addAll(displayLinePoints)
                        .width(AppUtils.dp2px(mActivity,4))
                        .setCustomTexture(BitmapDescriptorFactory.defaultMarker());
                getView().addPolyLine(polylineOptions, true);
            }

            preBean = bean;

        }
        getView().updateSeekBar(index);
        checkLeftRightStatus();
    }

    private void checkLeftRightStatus() {
        getView().setMoveLeftClickable(index >0);


        getView().setMoveRightClickable(data.size() > 0 && index < data.size());


    }

    public void doMoveRight() {
        index++;
        if (index > -1 && data.size() > index){
            DeviceCameraPersonFaceRsp.DataBean bean = data.get(index);
            LatLng latLng = new LatLng(bean.getLatitude(), bean.getLongitude());
//            getView().removeAvatarMarker();
            getView().setMapCenter(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 18f, 0, 30)));
            loadAvatar(bean,latLng,-1);

            displayLinePoints.add(latLng);
            if (displayLinePoints.size()>1) {
                getView().clearDisplayLine();

                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.addAll(displayLinePoints)
                        .width(AppUtils.dp2px(mActivity,4))
                        .setCustomTexture(BitmapDescriptorFactory.defaultMarker());
                getView().addPolyLine(polylineOptions, true);
            }



        }
        getView().updateSeekBar(index);
        checkLeftRightStatus();
    }

    public void doSeekBarTouch(int mSeekBarProgres) {
        if (mSeekBarProgres != index) {
            index = mSeekBarProgres;
        }else{
            index++;
            if(index >= data.size()){
                index = mSeekBarProgres;
            }
            getView().updateSeekBar(index);
        }
        DeviceCameraPersonFaceRsp.DataBean bean = data.get(mSeekBarProgres);
        LatLng latLng = new LatLng(bean.getLatitude(), bean.getLongitude());
//        getView().removeAvatarMarker();
        getView().setMapCenter(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 18f, 0, 30)));
        loadAvatar(bean,latLng,-1);

        displayLinePoints.clear();
        for (int i = 0; i <= mSeekBarProgres; i++) {
            displayLinePoints.add(new LatLng(data.get(i).getLatitude(),data.get(i).getLongitude()));
        }
        getView().clearDisplayLine();

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(displayLinePoints)
                .width(AppUtils.dp2px(mActivity,4))
                .setCustomTexture(BitmapDescriptorFactory.defaultMarker());
        getView().addPolyLine(polylineOptions, true);

        checkLeftRightStatus();
    }

    public void doPlay() {
        if (playBean == null) {
            return;
        }
        String captureTime = playBean.getCaptureTime();
        long time = Long.parseLong(captureTime);
        long l = time / 1000;

        String beginTime = String.valueOf(l - 15);
        String endTime = String.valueOf(l + 15);
        getView().showProgressDialog();
        RetrofitServiceHelper.getInstance().getDeviceCameraPlayHistoryAddress(playBean.getCid(), beginTime, endTime, null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceCameraHistoryRsp>(null) {
            @Override
            public void onCompleted(DeviceCameraHistoryRsp deviceCameraHistoryRsp) {
                List<DeviceCameraHistoryBean> data = deviceCameraHistoryRsp.getData();
                if (data != null && data.size() > 0) {
                    DeviceCameraHistoryBean deviceCameraHistoryBean = data.get(0);
                    String url1 = deviceCameraHistoryBean.getUrl();
                    if (isAttachedView()) {
                        getView().startPlay(url1);
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
