package com.sensoro.common.fragment;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.sensoro.common.R;
import com.sensoro.common.R2;
import com.sensoro.common.base.BaseFragment;
import com.sensoro.common.imainview.IFireSecurityWarnView;
import com.sensoro.common.presenter.FireSecurityWarnPresenter;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.widgets.SensoroTextWidthColorBar;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.slidebar.TextWidthColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

/**
 * @author bin.tian
 */
public class FireSecurityWarnFragment extends BaseFragment<IFireSecurityWarnView, FireSecurityWarnPresenter> implements IFireSecurityWarnView {

    @BindView(R2.id.content_viewPager)
    ViewPager mContentViewPager;
    @BindView(R2.id.indicatorView)
    FixedIndicatorView mIndicatorView;
    @BindView(R2.id.line_top)
    View lineTop;

    private FireSecurityWarnPageAdapter mFireSecurityWarnPageAdapter;

    @Override
    protected void initData(Context activity) {
        initView();
        mPresenter.initData(activity);
    }

    private void initView() {
        mFireSecurityWarnPageAdapter = new FireSecurityWarnPageAdapter(mRootFragment.getFragmentManager());
        mIndicatorView.setScrollBar(new SensoroTextWidthColorBar(getContext(), mIndicatorView, ContextCompat.getColor(getContext(), R.color.c_1DBB99), AppUtils.dp2px(getContext(), 2F)));
        mIndicatorView.setSplitMethod(FixedIndicatorView.SPLITMETHOD_EQUALS);
        IndicatorViewPager indicatorViewPager = new IndicatorViewPager(mIndicatorView, mContentViewPager);
        indicatorViewPager.setAdapter(mFireSecurityWarnPageAdapter);
        mIndicatorView.setOnTransitionListener(new OnTransitionTextListener(16F,
                14F,
                ContextCompat.getColor(getContext(), R.color.c_252525),
                ContextCompat.getColor(getContext(), R.color.c_a6a6a6)));
    }

    @Override
    protected int initRootViewId() {
        return R.layout.fragment_fire_security_warn;
    }

    @Override
    protected FireSecurityWarnPresenter createPresenter() {
        return new FireSecurityWarnPresenter();
    }

    @Override
    public void onFragmentStart() {

    }

    @Override
    public void onFragmentStop() {

    }

    @Override
    public void updateFireSecurityPageAdapterData(List<String> fragmentTitleList, List<Fragment> fragments) {
        mFireSecurityWarnPageAdapter.setFragmentData(fragmentTitleList, fragments);
        mFireSecurityWarnPageAdapter.notifyDataSetChanged();
    }

    @Override
    public void setHasFireSecurityView(boolean visible) {
        mIndicatorView.setVisibility(visible ? View.VISIBLE : View.GONE);
        lineTop.setVisibility(visible ? View.VISIBLE : View.GONE);
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
    public void startAC(Intent intent) {
        Objects.requireNonNull(mRootFragment.getActivity()).startActivity(intent);
    }

    @Override
    public void finishAc() {
        Objects.requireNonNull(mRootFragment.getActivity()).finish();
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


    private class FireSecurityWarnPageAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
        private final List<String> fragmentTitleList = new ArrayList<>(2);
        private final List<Fragment> fragmentList = new ArrayList<>(2);

        public FireSecurityWarnPageAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.fire_security_tap_top_layout, container, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(fragmentTitleList.get(position));
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            return fragmentList.get(position);
        }


        public void setFragmentData(List<String> fragmentTitleList, List<Fragment> fragmentList) {
            this.fragmentTitleList.clear();
            this.fragmentList.clear();
            this.fragmentTitleList.addAll(fragmentTitleList);
            this.fragmentList.addAll(fragmentList);
        }
    }

    ;
}
