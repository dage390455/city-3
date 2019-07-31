package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.internal.LinkedTreeMap;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.DeployAnalyzerModel;
import com.sensoro.common.model.DeployResultModel;
import com.sensoro.common.server.bean.DeviceInfo;
import com.sensoro.common.server.bean.MergeTypeStyles;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.smartcity.activity.DeployRepairInstructionActivity;
import com.sensoro.smartcity.imainviews.IOfflineDeployActivityView;
import com.sensoro.smartcity.util.DeployRetryUtil;
import com.sensoro.smartcity.util.WidgetUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OfflineDeployPresenter extends BasePresenter<IOfflineDeployActivityView> {
    private Activity mContext;
    private DeployRetryUtil deployRetryUtil;

    private ArrayList<DeployAnalyzerModel> deviceInfos = new ArrayList<>();
    private DeployAnalyzerModel tempdeployAnalyzerModel;
    private boolean isbatch;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;

        deployRetryUtil = DeployRetryUtil.getInstance();
//        LinkedHashMap<String, DeployAnalyzerModel> allTask = deployRetryUtil.getAllTask();
        LinkedTreeMap<String, DeployAnalyzerModel> allTask = PreferencesHelper.getInstance().getofflineDeployData();

        if (null != allTask && allTask.size() > 0) {
            Iterator iter = allTask.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
//                Object key = entry.getKey();
                DeployAnalyzerModel val = (DeployAnalyzerModel) entry.getValue();
                deviceInfos.add(val);
//                deviceInfos.add(val);
//                deviceInfos.add(val);
//                deviceInfos.add(val);
//                deviceInfos.add(val);
                getView().updateAdapter(deviceInfos);

            }

        }

    }

    public void removeTask(DeployAnalyzerModel model) {

//        deployRetryUtil.removeTask(model);
    }

    /**
     * 批量
     */
    public void dobatch() {

        if (deviceInfos.size() > 0) {
            DeployAnalyzerModel deployAnalyzerModel = deviceInfos.get(0);
            uploadTask(deployAnalyzerModel, true);
        } else {
//            getView().toastLong("暂无任务");

        }

    }


    /**
     * 强制
     */

    public void doForceUpload() {


        deployRetryUtil.doUploadImages(mContext, tempdeployAnalyzerModel, retryListener);

    }

    /**
     * 单个
     *
     * @param model
     */

    public void uploadTask(DeployAnalyzerModel model, boolean isbatch) {

        this.tempdeployAnalyzerModel = model;
        this.isbatch = isbatch;
        if (isbatch) {
            getView().showProgressDialog();
            getView().setUploadClickable(false);
        }
        getView().setCurrentTaskIndex(deviceInfos.indexOf(model));
        deployRetryUtil.retryTry(mContext, model, retryListener);
    }


    private DeployRetryUtil.OnRetryListener retryListener = new DeployRetryUtil.OnRetryListener() {
        @Override
        public void onCompleted(DeployResultModel deployResultModel) {
            deviceInfos.remove(tempdeployAnalyzerModel);
//                deployRetryUtil.removeTask(model);
            getView().updateAdapter(deviceInfos);
            if (isbatch) {
                if (deviceInfos.size() > 0) {
                    uploadTask(deviceInfos.get(0), isbatch);
                } else {
                    getView().setUploadClickable(true);
                    getView().dismissProgressDialog();
                    getView().toastLong("部署成功");

                }
            } else {
                getView().dismissProgressDialog();
                getView().setCurrentTaskIndex(-1);
                getView().toastLong("部署成功");


            }


        }

        @Override
        public void onErrorMsg(int errorCode, String errorMsg) {
            getView().setCurrentTaskIndex(-1);
            getView().dismissProgressDialog();
            getView().toastLong(errorMsg);
            getView().setUploadClickable(true);


        }

        @Override
        public void onUpdateDeviceStatus(ResponseResult<DeviceInfo> data) {
            if (data != null && data.getData() != null) {
                //只记录当前的信号和状态
                tempdeployAnalyzerModel.status = data.getData().getStatus();
                getView().notifyDataSetChanged();
//                switch (data.getData().getStatus()) {
//                    case Constants.SENSOR_STATUS_ALARM:
//
//
//                        String alarmReason = handleAlarmReason(data.getData());
//                        getView().showWarnDialog(PreferencesHelper.getInstance().getUserData().hasForceUpload, alarmReason, mContext.getString(R.string.deploy_check_suggest_repair_instruction));
//                        break;
//                    case Constants.SENSOR_STATUS_MALFUNCTION:
//                        String reason = handleMalfunctionReason(data.getData());
//                        getView().showWarnDialog(PreferencesHelper.getInstance().getUserData().hasForceUpload, reason, mContext.getString(R.string.deploy_check_suggest_repair_instruction));
//                        break;
//                }
            }
        }

        @Override
        public void onGetDeviceRealStatusErrorMsg(int errorCode, String errorMsg) {


            //获取信号失败，显示失败原因，根据权限是否显示强制上传
            if (errorCode == -1) {
                tempdeployAnalyzerModel.getStateErrorMsg = errorMsg;
                getView().notifyDataSetChanged();


            } else {
                getView().toastShort(errorMsg);


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
            getView().setCurrentTaskIndex(-1);
            getView().toastLong(errMsg);
            getView().dismissProgressDialog();
            getView().setUploadClickable(true);


        }

        @Override
        public void onProgress(String content, double percent) {

        }
    };

//    private String handleMalfunctionReason(DeviceInfo deviceInfo) {
//        ArrayList<String> malfunctionBeanData = new ArrayList<>();
//        Map<String, MalfunctionDataBean> malfunctionData = deviceInfo.getMalfunctionData();
//        //TODO 添加故障字段数组
//        if (malfunctionData != null) {
//            LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
//            Set<Map.Entry<String, MalfunctionDataBean>> entrySet = malfunctionData.entrySet();
//            for (Map.Entry<String, MalfunctionDataBean> entry : entrySet) {
//                MalfunctionDataBean entryValue = entry.getValue();
//                if (entryValue != null) {
//                    Map<String, MalfunctionDataBean> details = entryValue.getDetails();
//                    if (details != null) {
//                        Set<String> keySet = details.keySet();
//                        linkedHashSet.addAll(keySet);
//                    }
//                }
//            }
//            ArrayList<String> keyList = new ArrayList<>(linkedHashSet);
//            Collections.sort(keyList);
//            for (String key : keyList) {
//                MalfunctionTypeStyles configMalfunctionSubTypes = PreferencesHelper.getInstance().getConfigMalfunctionSubTypes(key);
//                if (configMalfunctionSubTypes != null) {
//                    malfunctionBeanData.add(configMalfunctionSubTypes.getName());
//                }
//
//            }
//        }
//        StringBuilder sb = new StringBuilder(mContext.getString(R.string.device_is_malfunction));
//        if (malfunctionBeanData.size() > 0) {
//            sb.append(mContext.getString(R.string.reason)).append("：");
//            for (int i = 0; i < malfunctionBeanData.size(); i++) {
//                if (i == malfunctionBeanData.size() - 1) {
//                    sb.append(malfunctionBeanData.get(i)).append("，");
//                } else {
//                    sb.append(malfunctionBeanData.get(i)).append("、");
//                }
//            }
//        }
//        return sb.toString();
//    }

//    private String handleAlarmReason(DeviceInfo deviceInfo) {
//        StringBuilder sb = new StringBuilder(mContext.getString(R.string.device_is_alarm));
//        DeviceTypeStyles configDeviceType = PreferencesHelper.getInstance().getConfigDeviceType(deviceInfo.getDeviceType());
//        if (configDeviceType == null) {
//            return sb.toString();
//        }
//        Map<String, SensorStruct> sensoroDetails = deviceInfo.getSensoroDetails();
//        if (sensoroDetails != null && sensoroDetails.size() > 0) {
//            ArrayList<String> sensoroTypes = new ArrayList<>(sensoroDetails.keySet());
//            Collections.sort(sensoroTypes);
//            sb.append(mContext.getString(R.string.reason)).append("：");
//            for (String sensoroType : sensoroTypes) {
//                MonitoringPointRcContentAdapterModel model = MonitorPointModelsFactory.createMonitoringPointRcContentAdapterModel(mContext, deviceInfo, sensoroDetails, sensoroType);
//                if (model != null && model.hasAlarmStatus()) {
//                    SensorTypeStyles sensorTypeStyles = PreferencesHelper.getInstance().getConfigSensorType(sensoroType);
//                    if (sensorTypeStyles != null && sensorTypeStyles.isBool()) {
//                        sb.append(model.content);
//                    } else {
//                        sb.append(model.name).append(" ").append(model.content);
//                    }
//                    if (!TextUtils.isEmpty(model.unit)) {
//                        sb.append(model.unit);
//                    }
//                    sb.append("、");
//                }
//            }
//            String s = sb.toString();
//            if (s.endsWith("、")) {
//                s = s.substring(0, s.lastIndexOf("、"));
//            }
//            s += "，";
//            return s;
//        } else {
//            return sb.toString();
//        }
//    }


    public String getRepairInstructionUrl() {
        String mergeType = WidgetUtil.handleMergeType(tempdeployAnalyzerModel.deviceType);
        if (TextUtils.isEmpty(mergeType)) {
            return null;
        }
        MergeTypeStyles configMergeType = PreferencesHelper.getInstance().getConfigMergeType(mergeType);
        if (configMergeType == null) {
            return null;
        }
        return configMergeType.getFixSpecificationUrl();
    }

    /**
     * 跳转配置说明界面
     *
     * @param repairInstructionUrl
     */
    public void doInstruction(String repairInstructionUrl) {
        Intent intent = new Intent(mContext, DeployRepairInstructionActivity.class);
        intent.putExtra(Constants.EXTRA_DEPLOY_CHECK_REPAIR_INSTRUCTION_URL, repairInstructionUrl);
        getView().startAC(intent);
    }

    @Override

    public void onDestroy() {


    }
}
