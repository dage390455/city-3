package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.sensoro.bottomnavigation.BottomNavigationBar;
import com.sensoro.bottomnavigation.BottomNavigationItem;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.MainFragmentPageAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IMainView;
import com.sensoro.smartcity.presenter.MainPresenter;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.widget.HomeViewPager;
import com.sensoro.smartcity.widget.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity<IMainView, MainPresenter> implements IMainView
        , RadioGroup.OnCheckedChangeListener {


    @BindView(R.id.ac_main_hvp_content)
    HomeViewPager acMainHvpContent;
    @BindView(R.id.ac_main_rb_main)
    RadioButton acMainRbMain;
    @BindView(R.id.ac_main_rb_warning)
    RadioButton acMainRbWarning;
    @BindView(R.id.ac_main_rb_manage)
    RadioButton acMainRbManage;
    @BindView(R.id.ac_main_rl_guide)
    RadioGroup acMainRlGuide;
    @BindView(R.id.ac_main_tv_warning_count)
    TextView acMainTvWarningCount;
    @BindView(R.id.ac_main_bottom_navigation_bar)
    BottomNavigationBar acMainBottomBar;

    private MainFragmentPageAdapter mPageAdapter;
    private PopupWindow mPopupWindow;
    private int mode;

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
        acMainRlGuide.setOnCheckedChangeListener(this);

        initBottomBar();
    }

    private void initBottomBar() {
        acMainBottomBar
                .addItem(new BottomNavigationItem(R.drawable.home_normal, "首页"))
                .addItem(new BottomNavigationItem(R.drawable.warning_norm, "预警"))
                .addItem(new BottomNavigationItem(R.drawable.manage_normal, "管理"))
                .setFirstSelectedPosition(0)
                .initialise();
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
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.ac_main_rb_main:
                if (acMainRbMain.isChecked()) {
                    setHpCurrentItem(0);
                }

                break;
            case R.id.ac_main_rb_warning:
                if (acMainRbWarning.isChecked()) {
                    setHpCurrentItem(1);
                }
                break;
            case R.id.ac_main_rb_manage:
                if (acMainRbManage.isChecked()) {
                    setHpCurrentItem(2);
                }
                break;

        }
    }


    @Override
    public void setHpCurrentItem(int position) {
        acMainHvpContent.setCurrentItem(position);

    }

    @Override
    public void setRbChecked(int id) {
        acMainRlGuide.check(id);
    }

    @Override
    public void updateMainPageAdapterData(List<Fragment> fragments) {
        mPageAdapter.setFragmentList(fragments);
        mPageAdapter.notifyDataSetChanged();
    }

    @Override
    public void setHasDeviceBriefControl(boolean hasDeviceBriefControl) {
        acMainRbMain.setVisibility(hasDeviceBriefControl ? View.VISIBLE : View.GONE);

    }

    @Override
    public void setHasAlarmInfoControl(boolean hasDeviceAlarmInfoControl) {
        acMainRbWarning.setVisibility(hasDeviceAlarmInfoControl ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setHasManagerControl(boolean hasManagerControl) {
        acMainRbManage.setVisibility(hasManagerControl ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setAlarmWarnCount(int count) {
        try {
            if (PreferencesHelper.getInstance().getUserData().hasAlarmInfo) {
                if (count > 0) {
                    acMainTvWarningCount.setVisibility(View.VISIBLE);
                    if (count > 99) {
                        count = 99;
                    }
                    acMainTvWarningCount.setText(String.valueOf(count));
                } else {
                    acMainTvWarningCount.setVisibility(View.GONE);
                }
            } else {
                acMainTvWarningCount.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean isHomeFragmentChecked() {
        return acMainRbMain.isChecked();
    }


    private void initViewPager() {
        mPageAdapter = new MainFragmentPageAdapter(mActivity.getSupportFragmentManager());
        acMainHvpContent.setAdapter(mPageAdapter);
        acMainHvpContent.setOffscreenPageLimit(5);
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
}
