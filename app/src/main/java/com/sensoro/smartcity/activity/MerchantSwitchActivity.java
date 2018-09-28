package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.MerchantAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IMerchantSwitchActivityView;
import com.sensoro.smartcity.presenter.MerchantSwitchActivityPresenter;
import com.sensoro.smartcity.server.bean.UserInfo;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroToast;

import java.util.List;

import static com.sensoro.smartcity.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.smartcity.constant.Constants.DIRECTION_UP;

public class MerchantSwitchActivity extends BaseActivity<IMerchantSwitchActivityView, MerchantSwitchActivityPresenter> implements IMerchantSwitchActivityView
        , View.OnClickListener, AbsListView.OnScrollListener, AdapterView.OnItemClickListener {
    private ListView mPullListView;
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
    private RefreshLayout refreshLayout;
    private ImageView imvNoContent;


    @Override
    protected void onDestroy() {
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
        super.onDestroy();
    }

    private void initView() {
        mReturnTopImageView = (ImageView) findViewById(R.id.merchant_return_top);
        mReturnTopImageView.setOnClickListener(this);
        mMenuListImageView = (ImageView) findViewById(R.id.merchant_iv_menu_list);
        mMenuListImageView.setOnClickListener(this);
        mSearchImageView = (ImageView) findViewById(R.id.merchant_iv_search);
        mSearchImageView.setOnClickListener(this);
        mCurrentNameTextView = (TextView) findViewById(R.id.merchant_current_name);
        mCurrentPhoneTextView = (TextView) findViewById(R.id.merchant_current_phone);
        mCurrentStatusImageView = (ImageView) findViewById(R.id.merchant_current_status);
        seperatorView = findViewById(R.id.merchant_list_sep);
        seperatorBottomView = findViewById(R.id.merchant_list_bottom_sep);
        rlTitleAccount = (RelativeLayout) findViewById(R.id.rl_title_account);
        refreshLayout = findViewById(R.id.refreshLayout);
        imvNoContent = findViewById(R.id.no_content);

        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        mPullListView =  findViewById(R.id.fragment_merchant_list);
        refreshLayout.setEnableAutoLoadMore(true);//开启自动加载功能（非必须）
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                mPresenter.requestDataByDirection(DIRECTION_DOWN, false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                mPresenter.requestDataByDirection(DIRECTION_UP,false);
            }
        });
        //
//        mPullListView.setRefreshing(false);
//        mPullListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                isShowDialog = false;
//                mPresenter.requestDataByDirection(DIRECTION_DOWN, false);
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                isShowDialog = false;
//                mPresenter.requestDataByDirection(DIRECTION_UP, false);
//            }
//        });
//        mPullListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullListView.setOnScrollListener(this);
        mMerchantAdapter = new MerchantAdapter(mActivity);
        mPullListView.setAdapter(mMerchantAdapter);
        mPullListView.setOnItemClickListener(this);



    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPresenter.clickItem(position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.merchant_iv_menu_list:
                finishAc();
                break;
            case R.id.merchant_iv_search:
                mPresenter.startToSearchAC();
                break;
            case R.id.merchant_return_top:
//                mPullListView.getRefreshableView().smoothScrollToPosition(0);
                mPullListView.smoothScrollToPosition(0);
                break;
        }
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
        SensoroToast.INSTANCE.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
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
        if(isShow){
            refreshLayout.finishLoadMoreWithNoMoreData();
        }
//        if (isShow) {
//            seperatorView.setVisibility(View.VISIBLE);
//        } else {
//            seperatorView.setVisibility(View.GONE);
//            seperatorBottomView.setVisibility(View.GONE);
//        }
    }

    @Override
    public void setAdapterSelectedIndex(int index) {
        mMerchantAdapter.setSelectedIndex(index);
    }

    @Override
    public void updateAdapterUserInfo(List<UserInfo> data) {
        if (data != null && data.size() > 0) {
            mPullListView.setVisibility(View.VISIBLE);
            imvNoContent.setVisibility(View.GONE);
            mMerchantAdapter.setDataList(data);
            mMerchantAdapter.notifyDataSetChanged();
        }else{
            mPullListView.setVisibility(View.GONE);
            imvNoContent.setVisibility(View.VISIBLE);
        }

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
//        mPullListView.onRefreshComplete();
        refreshLayout.finishLoadMore();
        refreshLayout.finishRefresh();
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
//        int tempPos = mPullListView.getRefreshableView().getFirstVisiblePosition();
        int tempPos = mPullListView.getFirstVisiblePosition();
        if (tempPos > 0) {
            mReturnTopImageView.setVisibility(View.VISIBLE);
        } else {
            mReturnTopImageView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_merchant);
        initView();
        mPresenter.initData(mActivity);
    }

    @Override
    protected MerchantSwitchActivityPresenter createPresenter() {
        return new MerchantSwitchActivityPresenter();
    }

}
