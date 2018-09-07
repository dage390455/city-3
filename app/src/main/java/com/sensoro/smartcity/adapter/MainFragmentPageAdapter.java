package com.sensoro.smartcity.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class MainFragmentPageAdapter extends FragmentPagerAdapter {

    public MainFragmentPageAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    private List<Fragment> fragmentList;

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        int ret = 0;
        if (fragmentList != null) {
            ret = fragmentList.size();
        }
        return ret;
    }
}
