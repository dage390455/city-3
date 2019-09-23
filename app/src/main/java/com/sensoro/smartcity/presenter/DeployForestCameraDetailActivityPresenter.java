package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

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
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.iwidget.IOnStart;
import com.sensoro.common.model.DeployAnalyzerModel;
import com.sensoro.common.model.DeployContactModel;
import com.sensoro.common.model.DeployResultModel;
import com.sensoro.common.model.EventData;
import com.sensoro.common.model.ImageItem;
import com.sensoro.common.server.bean.DeployCameraUploadInfo;
import com.sensoro.common.server.bean.DeployPicInfo;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.utils.LogUtils;
import com.sensoro.common.utils.RegexUtils;
import com.sensoro.common.widgets.uploadPhotoUtil.UpLoadPhotosUtils;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.DeployForestCameraInstallPositionActivity;
import com.sensoro.smartcity.activity.DeployMapActivity;
import com.sensoro.smartcity.activity.DeployMapENActivity;
import com.sensoro.smartcity.activity.DeployMonitorAlarmContactActivity;
import com.sensoro.smartcity.activity.DeployMonitorNameAddressActivity;
import com.sensoro.smartcity.activity.DeployResultActivity;
import com.sensoro.smartcity.imainviews.IDeployForestCameraDetailActivityView;
import com.sensoro.smartcity.util.DeployRetryUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class DeployForestCameraDetailActivityPresenter extends BasePresenter<IDeployForestCameraDetailActivityView> implements IOnCreate, IOnStart, Constants {
    private Activity mContext;
    private Handler mHandler;
    private DeployAnalyzerModel deployAnalyzerModel;
    private DeployRetryUtil deployRetryUtil;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        mHandler = new Handler(Looper.getMainLooper());
        deployRetryUtil = DeployRetryUtil.getInstance();
        onCreate();
        Intent intent = mContext.getIntent();
        deployAnalyzerModel = (DeployAnalyzerModel) intent.getSerializableExtra(EXTRA_DEPLOY_ANALYZER_MODEL);
        getView().setNotOwnVisible(deployAnalyzerModel.notOwn);
        init();
    }

    private void init() {
        getView().setDeviceSn(mContext.getString(R.string.device_number) + deployAnalyzerModel.sn);
        if (!TextUtils.isEmpty(deployAnalyzerModel.nameAndAddress)) {
            getView().setNameAddressText(deployAnalyzerModel.nameAndAddress);
        }
        getView().setDeployPhotoVisible(true);
        getView().setDeployDeviceType("视频网关");
        getView().updateTagsData(deployAnalyzerModel.tagList);
        //联系人相关
        if (deployAnalyzerModel.deployContactModelList.size() > 0) {
            DeployContactModel deployContactModel = deployAnalyzerModel.deployContactModelList.get(0);
            String contact = deployContactModel.name;
            String content = deployContactModel.phone;
            getView().setFirstContact(contact + "(" + content + ")");
        }
        getView().setTotalContact(deployAnalyzerModel.deployContactModelList.size());
        //默认显示已定位
        if (TextUtils.isEmpty(deployAnalyzerModel.location)) {
            deployAnalyzerModel.address = mContext.getString(R.string.positioned);
        }
        //TODO 刷线摄像头状态
        getView().setUploadBtnStatus(checkCanUpload());
        //TODO 信息回查
        getView().setDeployInstallationLocation(deployAnalyzerModel.installationLocation);
//        getView().updateUploadTvText(mContext.getString(R.string.replacement_equipment));
        if (checkHasNoDeployPosition()) {
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


    //
    private void requestUpload() {
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


                        getView().showRetryDialog();
                        deployRetryUtil.addTask(deployAnalyzerModel);
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
//        RetrofitServiceHelper.getInstance().doUploadDeployCamera(deployAnalyzerModel.sn, deployAnalyzerModel.nameAndAddress, deployAnalyzerModel.tagList,
//                PreferencesHelper.getInstance().getUserData().phone, String.valueOf(lan), String.valueOf(lon), imgUrls, deployAnalyzerModel.address,
//                mMethodConfig.code, mOrientationConfig.code, deployAnalyzerModel.cameraStatus)
//                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                .safeSubscribe(new CityObserver<ResponseResult<DeployCameraUploadInfo>>(this) {
//                    @Override
//                    public void onCompleted(ResponseResult<DeployCameraUploadInfo> deployCameraUploadRsp) {
//                        freshSuccess(deployCameraUploadRsp);
//                        getView().dismissProgressDialog();
//                    }
//
//                    @Override
//                    public void onErrorMsg(int errorCode, String errorMsg) {
//                        if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
//                            getView().toastShort(errorMsg);
//                            deployRetryUtil.addTask(deployAnalyzerModel);
//                            getView().showRetryDialog();
//
//
//                        } else if (errorCode == 4013101 || errorCode == 4000013) {
//                            freshError(null, DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT);
//                        } else {
//                            freshError(errorMsg, DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED);
//                        }
//                        getView().dismissProgressDialog();
//                    }
//                });
    }

    // TODO: 2019-09-12 重试

//    public void doRetry() {
//        getView().showProgressDialog();
//        deployRetryUtil.retryTry(mContext, deployAnalyzerModel, new DeployRetryUtil.OnRetryListener() {
//            @Override
//            public void onStart() {
//                if (isAttachedView()) {
//                    getView().showStartUploadProgressDialog();
//                }
//            }
//
//            @Override
//            public void onComplete(List<ScenesData> scenesDataList) {
//
//            }
//
//            @Override
//            public void onError(String errMsg) {
//                if (isAttachedView()) {
//                    getView().setUploadBtnStatus(true);
//                    getView().dismissUploadProgressDialog();
//                    getView().toastShort(errMsg);
//                    //失败，本地照片存储,重试
//                    getView().showRetryDialog();
//                    getView().dismissProgressDialog();
//
//
//                }
//            }
//
//            @Override
//            public void onProgress(String content, double percent) {
//                if (isAttachedView()) {
//                    getView().showUploadProgressDialog(content, percent);
//                }
//            }
//
//
//            @Override
//            public void onDeployCompleted(DeployResultModel deployResultModel) {
//
//            }
//
//            @Override
//            public void onDeployCameraCompleted(ResponseResult<DeployCameraUploadInfo> deployCameraUploadRsp) {
//
//                freshSuccess(deployCameraUploadRsp);
//                getView().dismissProgressDialog();
//
//
//            }
//            @Override
//            public void onDeployErrorMsg(int errorCode, String errorMsg) {
//                if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
//                    getView().toastShort(errorMsg);
//                    deployRetryUtil.addTask(deployAnalyzerModel);
//                    getView().showRetryDialog();
//
//
//                } else if (errorCode == 4013101 || errorCode == 4000013) {
//                    freshError(null, DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT);
//                } else {
//                    freshError(errorMsg, DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED);
//                }
//                getView().dismissProgressDialog();
//
//            }
//
//
//            @Override
//            public void setDeployCameraStatus(String status) {
//                deployAnalyzerModel.cameraStatus = status;
//                getView().setDeployCameraStatus(deployAnalyzerModel.cameraStatus);
//                // TODO: 2019-09-12 摄像机部署 是否在线
//
////                deployRetryUtil.doUploadImages();
//
//
//
//            }
//            @Override
//            public void onUpdateDeviceStatus(ResponseResult<DeviceInfo> data) {
//            }
//
//            @Override
//            public void onGetDeviceRealStatusErrorMsg(int errorCode, String errorMsg) {
//            }
//
//
//        });
//
//
//    }

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
            case EVENT_DATA_DEPLOY_SETTING_FOREST_DEPLOY_INSTALL_POSITION:
                if (data instanceof String) {
                    deployAnalyzerModel.installationLocation = (String) data;
                    getView().setDeployInstallationLocation(deployAnalyzerModel.installationLocation);
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
                getView().setDeployPosition(checkHasNoDeployPosition(), deployAnalyzerModel.address);
                getView().setUploadBtnStatus(checkCanUpload());
                break;
            case Constants.EVENT_DATA_DEPLOY_SETTING_CONTACT:
                if (data instanceof List) {
                    //TODO 联系人
                    deployAnalyzerModel.deployContactModelList.clear();
                    deployAnalyzerModel.deployContactModelList.addAll((List<DeployContactModel>) data);
                    if (deployAnalyzerModel.deployContactModelList.size() > 0) {
                        DeployContactModel deployContactModel = deployAnalyzerModel.deployContactModelList.get(0);
                        String contact = deployContactModel.name;
                        String content = deployContactModel.phone;
                        getView().setFirstContact(contact + "(" + content + ")");
                    }
                    getView().setTotalContact(deployAnalyzerModel.deployContactModelList.size());
//                    getView().updateContactData(deployAnalyzerModel.deployContactModelList);
                }
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
                if (deployPicInfo.isRequired != null && deployPicInfo.isRequired) {
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
        //直接上传
        requestUpload();
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
        //联系人校验
        if (deployAnalyzerModel.deployContactModelList.size() > 0) {
            DeployContactModel deployContactModel = deployAnalyzerModel.deployContactModelList.get(0);
            if (TextUtils.isEmpty(deployContactModel.name) || TextUtils.isEmpty(deployContactModel.phone)) {
                return false;
            }
            if (!RegexUtils.checkPhone(deployContactModel.phone)) {
                return false;
            }
        } else {
            return false;
        }
        //照片校验
        if (getRealImageSize() <= 0) {
            return false;
        }
        //位置校验
        if (!checkHasNoDeployPosition()) {
            return false;
        }
        if (!checkHasNoDeployInstallPosition()) {
            return false;
        }

        return true;
    }

    private boolean checkHasNoDeployInstallPosition() {
        return !TextUtils.isEmpty(deployAnalyzerModel.installationLocation);
    }


    private boolean checkHasNoDeployPosition() {
        return deployAnalyzerModel.latLng.size() == 2;
    }


    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }


    public void doDeployInstallPosition() {
        Intent intent = new Intent(mContext, DeployForestCameraInstallPositionActivity.class);
        if (!TextUtils.isEmpty(deployAnalyzerModel.installationLocation)) {
            intent.putExtra(EXTRA_SETTING_FOREST_CAMERA_INSTALL_POSITION, deployAnalyzerModel.installationLocation);
        }
        getView().startAC(intent);
    }

    public void doAlarmContact() {
        Intent intent = new Intent(mContext, DeployMonitorAlarmContactActivity.class);
        if (deployAnalyzerModel.deployContactModelList.size() > 0) {
            intent.putExtra(Constants.EXTRA_SETTING_DEPLOY_CONTACT, (ArrayList<DeployContactModel>) deployAnalyzerModel.deployContactModelList);
        }
        getView().startAC(intent);
    }
}
