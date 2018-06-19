package com.sensoro.smartcity.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
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

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.SearchHistoryAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeploySettingContactActivityView;
import com.sensoro.smartcity.presenter.DeploySettingContactActivityPresenter;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SensoroToast;
import com.sensoro.smartcity.widget.SpacesItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeploySettingContactActivity extends BaseActivity<IDeploySettingContactActivityView,
        DeploySettingContactActivityPresenter> implements IDeploySettingContactActivityView,
        RecycleViewItemClickListener,
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
    private SearchHistoryAdapter mNameSearchHistoryAdapter;
    private SearchHistoryAdapter mPhoneSearchHistoryAdapter;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_deploy_setting_contact);
        ButterKnife.bind(mActivity);
        mPrestener.initData(mActivity);
        init();
    }


    @Override
    protected DeploySettingContactActivityPresenter createPresenter() {
        return new DeploySettingContactActivityPresenter();
    }

    private void init() {
        mNameEt.setOnEditorActionListener(this);
        mNameEt.addTextChangedListener(this);
        mPhoneEt.setOnEditorActionListener(this);
        mPhoneEt.addTextChangedListener(this);
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.x10);


        mNameSearchHistoryRv.setLayoutManager(layoutManager);
        mNameSearchHistoryRv.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mNameSearchHistoryAdapter = new SearchHistoryAdapter(mActivity, mPrestener.getNameHistoryKeywords(), new
                RecycleViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String name = mPrestener.getNameHistoryKeywords().get(position).trim();
                        setNameEditText(name);
                        mNameEt.clearFocus();
                        dismissInputMethodManager(view);
                    }
                });
        mNameSearchHistoryRv.setAdapter(mNameSearchHistoryAdapter);
        mNameEt.requestFocus();
        mNameEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mNameSearchHistoryRv.setVisibility(View.VISIBLE);
                mPhoneSearchHistoryRv.setVisibility(View.GONE);
                return false;
            }
        });

        //

        SensoroLinearLayoutManager layoutManager1 = new SensoroLinearLayoutManager(mActivity);
        mPhoneSearchHistoryRv.setLayoutManager(layoutManager1);
        mPhoneSearchHistoryRv.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mPhoneSearchHistoryAdapter = new SearchHistoryAdapter(mActivity, mPrestener.getPhoneHistoryKeywords(), new
                RecycleViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String phone = mPrestener.getPhoneHistoryKeywords().get(position).trim();
                        setPhoneEditText(phone);
                        mPhoneEt.clearFocus();
                        dismissInputMethodManager(view);
                    }
                });
        mPhoneSearchHistoryRv.setAdapter(mPhoneSearchHistoryAdapter);
        mPhoneEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mNameSearchHistoryRv.setVisibility(View.GONE);
                mPhoneSearchHistoryRv.setVisibility(View.VISIBLE);
                return false;
            }
        });
        updateAdapter();
    }


    private void dismissInputMethodManager(View view) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//从控件所在的窗口中隐藏
    }


    @OnClick(R.id.deploy_setting_contact_back)
    public void back() {
        mActivity.finish();
    }

    @OnClick(R.id.deploy_setting_contact_finish)
    public void doFinish() {
        String phoneStr = mPhoneEt.getText().toString();
        String nameStr = mNameEt.getText().toString();
        mPrestener.doFinish(nameStr, phoneStr);
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

    @Override
    public void setName(String name) {
        mNameEt.setText(name);
    }

    @Override
    public void setPhone(String phone) {
        mPhoneEt.setText(phone);
    }

    @Override
    public void setSearchHistoryLayoutVisible(boolean isVisible) {
        mSearchHistoryLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateAdapter() {
        mNameSearchHistoryAdapter.notifyDataSetChanged();
        mPhoneSearchHistoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void setNameEditText(String text) {
        if (text != null) {
            mNameEt.setText(text);
            mNameEt.setSelection(text.length());
        }
    }

    @Override
    public void setPhoneEditText(String text) {
        if (text != null) {
            mPhoneEt.setText(text);
            mPhoneEt.setSelection(text.length());
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

    }

    @Override
    public void setIntentResult(int requestCode, Intent data) {
        mActivity.setResult(requestCode, data);
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }
}
