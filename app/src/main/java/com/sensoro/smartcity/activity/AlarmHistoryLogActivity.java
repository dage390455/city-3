package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.AlarmHistoryLogRcContentAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IAlarmHistoryLogActivityView;
import com.sensoro.smartcity.model.CalendarDateModel;
import com.sensoro.smartcity.presenter.AlarmHistoryLogActivityPresenter;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.popup.AlarmPopUtilsTest;
import com.sensoro.smartcity.widget.popup.CalendarPopUtils;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sensoro.smartcity.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.smartcity.constant.Constants.DIRECTION_UP;

public class AlarmHistoryLogActivity extends BaseActivity<IAlarmHistoryLogActivityView, AlarmHistoryLogActivityPresenter>
        implements IAlarmHistoryLogActivityView, AlarmHistoryLogRcContentAdapter.OnAlarmHistoryLogConfirmListener, CalendarPopUtils.OnCalendarPopupCallbackListener, View.OnClickListener {
    @BindView(R.id.include_imv_title_imv_arrows_left)
    ImageView includeImvTitleImvArrowsLeft;
    @BindView(R.id.include_imv_title_tv_title)
    TextView includeImvTitleTvTitle;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.include_imv_title_imv_subtitle)
    ImageView includeImvTitleImvSubtitle;
    @BindView(R.id.fg_history_log_rc_content)
    RecyclerView acHistoryLogRcContent;
    @BindView(R.id.rl_alarm_log_date_edit)
    RelativeLayout rlAlarmLogDateEdit;
    @BindView(R.id.tv_alarm_log_date_edit)
    TextView tvAlarmLogDateEdit;
    @BindView(R.id.iv_alarm_log_date_close)
    ImageView ivAlarmLogDateClose;
    @BindView(R.id.alarm_return_top)
    ImageView mReturnTopImageView;
    @BindView(R.id.no_content)
    ImageView imv_content;
    @BindView(R.id.ic_no_content)
    LinearLayout icNoContent;
    private ProgressUtils mProgressUtils;
    private boolean isShowDialog = true;
    private CalendarPopUtils mCalendarPopUtils;
    private AlarmHistoryLogRcContentAdapter mAlarmHistoryLogRcContentAdapter;
    private Animation returnTopAnimation;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_alarm_history_log);
        ButterKnife.bind(mActivity);
        initView();
        mPresenter.initData(mActivity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AlarmPopUtilsTest.handlePhotoIntent(requestCode, resultCode, data);
    }

    private void initView() {
        includeImvTitleTvTitle.setText(mActivity.getString(R.string.historical_log));
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        mAlarmHistoryLogRcContentAdapter = new AlarmHistoryLogRcContentAdapter(mActivity);
        mAlarmHistoryLogRcContentAdapter.setOnAlarmHistoryLogConfirmListener(this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        acHistoryLogRcContent.setLayoutManager(linearLayoutManager);
        acHistoryLogRcContent.setAdapter(mAlarmHistoryLogRcContentAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL);
        acHistoryLogRcContent.addItemDecoration(dividerItemDecoration);
        //
        returnTopAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.return_top_in_anim);
        mReturnTopImageView.setAnimation(returnTopAnimation);
        mReturnTopImageView.setVisibility(View.GONE);
        mReturnTopImageView.setOnClickListener(this);
        //
        //新控件
        refreshLayout.setEnableAutoLoadMore(false);//开启自动加载功能（非必须）
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                mPresenter.requestDataByFilter(DIRECTION_DOWN);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                mPresenter.requestDataByFilter(DIRECTION_UP);
            }
        });
        //
        acHistoryLogRcContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if (xLinearLayoutManager.findFirstVisibleItemPosition() == 0 && newState == SCROLL_STATE_IDLE &&
//                        toolbarDirection == DIRECTION_DOWN) {
////                    mListRecyclerView.setre
//                }
                if (linearLayoutManager.findFirstVisibleItemPosition() > 4) {
                    if (newState == 0) {
                        mReturnTopImageView.setVisibility(View.VISIBLE);
                        if (returnTopAnimation != null && returnTopAnimation.hasEnded()) {
                            mReturnTopImageView.startAnimation(returnTopAnimation);
                        }
                    } else {
                        mReturnTopImageView.setVisibility(View.GONE);
                    }
                } else {
                    mReturnTopImageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
        mCalendarPopUtils = new CalendarPopUtils(mActivity);
        mCalendarPopUtils.setOnCalendarPopupCallbackListener(this);
        includeImvTitleImvSubtitle.setOnClickListener(this);
        ivAlarmLogDateClose.setOnClickListener(this);
        includeImvTitleImvArrowsLeft.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (returnTopAnimation != null) {
            returnTopAnimation.cancel();
            returnTopAnimation = null;
        }
    }

    @Override
    protected AlarmHistoryLogActivityPresenter createPresenter() {
        return new AlarmHistoryLogActivityPresenter();
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
        if (isShowDialog) {
            mProgressUtils.showProgress();
        }
        isShowDialog = true;
    }

    @Override
    public void dismissProgressDialog() {
        mProgressUtils.dismissProgress();
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }

    @Override
    public void onHistoryConfirm(View v, int position) {
        DeviceAlarmLogInfo deviceAlarmLogInfo = mAlarmHistoryLogRcContentAdapter.getData().get(position);
        mPresenter.onClickHistoryConfirm(deviceAlarmLogInfo);
    }

    @Override
    public void onCalendarPopupCallback(CalendarDateModel calendarDateModel) {
        mPresenter.onCalendarBack(calendarDateModel);
    }

    @Override
    public void showCalendar(long startTime, long endTime) {
        mCalendarPopUtils.show(includeImvTitleImvArrowsLeft, startTime, endTime);
    }

    @Override
    public void updateAlarmListAdapter(List<DeviceAlarmLogInfo> data) {
        if (data != null && data.size() > 0) {
            mAlarmHistoryLogRcContentAdapter.updateAdapter(data);
        }
        setNoContentVisible(data == null || data.size() < 1);
    }

    @Override
    public void setNoContentVisible(boolean isVisible) {
        icNoContent.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        acHistoryLogRcContent.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onPullRefreshCompleteNoMoreData() {
        refreshLayout.finishLoadMoreWithNoMoreData();
    }

    @Override
    public void onPullRefreshComplete() {
        refreshLayout.finishLoadMore();
        refreshLayout.finishRefresh();
    }

    @Override
    public void setDateSelectVisible(boolean isVisible) {
        rlAlarmLogDateEdit.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setDateSelectText(String text) {
        tvAlarmLogDateEdit.setText(text);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.include_imv_title_imv_subtitle:
                mPresenter.doSelectDate();
                break;
            case R.id.iv_alarm_log_date_close:
                mPresenter.closeDateSearch();
                break;
            case R.id.alarm_return_top:
                acHistoryLogRcContent.smoothScrollToPosition(0);
                mReturnTopImageView.setVisibility(View.GONE);
                refreshLayout.closeHeaderOrFooter();
                break;
            case R.id.include_imv_title_imv_arrows_left:
                finishAc();
                break;
        }
    }
}
