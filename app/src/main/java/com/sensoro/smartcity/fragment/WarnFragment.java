package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.MainWarnFragRcContentAdapter;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IWarnFragmentView;
import com.sensoro.smartcity.presenter.WarnFragmentPresenter;

import butterknife.BindView;
import butterknife.OnClick;

public class WarnFragment extends BaseFragment<IWarnFragmentView, WarnFragmentPresenter> implements
        IWarnFragmentView {
    @BindView(R.id.fg_main_warn_tv_search)
    TextView fgMainWarnTvSearch;
    @BindView(R.id.fg_main_warn_imv_calendar)
    ImageView fgMainWarnImvCalendar;
    @BindView(R.id.fg_main_warn_rc_content)
    RecyclerView fgMainWarnRcContent;

    @Override
    protected void initData(Context activity) {
        mPresenter.initData(activity);
    }

    @Override
    protected int initRootViewId() {
        return R.layout.fragment_main_warn;
    }

    @Override
    protected WarnFragmentPresenter createPresenter() {
        return new WarnFragmentPresenter();
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
    public void onFragmentStart() {

    }

    @Override
    public void onFragmentStop() {

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
    public void setRcContentAdapter(MainWarnFragRcContentAdapter adapter, LinearLayoutManager manager) {
        fgMainWarnRcContent.setLayoutManager(manager);
        fgMainWarnRcContent.setAdapter(adapter);
    }


    @OnClick({R.id.fg_main_warn_tv_search, R.id.fg_main_warn_imv_calendar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fg_main_warn_tv_search:
                break;
            case R.id.fg_main_warn_imv_calendar:
                break;
        }
    }
}
