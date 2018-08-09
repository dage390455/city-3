package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.adapter.AlarmListAdapter;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IAlarmListFragmentView;
import com.sensoro.smartcity.presenter.AlarmListFragmentPresenter;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroShadowView;
import com.sensoro.smartcity.widget.SensoroToast;
import com.sensoro.smartcity.widget.popup.SensoroPopupAlarmViewNew;

import java.util.List;

import static com.sensoro.smartcity.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.smartcity.constant.Constants.DIRECTION_UP;
import static com.sensoro.smartcity.constant.Constants.INPUT;

/**
 * Created by sensoro on 17/7/24.
 */

public class AlarmListFragment extends BaseFragment<IAlarmListFragmentView, AlarmListFragmentPresenter> implements
        IAlarmListFragmentView, View.OnClickListener, AdapterView
        .OnItemClickListener,
        AbsListView.OnScrollListener, AlarmListAdapter.AlarmConfirmStatusClickListener {

    private PullToRefreshListView mPtrListView;
    private ImageView mDateImageView;
    private ImageView mSearchImageView;
    private ImageView mAlarmMenuImageView;
    private ImageView mCloseImageView;
    private ImageView mReturnTopImageView;
    private TextView mSelectedDateTextView;
    private TextView mCancelTextView;
    private EditText mSearchEditText;
    private RelativeLayout mSearchLayout;
    private RelativeLayout mSelectedDateLayout;
    private RelativeLayout mTitleLayout;
    private SensoroShadowView mShadowView;
    private SensoroPopupAlarmViewNew mAlarmPopupView;
    private AlarmListAdapter mAlarmListAdapter;
    //
    private ProgressUtils mProgressUtils;
    private boolean isShowDialog = true;

    public static AlarmListFragment newInstance(String input) {
        AlarmListFragment alarmListFragment = new AlarmListFragment();
        Bundle args = new Bundle();
        args.putString(INPUT, input);
        alarmListFragment.setArguments(args);
        return alarmListFragment;
    }

    @Override
    protected void initData(Context activity) {
        initView();
        mPresenter.initData(activity);
    }

    @Override
    protected int initRootViewId() {
        return R.layout.fragment_alarm_list;
    }

    @Override
    protected AlarmListFragmentPresenter createPresenter() {
        return new AlarmListFragmentPresenter();
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
        if (mAlarmPopupView != null) {
            mAlarmPopupView.onDestroyPop();
        }
        super.onDestroyView();
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mRootFragment.getActivity()).build());
        mPtrListView = (PullToRefreshListView) mRootView.findViewById(R.id.alarm_ptr_list);
        mPtrListView.setRefreshing(false);
        mPtrListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                isShowDialog = false;
                requestDataByFilter(DIRECTION_DOWN, false);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                isShowDialog = false;
                requestDataByFilter(DIRECTION_UP, false);
            }
        });
        mPtrListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPtrListView.setOnScrollListener(this);
        mAlarmListAdapter = new AlarmListAdapter(mRootFragment.getActivity(), this);
        mPtrListView.setAdapter(mAlarmListAdapter);
        mPtrListView.setOnItemClickListener(this);
        mDateImageView = (ImageView) mRootView.findViewById(R.id.alarm_iv_date);
        mDateImageView.setOnClickListener(this);
        mSearchImageView = (ImageView) mRootView.findViewById(R.id.alarm_iv_search);
        mSearchImageView.setOnClickListener(this);
        mAlarmMenuImageView = (ImageView) mRootView.findViewById(R.id.alarm_iv_menu_list);
        mAlarmMenuImageView.setOnClickListener(this);
        mReturnTopImageView = (ImageView) mRootView.findViewById(R.id.alarm_return_top);
        mReturnTopImageView.setOnClickListener(this);
        mSearchLayout = (RelativeLayout) mRootView.findViewById(R.id.alarm_search_layout);
        mSearchEditText = (EditText) mRootView.findViewById(R.id.alarm_search_et);
        mCancelTextView = (TextView) mRootView.findViewById(R.id.alarm_cancel_tv);
        mCancelTextView.setOnClickListener(this);
        mSearchEditText.setOnClickListener(this);
        mTitleLayout = (RelativeLayout) mRootView.findViewById(R.id.alarm_title_layout);
        mSelectedDateLayout = (RelativeLayout) mRootView.findViewById(R.id.alarm_log_date_edit);
        mSelectedDateTextView = (TextView) mRootView.findViewById(R.id.alarm_log_selected_date);
        mCloseImageView = (ImageView) mRootView.findViewById(R.id.alarm_log_selected_close);
        mCloseImageView.setOnClickListener(this);
        mAlarmPopupView = (SensoroPopupAlarmViewNew) mRootView.findViewById(R.id.alarm_popup_view);
        mAlarmPopupView.setOnPopupCallbackListener(mPresenter);
        mAlarmPopupView.setDialog(mRootFragment.getActivity());
        mShadowView = (SensoroShadowView) mRootView.findViewById(R.id.alarm_popup_shadow);
    }


    private void cancelSearch() {
        setSearchLayoutVisible(false);
//        mSearchEditText.setHint("");
        mPresenter.requestDataAll(DIRECTION_DOWN, true);
//        requestDataByFilter(DIRECTION_DOWN, true);
    }

    public void handlerActivityResult(int requestCode, int resultCode, Intent data) {
        if (mAlarmPopupView != null) {
            mAlarmPopupView.handlerActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 搜索过滤器
     *
     * @param direction
     * @param isForce
     */
    private void requestDataByFilter(int direction, boolean isForce) {
        CharSequence searchText = mSearchEditText.getHint();
        if (!TextUtils.isEmpty(searchText) && isSearchLayoutVisible()) {
            mPresenter.requestSearchData(direction, isForce, searchText.toString());
        } else {
            mPresenter.requestDataAll(direction, isForce);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alarm_iv_date:
                mPresenter.clickByDate();
                break;
            case R.id.alarm_iv_search:
                mPresenter.searchByImageView();
                break;
            case R.id.alarm_iv_menu_list:
                ((MainActivity) getActivity()).openMenu();
                break;
            case R.id.alarm_log_selected_close:
                setSelectedDateLayoutVisible(false);
                requestDataByFilter(DIRECTION_DOWN, true);
                break;
            case R.id.alarm_cancel_tv:
                cancelSearch();
                break;
            case R.id.alarm_search_et:
                mPresenter.searchByEditText(mSearchEditText.getHint());
                break;
            case R.id.alarm_return_top:
                mPtrListView.getRefreshableView().smoothScrollToPosition(0);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPresenter.clickItem(position);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int tempPos = mPtrListView.getRefreshableView().getFirstVisiblePosition();
        if (tempPos > 0) {
            mReturnTopImageView.setVisibility(View.VISIBLE);
        } else {
            mReturnTopImageView.setVisibility(View.GONE);
        }
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
        SensoroToast.makeText(mRootFragment.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }

    @Override
    public void updateAlarmListAdapter(List<DeviceAlarmLogInfo> deviceAlarmLogInfoList) {
        mAlarmListAdapter.setData(deviceAlarmLogInfoList);
        mAlarmListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showAlarmPopupView() {
        mAlarmPopupView.show(mShadowView);
    }

    @Override
    public void dismissAlarmPopupView() {
        mAlarmPopupView.dismiss();
    }

    @Override
    public boolean isSelectedDateLayoutVisible() {
        return mSelectedDateLayout.getVisibility() == View.VISIBLE;
    }

    @Override
    public void setSelectedDateLayoutVisible(boolean isVisible) {
        mSelectedDateLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean isSearchLayoutVisible() {
        return mSearchLayout.getVisibility() == View.VISIBLE && mTitleLayout.getVisibility() == View.GONE;
    }

    @Override
    public void setSearchLayoutVisible(boolean isVisible) {
        if (isVisible) {
            mSearchLayout.setVisibility(View.VISIBLE);
            mTitleLayout.setVisibility(View.GONE);
        } else {
            mSearchLayout.setVisibility(View.GONE);
            mTitleLayout.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void setAlarmSearchText(String searchText) {
        setSearchLayoutVisible(true);
        mSearchEditText.setHint(searchText);
    }

    @Override
    public void onPullRefreshComplete() {
        mPtrListView.onRefreshComplete();
    }

    @Override
    public void setSelectedDateSearchText(String searchText) {
        mSelectedDateTextView.setText(searchText);
    }

    @Override
    public PullToRefreshBase.State getPullRefreshState() {
        return mPtrListView.getState();
    }


    @Override
    public void requestDataByDirection(int direction, boolean isForce) {
        mPresenter.requestDataAll(direction, isForce);
    }

    @Override
    public void setUpdateButtonClickable(boolean canClick) {
        if (mAlarmPopupView != null) {
            mAlarmPopupView.setUpdateButtonClickable(canClick);
        }
    }

    @Override
    public void onConfirmStatusClick(View view, int position, boolean isReConfirm) {
        mPresenter.clickItemByConfirmStatus(position, isReConfirm);
    }

    @Override
    public void startAC(Intent intent) {
        mRootFragment.getActivity().startActivity(intent);
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
}
