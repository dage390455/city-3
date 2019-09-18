package com.sensoro.smartcity.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.server.bean.MalfunctionListInfo;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.MalfunctionHistoryRcContentAdapter;
import com.sensoro.smartcity.imainviews.IMalfunctionHistoryActivityView;
import com.sensoro.common.model.CalendarDateModel;
import com.sensoro.smartcity.presenter.MalfunctionHistoryActivityPresenter;
import com.sensoro.common.widgets.CalendarPopUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sensoro.common.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.common.constant.Constants.DIRECTION_UP;

public class MalfunctionHistoryActivity extends BaseActivity<IMalfunctionHistoryActivityView, MalfunctionHistoryActivityPresenter>
        implements IMalfunctionHistoryActivityView, CalendarPopUtils.OnCalendarPopupCallbackListener, View.OnClickListener {
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
    @BindView(R.id.rl_malfunction_history_date_edit)
    RelativeLayout rlMalfunctionHistoryDateEdit;
    @BindView(R.id.tv_malfunction_history_date_edit)
    TextView tvMalfunctionHistoryDateEdit;
    @BindView(R.id.iv_malfunction_history_date_close)
    ImageView ivMalfunctionHistoryDateClose;
    @BindView(R.id.alarm_return_top)
    ImageView mReturnTopImageView;
    View icNoContent;
    private ProgressUtils mProgressUtils;
    private boolean isShowDialog = true;
    private CalendarPopUtils mCalendarPopUtils;
    private MalfunctionHistoryRcContentAdapter mMalfunctionHistoryRcContentAdapter;
    private Animation returnTopAnimation;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_malfunction_history);
        ButterKnife.bind(mActivity);
        initView();
        mPresenter.initData(mActivity);
    }


    private void initView() {
        icNoContent = LayoutInflater.from(this).inflate(R.layout.no_content, null);
        includeImvTitleTvTitle.setText(mActivity.getString(R.string.malfunction_history_log));
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        mMalfunctionHistoryRcContentAdapter = new MalfunctionHistoryRcContentAdapter(mActivity);
        mMalfunctionHistoryRcContentAdapter.setOnMalfunctionHistoryClickListener(new MalfunctionHistoryRcContentAdapter.OnMalfunctionHistoryClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                MalfunctionListInfo malfunctionListInfo = mMalfunctionHistoryRcContentAdapter.getData().get(position);
                mPresenter.doClickHistory(malfunctionListInfo);
            }
        });
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        acHistoryLogRcContent.setLayoutManager(linearLayoutManager);
        acHistoryLogRcContent.setAdapter(mMalfunctionHistoryRcContentAdapter);
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
        mCalendarPopUtils
                .setMonthStatus(1)
                .setOnCalendarPopupCallbackListener(this);
        includeImvTitleImvSubtitle.setOnClickListener(this);
        ivMalfunctionHistoryDateClose.setOnClickListener(this);
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
    protected MalfunctionHistoryActivityPresenter createPresenter() {
        return new MalfunctionHistoryActivityPresenter();
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
    public void onCalendarPopupCallback(CalendarDateModel calendarDateModel) {
        mPresenter.onCalendarBack(calendarDateModel);
    }

    @Override
    public void showCalendar(Long startTime, Long endTime) {
        mCalendarPopUtils.show(includeImvTitleImvArrowsLeft, startTime, endTime);
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void setNoContentVisible(boolean isVisible) {
        RefreshHeader refreshHeader = refreshLayout.getRefreshHeader();
        if (refreshHeader != null) {
            refreshHeader.setPrimaryColors(getResources().getColor(R.color.white));
        }
        if (isVisible) {
            refreshLayout.setRefreshContent(icNoContent);
        } else {
            refreshLayout.setRefreshContent(acHistoryLogRcContent);
        }
    }

    @Override
    public void updateMalfunctionListAdapter(List<MalfunctionListInfo> mMalfunctionInfoList) {
        if (mMalfunctionInfoList != null && mMalfunctionInfoList.size() > 0) {
            mMalfunctionHistoryRcContentAdapter.updateAdapter(mMalfunctionInfoList);
        }
        setNoContentVisible(mMalfunctionInfoList == null || mMalfunctionInfoList.size() < 1);
    }

    @Override
    public void onPullRefreshComplete() {
        refreshLayout.finishLoadMore();
        refreshLayout.finishRefresh();
    }

    @Override
    public void setDateSelectVisible(boolean isVisible) {
        rlMalfunctionHistoryDateEdit.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setDateSelectText(String text) {
        tvMalfunctionHistoryDateEdit.setText(text);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.include_imv_title_imv_subtitle:
                mPresenter.doSelectDate();
                break;
            case R.id.iv_malfunction_history_date_close:
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
