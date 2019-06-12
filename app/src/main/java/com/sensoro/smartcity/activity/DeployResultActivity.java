package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.imainviews.IDeployResultActivityView;
import com.sensoro.smartcity.presenter.DeployResultActivityPresenter;
import com.sensoro.common.utils.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sensoro on 17/8/4.
 */
@Route(path = ARouterConstants.ACTIVITY_DEPLOYRESULT)

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
    @BindView(R.id.rl_ac_deploy_resul_sn)
    RelativeLayout acDeployResultRlSn;
    @BindView(R.id.ac_deploy_result_tv_name)
    TextView acDeployResultTvName;
    @BindView(R.id.rl_ac_deploy_result_name)
    RelativeLayout acDeployResultRlName;
    @BindView(R.id.ac_deploy_result_tv_address)
    TextView acDeployResultTvAddress;
    @BindView(R.id.rl_ac_deploy_result_address)
    RelativeLayout acDeployResultRlAddress;
    @BindView(R.id.ac_deploy_result_ll_content)
    LinearLayout acDeployResultLlContent;
    @BindView(R.id.ac_deploy_result_tv_back_home)
    TextView acDeployResultTvBackHome;
    @BindView(R.id.ac_deploy_result_tv_continue)
    TextView acDeployResultTvContinue;
    @BindView(R.id.rl_ac_deploy_result_contact)
    RelativeLayout acDeployResultTvRlContact;
    @BindView(R.id.ac_deploy_result_tv_contact)
    TextView acDeployResultTvContact;
    @BindView(R.id.rl_ac_deploy_result_we_chat)
    RelativeLayout acDeployResultRlWeChat;
    @BindView(R.id.ac_deploy_result_tv_we_chat)
    TextView acDeployResultTvWeChat;
    @BindView(R.id.ac_deploy_result_tv_signal)
    TextView acDeployResultTvSignal;
    @BindView(R.id.rl_ac_deploy_result_status)
    RelativeLayout acDeployResultRlStatus;
    @BindView(R.id.ac_deploy_result_tv_status)
    TextView acDeployResultTvStatus;
    @BindView(R.id.rl_ac_deploy_result_recent_upload_time)
    RelativeLayout acDeployResultRlRecentUploadTime;
    @BindView(R.id.ac_deploy_result_tv_recent_upload_time)
    TextView acDeployResultTvRecentUploadTime;
    @BindView(R.id.ac_deploy_result_title)
    TextView acDeployResultTitle;
    @BindView(R.id.ac_deploy_result_line)
    View acDeployResultLine;
    @BindView(R.id.view_ac_deploy_result_divider)
    View viewAcDeployResultDivider;
    @BindView(R.id.ac_deploy_result_bottom)
    LinearLayout acDeployResultBottom;
