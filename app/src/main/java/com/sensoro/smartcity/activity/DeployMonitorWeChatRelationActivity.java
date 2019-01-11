package com.sensoro.smartcity.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.NameAddressHistoryAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeployMonitorWeChatRelationActivityView;
import com.sensoro.smartcity.presenter.DeployMonitorWeChatRelationActivityPresenter;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeployMonitorWeChatRelationActivity extends BaseActivity<IDeployMonitorWeChatRelationActivityView, DeployMonitorWeChatRelationActivityPresenter>
        implements IDeployMonitorWeChatRelationActivityView, RecycleViewItemClickListener {
    @BindView(R.id.ac_we_chat_relation_et)
    EditText acWeChatRelationEt;
    @BindView(R.id.ac_chat_relation_ll)
    LinearLayout acWeChatRelationLl;
    @BindView(R.id.ac_chat_relation_tv_history)
    TextView acWeChatRelationTvHistory;
    @BindView(R.id.ac_chat_relation_rc_history)
    RecyclerView acWeChatRelationRcHistory;
    @BindView(R.id.ac_chat_relation_tv_save)
    TextView acWeChatRelationTvSave;
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    private NameAddressHistoryAdapter mHistoryAdapter;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deploy_we_chat_relation);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        includeTextTitleTvTitle.setText(R.string.we_chat_relation);
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        initRcHistory();
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
        SensoroToast.INSTANCE.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }


    @OnClick({R.id.ac_chat_relation_tv_save, R.id.include_text_title_imv_arrows_left})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ac_chat_relation_tv_save:
                String text = acWeChatRelationEt.getText().toString();
                mPresenter.doChoose(text);
                break;
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
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
}
