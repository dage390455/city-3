package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.HistoryLogRcContentAdapter;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.HistoryLogFragmentPresenter;
import com.sensoro.smartcity.imainviews.IHistoryLogFragmentView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HistoryLogFragment extends BaseFragment<IHistoryLogFragmentView, HistoryLogFragmentPresenter>
        implements IHistoryLogFragmentView {
    @BindView(R.id.include_imv_title_imv_arrows_left)
    ImageView includeImvTitleImvArrowsLeft;
    @BindView(R.id.include_imv_title_tv_title)
    TextView includeImvTitleTvTitle;
    @BindView(R.id.include_imv_title_imv_subtitle)
    ImageView includeImvTitleImvSubtitle;
    @BindView(R.id.fg_history_log_rc_content)
    RecyclerView fgHistoryLogRcContent;
    Unbinder unbinder;

    @Override
    protected void initData(Context activity) {
        initView();
    }

    private void initView() {
        new HistoryLogRcContentAdapter(mRootFragment.getActivity());
    }

    @Override
    protected int initRootViewId() {
        return R.layout.fragment_history_log;
    }

    @Override
    protected HistoryLogFragmentPresenter createPresenter() {
        return new HistoryLogFragmentPresenter();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