//    @BindView(R.id.ac_deploy_result_tv_error_msg)
//    TextView acDeployResultTvErrorMsg;
    @BindView(R.id.rl_ac_deploy_result_setting)
    RelativeLayout acDeployResultRlSetting;
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
            acDeployResultRlWeChat.setVisibility(View.GONE);
        }
    }


    @Override
    protected DeployResultActivityPresenter createPresenter() {
        return new DeployResultActivityPresenter();
    }

    @Override
    public void refreshSignal(long updateTime, String signal) {
        String signal_text = null;
        Drawable drawable = null;
        Resources resources = mActivity.getResources();
        long time_diff = System.currentTimeMillis() - updateTime;
        if (signal != null && (time_diff < 2 * 60 * 1000)) {
            switch (signal) {
                case "good":
                    signal_text = mActivity.getString(R.string.s_good);
                    drawable = resources.getDrawable(R.drawable.signal_good);
                    break;
                case "normal":
                    signal_text = mActivity.getString(R.string.s_normal);
                    drawable = resources.getDrawable(R.drawable.signal_normal);
                    break;
                case "bad":
                    signal_text = mActivity.getString(R.string.s_bad);
                    drawable = resources.getDrawable(R.drawable.signal_bad);
                    break;
                default:
                    signal_text = mActivity.getString(R.string.s_bad);
                    drawable = resources.getDrawable(R.drawable.signal_bad);
                    break;
            }
        } else {
            signal_text = mActivity.getString(R.string.s_none);
            drawable = resources.getDrawable(R.drawable.signal_none);
        }

        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        acDeployResultTvSignal.setText(signal_text);
        acDeployResultTvSignal.setCompoundDrawables(drawable,null,null,null);
    }

    @Override
    public void setResultImageView(int resId) {
        acDeployResultImvIcon.setImageResource(resId);
    }

    @Override
    public void setTipsTextView(String text,int resId) {
        acDeployResultTvStateMsg.setTextColor(mActivity.getResources().getColor(resId));
        acDeployResultTvStateMsg.setText(text);
    }

    @Override
    public void setSnTextView(String sn) {
        acDeployResultTvSn.setText(sn);
    }

    @Override
    public void setNameTextView(String name) {
        acDeployResultRlName.setVisibility(View.VISIBLE);
        acDeployResultTvName.setText(name);
    }


    @Override
    public void setContactTextView(String content) {
        acDeployResultTvRlContact.setVisibility(View.VISIBLE);
        acDeployResultTvContact.setText(content);
    }

    @Override
    public void setWeChatTextView(String content) {
        if (AppUtils.isChineseLanguage()) {
            acDeployResultRlWeChat.setVisibility(View.VISIBLE);
            acDeployResultTvWeChat.setText(content);
        }

    }

    @Override
    public void setStatusTextView(String status, int color) {
        acDeployResultRlStatus.setVisibility(View.VISIBLE);
        if (viewAcDeployResultDivider.getVisibility() != View.VISIBLE) {
            viewAcDeployResultDivider.setVisibility(View.VISIBLE);
        }
        acDeployResultTvStatus.setText(status);
        acDeployResultTvStatus.setTextColor(color);
    }

    @Override
    public void setUpdateTextView(String update) {
        acDeployResultRlRecentUploadTime.setVisibility(View.VISIBLE);
        if (viewAcDeployResultDivider.getVisibility() != View.VISIBLE) {
            viewAcDeployResultDivider.setVisibility(View.VISIBLE);
        }
        acDeployResultTvRecentUploadTime.setText(update);
    }

    @Override
    public void setAddressTextView(String address) {
        acDeployResultRlAddress.setVisibility(View.VISIBLE);
        if (viewAcDeployResultDivider.getVisibility() != View.VISIBLE) {
            viewAcDeployResultDivider.setVisibility(View.VISIBLE);
        }
        acDeployResultTvAddress.setText(address);
    }

    @Override
    public void setUpdateTextViewVisible(boolean isVisible) {
        acDeployResultRlRecentUploadTime.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        if (isVisible && viewAcDeployResultDivider.getVisibility() != View.VISIBLE) {
            viewAcDeployResultDivider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setContactAndSignalVisible(boolean isVisible) {
//        contactTextView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        acDeployResultTvRlContact.setVisibility(isVisible ? View.VISIBLE : View.GONE);
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
        acDeployResultTvState.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void setResultSettingVisible(boolean isVisible) {
        acDeployResultRlSetting.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        if (viewAcDeployResultDivider.getVisibility() != View.VISIBLE) {
            viewAcDeployResultDivider.setVisibility(View.VISIBLE);
        }
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
    public void setDeployResultTvStateTextColor(int resColor) {
        acDeployResultTvState.setTextColor(mActivity.getResources().getColor(resColor));
    }

    @Override
    public void setDeployResultDividerVisible(boolean isVisible) {
        viewAcDeployResultDivider.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setDeployResultContinueTextBackground(Drawable drawable) {
        acDeployResultTvContinue.setBackground(drawable);
    }


    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
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


    @OnClick({R.id.ac_deploy_result_tv_back_home, R.id.ac_deploy_result_tv_continue})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ac_deploy_result_tv_back_home:
                mPresenter.backHome();
                break;
            case R.id.ac_deploy_result_tv_continue:
                mPresenter.gotoContinue();
                break;
        }
    }

}
