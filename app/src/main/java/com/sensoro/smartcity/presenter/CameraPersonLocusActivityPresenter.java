package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ICameraPersonLocusActivityView;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceCameraHistoryBean;
import com.sensoro.smartcity.server.response.DeviceCameraHistoryRsp;
import com.sensoro.smartcity.server.response.DeviceCameraPersonFaceRsp;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.widget.GlideCircleTransform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CameraPersonLocusActivityPresenter extends BasePresenter<ICameraPersonLocusActivityView> {
    private Activity mActivity;
    private int index = 0;
    private List<DeviceCameraPersonFaceRsp.DataBean> data;
    private int index1;
    private DeviceCameraPersonFaceRsp.DataBean preBean;
    private ArrayList<LatLng> displayLinePoints = new ArrayList<>();
    private ArrayList<Integer> hightLightPoints = new ArrayList<>();
    private MarkerOptions avatarMarkerOptions;
    private DeviceCameraPersonFaceRsp.DataBean playBean;
    private ImageView imageView;
    private int dp24;
    private String faceId;
    private int day = 1;
    private float mMapZoom = 18f;
    private Bitmap mAvatarPlaceholder;
    private Handler mHandler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getView().setSeekBarTimeVisible(false);
        }
    };

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        faceId = mActivity.getIntent().getStringExtra(Constants.EXTRA_PERSON_LOCUS_FACE_ID);
        dp24 = AppUtils.dp2px(mActivity, 24);
        initMarkerImageView();
        mHandler = new Handler();
        requestData(faceId);



    }

    private void initMarkerImageView() {
        imageView = new ImageView(mActivity);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.width=AppUtils.dp2px(mActivity,64);
        layoutParams.height=AppUtils.dp2px(mActivity,80);
        imageView.setBackgroundResource(R.drawable.person_locus_avatar_bg);
        imageView.setScaleType(ImageView.ScaleType.FIT_START);
        int dp8 = AppUtils.dp2px(mActivity, 8);
        imageView.setPadding(dp8,AppUtils.dp2px(mActivity, 4),dp8,AppUtils.dp2px(mActivity, 12));

        imageView.setImageResource(R.drawable.person_locus_placeholder);
        mAvatarPlaceholder = getViewBitmap(imageView);
    }

    private void requestData(String faceId) {

        getView().setSelectDayBg(day);
        Long endTime = System.currentTimeMillis();
        Long startTime = endTime - 24*60*60*1000L*day;
        if (isAttachedView()) {
            getView().showProgressDialog();
        }
        RetrofitServiceHelper.getInstance().getDeviceCameraPersonFace(faceId, startTime, endTime, 85, 0, 100, null)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<DeviceCameraPersonFaceRsp>(this) {
                    @Override
                    public void onCompleted(DeviceCameraPersonFaceRsp deviceCameraPersonFaceRsp) {
                        data = deviceCameraPersonFaceRsp.getData();
                        if (isAttachedView()) {
                            getView().removeAllMarker();
                            getView().clearDisplayLine();
                            getView().clearDisplayNormalLine();
                            getView().setMarkerAddress("");
                            getView().setMarkerTime("");
                        }
                        if (data != null && data.size() > 0) {
                            if (isAttachedView()) {
                                getView().setSeekBarVisible(true);
                            }


                            hightLightPoints.clear();
                            displayLinePoints.clear();
                            avatarMarkerOptions = null;


                            Collections.reverse(data);
                            List<LatLng> linePoints = new ArrayList<>();

                            if (isAttachedView()) {

                                for (int i = 0; i < data.size(); i++) {
                                    DeviceCameraPersonFaceRsp.DataBean dataFace = data.get(i);
                                    LatLng latLn = new LatLng(dataFace.getLatitude(), dataFace.getLongitude());

                                    linePoints.add(latLn);

                                    addNormalMarker(latLn,-2);
                                }


                                DeviceCameraPersonFaceRsp.DataBean dataBean = data.get(0);
                                preBean = dataBean;

                                setAddressTime(dataBean, true);

                                final LatLng latLng = new LatLng(dataBean.getLatitude(), dataBean.getLongitude());

                                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, mMapZoom, 0, 30));
                                getView().setMapCenter(cameraUpdate);

                                hightLightPoints.add(0);
                                addNormalMarker(latLng,0);

                                if (avatarMarkerOptions == null) {
                                    avatarMarkerOptions = new MarkerOptions()
                                            .position(latLng)
                                            .icon(BitmapDescriptorFactory.fromBitmap(mAvatarPlaceholder))
                                            .anchor(0.5f,0.96f)
                                            .draggable(false).title("dd").snippet("ddd")
                                            .zIndex(10f);
                                    if (isAttachedView()) {
                                        mActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                getView().addMarker(avatarMarkerOptions, -1);
                                            }
                                        });

                                    }
                                }else{
                                    if (isAttachedView()) {
                                        getView().updateAvatarMarker(latLng,mAvatarPlaceholder);
                                    }

                                }


                                loadAvatar(dataBean, latLng,-1);

                                displayLinePoints.add(latLng);


                                PolylineOptions polylineOptions = new PolylineOptions();
                                polylineOptions.addAll(linePoints)
                                .width(AppUtils.dp2px(mActivity,4))
                                .color(mActivity.getResources().getColor(R.color.c_b6b6b6));

                                if (isAttachedView()) {
                                    getView().addPolyLine(polylineOptions,false);
                                    checkLeftRightStatus();
                                    getView().initSeekBar(data.size()-1);
                                }


                            }
                        }else{
                            if (isAttachedView()) {
                                getView().setSeekBarVisible(false);
                            }
                        }


                        if (isAttachedView()) {
                            getView().dismissProgressDialog();
                        }
                        checkLeftRightStatus();
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
                .icon(BitmapDescriptorFactory.fromResource(tag < -1 ? R.drawable.person_locus_normal : R.drawable.person_locus_selected))
                .draggable(false);

        getView().addMarker(markerOptions,tag);
    }

    private void loadAvatar(final DeviceCameraPersonFaceRsp.DataBean dataBean, final LatLng latLng, final int tag) {

        Glide.with(mActivity)
                .load(Constants.CAMERA_BASE_URL+dataBean.getFaceUrl())
                .asBitmap()
                .thumbnail(0.1f)
//                .override(size,size)
                .transform(new GlideCircleTransform(mActivity,dp24))
                .error(R.drawable.deploy_pic_placeholder)           //设置错误图片
                .placeholder(R.drawable.ic_default_cround_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                imageView.setImageBitmap(resource);
                Bitmap viewBitmap = getViewBitmap(imageView);
                if (avatarMarkerOptions == null) {
                    avatarMarkerOptions = new MarkerOptions()
                            .position(latLng)
//                            .icon(BitmapDescriptorFactory.fromView(resource))
                            .icon(BitmapDescriptorFactory.fromBitmap(viewBitmap))
                            .anchor(0.5f,0.96f)
                            .draggable(false).title("").snippet("")
                    .zIndex(10f);
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
//                    avatarMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(resource));
                    getView().updateAvatarMarker(latLng,viewBitmap);
                }
                playBean = dataBean;


            }
        });
