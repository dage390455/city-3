package com.sensoro.smartcity.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.RelationAdapter;
import com.sensoro.smartcity.adapter.SearchHistoryAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeploySettingNameActivityView;
import com.sensoro.smartcity.presenter.DeploySettingNameActivityPresenter;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SensoroToast;
import com.sensoro.smartcity.widget.SpacesItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeploySettingNameActivity extends BaseActivity<IDeploySettingNameActivityView,
        DeploySettingNameActivityPresenter
        > implements IDeploySettingNameActivityView, TextView.OnEditorActionListener,
        TextWatcher, RecycleViewItemClickListener {


    @BindView(R.id.deploy_setting_back)
    ImageView backImageView;
    @BindView(R.id.deploy_setting_finish)
    TextView finishImageView;
    @BindView(R.id.deploy_setting_et)
    EditText mKeywordEt;
    @BindView(R.id.deploy_setting_history_rv)
    RecyclerView mSearchHistoryRv;
    @BindView(R.id.deploy_setting_relation_rv)
    RecyclerView mSearchRelationRv;
    @BindView(R.id.deploy_setting_relation_layout)
    LinearLayout mSearchRelationLayout;
    @BindView(R.id.deploy_setting_history_layout)
    LinearLayout mSearchHistoryLayout;
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private RelationAdapter mRelationAdapter;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deploy_setting_name);
        ButterKnife.bind(mActivity);
        mPrestener.initData(mActivity);
        initView();
    }


    @Override
    protected DeploySettingNameActivityPresenter createPresenter() {
        return new DeploySettingNameActivityPresenter();
    }

    private void initView() {
        try {
            initRelation();
            initSearchHistory();
            mKeywordEt.requestFocus();
            mKeywordEt.setOnEditorActionListener(this);
            mKeywordEt.addTextChangedListener(this);
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
                        String text = mPrestener.getHistoryKeywords().get(position).trim();
                        setEditText(text);
                        mKeywordEt.clearFocus();
                        dismissInputMethodManager(view);
                    }
                });
        mSearchHistoryRv.setAdapter(mSearchHistoryAdapter);
        updateSearchHistoryData();
        //弹出框value unit对齐，搜索框有内容点击历史搜索出现没有搜索内容
    }

    private void initRelation() {
        mRelationAdapter = new RelationAdapter(mActivity, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSearchRelationRv.setLayoutManager(linearLayoutManager);
        mSearchRelationRv.setAdapter(mRelationAdapter);
    }


    private void dismissInputMethodManager(View view) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//从控件所在的窗口中隐藏
    }


    @OnClick(R.id.deploy_setting_back)
    public void back() {
        finishAc();
    }

    @OnClick(R.id.deploy_setting_finish)
    public void doFinish() {
        String text = mKeywordEt.getText().toString();
        mPrestener.doChoose(text);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            dismissInputMethodManager(v);
            String text = mKeywordEt.getText().toString();
            mPrestener.doChoose(text);
            return true;
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mPrestener.handleTextChanged(s, start, before, count);
    }

    @Override
    public void afterTextChanged(Editable s) {
//        int selectionStart = mKeywordEt.getSelectionStart();
//        int selectionEnd = mKeywordEt.getSelectionEnd();
//        if (!TextUtils.isEmpty(tempWords)) {
//            byte[] bytes = new byte[0];
//            try {
//                String tempStr = tempWords.toString();
//                bytes = tempStr.getBytes("UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            if (bytes.length > 36) {
//                Toast.makeText(this, "最大不能超过12个汉字或32个字符", Toast.LENGTH_SHORT).show();
////                text = text.substring(0, text.length());
//////                etContent.setEditText(str);
//////                etContent.setSelection(str.length());
//                s.delete(selectionStart - 1, selectionEnd);
//                int tempSelection = selectionEnd;
//                mKeywordEt.setEditText(s);
//                mKeywordEt.setSelection(tempSelection);
//            }
//        }
    }

    @Override
    public void onItemClick(View view, int position) {
        String text = mRelationAdapter.getData().get(position);
        setEditText(text);
        mRelationAdapter.getData().clear();
        mRelationAdapter.notifyDataSetChanged();
    }

    @Override
    public void setEditText(String text) {
        if (!TextUtils.isEmpty(text)) {
            mKeywordEt.setText(text);
            mKeywordEt.setSelection(text.length());
        }
    }

    @Override
    public void setSearchHistoryLayoutVisible(boolean isVisible) {
        mSearchHistoryLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setSearchRelationLayoutVisible(boolean isVisible) {
        mSearchRelationLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateSearchHistoryData() {
        mSearchHistoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateRelationData(List<String> strList) {
        if (strList != null) {
            mRelationAdapter.setData(strList);
        }
        mRelationAdapter.notifyDataSetChanged();
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
        mActivity.setResult(resultCode, data);
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }
}
