package com.sensoro.smartcity.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public MainPagerAdapter(android.support.v4.app.FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
