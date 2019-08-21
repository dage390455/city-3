package com.sensoro.smartcity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sensoro.common.utils.LogUtils;
import com.sensoro.common.utils.MyPermissionManager;
import com.sensoro.common.widgets.PermissionDialogUtils;
import com.sensoro.inspectiontask.R;
import com.sensoro.smartcity.activity.InspectionTaskListActivity;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;

import butterknife.ButterKnife;

public class InspectionTestActivity extends Activity {


    private final String[] requestPermissions = {Permission.READ_PHONE_STATE, Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION, Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE, Permission.WRITE_CONTACTS, Permission.READ_CONTACTS, Permission.CAMERA, Permission.RECORD_AUDIO, Permission.CALL_PHONE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_test);



        findViewById(com.sensoro.imagepicker.R.id.btn_picker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), InspectionTaskListActivity.class));
            }
        });

        permissionDialogUtils = new PermissionDialogUtils(this);

    }


    @Override
    protected void onStart() {
        super.onStart();

        requestPermissions(requestPermissions);
    }


    private PermissionDialogUtils permissionDialogUtils;
    private void requestPermissions(final String[] permissions) {
        AndPermission.with(this).runtime()
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
                        permissionDialogUtils.setTipMessageText(getString(com.sensoro.imagepicker.R.string.permission_descript)).setTipConfirmText(getString(com.sensoro.imagepicker.R.string.reauthorization),getResources().getColor(com.sensoro.imagepicker.R.color.colorAccent)).show(new PermissionDialogUtils.TipDialogUtilsClickListener() {
                            @Override
                            public void onCancelClick() {
                                executor.cancel();
                                permissionDialogUtils.dismiss();
                                MyPermissionManager.restart(InspectionTestActivity.this);
                            }

                            @Override
                            public void onConfirmClick() {
                                executor.execute();
                                permissionDialogUtils.dismiss();
                            }
                        });
                    }
                })
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {


                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        // 用户拒绝权限，提示用户授权
                        if (AndPermission.hasAlwaysDeniedPermission(InspectionTestActivity.this, permissions)) {
                            // 如果用户勾选了禁止重复提醒，需要提示用户去到APP权限设置页面开启权限
                            String permissionTips = MyPermissionManager.getPermissionTips(data);
                            permissionDialogUtils.setTipConfirmText(InspectionTestActivity.this.getString(com.sensoro.imagepicker.R.string.go_setting), InspectionTestActivity.this.getResources().getColor(com.sensoro.imagepicker.R.color.c_f34a4a)).setTipMessageText(permissionTips + InspectionTestActivity.this.getString(com.sensoro.imagepicker.R.string.permission_check)).show(new PermissionDialogUtils.TipDialogUtilsClickListener() {
                                @Override
                                public void onCancelClick() {
                                    permissionDialogUtils.dismiss();
                                    MyPermissionManager.restart(InspectionTestActivity.this);
                                }

                                @Override
                                public void onConfirmClick() {
                                    permissionDialogUtils.dismiss();
                                    MyPermissionManager.startAppSetting(InspectionTestActivity.this);
                                }
                            });
                        } else {
                            requestPermissions(data.toArray(new String[data.size()]));
                        }

                    }
                })
                .start();

    }


}


