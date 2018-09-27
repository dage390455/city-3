package com.sensoro.smartcity.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.InspectionTaskRcContentAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IInspectionTaskActivityView;
import com.sensoro.smartcity.model.DeviceTypeModel;
import com.sensoro.smartcity.model.InspectionStatusCountModel;
import com.sensoro.smartcity.presenter.InspectionTaskActivityPresenter;
import com.sensoro.smartcity.server.bean.InspectionTaskDeviceDetail;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroToast;
import com.sensoro.smartcity.widget.popup.InspectionTaskStatePopUtils;
import com.sensoro.smartcity.widget.popup.SelectDeviceTypePopUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InspectionTaskActivity extends BaseActivity<IInspectionTaskActivityView, InspectionTaskActivityPresenter>
        implements IInspectionTaskActivityView, Constants {
    @BindView(R.id.ac_inspection_task_imv_arrows_left)
    ImageView acInspectionTaskImvArrowsLeft;
    @BindView(R.id.ac_inspection_task_ll_search)
    LinearLayout acInspectionTaskLlSearch;
    @BindView(R.id.ac_inspection_task_fl_state)
    FrameLayout acInspectionTaskFlState;
    @BindView(R.id.ac_inspection_task_fl_type)
    FrameLayout acInspectionTaskFlType;
    @BindView(R.id.ac_inspection_task_rc_content)
    RecyclerView acInspectionTaskRcContent;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.ac_inspection_task_et_search)
    EditText acInspectionTaskEtSearch;
    @BindView(R.id.tv_inspection_task_search_cancel)
    TextView tvInspectionTaskSearchCancel;
    @BindView(R.id.ac_inspection_task_imv_scan)
    ImageView acInspectionTaskImvScan;
    @BindView(R.id.ac_inspection_task_imv_map)
    ImageView acInspectionTaskImvMode;
    @BindView(R.id.ac_inspection_task_tv_inspection_count)
    TextView acInspectionTaskTvInspectionCount;
    @BindView(R.id.ac_inspection_task_tv_not_inspection_count)
    TextView acInspectionTaskTvNotInspectionCount;
    @BindView(R.id.ac_inspection_task_tv_state)
    TextView acInspectionTaskTvState;
    @BindView(R.id.ac_inspection_task_tv_type)
    TextView acInspectionTaskTvType;
    @BindView(R.id.ac_inspection_task_ll_select)
    LinearLayout acInspectionTaskLlSelect;
    private InspectionTaskRcContentAdapter mContentAdapter;
    private SelectDeviceTypePopUtils mSelectDeviceTypePop;
    private InspectionTaskStatePopUtils mSelectStatusPop;
    private ProgressUtils mProgressUtils;
    private boolean isShowDialog = true;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_inspection_task);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
