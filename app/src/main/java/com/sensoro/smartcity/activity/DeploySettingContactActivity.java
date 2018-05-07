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
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.SearchHistoryAdapter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SpacesItemDecoration;
import com.sensoro.smartcity.widget.statusbar.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeploySettingContactActivity extends BaseActivity implements Constants, RecycleViewItemClickListener,
        TextView.OnEditorActionListener, TextWatcher {


    @BindView(R.id.deploy_setting_contact_back)
    ImageView backImageView;
    @BindView(R.id.deploy_setting_contact_et)
    EditText mNameEt;
    @BindView(R.id.deploy_setting_content_et)
    EditText mPhoneEt;
    @BindView(R.id.deploy_setting_contact_history_rv)
    RecyclerView mNameSearchHistoryRv;
    @BindView(R.id.deploy_setting_content_history_rv)
    RecyclerView mPhoneSearchHistoryRv;
    @BindView(R.id.deploy_setting_contact_history_layout)
    LinearLayout mSearchHistoryLayout;

    private SharedPreferences mNamePref;
    private SharedPreferences mPhonePref;
    private SharedPreferences.Editor mNameEditor;
    private SharedPreferences.Editor mPhoneEditor;
    private List<String> mNameHistoryKeywords = new ArrayList<>();
    private List<String> mPhoneHistoryKeywords = new ArrayList<>();
    private SearchHistoryAdapter mNameSearchHistoryAdapter;
    private SearchHistoryAdapter mPhoneSearchHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deploy_setting_contact);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    private void init() {
        try {
            mNamePref = getApplicationContext().getApplicationContext().getSharedPreferences
                    (PREFERENCE_DEPLOY_CONTACT_HISTORY, Activity.MODE_PRIVATE);
            mNameEditor = mNamePref.edit();
            mPhonePref = getSharedPreferences(PREFERENCE_DEPLOY_CONTENT_HISTORY, Activity.MODE_PRIVATE);
            mPhoneEditor = mPhonePref.edit();
            initSearchNameHistory();
            initSearchPhoneHistory();
            String contact = getIntent().getStringExtra(EXTRA_SETTING_CONTACT);
            String content = getIntent().getStringExtra(EXTRA_SETTING_CONTENT);
            if (contact != null) {
                mNameEt.setText(contact);
            }
            if (content != null) {
                mPhoneEt.setText(content);
            }
            mNameEt.setOnEditorActionListener(this);
            mNameEt.addTextChangedListener(this);
            mPhoneEt.setOnEditorActionListener(this);
            mPhoneEt.addTextChangedListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean isNeedSlide() {
        return true;
    }

    private void initSearchNameHistory() {
        String history = mNamePref.getString(PREFERENCE_KEY_DEPLOY, "");
        if (!TextUtils.isEmpty(history)) {
            for (Object o : history.split(",")) {
                mNameHistoryKeywords.add((String) o);
            }
        }
        if (mNameHistoryKeywords.size() > 0) {
            mSearchHistoryLayout.setVisibility(View.VISIBLE);
        } else {
            mSearchHistoryLayout.setVisibility(View.GONE);
        }
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mNameSearchHistoryRv.setLayoutManager(layoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.x10);
        mNameSearchHistoryRv.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mNameSearchHistoryAdapter = new SearchHistoryAdapter(this, mNameHistoryKeywords, new
                RecycleViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mNameEt.setText(mNameHistoryKeywords.get(position).trim());
                        mNameEt.clearFocus();
                        mNameEt.setSelection(mNameHistoryKeywords.get(position).trim().length());
                        dismissInputMethodManager(view);
                    }
                });
        mNameSearchHistoryRv.setAdapter(mNameSearchHistoryAdapter);
        mNameSearchHistoryAdapter.notifyDataSetChanged();
        mNameEt.requestFocus();
        mNameEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mNameSearchHistoryRv.setVisibility(View.VISIBLE);
                mPhoneSearchHistoryRv.setVisibility(View.GONE);
                return false;
            }
        });
    }

    private void initSearchPhoneHistory() {
        String history = mPhonePref.getString(PREFERENCE_KEY_DEPLOY, "");
        if (!TextUtils.isEmpty(history)) {
            for (Object o : history.split(",")) {
                mPhoneHistoryKeywords.add((String) o);
            }
        }
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mPhoneSearchHistoryRv.setLayoutManager(layoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.x10);
        mPhoneSearchHistoryRv.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mPhoneSearchHistoryAdapter = new SearchHistoryAdapter(this, mPhoneHistoryKeywords, new
                RecycleViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mPhoneEt.setText(mPhoneHistoryKeywords.get(position).trim());
                        mPhoneEt.clearFocus();
                        mPhoneEt.setSelection(mPhoneHistoryKeywords.get(position).trim().length());
                        dismissInputMethodManager(view);
                    }
                });
        mPhoneSearchHistoryRv.setAdapter(mPhoneSearchHistoryAdapter);
        mPhoneSearchHistoryAdapter.notifyDataSetChanged();
        mPhoneEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mNameSearchHistoryRv.setVisibility(View.GONE);
                mPhoneSearchHistoryRv.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }

    private void dismissInputMethodManager(View view) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//从控件所在的窗口中隐藏
    }

    private void saveName() {
        String text = mNameEt.getText().toString();
        String oldText = mNamePref.getString(PREFERENCE_KEY_DEPLOY, "");
        if (!TextUtils.isEmpty(text)) {
            if (mNameHistoryKeywords.contains(text)) {
                mNameHistoryKeywords.clear();
                for (String o : oldText.split(",")) {
                    if (!o.equalsIgnoreCase(text)) {
                        mNameHistoryKeywords.add(o);
                    }
                }
                mNameHistoryKeywords.add(0, text);
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < mNameHistoryKeywords.size(); i++) {
                    if (i == (mNameHistoryKeywords.size() - 1)) {
                        stringBuffer.append(mNameHistoryKeywords.get(i));
                    } else {
                        stringBuffer.append(mNameHistoryKeywords.get(i) + ",");
                    }
                }
                mNameEditor.putString(PREFERENCE_KEY_DEPLOY, stringBuffer.toString());
                mNameEditor.commit();
            } else {
                mNameEditor.putString(PREFERENCE_KEY_DEPLOY, text + "," + oldText);
                mNameEditor.commit();
                mNameHistoryKeywords.add(0, text);
            }
        }
    }

    private void savePhone() {
        String text = mPhoneEt.getText().toString();
        String oldText = mPhonePref.getString(PREFERENCE_KEY_DEPLOY, "");
        if (!TextUtils.isEmpty(text)) {
            if (mPhoneHistoryKeywords.contains(text)) {
                mPhoneHistoryKeywords.clear();
                for (String o : oldText.split(",")) {
                    if (!o.equalsIgnoreCase(text)) {
                        mPhoneHistoryKeywords.add(o);
                    }
                }
                mPhoneHistoryKeywords.add(0, text);
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < mPhoneHistoryKeywords.size(); i++) {
                    if (i == (mPhoneHistoryKeywords.size() - 1)) {
                        stringBuffer.append(mPhoneHistoryKeywords.get(i));
                    } else {
                        stringBuffer.append(mPhoneHistoryKeywords.get(i) + ",");
                    }
                }
                mPhoneEditor.putString(PREFERENCE_KEY_DEPLOY, stringBuffer.toString());
                mPhoneEditor.commit();
            } else {
                mPhoneEditor.putString(PREFERENCE_KEY_DEPLOY, text + "," + oldText);
                mPhoneEditor.commit();
                mPhoneHistoryKeywords.add(0, text);
            }
        }
    }


    @OnClick(R.id.deploy_setting_contact_back)
    public void back() {
        this.finish();
    }

    @OnClick(R.id.deploy_setting_contact_finish)
    public void doFinish() {
        String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,1,2,5-9])|(177)|(171)|(176))\\d{8}$";
        Pattern p = Pattern.compile(regex);
        String phoneStr = mPhoneEt.getText().toString();
        String nameStr = mNameEt.getText().toString();
        if (!TextUtils.isEmpty(phoneStr) && p.matcher(phoneStr).matches() && !TextUtils.isEmpty(nameStr)) {
            saveName();
            savePhone();
            mNameEt.clearFocus();
            mPhoneEt.clearFocus();
            Intent data = new Intent();
            data.putExtra(EXTRA_SETTING_CONTACT, nameStr.trim());
            data.putExtra(EXTRA_SETTING_CONTENT, phoneStr.trim());
            setResult(RESULT_CODE_SETTING_CONTACT, data);
            finish();
        } else {
            Toast.makeText(this, R.string.tips_phone_empty, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            dismissInputMethodManager(v);
            return true;
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        if (!s.toString().equals("")) {
//            mSearchHistoryLayout.setVisibility(View.GONE);
//        } else {
//            mSearchHistoryLayout.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(View view, int position) {
    }
}
