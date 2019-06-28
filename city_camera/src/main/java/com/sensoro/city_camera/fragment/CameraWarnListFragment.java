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
import com.sensoro.city_camera.presenter.CameraWarnListFragmentPresenter;
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

import static com.sensoro.city_camera.presenter.CameraWarnListFragmentPresenter.FILTER_STATUS_ALL;
import static com.sensoro.city_camera.presenter.CameraWarnListFragmentPresenter.FILTER_STATUS_EFFECTIVE;
import static com.sensoro.city_camera.presenter.CameraWarnListFragmentPresenter.FILTER_STATUS_INVALID;
import static com.sensoro.city_camera.presenter.CameraWarnListFragmentPresenter.FILTER_STATUS_UNPROCESS;
import static com.sensoro.city_camera.presenter.CameraWarnListFragmentPresenter.FILTER_TIME_24H;
import static com.sensoro.city_camera.presenter.CameraWarnListFragmentPresenter.FILTER_TIME_3DAY;
import static com.sensoro.city_camera.presenter.CameraWarnListFragmentPresenter.FILTER_TIME_7DAY;
import static com.sensoro.city_camera.presenter.CameraWarnListFragmentPresenter.FILTER_TIME_ALL;
import static com.sensoro.common.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.common.constant.Constants.DIRECTION_UP;

/**
 * @author wangqinghao
 */
