package com.sensoro.smartcity.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
import com.sensoro.smartcity.adapter.SearchAlarmTagAdapter;
import com.sensoro.smartcity.adapter.SearchHistoryAdapter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.response.DeviceAlarmLogRsp;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SpacesItemDecoration;
import com.sensoro.smartcity.widget.statusbar.StatusBarCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sensoro on 17/7/11.
 */

public class SearchAlarmActivity extends BaseActivity implements View.OnClickListener, Constants, TextView.OnEditorActionListener, TextWatcher{

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

    private SharedPreferences mPref;
    private Editor mEditor;
    private List<String> mHistoryKeywords = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private SearchAlarmTagAdapter mAlarmTagAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_alarm);
        ButterKnife.bind(this);
        mProgressDialog = new ProgressDialog(this);
        mPref = getSharedPreferences(PREFERENCE_ALARM_HISTORY, Activity.MODE_PRIVATE);
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
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.x20);
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mSearchHistoryRv.setLayoutManager(layoutManager);
        mSearchHistoryRv.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mSearchHistoryAdapter = new SearchHistoryAdapter(this, mHistoryKeywords, new RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mKeywordEt.setText(mHistoryKeywords.get(position));
                mClearKeywordIv.setVisibility(View.VISIBLE);
                mProgressDialog.show();
                mKeywordEt.clearFocus();
                dismissInputMethodManager(view);
                requestData(mKeywordEt.getText().toString());
                save();
            }
        });
        mSearchHistoryRv.setAdapter(mSearchHistoryAdapter);
        mSearchHistoryAdapter.notifyDataSetChanged();
        List<String> tagList = Arrays.asList(ALARM_TAG_ARRAY);
        mAlarmTagAdapter = new SearchAlarmTagAdapter(this, tagList, new RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String type = ALARM_TAG_EN_ARRAY[position];
                Intent data = new Intent();
                data.putExtra(EXTRA_SENSOR_TYPE, type);
                data.putExtra(EXTRA_ALARM_SEARCH_INDEX, 1);
                data.putExtra(EXTRA_ALARM_SEARCH_TEXT, "类型:" + ALARM_TAG_ARRAY[position]);
                setResult(RESULT_CODE_SEARCH_ALARM, data);
                finish();
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        mTagRv.setLayoutManager(gridLayoutManager);
        mTagRv.addItemDecoration(new SpacesItemDecoration(10));
        mTagRv.setAdapter(mAlarmTagAdapter);
        mKeywordEt.requestFocus();
    }

    @Override
    protected boolean isNeedSlide() {
        return true;
    }

    private void dismissInputMethodManager(View view) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//从控件所在的窗口中隐藏
    }

    private void save() {
        String text = mKeywordEt.getText().toString();
        String oldText = mPref.getString(PREFERENCE_KEY_DEVICE, "");
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
                mEditor.putString(PREFERENCE_KEY_DEVICE, stringBuffer.toString());
                mEditor.commit();
            } else {
                mEditor.putString(PREFERENCE_KEY_DEVICE, text + "," + oldText);
                mEditor.commit();
                mHistoryKeywords.add(0, text);
            }
        }
    }



    private void cleanHistory() {
        mEditor.clear();
        mHistoryKeywords.clear();
        mEditor.commit();
        mSearchHistoryAdapter.notifyDataSetChanged();
        mSearchHistoryLayout.setVisibility(View.GONE);
    }

    public void requestData(final String text) {
        mProgressDialog.show();
        SensoroCityApplication sensoroCityApplication = (SensoroCityApplication) getApplication();
        sensoroCityApplication.smartCityServer.getDeviceAlarmLogList(null, null, text, null, 1, new Response.Listener<DeviceAlarmLogRsp>() {
            @Override
            public void onResponse(DeviceAlarmLogRsp response) {
                mProgressDialog.dismiss();
                if (response.getData().size() == 0) {
                    tipsLinearLayout.setVisibility(View.VISIBLE);
                    tagLinearLayout.setVisibility(View.GONE);
                } else {
                    Intent data = new Intent();
                    data.putExtra(EXTRA_ALARM_INFO, response);
                    data.putExtra(EXTRA_ALARM_SEARCH_INDEX, 0);
                    data.putExtra(EXTRA_ALARM_SEARCH_TEXT, text);
                    setResult(RESULT_CODE_SEARCH_ALARM, data);
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    String reason = new String(error.networkResponse.data);
                    try {
                        JSONObject jsonObject = new JSONObject(reason);
                        Toast.makeText(SearchAlarmActivity.this, jsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }catch (Exception e){

                    }
                } else {
                    Toast.makeText(SearchAlarmActivity.this, R.string.tips_network_error, Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();
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
            case R.id.search_alarm_clear_btn:
                cleanHistory();
                break;
            case R.id.search_alarm_cancel_tv:
                mKeywordEt.clearFocus();
                Intent data = new Intent();
                data.putExtra(EXTRA_ACTIVITY_CANCEL, true);
                setResult(RESULT_CODE_SEARCH_ALARM, data);
                finish();
                break;
            case R.id.search_alarm_clear_iv:
                mKeywordEt.setText("");
                mSearchHistoryAdapter.notifyDataSetChanged();
                mClearKeywordIv.setVisibility(View.GONE);
                tipsLinearLayout.setVisibility(View.GONE);
                tagLinearLayout.setVisibility(View.VISIBLE);
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
