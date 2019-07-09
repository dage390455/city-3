package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.server.bean.DeviceInfo;
import com.sensoro.common.widgets.BoldTextView;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.adapter.MainHomeFragRcContentAdapter;
import com.sensoro.smartcity.imainviews.INearByDeviceActivityView;
import com.sensoro.smartcity.presenter.NearByDevicePresenter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NearByDeviceActivity extends BaseActivity<INearByDeviceActivityView, NearByDevicePresenter> implements INearByDeviceActivityView {


    SensoroCityApplication sensoroCityApplication;
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    BoldTextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;
    @BindView(R.id.no_content)
    ImageView noContent;
    @BindView(R.id.no_content_tip)
    TextView noContentTip;
    @BindView(R.id.ic_no_content)
    LinearLayout icNoContent;
    @BindView(R.id.rc_content)
    RecyclerView rcContent;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    MainHomeFragRcContentAdapter adapter;
    private ProgressUtils mProgressUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_nearbydevice_list);

        ButterKnife.bind(this);
        sensoroCityApplication = (SensoroCityApplication) getApplication();
        includeTextTitleTvTitle.setText("附近设备");
//        includeTextTitleTvSubtitle.setText("");
        includeTextTitleTvSubtitle.setText("通知设置");
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());


        adapter = new MainHomeFragRcContentAdapter(mActivity);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rcContent.setLayoutManager(linearLayoutManager);

        rcContent.setAdapter(adapter);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setEnableRefresh(true);


        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                mPresenter.getDeviceBriefInfo();
            }
        });
        adapter.setOnContentItemClickListener(new MainHomeFragRcContentAdapter.OnContentItemClickListener() {
            @Override
            public void onAlarmInfoClick(View v, int position) {
                mPresenter.clickAlarmInfo(position);

            }

            @Override
            public void onItemClick(View v, int position) {

                mPresenter.itemClickStartActivity(position);


            }
        });
        mPresenter.initData(this);


    }

    @Override
    protected NearByDevicePresenter createPresenter() {
        return new NearByDevicePresenter();
    }


    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.include_text_title_tv_subtitle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_select_time_ac_camera_detail:
                finishAc();
                break;

            case R.id.include_text_title_tv_subtitle:
                startAC(new Intent(mActivity, SettingNotificationActivity.class));
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        mProgressUtils.showProgress();

    }

    @Override
    public void dismissProgressDialog() {

        mProgressUtils.dismissProgress();
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateAdapter(ArrayList<DeviceInfo> deviceInfos) {
        if (deviceInfos != null && deviceInfos.size() > 0) {

            adapter.updateData(deviceInfos);
        }
    }

    @Override
    public void onPullRefreshComplete() {
        refreshLayout.finishLoadMore();
        refreshLayout.finishRefresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.refreshSp();
    }
}
