package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.widget.statusbar.StatusBarCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sensoro on 17/11/7.
 */

public class DeployManualActivity extends BaseActivity implements Constants, TextView.OnEditorActionListener, TextWatcher {


    @BindView(R.id.deploy_manual_close)
    ImageView closeImageView;
    @BindView(R.id.deploy_clear_iv)
    ImageView clearImageView;
    @BindView(R.id.deploy_manual_et)
    EditText contentEditText;
    @BindView(R.id.deploy_manual_btn)
    Button nextButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deploy_manual);
        ButterKnife.bind(this);
        contentEditText.setOnEditorActionListener(this);
        contentEditText.addTextChangedListener(this);
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

    @OnClick(R.id.deploy_manual_close)
    public void close() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CONTAINS_DATA, false);
        setResult(RESULT_CODE_DEPLOY, intent);
        this.finish();
    }

    @OnClick(R.id.deploy_manual_btn)
    public void next() {
        if (contentEditText.getText().toString().length() == 16) {
            Intent intent = new Intent(this, DeployActivity.class);
            intent.putExtra(EXTRA_SENSOR_SN, contentEditText.getText().toString().toUpperCase());
            startActivity(intent);
            this.finish();
        } else {
            Toast.makeText(this, "请输入正确的SN,SN为16个字符", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            close();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        if (s.length() == 0) {
            clearImageView.setVisibility(View.GONE);
            nextButton.setBackground(getResources().getDrawable(R.drawable.shape_button_normal));
        } else {
            clearImageView.setVisibility(View.VISIBLE);
            nextButton.setBackground(getResources().getDrawable(R.drawable.shape_button));
        }
    }

    @OnClick(R.id.deploy_clear_iv)
    public void clear() {
        contentEditText.setText("");
    }

    @Override
    protected boolean isNeedSlide() {
        return true;
    }
}
