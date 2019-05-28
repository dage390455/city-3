package com.sensoro.nameplate.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.adapter.NameAddressHistoryAdapter;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.TipOperationDialogUtils;
import com.sensoro.nameplate.IMainViews.IDeployNameplateNameActivityView;
import com.sensoro.nameplate.R;
import com.sensoro.nameplate.R2;
import com.sensoro.nameplate.presenter.DeployNameplateNameActivityPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeployNameplateNameActivity extends BaseActivity<IDeployNameplateNameActivityView, DeployNameplateNameActivityPresenter>
        implements IDeployNameplateNameActivityView, RecycleViewItemClickListener, TipOperationDialogUtils.TipDialogUtilsClickListener {
    @BindView(R2.id.et_name_ac_deploy_nameplate_name)
    EditText acNameAddressEt;
    @BindView(R2.id.ll_edit_ac_deploy_nameplate_name)
    LinearLayout acNamAddressLl;
    @BindView(R2.id.tv_history_ac_deploy_nameplate_name)
    TextView acNamAddressTvHistory;
    @BindView(R2.id.rc_history_ac_deploy_nameplate_name)
    RecyclerView acNamAddressRcHistory;
    @BindView(R2.id.iv_delete_ac_deploy_nameplate_name)
    ImageView ivAcNamAddressDeleteHistory;
    @BindView(R2.id.include_text_title_tv_cancel)
    TextView includeTextTitleTvCancel;
    @BindView(R2.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R2.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    private NameAddressHistoryAdapter mHistoryAdapter;
    private ProgressUtils mProgressUtils;
    private TipOperationDialogUtils historyClearDialog;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deplaoy_nameplate_name);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        initTitle();
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

    private void initEtWatcher() {
        acNameAddressEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                mPresenter.checkCanSave(s.toString());
            }
        });
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

    private void initTitle() {
        includeTextTitleTvTitle.setText(R.string.name_address);
        includeTextTitleTvCancel.setVisibility(View.VISIBLE);
        includeTextTitleTvCancel.setTextColor(getResources().getColor(R.color.c_b6b6b6));
        includeTextTitleTvCancel.setText(R.string.cancel);
        includeTextTitleTvSubtitle.setVisibility(View.VISIBLE);
        includeTextTitleTvSubtitle.setText(getString(R.string.save));
        updateSaveStatus(true);
    }

    private void initRcHistory() {
        mHistoryAdapter = new NameAddressHistoryAdapter(mActivity);
        mHistoryAdapter.setRecycleViewItemClickListener(this);
        SensoroLinearLayoutManager manager = new SensoroLinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        acNamAddressRcHistory.setLayoutManager(manager);
        acNamAddressRcHistory.setAdapter(mHistoryAdapter);
        acNameAddressEt.requestFocus();
    }

    @Override
    protected DeployNameplateNameActivityPresenter createPresenter() {
        return new DeployNameplateNameActivityPresenter();
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


    @OnClick({R2.id.include_text_title_tv_subtitle, R2.id.include_text_title_tv_cancel, R2.id.iv_delete_ac_deploy_nameplate_name,
            R2.id.et_name_ac_deploy_nameplate_name})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.include_text_title_tv_subtitle) {
            AppUtils.dismissInputMethodManager(mActivity, acNameAddressEt);
            String text = acNameAddressEt.getText().toString().trim();
            mPresenter.doChoose(text);
        }else if(id == R.id.include_text_title_tv_cancel) {
            AppUtils.dismissInputMethodManager(mActivity, acNameAddressEt);
            finishAc();
        }else if(id == R.id.iv_delete_ac_deploy_nameplate_name) {
            showHistoryClearDialog();
        }else if(id == R.id.et_name_ac_deploy_nameplate_name) {
            acNameAddressEt.setCursorVisible(true);
            acNameAddressEt.requestFocus();
        }

    }


    @Override
    public void setEditText(String text) {
        acNameAddressEt.setText(text);
        acNameAddressEt.setSelection(text.length());
    }

    @Override
    public void updateSearchHistoryData(List<String> searchStr) {
        mHistoryAdapter.updateSearchHistoryAdapter(searchStr);
        ivAcNamAddressDeleteHistory.setVisibility(searchStr.size() > 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateTvTitle(String sn) {
        includeTextTitleTvTitle.setText(sn);
    }

    @Override
    public void onItemClick(View view, int position) {
        String text = mHistoryAdapter.getSearchHistoryList().get(position).trim();
        setEditText(text);
        acNameAddressEt.clearFocus();
        dismissInputMethodManager(view);
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
    protected void onDestroy() {
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }

        if (historyClearDialog != null) {
            historyClearDialog.destroy();
            historyClearDialog = null;
        }
        super.onDestroy();
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
}
