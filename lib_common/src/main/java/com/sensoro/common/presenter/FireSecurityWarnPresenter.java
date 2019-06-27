package com.sensoro.common.presenter;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.launcher.ARouter;
import com.sensoro.common.BuildConfig;
import com.sensoro.common.R;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.imainview.IFireSecurityWarnView;

import java.util.ArrayList;

/**
 * @author : bin.tian
 * date   : 2019-06-21
 */
public class FireSecurityWarnPresenter extends BasePresenter<IFireSecurityWarnView> {

    private Activity mActivity;
    private final ArrayList<Fragment> mFragmentList = new ArrayList<>(2);
    private final ArrayList<String> mFragmentTitleList = new ArrayList<>(2);
    //控制model
    private final boolean isModel = BuildConfig.IS_MODULE;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        initViewPager(context);
    }

    private void initViewPager(Context context) {
        mFragmentList.clear();
        //
        if (isModel) {
            Object fragmentCameraList = ARouter.getInstance().build(ARouterConstants.FRAGMENT_CAMERA_LIST).navigation(mActivity);
            if (fragmentCameraList instanceof Fragment) {
                mFragmentList.add((Fragment) fragmentCameraList);
                mFragmentTitleList.add(context.getString(R.string.fire_warn_title));
            }
            Object cameraWarn = ARouter.getInstance().build(ARouterConstants.FRAGMENT_CAMERA_WARN_LIST).navigation(mActivity);

            if (cameraWarn instanceof Fragment) {
                Fragment fragment = (Fragment) cameraWarn;
                mFragmentList.add(fragment);
                mFragmentTitleList.add(context.getString(com.sensoro.common.R.string.security_warn_title));
            }
        } else {
            Object fireWarnFragment = ARouter.getInstance().build(ARouterConstants.FRAGMENT_FIRE_WARN_FRAGMENT).navigation(mActivity);
            if (fireWarnFragment instanceof Fragment) {
                mFragmentList.add((Fragment) fireWarnFragment);
                mFragmentTitleList.add(context.getString(R.string.fire_warn_title));
            }
            Object cameraWarn = ARouter.getInstance().build(ARouterConstants.FRAGMENT_CAMERA_WARN_LIST).navigation(mActivity);

            if (cameraWarn instanceof Fragment) {
                Fragment fragment = (Fragment) cameraWarn;
                mFragmentList.add(fragment);
                mFragmentTitleList.add(context.getString(com.sensoro.common.R.string.security_warn_title));
            }
        }
        boolean hasFireSecurityList = PreferencesHelper.getInstance().getUserData().hasFireSecurityList;
        //TODO 控制显示安防预警权限后续添加，这里直接赋值测试
        hasFireSecurityList = true;
        //通过权限控制
        getView().setHasFireSecurityView(hasFireSecurityList);
        getView().updateFireSecurityPageAdapterData(mFragmentTitleList, mFragmentList);
    }

    @Override
    public void onDestroy() {

    }
}
