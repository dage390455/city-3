package com.sensoro.smartcity.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.common.adapter.SearchHistoryAdapter;
import com.sensoro.common.base.BaseFragment;
import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.common.server.bean.MalfunctionListInfo;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.SpacesItemDecoration;
import com.sensoro.common.widgets.TipOperationDialogUtils;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.MainMalfunctionFragRcContentAdapter;
import com.sensoro.smartcity.imainviews.IMalfunctionFragmentView;
import com.sensoro.smartcity.presenter.MalfunctionFragmentPresenter;
import com.sensoro.smartcity.widget.SensoroXLinearLayoutManager;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

import static com.sensoro.common.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.common.constant.Constants.DIRECTION_UP;

public class MalfunctionFragment extends BaseFragment<IMalfunctionFragmentView, MalfunctionFragmentPresenter>
        implements IMalfunctionFragmentView, TipOperationDialogUtils.TipDialogUtilsClickListener {
    @BindView(R.id.fg_main_top_search_et_search)
    EditText fgMainTopSearchEtSearch;
    @BindView(R.id.fg_main_top_search_frame_search)
    RelativeLayout fgMainTopSearchFrameSearch;
    @BindView(R.id.tv_top_search_alarm_search_cancel)
    TextView tvTopSearchAlarmSearchCancel;
    @BindView(R.id.fg_main_top_search_imv_calendar)
    ImageView fgMainTopSearchImvCalendar;
    @BindView(R.id.fg_main_top_search_tv_date_edit)
    TextView fgMainTopSearchTvDateEdit;
    @BindView(R.id.fg_main_warn_top_search_date_close)
    ImageView fgMainWarnTopSearchDateClose;
    @BindView(R.id.fg_main_top_search_rl_date_edit)
    RelativeLayout fgMainTopSearchRlDateEdit;
    @BindView(R.id.fg_main_top_search_title_root)
    LinearLayout fgMainTopSearchTitleRoot;
    View icNoContent;
    @BindView(R.id.fg_main_malfunction_rc_content)
    RecyclerView fgMainMalfunctionRcContent;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.alarm_return_top)
    ImageView alarmReturnTop;
    @BindView(R.id.fg_main_top_search_imv_clear)
    ImageView fgMainWarnFragmentImvClear;
    @BindView(R.id.rv_search_history)
    RecyclerView rvSearchHistory;
    @BindView(R.id.btn_search_clear)
    ImageView btnSearchClear;
    @BindView(R.id.ll_search_history)
    LinearLayout llSearchHistory;
    private ProgressUtils mProgressUtils;
    private Animation returnTopAnimation;
    private boolean isShowDialog = true;
    private MainMalfunctionFragRcContentAdapter mRcContentAdapter;
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private TipOperationDialogUtils historyClearDialog;

    @Override
    protected void initData(Context activity) {
        initView();
        mPresenter.initData(activity);
    }

    @Override
    protected int initRootViewId() {
        return R.layout.fragment_main_malfunction;
    }

    private void initView() {
        icNoContent = LayoutInflater.from(getActivity()).inflate(R.layout.no_content, null);

        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mRootFragment.getActivity()).build());
        returnTopAnimation = AnimationUtils.loadAnimation(mRootFragment.getContext(), R.anim.return_top_in_anim);
        alarmReturnTop.setAnimation(returnTopAnimation);
        alarmReturnTop.setVisibility(View.GONE);
        fgMainTopSearchEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 当按了搜索之后关闭软键盘
                    String text = fgMainTopSearchEtSearch.getText().toString();
