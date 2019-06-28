package com.sensoro.city_camera.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.gyf.immersionbar.ImmersionBar;
import com.sensoro.bottomnavigation.BadgeItem;
import com.sensoro.bottomnavigation.BottomNavigationBar;
import com.sensoro.bottomnavigation.BottomNavigationItem;
import com.sensoro.bottomnavigation.TextBadgeItem;
import com.sensoro.city_camera.IMainViews.ILauncherActivityView;
import com.sensoro.city_camera.R;
import com.sensoro.city_camera.R2;
import com.sensoro.city_camera.presenter.LauncherActivityPresenter;
import com.sensoro.common.adapter.MainFragmentPageAdapter;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.widgets.HomeViewPager;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LauncherActivity extends BaseActivity<ILauncherActivityView, LauncherActivityPresenter> implements ILauncherActivityView
        , BottomNavigationBar.OnTabSelectedListener {

    @BindView(R2.id.ac_main_hvp_content)
    HomeViewPager acMainHvpContent;
    @BindView(R2.id.ac_main_bottom_navigation_bar)
    BottomNavigationBar acMainBottomBar;
    private MainFragmentPageAdapter mPageAdapter;
    private BottomNavigationItem warnItem;
    public ProgressUtils mProgressUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_launcher);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
    }
    //    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        return mPresenter.onKeyDown(keyCode, event);
//    }

    @Override
    public void onBackPressed() {
        mPresenter.exit();
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        initViewPager();

        initBottomBar();
    }

    private void initBottomBar() {
        warnItem = new BottomNavigationItem(R.drawable.selector_ac_main_warning, mActivity.getString(R.string.main_page_warn));
        warnItem.setBadgeItem(new TextBadgeItem());
        BottomNavigationItem cameraItem = new BottomNavigationItem(R.drawable.selector_ac_main_warning, getString(R.string.main_page_camera));
        acMainBottomBar.setTabSelectedListener(this);
        acMainBottomBar
                .addItem(warnItem)
                .addItem(cameraItem)
                .setFirstSelectedPosition(0)
                .initialise();
        warnItem.getBadgeItem().hide(false);

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        mPresenter.handleActivityResult(requestCode, resultCode, data);
//    }


    @Override
    public void startAC(Intent intent) {
        mActivity.startActivity(intent);
    }

    @Override
    public void finishAc() {
        mActivity.finish();
    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {

    }

    @Override
    public void setIntentResult(int resultCode) {

    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {

    }

    @Override
    public void showProgressDialog() {
        if (mProgressUtils != null) {
            mProgressUtils.showProgress();
        }
    }

    @Override
    public void dismissProgressDialog() {
        if (mProgressUtils != null) {
            mProgressUtils.dismissProgress();
        }
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }


    @Override
    public void setHpCurrentItem(int position) {
        acMainHvpContent.setCurrentItem(position, false);

    }


    @Override
    public void updateMainPageAdapterData(List<Fragment> fragments) {
        mPageAdapter.setFragmentList(fragments);
        mPageAdapter.notifyDataSetChanged();
    }

    @Override
    public void setHasDeviceBriefControl(boolean hasDeviceBriefControl) {
        acMainBottomBar.setBottomNavigationItemVisible(0, hasDeviceBriefControl);

    }

    @Override
    public void setHasAlarmInfoControl(boolean hasDeviceAlarmInfoControl) {
        acMainBottomBar.setBottomNavigationItemVisible(1, hasDeviceAlarmInfoControl);
    }

    @Override
    public void setHasMalfunctionControl(boolean hasManagerControl) {
        acMainBottomBar.setBottomNavigationItemVisible(2, hasManagerControl);
    }

    @Override
    public void setAlarmWarnCount(int count) {
        BadgeItem badgeItem = warnItem.getBadgeItem();
        if (badgeItem instanceof TextBadgeItem) {
            TextBadgeItem warnBadgeItem = (TextBadgeItem) badgeItem;
            try {
                if (PreferencesHelper.getInstance().getUserData().hasAlarmInfo) {
                    if (count > 0) {
                        if (warnBadgeItem.isHidden()) {
                            warnBadgeItem.show();
                        }
                        warnBadgeItem.setText(String.valueOf(count));
                    } else {
                        warnBadgeItem.hide();
                    }
                } else {
                    warnBadgeItem.hide();
                }
            } catch (Exception e) {
                e.printStackTrace();
                warnBadgeItem.hide();
            }
        }
    }


    @Override
    public void setBottomBarSelected(int position) {
        acMainBottomBar.selectTab(position);
    }

    @Override
    public boolean isHomeFragmentChecked() {
        return acMainHvpContent.getCurrentItem() == 0;
    }


    private void initViewPager() {
        mPageAdapter = new MainFragmentPageAdapter(mActivity.getSupportFragmentManager());
        acMainHvpContent.setAdapter(mPageAdapter);
        acMainHvpContent.setOffscreenPageLimit(6);

    }

    @Override
    public boolean isActivityOverrideStatusBar() {
        immersionBar = ImmersionBar.with(mActivity);
        immersionBar
                .transparentStatusBar()
                .statusBarDarkFont(true)
                .init();
        return true;
    }

    @Override
    public void onTabSelected(int position) {
        setHpCurrentItem(position);
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {
        //
    }

    @Override
    protected void onDestroy() {
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
        super.onDestroy();
    }

    @Override
    protected LauncherActivityPresenter createPresenter() {
        return new LauncherActivityPresenter();
    }
}
