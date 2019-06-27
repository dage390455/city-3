package com.sensoro.city_camera.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.launcher.ARouter;
import com.sensoro.city_camera.IMainViews.ILauncherActivityView;
import com.sensoro.city_camera.R;
import com.sensoro.city_camera.fragment.CameraListFragment;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.fragment.FireSecurityWarnFragment;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.response.DevicesAlarmPopupConfigRsp;
import com.sensoro.common.server.response.LoginRsp;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.utils.LogUtils;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LauncherActivityPresenter extends BasePresenter<ILauncherActivityView> {
    private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private Activity mContext;
    //
    private long exitTime = 0;
    private final Handler mHandler = new Handler();
    //
    private CameraListFragment mCameraListFragment;
    private FireSecurityWarnFragment mFireSecurityWarnFragment;


    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        //提前获取一次
        //保存一遍当前的版本信息
        PreferencesHelper.getInstance().saveCurrentVersionCode(AppUtils.getVersionCode(mContext));
//        if (true){
//            Intent intent = new Intent(mContext, RecyclerViewActivity.class);
//            getView().startAC(intent);
//            getView().finishAc();
//            return;
//        }
        //
        init();
        PreferencesHelper.getInstance().getSessionId();
        PreferencesHelper.getInstance().getSessionToken();
        PreferencesHelper.getInstance().saveMyBaseUrl("city-dev-api.sensoro.com/");
        RetrofitServiceHelper.getInstance().saveBaseUrlType(5);
        RetrofitServiceHelper.getInstance().getBaseUrlType();
        getView().showProgressDialog();
        RetrofitServiceHelper.getInstance().login("15110041945", "aa1111", "").subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<LoginRsp>(null) {
            @Override
            public void onCompleted(LoginRsp loginRsp) {
                String sessionID = loginRsp.getData().getSessionID();
                String token = loginRsp.getData().getToken();
                RetrofitServiceHelper.getInstance().saveSessionId(sessionID,token);
                //
//                UserInfo userInfo = loginRsp.getData();
//                EventLoginData loginData = UserPermissionFactory.createLoginData(userInfo, phoneId);
//                if (loginData.needAuth) {
//                    openNextActivity(loginData);
//                    return;
//                }
//                getMergeType(loginData);
                init();
                getView().dismissProgressDialog();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                try {
                    LogUtils.loge(errorMsg);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });

    }

    private void init() {
        initViewPager();
        //每次初始化静默拉取一次预警弹窗的配置项
        RetrofitServiceHelper.getInstance().getDevicesAlarmPopupConfig().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DevicesAlarmPopupConfigRsp>(this) {
            @Override
            public void onCompleted(DevicesAlarmPopupConfigRsp devicesAlarmPopupConfigRsp) {
                PreferencesHelper.getInstance().saveAlarmPopupDataBeanCache(devicesAlarmPopupConfigRsp.getData());

            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
            }
        });
    }

    private void initViewPager() {
        //
        mFireSecurityWarnFragment = new FireSecurityWarnFragment();
        mCameraListFragment = (CameraListFragment) ARouter.getInstance().build(ARouterConstants.FRAGMENT_CAMERA_LIST).navigation();
        if (mFragmentList.size() > 0) {
            mFragmentList.clear();
        }
        mFragmentList.add(mFireSecurityWarnFragment);
        mFragmentList.add(mCameraListFragment);
        getView().updateMainPageAdapterData(mFragmentList);
        //
    }



    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        mFragmentList.clear();
    }



    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            getView().toastShort(mContext.getResources().getString(R.string.exit_main));
            exitTime = System.currentTimeMillis();
        } else {
            getView().finishAc();
        }
    }

}