@Route(path = ARouterConstants.FRAGMENT_CAMERA_WARN_LIST)
public class CameraWarnListFragment extends BaseFragment<ICameraWarnListFragmentView, CameraWarnListFragmentPresenter>
        implements ICameraWarnListFragmentView, CameraWarnFragRcContentAdapter.CameraWarnConfirmStatusClickListener, TipOperationDialogUtils.TipDialogUtilsClickListener {

    @BindView(R2.id.fg_camera_warns_top_search_title_root)
    LinearLayout fgMainWarnTitleRoot;
    @BindView(R2.id.fg_camera_warns_top_search_et_search)
    EditText edFilterContent;
    @BindView(R2.id.fg_camera_warns_top_search_imv_clear)
    ImageView ivFilterContentClear;
    @BindView(R2.id.tv_top_search_alarm_search_cancel)
    TextView tvFilterCancal;
    @BindView(R2.id.tv_search_camera_warns_time)
    TextView tvFilterCapturetime;
    @BindView(R2.id.iv_search_camera_warns_time)
    ImageView ivFilterCapturetime;
    @BindView(R2.id.tv_search_camera_warns_status)
    TextView tvFilterProcessStatus;
    @BindView(R2.id.iv_search_camera_warns_status)
    ImageView ivFilterProcessStatus;
    //抓拍时间
    @BindView(R2.id.layout_camerawarns_time_filter_content)
    LinearLayout layoutFilterCapturetimeContent;
    @BindView(R2.id.tv_search_camera_warns_time_Unlimited)
    TextView tvFilterCapturetimeUnlimited;
    @BindView(R2.id.tv_search_camera_warns_time_24h)
    TextView tvFilterCapturetime24h;
    @BindView(R2.id.tv_search_camera_warns_time_3day)
    TextView tvFilterCapturetime3Days;
    @BindView(R2.id.tv_search_camera_warns_time_7day)
    TextView tvFilterCapturetime7Days;
    @BindView(R2.id.tv_search_camera_warns_time_customizetime)
    TextView tvFilterCapturetimeCustomize;
    //处理状态
    @BindView(R2.id.layout_camerawarns_status_filter_content)
    LinearLayout layoutFilterStatusContent;
    @BindView(R2.id.tv_search_camera_warns_status_unlimited)
    TextView tvFilterStatusUnlimited;
    @BindView(R2.id.tv_search_status_unprocessed)
    TextView tvFilterStatusUnprocessed;
    @BindView(R2.id.tv_search_status_effective_warn)
    TextView tvFilterStatusEffective;
    @BindView(R2.id.tv_search_status_invalid_warn)
    TextView tvFilterStatusInvalid;

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
    private static final String TAG = "wqh_Test";
    private SecurityWarnConfirmDialog mSecurityWarnConfirmDialog;


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
        Log.d(TAG, "updateCameraWarnsListAdapter: " + securityAlarmInfoList.size());
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

    /**
     * 设置抓拍事件过滤条件
     * @param strDataRange
     */
    @Override
    public void setCustomizeCaptureTime(String strDataRange) {

        tvFilterCapturetimeCustomize.setText(strDataRange);
        setFilterTvStyle(tvFilterCapturetimeCustomize,true);

        tvFilterCapturetime.setText(strDataRange);
        setFilterTvStyle(tvFilterCapturetime,true);

    }

    @Override
    public void initFilterView() {
        edFilterContent.setText("");
        //抓拍时间
        setFilterTvStyle(tvFilterCapturetimeUnlimited,false);
        setFilterTvStyle(tvFilterCapturetime24h,false);
        setFilterTvStyle(tvFilterCapturetime3Days,false);
        setFilterTvStyle(tvFilterCapturetime7Days,false);
        tvFilterCapturetimeCustomize.setText(R.string.customize_time);
        setFilterTvStyle(tvFilterCapturetimeUnlimited,true);

        //处理状态
        setFilterTvStyle(tvFilterStatusUnlimited,false);
        setFilterTvStyle(tvFilterStatusUnprocessed,true);
        setFilterTvStyle(tvFilterStatusEffective,false);
        setFilterTvStyle(tvFilterStatusInvalid,false);
        //设置标题
        setFilterTvStyle(tvFilterProcessStatus,true);
        tvFilterProcessStatus.setText(tvFilterStatusUnprocessed.getText());

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
            R2.id.layout_camerawarns_status_filter_content, R2.id.tv_search_camera_warns_status_unlimited,
            R2.id.tv_search_status_unprocessed, R2.id.tv_search_status_effective_warn,
            R2.id.tv_search_status_invalid_warn, R2.id.layout_camerawarns_time_filter_content,
            R2.id.tv_search_camera_warns_time_Unlimited,
            R2.id.tv_search_camera_warns_time_24h, R2.id.tv_search_camera_warns_time_3day,
            R2.id.tv_search_camera_warns_time_7day, R2.id.tv_search_camera_warns_time_customizetime,
            R2.id.iv_search_camera_warns_status, R2.id.tv_search_camera_warns_status,
            R2.id.tv_search_camera_warns_time, R2.id.iv_search_camera_warns_time
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
        } else if (i == R.id.tv_search_camera_warns_time || i == R.id.iv_search_camera_warns_time) {
            setWarnFilterContent(WARN_FILTER_STATUS, false);
            setWarnFilterContent(WARN_FILTER_TIME, layoutFilterCapturetimeContent.getVisibility() == View.GONE);
        } else if (i == R.id.iv_search_camera_warns_status || i == R.id.tv_search_camera_warns_status) {
            setWarnFilterContent(WARN_FILTER_TIME, false);
            setWarnFilterContent(WARN_FILTER_STATUS, layoutFilterStatusContent.getVisibility() == View.GONE);

            //抓拍时间筛选
        } else if (i == R.id.tv_search_camera_warns_time_customizetime) {
            mPresenter.doCalendar(fgMainWarnTitleRoot);
            setFilterTimeDefault();
            setWarnFilterContent(WARN_FILTER_TIME, false);
        }else if(i == R.id.tv_search_camera_warns_time_Unlimited){
            mPresenter.filterDataByTime(FILTER_TIME_ALL);
            setFilterTimeDefault();
            setFilterTvStyle(tvFilterCapturetimeUnlimited,true);
            setWarnFilterContent(WARN_FILTER_TIME, false);
            //抓拍时间 选择栏标题设置
            setFilterTvStyle(tvFilterCapturetime,false);
            tvFilterCapturetime.setText(R.string.capture_time);

        }else if(i == R.id.tv_search_camera_warns_time_24h){
            mPresenter.filterDataByTime(FILTER_TIME_24H);
            setFilterTimeDefault();
            setFilterTvStyle(tvFilterCapturetime24h,true);
            setWarnFilterContent(WARN_FILTER_TIME, false);

            //抓拍时间 选择栏标题设置
            setFilterTvStyle(tvFilterCapturetime,true);
            tvFilterCapturetime.setText(R.string.twentyfour_hours);


        }else if(i == R.id.tv_search_camera_warns_time_3day){
            mPresenter.filterDataByTime(FILTER_TIME_3DAY);
            setFilterTimeDefault();
            setFilterTvStyle(tvFilterCapturetime3Days,true);
            setWarnFilterContent(WARN_FILTER_TIME, false);
            //抓拍时间 选择栏标题设置
            setFilterTvStyle(tvFilterCapturetime,true);
            tvFilterCapturetime.setText(R.string.three_days);
        }else if(i == R.id.tv_search_camera_warns_time_7day){
            mPresenter.filterDataByTime(FILTER_TIME_7DAY);
            setFilterTimeDefault();
            setFilterTvStyle(tvFilterCapturetime7Days,true);
            setWarnFilterContent(WARN_FILTER_TIME, false);
            //抓拍时间 选择栏标题设置
            setFilterTvStyle(tvFilterCapturetime,true);
            tvFilterCapturetime.setText(R.string.seven_days);
        }//处理状态
        else if(i == R.id.tv_search_camera_warns_status_unlimited){
            mPresenter.filterDataByStatus(FILTER_STATUS_ALL);
            setFilterStatusDefault();
            setFilterTvStyle(tvFilterStatusUnlimited,true);
            setWarnFilterContent(WARN_FILTER_STATUS, false);
            setWarnFilterContent(WARN_FILTER_TIME, false);
            //处理状态 选择栏标题设置
            setFilterTvStyle(tvFilterProcessStatus,false);
            tvFilterProcessStatus.setText(R.string.process_status);
        }else if(i == R.id.tv_search_status_unprocessed){
            mPresenter.filterDataByStatus(FILTER_STATUS_UNPROCESS);
            setFilterStatusDefault();
            setFilterTvStyle(tvFilterStatusUnprocessed,true);
            setWarnFilterContent(WARN_FILTER_STATUS, false);
            //处理状态 选择栏标题设置
            setFilterTvStyle(tvFilterProcessStatus,true);
            tvFilterProcessStatus.setText(R.string.unprocessed);
        }else if(i == R.id.tv_search_status_effective_warn){
            mPresenter.filterDataByStatus(FILTER_STATUS_EFFECTIVE);
            setFilterStatusDefault();
            setFilterTvStyle(tvFilterStatusEffective,true);
            setWarnFilterContent(WARN_FILTER_STATUS, false);
            //处理状态 选择栏标题设置
            setFilterTvStyle(tvFilterProcessStatus,true);
            tvFilterProcessStatus.setText(R.string.effective_warn);
        }else if(i == R.id.tv_search_status_invalid_warn){
            mPresenter.filterDataByStatus(FILTER_STATUS_INVALID);
            setFilterStatusDefault();
            setFilterTvStyle(tvFilterStatusInvalid,true);
            setWarnFilterContent(WARN_FILTER_STATUS, false);
            //处理状态 选择栏标题设置
            setFilterTvStyle(tvFilterProcessStatus,true);
            tvFilterProcessStatus.setText(R.string.invalid_warn);
        }

    }



    public static final int WARN_FILTER_TIME = 0;
    public static final int WARN_FILTER_STATUS = 1;


    /**
     * @param filterType 0 时间 1 处理状态
     * @param isVisiable true 显示 false 隐藏
     */
    private void setWarnFilterContent(int filterType, boolean isVisiable) {
        if (WARN_FILTER_TIME == filterType) {
            layoutFilterCapturetimeContent.setVisibility(isVisiable ? View.VISIBLE : View.GONE);
            ivFilterCapturetime.setImageResource(isVisiable ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);


        } else if (WARN_FILTER_STATUS == filterType) {
            layoutFilterStatusContent.setVisibility(isVisiable ? View.VISIBLE : View.GONE);
            ivFilterProcessStatus.setImageResource(isVisiable ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);

        }


    }

    /**
     * 设置自定义时间显示文字
     */
    public void setFilterCustomText(String dateRangeStr){
        tvFilterCapturetimeCustomize.setText(dateRangeStr);
        setFilterTvStyle(tvFilterCapturetimeCustomize,true);
    }

    /**
     * 抓拍时间初始化
     */
    public void setFilterTimeDefault() {
        //抓拍时间
        setFilterTvStyle(tvFilterCapturetimeUnlimited,false);
        setFilterTvStyle(tvFilterCapturetime24h,false);
        setFilterTvStyle(tvFilterCapturetime3Days,false);
        setFilterTvStyle(tvFilterCapturetime7Days,false);
        tvFilterCapturetimeCustomize.setText(R.string.customize_time);
        setFilterTvStyle(tvFilterCapturetimeCustomize,false);
    }
    /**
     * 抓拍状态初始化
     */
    public void setFilterStatusDefault() {
        //处理状态
        setFilterTvStyle(tvFilterStatusUnlimited,false);
        setFilterTvStyle(tvFilterStatusUnprocessed,false);
        setFilterTvStyle(tvFilterStatusEffective,false);
        setFilterTvStyle(tvFilterStatusInvalid,false);
    }
    /**
     * 设置选择前后字体样式
     * @param tv
     * @param isBold
     */
    public void setFilterTvStyle(TextView tv, boolean isBold){
        tv.getPaint().setFakeBoldText(isBold);
        tv.setTextColor(isBold?getResources().getColor(R.color.c_252525):getResources().getColor(R.color.c_a6a6a6));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CameraWarnListFragmentPresenter.REQUEST_CODE_DETAIL && resultCode == Activity.RESULT_OK){
            mPresenter.requestSearchData(DIRECTION_DOWN);
        }
    }
}
