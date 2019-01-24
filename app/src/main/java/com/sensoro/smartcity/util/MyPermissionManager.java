package com.sensoro.smartcity.util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.ACTIVITY_SERVICE;

public final class MyPermissionManager {
    public static class PermissionRequestCodeModel {
        public int onActivityResultRequestCode;
        public int onRequestPermissionsResultCode;
        public Activity activity;
        public ArrayList<String> permissions;

        public ArrayList<String> getPermissions(int... type) {
            return null;
        }
//        add(Manifest.permission.READ_EXTERNAL_STORAGE);
//        add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        //
//        add(Manifest.permission.ACCESS_COARSE_LOCATION);
//        add(Manifest.permission.ACCESS_FINE_LOCATION);
//        //
//        add(Manifest.permission.RECORD_AUDIO);
//        //
//        add(Manifest.permission.CAMERA);
//        //
//        add(Manifest.permission.READ_PHONE_STATE);
//        //
//        add(Manifest.permission.CALL_PHONE);
//        //
//        add(Manifest.permission.READ_CONTACTS);
//        add(Manifest.permission.WRITE_CONTACTS);
    }

    public static class MyPermissionModle {
        public String permission;
        public String permissionName;
    }

    public static MyPermissionModle createPermissionModle(String permission) {
        if (!TextUtils.isEmpty(permission)) {
            MyPermissionModle myPermissionModle = new MyPermissionModle();
            myPermissionModle.permission = permission;
            switch (permission) {
                case Manifest.permission.READ_EXTERNAL_STORAGE:
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    myPermissionModle.permissionName = "读写";
                    break;
                case Manifest.permission.ACCESS_COARSE_LOCATION:
                case Manifest.permission.ACCESS_FINE_LOCATION:
                    myPermissionModle.permissionName = "定位";
                    break;
                case Manifest.permission.RECORD_AUDIO:
                    myPermissionModle.permissionName = "录音";
                    break;
                case Manifest.permission.CAMERA:
                    myPermissionModle.permissionName = "拍照";
                    break;
                case Manifest.permission.READ_PHONE_STATE:
                    myPermissionModle.permissionName = "手机识别码";
                    break;
                case Manifest.permission.CALL_PHONE:
                    myPermissionModle.permissionName = "拨打电话";
                    break;
                case Manifest.permission.READ_CONTACTS:
                case Manifest.permission.WRITE_CONTACTS:
                    myPermissionModle.permissionName = "读写联系人";
                    break;
            }
        }
        return null;

    }

    private static final HashMap<PermissionsResultObserve, PermissionRequestCodeModel> PERMISSION_MAP = new HashMap<>();

    private MyPermissionManager() {
    }

    public static void registerPermissionsResultObserve(PermissionsResultObserve permissionsResultObserve, Activity activity) {
        if (permissionsResultObserve != null) {
            PermissionRequestCodeModel permissionRequestCodeModel = new PermissionRequestCodeModel();
            String simpleName = permissionsResultObserve.getClass().getSimpleName();
            String name = permissionsResultObserve.getClass().getName();
            try {
                LogUtils.loge("registerPermissionsResultObserve --simpleName = " + simpleName + ",name = " + name);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            permissionRequestCodeModel.onActivityResultRequestCode = simpleName.hashCode() ^ name.hashCode();
            permissionRequestCodeModel.onRequestPermissionsResultCode = permissionRequestCodeModel.onActivityResultRequestCode + 1;
            permissionRequestCodeModel.activity = activity;
            PERMISSION_MAP.put(permissionsResultObserve, permissionRequestCodeModel);
        }


    }

    public static void unRegisterPermissionsResultObserve(PermissionsResultObserve permissionsResultObserve) {
        if (permissionsResultObserve != null) {
            PermissionRequestCodeModel permissionRequestCodeModel = PERMISSION_MAP.get(permissionsResultObserve);
            if (permissionRequestCodeModel != null) {
                permissionRequestCodeModel.activity = null;
            }
            PERMISSION_MAP.remove(permissionsResultObserve);
        }
    }

    public static void requestPermissions(PermissionsResultObserve permissionsResultObserve, ArrayList<String> permissions) {
        PermissionRequestCodeModel permissionRequestCodeModel = PERMISSION_MAP.get(permissionsResultObserve);
        if (permissionRequestCodeModel != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                permissionRequestCodeModel.permissions = permissions;
                //获取未通过的权限列表
                ArrayList<String> newPermissions = checkEachSelfPermission(permissionRequestCodeModel, permissions);
                if (newPermissions.size() > 0) {// 是否有未通过的权限
                    requestEachPermissions(permissionRequestCodeModel, newPermissions.toArray(new String[newPermissions.size()]));
                } else {// 权限已经都申请通过了
                    if (permissionsResultObserve != null) {
                        permissionsResultObserve.onPermissionGranted();
                    }
                }
            } else {
                if (permissionsResultObserve != null) {
                    permissionsResultObserve.onPermissionGranted();
                }
            }
        }

    }


