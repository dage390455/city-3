package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.geocoder.RegeocodeRoad;
import com.amap.api.services.geocoder.StreetNumber;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.AlarmHistoryLogActivity;
import com.sensoro.smartcity.activity.DeployMonitorConfigurationActivity;
import com.sensoro.smartcity.activity.MonitorPointElectricDetailActivity;
import com.sensoro.smartcity.activity.MonitorPointMapActivity;
import com.sensoro.smartcity.activity.MonitorPointMapENActivity;
import com.sensoro.smartcity.adapter.MonitorDetailOperationAdapter;
import com.sensoro.smartcity.adapter.model.EarlyWarningthresholdDialogUtilsAdapterModel;
import com.sensoro.smartcity.adapter.model.MonitoringPointRcContentAdapterModel;
import com.sensoro.smartcity.analyzer.DeployConfigurationAnalyzer;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.constant.MonitorPointOperationCode;
import com.sensoro.smartcity.factory.MonitorPointModelsFactory;
import com.sensoro.smartcity.imainviews.IMonitorPointElectricDetailActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.DeployAnalyzerModel;
import com.sensoro.smartcity.model.Elect3DetailModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.model.TaskOptionModel;
import com.sensoro.smartcity.push.ThreadPoolManager;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.DeployRecordInfo;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.bean.DeviceTypeStyles;
import com.sensoro.smartcity.server.bean.DisplayOptionsBean;
import com.sensoro.smartcity.server.bean.MalfunctionDataBean;
import com.sensoro.smartcity.server.bean.MalfunctionTypeStyles;
import com.sensoro.smartcity.server.bean.MergeTypeStyles;
import com.sensoro.smartcity.server.bean.MonitorOptionsBean;
import com.sensoro.smartcity.server.bean.MonitorPointOperationTaskResultInfo;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.server.bean.SensorStruct;
import com.sensoro.smartcity.server.bean.SensorTypeStyles;
import com.sensoro.smartcity.server.response.DeployRecordRsp;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.MonitorPointOperationRequestRsp;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.smartcity.widget.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.widget.imagepicker.ui.ImagePreviewDelActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MonitorPointElectricDetailActivityPresenter extends BasePresenter<IMonitorPointElectricDetailActivityView> implements IOnCreate, Constants, GeocodeSearch.OnGeocodeSearchListener, MonitorDetailOperationAdapter.OnMonitorDetailOperationAdapterListener {
    private Activity mContext;
    private DeviceInfo mDeviceInfo;

    private String content;
    private boolean hasPhoneNumber;
    private String mScheduleNo;
    private GeocodeSearch geocoderSearch;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable DeviceTaskOvertime = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(DeviceTaskOvertime);
            mScheduleNo = null;
            getView().dismissOperatingLoadingDialog();
            getView().showErrorTipDialog(mContext.getString(R.string.operation_request_time_out));

        }
    };
    private final ArrayList<EarlyWarningthresholdDialogUtilsAdapterModel> mEarlyWarningthresholdDialogUtilsAdapterModels = new ArrayList<>();

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        mDeviceInfo = (DeviceInfo) mContext.getIntent().getSerializableExtra(EXTRA_DEVICE_INFO);
        geocoderSearch = new GeocodeSearch(mContext);
        geocoderSearch.setOnGeocodeSearchListener(this);
        requestDeviceRecentLog();
    }

    private void freshTopData() {
        String sn = mDeviceInfo.getSn();
        getView().setSNText(sn);
        String typeName = mContext.getString(R.string.power_supply);
        String mergeType = mDeviceInfo.getMergeType();
        String deviceType = mDeviceInfo.getDeviceType();
        if (TextUtils.isEmpty(mergeType)) {
            mergeType = WidgetUtil.handleMergeType(deviceType);
        }
        DeviceTypeStyles configDeviceType = PreferencesHelper.getInstance().getConfigDeviceType(deviceType);
        if (configDeviceType != null) {
            String category = configDeviceType.getCategory();
            if (TextUtils.isEmpty(category)) {
                category = mContext.getString(R.string.unknown);
            }
            getView().setMonitorDetailTvCategory(category);
        }
        MergeTypeStyles mergeTypeStyles = PreferencesHelper.getInstance().getConfigMergeType(mergeType);
        if (mergeTypeStyles != null) {
            typeName = mergeTypeStyles.getName();
        }
        getView().setDeviceTypeName(typeName);
        refreshOperationStatus();
        String statusText;
        int textColor;
        switch (mDeviceInfo.getStatus()) {
            case SENSOR_STATUS_ALARM:
                textColor = mContext.getResources().getColor(R.color.c_f34a4a);
                statusText = mContext.getString(R.string.main_page_warn);
                break;
            case SENSOR_STATUS_NORMAL:
                textColor = mContext.getResources().getColor(R.color.c_29c093);
                statusText = mContext.getString(R.string.normal);
                break;
            case SENSOR_STATUS_LOST:
                textColor = mContext.getResources().getColor(R.color.c_5d5d5d);
                statusText = mContext.getString(R.string.status_lost);
                break;
            case SENSOR_STATUS_INACTIVE:
                textColor = mContext.getResources().getColor(R.color.c_b6b6b6);
                statusText = mContext.getString(R.string.status_inactive);
                break;
            case SENSOR_STATUS_MALFUNCTION:
                textColor = mContext.getResources().getColor(R.color.c_fdc83b);
                statusText = mContext.getString(R.string.status_malfunction);
                break;
            default:
                textColor = mContext.getResources().getColor(R.color.c_29c093);
                statusText = mContext.getString(R.string.normal);
                break;
        }
        String name = mDeviceInfo.getName();
        getView().setStatusInfo(statusText, textColor);
        getView().setTitleNameTextView(TextUtils.isEmpty(name) ? sn : name);
        //
        String contact = null;
        String phone = null;
        try {
            contact = mDeviceInfo.getAlarms().getNotification().getContact();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            phone = mDeviceInfo.getAlarms().getNotification().getContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(contact) && TextUtils.isEmpty(phone)) {
            getView().setNoContact();
            hasPhoneNumber = false;
        } else {
            if (TextUtils.isEmpty(contact)) {
                contact = mContext.getString(R.string.not_set);
            }
            hasPhoneNumber = !TextUtils.isEmpty(phone);
            getView().setContactPhoneIconVisible(hasPhoneNumber);
            if (hasPhoneNumber) {
                this.content = phone;
            } else {
                this.content = mContext.getString(R.string.not_set);
            }
            getView().setContractName(contact);
            getView().setContractPhone(content);
        }
        long updatedTime = mDeviceInfo.getUpdatedTime();
        if (updatedTime == 0) {
            getView().setUpdateTime("-");
        } else {
            getView().setUpdateTime(DateUtil.getStrTimeTodayByDevice(mContext, updatedTime));
        }
        String tags[] = mDeviceInfo.getTags();
        if (tags != null && tags.length > 0) {
            List<String> list = Arrays.asList(tags);
            getView().updateTags(list);

        }
        Map<String, SensorStruct> sensoroDetails = mDeviceInfo.getSensoroDetails();
        if (sensoroDetails != null) {
            SensorStruct batteryStruct = sensoroDetails.get("battery");
            if (batteryStruct != null) {
                String battery = batteryStruct.getValue().toString();
                if (battery.equals("-1.0") || battery.equals("-1")) {
                    getView().setBatteryInfo(mContext.getString(R.string.power_supply));
                } else {
                    getView().setBatteryInfo(WidgetUtil.subZeroAndDot(battery) + "%");
                }
            }
        }

        Integer interval = mDeviceInfo.getInterval();
        if (interval != null) {
            getView().setInterval(DateUtil.secToTimeBefore(mContext, interval));
        }

    }

    private void refreshOperationStatus() {
        int status = mDeviceInfo.getStatus();
        HashMap<String, TaskOptionModel> taskOptionModelMap = MonitorPointModelsFactory.createTaskOptionModelMap(status);
        //TODO 配置文件显示状态
        DeviceTypeStyles configDeviceType = PreferencesHelper.getInstance().getConfigDeviceType(mDeviceInfo.getDeviceType());
        if (configDeviceType != null) {
            List<String> taskOptions = configDeviceType.getTaskOptions();
            if (taskOptions != null && taskOptions.size() > 0) {
                ArrayList<TaskOptionModel> taskOptionModelList = new ArrayList<>();
                for (String string : taskOptions) {
                    TaskOptionModel taskOptionModel = taskOptionModelMap.get(string);
                    if (taskOptionModel != null) {
                        taskOptionModelList.add(taskOptionModel);
                    }
                }
                getView().setDeviceOperationVisible(true);
                getView().updateTaskOptionModelAdapter(taskOptionModelList);
            } else {
                getView().setDeviceOperationVisible(false);
            }
        }
    }

    private void freshLocationDeviceInfo() {
        double[] lonlat = mDeviceInfo.getLonlat();
        try {
            double v = lonlat[1];
            double v1 = lonlat[0];
            if (v == 0 || v1 == 0) {
                getView().setDeviceLocation(mContext.getString(R.string.not_positioned), false);
                getView().setDeviceLocationTextColor(R.color.c_a6a6a6);
                return;
            }
            RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(v, v1), 200, GeocodeSearch.AMAP);
            geocoderSearch.getFromLocationAsyn(query);
        } catch (Exception e) {
            e.printStackTrace();
            getView().setDeviceLocationTextColor(R.color.c_a6a6a6);
            getView().setDeviceLocation(mContext.getString(R.string.not_positioned), false);

        }

    }

    private void requestDeviceRecentLog() {
        String sn = mDeviceInfo.getSn();
        getView().showProgressDialog();
        //合并请求
        RetrofitServiceHelper.INSTANCE.getDeviceDetailInfoList(sn, null, 1).subscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>(this) {
            @Override
            public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {
                if (deviceInfoListRsp.getData().size() > 0) {
                    mDeviceInfo = deviceInfoListRsp.getData().get(0);
                }
                freshLocationDeviceInfo();
                freshTopData();
                handleDeviceInfoAdapter();
                getView().dismissProgressDialog();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
        //静默拉取图片记录内容
        RetrofitServiceHelper.INSTANCE.getDeployRecordList(mDeviceInfo.getSn(), null, null, null, null, null, 1, 0, true).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeployRecordRsp>(this) {
            @Override
            public void onCompleted(DeployRecordRsp recordRsp) {
                List<DeployRecordInfo> data = recordRsp.getData();
                if (data != null && data.size() > 0) {
                    DeployRecordInfo deployRecordInfo = data.get(0);
                    if (deployRecordInfo != null) {
                        List<String> deployPics = deployRecordInfo.getDeployPics();
                        if (deployPics != null) {
                            ArrayList<ScenesData> list = new ArrayList<>();
                            for (String url : deployPics) {
                                ScenesData scenesData = new ScenesData();
                                scenesData.url = url;
                                list.add(scenesData);
                            }
                            getView().updateMonitorPhotos(list);
                        }
                    }
                }

            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().toastShort(errorMsg);
            }
        });

    }

    private void handleDeviceInfoAdapter() {
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if (mDeviceInfo != null) {
                    if (mDeviceInfo.getStatus() == SENSOR_STATUS_MALFUNCTION) {
                        Map<String, MalfunctionDataBean> malfunctionData = mDeviceInfo.getMalfunctionData();
                        //TODO 添加故障字段数组
                        if (malfunctionData != null) {
                            final ArrayList<MonitoringPointRcContentAdapterModel> malfunctionBeanData = new ArrayList<>();
                            Set<String> keySet = malfunctionData.keySet();
                            ArrayList<String> keyList = new ArrayList<>();
                            for (String key : keySet) {
                                if (!keyList.contains(key)) {
                                    keyList.add(key);
                                }
                            }
                            Collections.sort(keyList);
                            for (String key : keyList) {
                                MonitoringPointRcContentAdapterModel monitoringPointRcContentAdapterModel = new MonitoringPointRcContentAdapterModel();
                                monitoringPointRcContentAdapterModel.name = mContext.getString(R.string.malfunction_cause_detail);
                                monitoringPointRcContentAdapterModel.statusColorId = R.color.c_fdc83b;
                                MalfunctionTypeStyles configMalfunctionMainTypes = PreferencesHelper.getInstance().getConfigMalfunctionMainTypes(key);
                                if (configMalfunctionMainTypes != null) {
                                    monitoringPointRcContentAdapterModel.content = configMalfunctionMainTypes.getName();
                                    malfunctionBeanData.add(monitoringPointRcContentAdapterModel);
                                    try {
                                        LogUtils.loge("故障成因：key = " + key + "value = " + monitoringPointRcContentAdapterModel.content);
                                    } catch (Throwable throwable) {
                                        throwable.printStackTrace();
                                    }
                                    break;
                                }
                                monitoringPointRcContentAdapterModel.content = mContext.getString(R.string.unknown);
                                malfunctionBeanData.add(monitoringPointRcContentAdapterModel);

                            }
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isAttachedView()) {
                                        getView().updateDeviceMalfunctionInfoAdapter(malfunctionBeanData);
                                    }

                                }
                            });
                        }
                    }
                    //
                    String deviceType = mDeviceInfo.getDeviceType();
                    boolean needTop = false;
                    DeviceTypeStyles configDeviceType = PreferencesHelper.getInstance().getConfigDeviceType(deviceType);
                    Map<String, SensorStruct> sensoroDetails = mDeviceInfo.getSensoroDetails();
                    final ArrayList<MonitoringPointRcContentAdapterModel> dataBean = new ArrayList<>();
                    if (configDeviceType != null) {
                        //预警阈值信息处理
                        List<MonitorOptionsBean> monitorOptions = configDeviceType.getMonitorOptions();
                        handleEarlyWarningThresholdModel(monitorOptions);
                        //特殊头部展示
                        DisplayOptionsBean displayOptions = configDeviceType.getDisplayOptions();
                        boolean hasAlarmStatus = false;
                        if (displayOptions != null) {
                            List<String> majors = displayOptions.getMajors();
                            if (majors != null && majors.size() > 0) {
                                String sensoroType = majors.get(0);
                                if (!TextUtils.isEmpty(sensoroType)) {
                                    // 控制头部
                                    needTop = true;
                                    final MonitoringPointRcContentAdapterModel model = MonitorPointModelsFactory.createMonitoringPointRcContentAdapterModel(mContext, mDeviceInfo, sensoroDetails, sensoroType);
                                    if (model != null) {
                                        mContext.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (isAttachedView()) {
                                                    if (TextUtils.isEmpty(model.unit)) {
                                                        getView().setTopElectData(model.content, model.statusColorId, model.name);
                                                    } else {
                                                        getView().setTopElectData(model.content + model.unit, model.statusColorId, model.name);
                                                    }
                                                }
                                            }
                                        });

                                    }
                                }
                            }
                            List<String> minors = displayOptions.getMinors();
                            if (minors != null && minors.size() > 0) {

                                for (String type : minors) {
                                    MonitoringPointRcContentAdapterModel model = MonitorPointModelsFactory.createMonitoringPointRcContentAdapterModel(mContext, mDeviceInfo, sensoroDetails, type);
                                    if (model != null) {
                                        if (TextUtils.isEmpty(model.content)) {
                                            model.content = "-";
                                        }
                                        if (model.hasAlarmStatus()) {
                                            hasAlarmStatus = true;
                                        }
                                        dataBean.add(model);
                                    }
                                }
                                // 控制展开
                                mContext.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isAttachedView()) {
                                            getView().updateDeviceInfoAdapter(dataBean);
                                        }
                                    }
                                });

                            }
                            // 控制九宫格显示
                            DisplayOptionsBean.SpecialBean special = displayOptions.getSpecial();
                            if (special != null) {
                                String type = special.getType();
                                if ("table".equals(type)) {
                                    final List<List<DisplayOptionsBean.SpecialBean.DataBean>> specialData = special.getData();
                                    if (specialData != null && specialData.size() >= 4) {
                                        List<DisplayOptionsBean.SpecialBean.DataBean> dataBeans0 = specialData.get(0);
                                        if (dataBeans0 != null && dataBeans0.size() >= 4) {
                                            DisplayOptionsBean.SpecialBean.DataBean dataBean01 = dataBeans0.get(1);
                                            final Elect3DetailModel elect3TopModel1 = MonitorPointModelsFactory.createElect3NameModel(mContext, 1, dataBean01);
                                            if (elect3TopModel1 != null) {

                                                mContext.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (isAttachedView()) {
                                                            getView().set3ElectTopDetail(elect3TopModel1);
                                                        }
                                                    }
                                                });
                                            }
                                            DisplayOptionsBean.SpecialBean.DataBean dataBean02 = dataBeans0.get(2);
                                            final Elect3DetailModel elect3TopModel2 = MonitorPointModelsFactory.createElect3NameModel(mContext, 2, dataBean02);
                                            if (elect3TopModel2 != null) {
                                                mContext.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (isAttachedView()) {
                                                            getView().set3ElectTopDetail(elect3TopModel2);
                                                        }
                                                    }
                                                });

                                            }
                                            DisplayOptionsBean.SpecialBean.DataBean dataBean03 = dataBeans0.get(3);
                                            final Elect3DetailModel elect3TopModel3 = MonitorPointModelsFactory.createElect3NameModel(mContext, 3, dataBean03);
                                            if (elect3TopModel3 != null) {
                                                mContext.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (isAttachedView()) {
                                                            getView().set3ElectTopDetail(elect3TopModel3);
                                                        }
                                                    }
                                                });

                                            }
                                        }
                                        List<DisplayOptionsBean.SpecialBean.DataBean> dataBeans1 = specialData.get(1);
                                        if (dataBeans1 != null && dataBeans1.size() >= 4) {
                                            DisplayOptionsBean.SpecialBean.DataBean dataBean10 = dataBeans1.get(0);
                                            final Elect3DetailModel elect3NameModel10 = MonitorPointModelsFactory.createElect3NameModel(mContext, 0, dataBean10);
                                            if (elect3NameModel10 != null) {
                                                mContext.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (isAttachedView()) {
                                                            getView().set3ElectVDetail(elect3NameModel10);
                                                        }
                                                    }
                                                });
                                            }
                                            DisplayOptionsBean.SpecialBean.DataBean dataBean11 = dataBeans1.get(1);
                                            final Elect3DetailModel elect3DetailModel1 = MonitorPointModelsFactory.createElect3DetailModel(mDeviceInfo, 1, dataBean11, sensoroDetails);
                                            if (elect3DetailModel1 != null) {
                                                if (elect3DetailModel1.hasAlarmStatus()) {
                                                    hasAlarmStatus = true;
                                                }
                                                mContext.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (isAttachedView()) {
                                                            getView().set3ElectVDetail(elect3DetailModel1);
                                                        }
                                                    }
                                                });

                                            }
                                            DisplayOptionsBean.SpecialBean.DataBean dataBean12 = dataBeans1.get(2);
                                            final Elect3DetailModel elect3DetailModel2 = MonitorPointModelsFactory.createElect3DetailModel(mDeviceInfo, 2, dataBean12, sensoroDetails);
                                            if (elect3DetailModel2 != null) {
                                                if (elect3DetailModel2.hasAlarmStatus()) {
                                                    hasAlarmStatus = true;
                                                }
                                                mContext.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (isAttachedView()) {
                                                            getView().set3ElectVDetail(elect3DetailModel2);
                                                        }
                                                    }
                                                });

                                            }
                                            DisplayOptionsBean.SpecialBean.DataBean dataBean13 = dataBeans1.get(3);
                                            final Elect3DetailModel elect3DetailModel3 = MonitorPointModelsFactory.createElect3DetailModel(mDeviceInfo, 3, dataBean13, sensoroDetails);
                                            if (elect3DetailModel3 != null) {
                                                if (elect3DetailModel3.hasAlarmStatus()) {
                                                    hasAlarmStatus = true;
                                                }
                                                mContext.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (isAttachedView()) {
                                                            getView().set3ElectVDetail(elect3DetailModel3);
                                                        }
                                                    }
                                                });

                                            }

                                        }
