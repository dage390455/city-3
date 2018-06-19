package com.sensoro.smartcity.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.SearchHistoryAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.ISearchMerchantActivityView;
import com.sensoro.smartcity.presenter.SearchMerchantActivityPresenter;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SensoroToast;
import com.sensoro.smartcity.widget.SpacesItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sensoro on 17/7/11.
 */

public class SearchMerchantActivity extends BaseActivity<ISearchMerchantActivityView,
        SearchMerchantActivityPresenter> implements ISearchMerchantActivityView, View.OnClickListener,
        TextView.OnEditorActionListener {

    @BindView(R.id.search_merchant_et)
    EditText mKeywordEt;
    @BindView(R.id.search_merchant_cancel_tv)
    TextView mCancelTv;
    @BindView(R.id.search_merchant_clear_iv)
    ImageView mClearKeywordIv;
    @BindView(R.id.search_merchant_history_ll)
    LinearLayout mSearchHistoryLayout;
    @BindView(R.id.search_merchant_clear_btn)
    ImageView mClearBtn;
    @BindView(R.id.search_merchant_history_rv)
    RecyclerView mSearchHistoryRv;
    @BindView(R.id.search_merchant_tips)
    LinearLayout tipsLinearLayout;
    private ProgressUtils mProgressUtils;
    private SearchHistoryAdapter mSearchHistoryAdapter;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_merchant);
        ButterKnife.bind(mActivity);
        mPrestener.initData(mActivity);
        init();
    }

    private void init() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        mClearKeywordIv.setOnClickListener(this);
        mKeywordEt.setOnEditorActionListener(this);
        mCancelTv.setOnClickListener(this);
        mClearBtn.setOnClickListener(this);
        //弹出框value unit对齐，搜索框有内容点击历史搜索出现没有搜索内容
        final SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mSearchHistoryRv.setLayoutManager(layoutManager);
        int spacingInPixels = mActivity.getResources().getDimensionPixelSize(R.dimen.x10);
        mSearchHistoryRv.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
        mSearchHistoryAdapter = new SearchHistoryAdapter(mActivity, mPrestener.getmHistoryKeywords(), new
                RecycleViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String text = mPrestener.getmHistoryKeywords().get(position);
                        setEditText(text);
                        setClearKeywordIvVisible(true);
                        mKeywordEt.clearFocus();
                        mPrestener.requestData(text);
                        dismissInputMethodManager(view);
                    }
                });
        mSearchHistoryRv.setAdapter(mSearchHistoryAdapter);
        updateSearchHistory();
        showSoftInputFromWindow(mKeywordEt);
    }


    @Override
    protected SearchMerchantActivityPresenter createPresenter() {
        return new SearchMerchantActivityPresenter();
    }


    private void dismissInputMethodManager(View view) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//从控件所在的窗口中隐藏
    }

    private void showSoftInputFromWindow(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    protected void onDestroy() {
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_merchant_clear_btn:
                mPrestener.cleanHistory();
                break;
            case R.id.search_merchant_cancel_tv:
                mKeywordEt.clearFocus();
                finishAc();
                break;
            case R.id.search_merchant_clear_iv:
                mKeywordEt.getText().clear();
                setClearKeywordIvVisible(false);
                setTipsLinearLayoutVisible(false);
                updateSearchHistory();
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String text = mKeywordEt.getText().toString();
            if (TextUtils.isEmpty(text)) {
                SensoroToast.makeText(mActivity, "请输入搜索内容", Toast.LENGTH_SHORT).setGravity(Gravity.CENTER, 0, -10)
                        .show();
            } else {
                setClearKeywordIvVisible(true);
                mPrestener.save(text);
                mPrestener.requestData(text);
                mKeywordEt.clearFocus();
                dismissInputMethodManager(v);
            }
            return true;
        }
        return false;
    }

    @Override
    public void setSearchHistoryLayoutVisible(boolean isVisible) {
        mSearchHistoryLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateSearchHistory() {
        mSearchHistoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void setTipsLinearLayoutVisible(boolean isVisible) {
        tipsLinearLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setClearKeywordIvVisible(boolean isVisible) {
        mClearKeywordIv.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setEditText(String text) {
        if (text != null) {
            mKeywordEt.setText(text);
            mKeywordEt.setSelection(text.length());
        }
    }

    @Override
    public void startAC(Intent intent) {

    }

    @Override
    public void finishAc() {
        mActivity.finish();
    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {

    }

    @Override
    public void setIntentResult(int requestCode) {
        mActivity.setResult(requestCode);
    }

    @Override
    public void setIntentResult(int requestCode, Intent data) {
        mActivity.setResult(requestCode, data);
    }

    @Override
    public void showProgressDialog() {
        mProgressUtils.showProgress();
    }

    @Override
    public void dismissProgressDialog() {
        mProgressUtils.dismissProgress();
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }
}
