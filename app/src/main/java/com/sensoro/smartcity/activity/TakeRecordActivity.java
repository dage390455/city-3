package com.sensoro.smartcity.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gyf.immersionbar.ImmersionBar;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.model.ImageItem;
import com.sensoro.common.utils.MyPermissionManager;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.cameralibrary.JCameraView;
import com.sensoro.smartcity.cameralibrary.listener.JCameraListener;
import com.sensoro.smartcity.imainviews.ITakeRecordActivityView;
import com.sensoro.smartcity.presenter.TakeRecordActivityPresenter;
import com.sensoro.common.utils.WidgetUtil;

import java.io.File;
import java.io.Serializable;

import static com.sensoro.common.constant.Constants.RESULT_CODE_RECORD;
@Route(path = ARouterConstants.ACTIVITY_TAKE_RECORD)
public class TakeRecordActivity extends BaseActivity<ITakeRecordActivityView, TakeRecordActivityPresenter> implements ITakeRecordActivityView, View.OnClickListener {

    private ImageView imv_back;
    public ImmersionBar immersionBar;
    private JCameraView jCameraView;
    private final int GET_PERMISSION_REQUEST = 100; //权限申请自定义码
    private boolean granted = false;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_take_record);
        //
        initView();
        getPermissions();

        mPresenter.initData(mActivity);
    }

    private void initView() {
        jCameraView = findViewById(R.id.jcameraview);

        imv_back = findViewById(R.id.imv_back);


        jCameraView = (JCameraView) findViewById(R.id.jcameraview);

        //设置视频保存路径
        jCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "JCamera");

        //JCameraView监听
        jCameraView.setJCameraLisenter(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {

            }

            @Override
            public void recordSuccess(String videoPath, Bitmap firstFrame, int mProgress) {
                Log.i("JCameraView", videoPath + "===bitmap = " + firstFrame.getWidth());


                final String videoThumbPath = WidgetUtil.bitmap2File(firstFrame, videoPath);


                Intent intent = new Intent();
                final ImageItem imageItem = new ImageItem();
                imageItem.isRecord = true;
                imageItem.addTime = mProgress;
                imageItem.path = videoPath;
                imageItem.thumbPath = videoThumbPath;
                imageItem.name = videoPath.substring(videoPath.lastIndexOf("/") + 1);
                intent.putExtra(Constants.EXTRA_PATH_RECORD, (Serializable) imageItem);
                setFinishResult(RESULT_CODE_RECORD, intent);
                finishAc();

            }
        });

        imv_back.setOnClickListener(this);
    }

    @Override
    public void setFinishResult(int resultCodeRecord, Intent intent) {
        mActivity.setResult(resultCodeRecord, intent);
    }


    @Override
    public boolean isActivityOverrideStatusBar() {
        immersionBar = ImmersionBar.with(mActivity);
        immersionBar.transparentStatusBar().init();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (granted) {
            jCameraView.onResume();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        jCameraView.onPause();


    }

    @Override
    protected TakeRecordActivityPresenter createPresenter() {
        return new TakeRecordActivityPresenter();
    }

    @Override
    public void onBackPressed() {
        jCameraView.onPause();
        super.onBackPressed();
    }


    @Override
    protected void onStart() {
        super.onStart();
        //全屏显示
//        if (Build.VERSION.SDK_INT >= 19) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        } else {
//            View decorView = getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
//            decorView.setSystemUiVisibility(option);
//        }
    }


    @Override
    protected void onDestroy() {

//        if (immersionBar != null) {
//            immersionBar.destroy();
//        }
        mPresenter.onDestroy();
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();


                break;
        }
    }


    @Override
    public void startAC(Intent intent) {
        mActivity.startActivity(intent);
    }

    @Override
    public void finishAc() {
        finish();
    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {
        mActivity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void setIntentResult(int resultCode) {

    }


    @Override
    public void setIntentResult(int resultCode, Intent data) {

    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void dismissProgressDialog() {

    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }

    /**
     * 获取权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                //具有权限
                granted = true;
            } else {
                //不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions(TakeRecordActivity.this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA}, GET_PERMISSION_REQUEST);
                granted = false;
            }
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GET_PERMISSION_REQUEST) {
            int size = 0;
            if (grantResults.length >= 1) {
                int writeResult = grantResults[0];
                //读写内存权限
                boolean writeGranted = writeResult == PackageManager.PERMISSION_GRANTED;//读写内存权限
                if (!writeGranted) {
                    size++;
                }
                //录音权限
                int recordPermissionResult = grantResults[1];
                boolean recordPermissionGranted = recordPermissionResult == PackageManager.PERMISSION_GRANTED;
                if (!recordPermissionGranted) {
                    size++;
                }
                //相机权限
                int cameraPermissionResult = grantResults[2];
                boolean cameraPermissionGranted = cameraPermissionResult == PackageManager.PERMISSION_GRANTED;
                if (!cameraPermissionGranted) {
                    size++;
                }
                if (size == 0) {
                    granted = true;
                    jCameraView.onResume();
                } else {
                    SensoroToast.getInstance().makeText(mActivity, getResources().getString(R.string.please_go_to_setting), Toast.LENGTH_SHORT);
                    MyPermissionManager.startAppSetting(mActivity);
                    finish();
                }
            }
        }
    }
}
