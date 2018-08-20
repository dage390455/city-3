package com.sensoro.smartcity.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
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
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ISearchAlarmActivityView;
import com.sensoro.smartcity.presenter.SearchAlarmActivityPresenter;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SensoroToast;
import com.sensoro.smartcity.widget.SpacesItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sensoro on 17/7/11.
 */

public class SearchAlarmActivity extends BaseActivity<ISearchAlarmActivityView, SearchAlarmActivityPresenter>
        implements ISearchAlarmActivityView, View.OnClickListener, TextView
        .OnEditorActionListener, TextWatcher {

    @BindView(R.id.search_alarm_et)
    EditText mKeywordEt;
    @BindView(R.id.search_alarm_cancel_tv)
    TextView mCancelTv;
    @BindView(R.id.search_alarm_clear_iv)
    ImageView mClearKeywordIv;
    @BindView(R.id.search_alarm_history_ll)
    LinearLayout mSearchHistoryLayout;
    @BindView(R.id.search_alarm_clear_btn)
    ImageView mClearBtn;
    @BindView(R.id.search_alarm_history_rv)
    RecyclerView mSearchHistoryRv;
    @BindView(R.id.search_alarm_tag_rv)
    RecyclerView mTagRv;
    @BindView(R.id.search_alarm_tips)
    LinearLayout tipsLinearLayout;
    @BindView(R.id.search_alarm_tag_layout)
    LinearLayout tagLinearLayout;
    @BindView(R.id.search_tablayout)
    TabLayout searchTabLayout;
    private ProgressUtils mProgressUtils;
    private SearchHistoryAdapter mSearchHistoryAdapter;


    //


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_alarm);
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
        initSearchHistory();
    }

    @Override
    public void initTabs(int searchType) {

        switch (searchType) {
            case Constants.TYPE_DEVICE_NAME:
                searchTabLayout.addTab(searchTabLayout.newTab().setText("设备名称"), true);
                searchTabLayout.addTab(searchTabLayout.newTab().setText("设备号"), false);
                searchTabLayout.addTab(searchTabLayout.newTab().setText("手机号"), false);
                break;
            case Constants.TYPE_DEVICE_SN:
                searchTabLayout.addTab(searchTabLayout.newTab().setText("设备名称"), false);
                searchTabLayout.addTab(searchTabLayout.newTab().setText("设备号"), true);
                searchTabLayout.addTab(searchTabLayout.newTab().setText("手机号"), false);
                break;
            case Constants.TYPE_DEVICE_PHONE_NUM:
                searchTabLayout.addTab(searchTabLayout.newTab().setText("设备名称"), false);
                searchTabLayout.addTab(searchTabLayout.newTab().setText("设备号"), false);
                searchTabLayout.addTab(searchTabLayout.newTab().setText("手机号"), true);
                break;
            default:
                searchTabLayout.addTab(searchTabLayout.newTab().setText("设备名称"), true);
                searchTabLayout.addTab(searchTabLayout.newTab().setText("设备号"), false);
                searchTabLayout.addTab(searchTabLayout.newTab().setText("手机号"), false);
                break;
        }
        searchTabLayout.addOnTabSelectedListener(mPresenter);
    }

    @Override
    public void setEditText(String searchContent) {
        if (searchContent != null) {
            mKeywordEt.setText(searchContent);
            mKeywordEt.setSelection(searchContent.length());
        }
    }

    @Override
    public void setEditHintText(String hintText) {
        mKeywordEt.setHint(hintText);
    }

    @Override
    public void clearKeywordEt() {
        mKeywordEt.getText().clear();
    }


    @Override
    protected SearchAlarmActivityPresenter createPresenter() {
        return new SearchAlarmActivityPresenter();
    }

    private void initSearchHistory() {
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.x20);
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mSearchHistoryRv.setLayoutManager(layoutManager);
        mSearchHistoryRv.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mSearchHistoryAdapter = new SearchHistoryAdapter(mActivity, new
                RecycleViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mPresenter.clickSearchHistoryItem(position);
                        mKeywordEt.clearFocus();
                        dismissInputMethodManager(view);
                    }
                });
        mSearchHistoryRv.setAdapter(mSearchHistoryAdapter);
//        mSearchHistoryAdapter.updateSearchHistoryAdapter(mPresenter.getHistoryKeywords_deviceName());
        mKeywordEt.requestFocus();
    }


    private void dismissInputMethodManager(View view) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//从控件所在的窗口中隐藏
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
            case R.id.search_alarm_clear_btn:
                mPresenter.cleanHistory();
                break;
            case R.id.search_alarm_cancel_tv:
                mKeywordEt.clearFocus();
                finishAc();
                break;
            case R.id.search_alarm_clear_iv:
                mKeywordEt.getText().clear();
                mSearchHistoryAdapter.notifyDataSetChanged();
                setClearKeywordIvVisible(false);
                setTipsLinearLayoutVisible(false);
//                tagLinearLayout.setVisibility(View.VISIBLE);
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
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String text = mKeywordEt.getText().toString();
            if (!TextUtils.isEmpty(text)) {
                mKeywordEt.clearFocus();
                setClearKeywordIvVisible(true);
                dismissInputMethodManager(v);
                mPresenter.save(text.trim());
                mPresenter.requestData(text.trim());
            } else {
                SensoroToast.makeText(mActivity, "请输入搜索内容", Toast.LENGTH_SHORT).setGravity(Gravity.CENTER, 0, -10)
                        .show();
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
    public void setClearKeywordIvVisible(boolean isVisible) {
        mClearKeywordIv.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setTipsLinearLayoutVisible(boolean isVisible) {
        tipsLinearLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateSearchHistory(List<String> historyKeywords) {
        mSearchHistoryAdapter.updateSearchHistoryAdapter(historyKeywords);
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
    public void setIntentResult(int resultCode) {

    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {
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
