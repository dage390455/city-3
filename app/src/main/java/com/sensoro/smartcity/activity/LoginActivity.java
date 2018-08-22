package com.sensoro.smartcity.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
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

import static com.sensoro.smartcity.constant.Constants.PREFERENCE_KEY_URL;
import static com.sensoro.smartcity.constant.Constants.PREFERENCE_SCOPE;

/**
 * Created by sensoro on 17/7/24.
 */

public class LoginActivity extends BaseActivity<ILoginView, LoginPresenter> implements ILoginView,
        PermissionsResultObserve {

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
        }
    };
    private PermissionUtils mPermissionUtils;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        checkActivity();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(mActivity);
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        mPermissionUtils = new PermissionUtils(mActivity);
        mPermissionUtils.registerObserver(this);
        mPresenter.initData(mActivity);
        LogUtils.loge("onCreateInit");
        // 避免从桌面启动程序后，会重新实例化入口类的activity

    }

    //避免activity多次启动
    private void checkActivity() {
        if (!this.isTaskRoot()) {
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finishAc();
                    return;
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPermissionUtils.requestPermission(FORCE_REQUIRE_PERMISSIONS, true, MY_REQUEST_PERMISSION_CODE);
    }

    @Override
    protected LoginPresenter createPresenter() {
        return new LoginPresenter();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionUtils.onRequestPermissionsResult(MY_REQUEST_PERMISSION_CODE, requestCode, permissions, grantResults,
                FORCE_REQUIRE_PERMISSIONS);
    }

    private long[] mHits = new long[7]; // 数组长度代表点击次数

    @OnClick(R.id.login_logo)
    public void showSwitchApi() {
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - 1200)) {
            showPasswordDialog();
        }
    }

    private void showPasswordDialog() {

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
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    private int scope_selectedIndex = 0;

    private void switchApi() {
        final String[] urlArr = new String[]{"正式版", "Demo版"};
        SharedPreferences sp = getSharedPreferences(PREFERENCE_SCOPE, Context.MODE_PRIVATE);
        try {
            boolean isDemo = sp.getBoolean(PREFERENCE_KEY_URL, false);
            RetrofitServiceHelper.INSTANCE.setDemoTypeBaseUrl(isDemo);
            if (isDemo) {
                scope_selectedIndex = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionUtils.onActivityResult(requestCode, resultCode, data, MY_REQUEST_PERMISSION_CODE);
    }

    @Override
    protected void onDestroy() {
        mProgressUtils.destroyProgress();
        mPermissionUtils.unregisterObserver(this);
        LogUtils.loge("onDestroy");
        super.onDestroy();
    }

    @Override
    public void onPermissionGranted() {
        mPresenter.initPushSDK();
        LogUtils.logd(this, "onPermissionGranted: 权限获取完毕 ");
    }

    @Override
    public void onPermissionDenied() {
        //TODO 非必要权限
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
        SensoroToast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
    }

    @Override
    public void showAccountName(String name) {
        accountEt.setText(name);
    }

    @Override
    public void showAccountPwd(String pwd) {
        pwdEt.setText(pwd);
    }

    @Override
    public void setLogButtonState(int which) {
        if (which == 0) {
            login_btn.setBackground(getResources().getDrawable(R.drawable.shape_button));
        } else {
            login_btn.setBackground(getResources().getDrawable(R.drawable.shape_button_mocha));
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
}
