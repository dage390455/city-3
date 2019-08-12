package com.sensoro.smartcity.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.common.adapter.SearchHistoryAdapter;
import com.sensoro.common.base.BaseFragment;
import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.common.server.bean.DeviceAlarmLogInfo;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.SpacesItemDecoration;
import com.sensoro.common.widgets.TipOperationDialogUtils;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.MainWarnFragRcContentAdapter;
import com.sensoro.smartcity.imainviews.IWarnFragmentView;
import com.sensoro.smartcity.model.AlarmPopupModel;
import com.sensoro.smartcity.presenter.WarnFragmentPresenter;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.SensoroXLinearLayoutManager;
import com.sensoro.smartcity.widget.popup.AlarmPopUtils;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

import static com.sensoro.common.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.common.constant.Constants.DIRECTION_UP;

@Route(path = ARouterConstants.FRAGMENT_FIRE_WARN_FRAGMENT)
public class WarnFragment extends BaseFragment<IWarnFragmentView, WarnFragmentPresenter> implements
        IWarnFragmentView, MainWarnFragRcContentAdapter.AlarmConfirmStatusClickListener, TipOperationDialogUtils.TipDialogUtilsClickListener {
    @BindView(R.id.fg_main_top_search_title_root)
    LinearLayout fgMainWarnTitleRoot;
    @BindView(R.id.fg_main_top_search_frame_search)
    RelativeLayout fgMainWarnFrameSearch;
    @BindView(R.id.fg_main_top_search_et_search)
    EditText fgMainWarnEtSearch;
    @BindView(R.id.fg_main_top_search_imv_calendar)
    ImageView fgMainWarnImvCalendar;
    @BindView(R.id.fg_main_warn_rc_content)
    RecyclerView fgMainWarnRcContent;
    @BindView(R.id.tv_top_search_alarm_search_cancel)
    TextView tvWarnAlarmSearchCancel;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.fg_main_top_search_tv_date_edit)
    TextView fgMainWarnTvDateEdit;
    @BindView(R.id.fg_main_warn_top_search_date_close)
    ImageView fgMainWarnImvDateClose;
    @BindView(R.id.fg_main_top_search_rl_date_edit)
    RelativeLayout fgMainWarnRlDateEdit;
    @BindView(R.id.alarm_return_top)
    ImageView mReturnTopImageView;
    View icNoContent;
    @BindView(R.id.fg_main_top_search_imv_clear)
    ImageView fgMainWarnFragmentImvClear;
    @BindView(R.id.rv_search_history)
    RecyclerView rvSearchHistory;
    @BindView(R.id.btn_search_clear)
    ImageView btnSearchClear;
    @BindView(R.id.ll_search_history)
    LinearLayout llSearchHistory;
    private MainWarnFragRcContentAdapter mRcContentAdapter;
    private boolean isShowDialog = true;
    private ProgressUtils mProgressUtils;
    private AlarmPopUtils mAlarmPopUtils;
    private Animation returnTopAnimation;
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private TipOperationDialogUtils historyClearDialog;

    @Override
    protected void initData(Context activity) {
        initView();
        mPresenter.initData(activity);
//        if (PreferencesHelper.getInstance().getUserData().hasMonitorTaskList) {
//            //如果有布控权限，去除顶部的padding
        fgMainWarnTitleRoot.setPadding(0, 0, 0, 0);
//        } else {
//            fgMainWarnTitleRoot.setPadding(0, AppUtils.dp2px(mRootFragment.getActivity(), 20), 0, 0);
//        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        icNoContent = LayoutInflater.from(getActivity()).inflate(R.layout.no_content, null);
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mRootFragment.getActivity()).build());
        mAlarmPopUtils = new AlarmPopUtils(mRootFragment.getActivity());
        mAlarmPopUtils.setOnPopupCallbackListener(mPresenter);
        returnTopAnimation = AnimationUtils.loadAnimation(mRootFragment.getContext(), R.anim.return_top_in_anim);
        mReturnTopImageView.setAnimation(returnTopAnimation);
        mReturnTopImageView.setVisibility(View.GONE);
        fgMainWarnEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 当按了搜索之后关闭软键盘
                    String text = fgMainWarnEtSearch.getText().toString();
