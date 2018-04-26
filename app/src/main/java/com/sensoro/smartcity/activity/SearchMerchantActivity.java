package com.sensoro.smartcity.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.mobstat.StatService;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.adapter.SearchHistoryAdapter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.UserInfo;
import com.sensoro.smartcity.server.response.UserAccountRsp;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SpacesItemDecoration;
import com.sensoro.smartcity.widget.statusbar.StatusBarCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sensoro on 17/7/11.
 */

public class SearchMerchantActivity extends BaseActivity implements View.OnClickListener, Constants, TextView.OnEditorActionListener, TextWatcher {

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
    private SharedPreferences mPref;
    private Editor mEditor;
    private List<String> mHistoryKeywords = new ArrayList<>();
    private ProgressDialog progressDialog;
    private SearchHistoryAdapter mSearchHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_merchant);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        mPref = getSharedPreferences(PREFERENCE_MERCHANT_HISTORY, Activity.MODE_PRIVATE);
        mEditor = mPref.edit();
        mClearKeywordIv.setOnClickListener(this);
        mKeywordEt.setOnEditorActionListener(this);
        mKeywordEt.addTextChangedListener(this);
        mKeywordEt.requestFocus();
        mCancelTv.setOnClickListener(this);
        mClearBtn.setOnClickListener(this);
        initSearchHistory();
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

    @Override
    protected boolean isNeedSlide() {
        return true;
    }

    private void initSearchHistory() {
        String history = mPref.getString(PREFERENCE_KEY_DEVICE, "");
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
        mSearchHistoryRv.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
        mSearchHistoryAdapter = new SearchHistoryAdapter(this, mHistoryKeywords, new RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mKeywordEt.setText(mHistoryKeywords.get(position));
                mClearKeywordIv.setVisibility(View.VISIBLE);
                progressDialog.show();
                mKeywordEt.clearFocus();
                dismissInputMethodManager(view);
                requestData(mKeywordEt.getText().toString());
            }
        });
        mSearchHistoryRv.setAdapter(mSearchHistoryAdapter);
        mSearchHistoryAdapter.notifyDataSetChanged();
        mKeywordEt.requestFocus();
        //弹出框value unit对齐，搜索框有内容点击历史搜索出现没有搜索内容
    }

    private void dismissInputMethodManager(View view) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//从控件所在的窗口中隐藏
    }

    public void save() {
        String text = mKeywordEt.getText().toString();
        String oldText = mPref.getString(PREFERENCE_KEY_DEVICE, "");
        if (!TextUtils.isEmpty(text)) {
            if (mHistoryKeywords.contains(text)) {
                mHistoryKeywords.clear();
                for (String o : oldText.split(",")) {
                    if (!o.equalsIgnoreCase(text)) {
                        mHistoryKeywords.add(o);
                    }
                }
                mHistoryKeywords.add(0, text);
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < mHistoryKeywords.size(); i++) {
                    if (i == (mHistoryKeywords.size() - 1)) {
                        stringBuffer.append(mHistoryKeywords.get(i));
                    } else {
                        stringBuffer.append(mHistoryKeywords.get(i) + ",");
                    }
                }
                mEditor.putString(PREFERENCE_KEY_DEVICE, stringBuffer.toString());
                mEditor.commit();
            } else {
                mEditor.putString(PREFERENCE_KEY_DEVICE, text + "," + oldText);
                mEditor.commit();
                mHistoryKeywords.add(0, text);
            }
            mSearchHistoryAdapter.notifyDataSetChanged();
        }
    }

    public void cleanHistory() {
        mEditor.clear();
        mHistoryKeywords.clear();
        mEditor.commit();
        mSearchHistoryAdapter.notifyDataSetChanged();
        mSearchHistoryLayout.setVisibility(View.GONE);
    }

    public void requestData(String text) {
        progressDialog.show();
        SensoroCityApplication sensoroCityApplication = (SensoroCityApplication) getApplication();
        sensoroCityApplication.smartCityServer.getUserAccountList(text, null, null, null, "100000", new Response.Listener<UserAccountRsp>() {
            @Override
            public void onResponse(UserAccountRsp response) {
                progressDialog.dismiss();
                List<UserInfo> list = response.getData();
                if (list.size() == 0) {
                    tipsLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    Intent data = new Intent();
                    data.putExtra(EXTRA_MERCHANT_INFO, response);
                    setResult(RESULT_CODE_SEARCH_MERCHANT, data);
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                if (volleyError.networkResponse != null) {
                    String reason = new String(volleyError.networkResponse.data);
                    try {
                        JSONObject jsonObject = new JSONObject(reason);
                        Toast.makeText(getApplicationContext(), jsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }catch (Exception e){

                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.tips_network_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_merchant_clear_btn:
                cleanHistory();
                break;
            case R.id.search_merchant_cancel_tv:
                mKeywordEt.clearFocus();
                finish();
                break;
            case R.id.search_merchant_clear_iv:
                mKeywordEt.setText("");
                mClearKeywordIv.setVisibility(View.GONE);
                tipsLinearLayout.setVisibility(View.GONE);
                mSearchHistoryAdapter.notifyDataSetChanged();
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
            save();
            mClearKeywordIv.setVisibility(View.VISIBLE);
            mKeywordEt.clearFocus();
            dismissInputMethodManager(v);
            requestData(mKeywordEt.getText().toString());
            return true;
        }
        return false;
    }

}
