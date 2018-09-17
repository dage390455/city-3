package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.smartcity.activity.ContractManagerActivity;
import com.sensoro.smartcity.activity.LoginActivityTest;
import com.sensoro.smartcity.activity.MerchantSwitchActivity;
import com.sensoro.smartcity.activity.ScanActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IManagerFragmentView;
import com.sensoro.smartcity.iwidget.IOnFragmentStart;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ManagerFragmentPresenter extends BasePresenter<IManagerFragmentView> implements IOnFragmentStart {
    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
    }

    @Override
    public void onDestroy() {

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
                        Intent intent = new Intent(mContext, LoginActivityTest.class);
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
        if (PreferencesHelper.getInstance().getUserData() != null) {
            if (TextUtils.isEmpty(PreferencesHelper.getInstance().getUserData().userName)) {
                getView().setMerchantName("SENSORO");
            } else {
                getView().setMerchantName(PreferencesHelper.getInstance().getUserData().userName);
            }

            //                getView().updateMenuPager(MenuPageFactory.createMenuPageList(mEventLoginData));
            if (PreferencesHelper.getInstance().getUserData().isSupperAccount) {

//                    merchantSwitchFragment.requestDataByDirection(DIRECTION_DOWN, true);
            } else {
//                    indexFragment.reFreshDataByDirection(DIRECTION_DOWN);
            }
//                merchantSwitchFragment.refreshData(mEventLoginData.userName, (mEventLoginData.phone == null ? "" : mEventLoginData.phone), mEventLoginData.phoneId);
//                getView().setMenuSelected(0);
            //TODO 版本信息
            UpgradeInfo upgradeInfo = Beta.getUpgradeInfo();
//        upgradeInfo.
        }

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
        getView().toastShort("无此权限");

    }

    public void doChangeMerchants() {
        if (PreferencesHelper.getInstance().getUserData() != null) {
            if (PreferencesHelper.getInstance().getUserData().hasSubMerchant) {
                Intent intent = new Intent(mContext, MerchantSwitchActivity.class);
                intent.putExtra("login_data", PreferencesHelper.getInstance().getUserData());
                getView().startAC(intent);
                return;
            }
        }
        getView().toastShort("无此权限");

    }

    public void doScanDeploy() {
        if (PreferencesHelper.getInstance().getUserData() != null) {
            if (!PreferencesHelper.getInstance().getUserData().isSupperAccount) {
                Intent intent = new Intent(mContext, ScanActivity.class);
                intent.putExtra("type", Constants.TYPE_SCAN_DEPLOY_DEVICE);
                getView().startAC(intent);
                return;
            }
        }
        getView().toastShort("无此权限");

    }

    public void doScanLogin() {
        if (PreferencesHelper.getInstance().getUserData() != null) {
            if (PreferencesHelper.getInstance().getUserData().hasScanLogin) {
                Intent intent = new Intent(mContext, ScanActivity.class);
                intent.putExtra("type", Constants.TYPE_SCAN_LOGIN);
                getView().startAC(intent);
                return;
            }
        }
        getView().toastShort("无此权限");

    }

    public void doPollingMission() {
        getView().toastShort("此功能暂未开放");
    }

    public void doMaintenanceMission() {
        getView().toastShort("此功能暂未开放");
    }

    public void doAboutUs() {
        getView().toastShort("关于我们");
    }

    public void doVersionInfo() {
        Beta.checkUpgrade();
    }
}
