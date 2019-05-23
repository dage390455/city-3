package com.sensoro.nameplate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.TipOperationDialogUtils;
import com.sensoro.nameplate.IMainViews.IEditNameplateDetailActivityView;
import com.sensoro.nameplate.R;
import com.sensoro.nameplate.R2;
import com.sensoro.nameplate.adapter.DeployDeviceTagAddTagAdapter;
import com.sensoro.nameplate.presenter.EditNameplateDetailActivityPresenter;
import com.sensoro.nameplate.widget.TagDialogUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class EditNameplateDetailActivity extends BaseActivity<IEditNameplateDetailActivityView, EditNameplateDetailActivityPresenter>
        implements IEditNameplateDetailActivityView, DeployDeviceTagAddTagAdapter.DeployDeviceTagAddTagItemClickListener, RecycleViewItemClickListener,
        TagDialogUtils.OnTagDialogListener, TipOperationDialogUtils.TipDialogUtilsClickListener {
    @BindView(R2.id.include_text_title_tv_cancel)
    TextView includeTextTitleTvCancel;
    @BindView(R2.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R2.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R2.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R2.id.et_edit_nameplate_detail)
    EditText etEditNameplateDetail;
    @BindView(R2.id.ll_edit_nameplate_detail)
    LinearLayout llEditNameplateDetail;
    @BindView(R2.id.rv_edit_nameplate_detail_add_tag)
    RecyclerView rvEditNameplateDetailAddTag;
    private DeployDeviceTagAddTagAdapter mAddTagAdapter;
    private TagDialogUtils tagDialogUtils;
    private TipOperationDialogUtils historyClearDialog;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_edit_nameplate_detail);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        tagDialogUtils = new TagDialogUtils(mActivity);
        tagDialogUtils.registerListener(this);
        initTitle();
        initRcAddTag();
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


    private void initRcAddTag() {
        mAddTagAdapter = new DeployDeviceTagAddTagAdapter(mActivity);
        SensoroLinearLayoutManager manager = new SensoroLinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvEditNameplateDetailAddTag.setLayoutManager(manager);
        rvEditNameplateDetailAddTag.setAdapter(mAddTagAdapter);
        mAddTagAdapter.setDeployDeviceTagAddTagItemClickListener(this);
    }

    @Override
    protected EditNameplateDetailActivityPresenter createPresenter() {
        return new EditNameplateDetailActivityPresenter();
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
    public void showDialogWithEdit(String text, int position) {
        tagDialogUtils.show(text, position);
    }

    @Override
    public void dismissDialog() {
        tagDialogUtils.dismissDialog();
    }

    @Override
    public void onItemClick(View view, int position) {
//        mPresenter.addTags(position);
    }

    @Override
    public void onConfirm(int type, String text, int position) {
        switch (type) {
            case TagDialogUtils.DIALOG_TAG_ADD:
                mPresenter.addTags(text);
                break;
            case TagDialogUtils.DIALOG_TAG_EDIT:
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
        if (historyClearDialog != null) {
            historyClearDialog.dismiss();
        }

    }


    @OnClick({R2.id.include_text_title_tv_cancel, R2.id.include_text_title_tv_subtitle, R2.id.et_edit_nameplate_detail})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (R.id.include_text_title_tv_cancel == id) {
            finishAc();
        } else if (R.id.include_text_title_tv_subtitle == id) {
            mPresenter.doFinish();
        } else if (R.id.et_edit_nameplate_detail == id) {
            historyClearDialog.show();
        } else {

        }
    }
}
