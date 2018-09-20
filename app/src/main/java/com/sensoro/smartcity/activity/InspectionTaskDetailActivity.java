package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.TagAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IInspectionTaskDetailActivityView;
import com.sensoro.smartcity.presenter.InspectionTaskDetailActivityPresenter;
import com.sensoro.smartcity.widget.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InspectionTaskDetailActivity extends BaseActivity<IInspectionTaskDetailActivityView, InspectionTaskDetailActivityPresenter>
        implements IInspectionTaskDetailActivityView {
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.ac_inspection_detail_btn_start)
    TextView acInspectionDetailBtnStart;
    @BindView(R.id.ac_inspection_detail_task_number)
    TextView acInspectionDetailTaskNumber;
    @BindView(R.id.ac_inspection_detail_task_tv_time)
    TextView acInspectionDetailTaskTvTime;
    @BindView(R.id.ac_inspection_detail_task_tv_device_count)
    TextView acInspectionDetailTaskTvDeviceCount;
    @BindView(R.id.ac_inspection_detail_task_tv_state)
    TextView acInspectionDetailTaskTvState;
    @BindView(R.id.ac_inspection_detail_task_rl_content)
    RelativeLayout acInspectionDetailTaskRlContent;
    @BindView(R.id.ac_inspection_detail_task_rc_device_count_tag)
    RecyclerView acInspectionDetailTaskRcDeviceCountTag;
    private TagAdapter mTagAdapter;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_inspection_task_detail);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);

    }

    private void initView() {
        includeTextTitleTvTitle.setText("巡检任务");
        includeTextTitleTvSubtitle.setVisibility(View.GONE);

        acInspectionDetailTaskNumber.setText("XT20180723");
        acInspectionDetailTaskTvTime.setText("2018.05.02-2018.05.05");

        initTvState(R.color.c_8058a5,"待执行");

        initRcDeviceCountTag();

    }

    private void initRcDeviceCountTag() {
        mTagAdapter = new TagAdapter(mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        acInspectionDetailTaskRcDeviceCountTag.setLayoutManager(manager);
        acInspectionDetailTaskRcDeviceCountTag.setAdapter(mTagAdapter);
    }

    private void initTvState(@ColorRes int colorId,String text) {
        Resources resources = mActivity.getResources();
        GradientDrawable gd = (GradientDrawable) resources.getDrawable(R.drawable.shape_small_oval_29c);
        gd.setBounds(0,0,gd.getMinimumWidth(),gd.getMinimumHeight());
        int color = resources.getColor(colorId);
        gd.setColor(color);
        acInspectionDetailTaskTvState.setCompoundDrawables(gd,null,null,null);
        acInspectionDetailTaskTvState.setTextColor(color);
        acInspectionDetailTaskTvState.setText(text);
    }

    @Override
    protected InspectionTaskDetailActivityPresenter createPresenter() {
        return new InspectionTaskDetailActivityPresenter();
    }

    @Override
    public void startAC(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void finishAc() {
        finish();
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
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_LONG).show();
    }


    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.ac_inspection_detail_btn_start,R.id.ac_inspection_detail_task_rl_content})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;
            case R.id.ac_inspection_detail_btn_start:
                mPresenter.doBtnStart();
                break;
            case R.id.ac_inspection_detail_task_rl_content:
                mPresenter.doRlContent();
                break;
        }
    }

    @Override
    public void updateTagsData(List<String> tagList) {
        mTagAdapter.updateTags(tagList);
    }
}
