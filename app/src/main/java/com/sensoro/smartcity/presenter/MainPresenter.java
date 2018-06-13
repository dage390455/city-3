package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.igexin.sdk.PushManager;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.LoginActivity;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.fragment.AlarmListFragment;
import com.sensoro.smartcity.fragment.IndexFragment;
import com.sensoro.smartcity.fragment.MerchantSwitchFragment;
import com.sensoro.smartcity.fragment.PointDeployFragment;
import com.sensoro.smartcity.imainviews.IMainView;
import com.sensoro.smartcity.iwidget.IOndestroy;
import com.sensoro.smartcity.push.SensoroPushIntentService;
import com.sensoro.smartcity.push.SensoroPushService;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.Character;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.response.CityObserver;
import com.sensoro.smartcity.server.response.DeviceAlarmLogRsp;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.server.response.UpdateRsp;
import com.sensoro.smartcity.server.response.UserAccountRsp;
import com.sensoro.smartcity.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainPresenter extends BasePresenter<IMainView> implements IOndestroy, Constants {
    private Activity mActivity;

    private final List<String> dataSupper = new ArrayList<>();
    private final List<String> dataNormal = new ArrayList<>();
    private final List<String> dataBussise = new ArrayList<>();
    private String mUserName = null;
    private String mPhone = null;
    private String mPhoneId = null;
    private Character mCharacter = null;
    private String mIsSupperAccountStr;
    private String roles;

    /**
     * 超级用户
     *
     * @return
     */
    public boolean isSupperAccount() {
        return !TextUtils.isEmpty(mIsSupperAccountStr) && "true".equalsIgnoreCase(mIsSupperAccountStr);
    }

    public String getRoles() {
        return roles;
    }

    public List<Fragment> getFragmentList() {
        return fragmentList;
    }

    private final List<Fragment> fragmentList = new ArrayList<>();
    //
    private IndexFragment indexFragment = null;
    private AlarmListFragment alarmListFragment = null;
    private MerchantSwitchFragment merchantSwitchFragment = null;
    private PointDeployFragment pointDeployFragment = null;
    //
    private volatile Socket mSocket = null;
    private final DeviceInfoListener mInfoListener = new DeviceInfoListener();

    private final Handler mHandler = new Handler();
    private final TaskRunnable mRunnable = new TaskRunnable();
    //
//    private int current_iteam = 0;
    public static final int SUPPER_ACCOUNT = 1;
    public static final int NORMOL_ACCOUNT = 2;
    public static final int BUSSISE_ACCOUNT = 3;
    private int accountType = NORMOL_ACCOUNT;

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
        String[] titleArray = mActivity.getResources().getStringArray(R.array.drawer_title_array);
        dataNormal.addAll(Arrays.asList(titleArray));
        String[] titleArray_no = mActivity.getResources().getStringArray(R.array.drawer_title_array_nobussise);
        dataBussise.addAll(Arrays.asList(titleArray_no));
        String[] titleArray_supper = mActivity.getResources().getStringArray(R.array.drawer_title_array_supper);
        dataSupper.addAll(Arrays.asList(titleArray_supper));
        mUserName = mActivity.getIntent().getStringExtra(EXTRA_USER_NAME);
        mPhone = mActivity.getIntent().getStringExtra(EXTRA_PHONE);
        mPhoneId = mActivity.getIntent().getStringExtra(EXTRA_PHONE_ID);
        mCharacter = (Character) mActivity.getIntent().getSerializableExtra(EXTRA_CHARACTER);
        roles = mActivity.getIntent().getStringExtra(EXTRA_USER_ROLES);
        mIsSupperAccountStr = mActivity.getIntent().getStringExtra(EXTRA_IS_SPECIFIC);
        //
        indexFragment = IndexFragment.newInstance(mCharacter);
        alarmListFragment = AlarmListFragment.newInstance("");
        merchantSwitchFragment = MerchantSwitchFragment.newInstance("");
        pointDeployFragment = PointDeployFragment.newInstance("");
        fragmentList.add(indexFragment);
        fragmentList.add(alarmListFragment);
        fragmentList.add(merchantSwitchFragment);
        fragmentList.add(pointDeployFragment);
        getView().updateMainPageAdapterData();
        getView().showAccountInfo(mUserName, mPhone);
        mHandler.postDelayed(mRunnable, 3000L);
    }

    public void changeAccount(String userName, String phone, String roles, String isSpecific) {
        this.mUserName = userName;
        this.mPhone = phone;
        this.mIsSupperAccountStr = isSpecific;
        this.roles = roles;
        getView().showAccountInfo(mUserName, mPhone);
        if (indexFragment != null) {
            getView().setCurrentItem(0);
            indexFragment.reFreshDataByDirection(Constants.DIRECTION_DOWN);
            if ("true".equalsIgnoreCase(isSpecific)) {
                accountType = SUPPER_ACCOUNT;
                getView().setMenuInfoAdapterData(dataSupper);
            } else {
                if (this.roles.equalsIgnoreCase("business")) {
                    accountType = BUSSISE_ACCOUNT;
                    getView().setMenuInfoAdapterData(dataBussise);
                } else {
                    accountType = NORMOL_ACCOUNT;
                    getView().setMenuInfoAdapterData(dataNormal);
                }
            }
            getView().freshAccountSwitch(accountType);
            reconnect();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    indexFragment.requestTopData(false);
                }
            });

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
        if (isSupperAccount()) {
            accountType = SUPPER_ACCOUNT;
            getView().setCurrentItem(2);
            getView().setMenuInfoAdapterData(dataSupper);
            pointDeployFragment.hiddenRootView();
        }
        //TODO 考虑到声明周期问题 暂时延缓后续优化
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isSupperAccount()) {
                    merchantSwitchFragment.requestData();
                } else if (roles.equalsIgnoreCase("business")) {
                    accountType = BUSSISE_ACCOUNT;
                    getView().setMenuInfoAdapterData(dataBussise);
                } else {
                    accountType = NORMOL_ACCOUNT;
                    getView().setMenuInfoAdapterData(dataNormal);
                }

                merchantSwitchFragment.refreshData(mUserName, (mPhone == null ? "" : mPhone), mPhoneId);
                getView().freshAccountSwitch(accountType);
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
            public void onErrorMsg(String errorMsg) {
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
            public void onErrorMsg(String errorMsg) {
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


    private boolean isMainActivityTop() {
        ActivityManager manager = (ActivityManager) mActivity.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(MainActivity.class.getName());
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
    }

    public void updateApp(String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        getView().startAC(intent);
    }

    private class TaskRunnable implements Runnable {

        @Override
        public void run() {
            requestUpdate();
            createSocket();
        }
    }

    private class DeviceInfoListener implements Emitter.Listener {

        @Override
        public void call(Object... args) {
            try {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof JSONObject) {
//                        JSONObject jsonObject = (JSONObject) args[i];
                    } else {
                        JSONArray jsonArray = (JSONArray) args[i];
                        final JSONObject jsonObject = jsonArray.getJSONObject(0);
                        if (isMainActivityTop() && !isSupperAccount()) {
                            indexFragment.handleSocketInfo(jsonObject.toString());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CODE_ORIGIN) {
            getView().setCurrentItem(0);
        } else if (resultCode == RESULT_CODE_MAP) {
            if (data.getSerializableExtra(EXTRA_DEVICE_INFO) != null) {
                DeviceInfo deviceInfo = (DeviceInfo) data.getSerializableExtra(EXTRA_DEVICE_INFO);
                refreshDeviceInfo(deviceInfo);
            }
            getView().setCurrentItem(0);
        } else if (resultCode == RESULT_CODE_SEARCH_DEVICE) {
            DeviceInfoListRsp infoRspData = (DeviceInfoListRsp) data.getSerializableExtra(EXTRA_SENSOR_INFO);
            if (infoRspData != null) {
                indexFragment.refreshBySearch(infoRspData);
            } else {
                DeviceInfo deviceInfo = (DeviceInfo) data.getSerializableExtra(EXTRA_DEVICE_INFO);
                refreshDeviceInfo(deviceInfo);
            }
            getView().setCurrentItem(0);
        } else if (resultCode == RESULT_CODE_SEARCH_MERCHANT) {
            UserAccountRsp infoRspData = (UserAccountRsp) data.getSerializableExtra(EXTRA_MERCHANT_INFO);
            if (infoRspData != null) {
                merchantSwitchFragment.refresh(infoRspData);
            }
            getView().setCurrentItem(2);
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
            getView().setCurrentItem(1);
        } else if (resultCode == RESULT_CODE_ALARM) {
            alarmListFragment.requestDataByDirection(DIRECTION_DOWN, true);
            getView().setCurrentItem(1);
        } else if (resultCode == RESULT_CODE_ALARM_DETAIL) {
            DeviceAlarmLogInfo deviceAlarmLogInfo = (DeviceAlarmLogInfo) data.getSerializableExtra(EXTRA_ALARM_INFO);
            alarmListFragment.onPopupCallback(deviceAlarmLogInfo);
            getView().setCurrentItem(1);
        } else if (resultCode == RESULT_CODE_CALENDAR) {
            String startDate = data.getStringExtra(EXTRA_ALARM_START_DATE);
            String endDate = data.getStringExtra(EXTRA_ALARM_END_DATE);
            alarmListFragment.requestDataByDate(startDate, endDate);
            getView().setCurrentItem(1);
        } else if (resultCode == RESULT_CODE_DEPLOY) {
            pointDeployFragment.showRootView();
            boolean containsData = data.getBooleanExtra(EXTRA_CONTAINS_DATA, false);
            if (containsData) {
                DeviceInfo deviceInfo = (DeviceInfo) data.getSerializableExtra(EXTRA_DEVICE_INFO);
                refreshDeviceInfo(deviceInfo);
            }
            getView().setCurrentItem(3);
        }
        getView().freshAccountSwitch(accountType);
    }

    public void switchAccountByType(int position) {
        //账户切换
        switch (accountType) {
            case SUPPER_ACCOUNT:
                pointDeployFragment.hiddenRootView();
                merchantSwitchFragment.requestData();
                merchantSwitchFragment.refreshData(mUserName, mPhone, mPhoneId);
                break;
            case NORMOL_ACCOUNT:
                switch (position) {
                    case 0:
                        pointDeployFragment.hiddenRootView();
                        indexFragment.reFreshDataByDirection(DIRECTION_DOWN);
                        break;
                    case 1:
                        pointDeployFragment.hiddenRootView();
                        alarmListFragment.requestDataByDirection(DIRECTION_DOWN, true);
                        break;
                    case 2:
                        pointDeployFragment.hiddenRootView();
                        merchantSwitchFragment.requestData();
                        merchantSwitchFragment.refreshData(mUserName, mPhone, mPhoneId);
                        break;
                    case 3:
                        pointDeployFragment.showRootView();
                        break;
                    default:
                        pointDeployFragment.hiddenRootView();
                        break;
                }
                break;
            case BUSSISE_ACCOUNT:
                switch (position) {
                    case 0:
                        pointDeployFragment.hiddenRootView();
                        indexFragment.reFreshDataByDirection(DIRECTION_DOWN);
                        break;
                    case 1:
                        pointDeployFragment.hiddenRootView();
                        alarmListFragment.requestDataByDirection(DIRECTION_DOWN, true);
                        break;
                    case 2:
                        pointDeployFragment.showRootView();
                        break;
                    default:
                        pointDeployFragment.hiddenRootView();
                        break;
                }
                break;
        }
        getView().changeAccount(accountType, position);
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