//地图模式切换，图片 map_list_mode map_mode,已经准备好了
        acInspectionTaskEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 当按了搜索之后关闭软键盘
                    String text = acInspectionTaskEtSearch.getText().toString();
                    mPresenter.requestSearchData(DIRECTION_DOWN, text);
                    dismissInputMethodManager(acInspectionTaskEtSearch);
                    return true;
                }
                return false;
            }
        });
        initRcContent();

        initSelectDeviceTypePop();

        initSelectStatusPop();
    }

    private void dismissInputMethodManager(View view) {
        acInspectionTaskEtSearch.setCursorVisible(false);
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//从控件所在的窗口中隐藏
    }

    public void forceOpenSoftKeyboard() {
        acInspectionTaskEtSearch.setCursorVisible(true);
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onDestroy() {
        mProgressUtils.destroyProgress();
        super.onDestroy();
    }

    private void initSelectDeviceTypePop() {
        mSelectDeviceTypePop = new SelectDeviceTypePopUtils(mActivity);
        mSelectDeviceTypePop.setTitleVisible(false);
        mSelectDeviceTypePop.setUpAnimation();
        mSelectDeviceTypePop.setSelectDeviceTypeItemClickListener(new SelectDeviceTypePopUtils.SelectDeviceTypeItemClickListener() {
            @Override
            public void onSelectDeviceTypeItemClick(View view, int position) {
                //选择类型的pop点击事件
                acInspectionTaskTvType.setText(mSelectDeviceTypePop.getItem(position).name);
                mSelectDeviceTypePop.dismiss();
//                mPresenter.requestDataByDirection(DIRECTION_DOWN);
            }
        });
    }

    private void initSelectStatusPop() {
        mSelectStatusPop = new InspectionTaskStatePopUtils(mActivity);
        mSelectStatusPop.setUpAnimation();
        mSelectStatusPop.clearAnimation();
        mSelectStatusPop.setSelectDeviceTypeItemClickListener(new InspectionTaskStatePopUtils.SelectDeviceTypeItemClickListener() {
            @Override
            public void onSelectDeviceTypeItemClick(View view, int position) {
                //选择类型的pop点击事件
                InspectionStatusCountModel item = mSelectStatusPop.getItem(position);
                acInspectionTaskTvState.setText(item.statusTitle);
                mPresenter.doSelectStatusDevice(item);
                mSelectStatusPop.dismiss();

            }
        });
    }

    private void initRcContent() {
        mContentAdapter = new InspectionTaskRcContentAdapter(mActivity);
        final LinearLayoutManager manager = new LinearLayoutManager(mActivity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        acInspectionTaskRcContent.setLayoutManager(manager);
        acInspectionTaskRcContent.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
        acInspectionTaskRcContent.setAdapter(mContentAdapter);

        mContentAdapter.setOnRecycleViewItemClickListener(new InspectionTaskRcContentAdapter.InspectionTaskRcItemClickListener() {
            @Override
            public void onInspectionTaskInspectionClick(int position, int status) {
                mPresenter.doItemClick(position,status);
            }

            @Override
            public void onInspectionTaskNavigationClick(int position) {
                mPresenter.doNavigation(position);
            }

        });

        acInspectionTaskRcContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                isShowDialog = false;
                String text = acInspectionTaskEtSearch.getText().toString();
                mPresenter.requestSearchData(DIRECTION_DOWN, text);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                String text = acInspectionTaskEtSearch.getText().toString();
                mPresenter.requestSearchData(DIRECTION_UP, text);
            }
        });
    }

    @Override
    protected InspectionTaskActivityPresenter createPresenter() {
        return new InspectionTaskActivityPresenter();
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
    public void onPullRefreshComplete() {
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
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
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.INSTANCE.makeText(msg, Toast.LENGTH_LONG).show();
    }


    @OnClick({R.id.ac_inspection_task_imv_arrows_left, R.id.ac_inspection_task_ll_search, R.id.ac_inspection_task_et_search,
            R.id.ac_inspection_task_fl_state, R.id.ac_inspection_task_fl_type, R.id.ac_inspection_task_imv_scan, R.id.tv_inspection_task_search_cancel
            , R.id.ac_inspection_task_imv_map})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ac_inspection_task_imv_arrows_left:
                finishAc();
                break;
            case R.id.ac_inspection_task_ll_search:
            case R.id.ac_inspection_task_et_search:
                //TODO
                acInspectionTaskEtSearch.requestFocus();
                forceOpenSoftKeyboard();
                break;
            case R.id.ac_inspection_task_fl_state:
                mPresenter.doInspectionStatus(true);
                break;
            case R.id.ac_inspection_task_fl_type:
                mPresenter.doInspectionType();
                break;
            case R.id.ac_inspection_task_imv_scan:
                toastShort("扫描");
                break;
            case R.id.tv_inspection_task_search_cancel:
                doCancelSearch();
                break;
            case R.id.ac_inspection_task_imv_map:

                break;
        }
    }

    private void doCancelSearch() {
        if (getSearchTextVisible()) {
            acInspectionTaskEtSearch.getText().clear();
        }
        mPresenter.doCancelSearch();
    }

    @Override
    public void setSearchButtonTextVisible(boolean isVisible) {
        if (isVisible) {
            tvInspectionTaskSearchCancel.setVisibility(View.VISIBLE);
            dismissInputMethodManager(acInspectionTaskEtSearch);
        } else {
            tvInspectionTaskSearchCancel.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean getSearchTextVisible() {
        return tvInspectionTaskSearchCancel.getVisibility() == View.VISIBLE;
    }

    @Override
    public void updateSelectDeviceTypeList(List<DeviceTypeModel> data) {
        if (mSelectDeviceTypePop != null) {
            mSelectDeviceTypePop.updateSelectDeviceTypeList(data);
//            mSelectDeviceTypePop.showAsDropDown(acInspectionTaskLlSelect);
        }

    }

    @Override
    public void onPullRefreshCompleteNoMoreData() {
        refreshLayout.finishLoadMoreWithNoMoreData();
    }

    @Override
    public void setBottomInspectionStateTitle(String finish, String unFinish) {
        acInspectionTaskTvNotInspectionCount.setText(unFinish);
        acInspectionTaskTvInspectionCount.setText(finish);
    }

    @Override
    public List<DeviceTypeModel> getSelectDeviceList() {
        if (mSelectDeviceTypePop != null) {
            return mSelectDeviceTypePop.getSelectDeviceTypeList();
        }

        return null;
    }

    @Override
    public void showSelectDeviceTypePop() {
        if (mSelectDeviceTypePop != null) {
            mSelectDeviceTypePop.showAsDropDown(acInspectionTaskLlSelect);
        }

    }

    @Override
    public void updateSelectDeviceStatusList(List<InspectionStatusCountModel> data) {
        if (mSelectStatusPop != null) {
            mSelectStatusPop.updateSelectDeviceStatusList(data);
            mSelectStatusPop.showAsDropDown(acInspectionTaskLlSelect);
        }
    }

    @Override
    public void updateInspectionTaskDeviceItem(List<InspectionTaskDeviceDetail> data) {
        mContentAdapter.updateDevices(data);
    }
}
