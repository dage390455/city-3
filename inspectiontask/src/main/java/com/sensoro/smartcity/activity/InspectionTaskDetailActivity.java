package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.common.adapter.TagAdapter;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.inspectiontask.R;
import com.sensoro.inspectiontask.R2;
import com.sensoro.smartcity.imainviews.IInspectionTaskDetailActivityView;
import com.sensoro.smartcity.presenter.InspectionTaskDetailActivityPresenter;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.SpacesItemDecoration;
import com.sensoro.common.utils.ViewHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InspectionTaskDetailActivity extends BaseActivity<IInspectionTaskDetailActivityView, InspectionTaskDetailActivityPresenter>
        implements IInspectionTaskDetailActivityView {
    @BindView(R2.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R2.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R2.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R2.id.ac_inspection_detail_btn_start)
    TextView acInspectionDetailBtnStart;
    @BindView(R2.id.ac_inspection_detail_task_number)
    TextView acInspectionDetailTaskNumber;
    @BindView(R2.id.ac_inspection_detail_task_tv_time)
    TextView acInspectionDetailTaskTvTime;
    @BindView(R2.id.ac_inspection_detail_task_tv_device_count)
    TextView acInspectionDetailTaskTvDeviceCount;
    @BindView(R2.id.ac_inspection_detail_task_tv_state)
    TextView acInspectionDetailTaskTvState;
    @BindView(R2.id.ac_inspection_detail_task_rl_content)
    RelativeLayout acInspectionDetailTaskRlContent;
    @BindView(R2.id.ac_inspection_detail_task_rc_device_count_tag)
    RecyclerView acInspectionDetailTaskRcDeviceCountTag;
    private TagAdapter mTagAdapter;
    private ProgressUtils mProgressDialog;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_inspection_task_detail);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);

    }

    private void initView() {
        mProgressDialog = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        includeTextTitleTvTitle.setText(R.string.inspection_task);
        includeTextTitleTvSubtitle.setVisibility(View.GONE);

        initRcDeviceCountTag();

    }

    private void initRcDeviceCountTag() {
        mTagAdapter = new TagAdapter(mActivity, R.color.c_252525, R.color.c_dfdfdf);
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        acInspectionDetailTaskRcDeviceCountTag.setLayoutManager(layoutManager);
        int spacingInPixels = mActivity.getResources().getDimensionPixelSize(R.dimen.x10);
        acInspectionDetailTaskRcDeviceCountTag.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
        acInspectionDetailTaskRcDeviceCountTag.setAdapter(mTagAdapter);
    }


    @Override
    protected InspectionTaskDetailActivityPresenter createPresenter() {
        return new InspectionTaskDetailActivityPresenter();
    }

    @Override
    public void startAC(Intent intent) {
        mActivity.startActivity(intent);
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
        mProgressDialog.showProgress();
    }

    @Override
    public void dismissProgressDialog() {
        mProgressDialog.destroyProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.destroyProgress();
            mProgressDialog = null;
        }
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_LONG).show();
    }


    @OnClick({R2.id.include_text_title_imv_arrows_left, R2.id.ac_inspection_detail_btn_start, R2.id.ac_inspection_detail_task_rl_content})
    public void onViewClicked(View view) {
        int viewID=view.getId();
        if(viewID==R.id.include_text_title_imv_arrows_left){
            finishAc();
        }else  if(viewID==R.id.ac_inspection_detail_btn_start){
            mPresenter.doBtnStart();
        }else if(viewID==R.id.ac_inspection_detail_task_rl_content){
            mPresenter.doRlContent();
        }

    }

    @Override
    public void updateTagsData(List<String> tagList) {
        mTagAdapter.updateTags(tagList);
    }

    @Override
    public void setTvState(int colorId, String text) {
        ViewHelper.changeTvState(mActivity,acInspectionDetailTaskTvState,colorId, text);
    }



    @Override
    public void setTvTaskNumber(String id) {
        acInspectionDetailTaskNumber.setText(id);
    }

    @Override
    public void setTvTaskTime(String time) {
        acInspectionDetailTaskTvTime.setText(time);
    }

    @Override
    public void setTvbtnStartState(@DrawableRes int drawableRes, @ColorRes int color, String text) {
        acInspectionDetailBtnStart.setBackgroundResource(drawableRes);
        acInspectionDetailBtnStart.setText(text);
        acInspectionDetailBtnStart.setTextColor(mActivity.getResources().getColor(color));
    }
}
