package com.sensoro.smartcity.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.mobstat.StatService;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.widget.statusbar.StatusBarCompat;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sensoro on 17/11/7.
 */

public class DeployManualActivity extends BaseActivity implements Constants, TextView.OnEditorActionListener,
        TextWatcher {


    @BindView(R.id.deploy_manual_close)
    ImageView closeImageView;
    @BindView(R.id.deploy_clear_iv)
    ImageView clearImageView;
    @BindView(R.id.deploy_manual_et)
    EditText contentEditText;
    @BindView(R.id.deploy_manual_btn)
    Button nextButton;
    private ProgressDialog mProgressDialog;

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
    protected void onDestroy() {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
        super.onDestroy();
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
        String s = contentEditText.getText().toString();
        if (!TextUtils.isEmpty(s) && s.length() == 16) {
//            Intent intent = new Intent(this, DeployActivity.class);
//            intent.putExtra(EXTRA_SENSOR_SN, contentEditText.getText().toString().toUpperCase());
//            startActivity(intent);
            requestData(s);
        } else {
            Toast.makeText(this, "请输入正确的SN,SN为16个字符", Toast.LENGTH_SHORT).show();
        }

    }

    private void requestData(String scanSerialNumber) {
        if (TextUtils.isEmpty(scanSerialNumber)) {
            Toast.makeText(DeployManualActivity.this, R.string.invalid_qr_code, Toast.LENGTH_SHORT).show();
        } else {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.show();
            SensoroCityApplication.getInstance().smartCityServer.getDeviceDetailInfoList(scanSerialNumber.toUpperCase
                            (), null, 1,
                    new Response
                            .Listener<DeviceInfoListRsp>() {
                        @Override
                        public void onResponse(DeviceInfoListRsp response) {
                            if (mProgressDialog != null) {
                                mProgressDialog.dismiss();
                            }
                            refresh(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            if (mProgressDialog != null) {
                                mProgressDialog.dismiss();
                            }
                            if (volleyError.networkResponse != null) {
                                String reason = new String(volleyError.networkResponse.data);
                                try {
                                    JSONObject jsonObject = new JSONObject(reason);
                                    Toast.makeText(DeployManualActivity.this, jsonObject.getString("errmsg"), Toast
                                            .LENGTH_SHORT)
                                            .show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {

                                }
                            } else {
                                Toast.makeText(DeployManualActivity.this, R.string.tips_network_error, Toast
                                        .LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }

    private void refresh(DeviceInfoListRsp response) {
        try {
            Intent intent = new Intent();
            if (response.getData().size() > 0) {
                intent.setClass(this, DeployActivity.class);
                intent.putExtra(EXTRA_DEVICE_INFO, response.getData().get(0));
                intent.putExtra("uid", this.getIntent().getStringExtra("uid"));
                startActivityForResult(intent, REQUEST_CODE_DEPLOY);
                this.finish();
            } else {
                intent.setClass(this, DeployResultActivity.class);
                intent.putExtra(EXTRA_SENSOR_RESULT, -1);
                startActivityForResult(intent, REQUEST_CODE_DEPLOY);
                this.finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
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
        contentEditText.getText().clear();
    }

    @Override
    protected boolean isNeedSlide() {
        return true;
    }
}
