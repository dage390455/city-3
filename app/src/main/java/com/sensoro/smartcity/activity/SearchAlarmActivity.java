package com.sensoro.smartcity.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
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
import com.sensoro.smartcity.adapter.SearchAlarmPagerAdapter;
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

public class SearchAlarmActivity extends BaseActivity implements View.OnClickListener, Constants, TextView
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
    TabLayout searchTablayout;
    @BindView(R.id.search_viewpager)
    ViewPager searchViewpager;
    private SharedPreferences mPref;
    private Editor mEditor;
    private final List<String> mHistoryKeywords_deviceName = new ArrayList<>();
    private final List<String> mHistoryKeywords_deviceNumber = new ArrayList<>();
    private final List<String> mHistoryKeywords_devicePhone = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private SearchAlarmTagAdapter mAlarmTagAdapter;
    private SearchAlarmPagerAdapter searchAlarmPagerAdapter;
    private Long mStartTime = null;
    private Long mEndTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_alarm);
        ButterKnife.bind(this);
        mProgressDialog = new ProgressDialog(this);
        mPref = getApplicationContext().getSharedPreferences(PREFERENCE_ALARM_HISTORY, Activity.MODE_PRIVATE);
        mClearKeywordIv.setOnClickListener(this);
        mKeywordEt.setOnEditorActionListener(this);
        mKeywordEt.addTextChangedListener(this);
        mKeywordEt.requestFocus();
        mCancelTv.setOnClickListener(this);
        mClearBtn.setOnClickListener(this);
        long longStartTime = getIntent().getLongExtra(PREFERENCE_KEY_START_TIME, -1);
        long longEndTime = getIntent().getLongExtra(PREFERENCE_KEY_END_TIME, -1);
        if (longStartTime != -1) {
            mStartTime = longStartTime;
        }
        if (longEndTime != -1) {
            mEndTime = longEndTime;
        }
//        searchTablayout.setupWithViewPager(searchViewpager);
//        searchAlarmPagerAdapter = new SearchAlarmPagerAdapter(this, this.getSupportFragmentManager());
//        searchAlarmPagerAdapter.
//        searchViewpager.setAdapter(searchAlarmPagerAdapter);
        searchTablayout.addTab(searchTablayout.newTab().setText("设备名称"));
        searchTablayout.addTab(searchTablayout.newTab().setText("设备号"));
        searchTablayout.addTab(searchTablayout.newTab().setText("手机号"));
        searchTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        mKeywordEt.setHint("设备名称");
                        SensoroCityApplication.getInstance().searchType = Constants.TYPE_DEVICE_NAME;
                        refreshHistory(mHistoryKeywords_deviceName);
                        break;
                    case 1:
                        mKeywordEt.setHint("设备号");
                        SensoroCityApplication.getInstance().searchType = Constants.TYPE_DEVICE_NUMBER;
                        refreshHistory(mHistoryKeywords_deviceNumber);
                        break;
                    case 2:
                        mKeywordEt.setHint("手机号");
                        SensoroCityApplication.getInstance().searchType = Constants.TYPE_DEVICE_PHONE_NUM;
                        refreshHistory(mHistoryKeywords_devicePhone);
                        break;
                    default:
                        mKeywordEt.setHint("设备名称");
                        SensoroCityApplication.getInstance().searchType = Constants.TYPE_DEVICE_NAME;
                        refreshHistory(mHistoryKeywords_deviceName);
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
//        String extra_search_content = getIntent().getStringExtra("extra_search_content");
//        if (!TextUtils.isEmpty(extra_search_content)) {
//            mKeywordEt.setText(extra_search_content);
//        }
        initSearchHistory(mHistoryKeywords_deviceName);
        refreshHistory(mHistoryKeywords_deviceName);
        StatusBarCompat.setStatusBarColor(this);
        mEditor = mPref.edit();
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

    private void initSearchHistory(final List<String> searchStr) {
        String ori = mPref.getString(PREFERENCE_KEY_DEVICE_NAME, "");
        if (!TextUtils.isEmpty(ori)) {
            searchStr.addAll(Arrays.asList(ori.split(",")));
        }
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.x20);
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mSearchHistoryRv.setLayoutManager(layoutManager);
        mSearchHistoryRv.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mSearchHistoryAdapter = new SearchHistoryAdapter(this, searchStr, new
                RecycleViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        switch (SensoroCityApplication.getInstance().searchType) {
                            case Constants.TYPE_DEVICE_NAME:
                                mKeywordEt.setText(mHistoryKeywords_deviceName.get(position));
                                break;
                            case Constants.TYPE_DEVICE_NUMBER:
                                mKeywordEt.setText(mHistoryKeywords_deviceNumber.get(position));
                                break;
                            case Constants.TYPE_DEVICE_PHONE_NUM:
                                mKeywordEt.setText(mHistoryKeywords_devicePhone.get(position));
                                break;
                            default:
                                break;
                        }
                        mClearKeywordIv.setVisibility(View.VISIBLE);
                        mProgressDialog.show();
                        mKeywordEt.clearFocus();
                        dismissInputMethodManager(view);
                        save();
                        String text = mKeywordEt.getText().toString();
                        requestData(text);
                    }
                });
        mSearchHistoryRv.setAdapter(mSearchHistoryAdapter);
        mSearchHistoryAdapter.notifyDataSetChanged();
