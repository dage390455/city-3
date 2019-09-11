package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.DeployAnalyzerModel;
import com.sensoro.common.model.DeployResultModel;
import com.sensoro.common.server.bean.DeviceInfo;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.common.server.response.ResponseResult;
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
        //TODO 大小写 问题 发黄不看着别扭吗 老铁
        //TODO 国际化
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
     * 上传任务
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
     * 任务回调
     */
    private DeployRetryUtil.OnRetryListener retryListener = new DeployRetryUtil.OnRetryListener() {
        @Override
        public void onCompleted(DeployResultModel deployResultModel) {
            if (isAttachedView()) {

                if (isBatch) {
                    //下一个
                    doNext(true);
                } else {
                    deviceInfos.remove(tempDeployAnalyzerModel);
                    getView().updateAdapter(deviceInfos);
                    getView().dismissProgressDialog();
                    getView().setCurrentTaskIndex(-1);
                    getView().toastLong("部署成功");


                }


            }

        }


        @Override
        public void onErrorMsg(int errorCode, String errorMsg) {
            if (isAttachedView()) {

                getView().setCurrentTaskIndex(-1);
                getView().dismissProgressDialog();
                getView().toastLong(errorMsg);
                getView().setUploadClickable(true);
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
                        //获取最新信号失败
                        showState();
                    } else {
                        if (status != Constants.SENSOR_STATUS_ALARM && status != Constants.SENSOR_STATUS_MALFUNCTION) {
                            deployRetryUtil.doUploadImages(mContext, tempDeployAnalyzerModel, retryListener);
                        } else {
                            showState();
                        }
                    }
                    getView().notifyDataSetChanged();

                }

            }
        }

        /**
         * 没有权限显示无信号否则显示强制上传
         */
        private void showState() {
            if (PreferencesHelper.getInstance().getUserData().hasForceUpload) {
                //显示强制上传
                tempDeployAnalyzerModel.isShowForce = true;

            }
            onGetDeviceRealStatusErrorMsg(-1, "无信号");
            getView().setCurrentTaskIndex(-1);
            getView().setUploadClickable(true);

            if (isBatch) {
                getView().dismissProgressDialog();
                doNext(false);
            }
        }

        @Override
        public void onGetDeviceRealStatusErrorMsg(int errorCode, String errorMsg) {

            if (isAttachedView()) {

                if (errorCode == -1) {
                    tempDeployAnalyzerModel.getStateErrorMsg = errorMsg;
                    getView().notifyDataSetChanged();
                } else {
                    getView().toastShort(errorMsg);
                }
                getView().setCurrentTaskIndex(-1);
                getView().setUploadClickable(true);
                if (isBatch) {
                    doNext(false);
                    getView().dismissProgressDialog();

                }
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
     *
     * @param isdelete 成功删除
     */
    private void doNext(boolean isdelete) {
        int indexOf = deviceInfos.indexOf(tempDeployAnalyzerModel);
        if (indexOf >= 0 && deviceInfos.size() > indexOf + 1) {
            DeployAnalyzerModel nextModel = deviceInfos.get(indexOf + 1);

            if (isdelete) {
                deviceInfos.remove(tempDeployAnalyzerModel);
                getView().updateAdapter(deviceInfos);
            }
            uploadTask(nextModel, isBatch);
        } else {
            if (isdelete) {
                deviceInfos.remove(tempDeployAnalyzerModel);
                getView().updateAdapter(deviceInfos);
                getView().setUploadClickable(true);
                getView().dismissProgressDialog();
                getView().setCurrentTaskIndex(-1);
                getView().toastLong("部署成功");
            }


        }
    }

    @Override

    public void onDestroy() {

        deviceInfos.clear();

    }

    public void doOfflineTaskDetail(DeployAnalyzerModel deployAnalyzerModel) {
        //TODO 跳转到离线上传详情界面
        getView().toastShort("离线上传详情");

    }
}
