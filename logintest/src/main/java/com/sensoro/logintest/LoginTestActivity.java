package com.sensoro.logintest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.common.utils.LogUtils;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sensoro on 17/7/24.
 */


@Route(path = ARouterConstants.ACTIVITY_LOGIN_TEST)
public class LoginTestActivity extends BaseActivity<ILoginView, LoginPresenter> implements ILoginView, LoginUrlDialogUtils.OnLoginUrlDialogListener {

    @BindView(R2.id.login_btn)
    Button login_btn;
    @BindView(R2.id.ac_login_imv_account_icon)
    ImageView acLoginImvAccountIcon;
    @BindView(R2.id.ac_login_et_account)
    EditText acLoginEtAccount;
    @BindView(R2.id.ac_login_imv_account_clear)
    ImageView acLoginImvAccountClear;
    @BindView(R2.id.ac_login_imv_psd_icon)
    ImageView acLoginImvPsdIcon;
    @BindView(R2.id.ac_login_et_psd)
    EditText acLoginEtPsd;
    @BindView(R2.id.ac_login_imv_psd_clear)
    ImageView acLoginImvPsdClear;
    @BindView(R2.id.ac_login_tv_logo_bottom)
    TextView acLoginTvLogoBottom;
    @BindView(R2.id.ac_login_tv_logo)
    TextView acLoginTvLogo;
    @BindView(R2.id.ac_login_tv_logo_description)
    TextView acLoginTvLogoDescription;
    @BindView(R2.id.ac_login_root)
    FrameLayout acLoginRoot;
    @BindView(R2.id.tv_url_desc)
    TextView tvUrlDesc;
    private ProgressUtils mProgressUtils;
    private LoginUrlDialogUtils loginUrlDialogUtils;
    //
    private final String[] urlArr = new String[]{"正式版", "Demo版", "测试版", "预发布", "Dev", "自定义"};

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login_test);
        ButterKnife.bind(mActivity);
        //地址修改
        String myBaseUrl = PreferencesHelper.getInstance().getMyBaseUrl();
        if (!TextUtils.isEmpty(myBaseUrl)) {
            urlArr[5] = "自定义：" + myBaseUrl;
        }
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        mPresenter.initData(mActivity);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        FloatWindowManager.getInstance().applyOrShowFloatWindow(mActivity);
    }

    private void initView() {
        loginUrlDialogUtils = new LoginUrlDialogUtils(mActivity);
        loginUrlDialogUtils.registerListener(this);
        loginUrlDialogUtils.setTitle("例：city-dev");
        acLoginTvLogoBottom.setText(String.format(Locale.ROOT, "Copyright \u00a9 %s SENSORO", DateUtil.getStrTime_yy(System.currentTimeMillis())));
        if (acLoginEtAccount.getText().length() > 0 || acLoginEtPsd.getText().length() > 0) {
            updateLogoDescriptionState(false);
        }

        acLoginRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                acLoginRoot.getWindowVisibleDisplayFrame(rect);
                int height = acLoginRoot.getRootView().getHeight();
                int i = height - rect.bottom;
                if (i > 200) {
                    updateLogoDescriptionState(false);
                } else {
                    updateLogoDescriptionState(true);
                }

            }
        });

        acLoginEtAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    updateAccountIcon(false);
                } else {
                    updateAccountIcon(true);
                    if (acLoginEtPsd.getText().length() == 0) {
                        updateLogoDescriptionState(true);
                    }
                }
            }
        });
        acLoginEtPsd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    updatePsdIcon(false);

                } else {
                    updatePsdIcon(true);
                    if (acLoginEtAccount.getText().length() == 0) {
                        updateLogoDescriptionState(true);
                    }
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected LoginPresenter createPresenter() {
        return new LoginPresenter();
    }


    private long[] mHits = new long[7]; // 数组长度代表点击次数
    private boolean isShowPasswordDialog = true;

    @OnClick(R2.id.login_logo)
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
            View view = View.inflate(mActivity, R.layout.dialog_input_test, null);
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
            input_edt.setCursorVisible(true);
            dialog.show();
            btn_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str = input_edt.getText().toString();
                    if (str.equals("SENSORO")) {
                        switchApi();
                    } else {
                        toastShort(mActivity.getString(R.string.wrong_password));
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

        //
        scope_selectedIndex = RetrofitServiceHelper.getInstance().getBaseUrlType();
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
                    }
                }).setNegativeButton("取消", null).
                        create();
        alertDialog.show();
    }


    @Override
    protected void onDestroy() {
        mProgressUtils.destroyProgress();
        loginUrlDialogUtils.unregisterListener();
        try {
            LogUtils.loge("onDestroy");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
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
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
    }

    @Override
    public void showAccountName(String name) {
        if (!TextUtils.isEmpty(name)) {
            acLoginEtAccount.setText(name);
            acLoginEtAccount.setSelection(name.length());

        }

        updateAccountIcon(TextUtils.isEmpty(name));
    }

    private void updateAccountIcon(boolean isEmpty) {
        if (isEmpty) {
            acLoginImvAccountIcon.setColorFilter(R.color.c_a6a6a6, PorterDuff.Mode.SRC_IN);
        } else {
            acLoginImvAccountIcon.clearColorFilter();
        }
        acLoginImvAccountClear.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void showAccountPwd(String pwd) {
        if (!TextUtils.isEmpty(pwd)) {
            acLoginEtPsd.setText(pwd);
            acLoginEtPsd.setSelection(pwd.length());
        }

        updatePsdIcon(TextUtils.isEmpty(pwd));
    }

    private void updatePsdIcon(boolean isEmpty) {
        if (isEmpty) {
            acLoginImvPsdIcon.setColorFilter(R.color.c_a6a6a6, PorterDuff.Mode.SRC_IN);
        } else {
            acLoginImvPsdIcon.clearColorFilter();
        }

        acLoginImvPsdClear.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void setLogButtonState(int which) {
        switch (which) {
            case 0:
                tvUrlDesc.setVisibility(View.GONE);
                login_btn.setBackground(getResources().getDrawable(R.drawable.shape_button));
                break;
            case 1:
                tvUrlDesc.setVisibility(View.VISIBLE);
                tvUrlDesc.setText(urlArr[which]);
                login_btn.setBackground(getResources().getDrawable(R.drawable.shape_button));
                break;
            case 2:
                tvUrlDesc.setVisibility(View.VISIBLE);
                tvUrlDesc.setText(urlArr[which]);
                login_btn.setBackground(getResources().getDrawable(R.drawable.shape_button));
                break;
            case 3:
                tvUrlDesc.setVisibility(View.VISIBLE);
                tvUrlDesc.setText(urlArr[which]);
                login_btn.setBackground(getResources().getDrawable(R.drawable.shape_button));
                break;
            case 4:
                tvUrlDesc.setVisibility(View.VISIBLE);
                tvUrlDesc.setText(urlArr[which]);
                login_btn.setBackground(getResources().getDrawable(R.drawable.shape_button));
                break;
            case 5:
                tvUrlDesc.setVisibility(View.VISIBLE);
                tvUrlDesc.setText(urlArr[which]);
                login_btn.setBackground(getResources().getDrawable(R.drawable.shape_button));
                break;
            default:
                tvUrlDesc.setVisibility(View.GONE);
                login_btn.setBackground(getResources().getDrawable(R.drawable.shape_button));
                break;
        }
    }

    @Override
    public void showMyBaseUrlDialog(String url) {
        if (loginUrlDialogUtils != null) {
            loginUrlDialogUtils.show(url);
        }
    }

    @Override
    public void dismissLoginDialog() {
        if (loginUrlDialogUtils != null) {
            loginUrlDialogUtils.dismissDialog();
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

    @OnClick({R2.id.ac_login_et_account, R2.id.ac_login_et_psd, R2.id.ac_login_imv_account_clear,
            R2.id.ac_login_imv_psd_clear, R2.id.login_btn})
    public void onViewClicked(View view) {
        int viewID=view.getId();
        if(viewID==R.id.ac_login_et_account){

        }else  if(viewID==R.id.ac_login_et_psd){

        }else if(viewID==R.id.ac_login_imv_account_clear){
            acLoginEtAccount.getText().clear();
        }else if(viewID==R.id.ac_login_imv_psd_clear){
            acLoginEtPsd.getText().clear();
        }else if(viewID==R.id.login_btn){
            String account = acLoginEtAccount.getText().toString();
            String pwd = acLoginEtPsd.getText().toString();
            mPresenter.login(account, pwd);
        }


    }

    private void updateLogoDescriptionState(boolean isVisible) {
        acLoginTvLogo.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        acLoginTvLogoDescription.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onConfirm(String text) {
        //dialog
        if (!TextUtils.isEmpty(text)) {
            urlArr[5] = "自定义：" + text;
            mPresenter.doSaveMyBaseUrl(text);
        } else {
            toastShort("输入不能为空");
        }
    }
}
