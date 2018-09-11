package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
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

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.sensoro.smartcity.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.smartcity.constant.Constants.DIRECTION_UP;

public class WarnFragment extends BaseFragment<IWarnFragmentView, WarnFragmentPresenter> implements
        IWarnFragmentView, MainWarnFragRcContentAdapter.AlarmConfirmStatusClickListener, RecycleViewItemClickListener {
    @BindView(R.id.fg_main_warn_tv_search)
    TextView fgMainWarnTvSearch;
    @BindView(R.id.fg_main_warn_imv_calendar)
    ImageView fgMainWarnImvCalendar;
    @BindView(R.id.fg_main_warn_rc_content)
    XRecyclerView fgMainWarnRcContent;
    private MainWarnFragRcContentAdapter mRcContentAdapter;
    private boolean isShowDialog = true;
    private ProgressUtils mProgressUtils;
    @Override
    protected void initData(Context activity) {
        initView();
        mPresenter.initData(activity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mRootFragment.getActivity()).build());
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
        final SensoroXLinearLayoutManager xLinearLayoutManager = new SensoroXLinearLayoutManager(mRootFragment.getActivity());
        xLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fgMainWarnRcContent.setLayoutManager(xLinearLayoutManager);
        fgMainWarnRcContent.setAdapter(mRcContentAdapter);
        fgMainWarnRcContent.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
        fgMainWarnRcContent.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
//        int spacingInPixels = mRootFragment.getResources().getDimensionPixelSize(R.dimen.x8);
//        fgMainHomeRcContent.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
        fgMainWarnRcContent.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                isShowDialog = false;
                requestDataByFilter(DIRECTION_DOWN, false);
            }

            @Override
            public void onLoadMore() {
                isShowDialog = false;
                requestDataByFilter(DIRECTION_UP, false);
            }
        });
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
    @OnClick({R.id.fg_main_warn_tv_search, R.id.fg_main_warn_imv_calendar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fg_main_warn_tv_search:

                break;
            case R.id.fg_main_warn_imv_calendar:
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

    }

    @Override
    public void dismissAlarmPopupView() {

    }

    @Override
    public void onPullRefreshComplete() {
        fgMainWarnRcContent.refreshComplete();
    }

    @Override
    public void onConfirmStatusClick(View view, int position, boolean isReConfirm) {

    }

    @Override
    public void onItemClick(View view, int position) {
        mPresenter.clickItem(position);
    }
}
