package com.sensoro.smartcity.util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.sensoro.common.base.ContextUtils;
import com.sensoro.smartcity.R;

import java.util.LinkedHashSet;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public final class MyPermissionManager {

    public static String getPermissionTips(List<String> permission) {
        LinkedHashSet<String> perStr = new LinkedHashSet<>();
        Context app = ContextUtils.getContext();
        if (permission != null && permission.size() > 0) {
            for (String p : permission) {
                if (!TextUtils.isEmpty(p)) {
                    switch (p) {
                        case Manifest.permission.READ_EXTERNAL_STORAGE:
                        case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                            perStr.add(app.getString(R.string.permission_read_write));
                            break;
                        case Manifest.permission.ACCESS_COARSE_LOCATION:
                        case Manifest.permission.ACCESS_FINE_LOCATION:
                            perStr.add(app.getString(R.string.permission_location));
                            break;
                        case Manifest.permission.RECORD_AUDIO:
                            perStr.add(app.getString(R.string.permission_record));
                            break;
                        case Manifest.permission.CAMERA:
                            perStr.add(app.getString(R.string.permission_camera));
                            break;
                        case Manifest.permission.READ_PHONE_STATE:
                            perStr.add(app.getString(R.string.permission_phone_state));
                            break;
                        case Manifest.permission.CALL_PHONE:
                            perStr.add(app.getString(R.string.permission_call));
                            break;
                        case Manifest.permission.READ_CONTACTS:
                        case Manifest.permission.WRITE_CONTACTS:
                            perStr.add(app.getString(R.string.permission_contact));
                            break;
                        default:
                            break;
                    }
                }

            }
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : perStr) {
                stringBuilder.append(s).append("、");
            }
            String temp = stringBuilder.toString();
            if (temp.endsWith("、")) {
                temp = temp.substring(0, temp.lastIndexOf("、"));
            }
            return temp;
        }
        return "";

    }

    private MyPermissionManager() {
    }


    public static void startAppSetting(Activity activity) {
        Intent in = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        in.setData(uri);
        activity.startActivity(in);
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
        ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_left, R.anim.slide_out);
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

    public static boolean hasRecordPermission() {
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
}
