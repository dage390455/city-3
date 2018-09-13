package com.sensoro.smartcity.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.ILoginView;
import com.sensoro.smartcity.presenter.LoginPresenter;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PermissionUtils;
import com.sensoro.smartcity.util.PermissionsResultObserve;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroImageView;
import com.sensoro.smartcity.widget.SensoroToast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sensoro on 17/7/24.
 */

public class LoginActivity extends BaseActivity<ILoginView, LoginPresenter> implements ILoginView, PermissionsResultObserve {

    @BindView(R.id.login_email)
    EditText accountEt;
    @BindView(R.id.login_pwd)
    EditText pwdEt;
    @BindView(R.id.login_bg_iv)
    SensoroImageView bgImageView;
    @BindView(R.id.login_cover)
    View coverView;
    @BindView(R.id.login_btn)
    Button login_btn;
    private ProgressUtils mProgressUtils;
    private static final int MY_REQUEST_PERMISSION_CODE = 0x14;
    private static final ArrayList<String> FORCE_REQUIRE_PERMISSIONS = new ArrayList<String>() {
        {
            add(Manifest.permission.INTERNET);
            add(Manifest.permission.READ_EXTERNAL_STORAGE);
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            add(Manifest.permission.ACCESS_FINE_LOCATION);
            add(Manifest.permission.ACCESS_COARSE_LOCATION);
            add(Manifest.permission.READ_PHONE_STATE);
            add(Manifest.permission.CAMERA);
            add(Manifest.permission.VIBRATE);
            add(Manifest.permission.RECORD_AUDIO);
            add(Manifest.permission.CALL_PHONE);
        }
    };
    private PermissionUtils mPermissionUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        ButterKnife.bind(mActivity);
        mPermissionUtils = new PermissionUtils(mActivity);
        mPermissionUtils.registerObserver(this);
        mPermissionUtils.requestPermission(FORCE_REQUIRE_PERMISSIONS, true, MY_REQUEST_PERMISSION_CODE);
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionUtils.onRequestPermissionsResult(MY_REQUEST_PERMISSION_CODE, requestCode, permissions, grantResults,
                FORCE_REQUIRE_PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionUtils.onActivityResult(requestCode, resultCode, data, MY_REQUEST_PERMISSION_CODE);
    }


    @Override
    protected LoginPresenter createPresenter() {
        return new LoginPresenter();
    }


    private long[] mHits = new long[7]; // 数组长度代表点击次数
    private boolean isShowPasswordDialog = true;

    @OnClick(R.id.login_logo)
    public void showSwitchApi() {
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - 1200)) {
            showPasswordDialog();
        }
    }

    private void showPasswordDialog() {
        if (isShowPasswordDialog) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            View view = View.inflate(mActivity, R.layout.dialog_input, null);
            builder.setView(view);
            builder.setCancelable(true);
            final EditText input_edt = (EditText) view
                    .findViewById(R.id.dialog_edit);//输入内容
            Button btn_cancel = (Button) view
                    .findViewById(R.id.btn_cancel);//取消按钮
            Button btn_confirm = (Button) view
                    .findViewById(R.id.btn_confirm);//确定按钮
            //取消或确定按钮监听事件处理
            final AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            btn_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str = input_edt.getText().toString();
                    if (str.equals("SENSORO")) {
                        switchApi();
                    } else {
                        toastLong("密码错误！");
                    }
                    dialog.cancel();
                    isShowPasswordDialog = true;
                }
            });
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isShowPasswordDialog = true;
                    dialog.cancel();
                }
            });
        }
        isShowPasswordDialog = false;
    }

    private int scope_selectedIndex = 0;

    private void switchApi() {
        final String[] urlArr = new String[]{"正式版", "Demo版", "测试版", "摩卡环境"};
        //
        scope_selectedIndex = RetrofitServiceHelper.INSTANCE.getBaseUrlType();
        Dialog alertDialog = new AlertDialog.Builder(mActivity).
                setTitle("环境切换")
                .setSingleChoiceItems(urlArr, scope_selectedIndex, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scope_selectedIndex = which;
                    }
                }).setPositiveButton("确认", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.saveScopeData(scope_selectedIndex);
                        toastShort(urlArr[scope_selectedIndex]);
                    }
                }).
                        setNegativeButton("取消", null).
                        create();
        alertDialog.show();
    }


    @OnClick(R.id.login_btn)
    public void doForwardMain() {
        String account = accountEt.getText().toString();
        String pwd = pwdEt.getText().toString();
        mPresenter.login(account, pwd);
    }


    @Override
    protected void onDestroy() {
        mPermissionUtils.unregisterObserver(this);
        mProgressUtils.destroyProgress();
        LogUtils.loge("onDestroy");
        super.onDestroy();
    }

    @Override
    public void showProgressDialog() {
        mProgressUtils.showProgress();
    }

    @Override
    public void dismissProgressDialog() {
        mProgressUtils.dismissProgress();
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.INSTANCE.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
    }

    @Override
    public void showAccountName(String name) {
        if (!TextUtils.isEmpty(name)) {
            accountEt.setText(name);
            accountEt.setSelection(name.length());
        }
    }

    @Override
    public void showAccountPwd(String pwd) {
        if (!TextUtils.isEmpty(pwd)) {
            pwdEt.setText(pwd);
            pwdEt.setSelection(pwd.length());
        }
    }

    @Override
    public void setLogButtonState(int which) {
        switch (which) {
            case 0:
                login_btn.setBackground(getResources().getDrawable(R.drawable.shape_button));
                break;
            case 1:
                login_btn.setBackground(getResources().getDrawable(R.drawable.shape_button_demo));
                break;
            case 2:
                login_btn.setBackground(getResources().getDrawable(R.drawable.shape_button_test));
                break;
            case 3:
                login_btn.setBackground(getResources().getDrawable(R.drawable.shape_button_mocha));
                break;
            default:
                login_btn.setBackground(getResources().getDrawable(R.drawable.shape_button));
                break;
        }
    }


    @Override
    public void startAC(Intent intent) {
        mActivity.startActivity(intent);
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

    }

    @Override
    public void onPermissionGranted() {
        mPresenter.initData(mActivity);
    }

    @Override
    public void onPermissionDenied() {

    }
}
