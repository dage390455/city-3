package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;

import com.igexin.sdk.PushManager;
import com.lzy.imagepicker.ImagePicker;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.LoginActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.factory.MenuPageFactory;
import com.sensoro.smartcity.fragment.AlarmListFragment;
import com.sensoro.smartcity.fragment.IndexFragment;
import com.sensoro.smartcity.fragment.MerchantSwitchFragment;
import com.sensoro.smartcity.fragment.PointDeployFragment;
import com.sensoro.smartcity.fragment.StationDeployFragment;
import com.sensoro.smartcity.imainviews.IMainView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.iwidget.IOndestroy;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.model.MenuPageInfo;
import com.sensoro.smartcity.push.SensoroPushIntentService;
import com.sensoro.smartcity.push.SensoroPushService;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.Character;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.response.DeviceAlarmLogRsp;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.server.response.UpdateRsp;
import com.sensoro.smartcity.util.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainPresenter extends BasePresenter<IMainView> implements IOndestroy, Constants, IOnCreate {
    private Activity mActivity;

    private String mUserName = null;
    private String mPhone = null;
    private String mPhoneId = null;
    private Character mCharacter = null;
    private volatile boolean mIsSupperAccount;
    private String roles;


    private final List<Fragment> fragmentList = new ArrayList<>();
    //
    private IndexFragment indexFragment = null;
    private AlarmListFragment alarmListFragment = null;
    private MerchantSwitchFragment merchantSwitchFragment = null;
    private PointDeployFragment pointDeployFragment = null;
    private StationDeployFragment stationDeployFragment = null;
    //
    private volatile Socket mSocket = null;
    private final DeviceInfoListener mInfoListener = new DeviceInfoListener();

    private final Handler mHandler = new Handler();
    private final TaskRunnable mRunnable = new TaskRunnable();
    //
    private boolean hasStation = false;

    /**
     * 超级用户
     *
     * @return
     */
    public boolean isSupperAccount() {
        return mIsSupperAccount;
    }

    public String getRoles() {
        return roles;
    }

    public List<Fragment> getFragmentList() {
        return fragmentList;
    }

    public void checkPush() {
        boolean pushTurnedOn = PushManager.getInstance().isPushTurnedOn(SensoroCityApplication.getInstance());
        LogUtils.logd(this, "checkPush: " + pushTurnedOn);
        if (!pushTurnedOn) {
            PushManager.getInstance().initialize(SensoroCityApplication.getInstance(), SensoroPushService.class);
            // 注册 intentService 后 PushDemoReceiver 无效, sdk 会使用 DemoIntentService 传递数据,
            // AndroidManifest 对应保留一个即可(如果注册 DemoIntentService, 可以去掉 PushDemoReceiver, 如果注册了
            // IntentService, 必须在 AndroidManifest 中声明)
            PushManager.getInstance().registerPushIntentService(SensoroCityApplication.getInstance(),
                    SensoroPushIntentService
                            .class);
        }
    }


    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        //
        mUserName = mActivity.getIntent().getStringExtra(EXTRA_USER_NAME);
        mPhone = mActivity.getIntent().getStringExtra(EXTRA_PHONE);
        mPhoneId = mActivity.getIntent().getStringExtra(EXTRA_PHONE_ID);
        mCharacter = (Character) mActivity.getIntent().getSerializableExtra(EXTRA_CHARACTER);
        roles = mActivity.getIntent().getStringExtra(EXTRA_USER_ROLES);
        mIsSupperAccount = mActivity.getIntent().getBooleanExtra(EXTRA_IS_SPECIFIC, false);
        hasStation = mActivity.getIntent().getBooleanExtra(EXTRA_GRANTS_INFO, false);
        //
        indexFragment = IndexFragment.newInstance(mCharacter);
        alarmListFragment = AlarmListFragment.newInstance("alarm");
        merchantSwitchFragment = MerchantSwitchFragment.newInstance("merchant");
        pointDeployFragment = PointDeployFragment.newInstance("point");
        stationDeployFragment = StationDeployFragment.newInstance("station");
        //
        fragmentList.add(indexFragment);
        fragmentList.add(alarmListFragment);
        fragmentList.add(merchantSwitchFragment);
        fragmentList.add(pointDeployFragment);
        fragmentList.add(stationDeployFragment);
        getView().updateMainPageAdapterData();
        getView().showAccountInfo(mUserName, mPhone);
        mHandler.postDelayed(mRunnable, 3000L);
    }

    public void changeAccount(String userName, String phone, String roles, boolean isSpecific, boolean hasStation) {
        this.mUserName = userName;
        this.mPhone = phone;
        this.mIsSupperAccount = isSpecific;
        this.roles = roles;
        this.hasStation = hasStation;
        getView().showAccountInfo(mUserName, mPhone);
        if (indexFragment != null) {
            if (mIsSupperAccount) {
                merchantSwitchFragment.requestData();
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        indexFragment.reFreshDataByDirection(DIRECTION_DOWN);
                        indexFragment.requestTopData(true);
                    }
                });
            }
            //
            getView().updateMenuPager(MenuPageFactory.createMenuPageList(mIsSupperAccount, roles, hasStation));
            getView().setCurrentPagerItem(0);
            getView().setMenuSelected(0);
            reconnect();
        }

    }

    public void setAppVersion() {
        PackageManager manager = mActivity.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(mActivity.getPackageName(), 0);
            String appVersionName = info.versionName; // 版本名
            int currentVersionCode = info.versionCode; // 版本号
            getView().setAPPVersionCode("City " + appVersionName);
            System.out.println(currentVersionCode + " " + appVersionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 简单判断账户类型
     */
    public void freshAccountType() {
        if (mIsSupperAccount) {
            getView().setCurrentPagerItem(2);
        } else {
            getView().setCurrentPagerItem(0);
        }
        //TODO 考虑到声明周期问题 暂时延缓后续优化

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getView().updateMenuPager(MenuPageFactory.createMenuPageList(mIsSupperAccount, roles, hasStation));
                if (mIsSupperAccount) {
                    merchantSwitchFragment.requestData();
                }
                merchantSwitchFragment.refreshData(mUserName, (mPhone == null ? "" : mPhone), mPhoneId);
                getView().setMenuSelected(0);
            }
        }, 50);
    }

    private void requestUpdate() {
        RetrofitServiceHelper.INSTANCE.getUpdateInfo().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
                .mainThread()).subscribe(new CityObserver<UpdateRsp>() {


            @Override
            public void onCompleted() {

            }

            @Override
            public void onNext(UpdateRsp updateRsp) {
                LogUtils.logd(this, "获取app升级ok========" + updateRsp.toString());
                try {
                    PackageInfo info = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0);
                    if (info.versionCode < updateRsp.getVersion()) {
                        String changelog = updateRsp.getChangelog();
                        String install_url = updateRsp.getInstall_url();
                        getView().showUpdateAppDialog(changelog, install_url);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                LogUtils.loge(this, "app升级" + errorMsg);
            }
        });
    }


    private void createSocket() {
        try {
            String sessionId = RetrofitServiceHelper.INSTANCE.getSessionId();
            IO.Options options = new IO.Options();
            options.query = "session=" + sessionId;
            options.forceNew = true;
            mSocket = IO.socket(RetrofitServiceHelper.INSTANCE.BASE_URL, options);
            mSocket.on(SOCKET_EVENT_DEVICE_INFO, mInfoListener);
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    private void reconnect() {
        try {
            if (mSocket != null) {
                mSocket.disconnect();
                mSocket.off(SOCKET_EVENT_DEVICE_INFO, mInfoListener);
                mSocket = null;
            }
            String sessionId = RetrofitServiceHelper.INSTANCE.getSessionId();
            IO.Options options = new IO.Options();
            options.query = "session=" + sessionId;
            options.forceNew = true;
            mSocket = IO.socket(RetrofitServiceHelper.INSTANCE.BASE_URL, options);
            mSocket.on(SOCKET_EVENT_DEVICE_INFO, mInfoListener);
            mSocket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    public void logout() {
        String phoneId = mActivity.getIntent().getStringExtra(EXTRA_PHONE_ID);
        String uid = mActivity.getIntent().getStringExtra(EXTRA_USER_ID);
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.logout(phoneId, uid).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
                .mainThread()).subscribe(new CityObserver<ResponseBase>() {
            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }

            @Override
            public void onCompleted() {
                getView().dismissProgressDialog();
            }


            @Override
            public void onNext(ResponseBase responseBase) {
                if (responseBase.getErrcode() == ResponseBase.CODE_SUCCESS) {
                    Intent intent = new Intent(mActivity, LoginActivity.class);
                    getView().startAC(intent);
                    getView().finishAc();
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off(SOCKET_EVENT_DEVICE_INFO, mInfoListener);
            mSocket = null;
        }
        mHandler.removeCallbacks(mRunnable);
        mHandler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
    }

    public void updateApp(String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        getView().startAC(intent);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData data) {
        //TODO 可以修改以此种方式传递，方便管理
        LogUtils.loge(this, data.toString());
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    private class TaskRunnable implements Runnable {

        @Override
        public void run() {
            requestUpdate();
            if (!mIsSupperAccount) {
                createSocket();
            }
        }
    }

    private final class DeviceInfoListener implements Emitter.Listener {

        @Override
        public void call(Object... args) {
            try {
                synchronized (DeviceInfoListener.class) {
                    for (Object arg : args) {
                        if (arg instanceof JSONObject) {
//                            JSONObject jsonObject = (JSONObject) arg;
//                            String s = jsonObject.toString();
//                            LogUtils.loge(this, "jsonObject = " + s);
                        } else {
                            if (arg instanceof JSONArray) {
                                JSONArray jsonArray = (JSONArray) arg;
                                final JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String s = jsonObject.toString();
                                LogUtils.loge(this, "jsonArray = " + s);
                                if (!mIsSupperAccount) {
                                    indexFragment.handleSocketInfo(jsonObject.toString());
                                }
                            }

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CODE_MAP) {
            if (data.getSerializableExtra(EXTRA_DEVICE_INFO) != null) {
                DeviceInfo deviceInfo = (DeviceInfo) data.getSerializableExtra(EXTRA_DEVICE_INFO);
                refreshDeviceInfo(deviceInfo);
            }
            getView().setCurrentPagerItem(0);
            getView().setMenuSelected(0);
        } else if (resultCode == RESULT_CODE_SEARCH_DEVICE) {
            DeviceInfoListRsp infoRspData = (DeviceInfoListRsp) data.getSerializableExtra(EXTRA_SENSOR_INFO);
            if (infoRspData != null) {
                indexFragment.refreshBySearch(infoRspData);
            } else {
                DeviceInfo deviceInfo = (DeviceInfo) data.getSerializableExtra(EXTRA_DEVICE_INFO);
                refreshDeviceInfo(deviceInfo);
            }
            getView().setCurrentPagerItem(0);
            getView().setMenuSelected(0);
        } else if (resultCode == RESULT_CODE_SEARCH_MERCHANT) {
            String nickname = data.getStringExtra("nickname");
            String phone = data.getStringExtra("phone");
            String roles = data.getStringExtra("roles");
            boolean isSpecific = data.getBooleanExtra("isSpecific", false);
            hasStation = data.getBooleanExtra(EXTRA_GRANTS_INFO, false);
            changeAccount(nickname, phone, roles, isSpecific, hasStation);
        } else if (resultCode == RESULT_CODE_SEARCH_ALARM) {
            String type = data.getStringExtra(EXTRA_SENSOR_TYPE);
            int searchIndex = data.getIntExtra(EXTRA_ALARM_SEARCH_INDEX, -1);
            String searchText = data.getStringExtra(EXTRA_ALARM_SEARCH_TEXT);
            boolean isFromCancel = data.getBooleanExtra(EXTRA_ACTIVITY_CANCEL, false);
            if (isFromCancel) {
                alarmListFragment.requestDataByDirection(DIRECTION_DOWN, true);
            } else {
                if (searchIndex == 0) {
                    DeviceAlarmLogRsp alarmListRsp = (DeviceAlarmLogRsp) data.getSerializableExtra(EXTRA_ALARM_INFO);
                    alarmListFragment.refreshUIBySearch(DIRECTION_DOWN, alarmListRsp, searchText);
                } else {
                    alarmListFragment.refreshUIByType(type);
                }
            }
            getView().setCurrentPagerItem(1);
            getView().setMenuSelected(1);

        } else if (resultCode == RESULT_CODE_ALARM) {
            alarmListFragment.requestDataByDirection(DIRECTION_DOWN, true);
            getView().setCurrentPagerItem(1);
            getView().setMenuSelected(1);
        } else if (resultCode == RESULT_CODE_ALARM_DETAIL) {
            DeviceAlarmLogInfo deviceAlarmLogInfo = (DeviceAlarmLogInfo) data.getSerializableExtra(EXTRA_ALARM_INFO);
            alarmListFragment.freshDeviceAlarmLogInfo(deviceAlarmLogInfo);
            getView().setCurrentPagerItem(1);
            getView().setMenuSelected(1);
        } else if (resultCode == RESULT_CODE_CALENDAR) {
            String startDate = data.getStringExtra(EXTRA_ALARM_START_DATE);
            String endDate = data.getStringExtra(EXTRA_ALARM_END_DATE);
            alarmListFragment.requestDataByDate(startDate, endDate);
            getView().setCurrentPagerItem(1);
            getView().setMenuSelected(1);
        } else if (resultCode == RESULT_CODE_DEPLOY) {
            boolean containsData = data.getBooleanExtra(EXTRA_CONTAINS_DATA, false);
            if (containsData) {
                DeviceInfo deviceInfo = (DeviceInfo) data.getSerializableExtra(EXTRA_DEVICE_INFO);
                refreshDeviceInfo(deviceInfo);
            }
            //
            boolean is_station = data.getBooleanExtra(EXTRA_IS_STATION_DEPLOY, false);
            if (is_station) {
                getView().setCurrentPagerItem(4);
            } else {
                getView().setCurrentPagerItem(3);
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_ITEMS || resultCode == ImagePicker.RESULT_CODE_BACK) {
            if (alarmListFragment != null) {
                alarmListFragment.handlerActivityResult(requestCode, resultCode, data);
            }
        }

    }

    /**
     * 通过menupage判断类型
     *
     * @param menuPageId
     */
    public void clickMenuItem(int menuPageId) {
        switch (menuPageId) {
            case MenuPageInfo.MENU_PAGE_INDEX:
                indexFragment.reFreshDataByDirection(DIRECTION_DOWN);
                getView().setCurrentPagerItem(0);
                break;
            case MenuPageInfo.MENU_PAGE_ALARM:
                alarmListFragment.requestDataByDirection(DIRECTION_DOWN, true);
                getView().setCurrentPagerItem(1);
                break;
            case MenuPageInfo.MENU_PAGE_MERCHANT:
                merchantSwitchFragment.requestData();
                merchantSwitchFragment.refreshData(mUserName, mPhone, mPhoneId);
                getView().setCurrentPagerItem(2);
                break;
            case MenuPageInfo.MENU_PAGE_POINT:
                getView().setCurrentPagerItem(3);
                break;
            case MenuPageInfo.MENU_PAGE_STATION:
                getView().setCurrentPagerItem(4);
                break;
            case MenuPageInfo.MENU_PAGE_CONTRACT:
                //TODO 合同管理
                break;
            default:
                break;
        }
    }

    private void refreshDeviceInfo(DeviceInfo deviceInfo) {
        for (int i = 0; i < SensoroCityApplication.getInstance().getData().size(); i++) {
            DeviceInfo tempDeviceInfo = SensoroCityApplication.getInstance().getData().get(i);
            if (deviceInfo.getSn().equals(tempDeviceInfo.getSn())) {
                SensoroCityApplication.getInstance().getData().set(i, deviceInfo);
                break;
            }
        }
    }
}
