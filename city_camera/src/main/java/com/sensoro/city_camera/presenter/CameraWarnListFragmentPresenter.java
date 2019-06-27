package com.sensoro.city_camera.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.sensoro.city_camera.IMainViews.ICameraWarnListFragmentView;
import com.sensoro.city_camera.R;
import com.sensoro.city_camera.activity.SecurityWarnDetailActivity;
import com.sensoro.common.analyzer.PreferencesSaveAnalyzer;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.constant.SearchHistoryTypeConstants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.manger.ThreadPoolManager;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.security.bean.SecurityAlarmInfo;
import com.sensoro.common.server.bean.EventCameraWarnStatusModel;
import com.sensoro.common.server.security.response.SecurityAlarmListRsp;
import com.sensoro.common.widgets.popup.CalendarPopUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author wangqinghao
 */
public class CameraWarnListFragmentPresenter extends BasePresenter<ICameraWarnListFragmentView> implements IOnCreate, Runnable {
    private final List<SecurityAlarmInfo> mSecurityAlarmInfoList = new ArrayList<>();
    private final List<String> mSearchHistoryList = new ArrayList<>();
    private volatile int cur_page = 1;
    private long startTime;
    private long endTime;
    private Activity mContext;
    private boolean isReConfirm = false;
    private SecurityAlarmInfo mCurrentSecurityAlarmInfo;
    private CalendarPopUtils mCalendarPopUtils;
    private String tempSearchText;
    private volatile boolean needFresh = false;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final Comparator<SecurityAlarmInfo> cameraWarnInfoComparator = new Comparator<SecurityAlarmInfo>() {
        @Override
        public int compare(SecurityAlarmInfo o1, SecurityAlarmInfo o2) {
            long l = o2.getAlarmTime() - o1.getAlarmTime();
            if (l > 0) {
                return 1;
            } else if (l < 0) {
                return -1;
            } else {
                return 0;
            }

        }
    };

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        //if (PreferencesHelper.getInstance().getUserData().hasAlarmInfo) {
        if (true) {
            //requestSearchData(Constants.DIRECTION_DOWN, null);
            //demo
            requestSearchData(Constants.DIRECTION_UP, null);
            //demo
            //mHandler.post(this);
        }
        //安防历史搜索记录
        List<String> list = PreferencesHelper.getInstance().getSearchHistoryData(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_WARN);
        if (list != null) {
            mSearchHistoryList.addAll(list);
            getView().updateSearchHistoryList(mSearchHistoryList);
        }

    }

    private void freshUI(final int direction, SecurityAlarmListRsp securityAlarmListRsp) {
        final List<SecurityAlarmInfo> securityAlarmInfoList = securityAlarmListRsp.getData().list;
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if (direction == Constants.DIRECTION_DOWN) {
                    mSecurityAlarmInfoList.clear();
                }
                synchronized (mSecurityAlarmInfoList) {
                    out:
                    for (int i = 0; i < securityAlarmInfoList.size(); i++) {
                        SecurityAlarmInfo securityAlarmInfo = securityAlarmInfoList.get(i);
                        for (int j = 0; j < mSecurityAlarmInfoList.size(); j++) {
                            if (mSecurityAlarmInfoList.get(j).getId().equals(securityAlarmInfo.getId())) {
                                mSecurityAlarmInfoList.set(i, securityAlarmInfo);
                                break out;
                            }
                        }
                        mSecurityAlarmInfoList.add(securityAlarmInfo);
                    }
                    Collections.sort(mSecurityAlarmInfoList, cameraWarnInfoComparator);
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isAttachedView()) {
                                getView().updateCameraWarnsListAdapter(mSecurityAlarmInfoList);
                            }

                        }
                    });
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
        mSecurityAlarmInfoList.clear();

    }

    /**
     * 搜索数据
     *
     * @param direction
     * @param searchText
     */
    public void requestSearchData(final int direction, String searchText) {
        //搜索缓存的数据
        //if (!PreferencesHelper.getInstance().getUserData().hasAlarmInfo) {
        if (false) {
            return;
        }
        if (TextUtils.isEmpty(searchText)) {
            tempSearchText = null;
        } else {
            tempSearchText = searchText;
        }
        switch (direction) {
            case Constants.DIRECTION_DOWN:
                cur_page = 1;
                getView().showProgressDialog();
//                //demo  begin
//                addTestData(false);
//                getView().dismissProgressDialog();
//                SecurityAlarmListRsp securityAlarmListRsp = new SecurityAlarmListRsp();
//                securityAlarmListRsp.setList(mSecurityAlarmInfoList);
//                //getView().updateCameraWarnsListAdapter(mSecurityAlarmInfoList);
//                freshUI(direction, securityAlarmListRsp); //demo
//                getView().onPullRefreshComplete();
//                //demo finishAc

                RetrofitServiceHelper.getInstance().getSecurityAlarmList(cur_page, null, null, 2, null, 1
                ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<SecurityAlarmListRsp>(this) {


                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().onPullRefreshComplete();
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }

                    @Override
                    public void onCompleted(SecurityAlarmListRsp securityAlarmListRsp) {
                        getView().dismissProgressDialog();
                        freshUI(direction, securityAlarmListRsp);
                        getView().onPullRefreshComplete();
                    }
                });

                break;
            case Constants.DIRECTION_UP:
                cur_page++;
                getView().showProgressDialog();

//                //demo  begin
//                addTestData(false);
//                getView().dismissProgressDialog();
//                SecurityAlarmListRsp securityAlarmListRsp1 = new SecurityAlarmListRsp();
//                securityAlarmListRsp1.setList(mSecurityAlarmInfoList);
//                freshUI(direction, securityAlarmListRsp1);
//                getView().onPullRefreshComplete();
//                //demo finish

                RetrofitServiceHelper.getInstance().getSecurityAlarmList(cur_page, null, null, 0, null, 0
                ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<SecurityAlarmListRsp>(this) {


                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        cur_page--;
                        getView().onPullRefreshComplete();
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }

                    @Override
                    public void onCompleted(SecurityAlarmListRsp securityAlarmListRsp) {
                        getView().dismissProgressDialog();
                        if (securityAlarmListRsp.getData().list.size() == 0) {
                            getView().toastShort(mContext.getString(R.string.no_more_data));
                            getView().onPullRefreshCompleteNoMoreData();
                            cur_page--;
                        } else {
                            freshUI(direction, securityAlarmListRsp);
                            getView().onPullRefreshComplete();
                        }
                    }
                });
                break;
            default:
                break;
        }

    }

    public void clickItem(SecurityAlarmInfo securityAlarmInfo, boolean isReConfirm) {
        Intent intent = new Intent(mContext, SecurityWarnDetailActivity.class);
        intent.putExtra("id", securityAlarmInfo.getId());
        getView().startAC(intent);

    }

    /**
     * 单个安防预警确认
     *
     * @param securityAlarmInfo
     */
    public void cameraWarnConfirm(final SecurityAlarmInfo securityAlarmInfo) {
        getView().toastLong(securityAlarmInfo.getTaskName());
    }

    /**
     * 刷新单条数据
     */
    public void refreshSigleCameraWarnInfo() {

    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(EventCameraWarnStatusModel eventAlarmStatusModel) {
        if (TextUtils.isEmpty(tempSearchText) && !getView().getSearchTextCancelVisible()) {
            switch (eventAlarmStatusModel.status) {

            }
        }


    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {

        }

    }

    public void doCancelSearch() {
        tempSearchText = null;
        requestSearchData(Constants.DIRECTION_DOWN, null);
    }

    /**
     * 首次刷新列表
     */
    @Override
    public void run() {
        scheduleRefresh();
        mHandler.postDelayed(this, 3000);
    }

    private void scheduleRefresh() {
        if (needFresh) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isAttachedView()) {
                        getView().updateCameraWarnsListAdapter(mSecurityAlarmInfoList);
                    }
                    needFresh = false;
                }
            });
        }
    }

    /**
     * 保存历史搜索记录
     *
     * @param text
     */
    public void save(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        List<String> warnList = PreferencesSaveAnalyzer.handleDeployRecord(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_WARN, text);
        mSearchHistoryList.clear();
        mSearchHistoryList.addAll(warnList);
        getView().updateSearchHistoryList(mSearchHistoryList);

    }

    /**
     * 清除历史搜索记录
     */
    public void clearSearchHistory() {
        PreferencesSaveAnalyzer.clearAllData(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_WARN);
        mSearchHistoryList.clear();
        getView().updateSearchHistoryList(mSearchHistoryList);
    }

    /**
     * Item Button 点击事件处理
     */
    public void clickItemByConfirmStatus(final SecurityAlarmInfo securityAlarmInfo, boolean isReConfirm) {
        this.isReConfirm = isReConfirm;
        mCurrentSecurityAlarmInfo = securityAlarmInfo;
        getView().toastLong("item Button 点击");


    }

    private void freshSingleWarnLogInfo(SecurityAlarmInfo securityAlarmInfo) {
        synchronized (securityAlarmInfo) {
            // 处理只针对当前集合做处理
            boolean canRefresh = false;
            for (int i = 0; i < mSecurityAlarmInfoList.size(); i++) {
                SecurityAlarmInfo tempLogInfo = mSecurityAlarmInfoList.get(i);
                if (tempLogInfo.getId().equals(securityAlarmInfo.getId())) {
                    //刷新单个信息
                    /*AlarmInfo.RecordInfo[] recordInfoArray = deviceAlarmLogInfo.getRecords();
                    deviceAlarmLogInfo.setSort(1);
                    for (AlarmInfo.RecordInfo recordInfo : recordInfoArray) {
                        if (recordInfo.getType().equals("recovery")) {
                            deviceAlarmLogInfo.setSort(4);
                            break;
                        }
                    }
                    mDeviceAlarmLogInfoList.set(i, deviceAlarmLogInfo);*/
                    canRefresh = true;
                    break;
                }
            }
            if (canRefresh) {
                Collections.sort(mSecurityAlarmInfoList, cameraWarnInfoComparator);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isAttachedView()) {
                            getView().updateCameraWarnsListAdapter(mSecurityAlarmInfoList);
                        }

                    }
                });
            }

        }
    }
    /*private void addTestData(boolean isClearData){
        if(isClearData){
            mSecurityAlarmInfoList.clear();
        }
        int[] warnValid = {0,1};
        int[] warnType = {1,2,3};
        String[] warnAddress = {"地址1111111111","地址2222222222","地址3333333333"};
        String[] warnName = {"外来人员布控人物","重点人员布控人物","人员入侵布控任务"};
        String capturePhotoUrl = "http://pic13.nipic.com/20110409/7119492_114440620000_2.jpg";
        String focusOriPhoto = "http://pic41.nipic.com/20140508/18609517_112216473140_2.jpg";
        double[] focusMatchrates = {91,50};
        int dataSize = 6;
        for (int i=0;i<dataSize;i++){
            SecurityAlarmInfo info = new SecurityAlarmInfo();
            //int roundInt = (int)Math.round(0.5);
            int roundInt = i;
            info.setId("000"+i) ;
            info.setIsEffective(warnValid[roundInt%warnValid.length]);
            info.setTaskType(warnType[roundInt%warnType.length]);
            info.setFaceUrl(capturePhotoUrl);
            info.setImageUrl(focusOriPhoto);
            info.setScore(focusMatchrates[roundInt%focusMatchrates.length]);
            info.setTaskName(warnName[roundInt%warnName.length]+""+roundInt);
            info.setAddress(warnAddress[roundInt%warnAddress.length]+""+roundInt);
            info.setAlarmTime( System.currentTimeMillis()-roundInt*100);
            mSecurityAlarmInfoList.add(info);
        }
    }*/


}
