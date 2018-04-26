package com.sensoro.smartcity.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.mobstat.StatService;
import com.igexin.sdk.PushManager;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.push.SensoroPushIntentService;
import com.sensoro.smartcity.push.SensoroPushService;
import com.sensoro.smartcity.server.SmartCityServerImpl;
import com.sensoro.smartcity.server.response.LoginRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.util.AESUtil;
import com.sensoro.smartcity.util.PermissionUtils;
import com.sensoro.smartcity.util.PermissionsResultObserve;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.widget.SensoroImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sensoro on 17/7/24.
 */

public class LoginActivity extends BaseActivity implements Constants {

    private static final String TAG = "LoginActivity";
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
    ProgressDialog mProgressDialog;
    private static final int REQUEST_PERMISSION = 0;
    private static final int MY_REQUEST_PERMISSION_CODE = 0x14;
    protected static final ArrayList<String> FORCE_REQUIRE_PERMISSIONS = new ArrayList<String>() {
        {
            add(Manifest.permission.INTERNET);
            add(Manifest.permission.READ_EXTERNAL_STORAGE);
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            add(Manifest.permission.ACCESS_FINE_LOCATION);
            add(Manifest.permission.ACCESS_COARSE_LOCATION);
            add(Manifest.permission.READ_PHONE_STATE);
            add(Manifest.permission.CAMERA);
        }
    };
    private PermissionUtils mPermissionUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        StatService.setDebugOn(true);
        StatService.start(this);
        mPermissionUtils = new PermissionUtils(this);
        requestPermissions();
        readLoginData();
        init();
        initSeverUrl();
    }

    private void requestPermissions() {
        mPermissionUtils.requestPermission(FORCE_REQUIRE_PERMISSIONS, true, MY_REQUEST_PERMISSION_CODE, new
                PermissionsResultObserve() {
                    @Override
                    public void onPermissionGranted() {
                        PushManager.getInstance().initialize(LoginActivity.this.getApplicationContext(),
                                SensoroPushService
                                        .class);
                        Log.e(TAG, "onPermissionGranted: 权限获取完毕 ");
                    }

                    @Override
                    public void onPermissionDenied() {
                        //TODO 非必要权限

                    }
                });
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

    private void initSeverUrl() {
        try {
            ApplicationInfo appInfo = this.getPackageManager()
                    .getApplicationInfo(getPackageName(),
                            PackageManager.GET_META_DATA);
            String msg = appInfo.metaData.getString("InstallChannel");
            if (msg.equalsIgnoreCase("Mocha")) {
                SmartCityServerImpl.SCOPE = SmartCityServerImpl.SCOPE_MOCHA;
            } else if (msg.equalsIgnoreCase("Master")) {
                SmartCityServerImpl.SCOPE = SmartCityServerImpl.SCOPE_MASTER;
            } else {
                SmartCityServerImpl.SCOPE = SmartCityServerImpl.SCOPE_MOCHA;
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void printPx2Dp() {
        int width = 750;//屏幕宽度
        int height = 1334;//屏幕高度
        float screenInch = 4.7f;//屏幕尺寸
//设备密度公式
        float density = (float) Math.sqrt(width * width + height * height) / screenInch / 160;

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n");
        for (int px = 0; px <= 1000; px += 2) {
            //像素值除以density
            String dp = px * 1.0f / density + "";
            //拼接成资源文件的内容，方便引用
            if (dp.indexOf(".") + 4 < dp.length()) {//保留3位小数
                dp = dp.substring(0, dp.indexOf(".") + 4);
            }
            stringBuilder.append("<dimen name=\"px").append(px + "").append("dp\">").append(dp).append("dp</dimen>\n");
        }
        stringBuilder.append("</resources>");
        System.out.println(stringBuilder.toString());
    }

    @Override
    protected boolean isNeedSlide() {
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mPermissionUtils.onRequestPermissionsResult(MY_REQUEST_PERMISSION_CODE, requestCode, permissions, grantResults,
                FORCE_REQUIRE_PERMISSIONS);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void readLoginData() {
        SharedPreferences sp = getSharedPreferences(PREFERENCE_LOGIN, Context.MODE_PRIVATE);
        String name = sp.getString(PREFERENCE_KEY_NAME, null);
        String pwd = sp.getString(PREFERENCE_KEY_PASSWORD, null);
        if (name != null) {
            String aes_pwd = AESUtil.decode(pwd);
            if (name != null) {
                accountEt.setText(name);
            }
            if (pwd != null) {
                pwdEt.setText(aes_pwd);
            }
        }
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_input, null);
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
                    Toast.makeText(LoginActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
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
        String url = sp.getString(PREFERENCE_KEY_URL, null);
        if (url != null) {
            SmartCityServerImpl.SCOPE = url;
            if (url.equals(SmartCityServerImpl.SCOPE_MOCHA)) {
                scope_selectedIndex = 1;
            }
        }
        Dialog alertDialog = new AlertDialog.Builder(this).
                setTitle("环境切换")
                .setSingleChoiceItems(urlArr, scope_selectedIndex, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scope_selectedIndex = which;
                    }
                }).setPositiveButton("确认", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (scope_selectedIndex == 0) {
                            login_btn.setBackground(getResources().getDrawable(R.drawable.shape_button));
                            SmartCityServerImpl.SCOPE = SmartCityServerImpl.SCOPE_MASTER;
                        } else {
                            login_btn.setBackground(getResources().getDrawable(R.drawable.shape_button_mocha));
                            SmartCityServerImpl.SCOPE = SmartCityServerImpl.SCOPE_MOCHA;
                        }
                        saveScopeData(getApplicationContext(), SmartCityServerImpl.SCOPE);
                        Toast.makeText(LoginActivity.this, urlArr[scope_selectedIndex], Toast.LENGTH_SHORT).show();
                    }
                }).
                        setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                            }
                        }).
                        create();
        alertDialog.show();
    }

    public void saveScopeData(Context context, String url) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE_SCOPE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PREFERENCE_KEY_URL, url);
        editor.commit();
    }

    @OnClick(R.id.login_btn)
    public void doForwardMain() {
        SharedPreferences sp = getSharedPreferences(PREFERENCE_SCOPE, Context.MODE_PRIVATE);
        String url = sp.getString(PREFERENCE_KEY_URL, null);
        if (!TextUtils.isEmpty(url)) {
            SmartCityServerImpl.SCOPE = url;
        }
        mProgressDialog.show();
        final String account = accountEt.getText().toString();
        final String pwd = pwdEt.getText().toString();
        if (account == null) {
            Toast.makeText(this, R.string.tips_username_empty, Toast.LENGTH_SHORT).show();
        } else if (pwd == null) {
            Toast.makeText(this, R.string.tips_login_pwd_empty, Toast.LENGTH_SHORT).show();
        } else {
            String phoneId = PushManager.getInstance().getClientid(getApplicationContext());
            SensoroCityApplication.getInstance().smartCityServer.login(account, pwd, phoneId, new Response
                    .Listener<LoginRsp>() {
                @Override
                public void onResponse(LoginRsp response) {
                    if (response.getErrcode() == ResponseBase.CODE_SUCCESS) {
                        String isSpecific = response.getData().getIsSpecific();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra(EXTRA_USER_ID, response.getData().get_id());
                        intent.putExtra(EXTRA_USER_NAME, response.getData().getNickname());
                        intent.putExtra(EXTRA_PHONE, response.getData().getPhone());
                        intent.putExtra(EXTRA_CHARACTER, response.getData().getCharacter());
                        intent.putExtra(EXTRA_USER_ROLES, response.getData().getRoles());
                        intent.putExtra(EXTRA_IS_SPECIFIC, isSpecific);
                        intent.putExtra(EXTRA_PHONE_ID, PushManager.getInstance().getClientid(getApplicationContext()));
                        startActivity(intent);
                        PreferencesHelper.getInstance().saveLoginData(LoginActivity.this, account, pwd);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, R.string.tips_user_info_error, Toast.LENGTH_LONG).show();
                    }
                    mProgressDialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse != null) {
                        String reason = new String(error.networkResponse.data);
                        try {
                            JSONObject jsonObject = new JSONObject(reason);
                            Toast.makeText(LoginActivity.this, jsonObject.getString("errmsg"), Toast.LENGTH_SHORT)
                                    .show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {

                        }
                    } else {
                        Toast.makeText(LoginActivity.this, R.string.tips_network_error, Toast.LENGTH_SHORT).show();
                    }
                    mProgressDialog.dismiss();
                }
            });
        }
    }

    void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest
                        .permission.READ_PHONE_STATE},
                REQUEST_PERMISSION);
    }

    void initPushSDK() {

        PackageManager pkgManager = getPackageManager();

        // 读写 sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
        boolean sdCardWritePermission =
                pkgManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName()) ==
                        PackageManager.PERMISSION_GRANTED;

        // read phone state用于获取 imei 设备信息
        boolean phoneSatePermission =
                pkgManager.checkPermission(Manifest.permission.READ_PHONE_STATE, getPackageName()) == PackageManager
                        .PERMISSION_GRANTED;

        if (Build.VERSION.SDK_INT >= 23 && !sdCardWritePermission || !phoneSatePermission) {
            requestPermission();
        } else {
            PushManager.getInstance().initialize(this.getApplicationContext(), SensoroPushService.class);
        }

        // 注册 intentService 后 PushDemoReceiver 无效, sdk 会使用 DemoIntentService 传递数据,
        // AndroidManifest 对应保留一个即可(如果注册 DemoIntentService, 可以去掉 PushDemoReceiver, 如果注册了
        // IntentService, 必须在 AndroidManifest 中声明)
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), SensoroPushIntentService
                .class);
    }


    private void init() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.logining));
        initPushSDK();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionUtils.onActivityResult(requestCode, resultCode, data, MY_REQUEST_PERMISSION_CODE);
    }
}
