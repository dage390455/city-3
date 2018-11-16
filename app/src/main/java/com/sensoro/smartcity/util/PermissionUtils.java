package com.sensoro.smartcity.util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
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
import com.sensoro.smartcity.widget.SensoroToast;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

import static android.content.Context.ACTIVITY_SERVICE;

public final class PermissionUtils {
    private static final int MY_REQUEST_PERMISSION_CODE = 0x114;
    private static final ArrayList<String> FORCE_REQUIRE_PERMISSIONS = new ArrayList<String>() {
        {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            add(Manifest.permission.ACCESS_COARSE_LOCATION);
            add(Manifest.permission.CAMERA);
            add(Manifest.permission.RECORD_AUDIO);
            add(Manifest.permission.ACCESS_FINE_LOCATION);
            add(Manifest.permission.READ_EXTERNAL_STORAGE);
            add(Manifest.permission.READ_PHONE_STATE);
            add(Manifest.permission.CALL_PHONE);
        }
    };
    private volatile PermissionsResultObserve mListener;
    private SoftReference<Activity> mContext;

    public PermissionUtils(Activity activity) {
        mContext = new SoftReference<>(activity);
    }

    /**
     * 权限允许或拒绝对话框
     *
     * @param
     */
    public void requestPermission() {
        if (mListener == null) {
            throw new NullPointerException("请先注册监听!");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //获取未通过的权限列表
            ArrayList<String> newPermissions = checkEachSelfPermission(FORCE_REQUIRE_PERMISSIONS);
            if (newPermissions.size() > 0) {// 是否有未通过的权限
                requestEachPermissions(newPermissions.toArray(new String[newPermissions.size()]),
                        MY_REQUEST_PERMISSION_CODE);
            } else {// 权限已经都申请通过了
                if (mListener != null) {
                    if (hasRecordPermission() && checkPhoto()) {
                        mListener.onPermissionGranted();
                    } else {
                        showPermissionSettingDialog(MY_REQUEST_PERMISSION_CODE);
                    }
                }
            }
        } else {
            if (mListener != null) {
                if (hasRecordPermission() && checkPhoto()) {
                    mListener.onPermissionGranted();
                } else {
                    showPermissionSettingDialog(MY_REQUEST_PERMISSION_CODE);
                }
            }
        }
    }

    /**
     * 申请权限前判断是否需要声明
     *
     * @param permissions
     */
    private void requestEachPermissions(String[] permissions, int myRequestPermissionCode) {
        if (shouldShowRequestPermissionRationale(permissions)) {// 需要再次声明
            showRationaleDialog(permissions, myRequestPermissionCode);
        } else {
            if (hasActivity()) {
                ActivityCompat.requestPermissions(mContext.get(), permissions,
                        myRequestPermissionCode);
            }
        }
    }

    private boolean hasActivity() {
        return mContext.get() != null;
    }

