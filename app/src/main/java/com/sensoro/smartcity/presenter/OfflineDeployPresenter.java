package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.DeployAnalyzerModel;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.DeviceInfo;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.OfflineDeployTaskDetailActivity;
import com.sensoro.smartcity.imainviews.IOfflineDeployActivityView;
import com.sensoro.smartcity.util.DeployRetryUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class OfflineDeployPresenter extends BasePresenter<IOfflineDeployActivityView> {
    private Activity mContext;
    private ArrayList<DeployAnalyzerModel> deviceInfos = new ArrayList<>();
    private DeployAnalyzerModel tempDeployAnalyzerModel;
    private boolean isBatch = false;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        List<String> deviceSN = new ArrayList<>();
        List<String> cameraSN = new ArrayList<>();
        LinkedHashMap<String, DeployAnalyzerModel> allTask = PreferencesHelper.getInstance().getOfflineDeployData();
        if (null != allTask && allTask.size() > 0) {
            getView().showProgressDialog();
            Iterator iter = allTask.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                DeployAnalyzerModel val = (DeployAnalyzerModel) entry.getValue();
                if (Constants.TYPE_SCAN_DEPLOY_CAMERA == val.deployType) {
                    cameraSN.add(val.sn);
                } else if (Constants.TYPE_SCAN_DEPLOY_STATION == val.deployType) {
                    //重新部署
                } else {
                    deviceSN.add(val.sn);
                }
                deviceInfos.add(val);
            }
            //TODO 查找设备的部署状态 走三个接口 设备 camera station 目前 camera接口数据不匹配 需要修改 station接口未提供
            LinkedHashMap<String, Long> longLinkedHashMap = new LinkedHashMap<>();
//            RetrofitServiceHelper.getInstance().getDeviceBriefInfoList(deviceSN, 1, 10000, null, null, null, null).subscribeOn
//                    (Schedulers.io()).flatMap(new Function<ResponseResult<List<DeviceInfo>>, ObservableSource<ResponseResult<List<DeviceCameraInfo>>>>() {
//                @Override
//                public ObservableSource<ResponseResult<List<DeviceCameraInfo>>> apply(ResponseResult<List<DeviceInfo>> listResponseResult) throws Exception {
//                    List<DeviceInfo> data = listResponseResult.getData();
//                    if (data != null && data.size() > 0) {
//                        for (DeviceInfo deviceInfo : data) {
//                            Long deployTime = deviceInfo.getDeployTime();
//                            if (deployTime != null) {
//                                String sn = deviceInfo.getSn();
//                                longLinkedHashMap.put(sn, deployTime);
//                            }
//                        }
//                    }
//                    return RetrofitServiceHelper.getInstance().getCameraList(cameraSN);
//                }
//            }).doOnNext(new Consumer<ResponseResult<List<DeviceCameraInfo>>>() {
//                @Override
//                public void accept(ResponseResult<List<DeviceCameraInfo>> responseResult) throws Exception {
//                    List<DeviceCameraInfo> data = responseResult.getData();
//                    if (data != null && data.size() > 0) {
//                        for (DeviceCameraInfo cameraInfo : data) {
//                            String sn = cameraInfo.getSn();
//                            Long createTime = cameraInfo.getCreateTime();
//                            if (createTime != null) {
//                                longLinkedHashMap.put(sn, createTime);
//                            }
//                        }
//                    }
//                }
//            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<DeviceCameraInfo>>>(this) {
//                @Override
//                public void onCompleted(ResponseResult<List<DeviceCameraInfo>> responseResult) {
//                    for (DeployAnalyzerModel deployAnalyzerModel : deviceInfos) {
//                        Long deployTimeLong = longLinkedHashMap.get(deployAnalyzerModel.sn);
//                        if (deployTimeLong != null) {
//                            deployAnalyzerModel.deployTime = deployTimeLong;
//                        }
//                    }
//                    getView().dismissProgressDialog();
//                    getView().updateAdapter(deviceInfos);
//                }
//
//                @Override
//                public void onErrorMsg(int errorCode, String errorMsg) {
//                    getView().dismissProgressDialog();
//                    getView().toastShort(errorMsg);
//
//                }
//            });
            RetrofitServiceHelper.getInstance().getDeviceBriefInfoList(deviceSN, 1, 10000, null, null, null, null).subscribeOn
                    (Schedulers.io()).doOnNext(new Consumer<ResponseResult<List<DeviceInfo>>>() {
                @Override
                public void accept(ResponseResult<List<DeviceInfo>> listResponseResult) throws Exception {
                    List<DeviceInfo> data = listResponseResult.getData();
                    if (data != null && data.size() > 0) {
                        for (DeviceInfo deviceInfo : data) {
                            Long deployTime = deviceInfo.getDeployTime();
                            if (deployTime != null) {
                                String sn = deviceInfo.getSn();
                                longLinkedHashMap.put(sn, deployTime);
                            }
                        }
                    }
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<DeviceInfo>>>(this) {
                @Override
                public void onCompleted(ResponseResult<List<DeviceInfo>> responseResult) {
                    for (DeployAnalyzerModel deployAnalyzerModel : deviceInfos) {
                        Long deployTimeLong = longLinkedHashMap.get(deployAnalyzerModel.sn);
                        if (deployTimeLong != null) {
                            deployAnalyzerModel.deployTime = deployTimeLong;
                        }
                    }
                    getView().dismissProgressDialog();
                    getView().updateAdapter(deviceInfos);
                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    getView().dismissProgressDialog();
                    getView().toastShort(errorMsg);

                }
            });
        } else {
            getView().updateAdapter(null);
        }

    }

    public void removeTask(DeployAnalyzerModel model) {
        DeployRetryUtil.getInstance().removeTask(model);
    }

    /**
     * 批量
     */
    public void doBatch() {
        if (deviceInfos.size() > 0) {
            DeployAnalyzerModel deployAnalyzerModel = deviceInfos.get(0);
            uploadTask(deployAnalyzerModel, true);
        }
    }


    /**
     * 强制
     */

    public void doForceUpload(int pos) {
        this.isBatch = false;
        DeployAnalyzerModel deployAnalyzerModel = deviceInfos.get(pos);
        if (null != deployAnalyzerModel) {
            tempDeployAnalyzerModel = deployAnalyzerModel;
            getView().setCurrentTaskIndex(deviceInfos.indexOf(deployAnalyzerModel));
            DeployRetryUtil.getInstance().doUploadImages(mContext, deployAnalyzerModel, retryListener);
        }
    }

    /**
     * 设备上传任务回调，区分摄像机（部署接口及判断是否在线）
     *
     * @param model
     * @param isbatch
     */

    public void uploadTask(DeployAnalyzerModel model, boolean isbatch) {

        this.tempDeployAnalyzerModel = model;
        this.isBatch = isbatch;
        if (isbatch) {
            getView().showProgressDialog();
            getView().setUploadClickable(false);
        }
        getView().setCurrentTaskIndex(deviceInfos.indexOf(model));
        DeployRetryUtil.getInstance().retryTry(mContext, model, retryListener);
    }


    /**
     * 设备上传任务回调，区分摄像机（部署接口及判断是否在线）
     */
    private DeployRetryUtil.OnRetryListener retryListener = new DeployRetryUtil.OnRetryListener() {
        @Override
        public void onDeployCompleted() {
            if (isAttachedView()) {
                deviceInfos.remove(tempDeployAnalyzerModel);
                getView().updateAdapter(deviceInfos);
                getView().setCurrentTaskIndex(-1);
                getView().toastLong(mContext.getResources().getString(R.string.successful_deployment));
                doNext();


            }

        }


        @Override
        public void onDeployErrorMsg(int errorCode, String errorMsg) {
            if (isAttachedView()) {

                if (isAttachedView()) {
                    if (errorCode == -1) {
                        tempDeployAnalyzerModel.getStateErrorMsg = errorMsg;
                        getView().notifyDataSetChanged();
                    } else {
                        getView().toastShort(errorMsg);
                    }
                    getView().setCurrentTaskIndex(-1);
                    getView().setUploadClickable(true);
                    doNext();

                }
            }


        }

        @Override
        public void onUpdateDeviceStatus(ResponseResult<DeviceInfo> data) {
            if (isAttachedView()) {

                if (data != null && data.getData() != null) {
                    int status = data.getData().getStatus();
                    tempDeployAnalyzerModel.realStatus = status;
                    long updatedTime = data.getData().getUpdatedTime();
                    //最后更新时间是否在此操作之前
                    if (tempDeployAnalyzerModel.lastOperateTime > updatedTime) {
                        showForceUpLoad(mContext.getResources().getString(R.string.nosignal));
                    } else {
                        /**
                         * 部署设备
                         */
                        if (tempDeployAnalyzerModel.signal.equals("normal") || tempDeployAnalyzerModel.signal.equals("good")) {
                            if (status != Constants.SENSOR_STATUS_ALARM && status != Constants.SENSOR_STATUS_MALFUNCTION) {
                                DeployRetryUtil.getInstance().doUploadImages(mContext, tempDeployAnalyzerModel, retryListener);
                            }
                        } else {
                            showForceUpLoad(mContext.getResources().getString(R.string.nosignal));
                        }

                    }
                    getView().notifyDataSetChanged();

                }

            }
        }

        /**
         * 没有权限、无信号、摄像机离线、显示强制上传
         */
        private void showForceUpLoad(String errorMsg) {

            if (PreferencesHelper.getInstance().getUserData().hasForceUpload) {
                tempDeployAnalyzerModel.isShowForce = true;

            }
            onDeployErrorMsg(-1, errorMsg);
        }


        // TODO: 2019-09-12 摄像机部署 是否在线
        @Override
        public void setDeployCameraStatus(String status) {

            if (!TextUtils.isEmpty(status)) {
                //在线
                if ("1".equals(status)) {
                    DeployRetryUtil.getInstance().doUploadImages(mContext, tempDeployAnalyzerModel, retryListener);
                } else {
                    showForceUpLoad(mContext.getResources().getString(R.string.offline));

                }
            } else {
                showForceUpLoad(mContext.getResources().getString(R.string.offline));
            }


        }


        @Override
        public void onStart() {

        }

        @Override
        public void onComplete(List<ScenesData> scenesDataList) {

        }

        @Override
        public void onError(String errMsg) {
            if (isAttachedView()) {

                getView().setCurrentTaskIndex(-1);
                getView().toastLong(errMsg);
                getView().dismissProgressDialog();
                getView().setUploadClickable(true);
            }


        }

        @Override
        public void onProgress(String content, double percent) {

        }
    };

    /**
     * 上传下一个
     */

    private void doNext() {

        if (isBatch) {
            int indexOf = deviceInfos.indexOf(tempDeployAnalyzerModel);
            if (indexOf >= 0 && deviceInfos.size() > indexOf + 1) {
                // TODO: 2019-09-12 判断是否已上传
                DeployAnalyzerModel nextModel = deviceInfos.get(indexOf + 1);

                uploadTask(nextModel, isBatch);
            } else {
                getView().dismissProgressDialog();

            }
        }
    }

    @Override

    public void onDestroy() {
        deviceInfos.clear();

    }

    public void doOfflineTaskDetail(DeployAnalyzerModel deployAnalyzerModel) {
        //TODO 跳转到离线上传详情界面
//        getView().toastShort("离线上传详情");
        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_DEPLOY_ANALYZER_MODEL, deployAnalyzerModel);
        intent.setClass(mContext, OfflineDeployTaskDetailActivity.class);
        getView().startAC(intent);
    }
}
