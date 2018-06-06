package com.sensoro.smartcity.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.SearchHistoryAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeploySettingTagActivityView;
import com.sensoro.smartcity.presenter.DeploySettingTagActivityPresenter;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SpacesItemDecoration;

import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mabbas007.tagsedittext.TagsEditText;

public class DeploySettingTagActivity extends BaseActivity<IDeploySettingTagActivityView,
        DeploySettingTagActivityPresenter> implements IDeploySettingTagActivityView,
        TagsEditText.TagsEditListener {


    @BindView(R.id.deploy_setting_tag_back)
    ImageView backImageView;
    @BindView(R.id.deploy_setting_tag_finish)
    TextView finishImageView;
    @BindView(R.id.deploy_setting_tag_et)
    TagsEditText mKeywordEt;
    @BindView(R.id.deploy_setting_tag_history_rv)
    RecyclerView mSearchHistoryRv;
    @BindView(R.id.deploy_setting_tag_relation_rv)
    RecyclerView mSearchRelationRv;
    @BindView(R.id.deploy_setting_tag_history_layout)
    LinearLayout mSearchHistoryLayout;
    private SearchHistoryAdapter mSearchHistoryAdapter;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deploy_setting_tag);
        ButterKnife.bind(mActivity);
        mPrestener.initData(mActivity);
        init();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mKeywordEt.onSaveInstanceState();
    }


    @Override
    protected DeploySettingTagActivityPresenter createPresenter() {
        return new DeploySettingTagActivityPresenter();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mKeywordEt.showDropDown();
        }
    }

    private void init() {
        try {
            mKeywordEt.setTagsListener(this);
            initSearchHistory();
            mKeywordEt.requestFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void initSearchHistory() {
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mSearchHistoryRv.setLayoutManager(layoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.x10);
        mSearchHistoryRv.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mSearchHistoryAdapter = new SearchHistoryAdapter(mActivity, mPrestener.getHistoryKeywords(), new
                RecycleViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mPrestener.clickHistory(position);
                    }
                });
        mSearchHistoryRv.setAdapter(mSearchHistoryAdapter);
        mSearchHistoryAdapter.notifyDataSetChanged();
        mKeywordEt.requestFocus();
        //弹出框value unit对齐，搜索框有内容点击历史搜索出现没有搜索内容
    }


    public void dismissInputMethodManager(View view) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//从控件所在的窗口中隐藏
    }


    @OnClick(R.id.deploy_setting_tag_finish)
    public void doFinish() {
        List<String> tags = mKeywordEt.getTags();
        mPrestener.doChoose(true, tags);
    }


    @OnClick(R.id.deploy_setting_tag_back)
    public void back() {
        this.finish();
    }


    @Override
    public void onTagsChanged(Collection<String> tags) {
        mPrestener.setTagList((List<String>) tags);
    }

    @Override
    public void onEditingFinished() {
        mKeywordEt.clearFocus();
//            dismissInputMethodManager(mKeywordEt);
//            doChoose(false);
    }

    @Override
    public void setSearchHistoryLayoutVisible(boolean isVisible) {
        mSearchHistoryLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateTags(List<String> tags) {
        mKeywordEt.setTags(tags);
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

    }

    @Override
    public void setIntentResult(int requestCode, Intent data) {
        mActivity.setResult(requestCode, data);
    }

    @Override
    public void toastShort(String msg) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastShort(int resId) {

    }

    @Override
    public void toastLong(String msg) {

    }

    @Override
    public void toastLong(int resId) {

    }
}
