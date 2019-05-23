package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.MerchantAdapter;
import com.sensoro.common.adapter.SearchHistoryAdapter;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IMerchantSwitchActivityView;
import com.sensoro.smartcity.presenter.MerchantSwitchActivityPresenter;
import com.sensoro.common.server.bean.UserInfo;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.common.widgets.SpacesItemDecoration;
import com.sensoro.common.widgets.TipOperationDialogUtils;
import com.sensoro.common.widgets.SensoroToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sensoro.smartcity.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.smartcity.constant.Constants.DIRECTION_UP;

public class MerchantSwitchActivity extends BaseActivity<IMerchantSwitchActivityView, MerchantSwitchActivityPresenter> implements IMerchantSwitchActivityView
        , View.OnClickListener, AbsListView.OnScrollListener, AdapterView.OnItemClickListener,TipOperationDialogUtils.TipDialogUtilsClickListener {
    @BindView(R.id.ll_main_merchant)
    LinearLayout llMainMerchant;
    @BindView(R.id.tv_back_to_main_merchant)
    TextView tvBackToMainMerchant;
    @BindView(R.id.fragment_merchant_list)
    ListView mPullListView;
    @BindView(R.id.merchant_iv_menu_list)
    ImageView mMenuListImageView;
    @BindView(R.id.merchant_list_bottom_sep)
    View seperatorBottomView;
    @BindView(R.id.merchant_current_name)
    TextView mCurrentNameTextView;
    @BindView(R.id.merchant_current_phone)
    TextView mCurrentPhoneTextView;
    @BindView(R.id.rl_title_account)
    RelativeLayout rlTitleAccount;
    @BindView(R.id.merchant_return_top)
    ImageView mReturnTopImageView;
    //
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.rv_search_history)
    RecyclerView rvSearchHistory;
    @BindView(R.id.btn_search_clear)
    ImageView btnSearchClear;
    @BindView(R.id.ll_search_history)
    LinearLayout llSearchHistory;
    @BindView(R.id.merchant_et_search)
    EditText mMerchantEtSearch;
    @BindView(R.id.merchant_imv_clear)
    ImageView mMerchantEtClear;
    @BindView(R.id.no_content)
    ImageView imvNoContent;
    @BindView(R.id.no_content_tip)
    TextView tvContentTip;
    @BindView(R.id.ic_no_content)
    LinearLayout icNoContent;
    @BindView(R.id.merchant_ll_list_root)
    LinearLayout merchantLlListRoot;
    @BindView(R.id.merchant_tv_cancel)
    TextView merchantTvCancel;

    private ProgressUtils mProgressUtils;
    private boolean isShowDialog = true;
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private MerchantAdapter mMerchantAdapter;
    private TipOperationDialogUtils historyClearDialog;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_merchant);
        ButterKnife.bind(mActivity);
        initView();
        mPresenter.initData(mActivity);
    }

    @Override
    protected MerchantSwitchActivityPresenter createPresenter() {
        return new MerchantSwitchActivityPresenter();
    }

    @Override
    protected void onDestroy() {
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }

        if (historyClearDialog != null) {
            historyClearDialog.destroy();
            historyClearDialog = null;
        }
        super.onDestroy();
    }

    private void initView() {
        mReturnTopImageView.setOnClickListener(this);
        mMenuListImageView.setOnClickListener(this);
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        refreshLayout.setEnableAutoLoadMore(false);//开启自动加载功能（非必须）
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                mPresenter.requestDataByDirection(DIRECTION_DOWN, false, mMerchantEtSearch.getText().toString());
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                mPresenter.requestDataByDirection(DIRECTION_UP, false, mMerchantEtSearch.getText().toString());
            }
        });

        tvContentTip.setText(mActivity.getString(R.string.no_search_history));
        mMerchantEtSearch.setCursorVisible(false);
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

        initRcHistorySearch();

        mMerchantEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 当按了搜索之后关闭软键盘
                    String text = mMerchantEtSearch.getText().toString();
