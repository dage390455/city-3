package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.adapter.MerchantAdapter;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IMerchantSwitchFragmentView;
import com.sensoro.smartcity.presenter.MerchantSwitchFragmentPresenter;
import com.sensoro.smartcity.server.bean.UserInfo;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroToast;

import java.util.List;

import static com.sensoro.smartcity.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.smartcity.constant.Constants.DIRECTION_UP;
import static com.sensoro.smartcity.constant.Constants.INPUT;

/**
 * Created by sensoro on 17/7/24.
 */

public class MerchantSwitchFragment extends BaseFragment<IMerchantSwitchFragmentView,
        MerchantSwitchFragmentPresenter> implements IMerchantSwitchFragmentView, View
        .OnClickListener, AbsListView.OnScrollListener, AdapterView.OnItemClickListener {
    private PullToRefreshListView mPullListView;
    private ImageView mMenuListImageView;
    private ImageView mSearchImageView;
    private View seperatorView;
    private View seperatorBottomView;
    private TextView mCurrentNameTextView;
    private TextView mCurrentPhoneTextView;
    private ImageView mCurrentStatusImageView;
    private MerchantAdapter mMerchantAdapter;
    private RelativeLayout rlTitleAccount;
    private ImageView mReturnTopImageView;
    //
    private ProgressUtils mProgressUtils;
    private boolean isShowDialog = true;


    public static MerchantSwitchFragment newInstance(String input) {
        MerchantSwitchFragment merchantSwitchFragment = new MerchantSwitchFragment();
        Bundle args = new Bundle();
        args.putString(INPUT, input);
        merchantSwitchFragment.setArguments(args);
        return merchantSwitchFragment;
    }


    @Override
    protected void initData(Context activity) {
        initView();
        mPresenter.initData(activity);
    }


    @Override
    protected int initRootViewId() {
        return R.layout.fragment_merchant;
    }

    @Override
    protected MerchantSwitchFragmentPresenter createPresenter() {
        return new MerchantSwitchFragmentPresenter();
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
        super.onDestroyView();
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mRootFragment.getActivity()).build());
        mPullListView = (PullToRefreshListView) mRootView.findViewById(R.id.fragment_merchant_list);
        //
        mPullListView.setRefreshing(false);
        mPullListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                isShowDialog = false;
                mPresenter.requestDataByDirection(DIRECTION_DOWN, false);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                isShowDialog = false;
                mPresenter.requestDataByDirection(DIRECTION_UP, false);
            }
        });
        mPullListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullListView.setOnScrollListener(this);
        mMerchantAdapter = new MerchantAdapter(mRootFragment.getActivity());
        mPullListView.setAdapter(mMerchantAdapter);
        mPullListView.setOnItemClickListener(this);

        mReturnTopImageView = (ImageView) mRootView.findViewById(R.id.merchant_return_top);
        mReturnTopImageView.setOnClickListener(this);
        mMenuListImageView = (ImageView) mRootView.findViewById(R.id.merchant_iv_menu_list);
        mMenuListImageView.setOnClickListener(this);
        mSearchImageView = (ImageView) mRootView.findViewById(R.id.merchant_iv_search);
        mSearchImageView.setOnClickListener(this);
        mCurrentNameTextView = (TextView) mRootView.findViewById(R.id.merchant_current_name);
        mCurrentPhoneTextView = (TextView) mRootView.findViewById(R.id.merchant_current_phone);
        mCurrentStatusImageView = (ImageView) mRootView.findViewById(R.id.merchant_current_status);
        seperatorView = mRootView.findViewById(R.id.merchant_list_sep);
        seperatorBottomView = mRootView.findViewById(R.id.merchant_list_bottom_sep);
        rlTitleAccount = (RelativeLayout) mRootView.findViewById(R.id.rl_title_account);

    }

    public void refreshData(String username, String phone, String phoneId) {
        if (mPresenter != null) {
            mPresenter.refreshUserData(username, phone, phoneId);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPresenter.clickItem(position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.merchant_iv_menu_list:
                ((MainActivity) mRootFragment.getActivity()).openMenu();
                break;
            case R.id.merchant_iv_search:
                mPresenter.startToSearchAC();
                break;
            case R.id.merchant_return_top:
                mPullListView.getRefreshableView().smoothScrollToPosition(0);
                break;
        }
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
    public void setCurrentStatusImageViewVisible(boolean visible) {
        mCurrentStatusImageView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setCurrentNameAndPhone(String name, String phone) {
        mCurrentNameTextView.setText(name);
        mCurrentPhoneTextView.setText(phone);
    }

    @Override
    public void showSeperatorView(boolean isShow) {
        if (isShow) {
            seperatorView.setVisibility(View.VISIBLE);
        } else {
            seperatorView.setVisibility(View.GONE);
            seperatorBottomView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setAdapterSelectedIndex(int index) {
        mMerchantAdapter.setSelectedIndex(index);
    }

    @Override
    public void updateAdapterUserInfo(List<UserInfo> data) {
        mMerchantAdapter.setDataList(data);
        mMerchantAdapter.notifyDataSetChanged();
//        ViewParent parent = mPullListView.getParent();
//        if (parent instanceof LinearLayout) {
//            if (data.size() == 0) {
//                ((LinearLayout) parent).setBackgroundColor(mRootFragment.getActivity().getResources().getColor(R.color.f7f8f9));
//            } else {
//                ((LinearLayout) parent).setBackgroundColor(mRootFragment.getActivity().getResources().getColor(R.color.white));
//            }
//        }
    }

    @Override
    public void onPullRefreshComplete() {
        mPullListView.onRefreshComplete();
    }

    @Override
    public void requestDataByDirection(int direction, boolean isForce) {
        mPresenter.requestDataByDirection(direction, isForce);
    }

    @Override
    public void onFragmentStart() {

    }

    @Override
    public void onFragmentStop() {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int tempPos = mPullListView.getRefreshableView().getFirstVisiblePosition();
        if (tempPos > 0) {
            mReturnTopImageView.setVisibility(View.VISIBLE);
        } else {
            mReturnTopImageView.setVisibility(View.GONE);
        }
    }
}
