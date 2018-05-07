package com.sensoro.smartcity.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.baidu.mobstat.StatService;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.adapter.RelationAdapter;
import com.sensoro.smartcity.adapter.SearchHistoryAdapter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SpacesItemDecoration;
import com.sensoro.smartcity.widget.statusbar.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeploySettingNameActivity extends BaseActivity implements Constants, TextView.OnEditorActionListener,
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

    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private List<String> mHistoryKeywords = new ArrayList<>();
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private RelationAdapter mRelationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deploy_setting_name);
        ButterKnife.bind(this);
        init();
        StatusBarCompat.setStatusBarColor(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    private void init() {
        try {
            mPref = getApplicationContext().getSharedPreferences(PREFERENCE_DEPLOY_NAME_HISTORY, Activity.MODE_PRIVATE);
            mEditor = mPref.edit();
            initRelation();
            initSearchHistory();
            String name = getIntent().getStringExtra(EXTRA_SETTING_NAME_ADDRESS);
            if (name != null) {
                mKeywordEt.setText(name);
                mKeywordEt.setSelection(name.length());
            }
            mKeywordEt.requestFocus();
            mKeywordEt.setOnEditorActionListener(this);
            mKeywordEt.addTextChangedListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSearchHistory() {
        String history = mPref.getString(PREFERENCE_KEY_DEPLOY, "");
        if (!TextUtils.isEmpty(history)) {
            List<String> list = new ArrayList<String>();
            for (Object o : history.split(",")) {
                list.add((String) o);
            }
            mHistoryKeywords = list;
        }
        if (mHistoryKeywords.size() > 0) {
            mSearchHistoryLayout.setVisibility(View.VISIBLE);
        } else {
            mSearchHistoryLayout.setVisibility(View.GONE);
        }
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mSearchHistoryRv.setLayoutManager(layoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.x10);
        mSearchHistoryRv.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mSearchHistoryAdapter = new SearchHistoryAdapter(this, mHistoryKeywords, new RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mKeywordEt.setText(mHistoryKeywords.get(position).trim());
                mKeywordEt.clearFocus();
                mKeywordEt.setSelection(mHistoryKeywords.get(position).trim().length());
                dismissInputMethodManager(view);
            }
        });
        mSearchHistoryRv.setAdapter(mSearchHistoryAdapter);
        mSearchHistoryAdapter.notifyDataSetChanged();
        mKeywordEt.requestFocus();
        //弹出框value unit对齐，搜索框有内容点击历史搜索出现没有搜索内容
    }

    private void initRelation() {
        mRelationAdapter = new RelationAdapter(this, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSearchRelationRv.setLayoutManager(linearLayoutManager);
        mSearchRelationRv.setAdapter(mRelationAdapter);
    }

    @Override
    protected boolean isNeedSlide() {
        return true;
    }

    private void filterDeviceInfoByNameAndAddress(String filter) {
        List<DeviceInfo> originDeviceInfoList = new ArrayList<>();
        originDeviceInfoList.addAll(SensoroCityApplication.getInstance().getData());
        ArrayList<DeviceInfo> deleteDeviceInfoList = new ArrayList<>();
        for (DeviceInfo deviceInfo : originDeviceInfoList) {
            if (!TextUtils.isEmpty(deviceInfo.getName())) {
                if (!deviceInfo.getName().contains(filter.toUpperCase())) {
                    deleteDeviceInfoList.add(deviceInfo);
                }
            } else {
                deleteDeviceInfoList.add(deviceInfo);
            }

        }
        for (DeviceInfo deviceInfo : deleteDeviceInfoList) {
            originDeviceInfoList.remove(deviceInfo);
        }
        List<String> tempList = new ArrayList<>();
        for (DeviceInfo deviceInfo : originDeviceInfoList) {
            if (!TextUtils.isEmpty(deviceInfo.getName())) {
                tempList.add(deviceInfo.getName());
            }
        }
        mRelationAdapter.setData(tempList);
        mRelationAdapter.notifyDataSetChanged();

    }

    private void dismissInputMethodManager(View view) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//从控件所在的窗口中隐藏
    }

    private void save() {
        String text = mKeywordEt.getText().toString();
        String oldText = mPref.getString(PREFERENCE_KEY_DEPLOY, "");
        if (!TextUtils.isEmpty(text)) {
            if (mHistoryKeywords.contains(text)) {
                List<String> list = new ArrayList<String>();
                for (String o : oldText.split(",")) {
                    if (!o.equalsIgnoreCase(text)) {
                        list.add(o);
                    }
                }
                list.add(0, text);
                mHistoryKeywords = list;
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < list.size(); i++) {
                    if (i == (list.size() - 1)) {
                        stringBuffer.append(list.get(i));
                    } else {
                        stringBuffer.append(list.get(i) + ",");
                    }
                }
                mEditor.putString(PREFERENCE_KEY_DEPLOY, stringBuffer.toString());
                mEditor.commit();
            } else {
                mEditor.putString(PREFERENCE_KEY_DEPLOY, text + "," + oldText);
                mEditor.commit();
                mHistoryKeywords.add(0, text);
            }
        }
    }

    private void doChoose() {
        save();
        mKeywordEt.clearFocus();
        Intent data = new Intent();
        data.putExtra(EXTRA_SETTING_NAME_ADDRESS, mKeywordEt.getText().toString());
        setResult(RESULT_CODE_SETTING_NAME_ADDRESS, data);
        finish();
    }

    @OnClick(R.id.deploy_setting_back)
    public void back() {
        this.finish();
    }

    @OnClick(R.id.deploy_setting_finish)
    public void doFinish() {
        doChoose();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            dismissInputMethodManager(v);
            doChoose();
            return true;
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(s)) {
            mSearchHistoryLayout.setVisibility(View.GONE);
            mSearchRelationLayout.setVisibility(View.VISIBLE);
            filterDeviceInfoByNameAndAddress(s.toString());
        } else {
            mSearchHistoryLayout.setVisibility(View.VISIBLE);
            mSearchRelationLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(View view, int position) {
        String text = mRelationAdapter.getData().get(position);
        mKeywordEt.setText(text);
        mRelationAdapter.getData().clear();
        mRelationAdapter.notifyDataSetChanged();
    }
}
