package com.sensoro.smartcity.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.common.adapter.NameAddressHistoryAdapter;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeployMonitorWeChatRelationActivityView;
import com.sensoro.smartcity.presenter.DeployMonitorWeChatRelationActivityPresenter;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.common.widgets.TipOperationDialogUtils;
import com.sensoro.common.widgets.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeployMonitorWeChatRelationActivity extends BaseActivity<IDeployMonitorWeChatRelationActivityView, DeployMonitorWeChatRelationActivityPresenter>
        implements IDeployMonitorWeChatRelationActivityView, RecycleViewItemClickListener, TipOperationDialogUtils.TipDialogUtilsClickListener {
    @BindView(R.id.ac_we_chat_relation_et)
    EditText acWeChatRelationEt;
    @BindView(R.id.ac_chat_relation_ll)
    LinearLayout acWeChatRelationLl;
    @BindView(R.id.ac_chat_relation_tv_history)
    TextView acWeChatRelationTvHistory;
    @BindView(R.id.ac_chat_relation_rc_history)
    RecyclerView acWeChatRelationRcHistory;
    @BindView(R.id.iv_ac_chat_relation_delete_history)
    ImageView ivAcWeChatRelationDeleteHistory;
    @BindView(R.id.include_text_title_tv_cancel)
    TextView includeTextTitleTvCancel;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.tv_ac_chat_relation_qr_code_desc)
    TextView tvAcChatRelationQrCodeDesc;
    private NameAddressHistoryAdapter mHistoryAdapter;
    private TipOperationDialogUtils historyClearDialog;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deploy_we_chat_relation);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        initTitle();
        initMiniProgramDesc();
        initRcHistory();
        initClearHistoryDialog();
//        initEtWatcher();
    }

    private void initClearHistoryDialog() {
        historyClearDialog = new TipOperationDialogUtils(mActivity, true);
        historyClearDialog.setTipTitleText(getString(R.string.history_clear_all));
        historyClearDialog.setTipMessageText(getString(R.string.confirm_clear_history_record), R.color.c_a6a6a6);
        historyClearDialog.setTipCancelText(getString(R.string.cancel), getResources().getColor(R.color.c_1dbb99));
        historyClearDialog.setTipConfirmText(getString(R.string.clear), getResources().getColor(R.color.c_a6a6a6));
        historyClearDialog.setTipDialogUtilsClickListener(this);
    }

    private void initMiniProgramDesc() {
        String desc = getString(R.string.mini_program_description);
        String temp = "SensoroCity";
        int start = desc.indexOf(temp);
        if (start == -1) {
            tvAcChatRelationQrCodeDesc.setText(desc);
            return;
        }
        SpannableString spannableString = new SpannableString(desc);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.c_252525));
        spannableString.setSpan(foregroundColorSpan, start, start + temp.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        tvAcChatRelationQrCodeDesc.setText(spannableString);

    }

    private void initEtWatcher() {
        acWeChatRelationEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.checkCanSave(s.toString());
            }
        });
    }

    private void initTitle() {
        includeTextTitleTvTitle.setText(R.string.we_chat_relation);
        includeTextTitleTvCancel.setVisibility(View.VISIBLE);
        includeTextTitleTvCancel.setTextColor(getResources().getColor(R.color.c_b6b6b6));
        includeTextTitleTvCancel.setText(R.string.cancel);
        includeTextTitleTvSubtitle.setVisibility(View.VISIBLE);
        includeTextTitleTvSubtitle.setText(getString(R.string.save));
        updateSaveStatus(true);
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

    private void initRcHistory() {
        mHistoryAdapter = new NameAddressHistoryAdapter(mActivity);
        mHistoryAdapter.setRecycleViewItemClickListener(this);
        SensoroLinearLayoutManager manager = new SensoroLinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        acWeChatRelationRcHistory.setLayoutManager(manager);
        acWeChatRelationRcHistory.setAdapter(mHistoryAdapter);
        acWeChatRelationEt.requestFocus();
    }

    @Override
    protected DeployMonitorWeChatRelationActivityPresenter createPresenter() {
        return new DeployMonitorWeChatRelationActivityPresenter();
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

    private void dismissInputMethodManager(View view) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//从控件所在的窗口中隐藏
        }
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }

    @OnClick({R.id.include_text_title_tv_subtitle, R.id.include_text_title_tv_cancel, R.id.iv_ac_chat_relation_delete_history, R.id.ac_we_chat_relation_et})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_tv_subtitle:
                AppUtils.dismissInputMethodManager(mActivity, acWeChatRelationEt);
                String text = acWeChatRelationEt.getText().toString();
                mPresenter.doChoose(text);
                break;
            case R.id.include_text_title_tv_cancel:
                AppUtils.dismissInputMethodManager(mActivity, acWeChatRelationEt);
                finishAc();
                break;
            case R.id.iv_ac_chat_relation_delete_history:
                showHistoryClearDialog();
                break;
            case R.id.ac_we_chat_relation_et:
                acWeChatRelationEt.requestFocus();
                acWeChatRelationEt.setCursorVisible(true);
                break;
        }

    }

    @Override
    public void setEditText(String text) {
        acWeChatRelationEt.setText(text);
        acWeChatRelationEt.setSelection(text.length());
    }

    @Override
    public void updateSearchHistoryData(List<String> searchStr) {
        mHistoryAdapter.updateSearchHistoryAdapter(searchStr);
        ivAcWeChatRelationDeleteHistory.setVisibility(searchStr.size() > 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateTvTitle(String sn) {
        includeTextTitleTvTitle.setText(sn);
    }

    @Override
    public void onItemClick(View view, int position) {
        String text = mHistoryAdapter.getSearchHistoryList().get(position).trim();
        setEditText(text);
        acWeChatRelationEt.clearFocus();
        dismissInputMethodManager(view);
    }

    @Override
    public void onCancelClick() {
        if (historyClearDialog != null) {
            historyClearDialog.dismiss();

        }
    }

    @Override
    public void onConfirmClick(String content, String diameter) {
        mPresenter.clearHistory();
        if (historyClearDialog != null) {
            historyClearDialog.dismiss();

        }
    }

    @Override
    protected void onDestroy() {
        if (historyClearDialog != null) {
            historyClearDialog.destroy();
            historyClearDialog = null;
        }
        super.onDestroy();
    }
}