//                    if (TextUtils.isEmpty(text)) {
//                        SensoroToast.INSTANCE.makeText(mRootFragment.getActivity(), mRootFragment.getString(R.string.enter_search_content), Toast.LENGTH_SHORT).setGravity(Gravity.CENTER, 0, -10)
//                                .show();
//                        return true;
//                    }
                    mPresenter.save(text);
                    fgMainTopSearchEtSearch.clearFocus();
                    mPresenter.requestSearchData(DIRECTION_DOWN, text);
                    AppUtils.dismissInputMethodManager(mRootFragment.getActivity(), fgMainTopSearchEtSearch);
                    setSearchHistoryVisible(false);
                    return true;
                }
                return false;
            }
        });
        fgMainTopSearchEtSearch.addTextChangedListener(new TextWatcher() {
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

        initRcContent();
        initRcSearchHistory();
        initClearHistoryDialog();

        AppUtils.getInputSoftStatus(mRootView, new AppUtils.InputSoftStatusListener() {
            @Override
            public void onKeyBoardClose() {
                fgMainTopSearchEtSearch.setCursorVisible(false);
            }

            @Override
            public void onKeyBoardOpen() {
                fgMainTopSearchEtSearch.setCursorVisible(true);
            }
        });
    }

    private void initClearHistoryDialog() {
        historyClearDialog = new TipOperationDialogUtils(mRootFragment.getActivity(), true);
        historyClearDialog.setTipTitleText(getString(R.string.history_clear_all));
        historyClearDialog.setTipMessageText(getString(R.string.confirm_clear_history_record), R.color.c_a6a6a6);
        historyClearDialog.setTipCancelText(getString(R.string.cancel), getResources().getColor(R.color.c_1dbb99));
        historyClearDialog.setTipConfirmText(getString(R.string.clear), getResources().getColor(R.color.c_a6a6a6));
        historyClearDialog.setTipDialogUtilsClickListener(this);
    }

    private void initRcSearchHistory() {
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mRootFragment.getActivity()) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
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
                            fgMainTopSearchEtSearch.setText(text);
                            fgMainTopSearchEtSearch.setSelection(fgMainTopSearchEtSearch.getText().toString().length());
                        }
                        fgMainWarnFragmentImvClear.setVisibility(View.VISIBLE);
                        fgMainTopSearchEtSearch.clearFocus();
                        AppUtils.dismissInputMethodManager(mRootFragment.getActivity(), fgMainTopSearchEtSearch);
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
    public void showHistoryClearDialog() {
        if (historyClearDialog != null) {
            historyClearDialog.show();
        }
    }

    private void initRcContent() {
        mRcContentAdapter = new MainMalfunctionFragRcContentAdapter(mRootFragment.getActivity());
//        mRcContentAdapter.setAlarmConfirmStatusClickListener(this);
        final SensoroXLinearLayoutManager xLinearLayoutManager = new SensoroXLinearLayoutManager(mRootFragment.getActivity());
        xLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fgMainMalfunctionRcContent.setLayoutManager(xLinearLayoutManager);
        fgMainMalfunctionRcContent.setAdapter(mRcContentAdapter);
        //
        //新控件
        refreshLayout.setEnableAutoLoadMore(false);//开启自动加载功能（非必须）
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                String text = fgMainTopSearchEtSearch.getText().toString();
                mPresenter.requestSearchData(DIRECTION_DOWN, text);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                String text = fgMainTopSearchEtSearch.getText().toString();
                mPresenter.requestSearchData(DIRECTION_UP, text);
            }
        });
        //
        fgMainMalfunctionRcContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if (xLinearLayoutManager.findFirstVisibleItemPosition() == 0 && newState == SCROLL_STATE_IDLE &&
//                        toolbarDirection == DIRECTION_DOWN) {
////                    mListRecyclerView.setre
//                }
                if (xLinearLayoutManager.findFirstVisibleItemPosition() > 4) {
                    if (newState == 0) {
                        alarmReturnTop.setVisibility(View.VISIBLE);
                        if (returnTopAnimation != null && returnTopAnimation.hasEnded()) {
                            alarmReturnTop.startAnimation(returnTopAnimation);
                        }
                    } else {
                        alarmReturnTop.setVisibility(View.GONE);
                    }
                } else {
                    alarmReturnTop.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });

        mRcContentAdapter.setOnItemClickListener(new RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MalfunctionListInfo item = mRcContentAdapter.getItem(position);
                if (item != null) {
                    mPresenter.doMalfunctionDetail(item);
                } else {
                    toastShort(getString(R.string.unknown_error));
                }
            }
        });
    }

    @Override
    protected MalfunctionFragmentPresenter createPresenter() {
        return new MalfunctionFragmentPresenter();
    }

    @OnClick({R.id.fg_main_top_search_frame_search, R.id.fg_main_top_search_et_search, R.id.fg_main_top_search_imv_calendar, R.id.fg_main_warn_top_search_date_close,
            R.id.tv_top_search_alarm_search_cancel, R.id.alarm_return_top, R.id.fg_main_top_search_imv_clear, R.id.btn_search_clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fg_main_top_search_frame_search:
            case R.id.fg_main_top_search_et_search:
                fgMainTopSearchEtSearch.requestFocus();
                fgMainTopSearchEtSearch.setCursorVisible(true);
                setSearchHistoryVisible(true);
//                setSearchButtonTextVisible(true);
//                forceOpenSoftKeyboard();
                break;
            case R.id.fg_main_top_search_imv_calendar:
                mPresenter.doCalendar(fgMainTopSearchTitleRoot);
                AppUtils.dismissInputMethodManager(mRootFragment.getActivity(), fgMainTopSearchEtSearch);
                break;
            case R.id.fg_main_warn_top_search_date_close:
                fgMainTopSearchRlDateEdit.setVisibility(View.GONE);
                String text = fgMainTopSearchEtSearch.getText().toString();
                setSearchHistoryVisible(false);
                AppUtils.dismissInputMethodManager(mRootFragment.getActivity(), fgMainTopSearchEtSearch);
                mPresenter.requestSearchData(DIRECTION_DOWN, text);
                break;
            case R.id.tv_top_search_alarm_search_cancel:
                cancelSearchState();
                break;
            case R.id.alarm_return_top:
                fgMainMalfunctionRcContent.smoothScrollToPosition(0);
                alarmReturnTop.setVisibility(View.GONE);
                break;
            case R.id.btn_search_clear:
                showHistoryClearDialog();
//                setSearchButtonTextVisible(true);
                break;
            case R.id.fg_main_top_search_imv_clear:
                fgMainTopSearchEtSearch.getText().clear();
                fgMainTopSearchEtSearch.requestFocus();
                AppUtils.openInputMethodManager(mRootFragment.getActivity(), fgMainTopSearchEtSearch);
                setSearchHistoryVisible(true);
                break;

        }
    }

    @Override
    public void cancelSearchState() {
        doCancelSearch();
        setSearchHistoryVisible(false);
//                setSearchButtonTextVisible(false);
        AppUtils.dismissInputMethodManager(mRootFragment.getActivity(), fgMainTopSearchEtSearch);
    }

    private void doCancelSearch() {
        if (getSearchTextVisible()) {
            fgMainTopSearchEtSearch.getText().clear();
        }
        mPresenter.doCancelSearch();
    }

    private boolean getSearchTextVisible() {
        return tvTopSearchAlarmSearchCancel.getVisibility() == View.VISIBLE;
    }

    @Override
    public void startAC(Intent intent) {
        Objects.requireNonNull(mRootFragment.getActivity()).startActivity(intent);
    }

    @Override
    public void finishAc() {

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
        String text = fgMainTopSearchEtSearch.getText().toString();
        mPresenter.requestSearchData(DIRECTION_DOWN, text);
    }

    @Override
    public void onFragmentStop() {

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
        if (historyClearDialog != null) {
            historyClearDialog.destroy();
            historyClearDialog = null;
        }
        super.onDestroyView();
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
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean isSelectedDateLayoutVisible() {
        return fgMainTopSearchRlDateEdit.getVisibility() == View.VISIBLE;
    }

    @Override
    public void setSelectedDateLayoutVisible(boolean isVisible) {
        fgMainTopSearchRlDateEdit.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setSelectedDateSearchText(String s) {
        fgMainTopSearchTvDateEdit.setText(s);
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
    public void setSearchButtonTextVisible(boolean b) {
        if (b) {
            tvTopSearchAlarmSearchCancel.setVisibility(View.VISIBLE);
//            setEditTextState(false);
//            AppUtils.dismissInputMethodManager(mRootFragment.getActivity(),fgMainTopSearchEtSearch);
        } else if (TextUtils.isEmpty(fgMainTopSearchEtSearch.getText().toString())) {
            tvTopSearchAlarmSearchCancel.setVisibility(View.GONE);
//            setEditTextState(true);
        }
    }

    @Override
    public void updateAlarmListAdapter(List<MalfunctionListInfo> mMalfunctionInfoList) {
        if (mMalfunctionInfoList.size() > 0) {
            mRcContentAdapter.setData(mMalfunctionInfoList);
            mRcContentAdapter.notifyDataSetChanged();
        }
        setNoContentVisible(mMalfunctionInfoList.size() < 1);
    }

    @Override
    public void UpdateSearchHistoryList(List<String> data) {
        btnSearchClear.setVisibility(data.size() > 0 ? View.VISIBLE : View.GONE);
        mSearchHistoryAdapter.updateSearchHistoryAdapter(data);
    }

    @Override
    public void setSearchHistoryVisible(boolean isVisible) {
        llSearchHistory.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        refreshLayout.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        setSearchButtonTextVisible(isVisible);
    }

    @SuppressLint("RestrictedApi")
    private void setNoContentVisible(boolean isVisible) {


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
            refreshLayout.setRefreshContent(fgMainMalfunctionRcContent);
        }
    }

    @Override
    public void onCancelClick() {
        if (historyClearDialog != null) {
            historyClearDialog.dismiss();
        }

    }

    @Override
    public void onConfirmClick(String content, String diameter) {
        mPresenter.clearSearchHistory();
        if (historyClearDialog != null) {
            historyClearDialog.dismiss();
        }

    }
}
