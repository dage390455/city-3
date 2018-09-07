package com.sensoro.smartcity.presenter;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.MainActivityTest;
import com.sensoro.smartcity.adapter.MainFragmentPageAdapter;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.fragment.HomeFragment;
import com.sensoro.smartcity.fragment.ManagerFragment;
import com.sensoro.smartcity.fragment.WarnFragment;
import com.sensoro.smartcity.imainviews.IMainViewTest;

import java.util.ArrayList;

public class MainPresenterTest extends BasePresenter<IMainViewTest> {

    private ArrayList<Fragment> mFragmentList;
    private Context mContext;

    @Override
    public void initData(Context context) {
        mContext = context;
        initViewPager();
    }

    private void initViewPager() {
        HomeFragment homeFragment = new HomeFragment();
        WarnFragment warnFragment = new WarnFragment();
        ManagerFragment managerFragment = new ManagerFragment();
        mFragmentList = new ArrayList<>();
        mFragmentList.add(homeFragment);
        mFragmentList.add(warnFragment);
        mFragmentList.add(managerFragment);
        getView().setMainHomeVpAdapter(new MainFragmentPageAdapter(
                ((MainActivityTest) mContext).getSupportFragmentManager(), mFragmentList));
        getView().setHpCurrentItem(0);
        getView().setRbChecked(R.id.ac_main_rb_main);
    }

    @Override
    public void onDestroy() {

    }
}
