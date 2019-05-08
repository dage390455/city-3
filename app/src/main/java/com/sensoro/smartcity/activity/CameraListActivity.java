package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
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
import com.sensoro.smartcity.adapter.SearchHistoryAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ICameraListActivityView;
import com.sensoro.smartcity.model.CalendarDateModel;
import com.sensoro.smartcity.model.CameraFilterModel;
import com.sensoro.smartcity.presenter.CameraListActivityPresenter;
import com.sensoro.smartcity.server.bean.DeviceCameraInfo;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SpacesItemDecoration;
import com.sensoro.smartcity.widget.dialog.TipOperationDialogUtils;
import com.sensoro.smartcity.widget.popup.CalendarPopUtils;
import com.sensoro.smartcity.widget.popup.CameraListFilterPopupWindow;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.ArrayList;
import java.util.HashMap;
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
    @BindView(R.id.rv_search_history)
    RecyclerView rvSearchHistory;
    @BindView(R.id.camera_list_ll_root)
    View mRootView;
    @BindView(R.id.ll_search_history)
    LinearLayout llSearchHistory;

    @BindView(R.id.btn_search_clear)
    ImageView btnSearchClear;
    private ProgressUtils mProgressUtils;
    private boolean isShowDialog = true;
    private DeviceCameraContentAdapter mDeviceCameraContentAdapter;
    private Animation returnTopAnimation;

    private CameraListFilterPopupWindow mCameraListFilterPopupWindow;
    //
    private List<CameraFilterModel> mCameraFilterModelList = new ArrayList<>();

    private HashMap filterHashMap = new HashMap();
    //
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private TipOperationDialogUtils historyClearDialog;

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
        cameraListIvSearchClear.setOnClickListener(this);
        cameraListEtSearch.setOnClickListener(this);
        cameraListTvSearchCancel.setOnClickListener(this);
        btnSearchClear.setOnClickListener(this);
        cameraListIvTopBack.setOnClickListener(this);
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


        mCameraListFilterPopupWindow.setDismissListener(new CameraListFilterPopupWindow.DismissListener() {
            @Override
            public void dismiss() {

                if (filterHashMap.size() == 0) {
                    cameraListIvFilter.setImageResource(R.drawable.camera_filter_unselected);
                }
            }
        });
        mCameraListFilterPopupWindow.setSelectModleListener(new CameraListFilterPopupWindow.SelectModleListener() {
            @Override
            public void selectedListener(HashMap hashMap) {


                filterHashMap.clear();
                if (null != hashMap && hashMap.size() > 0) {
                    filterHashMap.putAll(hashMap);
                    cameraListIvFilter.setImageResource(R.drawable.camera_filter_selected);
                } else {

                    mPresenter.clearMap();
                    cameraListIvFilter.setImageResource(R.drawable.camera_filter_unselected);
                }
                mPresenter.getDeviceCameraListByFilter(hashMap);
            }
        });

        cameraListEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 当按了搜索之后关闭软键盘
                    String text = cameraListEtSearch.getText().toString();
