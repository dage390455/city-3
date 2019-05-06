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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.DeviceCameraContentAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ICameraListActivityView;
import com.sensoro.smartcity.model.CalendarDateModel;
import com.sensoro.smartcity.model.CameraFilterModel;
import com.sensoro.smartcity.presenter.CameraListActivityPresenter;
import com.sensoro.smartcity.server.bean.DeviceCameraInfo;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.popup.CalendarPopUtils;
import com.sensoro.smartcity.widget.popup.CameraListFilterPopupWindow;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CameraListActivity extends BaseActivity<ICameraListActivityView, CameraListActivityPresenter>
        implements ICameraListActivityView, DeviceCameraContentAdapter.OnDeviceCameraContentClickListener, CalendarPopUtils.OnCalendarPopupCallbackListener, View.OnClickListener {

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.fg_history_log_rc_content)
    RecyclerView acHistoryLogRcContent;
    @BindView(R.id.alarm_return_top)
    ImageView mReturnTopImageView;
    @BindView(R.id.no_content)
    ImageView imv_content;
    @BindView(R.id.ic_no_content)
    LinearLayout icNoContent;
    @BindView(R.id.camera_list_ll_top_search)
    LinearLayout cameraListLlTopSearch;
    @BindView(R.id.camera_list_iv_top_back)
    ImageView cameraListIvTopBack;
    @BindView(R.id.camera_list_iv_search_clear)
    ImageView cameraListIvSearchClear;
    @BindView(R.id.camera_list_et_search)
    EditText cameraListEtSearch;
    @BindView(R.id.camera_list_tv_search_cancel)
    TextView cameraListTvSearchCancel;
    @BindView(R.id.camera_list_iv_filter)
    ImageView cameraListIvFilter;
    @BindView(R.id.no_content_tip)
    TextView noContentTip;
    private ProgressUtils mProgressUtils;
    private boolean isShowDialog = true;
    private DeviceCameraContentAdapter mDeviceCameraContentAdapter;
    private Animation returnTopAnimation;

    private CameraListFilterPopupWindow mCameraListFilterPopupWindow;
    private List<CameraFilterModel> mCameraFilterModelList = new ArrayList<>();

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_camera_list);
        ButterKnife.bind(mActivity);
        initView();
        mPresenter.initData(mActivity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        mDeviceCameraContentAdapter = new DeviceCameraContentAdapter(mActivity);
        mDeviceCameraContentAdapter.setOnAlarmHistoryLogConfirmListener(this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        acHistoryLogRcContent.setLayoutManager(linearLayoutManager);
        acHistoryLogRcContent.setAdapter(mDeviceCameraContentAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL);
        acHistoryLogRcContent.addItemDecoration(dividerItemDecoration);
        //
        returnTopAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.return_top_in_anim);
        mReturnTopImageView.setAnimation(returnTopAnimation);
        mReturnTopImageView.setVisibility(View.GONE);
        mReturnTopImageView.setOnClickListener(this);
        cameraListIvFilter.setOnClickListener(this);
        //
        //新控件
        refreshLayout.setEnableAutoLoadMore(false);//开启自动加载功能（非必须）
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                mPresenter.requestDataByFilter(Constants.DIRECTION_DOWN);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                mPresenter.requestDataByFilter(Constants.DIRECTION_UP);
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
        mCameraListFilterPopupWindow = new CameraListFilterPopupWindow(this);

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
    protected CameraListActivityPresenter createPresenter() {
        return new CameraListActivityPresenter();
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
    public void onItemClick(View v, int position) {
        DeviceCameraInfo deviceCameraInfo = mDeviceCameraContentAdapter.getData().get(position);
        mPresenter.onClickDeviceCamera(deviceCameraInfo);
    }

    @Override
    public void onCalendarPopupCallback(CalendarDateModel calendarDateModel) {
        mPresenter.onCalendarBack(calendarDateModel);
    }

    @Override
    public void showCalendar(long startTime, long endTime) {
//        mCalendarPopUtils.show(includeImvTitleImvArrowsLeft, startTime, endTime);
    }

    @Override
    public void updateDeviceCameraAdapter(List<DeviceCameraInfo> data) {
        if (data != null && data.size() > 0) {
            mDeviceCameraContentAdapter.updateAdapter(data);
        }
        setNoContentVisible(data == null || data.size() < 1);
    }


    @Override
    public void setNoContentVisible(boolean isVisible) {
        icNoContent.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        acHistoryLogRcContent.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    }

    @Override
    public void setSmartRefreshEnable(boolean enable) {
        refreshLayout.setEnableLoadMore(enable);
        refreshLayout.setEnableRefresh(enable);
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
    }

    @Override
    public void setDateSelectText(String text) {
//        tvAlarmLogDateEdit.setText(text);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_alarm_log_date_close:
                mPresenter.closeDateSearch();
                break;
            case R.id.alarm_return_top:
                acHistoryLogRcContent.smoothScrollToPosition(0);
                mReturnTopImageView.setVisibility(View.GONE);
                refreshLayout.closeHeaderOrFooter();
                break;
            case R.id.camera_list_iv_top_back:
                finishAc();
                break;

            case R.id.camera_list_iv_filter:


                if (mCameraFilterModelList.size() == 0) {

                    mPresenter.getFilterPopData();
                } else {
                    if (!mCameraListFilterPopupWindow.isShowing()) {
                        mCameraListFilterPopupWindow.updateSelectDeviceStatusList(mCameraFilterModelList);
                        cameraListIvFilter.setImageResource(R.drawable.camera_filter_selected);
                        mCameraListFilterPopupWindow.showAsDropDown(cameraListLlTopSearch);
                    } else {
                        cameraListIvFilter.setImageResource(R.drawable.camera_filter_unselected);
                        mCameraListFilterPopupWindow.dismiss();
                    }
                }


                break;
            default:
                break;
        }


    }


    @Override
    public void updateFilterPop(List<CameraFilterModel> data) {

        if (!mCameraListFilterPopupWindow.isShowing()) {
            mCameraFilterModelList.clear();
            mCameraFilterModelList.addAll(data);
            mCameraListFilterPopupWindow.updateSelectDeviceStatusList(mCameraFilterModelList);
            cameraListIvFilter.setImageResource(R.drawable.camera_filter_selected);
            mCameraListFilterPopupWindow.showAsDropDown(cameraListLlTopSearch);
        } else {
            cameraListIvFilter.setImageResource(R.drawable.camera_filter_unselected);
            mCameraListFilterPopupWindow.dismiss();
        }

    }

    @Override
    public void onBackPressed() {
        if (mCameraListFilterPopupWindow.isShowing()) {
            cameraListIvFilter.setImageResource(R.drawable.camera_filter_unselected);

            mCameraListFilterPopupWindow.dismiss();
        } else {
            super.onBackPressed();
        }

    }


}