//
                                        List<DisplayOptionsBean.SpecialBean.DataBean> dataBeans2 = specialData.get(2);
                                        if (dataBeans2 != null && dataBeans2.size() >= 4) {
                                            DisplayOptionsBean.SpecialBean.DataBean dataBean20 = dataBeans2.get(0);
                                            final Elect3DetailModel elect3NameModel20 = MonitorPointModelsFactory.createElect3NameModel(mContext, 0, dataBean20);
                                            if (elect3NameModel20 != null) {
                                                mContext.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (isAttachedView()) {
                                                            getView().set3ElectADetail(elect3NameModel20);
                                                        }
                                                    }
                                                });
                                            }
                                            DisplayOptionsBean.SpecialBean.DataBean dataBean21 = dataBeans2.get(1);
                                            final Elect3DetailModel elect3DetailModel1 = MonitorPointModelsFactory.createElect3DetailModel(mDeviceInfo, 1, dataBean21, sensoroDetails);
                                            if (elect3DetailModel1 != null) {
                                                if (elect3DetailModel1.hasAlarmStatus()) {
                                                    hasAlarmStatus = true;
                                                }
                                                mContext.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (isAttachedView()) {
                                                            getView().set3ElectADetail(elect3DetailModel1);
                                                        }
                                                    }
                                                });

                                            }
                                            DisplayOptionsBean.SpecialBean.DataBean dataBean22 = dataBeans2.get(2);
                                            final Elect3DetailModel elect3DetailModel2 = MonitorPointModelsFactory.createElect3DetailModel(mDeviceInfo, 2, dataBean22, sensoroDetails);
                                            if (elect3DetailModel2 != null) {
                                                if (elect3DetailModel2.hasAlarmStatus()) {
                                                    hasAlarmStatus = true;
                                                }
                                                mContext.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (isAttachedView()) {
                                                            getView().set3ElectADetail(elect3DetailModel2);
                                                        }
                                                    }
                                                });

                                            }
                                            DisplayOptionsBean.SpecialBean.DataBean dataBean23 = dataBeans2.get(3);
                                            final Elect3DetailModel elect3DetailModel3 = MonitorPointModelsFactory.createElect3DetailModel(mDeviceInfo, 3, dataBean23, sensoroDetails);
                                            if (elect3DetailModel3 != null) {
                                                if (elect3DetailModel3.hasAlarmStatus()) {
                                                    hasAlarmStatus = true;
                                                }
                                                mContext.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (isAttachedView()) {
                                                            getView().set3ElectADetail(elect3DetailModel3);
                                                        }
                                                    }
                                                });

                                            }
                                        }

                                        List<DisplayOptionsBean.SpecialBean.DataBean> dataBeans3 = specialData.get(3);
                                        if (dataBeans3 != null && dataBeans3.size() >= 3) {
                                            DisplayOptionsBean.SpecialBean.DataBean dataBean30 = dataBeans3.get(0);
                                            final Elect3DetailModel elect3NameModel30 = MonitorPointModelsFactory.createElect3NameModel(mContext, 0, dataBean30);
                                            if (elect3NameModel30 != null) {
                                                mContext.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (isAttachedView()) {
                                                            getView().set3ElectTDetail(elect3NameModel30);
                                                        }
                                                    }
                                                });
                                            }
                                            DisplayOptionsBean.SpecialBean.DataBean dataBean31 = dataBeans3.get(1);
                                            final Elect3DetailModel elect3DetailModel1 = MonitorPointModelsFactory.createElect3DetailModel(mDeviceInfo, 1, dataBean31, sensoroDetails);
                                            if (elect3DetailModel1 != null) {
                                                if (elect3DetailModel1.hasAlarmStatus()) {
                                                    hasAlarmStatus = true;
                                                }
                                                mContext.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (isAttachedView()) {
                                                            getView().set3ElectTDetail(elect3DetailModel1);
                                                        }
                                                    }
                                                });

                                            }
                                            DisplayOptionsBean.SpecialBean.DataBean dataBean32 = dataBeans3.get(2);
                                            final Elect3DetailModel elect3DetailModel2 = MonitorPointModelsFactory.createElect3DetailModel(mDeviceInfo, 2, dataBean32, sensoroDetails);
                                            if (elect3DetailModel2 != null) {
                                                if (elect3DetailModel2.hasAlarmStatus()) {
                                                    hasAlarmStatus = true;
                                                }
                                                mContext.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (isAttachedView()) {
                                                            getView().set3ElectTDetail(elect3DetailModel2);
                                                        }
                                                    }
                                                });

                                            }
                                            DisplayOptionsBean.SpecialBean.DataBean dataBean33 = dataBeans3.get(3);
                                            final Elect3DetailModel elect3DetailModel3 = MonitorPointModelsFactory.createElect3DetailModel(mDeviceInfo, 3, dataBean33, sensoroDetails);
                                            if (elect3DetailModel3 != null) {
                                                if (elect3DetailModel3.hasAlarmStatus()) {
                                                    hasAlarmStatus = true;
                                                }
                                                mContext.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (isAttachedView()) {
                                                            getView().set3ElectTDetail(elect3DetailModel3);
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                        mContext.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (isAttachedView()) {
                                                    getView().setElect3DetailVisible(true);
                                                }
                                            }
                                        });
                                    }

                                }
                            }
                            final boolean finalHasAlarmStatus = hasAlarmStatus;
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isAttachedView()) {
                                        getView().setIvAlarmStatusVisible(finalHasAlarmStatus);
                                    }
                                }
                            });
                        }
                    }

                    if (needTop) {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isAttachedView()) {
                                    getView().setAcMonitoringElectPointLineVisible(true);
                                    getView().setLlElectTopVisible(true);
                                }
                            }
                        });
                    } else {
                        //容错处理 若无任何添加，尝试查找全部
                        if (dataBean.size() == 0 && configDeviceType != null) {
                            List<String> sensorTypes = configDeviceType.getSensorTypes();
                            if (sensorTypes != null && sensorTypes.size() > 0 && sensoroDetails != null) {
                                for (String type : sensorTypes) {
                                    MonitoringPointRcContentAdapterModel model = MonitorPointModelsFactory.createMonitoringPointRcContentAdapterModel(mContext, mDeviceInfo, sensoroDetails, type);
                                    if (model != null) {
                                        if (TextUtils.isEmpty(model.content)) {
                                            model.content = "-";
                                        }
                                        dataBean.add(model);
                                    }
                                }
                            }
                        }
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isAttachedView()) {
                                    getView().setElectDetailVisible(true);
                                    getView().updateDeviceInfoAdapter(dataBean);
                                    getView().setLlElectTopVisible(false);
                                }
                            }
                        });
                    }
                }

            }
        });

    }

    /**
     * 处理预警阈值信息
     *
     * @param monitorOptions
     */
    private void handleEarlyWarningThresholdModel(List<MonitorOptionsBean> monitorOptions) {
        synchronized (mEarlyWarningthresholdDialogUtilsAdapterModels) {
            if (mDeviceInfo != null) {
                AlarmInfo alarms = mDeviceInfo.getAlarms();
                final HashMap<String, AlarmInfo.RuleInfo> ruleInfoHashMap = new HashMap<>();
                //先填充数据
                if (alarms != null) {
                    AlarmInfo.RuleInfo rules[] = alarms.getRules();
                    if (rules != null && rules.length > 0) {
                        for (AlarmInfo.RuleInfo ruleInfo : rules) {
                            String sensorTypeStr = ruleInfo.getSensorTypes();
                            if (!TextUtils.isEmpty(sensorTypeStr)) {
                                String conditionType = ruleInfo.getConditionType();
                                if (TextUtils.isEmpty(conditionType)) {
                                    ruleInfoHashMap.put(sensorTypeStr, ruleInfo);
                                } else {
                                    ruleInfoHashMap.put(sensorTypeStr + conditionType, ruleInfo);
                                }


                            }
                        }
                    }

                }
                mEarlyWarningthresholdDialogUtilsAdapterModels.clear();
                boolean hasMonitorOptions = false;
                if (monitorOptions != null && monitorOptions.size() > 0) {
                    for (MonitorOptionsBean monitorOptionsBean : monitorOptions) {
                        EarlyWarningthresholdDialogUtilsAdapterModel earlyWarningthresholdDialogUtilsAdapterModel = new EarlyWarningthresholdDialogUtilsAdapterModel();
                        String name = monitorOptionsBean.getName();
                        if (TextUtils.isEmpty(name)) {
                            name = "";
//                            name = mContext.getString(R.string.unknown);
                        }
                        earlyWarningthresholdDialogUtilsAdapterModel.name = name;
                        List<MonitorOptionsBean.SensorTypesBean> sensorTypes = monitorOptionsBean.getSensorTypes();
                        StringBuilder stringBuilder = new StringBuilder();
                        for (MonitorOptionsBean.SensorTypesBean sensorTypeBean : sensorTypes) {
                            if (sensorTypeBean != null) {
                                hasMonitorOptions = true;
                                String key;
                                String id = sensorTypeBean.getId();
                                String conditionType = sensorTypeBean.getConditionType();
                                AlarmInfo.RuleInfo ruleInfo;
                                if (TextUtils.isEmpty(conditionType)) {
                                    key = id;
                                } else {
                                    key = id + conditionType;
                                }
                                ruleInfo = ruleInfoHashMap.get(key);
                                if (ruleInfo != null) {
                                    SensorTypeStyles configSensorType = PreferencesHelper.getInstance().getConfigSensorType(id);
                                    if (configSensorType != null) {
                                        id = configSensorType.getName();
                                        if (TextUtils.isEmpty(id)) {
                                            id = mContext.getString(R.string.unknown);
                                        }
                                        boolean bool = configSensorType.isBool();
                                        if (bool) {
                                            stringBuilder.append(configSensorType.getAlarm()).append(mContext.getString(R.string.is_alarm)).append("\n");
                                        } else {
                                            String unit = configSensorType.getUnit();
                                            float value = ruleInfo.getThresholds();
                                            Integer precision = configSensorType.getPrecision();
                                            String valueStr = String.valueOf(value);
                                            if (precision != null) {
                                                BigDecimal b = new BigDecimal(value);
                                                valueStr = b.setScale(precision, BigDecimal.ROUND_HALF_UP).toString();
                                            }
                                            if (!TextUtils.isEmpty(conditionType)) {
                                                String conditionTypeRule = ruleInfo.getConditionType();
                                                if (conditionType.equals(conditionTypeRule)) {
                                                    switch (conditionType) {
                                                        case "gt":
                                                            stringBuilder.append(id).append(" ").append(">=").append(" ").append(valueStr).append(unit);
                                                            break;
                                                        case "lt":
                                                            stringBuilder.append(id).append(" ").append("<=").append(" ").append(valueStr).append(unit);
                                                            break;
                                                    }
                                                    stringBuilder.append(" ").append(mContext.getString(R.string.is_alarm)).append("\n");
                                                }
                                            }

                                        }

                                    }

                                }
                            }

                        }
                        String content = stringBuilder.toString();
                        if (!TextUtils.isEmpty(content)) {
                            if (content.endsWith("\n")) {
                                content = content.substring(0, content.lastIndexOf("\n"));
                            }
                            earlyWarningthresholdDialogUtilsAdapterModel.content = content;
                            mEarlyWarningthresholdDialogUtilsAdapterModels.add(earlyWarningthresholdDialogUtilsAdapterModel);
                        }

                    }
                }
                final boolean finalHasMonitorOptions = hasMonitorOptions;
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isAttachedView()) {
                            getView().setElectInfoTipVisible(finalHasMonitorOptions);
                        }
                    }
                });
            }
        }

    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case EVENT_DATA_SOCKET_DATA_INFO:
                if (data instanceof DeviceInfo) {
                    final DeviceInfo pushDeviceInfo = (DeviceInfo) data;
                    if (pushDeviceInfo.getSn().equalsIgnoreCase(mDeviceInfo.getSn())) {
                        if (AppUtils.isActivityTop(mContext, MonitorPointElectricDetailActivity.class)) {
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isAttachedView()) {
                                        mDeviceInfo.cloneSocketData(pushDeviceInfo);
                                        // 单项数值设置
                                        if (isAttachedView()) {
                                            freshLocationDeviceInfo();
                                            freshTopData();
                                            handleDeviceInfoAdapter();
                                        }

                                    }
                                }
                            });
                        }
                    }
                }
                break;
            case EVENT_DATA_SOCKET_MONITOR_POINT_OPERATION_TASK_RESULT:
                if (data instanceof MonitorPointOperationTaskResultInfo) {
                    MonitorPointOperationTaskResultInfo info = (MonitorPointOperationTaskResultInfo) data;
                    final String scheduleNo = info.getScheduleNo();
                    if (!TextUtils.isEmpty(scheduleNo) && info.getTotal() == info.getComplete()) {
                        String[] split = scheduleNo.split(",");
                        if (split.length > 0) {
                            final String temp = split[0];
                            if (!TextUtils.isEmpty(temp)) {
                                if (AppUtils.isActivityTop(mContext, MonitorPointElectricDetailActivity.class)) {
                                    mContext.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!TextUtils.isEmpty(mScheduleNo) && mScheduleNo.equals(temp)) {
                                                mHandler.removeCallbacks(DeviceTaskOvertime);
                                                if (isAttachedView()) {
                                                    getView().dismissOperatingLoadingDialog();
                                                    getView().showOperationSuccessToast();
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }

                    }
                }
                break;
            case EVENT_DATA_DEVICE_POSITION_CALIBRATION:
                if (data instanceof DeviceInfo) {
                    final DeviceInfo pushDeviceInfo = (DeviceInfo) data;
                    if (pushDeviceInfo.getSn().equals(mDeviceInfo.getSn())) {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isAttachedView()) {
                                    mDeviceInfo.cloneSocketData(pushDeviceInfo);
                                    freshLocationDeviceInfo();
                                    freshTopData();
                                    handleDeviceInfoAdapter();
                                }
                            }
                        });
                    }
                }

                break;

        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
    }


    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
        String address;
        if (AppUtils.isChineseLanguage()) {
            address = regeocodeResult.getRegeocodeAddress().getFormatAddress();
            try {
                LogUtils.loge(this, "onRegeocodeSearched: " + "code = " + i + ",address = " + address);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            String subLoc = regeocodeAddress.getDistrict();// 区或县或县级市
            String ts = regeocodeAddress.getTownship();// 乡镇
            String thf = null;// 道路
            List<RegeocodeRoad> regeocodeRoads = regeocodeAddress.getRoads();// 道路列表
            if (regeocodeRoads != null && regeocodeRoads.size() > 0) {
                RegeocodeRoad regeocodeRoad = regeocodeRoads.get(0);
                if (regeocodeRoad != null) {
                    thf = regeocodeRoad.getName();
                }
            }
            String subthf = null;// 门牌号
            StreetNumber streetNumber = regeocodeAddress.getStreetNumber();
            if (streetNumber != null) {
                subthf = streetNumber.getNumber();
            }
            String fn = regeocodeAddress.getBuilding();// 标志性建筑,当道路为null时显示
            if (TextUtils.isEmpty(thf)) {
                if (!TextUtils.isEmpty(fn)) {
                    stringBuilder.append(fn);
                }
            }
            if (subLoc != null) {
                stringBuilder.append(subLoc);
            }
            if (ts != null) {
                stringBuilder.append(ts);
            }
            if (thf != null) {
                stringBuilder.append(thf);
            }
            if (subthf != null) {
                stringBuilder.append(subthf);
            }
            address = stringBuilder.toString();
            if (TextUtils.isEmpty(address)) {
                address = ts;
            }
        }
        if (TextUtils.isEmpty(address)) {
            address = mContext.getString(R.string.unknown_street);
        }
        mDeviceInfo.setAddress(address);
        if (isAttachedView()) {
            getView().setDeviceLocation(address, true);
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        try {
            LogUtils.loge(this, "onGeocodeSearched: " + "onGeocodeSearched");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void doNavigation() {
        double[] lonlat = mDeviceInfo.getLonlat();
        if (lonlat.length == 2) {
            double v = lonlat[1];
            double v1 = lonlat[0];
            if (v == 0 || v1 == 0) {
                getView().toastShort(mContext.getString(R.string.location_information_not_set));
                return;
            }
        } else {
            getView().toastShort(mContext.getString(R.string.location_information_not_set));
            return;
        }
        Intent intent = new Intent();
        if (AppUtils.isChineseLanguage()) {
            intent.setClass(mContext, MonitorPointMapActivity.class);
        } else {
            intent.setClass(mContext, MonitorPointMapENActivity.class);
        }
        intent.putExtra(EXTRA_DEVICE_INFO, mDeviceInfo);
        getView().startAC(intent);
    }

    public void doContact() {
        if (hasPhoneNumber) {
            if (TextUtils.isEmpty(content) || mContext.getString(R.string.not_set).equals(content)) {
                getView().toastShort(mContext.getString(R.string.phone_contact_not_set));
                return;
            }
            AppUtils.diallPhone(content, mContext);
        }

    }

    public void doMonitorHistory() {
        String sn = mDeviceInfo.getSn();
        Intent intent = new Intent(mContext, AlarmHistoryLogActivity.class);
        intent.putExtra(EXTRA_SENSOR_SN, sn);
        getView().startAC(intent);
    }

    public void doOperation(int type, String content, String diameter) {
        String operationType = null;
        Integer switchSpec = null;
        Double d = null;
        switch (type) {
            case MonitorPointOperationCode.ERASURE:
                operationType = "mute";
                break;
            case MonitorPointOperationCode.RESET:
                operationType = "reset";
                break;
            case MonitorPointOperationCode.PSD:
                operationType = "password";
                break;
            case MonitorPointOperationCode.QUERY:
                operationType = "view";
                break;
            case MonitorPointOperationCode.SELF_CHECK:
                operationType = "check";
                break;
            case MonitorPointOperationCode.AIR_SWITCH_CONFIG:
                operationType = "config";
                if (TextUtils.isEmpty(content)) {
                    getView().toastShort(mContext.getString(R.string.electric_current) + mContext.getString(R.string.input_not_null));
                    return;
                }
                try {
                    switchSpec = Integer.valueOf(content);
                    int[] ints = DeployConfigurationAnalyzer.analyzeDeviceType(mDeviceInfo.getDeviceType());
                    if (switchSpec < ints[0] || switchSpec > ints[1]) {
                        getView().toastShort(mContext.getString(R.string.electric_current) + String.format(Locale.CHINESE, "%s%d-%d", mContext.getString(R.string.monitor_point_operation_error_value_range), ints[0], ints[1]));
                        return;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    getView().toastShort(mContext.getString(R.string.electric_current) + mContext.getString(R.string.enter_the_correct_number_format));
                    return;
                }
                if (Constants.DEVICE_CONTROL_DEVICE_TYPES.contains(mDeviceInfo.getDeviceType())) {
                    if (TextUtils.isEmpty(diameter)) {
                        getView().toastShort(mContext.getString(R.string.diameter) + mContext.getString(R.string.input_not_null));
                        return;
                    }
                    try {
                        d = Double.parseDouble(diameter);
                        if (d < 0 || d >= 200) {
                            getView().toastShort(mContext.getString(R.string.diameter) + String.format(Locale.CHINESE, "%s%d-%d", mContext.getString(R.string.monitor_point_operation_error_value_range), 0, 200));
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        getView().toastShort(mContext.getString(R.string.diameter) + mContext.getString(R.string.enter_the_correct_number_format));
                        return;
                    }
                }

                break;
            case MonitorPointOperationCode.AIR_SWITCH_POWER_OFF:
                operationType = "open";
                //断电
                break;
            case MonitorPointOperationCode.AIR_SWITCH_POWER_ON:
                operationType = "close";
                //上电
                break;
        }

        requestCmd(operationType, switchSpec, d);
    }

    private void requestCmd(String operationType, Integer switchSpec, Double diameter) {
        ArrayList<String> sns = new ArrayList<>();
        sns.add(mDeviceInfo.getSn());
        getView().dismissTipDialog();
        getView().showOperationTipLoadingDialog();
        mScheduleNo = null;
        RetrofitServiceHelper.INSTANCE.doMonitorPointOperation(sns, operationType, null, null, switchSpec, null, diameter)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<MonitorPointOperationRequestRsp>(this) {
            @Override
            public void onCompleted(MonitorPointOperationRequestRsp response) {
                String scheduleNo = response.getScheduleNo();
                if (TextUtils.isEmpty(scheduleNo)) {
                    getView().dismissOperatingLoadingDialog();
                    getView().showErrorTipDialog(mContext.getString(R.string.monitor_point_operation_schedule_no_error));
                } else {
                    String[] split = scheduleNo.split(",");
                    if (split.length > 0) {
                        mScheduleNo = split[0];
                        mHandler.postDelayed(DeviceTaskOvertime, 10 * 1000);
                    } else {
                        getView().dismissOperatingLoadingDialog();
                        getView().showErrorTipDialog(mContext.getString(R.string.monitor_point_operation_schedule_no_error));

                    }
                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissOperatingLoadingDialog();
                getView().showErrorTipDialog(errorMsg);
            }
        });

    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    public void toPhotoDetail(int position, List<ScenesData> images) {
        if (images.size() > 0) {
            ArrayList<ImageItem> items = new ArrayList<>();
            for (ScenesData scenesData : images) {
                ImageItem imageItem = new ImageItem();
                imageItem.isRecord = false;
                imageItem.fromUrl = true;
                imageItem.path = scenesData.url;
                items.add(imageItem);
            }
            Intent intentPreview = new Intent(mContext, ImagePreviewDelActivity.class);
            intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, items);
            intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
            intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
            intentPreview.putExtra(EXTRA_JUST_DISPLAY_PIC, true);
            getView().startACForResult(intentPreview, REQUEST_CODE_PREVIEW);
        } else {
            getView().toastShort(mContext.getString(R.string.no_photos_added));
        }
    }

    public void showEarlyWarningThresholdDialogUtils() {

        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isAttachedView()) {
                    getView().updateEarlyWarningThresholdAdapterDialogUtils(mEarlyWarningthresholdDialogUtilsAdapterModels);
                }
            }
        });
    }

    @Override
    public void onClickOperation(View view, int position, TaskOptionModel taskOptionModel) {

        switch (taskOptionModel.optionType) {
            case MonitorPointOperationCode.ERASURE:
                getView().showTipDialog(false, null, R.string.is_device_erasure, R.string.device_erasure_tip_message, R.color.c_a6a6a6, R.string.erasure, R.color.c_f34a4a, MonitorPointOperationCode.ERASURE);
                break;
            case MonitorPointOperationCode.RESET:
                getView().showTipDialog(false, null, R.string.is_device_reset, R.string.device_reset_tip_message, R.color.c_a6a6a6, R.string.reset, R.color.c_f34a4a, MonitorPointOperationCode.RESET);
                break;
            case MonitorPointOperationCode.PSD:
                getView().showTipDialog(false, null, R.string.is_device_psd, R.string.device_psd_tip_message, R.color.c_a6a6a6, R.string.modify, R.color.c_f34a4a, MonitorPointOperationCode.PSD);
                break;
            case MonitorPointOperationCode.QUERY:
                getView().showTipDialog(false, null, R.string.is_device_query, R.string.device_query_tip_message, R.color.c_a6a6a6, R.string.monitor_point_detail_query, R.color.c_29c093, MonitorPointOperationCode.QUERY);
                break;
            case MonitorPointOperationCode.SELF_CHECK:
                getView().showTipDialog(false, null, R.string.is_device_self_check, R.string.device_self_check_tip_message, R.color.c_a6a6a6, R.string.self_check, R.color.c_29c093, MonitorPointOperationCode.SELF_CHECK);
                break;
            case MonitorPointOperationCode.AIR_SWITCH_CONFIG:
                //TODO 跳转阈值
                Intent intent = new Intent(mContext, DeployMonitorConfigurationActivity.class);
                intent.putExtra(EXTRA_DEPLOY_CONFIGURATION_ORIGIN_TYPE, DEPLOY_CONFIGURATION_SOURCE_TYPE_DEVICE_DETAIL);
                DeployAnalyzerModel deployAnalyzerModel = new DeployAnalyzerModel();
                deployAnalyzerModel.deviceType = mDeviceInfo.getDeviceType();
                deployAnalyzerModel.sn = mDeviceInfo.getSn();
                intent.putExtra(EXTRA_DEPLOY_ANALYZER_MODEL, deployAnalyzerModel);
                getView().startAC(intent);
//                getView().showTipDialog(true, mDeviceInfo.getDeviceType(), R.string.is_device_air_switch_config, R.string.device_air_switch_config_tip_message, R.color.c_a6a6a6, R.string.air_switch_config, R.color.c_f34a4a, MonitorPointOperationCode.AIR_SWITCH_CONFIG);
                break;
            case MonitorPointOperationCode.AIR_SWITCH_POWER_OFF:
                //断电
                getView().showTipDialog(false, mDeviceInfo.getDeviceType(), R.string.command_elec_disconnect_title, R.string.command_elec_disconnect_desc, R.color.c_f34a4a, R.string.command_elec_disconnect_btn_title, R.color.c_f34a4a, MonitorPointOperationCode.AIR_SWITCH_POWER_OFF);
                break;
            case MonitorPointOperationCode.AIR_SWITCH_POWER_ON:
                getView().showTipDialog(false, mDeviceInfo.getDeviceType(), R.string.command_elec_connect_title, R.string.command_elec_connect_desc, R.color.c_f34a4a, R.string.command_elec_connect_btn_title, R.color.c_f34a4a, MonitorPointOperationCode.AIR_SWITCH_POWER_ON);
                //上电
                break;
        }
    }
}
