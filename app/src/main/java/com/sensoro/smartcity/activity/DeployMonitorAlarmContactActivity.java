package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.AlarmContactHistoryAdapter;
import com.sensoro.smartcity.adapter.AlarmContactRcContentAdapter;
import com.sensoro.smartcity.adapter.DeployDeviceTagHistoryTagAdapter;
import com.sensoro.smartcity.adapter.ImagePickerAdapter;
import com.sensoro.smartcity.adapter.NameAddressHistoryAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IAlarmContactActivityView;
import com.sensoro.smartcity.presenter.AlarmContactActivityPresenter;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeployMonitorAlarmContactActivity extends BaseActivity<IAlarmContactActivityView, AlarmContactActivityPresenter>
        implements IAlarmContactActivityView ,RecycleViewItemClickListener {


    @BindView(R.id.include_text_title_tv_cancel)
    TextView includeTextTitleTvCancel;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.rc_ac_deploy_alarm_contact_history)
    RecyclerView rcAcDeployAlarmContactHistory;
    @BindView(R.id.ac_name_address_et_alarm_contact_name)
    EditText acNameAddressEtAlarmContactName;
    @BindView(R.id.ac_name_address_et_alarm_contact_phone)
    EditText acNameAddressEtAlarmContactPhone;
    @BindView(R.id.iv_ac_name_address_delete_tag)
    ImageView ivAcDeployAlarmContactDeleteHistory;
    @BindView(R.id.ac_name_address_ll_add_name_phone)
    LinearLayout acNameAddressLlAddNamePhone;
    private AlarmContactHistoryAdapter mHistoryAdapter;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_alarm_contact);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);

    }

    private void initView() {
        //TODO 暂不支持多个联系人，所以先不做喽
//        initRcContent();
        includeTextTitleTvTitle.setText(R.string.alert_contact);
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        initTitle();
        initRcHistory();
//        initEtWatcher();

    }

    private void initEtWatcher() {
        acNameAddressEtAlarmContactName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.checkCanSave(s.toString(),acNameAddressEtAlarmContactPhone.getText().toString());
            }
        });

        acNameAddressEtAlarmContactPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.checkCanSave(acNameAddressEtAlarmContactName.getText().toString(),s.toString());
            }
        });
    }

    private void initRcHistory() {
        mHistoryAdapter = new AlarmContactHistoryAdapter(mActivity);
        mHistoryAdapter.setRecycleViewItemClickListener(this);
        SensoroLinearLayoutManager manager = new SensoroLinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcAcDeployAlarmContactHistory.setLayoutManager(manager);
        rcAcDeployAlarmContactHistory.setAdapter(mHistoryAdapter);
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
        includeTextTitleTvSubtitle.setTextColor(isEnable ? getResources().getColor(R.color.c_29c093) : getResources().getColor(R.color.c_dfdfdf));

    }

    private void initRcContent() {
        AlarmContactRcContentAdapter alarmContactRcContentAdapter = new AlarmContactRcContentAdapter();
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
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }


    @OnClick({R.id.include_text_title_tv_cancel, R.id.include_text_title_tv_subtitle,R.id.iv_ac_name_address_delete_tag})
    public void onViewClicked(View view) {
        AppUtils.dismissInputMethodManager(mActivity,acNameAddressEtAlarmContactName);
        switch (view.getId()) {
            case R.id.include_text_title_tv_cancel:
                finishAc();
                break;
            case R.id.include_text_title_tv_subtitle:
                String name = acNameAddressEtAlarmContactName.getText().toString();
                String phone = acNameAddressEtAlarmContactPhone.getText().toString();
                mPresenter.doFinish(name,phone);
                break;
            case R.id.iv_ac_name_address_delete_tag:
                mPresenter.clearTag();
                break;
        }
    }


    @Override
    public void setNameAndPhone(String name, String phone) {
        acNameAddressEtAlarmContactName.setText(name);
        acNameAddressEtAlarmContactName.setSelection(name.length());
        acNameAddressEtAlarmContactPhone.setText(phone);
        acNameAddressEtAlarmContactPhone.setSelection(phone.length());
    }

    @Override
    public void updateHistoryData(ArrayList<String> mHistoryKeywords) {
        mHistoryAdapter.updateSearchHistoryAdapter(mHistoryKeywords);
        ivAcDeployAlarmContactDeleteHistory.setVisibility(mHistoryKeywords.size() > 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemClick(View view, int position) {
        String s = mHistoryAdapter.getSearchHistoryList().get(position);
        String[] split = s.split("#");
        setNameAndPhone(split[0],split[1]);
    }
}
