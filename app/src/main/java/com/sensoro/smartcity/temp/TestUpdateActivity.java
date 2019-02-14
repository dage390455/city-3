package com.sensoro.smartcity.temp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.sensoro.libbleserver.ble.callback.OnDeviceUpdateObserver;
import com.sensoro.libbleserver.ble.connection.SensoroDeviceConnection;
import com.sensoro.libbleserver.ble.entity.SensoroDevice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by fangping on 2016/7/14.
 */

public class TestUpdateActivity extends Activity {

    private static final String EXTERN_DIRECTORY_NAME = "ddong1031";
    private ProgressDialog progressDialog;
    private SensoroDevice mSensoroDevice;
    private SensoroDeviceConnection mSensoroDeviceSession;
    private String tempPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        mSensoroDevice = this.getIntent().getParcelableExtra("sensoro_device");
        loge("=========" + mSensoroDevice.toString());
        mSensoroDeviceSession = new SensoroDeviceConnection(this.getApplicationContext(), mSensoroDevice);
        tempPath = Environment.getExternalStorageDirectory().getPath() + "/" + EXTERN_DIRECTORY_NAME;
        File file = new File(tempPath);
        if (!file.exists()) {
            loge("成功创建文件" + file.mkdir());
        }
//        String fileName = "tracker_dfu_test.zip";
        String fileName = "SENSORO_SMOKE_FHSJ_H605_V110_DFU_v2.1.0_828ff7b_20190213.zip";
        tempPath = file.getAbsolutePath() + "/" + fileName;
        File file1 = new File(tempPath);
        if (file1.exists() && file1.isFile()) {
            loge("file length = " + file1.length());
//            return;
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    copyBigDataToSD();
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showNoticeDialog();
                        }
                    }, 500);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    /**
     * 测试升级
     */
    private void testUpdate() {
//        String path = Environment.getExternalStorageDirectory().getPath() + "/" + EXTERN_DIRECTORY_NAME;
        File file = new File(tempPath);
        if (file.exists() && file.isFile()) {
            toast("文件size = " + file.length());
        } else {
            toast("文件不存在或不完整");
            return;
        }
//        String fileName = "tracker_dfu_test.zip";
//        path = file.getAbsolutePath() + "/" + fileName;
        //参数1:升级文件路径
        //参数2：密码
        //参数3：监听
        mSensoroDeviceSession.startUpdate(tempPath, "2go5AP?M8:7c&!s{", new OnDeviceUpdateObserver() {
            @Override
            public void onEnteringDFU(String s, String s1, String s2) {
                toast("正在进入DFU-->>");
                loge("正在进入DFU-->>" + s + ",s1 = " + s1 + ",s2 = " + s2);
            }

            @Override
            public void onUpdateCompleted(String s, String s1, String s2) {
                loge("升级完成-->" + s + ",s1 = " + s1 + ",s2 = " + s2);
                toast("升级完成-->" + s + ",s1 = " + s1 + ",s2 = " + s2);
                dismissDownloadDialog();
            }

            @Override
            public void onDFUTransfer(String s, int i, float v, float v1, int i1, int i2, String s1) {
                loge("onDFUTransfer==========s = " + s + ",i = " + i + ",v = " + v + ",v1 = " + v1 + ",i1 = " + i1
                        + ",i2 = " + i2 + ",s1 = " + s1);
                updateProgress(i);
            }

            @Override
            public void onUpdateValidating(String s, String s1) {
                toast("onUpdateValidating=====" + s + "s1 = " + s1);
                loge("检验文件：onUpdateValidating=====" + s + "s1 = " + s1);
            }

            @Override
            public void onUpdateTimeout(int i, Object o, String s) {
                loge("超时");
            }

            @Override
            public void onDisconnecting() {
                toast("设备断开");
                loge("断开设备连接");
            }


            @Override
            public void onFailed(String s, String s1, Throwable throwable) {
                toast("升级失败======" + s + ",s1 = " + s1 + ",msg = " + (throwable == null ? "e 为空" : throwable
                        .getMessage()));
                loge("升级失败======" + s + ",s1 = " + s1 + ",msg = " + (throwable == null ? "e 为空" : throwable
                        .getMessage()));
                dismissDownloadDialog();
            }
        });
    }

    /**
     * 加入生命周期方法onSessionResume！！！
     */
    @Override
    protected void onResume() {
        super.onResume();
        mSensoroDeviceSession.onSessionResume();
    }

    /**
     * 加入生命周期方法onSessonPause！！！
     */
    @Override
    protected void onPause() {
        super.onPause();
        mSensoroDeviceSession.onSessonPause();
    }

    /**
     * 显示升级提示
     */
    private void showNoticeDialog() {
        new AlertDialog.Builder(this)
                .setTitle("即将升级")
                .setMessage("设备MacAddress：" + mSensoroDevice.getMacAddress())
                .setPositiveButton("升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showDownloadDialog();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    /**
     * 显示现在进度
     */
    private void showDownloadDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("请稍后...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        testUpdate();
    }

    /**
     * 更新进度
     *
     * @param progress
     */
    private void updateProgress(int progress) {
        if (progressDialog != null) {
            progressDialog.setProgress(progress);
        }
    }

    /**
     * 取消下载进度条
     */
    private void dismissDownloadDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void loge(String msg) {
        Log.e("ddong1031", "loge: ---------" + msg);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 拷贝assets文件
     *
     * @throws IOException
     */
    private void copyBigDataToSD() throws IOException {

        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(tempPath);
        myInput = this.getAssets().open("SENSORO_SMOKE_FHSJ_H605_V110_DFU_v2.1.0_828ff7b_20190213.zip");
//        myInput = this.getAssets().open("tracker_dfu_test.zip");
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while (length > 0) {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }
        myOutput.flush();
        myInput.close();
        myOutput.close();
        loge("文件拷贝完成！");
    }

}