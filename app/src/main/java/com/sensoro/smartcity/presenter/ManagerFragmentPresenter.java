package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.iwidget.IOnFragmentStart;
import com.sensoro.common.model.EventData;
import com.sensoro.common.model.EventLoginData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.response.ResponseBase;
import com.sensoro.nameplate.activity.DeployNameplateActivity;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.ContractManagerActivity;
import com.sensoro.smartcity.activity.DeployRecordActivity;
import com.sensoro.smartcity.activity.InspectionTaskListActivity;
import com.sensoro.smartcity.activity.LoginActivity;
import com.sensoro.smartcity.activity.MerchantSwitchActivity;
import com.sensoro.smartcity.activity.ScanActivity;
import com.sensoro.smartcity.activity.WireMaterialDiameterCalculatorActivity;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IManagerFragmentView;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.LogUtils;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

//import com.sensoro.nameplate.activity.DeployNameplateActivity;

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
            getView().setMerchantVisible(userData.hasSubMerchant || userData.hasControllerAid);
            getView().changeMerchantTitle(userData.hasSubMerchant);
            getView().setSignalCheckVisible(userData.hasSignalCheck);
            getView().setDeviceCameraVisible(userData.hasDeviceCameraList && chineseLanguage);
            //TODO 是否显示基站管理
            getView().setStationManagerVisible(userData.hasStationList);
            //TODO 是否显示铭牌管理
            getView().setNameplateVisible(userData.hasNameplateList);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    public void doExitAccount() {
        if (PreferencesHelper.getInstance().getUserData() != null) {
            getView().showProgressDialog();
            RetrofitServiceHelper.getInstance().logout(PreferencesHelper.getInstance().getUserData().phoneId, PreferencesHelper.getInstance().getUserData().userId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
                    .mainThread()).subscribe(new CityObserver<ResponseBase>(this) {
                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    getView().dismissProgressDialog();
                    //不是网络位置错误直接退出
                    if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                        getView().toastShort(errorMsg);
                    } else {
                        RetrofitServiceHelper.getInstance().clearLoginDataSessionId();
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        getView().startAC(intent);
                        getView().finishAc();
                    }

                }

                @Override
                public void onCompleted(ResponseBase responseBase) {
                    if (responseBase.getErrcode() == ResponseBase.CODE_SUCCESS) {
                        RetrofitServiceHelper.getInstance().clearLoginDataSessionId();
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
        try {
            LogUtils.loge("versionCode = " + versionCode + ",currentVersionCode = " + currentVersionCode);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
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
            if (PreferencesHelper.getInstance().getUserData().hasSubMerchant || PreferencesHelper.getInstance().getUserData().hasControllerAid) {
                Intent intent = new Intent(mContext, MerchantSwitchActivity.class);
                intent.putExtra(EXTRA_EVENT_LOGIN_DATA, PreferencesHelper.getInstance().getUserData());
                getView().startAC(intent);
//                return;
            }
        }
//        getView().toastShort("无此权限");

    }

    public void doScanDeploy() {
//        if (PreferencesHelper.getInstance().getUserData() != null) {
//            if (!PreferencesHelper.getInstance().getUserData().isSupperAccount) {
        Intent intent = new Intent(mContext, DeployNameplateActivity.class);
//                intent.putExtra(EXTRA_SCAN_ORIGIN_TYPE, Constants.TYPE_SCAN_DEPLOY_DEVICE);
        getView().startAC(intent);

//        Intent intent = new Intent(mContext, DeployNameplateActivity.class);
//        getView().startAC(intent);

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

    public void doWireMaterial_diameter() {
        Intent intent = new Intent(mContext, WireMaterialDiameterCalculatorActivity.class);
        getView().startAC(intent);
    }

    public void doManageNameplate() {
        Bundle bundle = new Bundle();
        bundle.putInt("abc", 123);
        startActivity(ARouterConstants.activity_deploy_detail, null, mContext);
    }
}
