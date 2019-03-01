package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.TagAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IInspectionActivityView;
import com.sensoro.smartcity.presenter.InspectionActivityPresenter;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.toast.SensoroToast;
import com.sensoro.smartcity.widget.SpacesItemDecoration;
import com.sensoro.smartcity.widget.dialog.TipDialogUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InspectionActivity extends BaseActivity<IInspectionActivityView, InspectionActivityPresenter>
        implements IInspectionActivityView, TipDialogUtils.TipDialogUtilsClickListener {
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.ac_inspection_tv_name)
    TextView acInspectionTvName;
    @BindView(R.id.ac_inspection_tv_sn)
    TextView acInspectionTvSn;
    @BindView(R.id.ac_inspection_rc_tag)
    RecyclerView acInspectionRcTag;
    @BindView(R.id.ac_inspection_tv_exception)
    TextView acInspectionTvException;
    @BindView(R.id.ac_inspection_tv_normal)
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
        mNormalDialog.setTipConfirmText(mActivity.getString(R.string.normal), mActivity.getResources().getColor(R.color.c_29c093));
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
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_SHORT).show();
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

    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.include_text_title_tv_subtitle, R.id.ac_inspection_tv_exception, R.id.ac_inspection_tv_normal})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;
            case R.id.include_text_title_tv_subtitle:
                mPresenter.doInspectionInstruction();
                break;
            case R.id.ac_inspection_tv_exception:
                mPresenter.doUploadException();
                break;
            case R.id.ac_inspection_tv_normal:
                showNormalDialog();
                break;
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
            acInspectionTvNormal.setBackgroundResource(R.drawable.shape_bg_corner_29c_shadow);
        } else {
            acInspectionTvNormal.setTextColor(mActivity.getResources().getColor(R.color.white));
            acInspectionTvNormal.setBackgroundResource(R.drawable.shape_bg_solid_df_corner);
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
