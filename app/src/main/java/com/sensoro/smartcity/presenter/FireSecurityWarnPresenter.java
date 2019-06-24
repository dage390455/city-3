package com.sensoro.smartcity.presenter;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.launcher.ARouter;
import com.sensoro.city_camera.fragment.CameraListFragment;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.smartcity.fragment.FireWarnFragment;
import com.sensoro.smartcity.imainviews.IFireSecurityWarnView;

import java.util.ArrayList;

/**
 * @author : bin.tian
 * date   : 2019-06-21
 */
public class FireSecurityWarnPresenter extends BasePresenter<IFireSecurityWarnView> {

    private FireWarnFragment mFireWarnFragment;
    private CameraListFragment mCameraListFragment;

    private final ArrayList<Fragment> mFragmentList = new ArrayList<>(2);
    private final ArrayList<String> mFragmentTitleList = new ArrayList<>(2);

    @Override
    public void initData(Context context) {
        initViewPager(context);
    }

    private void initViewPager(Context context){
        mFireWarnFragment = new FireWarnFragment();
        mCameraListFragment = (CameraListFragment) ARouter.getInstance().build(ARouterConstants.FRAGMENT_CAMERA_LIST).navigation();

        mFragmentList.clear();
        mFragmentList.add(mFireWarnFragment);
        mFragmentList.add(mCameraListFragment);

        mFragmentTitleList.add(context.getString(com.sensoro.common.R.string.fire_warn_title));
        mFragmentTitleList.add(context.getString(com.sensoro.common.R.string.security_warn_title));

        getView().updateFireSecurityPageAdapterData(mFragmentTitleList, mFragmentList);
    }

    @Override
    public void onDestroy() {

    }
}