    /**
     * 弹出声明的 Dialog
     *
     * @param permissions
     */
    private void showRationaleDialog(final String[] permissions, final int myRequestPermissionCode) {
        if (hasActivity()) {
            final Activity activity = mContext.get();
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
    private ArrayList<String> checkEachSelfPermission(ArrayList<String> permissions) {
        ArrayList<String> newPermissions = new ArrayList<String>();
        for (String permission : permissions) {
            if (hasActivity() && ContextCompat.checkSelfPermission(mContext.get(), permission) != PackageManager
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
    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        for (String permission : permissions) {
            if (hasActivity() && ActivityCompat.shouldShowRequestPermissionRationale(mContext.get(), permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 申请权限结果的回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_REQUEST_PERMISSION_CODE && permissions != null) {
            // 获取被拒绝的权限列表
            ArrayList<String> deniedPermissions = new ArrayList<>();
            if (checkEachPermissionsGranted(grantResults)) {
                if (mListener != null) {
                    if (hasRecordPermission() && checkPhoto()) {
                        mListener.onPermissionGranted();
                    } else {
                        showPermissionSettingDialog(MY_REQUEST_PERMISSION_CODE);
                    }
//                    mListener.onPermissionGranted();
                }
            } else {
                for (String permission : permissions) {
                    if (hasActivity() && ContextCompat.checkSelfPermission(mContext.get(), permission) !=
                            PackageManager.PERMISSION_GRANTED) {
                        deniedPermissions.add(permission);
                    }
                }
                // 判断被拒绝的权限中是否有包含必须具备的权限
                ArrayList<String> forceRequirePermissionsDenied =
                        checkForceRequirePermissionDenied(FORCE_REQUIRE_PERMISSIONS, deniedPermissions);
                if (forceRequirePermissionsDenied != null && forceRequirePermissionsDenied.size() > 0) {
                    // 必备的权限被拒绝，
                    showPermissionSettingDialog(MY_REQUEST_PERMISSION_CODE);
                } else {
                    // 不存在必备的权限被拒绝，可以进首页
                    if (mListener != null) {
                        if (hasRecordPermission() && checkPhoto()) {
                            mListener.onPermissionGranted();
                        } else {
                            showPermissionSettingDialog(MY_REQUEST_PERMISSION_CODE);
                        }
                    }
                }
            }

        }
    }

    /**
     * 检查回调结果
     *
     * @param grantResults
     * @return
     */
    private boolean checkEachPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<String> checkForceRequirePermissionDenied(
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

    /**
     * 手动开启权限弹窗
     */
    private void showPermissionSettingDialog(final int myRequestPermissionCode) {
        if (hasActivity()) {
            final Activity activity = mContext.get();
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(activity.getString(R.string.prompt))
                    .setMessage(activity.getString(R.string.necessary_permission_are_denied))
                    .setPositiveButton(activity.getString(R.string.go_to_setting), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent in = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                            in.setData(uri);
                            activity.startActivityForResult(in, myRequestPermissionCode);
                        }
                    })
                    .setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            restart(activity);
                        }
                    })
                    .setCancelable(false)
                    .show();
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data, int
            myRequestPermissionCode) {
        //如果需要跳转系统设置页后返回自动再次检查和执行业务 如果不需要则不需要重写onActivityResult
        if (requestCode == myRequestPermissionCode) {
//            requestPermission(mPermissionsList, mNeedFinish, myRequestPermissionCode);
            requestPermission();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //如果需要跳转系统设置页后返回自动再次检查和执行业务 如果不需要则不需要重写onActivityResult
        if (requestCode == MY_REQUEST_PERMISSION_CODE) {
//            requestPermission(mPermissionsList, mNeedFinish, MY_REQUEST_PERMISSION_CODE);
            requestPermission();
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
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
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


    public void registerObserver(PermissionsResultObserve permissionsResultObserve) {
        mListener = permissionsResultObserve;
    }

    public void unregisterObserver(PermissionsResultObserve permissionsResultObserve) {
        if (permissionsResultObserve != mListener) {
            throw new IllegalArgumentException("注册对象不一致！");
        }
        if (mListener != null) {
            mListener = null;
        }
        if (mContext != null) {
            mContext.clear();
            mContext = null;
        }
    }

    private boolean voicePermission() {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.
                checkSelfPermission(mContext.get(), android.Manifest.permission.RECORD_AUDIO));
    }

    public boolean checkPhoto() {
        if (Build.VERSION.SDK_INT >= 23) {
            //大于23但是系统返回的授权标识是错误的，这里返回的null，在异常的时候给出提示
            Camera camera = null;
            try {
                camera = Camera.open(0);
                Camera.Parameters param = camera.getParameters();
                if (param != null) {
                    return true;
                } else if(mContext.get() != null){
                    SensoroToast.INSTANCE.makeText(mContext.get().getString(R.string.open_camera_permission), Toast.LENGTH_SHORT).show();
                }

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

    private boolean hasRecordPermission() {
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

    public static boolean checkHasBlePermission(Activity activity) {
        final ArrayList<String> blePermissions = new ArrayList<String>() {
            {
                add(Manifest.permission.ACCESS_COARSE_LOCATION);
                add(Manifest.permission.ACCESS_FINE_LOCATION);
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
}
