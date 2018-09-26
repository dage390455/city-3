package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.InspectionTaskAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IInspectionTaskListActivityView;
import com.sensoro.smartcity.model.CalendarDateModel;
import com.sensoro.smartcity.server.bean.InspectionIndexTaskInfo;
import com.sensoro.smartcity.server.bean.InspectionTaskModel;
import com.sensoro.smartcity.presenter.InspectionTaskListActivityPresenter;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroToast;
import com.sensoro.smartcity.widget.popup.CalendarPopUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InspectionTaskListActivity extends BaseActivity<IInspectionTaskListActivityView, InspectionTaskListActivityPresenter>
        implements IInspectionTaskListActivityView, CalendarPopUtils.OnCalendarPopupCallbackListener,Constants {
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;
    @BindView(R.id.ac_inspection_task_list_rb_current)
    RadioButton acInspectionTaskListRbCurrent;
    @BindView(R.id.ac_inspection_task_list_rb_history)
    RadioButton acInspectionTaskListRbHistory;
    @BindView(R.id.ac_inspection_task_list_rg)
    RadioGroup acInspectionTaskListRg;
    @BindView(R.id.ac_inspection_task_list_imv_calendar)
    ImageView acInspectionTaskListImvCalendar;
    @BindView(R.id.ac_inspection_task_list_rc_content)
    RecyclerView acInspectionTaskListRcContent;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.ac_inspection_task_list_tv_date_edit)
    TextView acInspectionTaskListTvDateEdit;
    @BindView(R.id.ac_inspection_task_list_imv_date_close)
    ImageView acInspectionTaskListImvDateClose;
    @BindView(R.id.ac_inspection_task_list_rl_date_edit)
    RelativeLayout acInspectionTaskListRlDateEdit;
    private InspectionTaskAdapter mTaskAdapter;
    private CalendarPopUtils mCalendarPopUtils;
    private long startTime = -1;
    private long endTime = -1;
    private ProgressUtils mProgressUtils;
    private boolean isShowProgressDialog = true;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_inspection_task_list);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        includeTextTitleTvTitle.setText("巡检任务");
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        acInspectionTaskListImvCalendar.setVisibility(View.GONE);

        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        initRcContent();
        initCalendarPop();
    }

    private void initCalendarPop() {
        mCalendarPopUtils = new CalendarPopUtils(mActivity);
        mCalendarPopUtils.setOnCalendarPopupCallbackListener(this);
    }

    private void initRcContent() {
        mTaskAdapter = new InspectionTaskAdapter(mActivity);
        final LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        acInspectionTaskListRcContent.setLayoutManager(manager);
        acInspectionTaskListRcContent.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
        acInspectionTaskListRcContent.setAdapter(mTaskAdapter);

        mTaskAdapter.setOnRecycleViewItemClickListener(new RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
               mPresenter.doItemClick(mTaskAdapter.getItem(position));

            }
        });

        acInspectionTaskListRcContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if (xLinearLayoutManager.findFirstVisibleItemPosition() == 0 && newState == SCROLL_STATE_IDLE &&
//                        toolbarDirection == DIRECTION_DOWN) {
////                    mListRecyclerView.setre
//                }
                if (manager.findFirstVisibleItemPosition() > 4) {
                    if (newState == 0) {
//                        mReturnTopImageView.setVisibility(VISIBLE);
//                        if (returnTopAnimation.hasEnded()) {
//                            mReturnTopImageView.startAnimation(returnTopAnimation);
//                        }
                    } else {
//                        mReturnTopImageView.setVisibility(View.GONE);
                    }
                } else {
//                    mReturnTopImageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });

        refreshLayout.setEnableAutoLoadMore(true);//开启自动加载功能（非必须）
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                isShowProgressDialog = false;
                mPresenter.LoadMore(DIRECTION_DOWN);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                isShowProgressDialog = false;
                mPresenter.LoadMore(Constants.DIRECTION_UP);
            }
        });


    }

    @Override
    protected InspectionTaskListActivityPresenter createPresenter() {
        return new InspectionTaskListActivityPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mProgressUtils!=null){
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }

    }

    @Override
    public void startAC(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void finishAc() {
        finish();

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
        if (isShowProgressDialog) {
            mProgressUtils.showProgress();
        }
        isShowProgressDialog = true;
    }

    @Override
    public void dismissProgressDialog() {
        mProgressUtils.dismissProgress();
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_LONG).show();
    }


    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.ac_inspection_task_list_rb_current,
            R.id.ac_inspection_task_list_rb_history, R.id.ac_inspection_task_list_imv_calendar,
            R.id.ac_inspection_task_list_imv_date_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;
            case R.id.ac_inspection_task_list_rb_current:
                acInspectionTaskListImvCalendar.setVisibility(View.GONE);
                if (getRlDateEditIsVisible()) {
                    acInspectionTaskListRlDateEdit.setVisibility(View.GONE);
                }
                mPresenter.doUndone();
                break;
            case R.id.ac_inspection_task_list_rb_history:
                if (getRlDateEditIsVisible()) {
                    acInspectionTaskListRlDateEdit.setVisibility(View.GONE);
                }
                acInspectionTaskListImvCalendar.setVisibility(View.VISIBLE);
                mPresenter.doDone();
                break;
            case R.id.ac_inspection_task_list_imv_calendar:
                doCalendar();
                break;
            case R.id.ac_inspection_task_list_imv_date_close:
                acInspectionTaskListRlDateEdit.setVisibility(View.GONE);
                mPresenter.doDone();
                break;
        }
    }

    private void doCalendar() {
        long temp_startTime = -1;
        long temp_endTime = -1;
        if (getRlDateEditIsVisible()) {
            temp_startTime = startTime;
            temp_endTime = endTime;
        }

        mCalendarPopUtils.show(includeTextTitleClRoot, temp_startTime, temp_endTime);
    }

    @Override
    public void onCalendarPopupCallback(CalendarDateModel calendarDateModel) {
        setRlDateEditVisible(true);
        startTime = DateUtil.strToDate(calendarDateModel.startDate).getTime();
        endTime = DateUtil.strToDate(calendarDateModel.endDate).getTime();
        setSelectedDateSearchText(DateUtil.getMothDayFormatDate(startTime) + "-" + DateUtil
                .getMothDayFormatDate(endTime));
        endTime += 1000 * 60 * 60 * 24;
        mPresenter.requestDataByDate(startTime,endTime);

    }


    @Override
    public void setRlDateEditVisible(boolean isVisible) {
        acInspectionTaskListRlDateEdit.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean getRlDateEditIsVisible() {
        return acInspectionTaskListRlDateEdit.getVisibility() == View.VISIBLE;
    }

    @Override
    public void setSelectedDateSearchText(String time) {
        acInspectionTaskListTvDateEdit.setText(time);
    }

    @Override
    public void updateRcContent(List<InspectionIndexTaskInfo> tasks) {
        mTaskAdapter.updateTaskList(tasks);
    }

    @Override
    public void onPullRefreshCompleted() {
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
    }

    @Override
    public void recycleViewRefreshCompleteNoMoreData() {
        refreshLayout.finishLoadMoreWithNoMoreData();
    }
}