//                    if (TextUtils.isEmpty(text)) {
//                        SensoroToast.INSTANCE.makeText(mRootFragment.getActivity(), mRootFragment.getString(R.string.enter_search_content), Toast.LENGTH_SHORT).setGravity(Gravity.CENTER, 0, -10)
//                                .show();
//                        return true;
//                    }
                    mPresenter.save(text);
                    mMerchantEtSearch.clearFocus();
                    mPresenter.requestSearchData(DIRECTION_DOWN, text);
                    AppUtils.dismissInputMethodManager(mActivity, mMerchantEtSearch);
                    setSearchHistoryVisible(false);
                    return true;
                }
                return false;
            }
        });

        mMerchantEtSearch.addTextChangedListener(new TextWatcher() {
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

        initClearHistoryDialog();

    }

    private void initClearHistoryDialog() {
        historyClearDialog = new TipOperationDialogUtils(mActivity, true);
        historyClearDialog.setTipTitleText(getString(R.string.history_clear_all));
        historyClearDialog.setTipMessageText(getString(R.string.confirm_clear_history_record),R.color.c_a6a6a6);
        historyClearDialog.setTipCancelText(getString(R.string.cancel),getResources().getColor(R.color.c_1dbb99));
        historyClearDialog.setTipConfirmText(getString(R.string.clear),getResources().getColor(R.color.c_a6a6a6));
        historyClearDialog.setTipDialogUtilsClickListener(this);
    }

    private void setTvCancelVisible(boolean isVisible) {
        merchantTvCancel.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void setLlMainAccountVisible(boolean isVisible) {
        llMainMerchant.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void initRcHistorySearch() {
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mActivity) {
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
        rvSearchHistory.addItemDecoration(new SpacesItemDecoration(false, AppUtils.dp2px(mActivity, 6)));
        mSearchHistoryAdapter = new SearchHistoryAdapter(mActivity, new
                RecycleViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String text = mSearchHistoryAdapter.getSearchHistoryList().get(position);
                        if (!TextUtils.isEmpty(text)) {
                            mMerchantEtSearch.setText(text);
                            mMerchantEtSearch.setSelection(mMerchantEtSearch.getText().toString().length());
                        }
                        mPresenter.requestSearchData(DIRECTION_DOWN, text);
                        mMerchantEtClear.setVisibility(View.VISIBLE);
                        mMerchantEtSearch.clearFocus();
                        AppUtils.dismissInputMethodManager(mActivity, mMerchantEtSearch);
                        mPresenter.save(text);
                        setSearchHistoryVisible(false);
                    }
                });
        rvSearchHistory.setAdapter(mSearchHistoryAdapter);
    }

    private void setSearchClearImvVisible(boolean isVisible) {
        mMerchantEtClear.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void setSearchHistoryVisible(boolean isVisible) {
        llSearchHistory.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        merchantLlListRoot.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserInfo userInfo = mMerchantAdapter.getData().get(position);
        mPresenter.clickItem(userInfo);
    }

    @OnClick({R.id.merchant_frame_search, R.id.merchant_et_search, R.id.btn_search_clear, R.id.merchant_imv_clear, R.id.merchant_tv_cancel, R.id.tv_back_to_main_merchant})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.merchant_frame_search:
            case R.id.merchant_et_search:
                mMerchantEtSearch.requestFocus();
                mMerchantEtSearch.setCursorVisible(true);
                setSearchHistoryVisible(true);
                setLlMainAccountVisible(false);
                setTvCancelVisible(true);
                break;
            case R.id.btn_search_clear:
                showHistoryClearDialog();
                break;
            case R.id.merchant_imv_clear:
                mMerchantEtSearch.getText().clear();
                mMerchantEtSearch.requestFocus();
                AppUtils.openInputMethodManager(mActivity, mMerchantEtSearch);
                setSearchHistoryVisible(true);
                break;
            case R.id.merchant_tv_cancel:
                doCancelSearch();
                setSearchHistoryVisible(false);
                setTvCancelVisible(false);
                AppUtils.dismissInputMethodManager(mActivity, mMerchantEtSearch);
                setLlMainAccountVisible(true);
                break;
            case R.id.tv_back_to_main_merchant:
                mPresenter.doBackToMainMerchant();
                break;

        }
    }

    private void doCancelSearch() {
        mMerchantEtSearch.getText().clear();
        mPresenter.requestSearchData(DIRECTION_DOWN, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.merchant_iv_menu_list:
                finishAc();
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
        SensoroToast.getInstance().makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }

    @Override
    public void setCurrentNameAndPhone(String name, String phone) {
        if (name != null) {
            mCurrentNameTextView.setText(name);
        }
        if (phone != null) {
            mCurrentPhoneTextView.setText(phone);
        }
    }

    @Override
    public void updateAdapterUserInfo(List<UserInfo> data) {
        if (data != null && data.size() > 0) {
            icNoContent.setVisibility(View.GONE);
            merchantLlListRoot.setVisibility(View.VISIBLE);
            mMerchantAdapter.setDataList(data);
            mMerchantAdapter.notifyDataSetChanged();
        } else {
            if (isRlTitleAccountVisible()) {
                icNoContent.setVisibility(View.GONE);
                merchantLlListRoot.setVisibility(View.VISIBLE);
            } else {
                icNoContent.setVisibility(View.VISIBLE);
                merchantLlListRoot.setVisibility(View.GONE);
            }
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

    private boolean isRlTitleAccountVisible() {
        return llMainMerchant.getVisibility() == View.VISIBLE;
    }

    @Override
    protected void onPause() {
        AppUtils.dismissInputMethodManager(mActivity,mMerchantEtSearch);
        super.onPause();
    }

    @Override
    public void onPullRefreshComplete() {
//        mPullListView.onRefreshComplete();
        refreshLayout.finishLoadMore();
        refreshLayout.finishRefresh();
    }

    @Override
    public void updateSearchHistoryList(List<String> data) {
        btnSearchClear.setVisibility(data.size() > 0 ? View.VISIBLE : View.GONE);
        mSearchHistoryAdapter.updateSearchHistoryAdapter(data);
    }

    @Override
    public void setTvBackToMainMerchantVisible(boolean isVisible) {
        tvBackToMainMerchant.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showHistoryClearDialog() {
        if (historyClearDialog != null) {
            historyClearDialog.show();
        }
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
