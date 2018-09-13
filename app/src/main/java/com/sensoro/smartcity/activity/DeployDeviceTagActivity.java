package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.DeployDeviceTagAddTagAdapter;
import com.sensoro.smartcity.adapter.DeployDeviceTagHistoryTagAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeployActivityView;
import com.sensoro.smartcity.imainviews.IDeployDeviceTagActivityView;
import com.sensoro.smartcity.presenter.DeployDeviceTagActivityPresenter;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeployDeviceTagActivity extends BaseActivity<IDeployDeviceTagActivityView, DeployDeviceTagActivityPresenter>
        implements IDeployActivityView,DeployDeviceTagAddTagAdapter.DeployDeviceTagAddTagItemClickListener,View.OnClickListener{
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.ac_deploy_device_tag_commit)
    TextView acDeployDeviceTagCommit;
    @BindView(R.id.ac_deploy_device_tag_rc_add_tag)
    RecyclerView acDeployDeviceTagRcAddTag;
    @BindView(R.id.ac_deploy_device_tag_rc_history_tag)
    RecyclerView acDeployDeviceTagRcHistoryTag;
    private DeployDeviceTagAddTagAdapter mAddTagAdapter;
    private DeployDeviceTagHistoryTagAdapter mHistoryTagAdapter;
    private AlertDialog mAddTagDialog;
    private EditText mDialogEtInput;
    private ImageView mDialogImvClear;
    private TextView mDialogTvCancel;
    private TextView mDialogTvConfirm;

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

        initRcAddTag();

        initRcHistoryTag();

    }

    private void initAddTagDialog() {
        View view = View.inflate(mActivity, R.layout.dialog_frag_deploy_device_add_tag, null);
        mDialogEtInput = view.findViewById(R.id.dialog_add_tag_et_input);
        mDialogImvClear = view.findViewById(R.id.dialog_add_tag_imv_clear);
        mDialogTvCancel = view.findViewById(R.id.dialog_add_tag_tv_cancel);
        mDialogTvConfirm = view.findViewById(R.id.dialog_add_tag_tv_confirm);
        mDialogTvConfirm.setOnClickListener(this);
        mDialogTvCancel.setOnClickListener(this);
        mDialogImvClear.setOnClickListener(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setView(view);
        builder.setCancelable(false);
        mAddTagDialog = builder.create();

    }

    private void initRcHistoryTag() {
        mHistoryTagAdapter = new DeployDeviceTagHistoryTagAdapter(mActivity);
        SensoroLinearLayoutManager manager = new SensoroLinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        acDeployDeviceTagRcHistoryTag.setLayoutManager(manager);
        acDeployDeviceTagRcHistoryTag.setAdapter(mHistoryTagAdapter);
    }

    private void initRcAddTag() {
        mAddTagAdapter = new DeployDeviceTagAddTagAdapter(mActivity);
        SensoroLinearLayoutManager manager = new SensoroLinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        acDeployDeviceTagRcAddTag.setLayoutManager(manager);
        acDeployDeviceTagRcAddTag.setAdapter(mAddTagAdapter);
        mAddTagAdapter.setDeployDeviceTagAddTagItemClickListener(this);
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
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAddTagDialog!=null) {
            mAddTagDialog.cancel();
        }
    }

    @OnClick(R.id.ac_deploy_device_tag_commit)
    public void onViewClicked() {
    }

    @Override
    public void onAddClick() {
        if (mAddTagDialog == null) {
            initAddTagDialog();
        }
        mDialogEtInput.getText().clear();
        mAddTagDialog.show();
        toastShort("添加点击了");
    }

    @Override
    public void onDeleteClick() {
        toastShort("删除点击了");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_add_tag_tv_cancel:
                mAddTagDialog.dismiss();
                break;
            case R.id.dialog_add_tag_tv_confirm:
                mAddTagDialog.dismiss();
                break;
            case R.id.dialog_add_tag_imv_clear:
                mDialogEtInput.getText().clear();
                break;

        }
    }
}
