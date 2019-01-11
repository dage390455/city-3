package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.sensoro.bottomnavigation.BadgeItem;
import com.sensoro.bottomnavigation.BottomNavigationBar;
import com.sensoro.bottomnavigation.BottomNavigationItem;
import com.sensoro.bottomnavigation.TextBadgeItem;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.MainFragmentPageAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IMainView;
import com.sensoro.smartcity.presenter.MainPresenter;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.widget.HomeViewPager;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity<IMainView, MainPresenter> implements IMainView
        , BottomNavigationBar.OnTabSelectedListener {

    @BindView(R.id.ac_main_hvp_content)
    HomeViewPager acMainHvpContent;
    @BindView(R.id.ac_main_bottom_navigation_bar)
    BottomNavigationBar acMainBottomBar;

    private MainFragmentPageAdapter mPageAdapter;
    private BottomNavigationItem warnItem;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mPresenter.onKeyDown(keyCode, event);
    }

    private void initView() {
        initViewPager();

        initBottomBar();
    }

    private void initBottomBar() {
        BottomNavigationItem homeItem = new BottomNavigationItem(R.drawable.selector_ac_main_home, mActivity.getString(R.string.main_page_home));
        warnItem = new BottomNavigationItem(R.drawable.selector_ac_main_warning, mActivity.getString(R.string.main_page_warn));
        warnItem.setBadgeItem(new TextBadgeItem());
        BottomNavigationItem malfunctionItem = new BottomNavigationItem(R.drawable.selector_ac_main_malfunction, mActivity.getString(R.string.main_page_malfunction));
        BottomNavigationItem managerItem = new BottomNavigationItem(R.drawable.selector_ac_main_manage, mActivity.getString(R.string.main_page_manage));
        acMainBottomBar.setTabSelectedListener(this);
        acMainBottomBar
                .addItem(homeItem)
                .addItem(warnItem)
                .addItem(malfunctionItem)
                .addItem(managerItem)
                .setFirstSelectedPosition(0)
                .initialise();
        warnItem.getBadgeItem().hide(false);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter();
    }


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

    }

    @Override
    public void dismissProgressDialog() {

    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }


    @Override
    public void setHpCurrentItem(int position) {
        acMainHvpContent.setCurrentItem(position);

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
}
