package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.AlarmContactHistoryAdapter;
import com.sensoro.smartcity.adapter.AlarmContactRcContentAdapter;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IAlarmContactActivityView;
import com.sensoro.smartcity.model.DeployContactModel;
import com.sensoro.smartcity.presenter.AlarmContactActivityPresenter;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.common.utils.SoftHideKeyBoardUtil;
import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.dialog.TipOperationDialogUtils;
import com.sensoro.common.widgets.SensoroToast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeployMonitorAlarmContactActivity extends BaseActivity<IAlarmContactActivityView, AlarmContactActivityPresenter>
        implements IAlarmContactActivityView, RecycleViewItemClickListener, TipOperationDialogUtils.TipDialogUtilsClickListener, AlarmContactRcContentAdapter.OnAlarmContactAdapterListener {


    @BindView(R.id.alarm_contact_tv_add)
    TextView alarmContactTvAdd;
    @BindView(R.id.include_text_title_tv_cancel)
    TextView includeTextTitleTvCancel;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.rc_ac_deploy_alarm_contact_history)
    RecyclerView rcAcDeployAlarmContactHistory;
    //    @BindView(R.id.ac_name_address_et_alarm_contact_name)
//    EditText acNameAddressEtAlarmContactName;
//    @BindView(R.id.ac_name_address_et_alarm_contact_phone)
//    EditText acNameAddressEtAlarmContactPhone;
    @BindView(R.id.iv_ac_name_address_delete_tag)
    ImageView ivAcDeployAlarmContactDeleteHistory;
    //    @BindView(R.id.ac_name_address_ll_add_name_phone)
//    LinearLayout acNameAddressLlAddNamePhone;
    @BindView(R.id.rc_add_alarm_contact)
    RecyclerView rcAddAlarmContactRv;
    @BindView(R.id.item_adapter_alarm_contact_add_ll)
    LinearLayout itemAdapterAlarmContactAddLl;
    private AlarmContactHistoryAdapter mHistoryAdapter;
    private AlarmContactRcContentAdapter mAlarmContactRcContentAdapter;
    private TipOperationDialogUtils historyClearDialog;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_alarm_contact);
        ButterKnife.bind(this);
        SoftHideKeyBoardUtil.assistActivity(this);
        initView();
        mPresenter.initData(mActivity);

    }

    private void initView() {
        //TODO 暂不支持多个联系人，所以先不做喽
        initRcContent();
        includeTextTitleTvTitle.setText(R.string.alert_contact);
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        initTitle();
        initRcHistory();
        initClearHistoryDialog();

        mAlarmContactRcContentAdapter.setOnAlarmContactAdapterListener(this);

    }

    private void initClearHistoryDialog() {
        historyClearDialog = new TipOperationDialogUtils(mActivity, true);
        historyClearDialog.setTipTitleText(getString(R.string.history_clear_all));
        historyClearDialog.setTipMessageText(getString(R.string.confirm_clear_history_record), R.color.c_a6a6a6);
        historyClearDialog.setTipCancelText(getString(R.string.cancel), getResources().getColor(R.color.c_1dbb99));
        historyClearDialog.setTipConfirmText(getString(R.string.clear), getResources().getColor(R.color.c_a6a6a6));
        historyClearDialog.setTipDialogUtilsClickListener(this);
    }


    private void initRcHistory() {
        mHistoryAdapter = new AlarmContactHistoryAdapter(mActivity);
        mHistoryAdapter.setRecycleViewItemClickListener(this);
        SensoroLinearLayoutManager manager = new SensoroLinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcAcDeployAlarmContactHistory.setLayoutManager(manager);
        rcAcDeployAlarmContactHistory.setAdapter(mHistoryAdapter);


        SensoroLinearLayoutManager contactManager = new SensoroLinearLayoutManager(mActivity);
        contactManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcAddAlarmContactRv.setLayoutManager(contactManager);
        rcAddAlarmContactRv.setAdapter(mAlarmContactRcContentAdapter);
    }

    private void initTitle() {
        includeTextTitleTvTitle.setText(R.string.alert_contact);
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


    private void initRcContent() {
        mAlarmContactRcContentAdapter = new AlarmContactRcContentAdapter(this);
    }

    @Override
    protected AlarmContactActivityPresenter createPresenter() {
        return new AlarmContactActivityPresenter();
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


    @OnClick({R.id.alarm_contact_tv_add, R.id.include_text_title_tv_cancel, R.id.include_text_title_tv_subtitle, R.id.iv_ac_name_address_delete_tag})
    public void onViewClicked(View view) {

        switch (view.getId()) {


            case R.id.alarm_contact_tv_add:
                mAlarmContactRcContentAdapter.addNewDataAdapter();

                break;


            case R.id.include_text_title_tv_cancel:
                AppUtils.dismissInputMethodManager(mActivity);
                finishAc();
                break;
            case R.id.include_text_title_tv_subtitle:
//                AppUtils.dismissInputMethodManager(mActivity, acNameAddressEtAlarmContactName);
//                String name = acNameAddressEtAlarmContactName.getText().toString();
//                String phone = acNameAddressEtAlarmContactPhone.getText().toString();


                mPresenter.doFinish(mAlarmContactRcContentAdapter.mList);
                break;
            case R.id.iv_ac_name_address_delete_tag:
                AppUtils.dismissInputMethodManager(mActivity);
                showHistoryClearDialog();
                break;
//            case R.id.ac_name_address_et_alarm_contact_name:
//                acNameAddressEtAlarmContactName.requestFocus();
//                acNameAddressEtAlarmContactName.setCursorVisible(true);
//                break;
//            case R.id.ac_name_address_et_alarm_contact_phone:
//                acNameAddressEtAlarmContactPhone.requestFocus();
//                acNameAddressEtAlarmContactPhone.setCursorVisible(true);
//                break;
        }
    }


    @Override
    public void updateContactData(ArrayList<DeployContactModel> mdContactModelList) {
        mAlarmContactRcContentAdapter.updateAdapter(mdContactModelList);


    }

    @Override
    public void updateHistoryData(ArrayList<String> mHistoryKeywords) {
        mHistoryAdapter.updateSearchHistoryAdapter(mHistoryKeywords);
        ivAcDeployAlarmContactDeleteHistory.setVisibility(mHistoryKeywords.size() > 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemClick(View view, int position) {
        String s = mHistoryAdapter.getSearchHistoryList().get(position);

        if (mAlarmContactRcContentAdapter.mFocusPos != -1) {
            DeployContactModel model = mAlarmContactRcContentAdapter.mList.get(mAlarmContactRcContentAdapter.mFocusPos);

            if (model.clickType == 1) {

                model.name = s;
            } else if (model.clickType == 2) {
                model.phone = s;

            }
            mAlarmContactRcContentAdapter.notifyItemChanged(mAlarmContactRcContentAdapter.mFocusPos);
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
        mPresenter.clearTag();
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


    @Override
    public void onPhoneFocusChange(boolean hasFocus) {

        mPresenter.updateStatus(1);
    }

    @Override
    public void onNameFocusChange(boolean hasFocus) {
        mPresenter.updateStatus(0);

    }
}
