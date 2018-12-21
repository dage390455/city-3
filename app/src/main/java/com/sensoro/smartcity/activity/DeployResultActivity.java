package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeployResultActivityView;
import com.sensoro.smartcity.presenter.DeployResultActivityPresenter;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sensoro on 17/8/4.
 */

public class DeployResultActivity extends BaseActivity<IDeployResultActivityView, DeployResultActivityPresenter>
        implements IDeployResultActivityView {


    @BindView(R.id.ac_deploy_result_imv_icon)
    ImageView acDeployResultImvIcon;
    @BindView(R.id.ac_deploy_result_tv_state)
    TextView acDeployResultTvState;
    @BindView(R.id.ac_deploy_result_tv_state_msg)
    TextView acDeployResultTvStateMsg;
    @BindView(R.id.ac_deploy_result_tv_sn)
    TextView acDeployResultTvSn;
    @BindView(R.id.ac_deploy_result_ll_sn)
    LinearLayout acDeployResultLlSn;
    @BindView(R.id.ac_deploy_result_tv_name)
    TextView acDeployResultTvName;
    @BindView(R.id.ac_deploy_result_ll_name)
    LinearLayout acDeployResultLlName;
    @BindView(R.id.ac_deploy_result_tv_address)
    TextView acDeployResultTvAddress;
    @BindView(R.id.ac_deploy_result_ll_address)
    LinearLayout acDeployResultLlAddress;
    @BindView(R.id.ac_deploy_result_ll_content)
    LinearLayout acDeployResultLlContent;
    @BindView(R.id.ac_deploy_result_tv_back_home)
    TextView acDeployResultTvBackHome;
    @BindView(R.id.ac_deploy_result_tv_continue)
    TextView acDeployResultTvContinue;
    @BindView(R.id.ac_deploy_result_ll_contact)
    LinearLayout acDeployResultTvLlContact;
    @BindView(R.id.ac_deploy_result_tv_contact)
    TextView acDeployResultTvContact;
    @BindView(R.id.ac_deploy_result_ll_we_chat)
    LinearLayout acDeployResultLlWeChat;
    @BindView(R.id.ac_deploy_result_tv_we_chat)
    TextView acDeployResultTvWeChat;
    @BindView(R.id.ac_deploy_result_tv_signal)
    TextView acDeployResultTvSignal;
    @BindView(R.id.ac_deploy_result_ll_status)
    LinearLayout acDeployResultLlStatus;
    @BindView(R.id.ac_deploy_result_tv_status)
    TextView acDeployResultTvStatus;
    @BindView(R.id.ac_deploy_result_ll_recent_upload_time)
    LinearLayout acDeployResultLlRecentUploadTime;
    @BindView(R.id.ac_deploy_result_tv_recent_upload_time)
    TextView acDeployResultTvRecentUploadTime;
    @BindView(R.id.ac_deploy_result_imv_arrows_left)
    ImageView acDeployResultImvArrowsLeft;
    @BindView(R.id.ac_deploy_result_title)
    TextView acDeployResultTitle;
    @BindView(R.id.ac_deploy_result_ll_title)
    LinearLayout acDeployResultLlTitle;
    @BindView(R.id.ac_deploy_result_line)
    View acDeployResultLine;
    @BindView(R.id.ac_deploy_result_bottom)
    LinearLayout acDeployResultBottom;
    @BindView(R.id.ac_deploy_result_tv_error_msg)
    TextView acDeployResultTvErrorMsg;
    @BindView(R.id.ac_deploy_result_ll_setting)
    LinearLayout acDeployResultLlSetting;
    @BindView(R.id.ac_deploy_result_tv_setting)
    TextView acDeployResultTvSetting;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deploy_result_test);
        ButterKnife.bind(mActivity);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        if (!AppUtils.isChineseLanguage()) {
            acDeployResultLlWeChat.setVisibility(View.GONE);
        }
    }


    @Override
    protected DeployResultActivityPresenter createPresenter() {
        return new DeployResultActivityPresenter();
    }

    @Override
    public void refreshSignal(long updateTime, String signal) {
        String signal_text = null;
        long time_diff = System.currentTimeMillis() - updateTime;
        if (signal != null && (time_diff < 2 * 60 * 1000)) {
            switch (signal) {
                case "good":
                    signal_text = mActivity.getString(R.string.signal_excellent);
                    acDeployResultTvSignal.setBackground(getResources().getDrawable(R.drawable.shape_signal_good));
                    break;
                case "normal":
                    signal_text = mActivity.getString(R.string.signal_good);
                    acDeployResultTvSignal.setBackground(getResources().getDrawable(R.drawable.shape_signal_normal));
                    break;
                case "bad":
                    signal_text = mActivity.getString(R.string.signal_weak);
                    acDeployResultTvSignal.setBackground(getResources().getDrawable(R.drawable.shape_signal_bad));
                    break;
            }
        } else {
            signal_text = mActivity.getString(R.string.no_signal);
            acDeployResultTvSignal.setBackground(getResources().getDrawable(R.drawable.shape_signal_none));
        }
        acDeployResultTvSignal.setText(signal_text);
    }

    @Override
    public void setResultImageView(int resId) {
        acDeployResultImvIcon.setImageResource(resId);
    }

    @Override
    public void setTipsTextView(String text) {
        acDeployResultTvStateMsg.setText(text);
    }

    @Override
    public void setSnTextView(String sn) {
        acDeployResultTvSn.setText(sn);
    }

    @Override
    public void setNameTextView(String name) {
        acDeployResultLlName.setVisibility(View.VISIBLE);
        acDeployResultTvName.setText(name);
    }


    @Override
    public void setContactTextView(String content) {
        acDeployResultTvLlContact.setVisibility(View.VISIBLE);
        acDeployResultTvContact.setText(content);
    }

    @Override
    public void setWeChatTextView(String content) {
        if (AppUtils.isChineseLanguage()) {
            acDeployResultLlWeChat.setVisibility(View.VISIBLE);
            acDeployResultTvWeChat.setText(content);
        }

    }

    @Override
    public void setStatusTextView(String status) {
        acDeployResultLlStatus.setVisibility(View.VISIBLE);
        acDeployResultTvStatus.setText(status);
    }

    @Override
    public void setUpdateTextView(String update) {
        acDeployResultLlRecentUploadTime.setVisibility(View.VISIBLE);
        acDeployResultTvRecentUploadTime.setText(update);
    }

    @Override
    public void setAddressTextView(String address) {
        acDeployResultLlAddress.setVisibility(View.VISIBLE);
        acDeployResultTvAddress.setText(address);
    }

    @Override
    public void setDeployResultErrorInfo(String errorInfo) {
        acDeployResultTvStateMsg.setVisibility(View.VISIBLE);
        acDeployResultTvStateMsg.setText(errorInfo);
    }

    @Override
    public void setUpdateTextViewVisible(boolean isVisible) {
        acDeployResultLlRecentUploadTime.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setContactAndSignalVisible(boolean isVisible) {
//        contactTextView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        acDeployResultTvLlContact.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        acDeployResultTvSignal.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setStateTextView(String msg) {
        acDeployResultTvState.setText(msg);
    }

    @Override
    public void setDeployResultContinueText(String text) {
        acDeployResultTvContinue.setText(text);
    }

    @Override
    public void setDeployResultBackHomeText(String text) {
        acDeployResultTvBackHome.setText(text);
    }

    @Override
    public void setDeployResultContinueVisible(boolean isVisible) {
        acDeployResultTvContinue.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setStateTextViewVisible(boolean isVisible) {
        acDeployResultTvState.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setResultSettingVisible(boolean isVisible) {
        acDeployResultLlSetting.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setArrowsLeftVisible(boolean isVisible) {
        acDeployResultImvArrowsLeft.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setTitleText(String text) {
        acDeployResultTitle.setText(text);
    }

    @Override
    public void setDeployResultHasSetting(String setting) {
        acDeployResultTvSetting.setText(setting);
    }


    @Override
    public void toastShort(String msg) {
        SensoroToast.INSTANCE.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

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


    @OnClick({R.id.ac_deploy_result_tv_back_home, R.id.ac_deploy_result_tv_continue, R.id.ac_deploy_result_imv_arrows_left})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ac_deploy_result_tv_back_home:
                mPresenter.backHome();
                break;
            case R.id.ac_deploy_result_tv_continue:
                mPresenter.gotoContinue();
                break;
            case R.id.ac_deploy_result_imv_arrows_left:
                finishAc();
                break;
        }
    }

}
