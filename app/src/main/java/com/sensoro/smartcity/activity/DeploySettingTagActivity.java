package com.sensoro.smartcity.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
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
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SensoroToast;
import com.sensoro.smartcity.widget.SpacesItemDecoration;

import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mabbas007.tagsedittext.TagsEditText;
import mabbas007.tagsedittext.utils.ResourceUtils;

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
        initView();
        mPrestener.initData(mActivity);
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

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            mKeywordEt.showDropDown();
//        }
//    }

    private void initView() {
        try {
            mKeywordEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    LogUtils.loge(this, "actionId = " + actionId);
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        String text = mKeywordEt.getText().toString();
                        if (!TextUtils.isEmpty(text)) {
                            String[] split = text.split(" ");
                            if (split.length > 5) {
                                toastShort("最大标签不超过5个！");
                                return true;
                            }
                            for (String temp : split) {
                                if (!TextUtils.isEmpty(temp) && ResourceUtils.getByteFromWords(temp) > 30) {
                                    toastShort("标签最大不能超过10个汉字或30个字符");
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                }
            });
            mKeywordEt.setTagsListener(this);
            initSearchHistory();
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
        mActivity.finish();
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
    public void onTagDuplicate() {
        toastShort("标签不能重复！");
    }

    @Override
    public void setSearchHistoryLayoutVisible(boolean isVisible) {
        mSearchHistoryLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        mPrestener.onDestroy();
        mKeywordEt.destroyDrawingCache();
        super.onDestroy();
    }

    @Override
    public void updateTags(List<String> tags) {
        mKeywordEt.setTags(tags);
    }

    @Override
    public void updateSearchHistory() {
        mSearchHistoryAdapter.notifyDataSetChanged();
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
        SensoroToast.makeText(mActivity, msg, Toast.LENGTH_SHORT).setGravity(Gravity.CENTER, 0, -10).show();
    }

    @Override
    public void toastLong(String msg) {

    }

}
