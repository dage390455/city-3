package com.sensoro.smartcity.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
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

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.MainHomeFragRcContentAdapter;
import com.sensoro.smartcity.adapter.RelationAdapter;
import com.sensoro.common.adapter.SearchHistoryAdapter;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ISearchMonitorActivityView;
import com.sensoro.smartcity.presenter.SearchMonitorActivityPresenter;
import com.sensoro.common.server.bean.DeviceInfo;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SensoroXLinearLayoutManager;
import com.sensoro.common.widgets.SpacesItemDecoration;
import com.sensoro.common.widgets.TipOperationDialogUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.widget.popup.AlarmPopUtilsTest;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;
import static com.sensoro.smartcity.constant.Constants.DIRECTION_DOWN;

/**
 * Created by sensoro on 17/7/11.
 */

public class SearchMonitorActivity extends BaseActivity<ISearchMonitorActivityView, SearchMonitorActivityPresenter>
        implements ISearchMonitorActivityView, View.OnClickListener, TextView
        .OnEditorActionListener, TextWatcher, MainHomeFragRcContentAdapter.OnContentItemClickListener,
        TipOperationDialogUtils.TipDialogUtilsClickListener {
    @BindView(R.id.search_device_et)
    EditText mKeywordEt;
    @BindView(R.id.search_device_cancel_tv)
    TextView mCancelTv;
    @BindView(R.id.search_device_clear_iv)
    ImageView mClearKeywordIv;
    @BindView(R.id.search_device_history_ll)
    LinearLayout mSearchHistoryLayout;
    @BindView(R.id.search_device_clear_btn)
    ImageView mClearBtn;
    @BindView(R.id.search_device_history_rv)
    RecyclerView mSearchHistoryRv;
    @BindView(R.id.search_device_relation_rv)
    RecyclerView mRelationRecyclerView;
    @BindView(R.id.search_device_relation_layout)
    LinearLayout mRelationLayout;
    @BindView(R.id.index_return_top)
    ImageView mReturnTopImageView;
    @BindView(R.id.ac_search_device_refreshLayout)
    RefreshLayout acSearchDeviceRefreshLayout;
    @BindView(R.id.ac_search_device_rc_content)
    RecyclerView acSearchDeviceRcContent;
    @BindView(R.id.index_layout_list)
    RelativeLayout indexLayoutList;
    @BindView(R.id.no_content)
    ImageView imvNoContent;
    @BindView(R.id.no_content_tip)
    TextView tvNoContentTip;
    @BindView(R.id.ic_no_content)
    LinearLayout icNoContent;
    @BindView(R.id.search_device_ll_root)
    LinearLayout searchDeviceLlRoot;


    private Animation returnTopAnimation;
    private ProgressUtils mProgressUtils;
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private RelationAdapter mRelationAdapter;

    private boolean isShowDialog = true;
    private MainHomeFragRcContentAdapter mSearchRcContentAdapter;
    private SensoroXLinearLayoutManager xLinearLayoutManager;
    private boolean isKeyBoardOpen = false;
    private TipOperationDialogUtils historyClearDialog;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_device);
        ButterKnife.bind(mActivity);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        mClearKeywordIv.setOnClickListener(this);
        mKeywordEt.setOnEditorActionListener(this);
        mKeywordEt.addTextChangedListener(this);
        mCancelTv.setOnClickListener(this);
        mClearBtn.setOnClickListener(this);
        mKeywordEt.setOnClickListener(this);
        AppUtils.getInputSoftStatus(searchDeviceLlRoot, new AppUtils.InputSoftStatusListener() {
            @Override
            public void onKeyBoardClose() {
                isKeyBoardOpen = false;
                mKeywordEt.setCursorVisible(false);
            }

            @Override
            public void onKeyBoardOpen() {
                isKeyBoardOpen = true;
                mKeywordEt.setCursorVisible(true);
            }
        });

        initSearchHistory();
        initRelation();
        initIndex();
        initClearHistoryDialog();

        tvNoContentTip.setText(R.string.cant_find_related_content);
    }

    private void initClearHistoryDialog() {
        historyClearDialog = new TipOperationDialogUtils(mActivity, true);
        historyClearDialog.setTipTitleText(getString(R.string.history_clear_all));
        historyClearDialog.setTipMessageText(getString(R.string.confirm_clear_history_record), R.color.c_a6a6a6);
        historyClearDialog.setTipCancelText(getString(R.string.cancel), getResources().getColor(R.color.c_1dbb99));
        historyClearDialog.setTipConfirmText(getString(R.string.clear), getResources().getColor(R.color.c_a6a6a6));
        historyClearDialog.setTipDialogUtilsClickListener(this);
    }

    @Override
    protected SearchMonitorActivityPresenter createPresenter() {
        return new SearchMonitorActivityPresenter();
    }

    private void initIndex() {
        initListView();
        returnTopAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.return_top_in_anim);
        mReturnTopImageView.setAnimation(returnTopAnimation);
        mReturnTopImageView.setVisibility(View.GONE);
        mReturnTopImageView.setOnClickListener(this);
        setIndexListLayoutVisible(false);
        mKeywordEt.requestFocus();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    private void initListView() {

        mSearchRcContentAdapter = new MainHomeFragRcContentAdapter(mActivity);
        mSearchRcContentAdapter.setOnContentItemClickListener(this);
        //
        xLinearLayoutManager = new SensoroXLinearLayoutManager(mActivity);
        xLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        acSearchDeviceRcContent.setLayoutManager(xLinearLayoutManager);
        acSearchDeviceRcContent.setAdapter(mSearchRcContentAdapter);

        acSearchDeviceRcContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (xLinearLayoutManager.findFirstVisibleItemPosition() > 4) {
                    if (newState == 0) {
                        mReturnTopImageView.setVisibility(VISIBLE);
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
        });

        acSearchDeviceRefreshLayout.setEnableAutoLoadMore(true);//开启自动加载功能（非必须）
        acSearchDeviceRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                isShowDialog = false;
                String text = mKeywordEt.getText().toString();
                mPresenter.requestWithDirection(Constants.DIRECTION_DOWN, text);
//                mPresenter.requestTopData(false);
            }
        });
        acSearchDeviceRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                String text = mKeywordEt.getText().toString();
                isShowDialog = false;
                mPresenter.requestWithDirection(Constants.DIRECTION_UP, text);
            }
        });


    }


    @Override
    public void refreshData(List<DeviceInfo> dataList) {
        if (dataList != null) {
            Collections.sort(dataList);
            mSearchRcContentAdapter.updateData(dataList);
        }
        setIndexListLayoutVisible(true);
        setNoContentVisible(dataList == null || dataList.size() == 0);

    }

    @Override
    public void setNoContentVisible(boolean isVisible) {
        icNoContent.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        indexLayoutList.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    }

    @Override
    public void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && isKeyBoardOpen) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void setHistoryClearBtnVisible(boolean isVisible) {
        mClearBtn.setVisibility(isVisible ? VISIBLE : View.GONE);
    }

    @Override
    public void showHistoryClearDialog() {
        if (historyClearDialog != null) {
            historyClearDialog.show();
        }
    }

    @Override
    public void updateRelationData(List<String> strList) {
        if (strList != null) {
            mRelationAdapter.setData(strList);
        }
        mRelationAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateSearchHistoryData(List<String> strHistory) {
        setHistoryClearBtnVisible(strHistory != null && strHistory.size() > 0);
        mSearchHistoryAdapter.updateSearchHistoryAdapter(strHistory);
    }

    @Override
    public void setEditText(String text) {
        if (text != null) {
            mKeywordEt.setText(text);
            mKeywordEt.setSelection(mKeywordEt.getText().toString().length());
        }
    }


    @Override
    public void setTypeView(String typesText) {

    }


    private void initRelation() {
        mRelationAdapter = new RelationAdapter(mActivity, new RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mClearKeywordIv.setVisibility(View.VISIBLE);
                mKeywordEt.clearFocus();
                dismissInputMethodManager(view);
                mPresenter.clickRelationItem(position);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRelationRecyclerView.setLayoutManager(linearLayoutManager);
        mRelationRecyclerView.setAdapter(mRelationAdapter);
    }

    private void initSearchHistory() {
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mSearchHistoryRv.setLayoutManager(layoutManager);
        mSearchHistoryRv.addItemDecoration(new SpacesItemDecoration(AppUtils.dp2px(mActivity, 6), false, false));
        mSearchHistoryAdapter = new SearchHistoryAdapter(mActivity, new
                RecycleViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String text = mSearchHistoryAdapter.getSearchHistoryList().get(position);
                        setEditText(text);
                        mClearKeywordIv.setVisibility(View.VISIBLE);
                        mKeywordEt.clearFocus();
                        dismissInputMethodManager(view);
                        mPresenter.requestWithDirection(DIRECTION_DOWN, text);
                    }
                });
        mSearchHistoryRv.setAdapter(mSearchHistoryAdapter);
//        updateSearchHistoryData();
        //弹出框value unit对齐，搜索框有内容点击历史搜索出现没有搜索内容
    }

    @Override
    public void showListLayout() {
        Animation inAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.layout_in_anim);
        inAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                indexLayoutList.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        indexLayoutList.setAnimation(inAnimation);
        indexLayoutList.startAnimation(inAnimation);
    }


    @Override
    public void recycleViewRefreshComplete() {
        acSearchDeviceRefreshLayout.finishRefresh();
        acSearchDeviceRefreshLayout.finishLoadMore();
    }


    @Override
    public void setSearchHistoryLayoutVisible(boolean isVisible) {
        mSearchHistoryLayout.setVisibility(isVisible ? VISIBLE : View.GONE);
        mClearBtn.setVisibility(isVisible ? VISIBLE : View.GONE);
    }

    @Override
    public void setRelationLayoutVisible(boolean isVisible) {
        mRelationLayout.setVisibility(isVisible ? VISIBLE : View.GONE);
    }

    @Override
    public void setIndexListLayoutVisible(boolean isVisible) {
        indexLayoutList.setVisibility(isVisible ? VISIBLE : View.GONE);
    }


    @Override
    public void returnTop() {
        acSearchDeviceRcContent.smoothScrollToPosition(0);
        mReturnTopImageView.setVisibility(View.GONE);
    }

    private void dismissInputMethodManager(View view) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//从控件所在的窗口中隐藏
        }
    }


    @Override
    protected void onDestroy() {
        if (returnTopAnimation != null) {
            returnTopAnimation.cancel();
            returnTopAnimation = null;
        }
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_device_clear_btn:
                historyClearDialog.show();
                break;
            case R.id.search_device_cancel_tv:
                mKeywordEt.clearFocus();
                finishAc();
                break;
            case R.id.search_device_clear_iv:
                mKeywordEt.getText().clear();
                mClearKeywordIv.setVisibility(View.GONE);
                mPresenter.updateSearchHistoryData();
                break;
            case R.id.index_return_top:
                returnTop();
                break;
            case R.id.search_device_et:
                setSearchHistoryLayoutVisible(true);
                setIndexListLayoutVisible(false);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        if (!TextUtils.isEmpty(s.toString())) {
//            setSearchHistoryLayoutVisible(false);
//            mClearKeywordIv.setVisibility(View.VISIBLE);
////            mPresenter.filterDeviceInfo(s.toString());
//        } else {
//            setSearchHistoryLayoutVisible(true);
//            setIndexListLayoutVisible(false);
//        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String text = mKeywordEt.getText().toString();
            if (TextUtils.isEmpty(text)) {
                SensoroToast.getInstance().makeText(mActivity, mActivity.getString(R.string.enter_search_content), Toast.LENGTH_SHORT).setGravity(Gravity.CENTER, 0, -10)
                        .show();
                return true;
            }
            mPresenter.save(text);
            mClearKeywordIv.setVisibility(View.VISIBLE);
            mKeywordEt.clearFocus();
            dismissInputMethodManager(v);
            mPresenter.requestWithDirection(DIRECTION_DOWN, text);
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(View view, int position) {
        mPresenter.clickItem(position);
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
    public void startAC(Intent intent) {
        mActivity.startActivity(intent);
    }

    @Override
    public void finishAc() {
        mActivity.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishAc();
        }
        return super.onKeyDown(keyCode, event);
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
    public void onAlarmInfoClick(View v, int position) {
        mPresenter.clickAlarmInfo(position);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AlarmPopUtilsTest.handlePhotoIntent(requestCode,resultCode,data);
    }

    @Override
    public void onCancelClick() {
        if (historyClearDialog != null) {
            historyClearDialog.dismiss();

        }
    }

    @Override
    public void onConfirmClick(String content, String diameter) {
        mPresenter.cleanHistory();
        if (historyClearDialog != null) {
            historyClearDialog.dismiss();
        }
    }
}
