package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.ContractManagerActivity;
import com.sensoro.smartcity.activity.DeployRecordActivity;
import com.sensoro.smartcity.activity.InspectionTaskListActivity;
import com.sensoro.smartcity.activity.LoginActivity;
import com.sensoro.smartcity.activity.MerchantSwitchActivity;
import com.sensoro.smartcity.activity.ScanActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IManagerFragmentView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.iwidget.IOnFragmentStart;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.model.EventLoginData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ManagerFragmentPresenter extends BasePresenter<IManagerFragmentView> implements IOnCreate, IOnFragmentStart, Constants {
    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
    }

    /**
     * 权限检查
     *
     * @param userData
     */
    private void checkPermission(EventLoginData userData) {
        if (userData != null) {
            boolean hasContract = userData.hasContract;
            boolean chineseLanguage = AppUtils.isChineseLanguage();
            getView().setContractVisible(hasContract && chineseLanguage);
            getView().setInspectionVisible(userData.hasInspectionTaskList && chineseLanguage);
            getView().setScanLoginVisible(userData.hasScanLogin);
            getView().setMerchantVisible(userData.hasSubMerchant);
            getView().changeMerchantTitle(userData.hasSubMerchant);
            getView().setSignalCheckVisible(userData.hasSignalCheck);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    public void doExitAccount() {
        if (PreferencesHelper.getInstance().getUserData() != null) {
            getView().showProgressDialog();
            RetrofitServiceHelper.INSTANCE.logout(PreferencesHelper.getInstance().getUserData().phoneId, PreferencesHelper.getInstance().getUserData().userId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
                    .mainThread()).subscribe(new CityObserver<ResponseBase>(this) {
                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    getView().dismissProgressDialog();
                    getView().toastShort(errorMsg);
                }

                @Override
                public void onCompleted(ResponseBase responseBase) {
                    if (responseBase.getErrcode() == ResponseBase.CODE_SUCCESS) {
                        RetrofitServiceHelper.INSTANCE.clearLoginDataSessionId();
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        getView().startAC(intent);
                    }
                    getView().dismissProgressDialog();
                    getView().finishAc();
                }
            });
        }
    }

    @Override
    public void onFragmentStart() {
        //TODO 控制账号权限
        EventLoginData userData = PreferencesHelper.getInstance().getUserData();
        if (userData != null) {
            if (TextUtils.isEmpty(userData.userName)) {
                getView().setMerchantName("SENSORO");
            } else {
                getView().setMerchantName(userData.userName);
            }
            checkPermission(userData);
        }
        getView().setAppUpdateVisible(hasNewVersion());

    }

    private boolean hasNewVersion() {
        //TODO 版本信息
        UpgradeInfo upgradeInfo = Beta.getUpgradeInfo();
        if (upgradeInfo == null) {
            return false;
        }
        int versionCode = upgradeInfo.versionCode;
        int currentVersionCode = AppUtils.getVersionCode(mContext);
        LogUtils.loge("versionCode = " + versionCode + ",currentVersionCode = " + currentVersionCode);
        return currentVersionCode != 0 && versionCode > currentVersionCode;
    }

    @Override
    public void onFragmentStop() {

    }

    public void doContract() {
        if (PreferencesHelper.getInstance().getUserData() != null) {
            if (PreferencesHelper.getInstance().getUserData().hasContract) {
                Intent intent = new Intent(mContext, ContractManagerActivity.class);
                getView().startAC(intent);
                return;
            }
        }
        getView().toastShort(mContext.getString(R.string.no_such_permission));

    }

    public void doChangeMerchants() {
        if (PreferencesHelper.getInstance().getUserData() != null) {
            if (PreferencesHelper.getInstance().getUserData().hasSubMerchant) {
                Intent intent = new Intent(mContext, MerchantSwitchActivity.class);
                intent.putExtra(EXTRA_EVENT_LOGIN_DATA, PreferencesHelper.getInstance().getUserData());
                getView().startAC(intent);
//                return;
            }
        }
//        getView().toastShort("无此权限");

    }

    public void doScanDeploy() {
        if (PreferencesHelper.getInstance().getUserData() != null) {
            if (!PreferencesHelper.getInstance().getUserData().isSupperAccount) {
                Intent intent = new Intent(mContext, DeployRecordActivity.class);
//                intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, Constants.TYPE_SCAN_DEPLOY_DEVICE);
                getView().startAC(intent);
                return;
            }
        }
        getView().toastShort(mContext.getString(R.string.no_such_permission));

    }

    public void doScanLogin() {
        if (PreferencesHelper.getInstance().getUserData() != null) {
            if (PreferencesHelper.getInstance().getUserData().hasScanLogin) {
                Intent intent = new Intent(mContext, ScanActivity.class);
                intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, Constants.TYPE_SCAN_LOGIN);
                getView().startAC(intent);
                return;
            }
        }
        getView().toastShort(mContext.getString(R.string.no_such_permission));

    }

    public void doInspection() {
        if (PreferencesHelper.getInstance().getUserData() != null) {
            if (PreferencesHelper.getInstance().getUserData().hasInspectionTaskList) {
                Intent intent = new Intent(mContext, InspectionTaskListActivity.class);
                getView().startAC(intent);
                return;
            }
        }
        getView().toastShort(mContext.getString(R.string.no_such_permission));
    }

    public void doMaintenanceMission() {
        getView().toastShort(mContext.getString(R.string.this_feature_is_not_yet_open));
    }

    public void doAboutUs() {
        if (AppUtils.isChineseLanguage()) {
            AppUtils.openNetPage(mContext, "https://www.sensoro.com/zh/about.html");
        } else {
            AppUtils.openNetPage(mContext, "https://www.sensoro.com/en/about.html");
        }

    }

    public void doVersionInfo() {
        if (hasNewVersion()) {
            Beta.checkUpgrade();
        } else {
            getView().showVersionDialog();
        }
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case EVENT_DATA_SEARCH_MERCHANT:
                if (data instanceof EventLoginData) {
                    checkPermission((EventLoginData) data);
                }
                break;
        }
    }

    public void doSignalCheck() {
        Intent intent = new Intent(mContext, ScanActivity.class);
        intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, TYPE_SCAN_SIGNAL_CHECK);
        getView().startAC(intent);

    }
}
