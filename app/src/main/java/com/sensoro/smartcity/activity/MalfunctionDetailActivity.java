package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.MalfunctionDetailRcContentAdapter;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IMalfunctionDetailActivityView;
import com.sensoro.smartcity.presenter.MalfunctionDetailActivityPresenter;
import com.sensoro.common.server.bean.MalfunctionListInfo;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MalfunctionDetailActivity extends BaseActivity<IMalfunctionDetailActivityView, MalfunctionDetailActivityPresenter>
        implements IMalfunctionDetailActivityView {
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.ac_malfunction_detail_tv_name)
    TextView acMalfunctionDetailTvName;
    @BindView(R.id.ac_malfunction_detail_tv_sn)
    TextView acMalfunctionDetailTvSn;
    @BindView(R.id.ac_malfunction_detail_imv_icon)
    ImageView acMalfunctionDetailImvIcon;
    @BindView(R.id.ac_malfunction_detail_tv_time)
    TextView acMalfunctionDetailTvTime;
    @BindView(R.id.ac_malfunction_detail_tv_time_text)
    TextView acMalfunctionDetailTvTimeText;
    @BindView(R.id.ac_malfunction_detail_ll_time)
    LinearLayout acMalfunctionDetailLlTime;
    @BindView(R.id.ac_malfunction_detail_imv_count_icon)
    ImageView acMalfunctionDetailImCountIcon;
    @BindView(R.id.ac_malfunction_detail_tv_count)
    TextView acMalfunctionDetailTvCount;
    @BindView(R.id.ac_malfunction_detail_tv_count_text)
    TextView acMalfunctionDetailTvCountText;
    @BindView(R.id.ac_malfunction_detail_ll_count)
    LinearLayout acMalfunctionDetailLlCount;
    @BindView(R.id.ac_malfunction_detail_ll_card)
    LinearLayout acMalfunctionDetailLlCard;
    @BindView(R.id.ac_malfunction_detail_tv_contact_owner)
    TextView acMalfunctionDetailTvContactOwner;
    @BindView(R.id.ac_malfunction_detail_tv_quick_navigation)
    TextView acMalfunctionDetailTvQuickNavigation;
    @BindView(R.id.ac_malfunction_detail_tv_confirm)
    TextView acMalfunctionDetailTvConfirm;
    @BindView(R.id.ac_malfunction_detail_ll_bottom)
    LinearLayout acMalfunctionDetailLlBottom;
    @BindView(R.id.ac_malfunction_detail_rc_content)
    RecyclerView acMalfunctionDetailRcContent;
    @BindView(R.id.view3)
    View view3;
    private MalfunctionDetailRcContentAdapter mContentAdapter;
    private ProgressUtils mProgressUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_malfunction_detail);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        includeTextTitleTvTitle.setText(getString(R.string.malfunction_log));
        includeTextTitleTvSubtitle.setText(getString(R.string.malfunction_history_log));

        initRcContent();
    }

    private void initRcContent() {
        mContentAdapter = new MalfunctionDetailRcContentAdapter(mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        acMalfunctionDetailRcContent.setLayoutManager(manager);
        acMalfunctionDetailRcContent.setAdapter(mContentAdapter);
    }

    @Override
    protected MalfunctionDetailActivityPresenter createPresenter() {
        return new MalfunctionDetailActivityPresenter();
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


    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.ac_malfunction_detail_tv_contact_owner, R.id.ac_malfunction_detail_tv_quick_navigation,
            R.id.ac_malfunction_detail_tv_confirm, R.id.include_text_title_tv_subtitle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;
            case R.id.ac_malfunction_detail_tv_contact_owner:
                mPresenter.doContactOwner();
                break;
            case R.id.ac_malfunction_detail_tv_quick_navigation:
                mPresenter.doNavigation();
                break;
            case R.id.ac_malfunction_detail_tv_confirm:
                mPresenter.doChangeDevice();
                break;
            case R.id.include_text_title_tv_subtitle:
                mPresenter.doMalfunctionHistory();
                break;
        }
    }

    @Override
    public void setDeviceNameText(String deviceName) {
        acMalfunctionDetailTvName.setText(deviceName);
    }

    @Override
    public void setMalfunctionStatus(int malfunctionStatus, String time) {
        switch (malfunctionStatus) {
            case 1:
                acMalfunctionDetailLlTime.setBackgroundResource(R.drawable.shape_bg_corner_f4_shadow);
                acMalfunctionDetailImvIcon.setImageResource(R.drawable.alert_time_normal);
                acMalfunctionDetailTvTime.setTextColor(mActivity.getResources().getColor(R.color.c_252525));
                acMalfunctionDetailTvTimeText.setTextColor(mActivity.getResources().getColor(R.color.c_a6a6a6));
                break;
            case 2:
                acMalfunctionDetailLlTime.setBackgroundResource(R.drawable.shape_bg_corner_fdc83b_shadow);
                acMalfunctionDetailImvIcon.setImageResource(R.drawable.alert_time_white);
                acMalfunctionDetailTvTime.setTextColor(Color.WHITE);
                acMalfunctionDetailTvTimeText.setTextColor(Color.WHITE);
                break;
        }
        acMalfunctionDetailTvTime.setText(time);

    }

    @Override
    public void updateRcContent(List<MalfunctionListInfo.RecordsBean> records, String malfunctionText) {
        mContentAdapter.setData(records, malfunctionText);
    }

    @Override
    public void setMalfunctionCount(String count) {
        acMalfunctionDetailTvCount.setText(count);
    }

    @Override
    public void setMalfunctionDetailConfirmVisible(boolean isVisible) {
        acMalfunctionDetailTvConfirm.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setDeviceSn(String deviceSN) {
        acMalfunctionDetailTvSn.setText(deviceSN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
        }
    }
}
