package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeployActivityView;
import com.sensoro.smartcity.imainviews.IDeployDeviceTagActivityView;
import com.sensoro.smartcity.presenter.DeployDeviceTagActivityPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeployDeviceTagActivity extends BaseActivity<IDeployDeviceTagActivityView, DeployDeviceTagActivityPresenter>
        implements IDeployActivityView {
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.ac_deploy_device_tag_commit)
    TextView acDeployDeviceTagCommit;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deploy_device_tag);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        includeTextTitleTvTitle.setText("标签");
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
    }

    @Override
    protected DeployDeviceTagActivityPresenter createPresenter() {
        return new DeployDeviceTagActivityPresenter();
    }

    @Override
    public void setTitleTextView(String title) {

    }

    @Override
    public void setNameAddressEditText(String text) {

    }

    @Override
    public void setUploadButtonClickable(boolean isClickable) {

    }

    @Override
    public void setContactEditText(String contact) {

    }

    @Override
    public void addDefaultTextView() {

    }

    @Override
    public void refreshTagLayout(List<String> tagList) {

    }

    @Override
    public void refreshSignal(long updateTime, String signal) {

    }

    @Override
    public void setDeployDeviceRlSignalVisible(boolean isVisible) {

    }

    @Override
    public void setDeployContactRelativeLayoutVisible(boolean isVisible) {

    }

    @Override
    public void setDeployPhotoVisible(boolean isVisible) {

    }

    @Override
    public void showUploadProgressDialog(int currentNum, int count, double percent) {

    }

    @Override
    public void dismissUploadProgressDialog() {

    }

    @Override
    public void showStartUploadProgressDialog() {

    }

    @Override
    public void setDeployPhotoText(String text) {

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


}
