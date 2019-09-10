package com.sensoro.city_camera.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.launcher.ARouter;
import com.sensoro.city_camera.IMainViews.ILauncherActivityView;
import com.sensoro.city_camera.R;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.iwidget.IOnStart;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.AlarmPopupDataBean;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.utils.LogUtils;
import com.sensoro.common.utils.MyPermissionManager;
import com.sensoro.common.widgets.PermissionDialogUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LauncherActivityPresenter extends BasePresenter<ILauncherActivityView> implements IOnStart {
    private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private Activity mContext;
    //
    private long exitTime = 0;
    private final Handler mHandler = new Handler();
    //
    private PermissionDialogUtils permissionDialogUtils;
    private final String[] requestPermissions = {Permission.READ_PHONE_STATE, Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION, Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE, Permission.WRITE_CONTACTS, Permission.READ_CONTACTS, Permission.CAMERA, Permission.RECORD_AUDIO, Permission.CALL_PHONE};

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        permissionDialogUtils = new PermissionDialogUtils(mContext);
    }

    private void init() {
        initViewPager();
        //每次初始化静默拉取一次预警弹窗的配置项
        RetrofitServiceHelper.getInstance().getDevicesAlarmPopupConfig().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<AlarmPopupDataBean>>(this) {
            @Override
            public void onCompleted(ResponseResult<AlarmPopupDataBean> devicesAlarmPopupConfigRsp) {
                PreferencesHelper.getInstance().saveAlarmPopupDataBeanCache(devicesAlarmPopupConfigRsp.getData());

            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
            }
        });
    }

    private void initViewPager() {
        //

        if (mFragmentList.size() > 0) {
            mFragmentList.clear();
        }
        Object cameraWarn = ARouter.getInstance().build(ARouterConstants.FRAGMENT_CAMERA_WARN_LIST).navigation(mContext);
        if (cameraWarn instanceof Fragment) {
            Fragment fragment = (Fragment) cameraWarn;
            mFragmentList.add(fragment);
        }
        getView().updateMainPageAdapterData(mFragmentList);
        //
    }


    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        mFragmentList.clear();
    }


    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            getView().toastShort(mContext.getResources().getString(R.string.exit_main));
            exitTime = System.currentTimeMillis();
        } else {
            getView().finishAc();
        }
    }

    private void requestPermissions(final String[] permissions) {
        AndPermission.with(mContext).runtime()
                .permission(permissions)
                .rationale(new Rationale<List<String>>() {
                    @Override
                    public void showRationale(Context context, List<String> data, final RequestExecutor executor) {
                        // 重新授权的提示
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String str : data) {
                            stringBuilder.append(str).append(",");
                        }
                        try {
                            LogUtils.loge("权限列表：" + stringBuilder.toString());
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                        if (isAttachedView()) {
                            permissionDialogUtils.setTipMessageText(mContext.getString(R.string.permission_descript)).setTipConfirmText(mContext.getString(R.string.reauthorization), mContext.getResources().getColor(R.color.colorAccent)).show(new PermissionDialogUtils.TipDialogUtilsClickListener() {
                                @Override
                                public void onCancelClick() {
                                    if (isAttachedView()) {
                                        executor.cancel();
                                        permissionDialogUtils.dismiss();
                                        MyPermissionManager.restart(mContext);
                                    }
                                }

                                @Override
                                public void onConfirmClick() {
                                    if (isAttachedView()) {
                                        executor.execute();
                                        permissionDialogUtils.dismiss();
                                    }
                                }
                            });
                        }
                    }
                })
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        // 用户同意授权
                        if (isAttachedView()) {
                            //
                            init();
                        }
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        // 用户拒绝权限，提示用户授权
                        if (isAttachedView()) {
                            if (AndPermission.hasAlwaysDeniedPermission(mContext, permissions)) {
                                // 如果用户勾选了禁止重复提醒，需要提示用户去到APP权限设置页面开启权限
                                String permissionTips = MyPermissionManager.getPermissionTips(data);
                                permissionDialogUtils.setTipConfirmText(mContext.getString(R.string.go_setting), mContext.getResources().getColor(R.color.c_f34a4a)).setTipMessageText(permissionTips + mContext.getString(R.string.permission_check)).show(new PermissionDialogUtils.TipDialogUtilsClickListener() {
                                    @Override
                                    public void onCancelClick() {
                                        if (isAttachedView()) {
                                            permissionDialogUtils.dismiss();
                                            MyPermissionManager.restart(mContext);
                                        }
                                    }

                                    @Override
                                    public void onConfirmClick() {
                                        if (isAttachedView()) {
                                            permissionDialogUtils.dismiss();
                                            MyPermissionManager.startAppSetting(mContext);
                                        }
                                    }
                                });
                            } else {
                                requestPermissions(data.toArray(new String[data.size()]));
                            }
                        }


                    }
                })
                .start();

    }

    @Override
    public void onStart() {
        requestPermissions(requestPermissions);
    }

    @Override
    public void onStop() {

    }

}
