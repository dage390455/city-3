package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.MainWarnFragRcContentAdapter;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IWarnFragmentView;
import com.sensoro.smartcity.presenter.WarnFragmentPresenter;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroToast;
import com.sensoro.smartcity.widget.SensoroXLinearLayoutManager;
import com.sensoro.smartcity.widget.popup.AlarmPopUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.sensoro.smartcity.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.smartcity.constant.Constants.DIRECTION_UP;

public class WarnFragment extends BaseFragment<IWarnFragmentView, WarnFragmentPresenter> implements
        IWarnFragmentView, MainWarnFragRcContentAdapter.AlarmConfirmStatusClickListener, RecycleViewItemClickListener, MainWarnFragRcContentAdapter.OnPlayPhoneListener {
    @BindView(R.id.fg_main_warn_title_root)
    CardView fgMainWarnTitleRoot;
    @BindView(R.id.fg_main_warn_frame_search)
    FrameLayout fgMainWarnFrameSearch;
    @BindView(R.id.fg_main_warn_imv_calendar)
    ImageView fgMainWarnImvCalendar;
    @BindView(R.id.fg_main_warn_rc_content)
    RecyclerView fgMainWarnRcContent;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.fg_main_warn_tv_date_edit)
    TextView fgMainWarnTvDateEdit;
    @BindView(R.id.fg_main_warn_imv_date_close)
    ImageView fgMainWarnImvDateClose;
    @BindView(R.id.fg_main_warn_rl_date_edit)
    RelativeLayout fgMainWarnRlDateEdit;
    private MainWarnFragRcContentAdapter mRcContentAdapter;
    private boolean isShowDialog = true;
    private ProgressUtils mProgressUtils;
    private AlarmPopUtils mAlarmPopUtils;

    @Override
    protected void initData(Context activity) {
        initView();
        mPresenter.initData(activity);
    }


    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mRootFragment.getActivity()).build());
        mAlarmPopUtils = new AlarmPopUtils(mRootFragment.getActivity());
        mAlarmPopUtils.setDialog(mRootFragment.getActivity());
        mAlarmPopUtils.setOnPopupCallbackListener(mPresenter);
        initRcContent();
        initListener();
    }

    private void initListener() {

    }

    /**
     * 搜索过滤器
     *
     * @param direction
     * @param isForce
     */
    private void requestDataByFilter(int direction, boolean isForce) {
//        CharSequence searchText = mSearchEditText.getHint();
//        if (!TextUtils.isEmpty(searchText) && isSearchLayoutVisible()) {
//            mPresenter.requestSearchData(direction, isForce, searchText.toString());
//        } else {
        mPresenter.requestDataAll(direction, isForce);
//        }
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
        mRcContentAdapter.setOnItemClickListener(this);
        mRcContentAdapter.setOnPlayPhoneListener(this);
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
                requestDataByFilter(DIRECTION_DOWN, false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                requestDataByFilter(DIRECTION_UP, false);
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
    }

    @Override
    public void onDestroyView() {
        if (mRootView != null) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }

//        if (mGridAdapter != null) {
//            mGridAdapter.getData().clear();
//        }
//        if (mListAdapter != null) {
//            mListAdapter.getData().clear();
//        }
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (mAlarmPopupView.getVisibility() == View.VISIBLE) {
//                mAlarmPopupView.dismiss();
//                return false;
//            }
        }
        return true;
    }

    @OnClick({R.id.fg_main_warn_frame_search, R.id.fg_main_warn_imv_calendar, R.id.fg_main_warn_imv_date_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fg_main_warn_frame_search:
                mPresenter.doSearch();
                break;
            case R.id.fg_main_warn_imv_calendar:
                mPresenter.doCalendar(fgMainWarnTitleRoot);

                break;

            case R.id.fg_main_warn_imv_date_close:
                fgMainWarnRlDateEdit.setVisibility(View.GONE);
                requestDataByFilter(DIRECTION_DOWN, true);
                break;
        }
    }

    @Override
    public void updateAlarmListAdapter(List<DeviceAlarmLogInfo> deviceAlarmLogInfoList) {
        mRcContentAdapter.setData(deviceAlarmLogInfoList);
        mRcContentAdapter.notifyDataSetChanged();
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
    public void onConfirmStatusClick(View view, int position, boolean isReConfirm) {
        mPresenter.clickItemByConfirmStatus(position, isReConfirm);
    }

    @Override
    public void onItemClick(View view, int position) {
        mPresenter.clickItem(position);
    }


    @Override
    public void onCallPhone(View v, int position) {
        mPresenter.doContactOwner(position);
    }

}