    /**
     * 申请权限前判断是否需要声明
     *
     * @param permissions
     */

    private static void requestEachPermissions(PermissionRequestCodeModel permissionRequestCodeModel, String[] permissions) {
        if (shouldShowRequestPermissionRationale(permissionRequestCodeModel.activity, permissions)) {// 需要再次声明
            showRationaleDialog(permissionRequestCodeModel.activity, permissions, permissionRequestCodeModel.onRequestPermissionsResultCode);
        } else {
            if (permissionRequestCodeModel.activity != null) {
                ActivityCompat.requestPermissions(permissionRequestCodeModel.activity, permissions,
                        permissionRequestCodeModel.onRequestPermissionsResultCode);
            }
        }
    }

    private static void showRationaleDialog(final Activity activity, final String[] permissions, final int myRequestPermissionCode) {
        if (activity != null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(activity.getString(R.string.prompt))
                    .setMessage(activity.getString(R.string.permission_descript))
                    .setPositiveButton(activity.getString(R.string.dialog_input_confirm),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(activity, permissions,
                                            myRequestPermissionCode);
                                }
                            })
                    .setNegativeButton(activity.getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    SensoroToast.INSTANCE.makeText(activity, activity.getString(R.string.permission_instruction), Toast.LENGTH_SHORT).show();
                                    activity.finish();
                                }
                            })
                    .setCancelable(false)
                    .show();
        }

    }

    /**
     * 检察每个权限是否申请
     *
     * @param permissions
     * @return newPermissions.size > 0 表示有权限需要申请
     */
    private static ArrayList<String> checkEachSelfPermission(PermissionRequestCodeModel permissionRequestCodeModel, ArrayList<String> permissions) {
        ArrayList<String> newPermissions = new ArrayList<String>();
        for (String permission : permissions) {
            if (permissionRequestCodeModel.activity != null && ContextCompat.checkSelfPermission(permissionRequestCodeModel.activity, permission) != PackageManager
                    .PERMISSION_GRANTED) {
                newPermissions.add(permission);
            }
        }
        return newPermissions;
    }


    /**
     * 再次申请权限时，是否需要声明
     *
     * @param permissions
     * @return
     */
    private static boolean shouldShowRequestPermissionRationale(Activity activity, String[] permissions) {
        for (String permission : permissions) {
            if (activity != null && ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 检查回调结果
     *
     * @param grantResults
     * @return
     */
    private static boolean checkEachPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private static ArrayList<String> checkForceRequirePermissionDenied(
            ArrayList<String> forceRequirePermissions, ArrayList<String> deniedPermissions) {
        ArrayList<String> forceRequirePermissionsDenied = new ArrayList<>();
        if (forceRequirePermissions != null && forceRequirePermissions.size() > 0
                && deniedPermissions != null && deniedPermissions.size() > 0) {
            for (String forceRequire : forceRequirePermissions) {
                if (deniedPermissions.contains(forceRequire)) {
                    forceRequirePermissionsDenied.add(forceRequire);
                }
            }
        }
        return forceRequirePermissionsDenied;
    }

    private static void showPermissionSettingDialog(final PermissionRequestCodeModel permissionRequestCodeModel) {
        if (permissionRequestCodeModel.activity != null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(permissionRequestCodeModel.activity);
            builder.setTitle(permissionRequestCodeModel.activity.getString(R.string.prompt))
                    .setMessage(permissionRequestCodeModel.activity.getString(R.string.necessary_permission_are_denied))
                    .setPositiveButton(permissionRequestCodeModel.activity.getString(R.string.go_to_setting), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent in = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", permissionRequestCodeModel.activity.getPackageName(), null);
                            in.setData(uri);
                            permissionRequestCodeModel.activity.startActivityForResult(in, permissionRequestCodeModel.onActivityResultRequestCode);
                        }
                    })
                    .setNegativeButton(permissionRequestCodeModel.activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            restart(permissionRequestCodeModel.activity);
                        }
                    })
                    .setCancelable(false)
                    .show();
        }

    }

    /**
     * 获取App具体设置
     *
     * @param context 上下文
     */
    public static void getAppDetailsSettings(Context context, int requestCode) {
        getAppDetailsSettings(context, context.getPackageName(), requestCode);
    }

    /**
     * 获取App具体设置
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static void getAppDetailsSettings(Context context, String packageName, int requestCode) {
        if (TextUtils.isEmpty(packageName)) return;
        ((AppCompatActivity) context).startActivityForResult(
                getAppDetailsSettingsIntent(packageName), requestCode);
    }

    /**
     * 获取App具体设置的意图
     *
     * @param packageName 包名
     * @return intent
     */
    public static Intent getAppDetailsSettingsIntent(String packageName) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + packageName));
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * 通过任务管理器杀死进程
     * 需添加权限 {@code <uses-permission android:name="android.permission.RESTART_PACKAGES"/>}</p>
     *
     * @param context
     */
    public static void restart(Context context) {
        int currentVersion = Build.VERSION.SDK_INT;
        if (currentVersion > Build.VERSION_CODES.ECLAIR_MR1) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startMain);
            System.exit(0);
        } else {// android2.1
            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            am.restartPackage(context.getPackageName());
        }
    }


    public static boolean checkPhoto() {
        if (Build.VERSION.SDK_INT >= 23) {
            //大于23但是系统返回的授权标识是错误的，这里返回的null，在异常的时候给出提示
            Camera camera = null;
            try {
                camera = Camera.open(0);
                Camera.Parameters param = camera.getParameters();
                return param != null;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (camera != null) {
                    camera.release();
                }

            }
            return false;
        }
        return true;
    }

    private static boolean hasRecordPermission() {
        int minBufferSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        int bufferSizeInBytes = 640;
        byte[] audioData = new byte[bufferSizeInBytes];
        int readSize = 0;
        AudioRecord audioRecord = null;
        try {
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, 8000,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
            // 开始录音
            audioRecord.startRecording();
        } catch (Exception e) {
            //可能情况一
            if (audioRecord != null) {
                audioRecord.release();
                audioRecord = null;
            }
            return false;
        }
        // 检测是否在录音中,6.0以下会返回此状态
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            //可能情况二
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            }
            return false;
        } else {// 正在录音
            readSize = audioRecord.read(audioData, 0, bufferSizeInBytes);
            // 检测是否可以获取录音结果
            if (readSize <= 0) {
                //可能情况三
                if (audioRecord != null) {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;
                }
                return false;
            } else {                //有权限，正常启动录音并有数据
                if (audioRecord != null) {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;
                }
                return true;
            }
        }
    }

    public static synchronized boolean checkHasLocationPermission(PermissionsResultObserve permissionsResultObserve) {
        //
        if (permissionsResultObserve != null) {
            PermissionRequestCodeModel permissionRequestCodeModel = PERMISSION_MAP.get(permissionsResultObserve);
            if (permissionRequestCodeModel != null) {
                final ArrayList<String> blePermissions = new ArrayList<String>() {
                    {
                        add(Manifest.permission.ACCESS_COARSE_LOCATION);
                        add(Manifest.permission.ACCESS_FINE_LOCATION);
                    }

                };
                requestPermissions(permissionsResultObserve, blePermissions);
//                for (String permission : blePermissions) {
//                    if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager
//                            .PERMISSION_GRANTED) {
//                        return false;
//                    }
//                }
            }
        }

        return true;
    }

    /**
     * 是否打开gps定位信息
     *
     * @param context
     * @return
     */
    public static synchronized boolean isLocServiceEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        return false;
    }

    /**
     * 是否有读写联系人权限
     *
     * @param activity
     * @return
     */
    public static synchronized boolean checkHasContactsPermission(Activity activity) {
        final ArrayList<String> blePermissions = new ArrayList<String>() {
            {
                add(Manifest.permission.READ_CONTACTS);
                add(Manifest.permission.WRITE_CONTACTS);
            }

        };
        for (String permission : blePermissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager
                    .PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否有存储权限
     *
     * @param activity
     * @return
     */
    public static synchronized boolean checkHasWriteExternalStoragePermission(Activity activity) {
        final ArrayList<String> blePermissions = new ArrayList<String>() {
            {
                add(Manifest.permission.READ_EXTERNAL_STORAGE);
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

        };
        for (String permission : blePermissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager
                    .PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否有打电话权限
     *
     * @param activity
     * @return
     */
    public static synchronized boolean checkHasCallPhonePermission(Activity activity) {
        final ArrayList<String> blePermissions = new ArrayList<String>() {
            {
                add(Manifest.permission.CALL_PHONE);
            }

        };
        for (String permission : blePermissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager
                    .PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否有读取手机识别码权限
     *
     * @param activity
     * @return
     */
    public static synchronized boolean checkHasReadPhoneStatePermission(Activity activity) {
        final ArrayList<String> blePermissions = new ArrayList<String>() {
            {
                add(Manifest.permission.READ_PHONE_STATE);
            }

        };
        for (String permission : blePermissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager
                    .PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否有照相权限
     *
     * @param activity
     * @return
     */
    public static synchronized boolean checkHasCameraPermission(Activity activity) {
        final ArrayList<String> blePermissions = new ArrayList<String>() {
            {
                add(Manifest.permission.CAMERA);
            }

        };
        for (String permission : blePermissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager
                    .PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否有录音权限
     *
     * @param activity
     * @return
     */
    public static synchronized boolean checkHasRecordAudioPermission(Activity activity) {
        final ArrayList<String> blePermissions = new ArrayList<String>() {
            {
                add(Manifest.permission.RECORD_AUDIO);
            }

        };
        for (String permission : blePermissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager
                    .PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static synchronized void onRequestPermissionsResult(PermissionsResultObserve permissionsResultObserve, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissionsResultObserve != null) {
            PermissionRequestCodeModel permissionRequestCodeModel = PERMISSION_MAP.get(permissionsResultObserve);
            if (permissionRequestCodeModel != null) {
                if (requestCode == permissionRequestCodeModel.onRequestPermissionsResultCode && permissions != null) {
                    // 获取被拒绝的权限列表
                    ArrayList<String> deniedPermissions = new ArrayList<>();
                    if (checkEachPermissionsGranted(grantResults)) {
//                        if (hasRecordPermission() && checkPhoto()) {
//                            permissionsResultObserve.onPermissionGranted();
//                        } else {
                        showPermissionSettingDialog(permissionRequestCodeModel);
//                        }
//                    mListener.onPermissionGranted();
                    } else {
                        for (String permission : permissions) {
                            if (permissionRequestCodeModel.activity != null && ContextCompat.checkSelfPermission(permissionRequestCodeModel.activity, permission) !=
                                    PackageManager.PERMISSION_GRANTED) {
                                deniedPermissions.add(permission);
                            }
                        }
                        if (permissionRequestCodeModel.permissions != null) {
                            // 判断被拒绝的权限中是否有包含必须具备的权限
                            ArrayList<String> forceRequirePermissionsDenied =
                                    checkForceRequirePermissionDenied(permissionRequestCodeModel.permissions, deniedPermissions);
                            if (forceRequirePermissionsDenied != null && forceRequirePermissionsDenied.size() > 0) {
                                // 必备的权限被拒绝，
                                showPermissionSettingDialog(permissionRequestCodeModel);
                            } else {
                                // 不存在必备的权限被拒绝，可以进首页
                                if (hasRecordPermission() && checkPhoto()) {
                                    permissionsResultObserve.onPermissionGranted();
                                } else {
                                    showPermissionSettingDialog(permissionRequestCodeModel);
                                }
                            }
                        }

                    }

                }
            }
        }


    }

    public static synchronized void onActivityResult(PermissionsResultObserve permissionsResultObserve, int requestCode, int resultCode, Intent data) {
        if (permissionsResultObserve != null) {
            PermissionRequestCodeModel permissionRequestCodeModel = PERMISSION_MAP.get(permissionsResultObserve);
            if (permissionRequestCodeModel != null) {
                //如果需要跳转系统设置页后返回自动再次检查和执行业务 如果不需要则不需要重写onActivityResult
                if (requestCode == permissionRequestCodeModel.onActivityResultRequestCode) {
//            requestPermission(mPermissionsList, mNeedFinish, myRequestPermissionCode);
                    if (permissionRequestCodeModel.permissions != null) {
                        requestPermissions(permissionsResultObserve, permissionRequestCodeModel.permissions);
                    }

                }
            }

        }

    }


}
