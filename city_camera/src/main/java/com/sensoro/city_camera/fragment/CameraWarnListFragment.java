package com.sensoro.city_camera.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.city_camera.IMainViews.ICameraWarnListFragmentView;
import com.sensoro.city_camera.R;
import com.sensoro.city_camera.R2;
import com.sensoro.city_camera.adapter.CameraWarnFragRcContentAdapter;
import com.sensoro.city_camera.dialog.SecurityWarnConfirmDialog;
import com.sensoro.city_camera.model.FilterModel;
import com.sensoro.city_camera.presenter.CameraWarnListFragmentPresenter;
import com.sensoro.city_camera.widget.FilterPopUtils;
import com.sensoro.common.adapter.SearchHistoryAdapter;
import com.sensoro.common.base.BaseFragment;
import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.server.security.bean.SecurityAlarmInfo;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.utils.LogUtils;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.SpacesItemDecoration;
import com.sensoro.common.widgets.TipOperationDialogUtils;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

import static com.sensoro.common.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.common.constant.Constants.DIRECTION_UP;

/**
 * @author wangqinghao
 */
@Route(path = ARouterConstants.FRAGMENT_CAMERA_WARN_LIST)
public class CameraWarnListFragment extends BaseFragment<ICameraWarnListFragmentView, CameraWarnListFragmentPresenter>
        implements ICameraWarnListFragmentView, CameraWarnFragRcContentAdapter.CameraWarnConfirmStatusClickListener, TipOperationDialogUtils.TipDialogUtilsClickListener {

    @BindView(R2.id.fg_camera_warns_top_search_title_root)
    RelativeLayout fgMainWarnTitleRoot;
    @BindView(R2.id.fg_camera_warns_top_search_et_search)
    EditText edFilterContent;
    @BindView(R2.id.fg_camera_warns_top_search_imv_clear)
    ImageView ivFilterContentClear;
    @BindView(R2.id.tv_top_search_alarm_search_cancel)
    TextView tvFilterCancal;
    @BindView(R2.id.fg_camera_warns_top_filter_rl)
    RelativeLayout layouFilterContent;
    @BindView(R2.id.layout_filter_capture_time)
    RelativeLayout layoutCaptureTime;
    @BindView(R2.id.tv_search_camera_warns_time)
    TextView tvFilterCapturetime;
    @BindView(R2.id.iv_search_camera_warns_time)
    ImageView ivFilterCapturetime;

    @BindView(R2.id.layout_filter_process_status)
    RelativeLayout layoutProcessStatus;
    @BindView(R2.id.tv_search_camera_warns_status)
    TextView tvFilterProcessStatus;
    @BindView(R2.id.iv_search_camera_warns_status)
    ImageView ivFilterProcessStatus;

    @BindView(R2.id.fg_camera_warns_rc_content)
    RecyclerView rvCameraWarnsContent;
    @BindView(R2.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R2.id.ll_search_history)
    LinearLayout llSearchHistory;
    @BindView(R2.id.btn_search_clear)
    ImageView btnSearchClear;
    @BindView(R2.id.rv_search_history)
    RecyclerView rvSearchHistory;
    @BindView(R2.id.alarm_return_top)
    ImageView mReturnTopImageView;
    @BindView(R2.id.ic_no_content)
    LinearLayout icNoContent;

    private CameraWarnFragRcContentAdapter mRcContentAdapter;
    private boolean isShowDialog = true;
    private ProgressUtils mProgressUtils;
    private Animation returnTopAnimation;
    private SearchHistoryAdapter mSearchHistoryAdapter;
    //删除历史记录
    private TipOperationDialogUtils historyClearDialog;
    private SecurityWarnConfirmDialog mSecurityWarnConfirmDialog;

    private FilterPopUtils mCapturetimeFilterPopUtils;
    private FilterPopUtils mProcessStatusFilterPopUtils;

    public static final int WARN_FILTER_TIME = 0;
    public static final int WARN_FILTER_STATUS = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
    }

    @Override
    protected void initData(Context activity) {
        initView();
        mPresenter.initData(activity);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mRootFragment.getActivity()).build());
        mCapturetimeFilterPopUtils = new FilterPopUtils(getActivity());
        mProcessStatusFilterPopUtils = new FilterPopUtils(getActivity());

        //返回顶部
        returnTopAnimation = AnimationUtils.loadAnimation(mRootFragment.getContext(), R.anim.return_top_in_anim);
        mReturnTopImageView.setAnimation(returnTopAnimation);
        mReturnTopImageView.setVisibility(View.GONE);
        //搜索数据========
        edFilterContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 当按了搜索之后关闭软键盘
                    String text = edFilterContent.getText().toString();
                    mPresenter.setFilterText(text);
                    mPresenter.save(text);
                    edFilterContent.clearFocus();
                    mPresenter.requestSearchData(DIRECTION_DOWN);
                    AppUtils.dismissInputMethodManager(mRootFragment.getActivity(), edFilterContent);
                    setSearchHistoryVisible(false);

                    return true;
                }
                return false;
            }
        });
        edFilterContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setSearchClearImvVisible(s.length() > 0);
            }
        });
        AppUtils.getInputSoftStatus(mRootView, new AppUtils.InputSoftStatusListener() {
            @Override
            public void onKeyBoardClose() {
                edFilterContent.setCursorVisible(false);
            }

            @Override
            public void onKeyBoardOpen() {
                edFilterContent.setCursorVisible(true);
            }
        });
        //抓拍时间筛选
        mCapturetimeFilterPopUtils.setSelectDeviceTypeItemClickListener(new FilterPopUtils.SelectFilterTypeItemClickListener() {
            @Override
            public void onSelectFilterTypeItemClick(View view, int position) {
                //隐藏搜索历史弹窗
                if(llSearchHistory.getVisibility() == View.VISIBLE ){
                    setSearchHistoryVisible(false);
                }
                //选择类型的pop点击事件
                if (position == 4) {//自定义时间
                    mPresenter.doCalendar(fgMainWarnTitleRoot);
                } else {
                    mPresenter.setFilterCapturetime(position);
                }
                setWarnFilterContent(WARN_FILTER_TIME);
                //mCapturetimeFilterPopUtils.dismiss();
            }

            @Override
            public void onDismissPop() {
                layoutCaptureTime.performClick();
            }
        });
        //处理状态筛选
        mProcessStatusFilterPopUtils.setSelectDeviceTypeItemClickListener(new FilterPopUtils.SelectFilterTypeItemClickListener() {
            @Override
            public void onSelectFilterTypeItemClick(View view, int position) {
                //隐藏搜索历史弹窗
                if(llSearchHistory.getVisibility() == View.VISIBLE ){
                    setSearchHistoryVisible(false);
                }
                //处理状态类型
                mPresenter.setFilterProcessStatus(position);
                setWarnFilterContent(WARN_FILTER_STATUS);
                //mProcessStatusFilterPopUtils.dismiss();
            }

            @Override
            public void onDismissPop() {
                layoutProcessStatus.performClick();
            }
        });


        initRcContent();
        initRcSearchHistory();
        initClearHistoryDialog();
    }


    private void initRcContent() {
        mRcContentAdapter = new CameraWarnFragRcContentAdapter(mRootFragment.getActivity());
        mRcContentAdapter.setAlarmConfirmStatusClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mRootFragment.getActivity());
        rvCameraWarnsContent.setLayoutManager(linearLayoutManager);
        rvCameraWarnsContent.setAdapter(mRcContentAdapter);
        //
        //新控件
        refreshLayout.setEnableAutoLoadMore(false);//开启自动加载功能（非必须）
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                String text = edFilterContent.getText().toString();
                mPresenter.requestSearchData(DIRECTION_DOWN);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                String text = edFilterContent.getText().toString();
                mPresenter.requestSearchData(DIRECTION_UP);
            }
        });
        rvCameraWarnsContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
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
    }

    private void initRcSearchHistory() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mRootFragment.getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvSearchHistory.setLayoutManager(layoutManager);
        rvSearchHistory.addItemDecoration(new SpacesItemDecoration(false, AppUtils.dp2px(mRootFragment.getActivity(), 6)));
        mSearchHistoryAdapter = new SearchHistoryAdapter(mRootFragment.getActivity(), new
                RecycleViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String text = mSearchHistoryAdapter.getSearchHistoryList().get(position);
                        if (!TextUtils.isEmpty(text)) {
                            edFilterContent.setText(text);
                            edFilterContent.setSelection(edFilterContent.getText().toString().length());
                        }
                        ivFilterContentClear.setVisibility(View.VISIBLE);
                        edFilterContent.clearFocus();
                        AppUtils.dismissInputMethodManager(mRootFragment.getActivity(), edFilterContent);
                        setSearchHistoryVisible(false);
                        mPresenter.save(text);
                        mPresenter.requestSearchData(DIRECTION_DOWN);
                    }
                });
        rvSearchHistory.setAdapter(mSearchHistoryAdapter);
    }


    private void initClearHistoryDialog() {
        historyClearDialog = new TipOperationDialogUtils(mRootFragment.getActivity(), true);
        historyClearDialog.setTipTitleText(getString(R.string.history_clear_all));
        historyClearDialog.setTipMessageText(getString(R.string.confirm_clear_history_record), R.color.c_a6a6a6);
        historyClearDialog.setTipCancelText(getString(R.string.cancel), getResources().getColor(R.color.c_1dbb99));
        historyClearDialog.setTipConfirmText(getString(R.string.clear), getResources().getColor(R.color.c_a6a6a6));
        historyClearDialog.setTipDialogUtilsClickListener(this);
    }


    @Override
    protected int initRootViewId() {
        return R.layout.fragment_camera_warn_list;
    }

    @Override
    protected CameraWarnListFragmentPresenter createPresenter() {
        return new CameraWarnListFragmentPresenter();
    }


    @Override
    public void onFragmentStart() {

    }

    @Override
    public void onFragmentStop() {

    }

    @Override
    public void cancelSearchData() {
        //取消搜索
        if (getSearchTextCancelVisible()) {
            edFilterContent.getText().clear();
        }
        mPresenter.doCancelSearch();
        setSearchHistoryVisible(false);
        AppUtils.dismissInputMethodManager(mRootFragment.getActivity(), edFilterContent);
    }


    @Override
    public void updateCameraWarnsListAdapter(List<SecurityAlarmInfo> securityAlarmInfoList) {
        if (securityAlarmInfoList.size() > 0) {
            mRcContentAdapter.setData(securityAlarmInfoList);
            mRcContentAdapter.notifyDataSetChanged();
        }
        try {
            LogUtils.loge("updateAlarmListAdapter-->> 刷新 " + mRcContentAdapter.getData().size());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        setNoContentVisible(securityAlarmInfoList.size() < 1);

    }

    @Override
    public void onPullRefreshComplete() {
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
    }

    @Override
    public void onPullRefreshCompleteNoMoreData() {
        refreshLayout.finishLoadMoreWithNoMoreData();
    }

    @Override
    public void setSearchButtonTextCancelVisible(boolean isVisible) {
        if (isVisible) {
            tvFilterCancal.setVisibility(View.VISIBLE);
        } else if (TextUtils.isEmpty(edFilterContent.getText().toString())) {
            tvFilterCancal.setVisibility(View.GONE);
//            setEditTextState(true);
        }

    }

    @Override
    public boolean getSearchTextCancelVisible() {
        return tvFilterCancal.getVisibility() == View.VISIBLE;

    }

    @Override
    public void setNoContentVisible(boolean isVisible) {
        rvCameraWarnsContent.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        icNoContent.setVisibility(isVisible ? View.VISIBLE : View.GONE);

    }

    @Override
    public void setSearchClearImvVisible(boolean isVisible) {
        ivFilterContentClear.setVisibility(isVisible ? View.VISIBLE : View.GONE);

    }

    @Override
    public void updateSearchHistoryList(List<String> data) {
        btnSearchClear.setVisibility(data.size() > 0 ? View.VISIBLE : View.GONE);
        mSearchHistoryAdapter.updateSearchHistoryAdapter(data);

    }

    @Override
    public void setSearchHistoryVisible(boolean isVisible) {
        llSearchHistory.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        refreshLayout.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        setSearchButtonTextCancelVisible(isVisible);
    }

    @Override
    public void showHistoryClearDialog() {
        if (historyClearDialog != null) {
            historyClearDialog.show();
        }
    }


    @Override
    public void updateFilterProcessStatusList(List<FilterModel> processStatusList) {
        mProcessStatusFilterPopUtils.updateSelectDeviceStatusList(processStatusList);

    }

    @Override
    public void updateFilterCapturetimeList(List<FilterModel> capturetimeList) {
        mCapturetimeFilterPopUtils.updateSelectDeviceStatusList(capturetimeList);

    }

    @Override
    public void setFilterCapturetimeView(FilterModel capturetimeModel) {
        if (capturetimeModel.isSpecialShow) {
            tvFilterCapturetime.setTextColor(getResources().getColor(R.color.c_a6a6a6));
            tvFilterCapturetime.setText(R.string.capture_time);
        } else {
            tvFilterCapturetime.setTextColor(getResources().getColor(R.color.c_252525));
            tvFilterCapturetime.setText(capturetimeModel.statusTitle);
        }

    }

    @Override
    public void setFilterProcessStatusView(FilterModel processStatusModel) {
        if (processStatusModel.isSpecialShow) {
            tvFilterProcessStatus.setTextColor(getResources().getColor(R.color.c_a6a6a6));
            tvFilterProcessStatus.setText(R.string.process_status);
        } else {
            tvFilterProcessStatus.setTextColor(getResources().getColor(R.color.c_252525));
            tvFilterProcessStatus.setText(processStatusModel.statusTitle);
        }

    }

    @Override
    public void showConfirmDialog(SecurityAlarmInfo securityAlarmInfo) {
        if (mSecurityWarnConfirmDialog == null) {
            mSecurityWarnConfirmDialog = new SecurityWarnConfirmDialog();
            mSecurityWarnConfirmDialog.setSecurityConfirmCallback(mPresenter);
        }
        Bundle bundle = new Bundle();
        bundle.putString(SecurityWarnConfirmDialog.EXTRA_KEY_SECURITY_ID, securityAlarmInfo.getId());
        bundle.putString(SecurityWarnConfirmDialog.EXTRA_KEY_SECURITY_TITLE, securityAlarmInfo.getTaskName());
        bundle.putString(SecurityWarnConfirmDialog.EXTRA_KEY_SECURITY_TIME, String.valueOf(securityAlarmInfo.getAlarmTime()));
        bundle.putInt(SecurityWarnConfirmDialog.EXTRA_KEY_SECURITY_TYPE, securityAlarmInfo.getAlarmType());
        mSecurityWarnConfirmDialog.setArguments(bundle);
        mSecurityWarnConfirmDialog.show(getChildFragmentManager());
    }


    @Override
    public void startAC(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void finishAc() {
        Objects.requireNonNull(mRootFragment.getActivity()).finish();
    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
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
            if (mProgressUtils != null) {
                mProgressUtils.showProgress();
            }
        }
        isShowDialog = true;

    }

    @Override
    public void dismissProgressDialog() {
        if (mProgressUtils != null) {
            mProgressUtils.dismissProgress();
        }
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(mRootFragment.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.getInstance().makeText(mRootFragment.getActivity(), msg, Toast.LENGTH_LONG).show();

    }

    /*item 预警确认*/
    @Override
    public void onConfirmStatusClick(View view, int position) {
        try {
            SecurityAlarmInfo securityAlarmInfo = mRcContentAdapter.getData().get(position);
            mPresenter.clickItemByConfirmStatus(securityAlarmInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        try {
            SecurityAlarmInfo securityAlarmInfo = mRcContentAdapter.getData().get(position);
            mPresenter.clickItem(securityAlarmInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 历史记录删除弹框 确认按键监听 取消点击
     */

    @Override
    public void onCancelClick() {
        historyClearDialog.dismiss();

    }

    /**
     * 历史记录删除弹框 确认按键监听
     */
    @Override
    public void onConfirmClick(String content, String diameter) {
        mPresenter.clearSearchHistory();
        historyClearDialog.dismiss();
    }

    @Override
    public void onDestroyView() {
        if (mRootView != null) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }

        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
        if (historyClearDialog != null) {
            historyClearDialog.destroy();
            historyClearDialog = null;
        }
        super.onDestroyView();
    }

    @OnClick({R2.id.fg_camera_warns_top_search_frame_search, R2.id.fg_camera_warns_top_search_et_search,
            R2.id.fg_camera_warns_top_search_imv_clear, R2.id.btn_search_clear,
            R2.id.tv_top_search_alarm_search_cancel, R2.id.alarm_return_top,
            R2.id.iv_search_camera_warns_status, R2.id.tv_search_camera_warns_status,
            R2.id.tv_search_camera_warns_time, R2.id.iv_search_camera_warns_time,
            R2.id.layout_filter_process_status, R2.id.layout_filter_capture_time
    })
    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.fg_camera_warns_top_search_frame_search || i == R.id.fg_camera_warns_top_search_et_search) {
            edFilterContent.requestFocus();
            edFilterContent.setCursorVisible(true);
            setSearchHistoryVisible(true);
        } else if (i == R.id.fg_camera_warns_top_search_imv_clear) {
            edFilterContent.getText().clear();
            edFilterContent.requestFocus();
            AppUtils.openInputMethodManager(mRootFragment.getActivity(), edFilterContent);
            setSearchHistoryVisible(true);
        } else if (i == R.id.btn_search_clear) {
            showHistoryClearDialog();
        } else if (i == R.id.tv_top_search_alarm_search_cancel) {
            cancelSearchData();
        } else if (i == R.id.alarm_return_top) {
            rvCameraWarnsContent.smoothScrollToPosition(0);
            mReturnTopImageView.setVisibility(View.GONE);
        } else if (i == R.id.layout_filter_capture_time || i == R.id.tv_search_camera_warns_time || i == R.id.iv_search_camera_warns_time) {
            setWarnFilterContent(WARN_FILTER_TIME);
        } else if (i == R.id.layout_filter_process_status || i == R.id.iv_search_camera_warns_status || i == R.id.tv_search_camera_warns_status) {
            setWarnFilterContent(WARN_FILTER_STATUS);
        }
    }


    /**
     * 设置 抓拍时间 处理状态
     *
     * @param filterType 0 时间 1 处理状态
     */
    private void setWarnFilterContent(int filterType) {
        if (WARN_FILTER_TIME == filterType) {
            //隐藏 状态选择弹窗
            if (mProcessStatusFilterPopUtils.isShowing()) {
                mProcessStatusFilterPopUtils.dismiss();
                ivFilterProcessStatus.setImageResource(R.drawable.ic_arrow_down);
                mPresenter.setFilterProcessStatus(-1);
            }
            //显示/隐藏 时间选择弹窗
            if (mCapturetimeFilterPopUtils.isShowing()) {
                mCapturetimeFilterPopUtils.dismiss();
                //向下箭头
                ivFilterCapturetime.setImageResource(R.drawable.ic_arrow_down);
                mPresenter.setFilterCapturetime(-1);
            } else {
                mCapturetimeFilterPopUtils.showAsDropDown(layouFilterContent);
                //标题绿色 向上箭头
                ivFilterCapturetime.setImageResource(R.drawable.ic_arrow_up);
                tvFilterCapturetime.setText(R.string.capture_time);
                tvFilterCapturetime.getPaint().setFakeBoldText(true);
                tvFilterCapturetime.setTextColor(getResources().getColor(R.color.c_1dbb99));
            }

        } else if (WARN_FILTER_STATUS == filterType) {
            //隐藏 时间选择弹窗
            if (mCapturetimeFilterPopUtils.isShowing()) {
                mCapturetimeFilterPopUtils.dismiss();
                ivFilterCapturetime.setImageResource(R.drawable.ic_arrow_down);
                mPresenter.setFilterCapturetime(-1);
            }
            //显示/隐藏 状态选择弹窗
            if (mProcessStatusFilterPopUtils.isShowing()) {
                mProcessStatusFilterPopUtils.dismiss();
                //向下箭头
                ivFilterProcessStatus.setImageResource(R.drawable.ic_arrow_down);
                mPresenter.setFilterProcessStatus(-1);
            } else {
                mProcessStatusFilterPopUtils.showAsDropDown(layouFilterContent);
                //标题绿色 向上箭头
                ivFilterProcessStatus.setImageResource(R.drawable.ic_arrow_up);
                tvFilterProcessStatus.setText(R.string.process_status);
                tvFilterProcessStatus.getPaint().setFakeBoldText(true);
                tvFilterProcessStatus.setTextColor(getResources().getColor(R.color.c_1dbb99));

            }

        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CameraWarnListFragmentPresenter.REQUEST_CODE_DETAIL && resultCode == Activity.RESULT_OK) {
            mPresenter.requestSearchData(DIRECTION_DOWN);
        }
    }
}