//                    if (TextUtils.isEmpty(text)) {
//                        SensoroToast.INSTANCE.makeText(mRootFragment.getActivity(), mRootFragment.getString(R.string.enter_search_content), Toast.LENGTH_SHORT).setGravity(Gravity.CENTER, 0, -10)
//                                .show();
//                        return true;
//                    }


                    mPresenter.save(text);

                    cameraListEtSearch.clearFocus();
                    filterHashMap.put("search", text);
                    mPresenter.getDeviceCameraListByFilter(filterHashMap);
                    AppUtils.dismissInputMethodManager(CameraListActivity.this, cameraListEtSearch);
                    setSearchHistoryVisible(false);

                    return true;
                }
                return false;
            }
        });
        cameraListEtSearch.addTextChangedListener(new TextWatcher() {
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
                cameraListEtSearch.setCursorVisible(false);
            }

            @Override
            public void onKeyBoardOpen() {
                cameraListEtSearch.setCursorVisible(true);
            }
        });

        initRcSearchHistory();
        initClearHistoryDialog();

    }

    private void initClearHistoryDialog() {
        historyClearDialog = new TipOperationDialogUtils(CameraListActivity.this, true);
        historyClearDialog.setTipTitleText(getString(R.string.history_clear_all));
        historyClearDialog.setTipMessageText(getString(R.string.confirm_clear_history_record), R.color.c_a6a6a6);
        historyClearDialog.setTipCancelText(getString(R.string.cancel), getResources().getColor(R.color.c_29c093));
        historyClearDialog.setTipConfirmText(getString(R.string.clear), getResources().getColor(R.color.c_a6a6a6));
        historyClearDialog.setTipDialogUtilsClickListener(new TipOperationDialogUtils.TipDialogUtilsClickListener() {
            @Override
            public void onCancelClick() {
                historyClearDialog.dismiss();

            }

            @Override
            public void onConfirmClick(String content, String diameter) {
                mPresenter.clearSearchHistory();
                historyClearDialog.dismiss();
            }
        });
    }

    private void initRcSearchHistory() {
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(CameraListActivity.this) {
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
//        int spacingInPixels = AppUtils.dp2px(mRootFragment.getActivity(),12);
        rvSearchHistory.addItemDecoration(new SpacesItemDecoration(false, AppUtils.dp2px(CameraListActivity.this, 6)));
        mSearchHistoryAdapter = new SearchHistoryAdapter(CameraListActivity.this, new
                RecycleViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String text = mSearchHistoryAdapter.getSearchHistoryList().get(position);
                        if (!TextUtils.isEmpty(text)) {
                            filterHashMap.put("search", text);
                            cameraListEtSearch.setText(text);
                            cameraListEtSearch.setSelection(cameraListEtSearch.getText().toString().length());
                        }
                        cameraListIvSearchClear.setVisibility(View.VISIBLE);
                        cameraListEtSearch.clearFocus();
                        AppUtils.dismissInputMethodManager(CameraListActivity.this, cameraListEtSearch);
                        setSearchHistoryVisible(false);
                        mPresenter.save(text);
                        mPresenter.getDeviceCameraListByFilter(filterHashMap);
                    }
                });
        rvSearchHistory.setAdapter(mSearchHistoryAdapter);
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
    public void setSearchClearImvVisible(boolean isVisible) {
        cameraListIvSearchClear.setVisibility(isVisible ? View.VISIBLE : View.GONE);
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
    public void updateSearchHistoryList(List<String> data) {
        btnSearchClear.setVisibility(data.size() > 0 ? View.VISIBLE : View.GONE);
        mSearchHistoryAdapter.updateSearchHistoryAdapter(data);
    }


    @Override
    public void setSearchHistoryVisible(boolean isVisible) {
        llSearchHistory.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        refreshLayout.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        setSearchButtonTextVisible(isVisible);
    }

    @Override
    public void setSearchButtonTextVisible(boolean isVisible) {
        if (isVisible) {
            cameraListTvSearchCancel.setVisibility(View.VISIBLE);
//            setEditTextState(false);
//            AppUtils.dismissInputMethodManager(mRootFragment.getActivity(), fgMainWarnEtSearch);
        } else if (TextUtils.isEmpty(cameraListEtSearch.getText().toString())) {
            cameraListTvSearchCancel.setVisibility(View.GONE);
//            setEditTextState(true);
        }

    }

    @Override
    public void resetRefreshNoMoreData() {
        if (refreshLayout!= null) {
            refreshLayout.setNoMoreData(false);
        }
    }

    @Override
    public void showHistoryClearDialog() {
        if (historyClearDialog != null) {
            historyClearDialog.show();
        }
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
                AppUtils.dismissInputMethodManager(mActivity, cameraListEtSearch);
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


            case R.id.camera_list_et_search:

                if (mCameraListFilterPopupWindow.isShowing()) {

                    mCameraListFilterPopupWindow.dismiss();

                }

                cameraListEtSearch.requestFocus();
                cameraListEtSearch.setCursorVisible(true);
                setSearchHistoryVisible(true);
//                forceOpenSoftKeyboard();
                break;
            case R.id.camera_list_iv_search_clear:
                cameraListEtSearch.setText("");
                cameraListEtSearch.requestFocus();
                AppUtils.openInputMethodManager(CameraListActivity.this, cameraListEtSearch);
                setSearchHistoryVisible(true);
                break;


            case R.id.camera_list_tv_search_cancel:

                if (cameraListTvSearchCancel.getVisibility() == View.VISIBLE) {
                    cameraListEtSearch.getText().clear();
                }
                filterHashMap.remove("search");
                mPresenter.getDeviceCameraListByFilter(filterHashMap);
                setSearchHistoryVisible(false);
                AppUtils.dismissInputMethodManager(CameraListActivity.this, cameraListEtSearch);
                break;

            case R.id.btn_search_clear:
                showHistoryClearDialog();
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
