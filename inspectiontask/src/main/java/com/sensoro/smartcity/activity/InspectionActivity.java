package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.sensoro.common.adapter.TagAdapter;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.SpacesItemDecoration;
import com.sensoro.common.widgets.dialog.TipDialogUtils;
import com.sensoro.inspectiontask.R;
import com.sensoro.inspectiontask.R2;
import com.sensoro.smartcity.imainviews.IInspectionActivityView;
import com.sensoro.smartcity.presenter.InspectionActivityPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
@Route(path= ARouterConstants.ACTIVITY_INSPECTION)
public class InspectionActivity extends BaseActivity<IInspectionActivityView, InspectionActivityPresenter>
        implements IInspectionActivityView, TipDialogUtils.TipDialogUtilsClickListener {
    @BindView(R2.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R2.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R2.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R2.id.ac_inspection_tv_name)
    TextView acInspectionTvName;
    @BindView(R2.id.ac_inspection_tv_sn)
    TextView acInspectionTvSn;
    @BindView(R2.id.ac_inspection_rc_tag)
    RecyclerView acInspectionRcTag;
    @BindView(R2.id.ac_inspection_tv_exception)
    TextView acInspectionTvException;
    @BindView(R2.id.ac_inspection_tv_normal)
    TextView acInspectionTvNormal;
    private TagAdapter mTagAdapter;
    private TipDialogUtils mNormalDialog;
    private ProgressUtils mProgressUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.actvity_inspection);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        includeTextTitleTvTitle.setText(R.string.patrol_monitoring_point);
        includeTextTitleTvSubtitle.setText(R.string.inspection_content);
        initRcTag();
        acInspectionTvNormal.setEnabled(false);
        initNormalDialog();
    }

    @Override
    protected void onStart() {
        mPresenter.onStart();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    private void initNormalDialog() {
        mNormalDialog = new TipDialogUtils(mActivity);
        mNormalDialog.setTipMessageText(mActivity.getString(R.string.confirm_that_the_monitoring_point_is_normal));
        mNormalDialog.setTipCacnleText(mActivity.getString(R.string.i_will_see_again), mActivity.getResources().getColor(R.color.c_a6a6a6));
        mNormalDialog.setTipConfirmText(mActivity.getString(R.string.normal), mActivity.getResources().getColor(R.color.c_1dbb99));
        mNormalDialog.setTipDialogUtilsClickListener(this);
    }

    private void initRcTag() {
        mTagAdapter = new TagAdapter(mActivity, R.color.c_252525, R.color.c_dfdfdf);
        SensoroLinearLayoutManager manager = new SensoroLinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        acInspectionRcTag.setLayoutManager(manager);
        int spacingInPixels = mActivity.getResources().getDimensionPixelSize(R.dimen.x10);
        acInspectionRcTag.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
        acInspectionRcTag.setLayoutManager(manager);
        acInspectionRcTag.setAdapter(mTagAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected InspectionActivityPresenter createPresenter() {
        return new InspectionActivityPresenter();
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
        mProgressUtils.showProgress();
    }

    @Override
    public void dismissProgressDialog() {
        mProgressUtils.dismissProgress();
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }

    @Override
    protected void onDestroy() {
        mProgressUtils.destroyProgress();
        if (mNormalDialog != null) {
            mNormalDialog.destory();
        }
        super.onDestroy();
    }

    @OnClick({R2.id.include_text_title_imv_arrows_left, R2.id.include_text_title_tv_subtitle, R2.id.ac_inspection_tv_exception, R2.id.ac_inspection_tv_normal})
    public void onViewClicked(View view) {
        int id=view.getId();
        if(id==R.id.include_text_title_imv_arrows_left){
            finishAc();
        }else if(id==R.id.include_text_title_tv_subtitle){
            mPresenter.doInspectionInstruction();
        }else if(id==R.id.ac_inspection_tv_exception){
            mPresenter.doUploadException();
        }else if(id==R.id.ac_inspection_tv_normal){
            showNormalDialog();
        }
    }

    @Override
    public void updateTagsData(List<String> tagList) {
        mTagAdapter.updateTags(tagList);
    }

    @Override
    public void showNormalDialog() {
        mNormalDialog.show();
    }

    @Override
    public void setMonitorTitle(String title) {
        acInspectionTvName.setText(title);
    }

    @Override
    public void setMonitorSn(String sn) {
        acInspectionTvSn.setText(sn);
    }

    @Override
    public void setConfirmState(boolean hasBle) {
        acInspectionTvNormal.setEnabled(hasBle);
        if (hasBle) {
            acInspectionTvNormal.setTextColor(mActivity.getResources().getColor(R.color.white));
            acInspectionTvNormal.setBackgroundResource(R.drawable.shape_bg_inspectiontask_corner_dfdf_shadow);
        } else {
            acInspectionTvNormal.setTextColor(mActivity.getResources().getColor(R.color.white));
            acInspectionTvNormal.setBackgroundResource(R.drawable.shape_bg_inspectiontask_corner_dfdf_shadow);
        }

    }

    @Override
    public void onCancelClick() {
        mNormalDialog.dismiss();
    }

    @Override
    public void onConfirmClick() {
        mNormalDialog.dismiss();
        //上传正常
        mPresenter.doUploadNormal();
    }
}
