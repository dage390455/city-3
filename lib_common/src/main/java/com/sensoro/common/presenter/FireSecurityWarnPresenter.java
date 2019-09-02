package com.sensoro.common.presenter;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.launcher.ARouter;
import com.sensoro.common.BuildConfig;
import com.sensoro.common.R;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.imainview.IFireSecurityWarnView;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.model.EventData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * @author : bin.tian
 * date   : 2019-06-21
 */
public class FireSecurityWarnPresenter extends BasePresenter<IFireSecurityWarnView> implements IOnCreate {

    private Activity mActivity;
    private final ArrayList<Fragment> mFragmentList = new ArrayList<>(2);
    private final ArrayList<String> mFragmentTitleList = new ArrayList<>(2);
    //控制model
    private final boolean isModel = BuildConfig.IS_MODULE;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        onCreate();
        initViewPager(context);
    }

    private void initViewPager(Context context) {
        mFragmentList.clear();
        mFragmentTitleList.clear();
        //
        boolean hasMonitorTaskList = PreferencesHelper.getInstance().getUserData().hasMonitorTaskList;
        if (isModel) {
            boolean hasHasFireSecurity = false;
            Object fragmentCameraList = ARouter.getInstance().build(ARouterConstants.FRAGMENT_FIRE_WARN_FRAGMENT).navigation(mActivity);
            if (fragmentCameraList instanceof Fragment) {
                mFragmentList.add((Fragment) fragmentCameraList);
                mFragmentTitleList.add(context.getString(R.string.fire_warn_title));
            }
            if (hasMonitorTaskList) {
                Object cameraWarn = ARouter.getInstance().build(ARouterConstants.FRAGMENT_CAMERA_WARN_LIST).navigation(mActivity);
                if (cameraWarn instanceof Fragment) {
                    Fragment fragment = (Fragment) cameraWarn;
                    mFragmentList.add(fragment);
                    mFragmentTitleList.add(context.getString(com.sensoro.common.R.string.security_warn_title));
                    hasHasFireSecurity = true;
                }
            }
            getView().setHasFireSecurityView(hasHasFireSecurity);
        } else {
            Object fireWarnFragment = ARouter.getInstance().build(ARouterConstants.FRAGMENT_FIRE_WARN_FRAGMENT).navigation(mActivity);
            if (fireWarnFragment instanceof Fragment) {
                mFragmentList.add((Fragment) fireWarnFragment);
                mFragmentTitleList.add(context.getString(R.string.fire_warn_title));
            }
            if (hasMonitorTaskList) {
                Object cameraWarn = ARouter.getInstance().build(ARouterConstants.FRAGMENT_CAMERA_WARN_LIST).navigation(mActivity);
                if (cameraWarn instanceof Fragment) {
                    Fragment fragment = (Fragment) cameraWarn;
                    mFragmentList.add(fragment);
                    mFragmentTitleList.add(context.getString(com.sensoro.common.R.string.security_warn_title));
                }
            }
            getView().setHasFireSecurityView(hasMonitorTaskList);
        }
        // 控制显示安防预警列表查看权限
        getView().updateFireSecurityPageAdapterData(mFragmentTitleList, mFragmentList);
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        switch (code) {
            case Constants.EVENT_DATA_SEARCH_MERCHANT:
                if (isAttachedView()) {
                    initViewPager(mActivity);
                }
                break;
        }
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }
}
