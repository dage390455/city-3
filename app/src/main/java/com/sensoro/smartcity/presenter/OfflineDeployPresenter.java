package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.DeployAnalyzerModel;
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

public class OfflineDeployPresenter extends BasePresenter<IOfflineDeployActivityView> {
    private Activity mContext;
    private DeployRetryUtil deployRetryUtil;
    private ArrayList<DeployAnalyzerModel> deviceInfos = new ArrayList<>();
    private DeployAnalyzerModel tempDeployAnalyzerModel;
    private boolean isBatch = false;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        deployRetryUtil = DeployRetryUtil.getInstance();
        LinkedHashMap<String, DeployAnalyzerModel> allTask = PreferencesHelper.getInstance().getOfflineDeployData();
        if (null != allTask && allTask.size() > 0) {
            Iterator iter = allTask.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                DeployAnalyzerModel val = (DeployAnalyzerModel) entry.getValue();
                deviceInfos.add(val);

            }
//            Collections.reverse(deviceInfos);
            getView().updateAdapter(deviceInfos);

        } else {
            getView().updateAdapter(null);

        }

    }

    public void removeTask(DeployAnalyzerModel model) {
        deployRetryUtil.removeTask(model);
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
            deployRetryUtil.doUploadImages(mContext, deployAnalyzerModel, retryListener);
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
        deployRetryUtil.retryTry(mContext, model, retryListener);
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
                                deployRetryUtil.doUploadImages(mContext, tempDeployAnalyzerModel, retryListener);
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
                    deployRetryUtil.doUploadImages(mContext, tempDeployAnalyzerModel, retryListener);
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
