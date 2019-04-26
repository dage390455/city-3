package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IPersonLocusView;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.response.DeviceCameraPersonFaceRsp;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.DateUtil;
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

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        String faceId = mActivity.getIntent().getStringExtra(Constants.EXTRA_PERSON_LOCUS_FACE_ID);
        size = AppUtils.dp2px(mActivity, 24);
        requestData(faceId);

    }

    private void requestData(String faceId) {

        Long endTime = System.currentTimeMillis();
        Long startTime = endTime - 24*60*60*1000*30L;
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

//                                MarkerOptions markerOptions = new MarkerOptions()
//                                        .position(latLng)
//                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.person_locus_slected))
//                                        .draggable(false);

                                PolylineOptions polylineOptions = new PolylineOptions();
                                polylineOptions.addAll(linePoints)
                                .width(AppUtils.dp2px(mActivity,4))
                                .color(Color.parseColor("#D8D8D8"));

                                if (isAttachedView()) {
//                                    getView().addMarker(markerOptions);
                                    try {
                                        long l = Long.parseLong(dataBean.getCaptureTime());
                                        getView().setTimeText(DateUtil.getMothDayHourMinuteFormatDate(l));
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                    getView().addPolyLine(polylineOptions);
                                    checkLeftRightStatus();
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
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.person_locus_normal))
                .draggable(false);

        getView().addMarker(markerOptions,tag);
    }

    private void loadAvatar(DeviceCameraPersonFaceRsp.DataBean dataBean, final LatLng latLng, final int tag) {
        Glide.with(mActivity)
                .load(Constants.CAMERA_BASE_URL+dataBean.getFaceUrl())
//                .asBitmap()
                .thumbnail(0.1f)
                .override(size,size)
                .bitmapTransform(new GlideRoundTransform(mActivity))
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        Log.e("cxy",":::加载头像"+resource);
                        Drawable drawable = resource.getCurrent();
                        int w = drawable.getIntrinsicWidth();
                        int h = drawable.getIntrinsicHeight();
                        System.out.println("Drawable转Bitmap");
                        Bitmap.Config config =
                                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                        : Bitmap.Config.RGB_565;
                        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
                        //注意，下面三行代码要用到，否则在View或者SurfaceView里的canvas.drawBitmap会看不到图
                        Canvas canvas = new Canvas(bitmap);
                        drawable.setBounds(0, 0, w, h);
                        drawable.draw(canvas);

                        final MarkerOptions markerOptions = new MarkerOptions()
                                .position(latLng)
                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                .anchor(0.5f,1)
                                .draggable(false);
                        if (isAttachedView()) {
                            Log.e("cxy",":::add"+resource);
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getView().addMarker(markerOptions, tag);
                                }
                            });

                        }
                    }
                });
    }

    @Override
    public void onDestroy() {

    }

    public void doMoveLeft() {
        index--;
        if (index > 0 && data.size() > index) {
            DeviceCameraPersonFaceRsp.DataBean bean = data.get(index);
            LatLng latLng = new LatLng(bean.getLatitude(), bean.getLongitude());
            loadAvatar(bean,latLng,-1);

            getView().removeAvatarMarker();
            if(preBean != null){
                LatLng preLatLng = new LatLng(preBean.getLatitude(), preBean.getLongitude());
                addNormalMarker(preLatLng, index+1);

                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.add(preLatLng,latLng)
                        .width(AppUtils.dp2px(mActivity,4))
                        .color(Color.parseColor("#D8D8D8"));
            }

            preBean = bean;

        }
        checkLeftRightStatus();
    }

    private void checkLeftRightStatus() {
        getView().setMoveLeftClickable(index >0);

        getView().setMoveRightClickable(index < data.size());

    }

    public void doMoveRight() {
        index ++;
        if (index > -1 && data.size() > index){
            DeviceCameraPersonFaceRsp.DataBean bean = data.get(index);
            LatLng latLng = new LatLng(bean.getLatitude(), bean.getLongitude());

            loadAvatar(bean,latLng,-1);

            getView().removeAvatarMarker();
            if(preBean != null){
                LatLng preLatLng = new LatLng(preBean.getLatitude(), preBean.getLongitude());
//                addNormalMarker(preLatLng);

                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.add(preLatLng,latLng)
                        .width(AppUtils.dp2px(mActivity,4))
                        .setCustomTexture(BitmapDescriptorFactory.defaultMarker());
                getView().addPolyLine(polylineOptions);
            }
            preBean = bean;

        }
        checkLeftRightStatus();
    }
}
