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
import com.sensoro.smartcity.adapter.DeployDeviceTagAddTagAdapter;
import com.sensoro.smartcity.adapter.DeployDeviceTagHistoryTagAdapter;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeployDeviceTagActivityView;
import com.sensoro.smartcity.presenter.DeployDeviceTagActivityPresenter;
import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.dialog.TagDialogUtils;
import com.sensoro.smartcity.widget.dialog.TipOperationDialogUtils;
import com.sensoro.common.widgets.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sensoro.smartcity.widget.dialog.TagDialogUtils.DIALOG_TAG_ADD;
import static com.sensoro.smartcity.widget.dialog.TagDialogUtils.DIALOG_TAG_EDIT;

public class DeployDeviceTagActivity extends BaseActivity<IDeployDeviceTagActivityView, DeployDeviceTagActivityPresenter>
        implements IDeployDeviceTagActivityView, DeployDeviceTagAddTagAdapter.DeployDeviceTagAddTagItemClickListener, RecycleViewItemClickListener,
        TagDialogUtils.OnTagDialogListener, TipOperationDialogUtils.TipDialogUtilsClickListener {
    @BindView(R.id.include_text_title_tv_cancel)
    TextView includeTextTitleTvCancel;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;

    @BindView(R.id.iv_ac_deploy_device_tag_delete_history)
    ImageView ivAcDeployDeviceTagDeleteHistoryTag;
    @BindView(R.id.ac_deploy_device_tag_rc_add_tag)
    RecyclerView acDeployDeviceTagRcAddTag;
    @BindView(R.id.ac_deploy_device_tag_rc_history_tag)
    RecyclerView acDeployDeviceTagRcHistoryTag;
    private DeployDeviceTagAddTagAdapter mAddTagAdapter;
    private DeployDeviceTagHistoryTagAdapter mHistoryTagAdapter;
    private TagDialogUtils tagDialogUtils;
    private TipOperationDialogUtils historyClearDialog;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deploy_device_tag);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        tagDialogUtils = new TagDialogUtils(mActivity);
        tagDialogUtils.registerListener(this);
        initTitle();
        initRcAddTag();
        initRcHistoryTag();
        initClearHistoryDialog();

    }

    private void initTitle() {
        includeTextTitleTvTitle.setText(R.string.sensor_detail_tag);
        includeTextTitleTvCancel.setVisibility(View.VISIBLE);
        includeTextTitleTvCancel.setTextColor(getResources().getColor(R.color.c_b6b6b6));
        includeTextTitleTvCancel.setText(R.string.cancel);
        includeTextTitleTvSubtitle.setVisibility(View.VISIBLE);
        includeTextTitleTvSubtitle.setText(getString(R.string.save));
        updateSaveStatus(true);
    }

    private void initClearHistoryDialog() {
        historyClearDialog = new TipOperationDialogUtils(mActivity, true);
        historyClearDialog.setTipTitleText(getString(R.string.history_clear_all));
        historyClearDialog.setTipMessageText(getString(R.string.confirm_clear_history_record), R.color.c_a6a6a6);
        historyClearDialog.setTipCancelText(getString(R.string.cancel), getResources().getColor(R.color.c_1dbb99));
        historyClearDialog.setTipConfirmText(getString(R.string.clear), getResources().getColor(R.color.c_a6a6a6));
        historyClearDialog.setTipDialogUtilsClickListener(this);
    }

    @Override
    public void updateSaveStatus(boolean isEnable) {
        includeTextTitleTvSubtitle.setEnabled(isEnable);
        includeTextTitleTvSubtitle.setTextColor(isEnable ? getResources().getColor(R.color.c_1dbb99) : getResources().getColor(R.color.c_dfdfdf));

    }

    @Override
    public void showHistoryClearDialog() {
        if (historyClearDialog != null) {
            historyClearDialog.show();
        }
    }


    private void initRcHistoryTag() {
        mHistoryTagAdapter = new DeployDeviceTagHistoryTagAdapter(mActivity);
        mHistoryTagAdapter.setRecycleViewItemClickListener(this);
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
    public void startAC(Intent intent) {

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
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }

    @Override
    protected void onDestroy() {
        if (tagDialogUtils != null) {
            tagDialogUtils.unregisterListener();
            tagDialogUtils = null;
        }

        if (historyClearDialog != null) {
            historyClearDialog.destroy();
            historyClearDialog = null;
        }

        super.onDestroy();
    }

    @OnClick({R.id.include_text_title_tv_subtitle, R.id.include_text_title_tv_cancel, R.id.iv_ac_deploy_device_tag_delete_history})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_tv_subtitle:
                mPresenter.doFinish();
                break;
            case R.id.include_text_title_tv_cancel:
                finishAc();
                break;
            case R.id.iv_ac_deploy_device_tag_delete_history:
                historyClearDialog.show();
                break;
        }
    }

    @Override
    public void onAddClick() {
        tagDialogUtils.show();
    }

    @Override
    public void onDeleteClick(int position) {
        mPresenter.clickDeleteTag(position);
    }

    @Override
    public void onClickItem(View v, int position) {
        mPresenter.doEditTag(position);
    }

    @Override
    public void updateTags(List<String> tags) {
        mAddTagAdapter.setTags(tags);
//        updateSaveStatus(tags.size() > 0);
        mAddTagAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateSearchHistory(List<String> strHistory) {
        mHistoryTagAdapter.updateSearchHistoryAdapter(strHistory);
        ivAcDeployDeviceTagDeleteHistoryTag.setVisibility(strHistory.size() > 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showDialogWithEdit(String text, int position) {
        tagDialogUtils.show(text, position);
    }

    @Override
    public void dismissDialog() {
        tagDialogUtils.dismissDialog();
    }

    @Override
    public void onItemClick(View view, int position) {
        mPresenter.addTags(position);
    }

    @Override
    public void onConfirm(int type, String text, int position) {
        switch (type) {
            case DIALOG_TAG_ADD:
                mPresenter.addTags(text);
                break;
            case DIALOG_TAG_EDIT:
                mPresenter.updateEditTag(position, text);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCancelClick() {
        if (historyClearDialog != null) {
            historyClearDialog.dismiss();
        }
    }

    @Override
    public void onConfirmClick(String content, String diameter) {
        mPresenter.clearHistoryTag();
        if (historyClearDialog != null) {
            historyClearDialog.dismiss();
        }

    }


}
