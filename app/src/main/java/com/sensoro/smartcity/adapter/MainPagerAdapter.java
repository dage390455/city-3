package com.sensoro.smartcity.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragments = new ArrayList<>();
//    private int mSize;
//    private long baseId = 0;
//    private int id = 1;
//    private FragmentManager fm;

    public MainPagerAdapter(android.support.v4.app.FragmentManager fm) {
        super(fm);
//        this.fm = fm;
//        mSize = fragments == null ? 0 : fragments.size();
    }

    public void updateMainPagerAdapter(List<Fragment> fragments) {
        this.fragments.clear();
        this.fragments.addAll(fragments);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

//    @Override
//    public int getItemPosition(Object object) {
//        Fragment fragment = (Fragment) object;
//        int position = fragments.indexOf(fragment);
//
//        if (position >= 0) {
//            return position;
//        } else {
//            return POSITION_NONE;
//        }
//    }

//    @Override
//    public long getItemId(int position) {
//        return baseId + position;
//    }

//    public void setFragments(List<Fragment> fragments) {
//        this.fragments = fragments;
//        mSize = fragments == null ? 0 : fragments.size();
//    }

//    @Override
//    public boolean isViewFromObject(View view, Object object) {
//        return view == ((Fragment) object).getView();
//    }

    //
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        Fragment fragment = ((Fragment) object);
////        fragments.remove(fragment);
//    }
//
//    @Override
//    public void notifyDataSetChanged() {
//        super.notifyDataSetChanged();
//        id++;
//        baseId += getCount() + id;
//    }
}