//
    }


    private Bitmap getViewBitmap(View view) {

        view.setDrawingCacheEnabled(true);

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0,
                view.getMeasuredWidth(),
                view.getMeasuredHeight());

        view.buildDrawingCache();
        Bitmap cacheBitmap = view.getDrawingCache();
        return Bitmap.createBitmap(cacheBitmap);

    }
    @Override
    public void onDestroy() {

    }

    public void doMoveLeft() {
        index--;
        if (index > -1 &&data!= null&& data.size() > index) {
            DeviceCameraPersonFaceRsp.DataBean bean = data.get(index);
            LatLng latLng = new LatLng(bean.getLatitude(), bean.getLongitude());
            getView().setMapCenter(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, mMapZoom, 0, 30)));

            if (avatarMarkerOptions != null) {
                getView().updateAvatarMarker(latLng,mAvatarPlaceholder);
            }

            if (hightLightPoints.size() > 0) {
                getView().removeNormalMarker(hightLightPoints.get(hightLightPoints.size()-1));
                hightLightPoints.remove(displayLinePoints.size() -1);
            }

            loadAvatar(bean,latLng,-1);
            if (displayLinePoints.size() > 0) {
                displayLinePoints.remove(displayLinePoints.size()-1);

            }
            getView().clearDisplayLine();
            if (displayLinePoints.size()>1) {
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.addAll(displayLinePoints)
                        .width(AppUtils.dp2px(mActivity,4))
                        .color(mActivity.getResources().getColor(R.color.c_119f82));
                getView().addPolyLine(polylineOptions, true);
            }

            setAddressTime(bean, true);
            preBean = bean;

        }
        getView().updateSeekBar(index);
        checkLeftRightStatus();
    }

    private void setAddressTime(DeviceCameraPersonFaceRsp.DataBean bean, boolean isDelayed) {


        try {
            String captureTime = bean.getCaptureTime();
            long l = Long.parseLong(captureTime);

            getView().setMarkerTime(DateUtil.getStrTime_ymd_hm_ss(l));
            getView().setSeekBarTime(DateUtil.getStrTime_MM_dd_hms(l));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        getView().setMarkerAddress(bean.getAddress());

        mHandler.removeCallbacksAndMessages(null);
        if (isDelayed) {
            mHandler.postDelayed(runnable,1000);
        }
    }

    private void checkLeftRightStatus() {
        getView().setMoveLeftClickable(index >0);

        getView().setMoveRightClickable(data != null && data.size() > 1 && index < data.size()-1);


    }

    public void doMoveRight() {
        index++;
        if (index > -1 &&data!= null&& data.size() > index){
            DeviceCameraPersonFaceRsp.DataBean bean = data.get(index);
            LatLng latLng = new LatLng(bean.getLatitude(), bean.getLongitude());
            getView().setMapCenter(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, mMapZoom, 0, 30)));

            if (avatarMarkerOptions != null) {
                getView().updateAvatarMarker(latLng,mAvatarPlaceholder);
            }

            hightLightPoints.add(index);
            addNormalMarker(latLng,index);

            loadAvatar(bean,latLng,-1);

            displayLinePoints.add(latLng);
            if (displayLinePoints.size()>1) {
                getView().clearDisplayLine();

                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.addAll(displayLinePoints)
                        .width(AppUtils.dp2px(mActivity,4))
                        .color(mActivity.getResources().getColor(R.color.c_119f82));
                getView().addPolyLine(polylineOptions, true);
            }

            setAddressTime(bean, true);

            preBean = bean;
        }
        getView().updateSeekBar(index);
        checkLeftRightStatus();
    }

    public void doSeekBarTouch(int mSeekBarProgres) {
        if (data == null) {
            return;
        }
        if (mSeekBarProgres != index) {
            index = mSeekBarProgres;

        }else{
            index++;
            if(index >= data.size()){
                index = mSeekBarProgres;
            }
        }
        getView().updateSeekBar(index);
        DeviceCameraPersonFaceRsp.DataBean bean = data.get(mSeekBarProgres);
        LatLng latLng = new LatLng(bean.getLatitude(), bean.getLongitude());
        getView().setMapCenter(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, mMapZoom, 0, 30)));

        if (avatarMarkerOptions != null) {
            getView().updateAvatarMarker(latLng,mAvatarPlaceholder);
        }

        getView().clearNormalMarker();

        hightLightPoints.clear();
        displayLinePoints.clear();
        for (int i = 0; i <= mSeekBarProgres; i++) {
            LatLng latLng1 = new LatLng(data.get(i).getLatitude(), data.get(i).getLongitude());
            displayLinePoints.add(latLng1);
            hightLightPoints.add(i);
            addNormalMarker(latLng1,i);
        }
        loadAvatar(bean,latLng,-1);

        getView().clearDisplayLine();

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(displayLinePoints)
                .width(AppUtils.dp2px(mActivity,4))
                .color(mActivity.getResources().getColor(R.color.c_119f82));
        getView().addPolyLine(polylineOptions, true);
        preBean = bean;
        setAddressTime(bean,false);
        checkLeftRightStatus();
    }

    public void doPlay() {
        if (playBean == null) {
            return;
        }

        loadLastCover();
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

    private void loadLastCover() {
        Glide.with(mActivity).load(Constants.CAMERA_BASE_URL+playBean.getSceneUrl()).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                BitmapDrawable bitmapDrawable = new BitmapDrawable(resource);
                getView().setLastCover(bitmapDrawable);
            }
        });
    }

    public void doOneDay() {
        day = 1;
        index = 0;
        requestData(faceId);
    }

    public void doThreeDay() {
        day = 3;
        index = 0;
        requestData(faceId);

    }

    public void doSevenDay() {
        day = 7;
        index = 0;
        requestData(faceId);

    }

    public void setMapZoom(float zoom) {
        mMapZoom = zoom;
    }

    public void doMonitorMapLocation() {
        CameraPosition cameraPosition = new CameraPosition(new LatLng(preBean.getLatitude(),preBean.getLongitude()), mMapZoom, 0, 30);
        getView().setMapCenter( CameraUpdateFactory.newCameraPosition(cameraPosition));

    }
}
