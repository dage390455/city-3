package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.InspectionInstructionTabAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.presenter.IInspectionInstructionActivityView;
import com.sensoro.smartcity.presenter.InspectionInstructionActivityPresenter;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InspectionInstructionActivity extends BaseActivity<IInspectionInstructionActivityView, InspectionInstructionActivityPresenter>
        implements IInspectionInstructionActivityView {
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;
    @BindView(R.id.ac_inspection_instruction_rc_tab)
    RecyclerView acInspectionInstructionRcTab;
    @BindView(R.id.ac_inspection_instruction_web)
    WebView acInspectionInstructionWeb;
    private InspectionInstructionTabAdapter mTabAdapter;

    private List<String> tabs;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_inspection_instruction);
        ButterKnife.bind(this);
        iniView();
        mPresenter.initData(mActivity);
    }

    private void iniView() {
        includeTextTitleTvTitle.setText("巡检内容");
        includeTextTitleTvSubtitle.setVisibility(View.GONE);

        initRcTab();
    }

    private void initRcTab() {
        mTabAdapter = new InspectionInstructionTabAdapter(mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        acInspectionInstructionRcTab.setLayoutManager(manager);
        acInspectionInstructionRcTab.setAdapter(mTabAdapter);

        mTabAdapter.setRecycleViewItemClickListener(new RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });

        tabs = new ArrayList<>();
        tabs.add("烟感");
        tabs.add("电气火灾");
        tabs.add("一氧化碳");
        mTabAdapter.settabs(tabs);
    }

    @Override
    protected InspectionInstructionActivityPresenter createPresenter() {
        return new InspectionInstructionActivityPresenter();
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


    @OnClick(R.id.include_text_title_imv_arrows_left)
    public void onViewClicked() {
    }
}
