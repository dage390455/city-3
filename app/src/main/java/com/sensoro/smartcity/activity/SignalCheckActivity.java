package com.sensoro.smartcity.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.SignalCheckContentAdapter;
import com.sensoro.smartcity.adapter.TagAdapter;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.smartcity.imainviews.ISignalCheckActivityView;
import com.sensoro.smartcity.model.SignalData;
import com.sensoro.smartcity.presenter.SignalCheckActivityPresenter;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.SpacesItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignalCheckActivity extends BaseActivity<ISignalCheckActivityView, SignalCheckActivityPresenter> implements
        ISignalCheckActivityView {
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.ac_signal_check_tv_sn)
    TextView acSignalCheckTvSn;
    @BindView(R.id.ac_signal_check_tv_state)
    TextView acSignalCheckTvState;
    @BindView(R.id.ac_signal_check_tv_near)
    TextView acSignalCheckTvNear;
    @BindView(R.id.ac_signal_check_tv_time)
    TextView acSignalCheckTvTime;
    @BindView(R.id.ac_signal_check_tv_type_and_name)
    TextView acSignalCheckTvTypeAndName;
    @BindView(R.id.ac_signal_check_tv_signal_status)
    TextView acSignalCheckTvSignalStatus;
    @BindView(R.id.ac_signal_check_tv_rc_tag)
    RecyclerView acSignalCheckTvRcTag;
    @BindView(R.id.ac_signal_check_ll_name)
    LinearLayout acSignalCheckLlName;
    @BindView(R.id.ac_signal_check_imv_start_or_stop)
    ImageView acSignalCheckImvStartOrStop;
    @BindView(R.id.ac_signal_check_tv_rc_content)
    RecyclerView acSignalCheckTvRcContent;
    @BindView(R.id.ac_signal_check_ll_detail)
    LinearLayout acSignalCheckLlDetail;
    @BindView(R.id.ac_signal_check_ll_test)
    LinearLayout acSignalCheckLlTest;
    private TagAdapter mTagAdapter;
    private SignalCheckContentAdapter mContentAdapter;
    private int btnResId = R.drawable.signal_check_start_btn;
    private ProgressUtils mProgressUtils;
    private AlertDialog mSignalChoiceDialog;
    private ProgressUtils.Builder mProgressbuild;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_signal_check);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        mProgressbuild = new ProgressUtils.Builder(mActivity);
        mProgressUtils = new ProgressUtils(mProgressbuild.setCancelable(false).build());
        includeTextTitleTvTitle.setText(R.string.signal_test);
        includeTextTitleTvSubtitle.setText(R.string.frequency_random);

        initRcTag();

        initRcContent();

        initSingleChoiceDialog();
    }

    private void initSingleChoiceDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                .setTitle(R.string.setting)
                .setSingleChoiceItems(mPresenter.getLoraBandText(mActivity), 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton(R.string.determine, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        mSignalChoiceDialog = builder.create();
    }

    private void initRcContent() {
        mContentAdapter = new SignalCheckContentAdapter(mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        acSignalCheckTvRcContent.setLayoutManager(manager);
        acSignalCheckTvRcContent.setAdapter(mContentAdapter);
    }

    private void initRcTag() {
        mTagAdapter = new TagAdapter(mActivity, R.color.c_252525, R.color.c_dfdfdf);
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        acSignalCheckTvRcTag.setLayoutManager(layoutManager);
        int spacingInPixels = mActivity.getResources().getDimensionPixelSize(R.dimen.x10);
        acSignalCheckTvRcTag.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
        acSignalCheckTvRcTag.setAdapter(mTagAdapter);
    }

    @Override
    protected SignalCheckActivityPresenter createPresenter() {
        return new SignalCheckActivityPresenter();
    }

    @Override
    public void startAC(Intent intent) {
        mActivity.startActivity(intent);
    }

    @Override
    public void finishAc() {
        mActivity.finish();
    }

    @Override
    protected void onStart() {
        mPresenter.onStart();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
        }
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
        if (mProgressUtils != null) {
            mProgressUtils.showProgress();
        }
    }

    @Override
    public void dismissProgressDialog() {
        if (mProgressUtils != null) {
            mProgressUtils.dismissProgress();
        }
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_LONG).show();
    }


    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.include_text_title_tv_subtitle, R.id.ac_signal_check_imv_start_or_stop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;
            case R.id.include_text_title_tv_subtitle:
                mSignalChoiceDialog.show();
                break;
            case R.id.ac_signal_check_imv_start_or_stop:
                mPresenter.doStartOrStop();
                break;
        }
    }

    @Override
    public void setSnText(String sn) {
        acSignalCheckTvSn.setText(sn);
    }

    @Override
    public void setStatus(String statusText, int textColor) {
//        acSignalCheckTvState.setText(statusText);
//        acSignalCheckTvState.setTextColor(textColor);
        WidgetUtil.changeTvState(mActivity, acSignalCheckTvState, textColor, statusText);
    }

    @Override
    public void setUpdateTime(String time) {
        acSignalCheckTvTime.setText(time);
    }

    @Override
    public void setTypeAndName(String text) {
        acSignalCheckTvTypeAndName.setText(text);
    }

    @Override
    public void updateTag(List<String> tags) {
        if (tags.size() > 0) {
            mTagAdapter.updateTags(tags);
        } else {
            acSignalCheckTvRcTag.setVisibility(View.GONE);
        }
    }

    @Override
    public void setStartBtnIcon(int resId) {
        btnResId = resId;
        acSignalCheckImvStartOrStop.setImageResource(resId);
    }

    @Override
    public void updateProgressDialogMessage(final String content) {
        mProgressUtils.updateMessage(content);
    }

    @Override
    public boolean getIsStartSignalCheck() {
        return btnResId == R.drawable.signal_check_start_btn;
    }

    @Override
    public void setSubTitleVisible(boolean isVisible) {
        //暂时不需要频点随机，以后需要不需要不知道，所以注释先留着
//        includeTextTitleTvSubtitle.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateStatusText(String text) {
        acSignalCheckTvState.setText(text);
    }

    @Override
    public void updateContentAdapter(SignalData signalData) {
        mContentAdapter.updateData(signalData);
//        acSignalCheckTvRcContent.smoothScrollToPosition(mContentAdapter.getLastPosition());
    }

    @Override
    public void setLlTestVisible(boolean isVisible) {
        acSignalCheckLlTest.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setLlDetailVisible(boolean isVisible) {
        acSignalCheckLlDetail.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateSignalStatusText(String text) {
        acSignalCheckTvSignalStatus.setText(text);
    }

    @Override
    public void setNearVisible(boolean isVisible) {
        acSignalCheckTvNear.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}