//        List<String> tagList = Arrays.asList(ALARM_TAG_ARRAY);
//        mAlarmTagAdapter = new SearchAlarmTagAdapter(this, tagList, new RecycleViewItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                String type = ALARM_TAG_EN_ARRAY[position];
//                Intent data = new Intent();
//                data.putExtra(EXTRA_SENSOR_TYPE, type);
//                data.putExtra(EXTRA_ALARM_SEARCH_INDEX, 1);
//                data.putExtra(EXTRA_ALARM_SEARCH_TEXT, "类型:" + ALARM_TAG_ARRAY[position]);
//                setResult(RESULT_CODE_SEARCH_ALARM, data);
//                finish();
//            }
//        });
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
//        mTagRv.setLayoutManager(gridLayoutManager);
//        mTagRv.addItemDecoration(new SpacesItemDecoration(10));
//        mTagRv.setAdapter(mAlarmTagAdapter);
        mKeywordEt.requestFocus();
    }

    /**
     * 刷新数据
     *
     * @param searchStr
     */
    private void refreshHistory(final List<String> searchStr) {
        String ori;
        switch (SensoroCityApplication.getInstance().searchType) {
            case Constants.TYPE_DEVICE_NAME:
                ori = mPref.getString(PREFERENCE_KEY_DEVICE_NAME, "");
                break;
            case Constants.TYPE_DEVICE_NUMBER:
                ori = mPref.getString(PREFERENCE_KEY_DEVICE_NUM, "");
                break;
            case Constants.TYPE_DEVICE_PHONE_NUM:
                ori = mPref.getString(PREFERENCE_KEY_DEVICE_PHONE, "");
                break;
            default:
                ori = mPref.getString(PREFERENCE_KEY_DEVICE_NAME, "");
                break;
        }

        searchStr.clear();
        if (!TextUtils.isEmpty(ori)) {
            List<String> strings = Arrays.asList(ori.split(","));
            searchStr.addAll(strings);
        }
        mSearchHistoryAdapter.setData(searchStr);
        mSearchHistoryAdapter.notifyDataSetChanged();
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
        String oldTestDeviceName;
        String oldTestDeviceNum;
        String oldTestDevicePhone;
        if (!TextUtils.isEmpty(text)) {
            switch (SensoroCityApplication.getInstance().searchType) {
                case Constants.TYPE_DEVICE_NAME:
                    oldTestDeviceName = mPref.getString(PREFERENCE_KEY_DEVICE_NAME, "");

                    if (mHistoryKeywords_deviceName.contains(text)) {
                        List<String> list = new ArrayList<String>();
                        for (String o : oldTestDeviceName.split(",")) {
                            if (!o.equalsIgnoreCase(text)) {
                                list.add(o);
                            }
                        }
                        mHistoryKeywords_deviceName.clear();
                        mHistoryKeywords_deviceName.addAll(list);
                        StringBuffer stringBuffer = new StringBuffer();
                        for (int i = 0; i < list.size(); i++) {
                            if (i == (list.size() - 1)) {
                                stringBuffer.append(list.get(i));
                            } else {
                                stringBuffer.append(list.get(i) + ",");
                            }
                        }
                        mEditor.putString(PREFERENCE_KEY_DEVICE_NAME, stringBuffer.toString());
                        mEditor.commit();
                    } else {
                        if (TextUtils.isEmpty(oldTestDeviceName)) {
                            mEditor.putString(PREFERENCE_KEY_DEVICE_NAME, text);
                        } else {
                            mEditor.putString(PREFERENCE_KEY_DEVICE_NAME, text + "," + oldTestDeviceName);
                        }
                        mEditor.commit();
                        mHistoryKeywords_deviceName.add(0, text);
                    }
                    break;
                case Constants.TYPE_DEVICE_NUMBER:
                    oldTestDeviceNum = mPref.getString(PREFERENCE_KEY_DEVICE_NUM, "");
                    if (mHistoryKeywords_deviceNumber.contains(text)) {
                        List<String> list = new ArrayList<String>();
                        for (String o : oldTestDeviceNum.split(",")) {
                            if (!o.equalsIgnoreCase(text)) {
                                list.add(o);
                            }
                        }
                        list.add(0, text);
                        mHistoryKeywords_deviceNumber.clear();
                        mHistoryKeywords_deviceNumber.addAll(list);
                        StringBuffer stringBuffer = new StringBuffer();
                        for (int i = 0; i < list.size(); i++) {
                            if (i == (list.size() - 1)) {
                                stringBuffer.append(list.get(i));
                            } else {
                                stringBuffer.append(list.get(i) + ",");
                            }
                        }
                        mEditor.putString(PREFERENCE_KEY_DEVICE_NUM, stringBuffer.toString());
                        mEditor.commit();
                    } else {
                        if (TextUtils.isEmpty(oldTestDeviceNum)) {
                            mEditor.putString(PREFERENCE_KEY_DEVICE_NUM, text);
                        } else {
                            mEditor.putString(PREFERENCE_KEY_DEVICE_NUM, text + "," + oldTestDeviceNum);
                        }
                        mEditor.commit();
                        mHistoryKeywords_deviceNumber.add(0, text);
                    }
                    break;
                case Constants.TYPE_DEVICE_PHONE_NUM:
                    oldTestDevicePhone = mPref.getString(PREFERENCE_KEY_DEVICE_PHONE, "");
                    if (mHistoryKeywords_devicePhone.contains(text)) {
                        List<String> list = new ArrayList<String>();
                        for (String o : oldTestDevicePhone.split(",")) {
                            if (!o.equalsIgnoreCase(text)) {
                                list.add(o);
                            }
                        }
                        list.add(0, text);
                        mHistoryKeywords_devicePhone.clear();
                        mHistoryKeywords_devicePhone.addAll(list);
                        StringBuffer stringBuffer = new StringBuffer();
                        for (int i = 0; i < list.size(); i++) {
                            if (i == (list.size() - 1)) {
                                stringBuffer.append(list.get(i));
                            } else {
                                stringBuffer.append(list.get(i) + ",");
                            }
                        }
                        mEditor.putString(PREFERENCE_KEY_DEVICE_PHONE, stringBuffer.toString());
                        mEditor.commit();
                    } else {
                        if (TextUtils.isEmpty(oldTestDevicePhone)) {
                            mEditor.putString(PREFERENCE_KEY_DEVICE_PHONE, text);
                        } else {
                            mEditor.putString(PREFERENCE_KEY_DEVICE_PHONE, text + "," + oldTestDevicePhone);
                        }
                        mEditor.commit();
                        mHistoryKeywords_devicePhone.add(0, text);
                    }
                    break;
                default:
                    oldTestDeviceName = mPref.getString(PREFERENCE_KEY_DEVICE_NAME, "");
                    if (mHistoryKeywords_deviceName.contains(text)) {
                        List<String> list = new ArrayList<String>();
                        for (String o : oldTestDeviceName.split(",")) {
                            if (!o.equalsIgnoreCase(text)) {
                                list.add(o);
                            }
                        }
                        list.add(0, text);
                        mHistoryKeywords_deviceName.clear();
                        mHistoryKeywords_deviceName.addAll(list);
                        StringBuffer stringBuffer = new StringBuffer();
                        for (int i = 0; i < list.size(); i++) {
                            if (i == (list.size() - 1)) {
                                stringBuffer.append(list.get(i));
                            } else {
                                stringBuffer.append(list.get(i) + ",");
                            }
                        }
                        mEditor.putString(PREFERENCE_KEY_DEVICE_NAME, stringBuffer.toString());
                        mEditor.commit();
                    } else {
                        if (TextUtils.isEmpty(oldTestDeviceName)) {
                            mEditor.putString(PREFERENCE_KEY_DEVICE_NAME, text);
                        } else {
                            mEditor.putString(PREFERENCE_KEY_DEVICE_NAME, text + "," + oldTestDeviceName);
                        }
                        mEditor.commit();
                        mHistoryKeywords_deviceName.add(0, text);
                    }
                    break;
            }
        }

    }

    private void cleanHistory() {
        switch (SensoroCityApplication.getInstance().searchType) {
            case Constants.TYPE_DEVICE_NAME:
                mEditor.putString(PREFERENCE_KEY_DEVICE_NAME, "");
                mHistoryKeywords_deviceName.clear();
                mEditor.commit();
                mSearchHistoryAdapter.notifyDataSetChanged();
                mSearchHistoryLayout.setVisibility(View.GONE);
                break;
            case Constants.TYPE_DEVICE_NUMBER:
                mEditor.putString(PREFERENCE_KEY_DEVICE_NUM, "");
                mHistoryKeywords_deviceNumber.clear();
                mEditor.commit();
                mSearchHistoryAdapter.notifyDataSetChanged();
                mSearchHistoryLayout.setVisibility(View.GONE);
                break;
            case Constants.TYPE_DEVICE_PHONE_NUM:
                mEditor.putString(PREFERENCE_KEY_DEVICE_PHONE, "");
                mHistoryKeywords_devicePhone.clear();
                mEditor.commit();
                mSearchHistoryAdapter.notifyDataSetChanged();
                mSearchHistoryLayout.setVisibility(View.GONE);
                break;
            default:
                mEditor.putString(PREFERENCE_KEY_DEVICE_NAME, "");
                mHistoryKeywords_deviceName.clear();
                mEditor.commit();
                mSearchHistoryAdapter.notifyDataSetChanged();
                mSearchHistoryLayout.setVisibility(View.GONE);
                break;
        }

    }

    public void requestData(final String text) {
        switch (SensoroCityApplication.getInstance().searchType) {
            case Constants.TYPE_DEVICE_NAME:
                mProgressDialog.show();
                SensoroCityApplication.getInstance().smartCityServer.getDeviceAlarmLogListByDeviceName(mStartTime,
                        mEndTime,
                        text, null, 1, new
                                Response.Listener<DeviceAlarmLogRsp>() {
                                    @Override
                                    public void onResponse(DeviceAlarmLogRsp response) {
                                        mProgressDialog.dismiss();
                                        if (response.getData().size() == 0) {
                                            tipsLinearLayout.setVisibility(View.VISIBLE);
//                            tagLinearLayout.setVisibility(View.GONE);
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
                                mProgressDialog.dismiss();
                                if (error.networkResponse != null) {
                                    String reason = new String(error.networkResponse.data);
                                    try {
                                        JSONObject jsonObject = new JSONObject(reason);
                                        Toast.makeText(SearchAlarmActivity.this, jsonObject.getString("errmsg"), Toast
                                                .LENGTH_SHORT)
                                                .show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {

                                    }
                                } else {
                                    Toast.makeText(SearchAlarmActivity.this, R.string.tips_network_error, Toast
                                            .LENGTH_SHORT)
                                            .show();
                                }

                            }
                        });
                break;
            case Constants.TYPE_DEVICE_NUMBER:
                mProgressDialog.show();
                SensoroCityApplication.getInstance().smartCityServer.getDeviceAlarmLogList(mStartTime, mEndTime,
                        text, null, 1,
                        new
                                Response.Listener<DeviceAlarmLogRsp>() {
                                    @Override
                                    public void onResponse(DeviceAlarmLogRsp response) {
                                        mProgressDialog.dismiss();
                                        if (response.getData().size() == 0) {
                                            tipsLinearLayout.setVisibility(View.VISIBLE);
//                            tagLinearLayout.setVisibility(View.GONE);
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
                                mProgressDialog.dismiss();
                                if (error.networkResponse != null) {
                                    String reason = new String(error.networkResponse.data);
                                    try {
                                        JSONObject jsonObject = new JSONObject(reason);
                                        Toast.makeText(SearchAlarmActivity.this, jsonObject.getString("errmsg"), Toast
                                                .LENGTH_SHORT)
                                                .show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {

                                    }
                                } else {
                                    Toast.makeText(SearchAlarmActivity.this, R.string.tips_network_error, Toast
                                            .LENGTH_SHORT)
                                            .show();
                                }

                            }
                        });
                break;
            case Constants.TYPE_DEVICE_PHONE_NUM:
                mProgressDialog.show();
                SensoroCityApplication.getInstance().smartCityServer.getDeviceAlarmLogListByDevicePhone(mStartTime,
                        mEndTime,
                        text, null, 1, new
                                Response.Listener<DeviceAlarmLogRsp>() {
                                    @Override
                                    public void onResponse(DeviceAlarmLogRsp response) {
                                        mProgressDialog.dismiss();
                                        if (response.getData().size() == 0) {
                                            tipsLinearLayout.setVisibility(View.VISIBLE);
//                            tagLinearLayout.setVisibility(View.GONE);
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
                                mProgressDialog.dismiss();
                                if (error.networkResponse != null) {
                                    String reason = new String(error.networkResponse.data);
                                    try {
                                        JSONObject jsonObject = new JSONObject(reason);
                                        Toast.makeText(SearchAlarmActivity.this, jsonObject.getString("errmsg"), Toast
                                                .LENGTH_SHORT)
                                                .show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {

                                    }
                                } else {
                                    Toast.makeText(SearchAlarmActivity.this, R.string.tips_network_error, Toast
                                            .LENGTH_SHORT)
                                            .show();
                                }

                            }
                        });
                break;
            default:
                break;
        }

    }

    @Override
    protected void onDestroy() {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
        super.onDestroy();
    }

    public void test() {
        Toast.makeText(this, "ddddd", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_alarm_clear_btn:
                cleanHistory();
                break;
            case R.id.search_alarm_cancel_tv:
                mKeywordEt.clearFocus();
//                Intent data = new Intent();
//                data.putExtra(EXTRA_ACTIVITY_CANCEL, true);
//                setResult(RESULT_CODE_SEARCH_ALARM, data);
                finish();
                break;
            case R.id.search_alarm_clear_iv:
                mKeywordEt.getText().clear();
                mSearchHistoryAdapter.notifyDataSetChanged();
                mClearKeywordIv.setVisibility(View.GONE);
                tipsLinearLayout.setVisibility(View.GONE);
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
            save();
            mClearKeywordIv.setVisibility(View.VISIBLE);
            mKeywordEt.clearFocus();
            dismissInputMethodManager(v);
            String text = mKeywordEt.getText().toString();
            if (!TextUtils.isEmpty(text)){
                requestData(text);
            }
            return true;
        }
        return false;
    }


}
