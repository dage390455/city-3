package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.InspectionTaskAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IInspectionTaskListActivityView;
import com.sensoro.smartcity.presenter.InspectionTaskListActivityPresenter;
import com.sensoro.smartcity.widget.SensoroXLinearLayoutManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sensoro.smartcity.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.smartcity.constant.Constants.DIRECTION_UP;

public class InspectionTaskListActivity extends BaseActivity<IInspectionTaskListActivityView, InspectionTaskListActivityPresenter>
        implements IInspectionTaskListActivityView {
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
    private InspectionTaskAdapter mTaskAdapter;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_inspection_task_list);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        acInspectionTaskListImvCalendar.setVisibility(View.GONE);

        initRcContent();
    }

    private void initRcContent() {
        mTaskAdapter = new InspectionTaskAdapter(mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        acInspectionTaskListRcContent.setAdapter(mTaskAdapter);

        refreshLayout.setEnableAutoLoadMore(true);//开启自动加载功能（非必须）
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                Log.e("hcs",":::onrefresh");
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                Log.e("hcs",":loadmore::");
            }
        });
    }

    @Override
    protected InspectionTaskListActivityPresenter createPresenter() {
        return new InspectionTaskListActivityPresenter();
    }

    @Override
    public void startAC(Intent intent) {

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

    }

    @Override
    public void dismissProgressDialog() {

    }

    @Override
    public void toastShort(String msg) {

    }

    @Override
    public void toastLong(String msg) {

    }


    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.ac_inspection_task_list_rb_current, R.id.ac_inspection_task_list_rb_history, R.id.ac_inspection_task_list_imv_calendar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;
            case R.id.ac_inspection_task_list_rb_current:
                break;
            case R.id.ac_inspection_task_list_rb_history:
                break;
            case R.id.ac_inspection_task_list_imv_calendar:
                break;
        }
    }
}