//                    if (TextUtils.isEmpty(text)) {
//                        SensoroToast.INSTANCE.makeText(mRootFragment.getActivity(), mRootFragment.getString(R.string.enter_search_content), Toast.LENGTH_SHORT).setGravity(Gravity.CENTER, 0, -10)
//                                .show();
//                        return true;
//                    }
                    mPresenter.save(text);
                    fgMainWarnEtSearch.clearFocus();
                    mPresenter.requestSearchData(DIRECTION_DOWN, text);
                    AppUtils.dismissInputMethodManager(mRootFragment.getActivity(), fgMainWarnEtSearch);
                    setSearchHistoryVisible(false);

                    return true;
                }
                return false;
            }
        });
        fgMainWarnEtSearch.addTextChangedListener(new TextWatcher() {
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
        fgMainWarnEtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                fgMainWarnEtSearch.requestFocus();
                fgMainWarnEtSearch.setCursorVisible(true);
                setSearchHistoryVisible(true);
                return false;
            }
        });

        initRcContent();

        initRcSearchHistory();


        AppUtils.getInputSoftStatus(mRootView, new AppUtils.InputSoftStatusListener() {
            @Override
            public void onKeyBoardClose() {
                fgMainWarnEtSearch.setCursorVisible(false);
            }

            @Override
            public void onKeyBoardOpen() {
                fgMainWarnEtSearch.setCursorVisible(true);
            }
        });

        initClearHistoryDialog();
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
    public void setSearchHistoryVisible(boolean isVisible) {
        llSearchHistory.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        refreshLayout.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        setSearchButtonTextVisible(isVisible);
    }

    @Override
    public void showHistoryClearDialog() {
        if (historyClearDialog != null) {
            historyClearDialog.show();
        }
    }

    @Override
    public void dismissInput() {
        AppUtils.dismissInputMethodManager(mRootFragment.getActivity(), fgMainWarnEtSearch, false);
    }

    private void initRcSearchHistory() {
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mRootFragment.getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rvSearchHistory.setLayoutManager(layoutManager);
//        int spacingInPixels = AppUtils.dp2px(mRootFragment.getActivity(),12);
        rvSearchHistory.addItemDecoration(new SpacesItemDecoration(false, AppUtils.dp2px(mRootFragment.getActivity(), 6)));
        mSearchHistoryAdapter = new SearchHistoryAdapter(mRootFragment.getActivity(), new
                RecycleViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String text = mSearchHistoryAdapter.getSearchHistoryList().get(position);
                        if (!TextUtils.isEmpty(text)) {
                            fgMainWarnEtSearch.setText(text);
                            fgMainWarnEtSearch.setSelection(fgMainWarnEtSearch.getText().toString().length());
                        }
                        fgMainWarnFragmentImvClear.setVisibility(View.VISIBLE);
                        fgMainWarnEtSearch.clearFocus();
                        AppUtils.dismissInputMethodManager(mRootFragment.getActivity(), fgMainWarnEtSearch);
                        setSearchHistoryVisible(false);
                        mPresenter.save(text);
                        mPresenter.requestSearchData(DIRECTION_DOWN, text);
                    }
                });
        rvSearchHistory.setAdapter(mSearchHistoryAdapter);
    }

    @Override
    public void setSearchClearImvVisible(boolean isVisible) {
        fgMainWarnFragmentImvClear.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateSearchHistoryList(List<String> data) {
        btnSearchClear.setVisibility(data.size() > 0 ? View.VISIBLE : View.GONE);
        mSearchHistoryAdapter.updateSearchHistoryAdapter(data);
    }

    private void setEditTextState(boolean canEdit) {
        if (canEdit) {
            fgMainWarnEtSearch.setFocusableInTouchMode(true);
            fgMainWarnEtSearch.setFocusable(true);
            fgMainWarnEtSearch.requestFocus();
        } else {
            fgMainWarnEtSearch.setFocusable(false);
            fgMainWarnEtSearch.setFocusableInTouchMode(false);
        }
    }


    public void forceOpenSoftKeyboard() {
        fgMainWarnEtSearch.setCursorVisible(true);
        InputMethodManager imm = (InputMethodManager) mRootFragment.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected int initRootViewId() {
        return R.layout.fragment_main_warn;
    }

    @Override
    protected WarnFragmentPresenter createPresenter() {
        return new WarnFragmentPresenter();
    }

    @Override
    public void startAC(Intent intent) {
        Objects.requireNonNull(mRootFragment.getActivity()).startActivity(intent);
    }

    @Override
    public void finishAc() {
        Objects.requireNonNull(mRootFragment.getActivity()).finish();
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
    public void onFragmentStart() {
    }

    @Override
    public void onFragmentStop() {
        dismissInput();
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

    }

    private void initRcContent() {
        mRcContentAdapter = new MainWarnFragRcContentAdapter(mRootFragment.getActivity());
        mRcContentAdapter.setAlarmConfirmStatusClickListener(this);
        final SensoroXLinearLayoutManager xLinearLayoutManager = new SensoroXLinearLayoutManager(mRootFragment.getActivity());
        xLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fgMainWarnRcContent.setLayoutManager(xLinearLayoutManager);
        fgMainWarnRcContent.setAdapter(mRcContentAdapter);
        //
        //新控件
        refreshLayout.setEnableAutoLoadMore(false);//开启自动加载功能（非必须）
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                String text = fgMainWarnEtSearch.getText().toString();
                mPresenter.requestSearchData(DIRECTION_DOWN, text);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                String text = fgMainWarnEtSearch.getText().toString();
                mPresenter.requestSearchData(DIRECTION_UP, text);
            }
        });
        //
        fgMainWarnRcContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if (xLinearLayoutManager.findFirstVisibleItemPosition() == 0 && newState == SCROLL_STATE_IDLE &&
//                        toolbarDirection == DIRECTION_DOWN) {
////                    mListRecyclerView.setre
//                }
                if (xLinearLayoutManager.findFirstVisibleItemPosition() > 4) {
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

    @Override
    public void onDestroyView() {
        if (returnTopAnimation != null) {
            returnTopAnimation.cancel();
            returnTopAnimation = null;
        }

        if (mRootView != null) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }

        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
        if (mAlarmPopUtils != null) {
            mAlarmPopUtils.onDestroyPop();
        }

        if (historyClearDialog != null) {
            historyClearDialog.destroy();
            historyClearDialog = null;
        }
//        if (mAlarmPopupView != null) {
//            mAlarmPopupView.onDestroyPop();
//        }
//        if (mListRecyclerView != null) {
//            mListRecyclerView.destroy();
//        }
//        if (mGridRecyclerView != null) {
//            mGridRecyclerView.destroy();
//        }

        super.onDestroyView();
    }

    @OnClick({R.id.fg_main_top_search_imv_calendar, R.id.fg_main_warn_top_search_date_close,
            R.id.tv_top_search_alarm_search_cancel, R.id.alarm_return_top, R.id.fg_main_top_search_imv_clear, R.id.btn_search_clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.fg_main_top_search_imv_clear:
                fgMainWarnEtSearch.getText().clear();
                fgMainWarnEtSearch.requestFocus();
                AppUtils.openInputMethodManager(mRootFragment.getActivity(), fgMainWarnEtSearch);
                setSearchHistoryVisible(true);
                break;
            case R.id.btn_search_clear:
                showHistoryClearDialog();
                break;
            case R.id.fg_main_top_search_imv_calendar:
                mPresenter.doCalendar(fgMainWarnTitleRoot);
                AppUtils.dismissInputMethodManager(mRootFragment.getActivity(), fgMainWarnEtSearch);
                break;
            case R.id.fg_main_warn_top_search_date_close:
                fgMainWarnRlDateEdit.setVisibility(View.GONE);
                String text = fgMainWarnEtSearch.getText().toString();
                setSearchHistoryVisible(false);
                AppUtils.dismissInputMethodManager(mRootFragment.getActivity(), fgMainWarnEtSearch);
                mPresenter.requestSearchData(DIRECTION_DOWN, text);
                break;
            case R.id.tv_top_search_alarm_search_cancel:
                cancelSearchData();
                break;
            case R.id.alarm_return_top:
                fgMainWarnRcContent.smoothScrollToPosition(0);
                mReturnTopImageView.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void cancelSearchData() {
        doCancelSearch();
        setSearchHistoryVisible(false);
        AppUtils.dismissInputMethodManager(mRootFragment.getActivity(), fgMainWarnEtSearch);
    }


    private void doCancelSearch() {
        if (getSearchTextVisible()) {
            fgMainWarnEtSearch.getText().clear();
        }
        mPresenter.doCancelSearch();
    }

    @Override
    public void updateAlarmListAdapter(List<DeviceAlarmLogInfo> deviceAlarmLogInfoList) {
        if (deviceAlarmLogInfoList.size() > 0) {
            mRcContentAdapter.setData(deviceAlarmLogInfoList);
            mRcContentAdapter.notifyDataSetChanged();
        }
        try {
            LogUtils.loge("updateAlarmListAdapter-->> 刷新 " + mRcContentAdapter.getData().size());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        setNoContentVisible(deviceAlarmLogInfoList.size() < 1);

    }

    @Override
    public void showAlarmPopupView(AlarmPopupModel alarmPopupModel) {
        mAlarmPopUtils.show(alarmPopupModel);
    }

    @Override
    public void dismissAlarmPopupView() {
        mAlarmPopUtils.dismiss();
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
    public void setUpdateButtonClickable(boolean canClick) {
        mAlarmPopUtils.setUpdateButtonClickable(canClick);
    }

    @Override
    public void setSelectedDateLayoutVisible(boolean b) {
        fgMainWarnRlDateEdit.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setSelectedDateSearchText(String s) {
        fgMainWarnTvDateEdit.setText(s);
    }

    @Override
    public boolean isSelectedDateLayoutVisible() {
        return fgMainWarnRlDateEdit.getVisibility() == View.VISIBLE;
    }

    @Override
    public void setSearchButtonTextVisible(boolean isVisible) {
        if (isVisible) {
            tvWarnAlarmSearchCancel.setVisibility(View.VISIBLE);
//            setEditTextState(false);
//            AppUtils.dismissInputMethodManager(mRootFragment.getActivity(), fgMainWarnEtSearch);
        } else if (TextUtils.isEmpty(fgMainWarnEtSearch.getText().toString())) {
            tvWarnAlarmSearchCancel.setVisibility(View.GONE);
//            setEditTextState(true);
        }

    }


    @Override
    public boolean getSearchTextVisible() {
        return tvWarnAlarmSearchCancel.getVisibility() == View.VISIBLE;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setNoContentVisible(boolean isVisible) {

        RefreshHeader refreshHeader = refreshLayout.getRefreshHeader();
        if (refreshHeader != null) {
            if (isVisible) {
                refreshHeader.setPrimaryColors(getResources().getColor(R.color.c_f4f4f4));
            } else {
                refreshHeader.setPrimaryColors(getResources().getColor(R.color.white));
            }
        }

        if (isVisible) {
            refreshLayout.setRefreshContent(icNoContent);
        } else {
            refreshLayout.setRefreshContent(fgMainWarnRcContent);
        }
    }

    @Override
    public void onConfirmStatusClick(View view, int position, boolean isReConfirm) {
        try {
            DeviceAlarmLogInfo deviceAlarmLogInfo = mRcContentAdapter.getData().get(position);
            mPresenter.clickItemByConfirmStatus(deviceAlarmLogInfo, isReConfirm);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCallPhone(View v, int position) {
        try {
            DeviceAlarmLogInfo deviceAlarmLogInfo = mRcContentAdapter.getData().get(position);
            mPresenter.doContactOwner(deviceAlarmLogInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(View view, int position, boolean isReConfirm) {
        try {
            DeviceAlarmLogInfo deviceAlarmLogInfo = mRcContentAdapter.getData().get(position);
            mPresenter.clickItem(deviceAlarmLogInfo, isReConfirm);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCancelClick() {
        historyClearDialog.dismiss();
    }

    @Override
    public void onConfirmClick(String content, String diameter) {
        mPresenter.clearSearchHistory();
        historyClearDialog.dismiss();
    }
}
