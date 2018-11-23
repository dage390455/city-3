package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.sensoro.smartcity.adapter.MainMalfunctionFragRcContentAdapter;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IMalfunctionFragmentView;
import com.sensoro.smartcity.presenter.MalfunctionFragmentPresenter;
import com.sensoro.smartcity.server.bean.MalfunctionListInfo;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.toast.SensoroToast;
import com.sensoro.smartcity.widget.SensoroXLinearLayoutManager;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.sensoro.smartcity.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.smartcity.constant.Constants.DIRECTION_UP;

public class MalfunctionFragment extends BaseFragment<IMalfunctionFragmentView, MalfunctionFragmentPresenter>
        implements IMalfunctionFragmentView {
    @BindView(R.id.fg_main_top_search_et_search)
    EditText fgMainTopSearchEtSearch;
    @BindView(R.id.fg_main_top_search_frame_search)
    FrameLayout fgMainTopSearchFrameSearch;
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
    @BindView(R.id.no_content)
    ImageView noContent;
    @BindView(R.id.no_content_tip)
    TextView noContentTip;
    @BindView(R.id.ic_no_content)
    LinearLayout icNoContent;
    @BindView(R.id.fg_main_malfunction_rc_content)
    RecyclerView fgMainMalfunctionRcContent;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.alarm_return_top)
    ImageView alarmReturnTop;
    private ProgressUtils mProgressUtils;
    private Animation returnTopAnimation;
    private boolean isShowDialog = true;
    private MainMalfunctionFragRcContentAdapter mRcContentAdapter;

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
                    mPresenter.requestSearchData(DIRECTION_DOWN, text);
                    AppUtils.dismissInputMethodManager(mRootFragment.getActivity(),fgMainTopSearchEtSearch);
                    return true;
                }
                return false;
            }
        });

        initRcContent();


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
                        if (returnTopAnimation.hasEnded()) {
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
                }else {
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
            R.id.tv_top_search_alarm_search_cancel, R.id.alarm_return_top})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fg_main_top_search_frame_search:
            case R.id.fg_main_top_search_et_search:
                fgMainTopSearchEtSearch.requestFocus();
                fgMainTopSearchEtSearch.setCursorVisible(true);
//                forceOpenSoftKeyboard();
                break;
            case R.id.fg_main_top_search_imv_calendar:
                mPresenter.doCalendar(fgMainTopSearchTitleRoot);
                break;
            case R.id.fg_main_warn_top_search_date_close:
                fgMainTopSearchRlDateEdit.setVisibility(View.GONE);
                String text = fgMainTopSearchEtSearch.getText().toString();
                mPresenter.requestSearchData(DIRECTION_DOWN, text);
                break;
            case R.id.tv_top_search_alarm_search_cancel:
                doCancelSearch();
                break;
            case R.id.alarm_return_top:
                fgMainMalfunctionRcContent.smoothScrollToPosition(0);
                alarmReturnTop.setVisibility(View.GONE);
                break;
        }
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
        startActivity(intent);
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

    }

    @Override
    public void onFragmentStop() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
    }

    @Override
    public void showProgressDialog() {
        if (mProgressUtils != null && isShowDialog) {
            mProgressUtils.showProgress();
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
        SensoroToast.INSTANCE.makeText(msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.INSTANCE.makeText(msg,Toast.LENGTH_LONG).show();
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
            AppUtils.dismissInputMethodManager(mRootFragment.getActivity(),fgMainTopSearchEtSearch);
        } else {
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
        setNoContentVisible(mMalfunctionInfoList.size()<1);
    }

    private void setNoContentVisible(boolean b) {
        fgMainMalfunctionRcContent.setVisibility(b ? View.GONE : View.VISIBLE);
        icNoContent.setVisibility(b ? View.VISIBLE : View.GONE);
    }
}
