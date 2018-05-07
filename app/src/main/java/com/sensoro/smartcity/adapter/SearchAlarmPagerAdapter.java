package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.fragment.SerachAlarmPageFragment;

public class SearchAlarmPagerAdapter extends FragmentPagerAdapter {
    private static final int PAGE_COUNT = 3;

    private Context mContext;
    private String text;

    public SearchAlarmPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        int type;
        switch (position) {
            case 0:
                type = Constants.TYPE_DEVICE_NAME;
                break;
            case 1:
                type = Constants.TYPE_DEVICE_NUMBER;
                break;
            case 2:
                type = Constants.TYPE_DEVICE_PHONE_NUM;
                break;
            default:
                type = Constants.TYPE_DEVICE_NAME;
                break;
        }
        return SerachAlarmPageFragment.newInstance(type, text);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "设备名称";
            case 1:
                return "设备号";
            case 2:
                return "手机号";
            default:
                return "设备名称";
        }
    }

    public void setText(String text) {
        this.text = text;
    }
}
