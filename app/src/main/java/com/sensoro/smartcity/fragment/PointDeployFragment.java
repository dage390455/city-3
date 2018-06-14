package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.client.android.ViewfinderView;
import com.google.zxing.client.android.camera.CameraManager;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IPointDeployFragmentView;
import com.sensoro.smartcity.presenter.PointDeployFragmentPresenter;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroToast;

import static com.sensoro.smartcity.constant.Constants.INPUT;

/**
 * Created by sensoro on 17/7/24.
 */

public class PointDeployFragment extends BaseFragment<IPointDeployFragmentView, PointDeployFragmentPresenter>
        implements IPointDeployFragmentView, View.OnClickListener {
    private static final String TAG = PointDeployFragment.class.getSimpleName();


    private TextView statusView;

    private ViewfinderView viewfinderView;

    private ImageView mMenuImageView;
    private ImageView flashImageView;
    private ImageView manualImageView;


    private SurfaceView surfaceView;
    private ProgressUtils mProgressUtils;
    //    protected boolean isCreate = false;

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return mPrestener.getHandler();
    }

    public CameraManager getCameraManager() {
        return mPrestener.getCameraManager();
    }

    public static PointDeployFragment newInstance(String input) {
        PointDeployFragment pointDeployFragment = new PointDeployFragment();
        Bundle args = new Bundle();
        args.putString(INPUT, input);
        pointDeployFragment.setArguments(args);
        return pointDeployFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void initCamera() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mRootFragment.getActivity()).build());
        flashImageView = (ImageView) mRootView.findViewById(R.id.zxing_capture_iv_flash);
        manualImageView = (ImageView) mRootView.findViewById(R.id.zxing_capture_iv_manual);
        mMenuImageView = (ImageView) mRootView.findViewById(R.id.deploy_iv_menu_list);
        mMenuImageView.setColorFilter(getResources().getColor(R.color.white));
        surfaceView = (SurfaceView) mRootView.findViewById(R.id.preview_view);
        statusView = (TextView) mRootView.findViewById(R.id.status_view);
        ImageView mQrLineView = (ImageView) mRootView.findViewById(R.id.capture_scan_line);
        viewfinderView = (ViewfinderView) mRootView.findViewById(R.id.viewfinder_view);
        mMenuImageView.setOnClickListener(this);
        flashImageView.setOnClickListener(this);
        manualImageView.setOnClickListener(this);

        TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation
                .ABSOLUTE, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
        mAnimation.setDuration(1500);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(new LinearInterpolator());
        mQrLineView.setAnimation(mAnimation);

    }


    @Override
    public void onResume() {
        super.onResume();
        mPrestener.resumeCamera(surfaceView.getHolder());
        showRootView();
        // historyManager must be initialized here to update the history preference
        System.out.println("PointDeploy.OnResume===>");

    }


    public void hiddenRootView() {
//        if (!isHidden) {
////            cameraManager.closeDriver();
//            isHidden = true;
//            isShow = false;
//            pauseCamera();
//        }
        if (mRootView != null) {
            mRootView.setVisibility(View.GONE);
        }
//        cameraManager.closeDriver();
    }

    public void showRootView() {
        try {
//            if (!isShow) {
//                isShow = true;
//                isHidden = false;
////                cameraManager.openDriver(surfaceHolder);
//                resumeCamera();
//            }
            mRootView.setVisibility(View.VISIBLE);
            surfaceView.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        mPrestener.pauseCamera();
        hiddenRootView();
        System.out.println("PointDeploy.OnPause===>");
    }

    @Override
    protected void initData(Context activity) {
        hiddenRootView();
        initCamera();
        mPrestener.initData(activity);
        mPrestener.getFragment((PointDeployFragment) mRootFragment);
    }


    @Override
    public void onDestroy() {
        mProgressUtils.destroyProgress();
        mPrestener.onDestroy();
        super.onDestroy();
    }

    @Override
    protected int initRootViewId() {
        return R.layout.fragment_point_deploy;
    }

    @Override
    protected PointDeployFragmentPresenter createPresenter() {
        return new PointDeployFragmentPresenter();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.zxing_capture_iv_flash:
                mPrestener.openLight();
                break;
            case R.id.zxing_capture_iv_manual:
                mPrestener.openSNTextAc();
                break;
            case R.id.deploy_iv_menu_list:
                ((MainActivity) getActivity()).getMenuDrawer().openMenu();
                break;
            default:
                break;
        }
    }


    /**
     * A valid barcode has been found, so give an indication of success and show the results.
     *
     * @param rawResult   The contents of the barcode.
     * @param scaleFactor amount by which thumbnail was scaled
     * @param barcode     A greyscale bitmap of the camera data which was decoded.
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        mPrestener.handleDecode(rawResult, barcode, scaleFactor);
    }

    @Override
    public void resetStatusView() {
        statusView.setText(R.string.msg_default_status);
        statusView.setVisibility(View.VISIBLE);
        viewfinderView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setCameraCapture() {
        WindowManager manager = (WindowManager) mRootFragment.getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point theScreenResolution = new Point();
        display.getSize(theScreenResolution);
        if (theScreenResolution != null) {
            int width = CameraManager.findDesiredDimensionInRange(theScreenResolution.x, CameraManager
                    .MIN_FRAME_WIDTH, CameraManager.MAX_FRAME_WIDTH);
            int height = CameraManager.findDesiredDimensionInRange(theScreenResolution.y, CameraManager
                    .MIN_FRAME_HEIGHT, CameraManager.MAX_FRAME_HEIGHT);

            int leftOffset = (theScreenResolution.x - width) / 2;
            int topOffset = (theScreenResolution.y - height) / 2;
            Rect framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);

            RelativeLayout.LayoutParams topMaskLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, framingRect.top);
            topMaskLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            ImageView topMask = (ImageView) mRootView.findViewById(R.id.top_mask);
            topMask.setLayoutParams(topMaskLayoutParams);
            RelativeLayout.LayoutParams captureLayoutParams = new RelativeLayout.LayoutParams(framingRect.width(),
                    framingRect.height());
            captureLayoutParams.addRule(RelativeLayout.BELOW, R.id.top_mask);
            captureLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            RelativeLayout captureLayout = (RelativeLayout) mRootView.findViewById(R.id.capture_crop_layout);
            captureLayout.setLayoutParams(captureLayoutParams);
        } else {
            toastShort("相机初始化失败");
        }
    }

    @Override
    public void setStatusInfo(String info) {
        statusView.setText(info);
    }

    @Override
    public boolean isNotVisibleOrResumed() {
        return !this.isVisible() && !this.isResumed();
    }

    @Override
    public void setFlashLightState(boolean isOn) {
        flashImageView.setBackgroundResource(isOn ? R.drawable.zxing_flash_on : R.drawable.zxing_flash_off);
    }


    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.makeText(mRootFragment.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }

    @Override
    public void setCameraManager(CameraManager cameraManager) {
        viewfinderView.setCameraManager(cameraManager);
    }

    @Override
    public void startAC(Intent intent) {

    }

    @Override
    public void finishAc() {

    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {
        mRootFragment.getActivity().startActivityForResult(intent, requestCode);
    }

    @Override
    public void setIntentResult(int requestCode) {

    }

    @Override
    public void setIntentResult(int requestCode, Intent data) {

    }

    @Override
    public void showProgressDialog() {
        mProgressUtils.showProgress();
    }

    @Override
    public void dismissProgressDialog() {
        mProgressUtils.dismissProgress();
    }
}
