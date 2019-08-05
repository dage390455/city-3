package com.sensoro.smartcity.widget.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.manger.ActivityTaskManager;
import com.sensoro.common.model.EventData;
import com.sensoro.common.model.EventLoginData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.UserInfo;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.widgets.CustomCornerDialog;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.LoginActivity;
import com.sensoro.smartcity.factory.UserPermissionFactory;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PermissionChangeDialogUtils {

    //    private AlertDialog mDialog;
    private final TextView dialogTipTvCancel;
    private final TextView dialogTipTvConfirm;
    private OnPopupDismissListener dismissListener;

    public Activity getmActivity() {
        return mActivity;
    }

    private final Activity mActivity;
    //    private final TextView mTvConfirm;
    private CustomCornerDialog mDialog;
    private ProgressUtils mProgressUtils;

    public PermissionChangeDialogUtils(Activity activity) {
        mActivity = activity;
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        View view = View.inflate(activity, R.layout.item_dialog_permission_change, null);
        dialogTipTvCancel = view.findViewById(R.id.dialog_tip_tv_cancel);
        dialogTipTvConfirm = view.findViewById(R.id.dialog_tip_tv_confirm);
//        mTvConfirm = view.findViewById(R.id.dialog_tip_tv_confirm);
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setView(view);
//        builder.setCancelable(false);
//        mDialog = builder.create();
//        Window window = mDialog.getWindow();
//        if (window != null) {
//            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }

        mDialog = new CustomCornerDialog(activity, R.style.CustomCornerDialogStyle, view);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        dialogTipTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //稍后登录
                showProgressDialog();
                RetrofitServiceHelper.getInstance().getPermissionChangeInfo().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<UserInfo>>(null) {
                    @Override
                    public void onCompleted(ResponseResult<UserInfo> loginRsp) {
                        EventLoginData userData = PreferencesHelper.getInstance().getUserData();
                        UserInfo userInfo = loginRsp.getData();
                        EventLoginData loginData = UserPermissionFactory.createLoginData(userInfo, userData.phoneId);
                        PreferencesHelper.getInstance().saveUserData(loginData);
                        dismissProgressDialog();
                        dismiss();
                        EventData eventData = new EventData();
                        eventData.code = Constants.EVENT_DATA_PERMISSIONCHANGE;
                        EventBus.getDefault().post(eventData);

                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        toastShort(errorMsg);
                        dismissProgressDialog();
                    }
                });
            }
        });
        dialogTipTvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if (PreferencesHelper.getInstance().getUserData() != null) {
                    showProgressDialog();
                    RetrofitServiceHelper.getInstance().logout(PreferencesHelper.getInstance().getUserData().phoneId, PreferencesHelper.getInstance().getUserData().userId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
                            .mainThread()).subscribe(new CityObserver<ResponseResult>(null) {
                        @Override
                        public void onErrorMsg(int errorCode, String errorMsg) {
                            dismissProgressDialog();
                            //不是网络位置错误直接退出
                            if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                                toastShort(errorMsg);
                            } else {
                                RetrofitServiceHelper.getInstance().clearLoginDataSessionId();
                                RetrofitServiceHelper.getInstance().cancelAllRsp();
                                Intent loginIntent = new Intent();
                                loginIntent.setClass(mActivity, LoginActivity.class);
                                mActivity.overridePendingTransition(com.sensoro.common.R.anim.slide_left, com.sensoro.common.R.anim.slide_out);
                                mActivity.startActivity(loginIntent);
                                ActivityTaskManager.getInstance().finishAllActivity();
                            }

                        }

                        @Override
                        public void onCompleted(ResponseResult responseBase) {
                            dismissProgressDialog();
                            if (responseBase.getErrcode() == ResponseResult.CODE_SUCCESS) {
                                RetrofitServiceHelper.getInstance().clearLoginDataSessionId();
                                RetrofitServiceHelper.getInstance().cancelAllRsp();
                                Intent loginIntent = new Intent();
                                loginIntent.setClass(mActivity, LoginActivity.class);
                                mActivity.overridePendingTransition(com.sensoro.common.R.anim.slide_left, com.sensoro.common.R.anim.slide_out);
                                mActivity.startActivity(loginIntent);

                            }
                            ActivityTaskManager.getInstance().finishAllActivity();
                        }
                    });
                } else {
                    RetrofitServiceHelper.getInstance().clearLoginDataSessionId();
                    RetrofitServiceHelper.getInstance().cancelAllRsp();
                    Intent loginIntent = new Intent();
                    loginIntent.setClass(mActivity, LoginActivity.class);
                    mActivity.overridePendingTransition(com.sensoro.common.R.anim.slide_left, com.sensoro.common.R.anim.slide_out);
                    mActivity.startActivity(loginIntent);
                    ActivityTaskManager.getInstance().finishAllActivity();
                }
            }
        });

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (dismissListener != null) {
                    dismissListener.onDismiss();
                }
            }
        });

    }


    public void show() {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }

    public void showProgressDialog() {
        mProgressUtils.showProgress();
    }

    public void dismissProgressDialog() {
        mProgressUtils.dismissProgress();
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public void destroy() {
        if (mDialog != null) {
            mDialog.cancel();
            mDialog = null;
        }
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
        }
    }


    public void setDismissListener(OnPopupDismissListener listener) {

        dismissListener = listener;
    }

    public interface OnPopupDismissListener {


        void onDismiss();

    }

    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

}
