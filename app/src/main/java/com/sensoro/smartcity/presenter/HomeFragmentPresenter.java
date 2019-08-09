package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.manger.ThreadPoolManager;
import com.sensoro.common.model.EventData;
import com.sensoro.common.model.NetWorkStateModel;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.RetryWithDelay;
import com.sensoro.common.server.bean.AlarmDeviceCountsBean;
import com.sensoro.common.server.bean.AlarmPopupDataBean;
import com.sensoro.common.server.bean.DeviceAlarmLogInfo;
import com.sensoro.common.server.bean.DeviceInfo;
import com.sensoro.common.server.bean.DeviceMergeTypesInfo;
import com.sensoro.common.server.bean.DeviceTypeCount;
import com.sensoro.common.server.bean.MergeTypeStyles;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.ContractEditorActivity;
import com.sensoro.smartcity.activity.MonitorPointElectricDetailActivity;
import com.sensoro.smartcity.activity.ScanActivity;
import com.sensoro.smartcity.activity.SearchMonitorActivity;
import com.sensoro.smartcity.adapter.MainHomeFragRcContentAdapter;
import com.sensoro.smartcity.analyzer.AlarmPopupConfigAnalyzer;
import com.sensoro.smartcity.imainviews.IHomeFragmentView;
import com.sensoro.smartcity.model.AlarmPopupModel;
import com.sensoro.smartcity.model.HomeTopModel;
import com.sensoro.smartcity.model.SortConditionModel;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.popup.AlarmLogPopUtils;
import com.sensoro.smartcity.widget.popup.AlarmLogPopUtils.DialogDisplayStatusListener;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class HomeFragmentPresenter extends BasePresenter<IHomeFragmentView> implements IOnCreate
        , DialogDisplayStatusListener {
    private Activity mContext;
    private final Handler mHandler = new Handler();
    private volatile int page = 1;
    private volatile boolean needAlarmPlay = false;
    private volatile boolean needRefreshContent = false;
    private volatile boolean needShowAlarmWindow = false;
    private volatile boolean needRefreshHeader = false;
    private volatile boolean needFreshAll = false;

    private volatile boolean needResetHeaderPosition=false;//重置header到第一个位置
    //
    private volatile int totalMonitorPoint;
    private int mSoundId;
    private SoundPool mSoundPool;
    //TODO 联动类型选择
    private volatile String mTypeSelectedType;

    private final ArrayList<String> mMergeTypes = new ArrayList<>();


    private volatile SortConditionModel mSelectedCondition;


    private final ArrayList<SortConditionModel> mSortConditionList = new ArrayList<>();


    private volatile int tempAlarmCount = 0;



    private volatile List<HomeTopModel> mHomeTopModels = new ArrayList<>();
    //
    private volatile HomeTopModel mCurrentHomeTopModel;
    //
    private final HomeTopModel alarmModel = new HomeTopModel();
    private final HomeTopModel malfunctionModel = new HomeTopModel();
    private final HomeTopModel normalModel = new HomeTopModel();
    private final HomeTopModel lostModel = new HomeTopModel();
    private final HomeTopModel inactiveModel = new HomeTopModel();


    public volatile List<DeviceInfo> mDeviceInfoList = new ArrayList<>();


    public List<HomeTopModel> getmHomeTopModels() {
        return mHomeTopModels;
    }

    public void setmHomeTopModels(List<HomeTopModel> mHomeTopModels) {
        this.mHomeTopModels = mHomeTopModels;
    }
    public SortConditionModel getmSelectedCondition() {
        return mSelectedCondition;
    }

    public void setmSelectedCondition(SortConditionModel mSelectedCondition) {
        this.mSelectedCondition = mSelectedCondition;
    }

    /**
     * 推送轮训
     */
    private final Runnable mTask = new Runnable() {
        @Override
        public void run() {
            //是否有权限
            if (PreferencesHelper.getInstance().getUserData().hasDeviceBrief) {
                //采用线程池处理
                ThreadPoolManager.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("scheduleRefresh", "run: 刷新数据！");
                        scheduleRefresh();

                    }
                });
            }
            mHandler.postDelayed(mTask, 3000);
        }
    };

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;


        onCreate();
        //
        alarmModel.status = Constants.SENSOR_STATUS_ALARM;
        normalModel.status = Constants.SENSOR_STATUS_NORMAL;
        lostModel.status = Constants.SENSOR_STATUS_LOST;
        inactiveModel.status = Constants.SENSOR_STATUS_INACTIVE;
        malfunctionModel.status = Constants.SENSOR_STATUS_MALFUNCTION;
        mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        final SoundPool.OnLoadCompleteListener listener = new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                requestInitData(true, true);
            }
        };

        mSoundPool.setOnLoadCompleteListener(listener);
        mSoundId = mSoundPool.load(context, R.raw.alarm, 1);
        mHandler.postDelayed(mTask, 3000);
        getView().setImvHeaderLeftVisible(false);


    }

    public void requestInitData(boolean needShowProgressDialog, boolean needToast) {
        if (!PreferencesHelper.getInstance().getUserData().hasDeviceBrief) {
            needFreshAll = false;
            return;
        }
        if (needShowProgressDialog) {
            getView().showProgressDialog();
        }
        try {
            LogUtils.loge(this, "刷新Top,内容数据： " + System.currentTimeMillis());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        RetrofitServiceHelper.getInstance().getDeviceTypeCount().subscribeOn(Schedulers
                .io()).flatMap(new Function<ResponseResult<DeviceTypeCount>, ObservableSource<ResponseResult<List<DeviceInfo>>>>() {
            @Override
            public ObservableSource<ResponseResult<List<DeviceInfo>>> apply(ResponseResult<DeviceTypeCount> deviceTypeCountRsp) throws Exception {



                int currentStatus = -1;//原有的显示中的type
                if (mCurrentHomeTopModel != null) {
                    currentStatus = mCurrentHomeTopModel.status;
                    mCurrentHomeTopModel=null;
                }



                mHomeTopModels.clear();
                alarmModel.clearData();
                malfunctionModel.clearData();
                normalModel.clearData();
                lostModel.clearData();
                inactiveModel.clearData();
                final int alarmCount = deviceTypeCountRsp.getData().getAlarm();
                int normal = deviceTypeCountRsp.getData().getNormal();
                int lostCount = deviceTypeCountRsp.getData().getOffline();
                int inactiveCount = deviceTypeCountRsp.getData().getInactive();
                int malfunctionCount = deviceTypeCountRsp.getData().getMalfunction();
                if (alarmCount > 0) {
                    alarmModel.value = alarmCount;
                    tempAlarmCount = alarmCount;
                    mHomeTopModels.add(alarmModel);
                }
                if (malfunctionCount > 0) {
                    malfunctionModel.value = malfunctionCount;
                    mHomeTopModels.add(malfunctionModel);
                }
                if (normal > 0) {
                    normalModel.value = normal;
                    mHomeTopModels.add(normalModel);
                }
                if (lostCount > 0) {
                    lostModel.value = lostCount;
                    mHomeTopModels.add(lostModel);
                }
                if (inactiveCount > 0) {
                    inactiveModel.value = inactiveCount;
                    mHomeTopModels.add(inactiveModel);
                }



                if(needResetHeaderPosition){
                  if(mHomeTopModels.size() > 0){
                      mCurrentHomeTopModel = mHomeTopModels.get(0);
                      currentStatus = mCurrentHomeTopModel.status;
                  }
                }else{
                    boolean isExisted=false;
                    //找到最新池子中的当前显示的对象
                    for (HomeTopModel  item:mHomeTopModels) {
                        if (currentStatus ==item.status) {
                            isExisted=true;
                            mCurrentHomeTopModel =item;
                            currentStatus = mCurrentHomeTopModel.status;
                            break;
                        }
                    }
                    //上次没有显示中的，默认显示第一个
                    if (!isExisted&& mHomeTopModels.size() > 0) {
                        mCurrentHomeTopModel = mHomeTopModels.get(0);
                        currentStatus = mCurrentHomeTopModel.status;
                    }
                }





                //
                totalMonitorPoint = alarmCount + normal + lostCount + inactiveCount + malfunctionCount;
                page = 1;
                try {
                    com.sensoro.common.utils.LogUtils.loge("切换登录---->>>" + totalMonitorPoint);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }


                return getDeviceInfoListByStateRspObservable(currentStatus, false);
            }

        })
                .retryWhen(new RetryWithDelay(2, 100))
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<DeviceInfo>>>(this) {
                                                                                                         @Override
                                                                                                         public void onCompleted(ResponseResult<List<DeviceInfo>> deviceInfoListRsp) {


                                                                                                             getView().setDetectionPoints(WidgetUtil.handlerNumber(String.valueOf(totalMonitorPoint)));
                                                                                                             getView().refreshHeaderData(needResetHeaderPosition,true, mHomeTopModels);
                                                                                                             getView().refreshContentData(true, false, mDeviceInfoList);


                                                                                                             if (mHomeTopModels.size() <= 1) {
                                                                                                                 getView().setImvHeaderLeftVisible(false);
                                                                                                                 getView().setImvHeaderRightVisible(false);
                                                                                                             }
                                                                                                             if (mHomeTopModels.size() > 0) {
                                                                                                                 updateHeaderTop(mCurrentHomeTopModel);
                                                                                                             }
                                                                                                             getView().dismissAlarmInfoView();
                                                                                                             getView().recycleViewRefreshComplete();
                                                                                                             getView().dismissProgressDialog();
                                                                                                             needFreshAll = false;
                                                                                                             needResetHeaderPosition=false;


                                                                                                             if(needShowAlarmWindow){
                                                                                                                 if (isAttachedView()) {
                                                                                                                     if (needAlarmPlay) {
                                                                                                                         playSound();
                                                                                                                     }
                                                                                                                     shoAlarmWindow();
                                                                                                                 }
                                                                                                                 needAlarmPlay = false;
                                                                                                             }

                                                                                                         }

                                                                                                         @Override
                                                                                                         public void onErrorMsg(int errorCode, String errorMsg) {

                                                                                                             needFreshAll = errorCode == ERR_CODE_NET_CONNECT_EX;
                                                                                                             needResetHeaderPosition = errorCode == ERR_CODE_NET_CONNECT_EX;

                                                                                                             if (errorCode == ERR_CODE_NET_CONNECT_EX) {//网络连接异常需要清空数据集合，显示空数据页面
                                                                                                                 mDeviceInfoList.clear();
                                                                                                             }
                                                                                                             getView().setDetectionPoints(WidgetUtil.handlerNumber(String.valueOf(totalMonitorPoint)));


                                                                                                             getView().refreshHeaderData(needResetHeaderPosition,true, mHomeTopModels);
                                                                                                             getView().refreshContentData(true, false, mDeviceInfoList);


                                                                                                             if (mHomeTopModels.size() <= 1) {
                                                                                                                 getView().setImvHeaderLeftVisible(false);
                                                                                                                 getView().setImvHeaderRightVisible(false);
                                                                                                             }
                                                                                                             if (mHomeTopModels.size() > 0) {
                                                                                                                 updateHeaderTop(mCurrentHomeTopModel);
                                                                                                             }

                                                                                                             if (needToast) {
                                                                                                                 getView().toastShort(errorMsg);
                                                                                                             }
                                                                                                             getView().dismissAlarmInfoView();
                                                                                                             getView().recycleViewRefreshComplete();
                                                                                                             getView().dismissProgressDialog();


                                                                                                         }
                                                                                                     }
        );
    }
    public void updateHeaderTop(HomeTopModel homeTopModel) {
        try {
            mCurrentHomeTopModel = homeTopModel;
            String currentDataStr = getCurrentDataStr();
            int currentColor = getCurrentColor();
            getView().setToolbarTitleBackgroundColor(currentColor);
            getView().setToolbarTitleCount(currentDataStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 根据设备状态获取设备列表
     *
     * @param status
     * @return
     */
    @NonNull
    private Observable<ResponseResult<List<DeviceInfo>>> getDeviceInfoListByStateRspObservable(int status, boolean isPageChanged) {
        Observable<ResponseResult<List<DeviceInfo>>> listObservable = RetrofitServiceHelper.getInstance().getDeviceBriefInfoList(mSelectedCondition==null?null:mSelectedCondition.order, mSelectedCondition==null?null:mSelectedCondition.sort, page, null, mTypeSelectedType, status, null).subscribeOn(Schedulers
                .io()).doOnNext(new Consumer<ResponseResult<List<DeviceInfo>>>() {
            @Override
            public void accept(ResponseResult<List<DeviceInfo>> deviceInfoListRsp) throws Exception {

                try {
                    LogUtils.loge( "xxxxxxxx" , "getDeviceInfoListByStateRspObservable==="+mDeviceInfoList.size()+"");
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                mDeviceInfoList.clear();
                List<DeviceInfo> data = deviceInfoListRsp.getData();
                if (data != null && data.size() > 0) {
                    mDeviceInfoList.addAll(data);
                }

            }

        });

        return listObservable;

    }


    /**
     * 顶部的滑动关联刷新底部的设备列表
     *
     * @param homeTopModel
     */
    public void freshContentView(HomeTopModel homeTopModel, boolean isPageChanged) {
        this.mCurrentHomeTopModel = homeTopModel;
        try {
            if (isAttachedView()) {

                page = 1;
                getView().showProgressDialog();
                getDeviceInfoListByStateRspObservable(mCurrentHomeTopModel.status, isPageChanged).retryWhen(new RetryWithDelay(2, 100)).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<DeviceInfo>>>(this) {
                    @Override
                    public void onCompleted(ResponseResult<List<DeviceInfo>> deviceInfoListRsp) {

                        getView().refreshContentData(false, isPageChanged, mDeviceInfoList);
                        getView().dismissProgressDialog();
                        getView().dismissAlarmInfoView();
                    }
                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        if (errorCode == ERR_CODE_NET_CONNECT_EX) {//网络连接异常需要清空数据集合，显示空数据页面
                            mDeviceInfoList.clear();
                            getView().refreshContentData(false, isPageChanged, mDeviceInfoList);
                        }
                        getView().toastShort(errorMsg);
                        getView().recycleViewRefreshComplete();
                        getView().dismissProgressDialog();
                        getView().dismissAlarmInfoView();
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    int  lastModelCount=0;//上一次header的数量，如果发生变化，需要跳转到header第一个位置
    /**
     * 请求剩余的数据
     */

    private void scheduleRefresh() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (needFreshAll) {
                        requestInitData(false, false);
                } else {
                    if (needRefreshContent) {
                        if (isAttachedView()) {
                            getView().refreshContentData(false, false, mDeviceInfoList);
                        }
                        needRefreshContent = false;
                    }
                    if (needRefreshHeader) {
                        if (isAttachedView()) {
                            getView().refreshHeaderData(false,false, mHomeTopModels);
                            getView().setDetectionPoints(WidgetUtil.handlerNumber(String.valueOf(totalMonitorPoint)));
                            if (needAlarmPlay) {
                                playSound();
                            }
                            shoAlarmWindow();
                        }
                        try {
                            LogUtils.loge("needRefreshHeader", "needRefreshHeader");
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                        needAlarmPlay = false;
                        needRefreshHeader = false;
                    }
                }

            }
        });
    }

    /**
     * 是否处理socke事件中的推送设备
     *
     * @param deviceInfo
     * @return
     */
    private boolean needHandleDevicePush(DeviceInfo deviceInfo) {
        //TODO 添加过滤未部署的逻辑
        boolean deployFlag = deviceInfo.isDeployFlag();
        if (deployFlag) {
            String mergeType = deviceInfo.getMergeType();
            if (TextUtils.isEmpty(mergeType)) {
                String deviceType = deviceInfo.getDeviceType();
                mergeType = WidgetUtil.handleMergeType(deviceType);
            }
            if (TextUtils.isEmpty(mTypeSelectedType)) {
                if(organizeJsonData(deviceInfo)!=null){//在里面，直接刷新一下
                    needRefreshContent=true;
                }
            } else {
                if (mTypeSelectedType.equalsIgnoreCase(mergeType)) {
                    if(organizeJsonData(deviceInfo)!=null){//在里面，直接刷新一下
                        needRefreshContent=true;
                    }
                }
            }
        }
        return deployFlag;
    }


    private void playSound() {
        if (!"admin".equals(PreferencesHelper.getInstance().getUserData().roles)) {
            mSoundPool.play(mSoundId, 1, 1, 0, 0, 1);
        }
    }


    public void clickItem(int position, HomeTopModel homeTopModel) {
        try {
            DeviceInfo deviceInfo = mDeviceInfoList.get(position);
            Intent intent = new Intent();
            intent.setClass(mContext, MonitorPointElectricDetailActivity.class);
            intent.putExtra(Constants.EXTRA_DEVICE_INFO, deviceInfo);
            intent.putExtra(Constants.EXTRA_SENSOR_NAME, deviceInfo.getName());
            intent.putExtra(Constants.EXTRA_SENSOR_TYPES, deviceInfo.getSensorTypes());
            intent.putExtra(Constants.EXTRA_SENSOR_STATUS, deviceInfo.getStatus());
            intent.putExtra(Constants.EXTRA_SENSOR_TIME, deviceInfo.getUpdatedTime());
//            Double[] lonlat = (Double[]) ;

            intent.putExtra(Constants.EXTRA_SENSOR_LOCATION, deviceInfo.getLonlat().toArray());
            getView().startAC(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mSoundPool != null) {
            mSoundPool.unload(mSoundId);
            mSoundPool.stop(mSoundId);
            mSoundPool.release();
            mSoundPool.setOnLoadCompleteListener(null);
            mSoundPool = null;
        }
        mHandler.removeCallbacksAndMessages(null);
        mHomeTopModels.clear();
    }

    //子线程处理
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(DeviceInfo deviceInfo) {

        try {
            LogUtils.loge("onMessageEvent_DeviceInfo", "" + deviceInfo.getSn());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        needHandleDevicePush(deviceInfo);

    }

    //子线程处理
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(AlarmDeviceCountsBean alarmDeviceCountsBean) {
        try {
            LogUtils.loge("requestInitData", "刷新Top,当前状态： " + mCurrentHomeTopModel.status);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        int  lastModelCount=mHomeTopModels.size();


        int currentAlarmCount = alarmDeviceCountsBean.get_$0();
        int normalCount = alarmDeviceCountsBean.get_$1();
        int lostCount = alarmDeviceCountsBean.get_$2();
        int inactiveCount = alarmDeviceCountsBean.get_$3();
        int malfunctionCount = alarmDeviceCountsBean.get_$4();
        boolean isneedFresh = false;
        if (currentAlarmCount != alarmModel.value || normalCount != normalModel.value || lostCount != lostModel.value || inactiveCount != inactiveModel.value || malfunctionCount != malfunctionModel.value) {
            isneedFresh = true;
        }
        //
        if (tempAlarmCount == 0 && currentAlarmCount > 0) {
            needAlarmPlay = true;
        }
        try {
            LogUtils.loge("malfunctionCount = " + malfunctionCount);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        needShowAlarmWindow = currentAlarmCount > tempAlarmCount;
        try {
            LogUtils.loge("EVENT_DATA_SOCKET_DATA_COUNT-->> tempAlarmCount = " + tempAlarmCount + ",currentAlarmCount = " + currentAlarmCount + ",mCurrentHomeTopModel.type = " + mCurrentHomeTopModel.status);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        tempAlarmCount = currentAlarmCount;
        //
        mHomeTopModels.clear();
        if (currentAlarmCount > 0) {
            alarmModel.value = tempAlarmCount;
            mHomeTopModels.add(alarmModel);
        }
        if (malfunctionCount > 0) {
            malfunctionModel.value = malfunctionCount;
            mHomeTopModels.add(malfunctionModel);
        }
        if (normalCount > 0) {
            normalModel.value = normalCount;
            mHomeTopModels.add(normalModel);
        }
        if (lostCount > 0) {
            lostModel.value = lostCount;
            mHomeTopModels.add(lostModel);
        }
        if (inactiveCount > 0) {
            inactiveModel.value = inactiveCount;
            mHomeTopModels.add(inactiveModel);
        }
        //比较header数量是否发生变化
        if(lastModelCount!=mHomeTopModels.size()){
            needResetHeaderPosition=true;
        }
        try {
            LogUtils.loge( "needResetHeaderPosition" , needResetHeaderPosition+"");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        totalMonitorPoint = currentAlarmCount + normalCount + lostCount + inactiveCount + malfunctionCount;


//        needRefreshHeader=isneedFresh;
        needFreshAll=isneedFresh;



        try {
            LogUtils.loge("onMessageEventneedFresh","EVENT_DATA_SOCKET_DATA_COUNT-->> tempAlarmCount = " + tempAlarmCount + ",currentAlarmCount = " + currentAlarmCount + ",mCurrentHomeTopModel.type = " + mCurrentHomeTopModel.status);
            LogUtils.loge("onMessageEventneedFresh", "needFreshAll:" + needFreshAll);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    //子线程处理
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(int code) {
        if (Constants.EVENT_DATA_DEVICE_SOCKET_FLUSH == code) {
            //TODO
            needFreshAll = true;
            try {
                LogUtils.loge("EVENT_DATA_DEVICE_SOCKET_FLUSH --->> 添加、删除、迁移设备");
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    //子线程处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {


        //后台线程处理消息
        int code = eventData.code;
        Object data = eventData.data;


        switch (code) {
            case Constants.EVENT_DATA_DEPLOY_RESULT_FINISH:
                break;
            case Constants.EVENT_DATA_SEARCH_MERCHANT:
                if (isAttachedView()) {
                    try {
                        LogUtils.loge("changeuser","切换账户");
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    needResetHeaderPosition=true;
                    requestInitData(true, true);
                }
                break;
            case Constants.EVENT_DATA_LOCK_SCREEN_ON:
                //TODO 暂时不加
                if (data instanceof Boolean) {
//                    needFreshAll = (boolean) data;
                }
                try {
                    LogUtils.loge("EVENT_DATA_LOCK_SCREEN_ON --->> 手机亮屏");
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NetWorkStateModel netWorkStateModel) {
        if (netWorkStateModel != null) {
            if (!netWorkStateModel.ping) {
                //TODO 暂时不加
//                needFreshAll = true;
                try {
                    LogUtils.loge("CONNECTIVITY_ACTION --->> 网络断开 ");
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }
    }

    private void shoAlarmWindow() {

        //这里是为了控制显示问题，暂时采用延时方式
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
//                    int position = getView().getFirstVisibleItemPosition();
//                    requestDataByStatus(mHomeTopModels.get(position));
                    requestDataByStatus(mCurrentHomeTopModel);
                    try {
                        LogUtils.loge("mCurrentHomeTopModel.type = " + mCurrentHomeTopModel.status + ",mCurrentHomeTopModel.value = " + mCurrentHomeTopModel.value);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    try {
                        LogUtils.loge("shoAlarmWindow", "shoAlarmWindow");
                        LogUtils.loge("shoAlarmWindow", needShowAlarmWindow + "");
                        LogUtils.loge("shoAlarmWindow", mCurrentHomeTopModel.status + "");
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }

                    if (needShowAlarmWindow && mCurrentHomeTopModel.status != Constants.SENSOR_STATUS_ALARM) {
                        getView().showAlarmInfoView();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    needShowAlarmWindow = false;
                }
            }
        }, 300);
    }

    public void requestWithDirection(int direction, boolean needShowProgress, final HomeTopModel homeTopModel) {
        if (!PreferencesHelper.getInstance().getUserData().hasDeviceBrief) {
            return;
        }
        try {
            if (needShowProgress) {
                getView().showProgressDialog();
            }
            switch (direction) {
                case Constants.DIRECTION_DOWN:
                    page = 1;
                    RetrofitServiceHelper.getInstance().getDeviceBriefInfoList(mSelectedCondition==null?null:mSelectedCondition.order, mSelectedCondition==null?null:mSelectedCondition.sort,page, null, mTypeSelectedType, homeTopModel.status, null).subscribeOn(Schedulers
                            .io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<DeviceInfo>>>(this) {
                        @Override
                        public void onCompleted(ResponseResult<List<DeviceInfo>> deviceInfoListRsp) {
                            mDeviceInfoList.clear();
                            List<DeviceInfo> data = deviceInfoListRsp.getData();
                            if (data != null && data.size() > 0) {
                                mDeviceInfoList.addAll(data);
                            }

                            getView().refreshContentData(false, false,mDeviceInfoList);
                            getView().recycleViewRefreshComplete();
                            getView().dismissProgressDialog();
                        }

                        @Override
                        public void onErrorMsg(int errorCode, String errorMsg) {

                            if (errorCode == ERR_CODE_NET_CONNECT_EX) {//网络连接异常需要清空数据集合，显示空数据页面
                                mDeviceInfoList.clear();
                            }

                            try {
                                LogUtils.loge("onErrorMsg", "mDeviceInfoList.size=" + mDeviceInfoList.size());

                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }

                            getView().setDetectionPoints(WidgetUtil.handlerNumber(String.valueOf(totalMonitorPoint)));
                            getView().refreshContentData(true, false, mDeviceInfoList);

                            getView().toastShort(errorMsg);
                            getView().recycleViewRefreshComplete();
                            getView().dismissProgressDialog();

                        }
                    });
                    break;
                case Constants.DIRECTION_UP:
                    page++;
                    RetrofitServiceHelper.getInstance().getDeviceBriefInfoList(mSelectedCondition==null?null:mSelectedCondition.order, mSelectedCondition==null?null:mSelectedCondition.sort,page, null, mTypeSelectedType, homeTopModel.status, null).subscribeOn(Schedulers
                            .io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<DeviceInfo>>>(this) {
                        @Override
                        public void onCompleted(ResponseResult<List<DeviceInfo>> deviceInfoListRsp) {
                            try {
                                List<DeviceInfo> data = deviceInfoListRsp.getData();
                                if (data == null || data.size() == 0) {
                                    page--;
                                    getView().toastShort(mContext.getString(R.string.no_more_data));
                                }else{
                                    mDeviceInfoList.addAll(data);
                                }
                                try {
                                    LogUtils.loge("mDeviceInfoList.size", mDeviceInfoList.size() + "");
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                                getView().refreshContentData(false, false, mDeviceInfoList);
                                getView().recycleViewRefreshComplete();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            getView().dismissProgressDialog();
                        }

                        @Override
                        public void onErrorMsg(int errorCode, String errorMsg) {
                            getView().toastShort(errorMsg);
                            getView().recycleViewRefreshComplete();
                            getView().dismissProgressDialog();
                            page--;
                        }
                    });
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理push来的json数据
     */
    private final boolean[] homeTopModelCacheFresh = {false, false, false, false, false};

    private DeviceInfo organizeJsonData(DeviceInfo newDeviceInfo) {
        int status = newDeviceInfo.getStatus();
        String sn = newDeviceInfo.getSn();
        try {
            LogUtils.loge("mDeviceInfoListsize", "mDeviceInfoList.size=" + mDeviceInfoList.size());
            LogUtils.loge("mDeviceInfoListsize", "newDeviceInfo.sn=" + newDeviceInfo.getSn());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        if (mCurrentHomeTopModel != null && mCurrentHomeTopModel.status == status) {
            synchronized (mDeviceInfoList) {
                Iterator<DeviceInfo> iterator = mDeviceInfoList.iterator();
                while (iterator.hasNext()) {
                    DeviceInfo currentDeviceInfo = iterator.next();
                    if (currentDeviceInfo.getSn().equalsIgnoreCase(sn)) {
                        currentDeviceInfo.cloneSocketData(newDeviceInfo);
                        return currentDeviceInfo;
                    }
                }

                try {
                    LogUtils.loge("mDeviceInfoListsize", "mDeviceInfoList.size=" + mDeviceInfoList.size());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }
        return null;

    }

    public void requestDataByStatus(HomeTopModel homeTopModel) {
        try {
            updateHeaderTop(homeTopModel);

            int index = mHomeTopModels.indexOf(homeTopModel);
            if (mHomeTopModels.size() <= 1) {
                getView().setImvHeaderLeftVisible(false);
                getView().setImvHeaderRightVisible(false);
            } else {
                if (index == 0) {
                    getView().setImvHeaderLeftVisible(false);
                } else {
                    getView().setImvHeaderLeftVisible(true);
                }
                if (index == mHomeTopModels.size() - 1) {
                    getView().setImvHeaderRightVisible(false);
                } else {
                    getView().setImvHeaderRightVisible(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void requestDataByTypes(int position, final HomeTopModel homeTopModel) {
        try {
            if (position == 0) {
                mTypeSelectedType = null;
            } else {
                mTypeSelectedType = mMergeTypes.get(position - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mTypeSelectedType = null;
        }
        page = 1;
        getView().showProgressDialog();
        getDeviceInfoListByStateRspObservable(homeTopModel.status, false).retryWhen(new RetryWithDelay(2, 100)).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<DeviceInfo>>>(this) {
            @Override
            public void onCompleted(ResponseResult<List<DeviceInfo>> deviceInfoListRsp) {

                getView().refreshContentData(false, false, mDeviceInfoList);
                updateHeaderTop(homeTopModel);
                getView().dismissProgressDialog();
                getView().dismissAlarmInfoView();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().toastShort(errorMsg);
                getView().dismissProgressDialog();
                getView().dismissAlarmInfoView();
            }
        });
    }



    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    public void doScanLogin() {
        if (PreferencesHelper.getInstance().getUserData() != null) {
            if (PreferencesHelper.getInstance().getUserData().hasScanLogin) {
                Intent intent = new Intent(mContext, ScanActivity.class);
                intent.putExtra(Constants.EXTRA_SCAN_ORIGIN_TYPE, Constants.TYPE_SCAN_LOGIN);
                getView().startAC(intent);
                return;
            }
        }
        getView().toastShort(mContext.getString(R.string.no_such_permission));
    }

    public void clickAlarmInfo(int position, HomeTopModel homeTopModel) {
        try {
            DeviceInfo deviceInfo = mDeviceInfoList.get(position);
            requestAlarmInfo(deviceInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestAlarmInfo(DeviceInfo deviceInfo) {
        //
        getView().showProgressDialog();
        RetrofitServiceHelper.getInstance().getDeviceAlarmLogList(1, deviceInfo.getSn(), null, null, null, null, null, null)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<DeviceAlarmLogInfo>>>(this) {

            @Override
            public void onCompleted(ResponseResult<List<DeviceAlarmLogInfo>> deviceAlarmLogRsp) {
//                getView().dismissProgressDialog();
                List<DeviceAlarmLogInfo> data = deviceAlarmLogRsp.getData();
                if (data == null || data.size() == 0) {
                    getView().toastShort(mContext.getString(R.string.no_alert_log_information_was_obtained));
                    getView().dismissProgressDialog();
                } else {
                    DeviceAlarmLogInfo deviceAlarmLogInfo = data.get(0);
                    enterAlarmLogPop(deviceAlarmLogInfo);
                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().toastShort(errorMsg);
                getView().dismissProgressDialog();
            }
        });
    }

    private void enterAlarmLogPop(final DeviceAlarmLogInfo deviceAlarmLogInfo) {
        final AlarmLogPopUtils mAlarmLogPop = new AlarmLogPopUtils(mContext, this);
        mAlarmLogPop.refreshData(deviceAlarmLogInfo);
        //
        if (PreferencesHelper.getInstance().getAlarmPopupDataBeanCache() == null) {
            RetrofitServiceHelper.getInstance().getDevicesAlarmPopupConfig().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<AlarmPopupDataBean>>(this) {
                @Override
                public void onCompleted(ResponseResult<AlarmPopupDataBean> devicesAlarmPopupConfigRsp) {
                    PreferencesHelper.getInstance().saveAlarmPopupDataBeanCache(devicesAlarmPopupConfigRsp.getData());
                    final AlarmPopupModel alarmPopupModel = new AlarmPopupModel();
                    String deviceName = deviceAlarmLogInfo.getDeviceName();
                    if (TextUtils.isEmpty(deviceName)) {
                        alarmPopupModel.title = deviceAlarmLogInfo.getDeviceSN();
                    } else {
                        alarmPopupModel.title = deviceName;
                    }
                    alarmPopupModel.alarmStatus = deviceAlarmLogInfo.getAlarmStatus();
                    alarmPopupModel.updateTime = deviceAlarmLogInfo.getUpdatedTime();
                    alarmPopupModel.mergeType = WidgetUtil.handleMergeType(deviceAlarmLogInfo.getDeviceType());
                    alarmPopupModel.sensorType = deviceAlarmLogInfo.getSensorType();
                    //
                    AlarmPopupConfigAnalyzer.handleAlarmPopupModel(null, alarmPopupModel);
                    mAlarmLogPop.show(alarmPopupModel);
                    getView().dismissProgressDialog();

                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    getView().toastShort(errorMsg);
                    getView().dismissProgressDialog();
                }
            });
        } else {
            final AlarmPopupModel alarmPopupModel = new AlarmPopupModel();
            String deviceName = deviceAlarmLogInfo.getDeviceName();
            if (TextUtils.isEmpty(deviceName)) {
                alarmPopupModel.title = deviceAlarmLogInfo.getDeviceSN();
            } else {
                alarmPopupModel.title = deviceName;
            }
            alarmPopupModel.alarmStatus = deviceAlarmLogInfo.getAlarmStatus();
            alarmPopupModel.updateTime = deviceAlarmLogInfo.getUpdatedTime();
            alarmPopupModel.mergeType = WidgetUtil.handleMergeType(deviceAlarmLogInfo.getDeviceType());
            alarmPopupModel.sensorType = deviceAlarmLogInfo.getSensorType();
            //
            AlarmPopupConfigAnalyzer.handleAlarmPopupModel(null, alarmPopupModel);
            mAlarmLogPop.show(alarmPopupModel);
        }

    }

    public void doScanDeploy() {
        Intent intent = new Intent(mContext, ScanActivity.class);
        intent.putExtra(Constants.EXTRA_SCAN_ORIGIN_TYPE, Constants.TYPE_SCAN_DEPLOY_DEVICE);
        getView().startAC(intent);
    }

    public void doSearch() {
        Intent intent = new Intent(mContext, SearchMonitorActivity.class);
        getView().startAC(intent);
    }

    public void doContract() {
        if (PreferencesHelper.getInstance().getUserData() != null) {
            if (PreferencesHelper.getInstance().getUserData().hasContractCreate) {
                Intent intent = new Intent(mContext, ContractEditorActivity.class);
                intent.putExtra(Constants.EXTRA_CONTRACT_ORIGIN_TYPE, 1);
                getView().startAC(intent);
                return;
            }
        }
        getView().toastShort(mContext.getString(R.string.no_such_permission));
    }

    public void updateSelectDeviceTypePopAndShow() {
        mMergeTypes.clear();
        DeviceMergeTypesInfo localDevicesMergeTypes = PreferencesHelper.getInstance().getLocalDevicesMergeTypes();
        if (localDevicesMergeTypes != null) {
            final DeviceMergeTypesInfo.DeviceMergeTypeConfig config = localDevicesMergeTypes.getConfig();
            if (config != null) {
                Map<String, MergeTypeStyles> mergeType = config.getMergeType();
                if (mergeType != null) {
                    Set<Map.Entry<String, MergeTypeStyles>> entries = mergeType.entrySet();
                    for (Map.Entry<String, MergeTypeStyles> entry : entries) {
                        String key = entry.getKey();
                        MergeTypeStyles mergeTypeStyles = entry.getValue();
                        if (mergeTypeStyles.isOwn()) {
                            mMergeTypes.add(key);
                        }
                    }
                    Collections.sort(mMergeTypes);
                }
            }
        }
        getView().updateSelectDeviceTypePopAndShow(mMergeTypes);
    }


    public void initSortCondition() {
        String[] tilteArray = mContext.getResources().getStringArray(R.array.sortcondition);
        mSortConditionList.add(mSelectedCondition = new SortConditionModel("init", "status", tilteArray[0], true));
        mSortConditionList.add(new SortConditionModel("asc", "name", tilteArray[1], false));
        mSortConditionList.add(new SortConditionModel("desc", "name", tilteArray[2], false));

    }

    public void updateSelectSortConditionPopAndShow() {
        if (mSortConditionList.size() == 0) {
            initSortCondition();
        }
        getView().updateSelectFilterConditionPopAndShow(mSortConditionList, mSelectedCondition);
    }

    @Override
    public void onDialogShow() {
        getView().dismissProgressDialog();
    }

    //changqi 添加
    public HomeTopModel getCurrentHomeModel() {
        return mCurrentHomeTopModel;
    }

    private String getCurrentDataStr() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            switch (mCurrentHomeTopModel.status) {
                case 0:
                    stringBuilder.append(mContext.getString(R.string.main_page_warn));
                    break;
                case 1:
                    stringBuilder.append(mContext.getString(R.string.normal));
                    break;
                case 2:
                    stringBuilder.append(mContext.getString(R.string.status_lost));
                    break;
                case 3:
                    stringBuilder.append(mContext.getString(R.string.status_inactive));
                    break;
                case 4:
                    stringBuilder.append(mContext.getString(R.string.status_malfunction));
                    break;
            }
            return stringBuilder.append("(").append(mCurrentHomeTopModel.value).append(")").toString();
        } catch (Exception e) {
            return "";
        }
    }

    private int getCurrentColor() {
        try {
            switch (mCurrentHomeTopModel.status) {
                case 0:
                    return R.color.c_f34a4a;
                case 1:
                    return R.color.c_1dbb99;
                case 2:
                    return R.color.c_5d5d5d;
                case 3:
                    return R.color.c_b6b6b6;
                case 4:
                    return R.color.c_fdc83b;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.color.c_1dbb99;
    }
}
