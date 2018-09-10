package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.MainFragmentPageAdapter;
import com.sensoro.smartcity.adapter.TypeSelectAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.fragment.HomeFragment;
import com.sensoro.smartcity.fragment.ManagerFragment;
import com.sensoro.smartcity.fragment.WarnFragment;
import com.sensoro.smartcity.imainviews.IMainViewTest;
import com.sensoro.smartcity.presenter.MainPresenterTest;
import com.sensoro.smartcity.widget.HomeViewPager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivityTest extends BaseActivity<IMainViewTest, MainPresenterTest> implements IMainViewTest
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

    private ArrayList<Fragment> mFragmentList;
    private MainFragmentPageAdapter mPageAdapter;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        initViewPager();
        acMainRlGuide.setOnCheckedChangeListener(this);
        initPop();
    }

    private void initPop() {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_pop_type_select, null);
        RecyclerView mRcTypeSelect = view.findViewById(R.id.pop_type_select_rc);
        TypeSelectAdapter mTypeSelectAdapter = new TypeSelectAdapter(mActivity);
    }

    @Override
    protected MainPresenterTest createPresenter() {
        return new MainPresenterTest();
    }


    @Override
    public void startAC(Intent intent) {

    }

    @Override
    public void finishAc() {

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
    public void setMainHomeVpAdapter(MainFragmentPageAdapter mainFragmentPageAdapter) {
        acMainHvpContent.setAdapter(mainFragmentPageAdapter);
        acMainHvpContent.setOffscreenPageLimit(5);
    }


    @Override
    public void setHpCurrentItem(int position) {
        acMainHvpContent.setCurrentItem(position);

    }

    @Override
    public void setRbChecked(int id) {
        acMainRlGuide.check(id);
    }


    private void initViewPager() {
        HomeFragment homeFragment = new HomeFragment();
        WarnFragment warnFragment = new WarnFragment();
        ManagerFragment managerFragment = new ManagerFragment();
        mFragmentList = new ArrayList<>();
        mFragmentList.add(homeFragment);
        mFragmentList.add(warnFragment);
        mFragmentList.add(managerFragment);
        mPageAdapter = new MainFragmentPageAdapter(mActivity.getSupportFragmentManager(), mFragmentList);
        acMainHvpContent.setAdapter(mPageAdapter);
        acMainHvpContent.setCurrentItem(0);
        setRbChecked(R.id.ac_main_rb_main);
    }

}
