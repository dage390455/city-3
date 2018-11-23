package com.sensoro.smartcity.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
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

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.MainWarnFragRcContentAdapter;
import com.sensoro.smartcity.adapter.SearchHistoryAdapter;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IWarnFragmentView;
import com.sensoro.smartcity.presenter.WarnFragmentPresenter;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SensoroToast;
import com.sensoro.smartcity.widget.SensoroXLinearLayoutManager;
import com.sensoro.smartcity.widget.SpacesItemDecoration;
import com.sensoro.smartcity.widget.popup.AlarmPopUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.sensoro.smartcity.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.smartcity.constant.Constants.DIRECTION_UP;

public class WarnFragment extends BaseFragment<IWarnFragmentView, WarnFragmentPresenter> implements
        IWarnFragmentView, MainWarnFragRcContentAdapter.AlarmConfirmStatusClickListener {
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
    @BindView(R.id.no_content)
    ImageView imvNoContent;
    @BindView(R.id.ic_no_content)
    LinearLayout icNoContent;
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

    @Override
    protected void initData(Context activity) {
        initView();
        mPresenter.initData(activity);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
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
                setSearchClearImvVisible(s.length()>0);
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
    }

    @Override
    public void setSearchHistoryVisible(boolean isVisible) {
        llSearchHistory.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        refreshLayout.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        setSearchButtonTextVisible(isVisible);
    }

    private void initRcSearchHistory() {
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mRootFragment.getActivity()){
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
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.x10);
        rvSearchHistory.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
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
                        AppUtils.dismissInputMethodManager(mRootFragment.getActivity(),fgMainWarnEtSearch);
                        setSearchHistoryVisible(false);
                        mPresenter.requestSearchData(DIRECTION_DOWN,text);
                    }
                });
        rvSearchHistory.setAdapter(mSearchHistoryAdapter);
    }

    @Override
    public void setSearchClearImvVisible(boolean isVisible) {
        fgMainWarnFragmentImvClear.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void UpdateSearchHistoryList(List<String> data) {
        btnSearchClear.setVisibility(data.size() >0 ? View.VISIBLE : View.GONE);
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
        mRootFragment.getActivity().startActivity(intent);
    }

    @Override
    public void finishAc() {
        mRootFragment.getActivity().finish();
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
        SensoroToast.INSTANCE.makeText(mRootFragment.getActivity(), msg, Toast.LENGTH_SHORT).show();
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
                        if (returnTopAnimation.hasEnded()) {
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

    @OnClick({R.id.fg_main_top_search_frame_search, R.id.fg_main_top_search_et_search, R.id.fg_main_top_search_imv_calendar, R.id.fg_main_warn_top_search_date_close,
            R.id.tv_top_search_alarm_search_cancel, R.id.alarm_return_top,R.id.fg_main_top_search_imv_clear,R.id.btn_search_clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fg_main_top_search_frame_search:
            case R.id.fg_main_top_search_et_search:
                fgMainWarnEtSearch.requestFocus();
                fgMainWarnEtSearch.setCursorVisible(true);
                setSearchHistoryVisible(true);
//                forceOpenSoftKeyboard();
                break;
            case R.id.fg_main_top_search_imv_clear:
                fgMainWarnEtSearch.getText().clear();
                fgMainWarnEtSearch.requestFocus();
                AppUtils.openInputMethodManager(mRootFragment.getActivity(),fgMainWarnEtSearch);
                setSearchHistoryVisible(true);
                break;
            case R.id.btn_search_clear:
                mPresenter.clearSearchHistory();
                break;
            case R.id.fg_main_top_search_imv_calendar:
                mPresenter.doCalendar(fgMainWarnTitleRoot);
                AppUtils.dismissInputMethodManager(mRootFragment.getActivity(),fgMainWarnEtSearch);
                break;
            case R.id.fg_main_warn_top_search_date_close:
                fgMainWarnRlDateEdit.setVisibility(View.GONE);
                String text = fgMainWarnEtSearch.getText().toString();
                setSearchHistoryVisible(false);
                AppUtils.dismissInputMethodManager(mRootFragment.getActivity(),fgMainWarnEtSearch);
                mPresenter.requestSearchData(DIRECTION_DOWN, text);
                break;
            case R.id.tv_top_search_alarm_search_cancel:
                doCancelSearch();
                setSearchHistoryVisible(false);
                AppUtils.dismissInputMethodManager(mRootFragment.getActivity(),fgMainWarnEtSearch);
                break;
            case R.id.alarm_return_top:
                fgMainWarnRcContent.smoothScrollToPosition(0);
                mReturnTopImageView.setVisibility(View.GONE);
                break;
        }
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
        LogUtils.loge("updateAlarmListAdapter-->> 刷新 " + mRcContentAdapter.getData().size());
        setNoContentVisible(deviceAlarmLogInfoList.size() < 1);

    }

    @Override
    public void showAlarmPopupView() {
        mAlarmPopUtils.show();
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
        } else if(TextUtils.isEmpty(fgMainWarnEtSearch.getText().toString())){
            tvWarnAlarmSearchCancel.setVisibility(View.GONE);
//            setEditTextState(true);
        }

    }


    @Override
    public boolean getSearchTextVisible() {
        return tvWarnAlarmSearchCancel.getVisibility() == View.VISIBLE;
    }

    @Override
    public void setNoContentVisible(boolean isVisible) {
        fgMainWarnRcContent.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        icNoContent.setVisibility(isVisible ? View.VISIBLE : View.GONE);
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
}
