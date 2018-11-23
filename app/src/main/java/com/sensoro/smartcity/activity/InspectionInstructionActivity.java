package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.InspectionInstructionContentAdapter;
import com.sensoro.smartcity.adapter.InspectionInstructionTabAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IInspectionInstructionActivityView;
import com.sensoro.smartcity.presenter.InspectionInstructionActivityPresenter;
import com.sensoro.smartcity.server.bean.InspectionTaskInstructionModel;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;

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
    @BindView(R.id.ac_inspection_instruction_rc_content)
    RecyclerView acInspectionInstructionRcContent;
    private InspectionInstructionTabAdapter mTabAdapter;
    private InspectionInstructionContentAdapter mContentAdapter;

    private ProgressUtils mProgressUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_inspection_instruction);
        ButterKnife.bind(this);
        iniView();
        mPresenter.initData(mActivity);
    }

    private void iniView() {
        includeTextTitleTvTitle.setText(R.string.inspection_content);
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());

        initRcTab();

        initRcContent();
    }

    private void initRcContent() {
        mContentAdapter = new InspectionInstructionContentAdapter(mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        acInspectionInstructionRcContent.setLayoutManager(manager);
        acInspectionInstructionRcContent.setAdapter(mContentAdapter);

        mContentAdapter.setOnInspectionInstructionContentPicClickListenter(new InspectionInstructionContentAdapter.OnInspectionInstructionContentPicClickListenter() {
            @Override
            public void onInspectionInstructionContentPicClick(List<ScenesData> dataList, int position) {
                mPresenter.doPreviewPhoto(dataList,position);
            }
        });
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
                mPresenter.doRequestTemplate(mTabAdapter.getItem(position));
            }
        });

    }

    @Override
    protected InspectionInstructionActivityPresenter createPresenter() {
        return new InspectionInstructionActivityPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
    }

    @Override
    public void startAC(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void finishAc() {
        mActivity.finish();
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
        mProgressUtils.showProgress();
    }

    @Override
    public void dismissProgressDialog() {
        mProgressUtils.dismissProgress();
    }

    @Override
    public void toastShort(String msg) {

    }

    @Override
    public void toastLong(String msg) {

    }


    @OnClick(R.id.include_text_title_imv_arrows_left)
    public void onViewClicked() {
        finishAc();
    }

    @Override
    public void updateRcContentData(List<InspectionTaskInstructionModel.DataBean> data) {
        mContentAdapter.updateDataList(data);
    }

    @Override
    public void updateRcTag(List<String> deviceTypes) {
        mTabAdapter.updateTagDataList(deviceTypes);
    }
}
