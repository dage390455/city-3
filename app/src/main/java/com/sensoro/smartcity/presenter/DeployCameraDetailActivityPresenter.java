package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.geocoder.RegeocodeRoad;
import com.amap.api.services.geocoder.StreetNumber;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.iwidget.IOnStart;
import com.sensoro.common.model.CameraFilterModel;
import com.sensoro.common.model.DeployAnalyzerModel;
import com.sensoro.common.model.DeployResultModel;
import com.sensoro.common.model.EventData;
import com.sensoro.common.model.ImageItem;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.DeployCameraUploadInfo;
import com.sensoro.common.server.bean.DeployPicInfo;
import com.sensoro.common.server.bean.DeviceCameraDetailInfo;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.widgets.SelectDialog;
import com.sensoro.common.widgets.uploadPhotoUtil.UpLoadPhotosUtils;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.DeployCameraLiveDetailActivity;
import com.sensoro.smartcity.activity.DeployMapActivity;
import com.sensoro.smartcity.activity.DeployMapENActivity;
import com.sensoro.smartcity.activity.DeployMonitorNameAddressActivity;
import com.sensoro.smartcity.activity.DeployResultActivity;
import com.sensoro.smartcity.imainviews.IDeployCameraDetailActivityView;
import com.sensoro.smartcity.model.DeployCameraConfigModel;
import com.sensoro.smartcity.util.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DeployCameraDetailActivityPresenter extends BasePresenter<IDeployCameraDetailActivityView> implements IOnCreate, IOnStart, Constants {
    private Activity mContext;
    private Handler mHandler;
    private DeployAnalyzerModel deployAnalyzerModel;
    private final Runnable checkCameraStatusTask = new Runnable() {
        @Override
        public void run() {
            //TODO 轮询查看摄像头状态？
            RetrofitServiceHelper.getInstance().getDeviceCamera(deployAnalyzerModel.sn.toUpperCase()).subscribeOn
                    (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<DeviceCameraDetailInfo>>(DeployCameraDetailActivityPresenter.this) {
                @Override
                public void onCompleted(ResponseResult<DeviceCameraDetailInfo> deviceCameraDetailRsp) {
                    DeviceCameraDetailInfo data = deviceCameraDetailRsp.getData();
                    if (data != null) {
                        DeviceCameraDetailInfo.CameraBean camera = data.getCamera();
                        if (camera != null) {
                            DeviceCameraDetailInfo.CameraBean.InfoBean info = camera.getInfo();
                            if (info != null) {
                                String deviceStatus = info.getDeviceStatus();
                                if (!TextUtils.isEmpty(deviceStatus)) {
                                    deployAnalyzerModel.cameraStatus = deviceStatus;
                                    getView().setDeployCameraStatus(deployAnalyzerModel.cameraStatus);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                }
            });
            mHandler.postDelayed(checkCameraStatusTask, 10 * 1000);
        }
    };
    private final List<DeployCameraConfigModel> deployMethods = new ArrayList<>();
    private final List<DeployCameraConfigModel> deployOrientations = new ArrayList<>();
    private DeployCameraConfigModel mOrientationConfig;
    private DeployCameraConfigModel mMethodConfig;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        mHandler = new Handler(Looper.getMainLooper());
        onCreate();
        Intent intent = mContext.getIntent();
        deployAnalyzerModel = (DeployAnalyzerModel) intent.getSerializableExtra(EXTRA_DEPLOY_ANALYZER_MODEL);
        getView().setNotOwnVisible(deployAnalyzerModel.notOwn);
        mHandler.postDelayed(checkCameraStatusTask, 5 * 1000);
        init();
        requestData();
    }

    private void init() {
        getView().setDeviceSn(mContext.getString(R.string.device_number) + deployAnalyzerModel.sn);
        if (!TextUtils.isEmpty(deployAnalyzerModel.nameAndAddress)) {
            getView().setNameAddressText(deployAnalyzerModel.nameAndAddress);
        }
        getView().setDeployPhotoVisible(true);
        getView().setDeployDeviceType("摄像机");
        getView().updateTagsData(deployAnalyzerModel.tagList);
        //默认显示已定位
        deployAnalyzerModel.address = mContext.getString(R.string.positioned);
        if (checkHasDeployPosition()) {
            GeocodeSearch geocoderSearch = new GeocodeSearch(mContext);
            geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                @Override
                public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                    String address = "";
                    if (i == 1000) {
                        RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();

                        StringBuilder stringBuilder = new StringBuilder();
                        //
                        String province = regeocodeAddress.getProvince();
                        //
                        String district = regeocodeAddress.getDistrict();// 区或县或县级市
                        //
                        //
                        String township = regeocodeAddress.getTownship();// 乡镇
                        //
                        String streetName = null;// 道路
                        List<RegeocodeRoad> regeocodeRoads = regeocodeAddress.getRoads();// 道路列表
                        if (regeocodeRoads != null && regeocodeRoads.size() > 0) {
                            RegeocodeRoad regeocodeRoad = regeocodeRoads.get(0);
                            if (regeocodeRoad != null) {
                                streetName = regeocodeRoad.getName();
                            }
                        }
                        //
                        String streetNumber = null;// 门牌号
                        StreetNumber number = regeocodeAddress.getStreetNumber();
                        if (number != null) {
                            String street = number.getStreet();
                            if (street != null) {
                                streetNumber = street + number.getNumber();
                            } else {
                                streetNumber = number.getNumber();
                            }
                        }
                        //
                        String building = regeocodeAddress.getBuilding();// 标志性建筑,当道路为null时显示
                        //区县
                        if (!TextUtils.isEmpty(province)) {
                            stringBuilder.append(province);
                        }
                        if (!TextUtils.isEmpty(district)) {
                            stringBuilder.append(district);
                        }
                        //乡镇
                        if (!TextUtils.isEmpty(township)) {
                            stringBuilder.append(township);
                        }
                        //道路
                        if (!TextUtils.isEmpty(streetName)) {
                            stringBuilder.append(streetName);
                        }
                        //标志性建筑
                        if (!TextUtils.isEmpty(building)) {
                            stringBuilder.append(building);
                        } else {
                            //门牌号
                            if (!TextUtils.isEmpty(streetNumber)) {
                                stringBuilder.append(streetNumber);
                            }
                        }
                        if (TextUtils.isEmpty(stringBuilder)) {
                            address = township;
                        } else {
                            address = stringBuilder.append("附近").toString();
                        }

                    } else {
                        address = mContext.getString(R.string.not_positioned);
                    }
                    if (TextUtils.isEmpty(address)) {
                        address = mContext.getString(R.string.unknown_street);
                    }
                    deployAnalyzerModel.address = address;


                    getView().setDeployPosition(true, deployAnalyzerModel.address);
                }

                @Override
                public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                }
            });
            //查询一次地址信息
            LatLonPoint lp = new LatLonPoint(deployAnalyzerModel.latLng.get(1), deployAnalyzerModel.latLng.get(0));
            RegeocodeQuery query = new RegeocodeQuery(lp, 200, GeocodeSearch.AMAP);
            geocoderSearch.getFromLocationAsyn(query);
        } else {
            getView().setDeployPosition(false, null);
        }
    }

    private void requestData() {
        getView().showProgressDialog();
        RetrofitServiceHelper.getInstance().getCameraFilter().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<CameraFilterModel>>>(this) {
            @Override
            public void onCompleted(ResponseResult<List<CameraFilterModel>> cameraFilterRsp) {
                List<CameraFilterModel> data = cameraFilterRsp.getData();
                if (data != null) {
                    for (CameraFilterModel cameraFilterModel : data) {
                        String key = cameraFilterModel.getKey();
                        if ("orientation".equalsIgnoreCase(key)) {
                            List<CameraFilterModel.ListBean> list = cameraFilterModel.getList();
                            if (list != null) {
                                for (CameraFilterModel.ListBean listBean : list) {
                                    DeployCameraConfigModel deployCameraConfigModel = new DeployCameraConfigModel();
                                    deployCameraConfigModel.code = listBean.getCode();
                                    deployCameraConfigModel.name = listBean.getName();
                                    deployOrientations.add(deployCameraConfigModel);
                                    if (deployAnalyzerModel.orientation != null && deployAnalyzerModel.orientation.equals(deployCameraConfigModel.code)) {
                                        mOrientationConfig = deployCameraConfigModel;
                                    }
                                }
                            }
                            //安装朝向
                        } else if ("installationMode".equalsIgnoreCase(key)) {
                            //安装方式
                            List<CameraFilterModel.ListBean> list = cameraFilterModel.getList();
                            if (list != null) {
                                for (CameraFilterModel.ListBean listBean : list) {
                                    DeployCameraConfigModel deployCameraConfigModel = new DeployCameraConfigModel();
                                    deployCameraConfigModel.name = listBean.getName();
                                    deployCameraConfigModel.code = listBean.getCode();
                                    deployMethods.add(deployCameraConfigModel);
                                    if (deployAnalyzerModel.installationMode != null && deployAnalyzerModel.installationMode.equals(deployCameraConfigModel.code)) {
                                        mMethodConfig = deployCameraConfigModel;
                                    }
                                }
                            }
                        }
                    }
                }
                echoDeviceInfo();
                getView().dismissProgressDialog();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    //回显设备信息
    private void echoDeviceInfo() {
        //TODO 刷线摄像头状态
        getView().setUploadBtnStatus(checkCanUpload());
        //TODO 信息回查
        if (mOrientationConfig == null || TextUtils.isEmpty(deployAnalyzerModel.orientation)) {
            getView().setDeployOrientation(null);
        } else {
            getView().setDeployOrientation(mOrientationConfig.name);
        }
        if (mMethodConfig == null || TextUtils.isEmpty(deployAnalyzerModel.installationMode)) {
            getView().setDeployMethod(null);
        } else {
            getView().setDeployMethod(mMethodConfig.name);
        }
        getView().setDeployCameraStatus(deployAnalyzerModel.cameraStatus);
//        getView().updateUploadTvText(mContext.getString(R.string.replacement_equipment));
    }

    //
    public void requestUpload() {
        final double lon = deployAnalyzerModel.latLng.get(0);
        final double lan = deployAnalyzerModel.latLng.get(1);
        doUploadImages(lon, lan);
    }


    private void doUploadImages(final double lon, final double lan) {
        if (getRealImageSize() > 0) {
            //TODO 图片提交
            final UpLoadPhotosUtils.UpLoadPhotoListener upLoadPhotoListener = new UpLoadPhotosUtils
                    .UpLoadPhotoListener() {

                @Override
                public void onStart() {
                    if (isAttachedView()) {
                        getView().showStartUploadProgressDialog();
                    }

                }

                @Override
                public void onComplete(List<ScenesData> scenesDataList) {
                    ArrayList<String> strings = new ArrayList<>();
                    for (ScenesData scenesData : scenesDataList) {
                        scenesData.type = "image";
                        strings.add(scenesData.url);
                    }
                    try {
                        LogUtils.loge(this, "上传成功--- size = " + strings.size());
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    if (isAttachedView()) {
                        getView().dismissUploadProgressDialog();
                        // 上传结果
                        doDeployResult(lon, lan, strings);
                    }


                }

                @Override
                public void onError(String errMsg) {
                    if (isAttachedView()) {
                        getView().updateUploadState(true);
                        getView().dismissUploadProgressDialog();
                        getView().toastShort(errMsg);
                    }

                }

                @Override
                public void onProgress(String content, double percent) {
                    if (isAttachedView()) {
                        getView().showUploadProgressDialog(content, percent);
                    }

                }
            };
            UpLoadPhotosUtils upLoadPhotosUtils = new UpLoadPhotosUtils(mContext, upLoadPhotoListener);
            ArrayList<ImageItem> list = new ArrayList<>();
            for (DeployPicInfo deployPicInfo : deployAnalyzerModel.images) {
                if (deployPicInfo != null) {
                    if (deployPicInfo.photoItem != null) {
                        list.add(deployPicInfo.photoItem);
                    }
                }
            }
            upLoadPhotosUtils.doUploadPhoto(list);
        } else {
            doDeployResult(lon, lan, null);
        }
    }

    private void doDeployResult(double lon, double lan, List<String> imgUrls) {
        //TODO 上传接口
        getView().showProgressDialog();
        //
        RetrofitServiceHelper.getInstance().doUploadDeployCamera(deployAnalyzerModel.sn, deployAnalyzerModel.nameAndAddress, deployAnalyzerModel.tagList,
                PreferencesHelper.getInstance().getUserData().phone, String.valueOf(lan), String.valueOf(lon), imgUrls, deployAnalyzerModel.address,
                mMethodConfig.code, mOrientationConfig.code, deployAnalyzerModel.cameraStatus)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .safeSubscribe(new CityObserver<ResponseResult<DeployCameraUploadInfo>>(this) {
                    @Override
                    public void onCompleted(ResponseResult<DeployCameraUploadInfo> deployCameraUploadRsp) {
                        freshSuccess(deployCameraUploadRsp);
                        getView().dismissProgressDialog();
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                            getView().toastShort(errorMsg);
                        } else if (errorCode == 4013101 || errorCode == 4000013) {
                            freshError(null, DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT);
                        } else {
                            freshError(errorMsg, DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED);
                        }
                        getView().dismissProgressDialog();
                    }
                });
    }

    private void freshError(String errorInfo, int resultCode) {
        //
        Intent intent = new Intent();
        intent.setClass(mContext, DeployResultActivity.class);
        DeployResultModel deployResultModel = new DeployResultModel();
        deployResultModel.sn = deployAnalyzerModel.sn;
        deployResultModel.resultCode = resultCode;
        deployResultModel.scanType = deployAnalyzerModel.deployType;
        deployResultModel.errorMsg = errorInfo;
        deployResultModel.address = deployAnalyzerModel.address;
        //部署时间出错选当期系统时间
        deployResultModel.updateTime = System.currentTimeMillis();
        deployResultModel.name = deployAnalyzerModel.nameAndAddress;
        intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
        getView().startAC(intent);
    }

    private void freshSuccess(ResponseResult<DeployCameraUploadInfo> deployCameraUploadRsp) {
        DeployResultModel deployResultModel = new DeployResultModel();
        Intent intent = new Intent(mContext, DeployResultActivity.class);
        //
        DeployCameraUploadInfo data = deployCameraUploadRsp.getData();
        deployResultModel.sn = deployAnalyzerModel.sn;
        deployResultModel.resultCode = DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS;
        deployResultModel.scanType = deployAnalyzerModel.deployType;
        deployResultModel.address = deployAnalyzerModel.address;
        String createTime = data.getCreateTime();
        deployResultModel.updateTime = System.currentTimeMillis();
        if (!TextUtils.isEmpty(createTime)) {
            try {
                deployResultModel.updateTime = Long.parseLong(createTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        deployResultModel.name = deployAnalyzerModel.nameAndAddress;
        intent.putExtra(EXTRA_DEPLOY_RESULT_MODEL, deployResultModel);
        getView().startAC(intent);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        deployAnalyzerModel.tagList.clear();
        deployAnalyzerModel.images.clear();
        mHandler.removeCallbacksAndMessages(null);

    }

    public void doNameAddress() {
        Intent intent = new Intent(mContext, DeployMonitorNameAddressActivity.class);
        if (!TextUtils.isEmpty(deployAnalyzerModel.nameAndAddress)) {
            intent.putExtra(EXTRA_SETTING_NAME_ADDRESS, deployAnalyzerModel.nameAndAddress);
        }
        getView().startAC(intent);
    }

    public void doTag() {
        Bundle bundle = new Bundle();
        if (deployAnalyzerModel.tagList.size() > 0) {
            bundle.putStringArrayList(EXTRA_SETTING_TAG_LIST, (ArrayList<String>) deployAnalyzerModel.tagList);
        }

        startActivity(ARouterConstants.ACTIVITY_DEPLOY_DEVICE_TAG, bundle, mContext);
    }

    public void doSettingPhoto() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_DEPLOY_TO_PHOTO, deployAnalyzerModel.images);
        bundle.putString(EXTRA_SETTING_DEPLOY_DEVICE_TYPE, "deploy_camera");
        startActivity(ARouterConstants.ACTIVITY_DEPLOY_DEVICE_PIC, bundle, mContext);

//        Intent intent = new Intent(mContext, DeployMonitorDeployPicActivity.class);
//        if (getRealImageSize() > 0) {
//            intent.putExtra(EXTRA_DEPLOY_TO_PHOTO,  );
//        }
//        intent.putExtra(EXTRA_SETTING_DEPLOY_DEVICE_TYPE, "deploy_camera");
//        getView().startAC(intent);
    }

    public void doDeployMap() {
        Intent intent = new Intent();
        if (AppUtils.isChineseLanguage()) {
            intent.setClass(mContext, DeployMapActivity.class);
        } else {
            intent.setClass(mContext, DeployMapENActivity.class);
        }
        deployAnalyzerModel.mapSourceType = DEPLOY_MAP_SOURCE_TYPE_DEPLOY_MONITOR_DETAIL;
        intent.putExtra(EXTRA_DEPLOY_ANALYZER_MODEL, deployAnalyzerModel);
        getView().startAC(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case EVENT_DATA_DEPLOY_RESULT_FINISH:
            case EVENT_DATA_DEPLOY_CHANGE_RESULT_CONTINUE:
            case EVENT_DATA_DEPLOY_RESULT_CONTINUE:
                getView().finishAc();
                break;
            case EVENT_DATA_DEPLOY_SETTING_NAME_ADDRESS:
                if (data instanceof String) {
                    deployAnalyzerModel.nameAndAddress = (String) data;
                    getView().setNameAddressText(deployAnalyzerModel.nameAndAddress);
                }
                getView().setUploadBtnStatus(checkCanUpload());
                break;
            case EVENT_DATA_DEPLOY_SETTING_TAG:
                if (data instanceof List) {
                    deployAnalyzerModel.tagList.clear();
                    deployAnalyzerModel.tagList.addAll((List<String>) data);
                    getView().updateTagsData(deployAnalyzerModel.tagList);
                }
                break;
            case EVENT_DATA_DEPLOY_SETTING_PHOTO:
                if (data instanceof List) {
                    deployAnalyzerModel.images.clear();

                    deployAnalyzerModel.images.addAll((ArrayList<DeployPicInfo>) data);

                    int realImageSize = getRealImageSize();
                    switch (realImageSize) {
                        case -1:
                            getView().setDeployPhotoText(mContext.getString(R.string.missing_required_photo));
                            break;
                        case 0:
                            getView().setDeployPhotoText(null);
                            break;
                        default:
                            getView().setDeployPhotoText(mContext.getString(R.string.added) + realImageSize + mContext.getString(R.string.images));
                            break;
                    }
                    getView().setUploadBtnStatus(checkCanUpload());
                }
                break;
            case EVENT_DATA_DEPLOY_MAP:
                if (data instanceof DeployAnalyzerModel) {
                    this.deployAnalyzerModel = (DeployAnalyzerModel) data;
                    //TODO 刷新数据状态
                }
                getView().setDeployPosition(checkHasDeployPosition(), deployAnalyzerModel.address);
                getView().setUploadBtnStatus(checkCanUpload());
                break;
            default:
                break;
        }
    }

    /**
     * -1为缺少必传照片
     *
     * @return
     */
    private int getRealImageSize() {
        int count = 0;
        boolean need = false;
        for (DeployPicInfo deployPicInfo : deployAnalyzerModel.images) {
            if (deployPicInfo != null) {
                if (deployPicInfo.isRequired != null &&deployPicInfo.isRequired) {
                    if (deployPicInfo.photoItem == null) {
                        need = true;
                    }
                }
                if (deployPicInfo.photoItem != null) {
                    count++;
                }
            }
        }
        if (count == 0) {
            return count;
        } else {
            if (need) {
                return -1;
            }
        }
        return count;
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    public void doConfirm() {
        if (checkHasCameraStatus()) {
            //直接上传
            requestUpload();
        } else {
            getView().showWarnDialog(PreferencesHelper.getInstance().getUserData().hasForceUpload);
        }
    }

    private boolean checkCanUpload() {
        String name_default = mContext.getString(R.string.tips_hint_name_address);
        if (TextUtils.isEmpty(deployAnalyzerModel.nameAndAddress) || deployAnalyzerModel.nameAndAddress.equals(name_default)) {
            return false;
        } else {
            byte[] bytes = new byte[0];
            try {
                bytes = deployAnalyzerModel.nameAndAddress.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (bytes.length > 48) {
                return false;
            }
        }
        //照片校验
        if (getRealImageSize() <= 0) {
            return false;
        }
        //经纬度校验
        if (!checkHasDeployPosition()) {
            return false;
        }
        if (!checkHasDeployMethod()) {
            return false;
        }
        if (!checkHasDeployOrientation()) {
            return false;
        }
        return true;
    }

    /**
     * 检查是否能强制上传
     */
    private boolean checkHasCameraStatus() {
        return "1".equals(deployAnalyzerModel.cameraStatus);
    }

    private boolean checkHasDeployMethod() {
        return mMethodConfig != null;
    }

    private boolean checkHasDeployOrientation() {
        return mOrientationConfig != null;
    }

    private boolean checkHasDeployPosition() {
        return deployAnalyzerModel.latLng.size() == 2;
    }


    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    public void doDeployMethod() {
        if (deployMethods.size() > 0) {
            handleMethod();
        } else {
            getView().showProgressDialog();
            RetrofitServiceHelper.getInstance().getCameraFilter().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<CameraFilterModel>>>(this) {
                @Override
                public void onCompleted(ResponseResult<List<CameraFilterModel>> cameraFilterRsp) {
                    List<CameraFilterModel> data = cameraFilterRsp.getData();
                    if (data != null) {
                        for (CameraFilterModel cameraFilterModel : data) {
                            String key = cameraFilterModel.getKey();
                            if ("installationMode".equalsIgnoreCase(key)) {
                                //安装方式
                                List<CameraFilterModel.ListBean> list = cameraFilterModel.getList();
                                if (list != null) {
                                    for (CameraFilterModel.ListBean listBean : list) {
                                        DeployCameraConfigModel deployCameraConfigModel = new DeployCameraConfigModel();
                                        deployCameraConfigModel.name = listBean.getName();
                                        deployCameraConfigModel.code = listBean.getCode();
                                        deployMethods.add(deployCameraConfigModel);
                                    }
                                }
                            }
                        }
                    }
                    handleMethod();
                    getView().dismissProgressDialog();
                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    getView().dismissProgressDialog();
                    getView().toastShort(errorMsg);
                }
            });
        }
    }

    public void doDeployOrientation() {
        if (deployOrientations.size() > 0) {
            handleOrientation();
        } else {
            getView().showProgressDialog();
            RetrofitServiceHelper.getInstance().getCameraFilter().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<CameraFilterModel>>>(this) {
                @Override
                public void onCompleted(ResponseResult<List<CameraFilterModel>> cameraFilterRsp) {
                    List<CameraFilterModel> data = cameraFilterRsp.getData();
                    if (data != null) {
                        for (CameraFilterModel cameraFilterModel : data) {
                            String key = cameraFilterModel.getKey();
                            if ("orientation".equalsIgnoreCase(key)) {
                                List<CameraFilterModel.ListBean> list = cameraFilterModel.getList();
                                if (list != null) {
                                    for (CameraFilterModel.ListBean listBean : list) {
                                        DeployCameraConfigModel deployCameraConfigModel = new DeployCameraConfigModel();
                                        deployCameraConfigModel.code = listBean.getCode();
                                        deployCameraConfigModel.name = listBean.getName();
                                        deployOrientations.add(deployCameraConfigModel);
                                    }
                                }
                                //安装朝向
                            }
                        }
                    }
                    handleOrientation();
                    getView().dismissProgressDialog();
                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    getView().dismissProgressDialog();
                    getView().toastShort(errorMsg);
                }
            });
        }

    }

    //处理安装朝向
    private void handleOrientation() {
        ArrayList<String> strings = new ArrayList<>();
        for (DeployCameraConfigModel deployMethod : deployOrientations) {
            strings.add(deployMethod.name);
        }
        AppUtils.showDialog(mContext, new SelectDialog.SelectDialogListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mOrientationConfig = deployOrientations.get(position);
                String orientation = mOrientationConfig.name;
                getView().setDeployOrientation(orientation);
                getView().setUploadBtnStatus(checkCanUpload());

            }
        }, strings, mContext.getResources().getString(R.string.deploy_camera_sets_lens_orientation));
    }

    //处理安装方式
    private void handleMethod() {
        ArrayList<String> strings = new ArrayList<>();
        for (DeployCameraConfigModel deployMethod : deployMethods) {
            strings.add(deployMethod.name);
        }
        AppUtils.showDialog(mContext, new SelectDialog.SelectDialogListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMethodConfig = deployMethods.get(position);
                String method = mMethodConfig.name;
                getView().setDeployMethod(method);
                getView().setUploadBtnStatus(checkCanUpload());
            }
        }, strings, mContext.getResources().getString(R.string.deploy_camera_install_method));
    }

    public void doDeployCameraLive() {
        if (checkHasCameraStatus()) {
            Intent intent = new Intent(mContext, DeployCameraLiveDetailActivity.class);
            intent.putExtra(EXTRA_DEPLOY_ANALYZER_MODEL, deployAnalyzerModel);
            getView().startAC(intent);
        }
    }
}
