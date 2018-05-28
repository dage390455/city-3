package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
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

import com.baidu.mobstat.StatService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.AmbientLightManager;
import com.google.zxing.client.android.BeepManager;
import com.google.zxing.client.android.CaptureActivityHandler;
import com.google.zxing.client.android.DecodeFormatManager;
import com.google.zxing.client.android.DecodeHintManager;
import com.google.zxing.client.android.InactivityTimer;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.android.PreferencesActivity;
import com.google.zxing.client.android.ViewfinderView;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.history.HistoryManager;
import com.google.zxing.client.android.result.ResultHandler;
import com.google.zxing.client.android.result.ResultHandlerFactory;
import com.google.zxing.common.HybridBinarizer;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.DeployActivity;
import com.sensoro.smartcity.activity.DeployManualActivity;
import com.sensoro.smartcity.activity.DeployResultActivity;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.response.CityObserver;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.widget.ProgressUtils;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.sensoro.smartcity.constant.Constants.EXTRA_DEVICE_INFO;
import static com.sensoro.smartcity.constant.Constants.EXTRA_SENSOR_RESULT;
import static com.sensoro.smartcity.constant.Constants.INPUT;
import static com.sensoro.smartcity.constant.Constants.REQUEST_CODE_DEPLOY;

/**
 * Created by sensoro on 17/7/24.
 */

public class PointDeployFragment extends Fragment implements View.OnClickListener, SurfaceHolder.Callback {
    private static final String TAG = PointDeployFragment.class.getSimpleName();

    private static final long BULK_MODE_SCAN_DELAY_MS = 1000L;
    public static final int HISTORY_REQUEST_CODE = 0x0000bacc;
    public static final int PHOTO_REQUEST_CODE = 0x000000aa;
    private static final Collection<ResultMetadataType> DISPLAYABLE_METADATA_TYPES =
            EnumSet.of(ResultMetadataType.ISSUE_NUMBER,
                    ResultMetadataType.SUGGESTED_PRICE,
                    ResultMetadataType.ERROR_CORRECTION_LEVEL,
                    ResultMetadataType.POSSIBLE_COUNTRY);
    private static final int REQUEST_TAKE_PHOTO_PERMISSION = 0x102;
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private Result savedResultToShow;
    private TextView statusView;
    private boolean hasSurface;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, ?> decodeHints;
    private String characterSet;
    private ViewfinderView viewfinderView;
    private HistoryManager historyManager;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private AmbientLightManager ambientLightManager;
    private ImageView mMenuImageView;
    private ImageView flashImageView;
    private ImageView manualImageView;
    private boolean isFlashOn;
    private View rootView;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private ProgressUtils mProgressUtils;
    //    protected boolean isCreate = false;

    public PointDeployFragment() {
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
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
        Window window = this.getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void initCamera() {
        flashImageView = (ImageView) rootView.findViewById(R.id.zxing_capture_iv_flash);
        manualImageView = (ImageView) rootView.findViewById(R.id.zxing_capture_iv_manual);
        mMenuImageView = (ImageView) rootView.findViewById(R.id.deploy_iv_menu_list);
        mMenuImageView.setColorFilter(getResources().getColor(R.color.white));
        mMenuImageView.setOnClickListener(this);
        flashImageView.setOnClickListener(this);
        manualImageView.setOnClickListener(this);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this.getActivity());
        beepManager = new BeepManager(this.getActivity());
        ambientLightManager = new AmbientLightManager(this.getActivity());

        PreferenceManager.setDefaultValues(this.getActivity(), R.xml.preferences, false);
        surfaceView = (SurfaceView) rootView.findViewById(R.id.preview_view);
        statusView = (TextView) rootView.findViewById(R.id.status_view);
        ImageView mQrLineView = (ImageView) rootView.findViewById(R.id.capture_scan_line);
        TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation
                .ABSOLUTE, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
        mAnimation.setDuration(1500);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(new LinearInterpolator());
        mQrLineView.setAnimation(mAnimation);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            try {
                rootView = inflater.inflate(R.layout.fragment_point_deploy, container, false);
                hiddenRootView();
                initCamera();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), R.string.tips_data_error, Toast.LENGTH_SHORT).show();
            }

        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeCamera();
        showRootView();
        StatService.onPageStart(getActivity(), "PointDeloyFragment");
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
        if (rootView != null) {
            rootView.setVisibility(View.GONE);
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
            rootView.setVisibility(View.VISIBLE);
            surfaceView.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resumeCamera() {
        // historyManager must be initialized here to update the history preference
        historyManager = new HistoryManager(this.getActivity());
        historyManager.trimHistory();

        // CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
        // want to open the camera driver and measure the screen size if we're going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the wrong size and partially
        // off screen.
        cameraManager = new CameraManager(this.getContext());
        viewfinderView = (ViewfinderView) rootView.findViewById(R.id.viewfinder_view);
        viewfinderView.setCameraManager(cameraManager);
        handler = null;
        resetStatusView();

        surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            // The activity was paused but not stopped, so the surface still exists. Therefore
            // surfaceCreated() won't be called, so initView the camera here.
            System.out.println("=====>hasSurface");
            initCamera(surfaceHolder);
        } else {
            System.out.println("=====>hasSurface.false");
            // Install the callback and wait for surfaceCreated() to initView the camera.
            surfaceHolder.addCallback(this);
        }
        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
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
            ImageView topMask = (ImageView) rootView.findViewById(R.id.top_mask);
            topMask.setLayoutParams(topMaskLayoutParams);
            RelativeLayout.LayoutParams captureLayoutParams = new RelativeLayout.LayoutParams(framingRect.width(),
                    framingRect.height());
            captureLayoutParams.addRule(RelativeLayout.BELOW, R.id.top_mask);
            captureLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            RelativeLayout captureLayout = (RelativeLayout) rootView.findViewById(R.id.capture_crop_layout);
            captureLayout.setLayoutParams(captureLayoutParams);
        } else {
            Toast.makeText(getContext(), "相机初始化失败", Toast.LENGTH_SHORT).show();
        }
        beepManager.updatePrefs();
        ambientLightManager.start(cameraManager);
        inactivityTimer.onResume();

        Intent intent = this.getActivity().getIntent();

        decodeFormats = null;
        characterSet = null;

        if (intent != null) {
            String action = intent.getAction();
            if (Intents.Scan.ACTION.equals(action)) {

                // Scan the formats the intent requested, and return the result to the calling activity.
                decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
                decodeHints = DecodeHintManager.parseDecodeHints(intent);

                if (intent.hasExtra(Intents.Scan.WIDTH) && intent.hasExtra(Intents.Scan.HEIGHT)) {
                    int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
                    int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
                    if (width > 0 && height > 0) {
                        cameraManager.setManualFramingRect(width, height);
                    }
                }

                if (intent.hasExtra(Intents.Scan.CAMERA_ID)) {
                    int cameraId = intent.getIntExtra(Intents.Scan.CAMERA_ID, -1);
                    if (cameraId >= 0) {
                        cameraManager.setManualCameraId(cameraId);
                    }
                }

                String customPromptMessage = intent.getStringExtra(Intents.Scan.PROMPT_MESSAGE);
                if (customPromptMessage != null) {
                    statusView.setText(customPromptMessage);
                }

            }

            characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);
            isFlashOn = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseCamera();
        hiddenRootView();
        StatService.onPageEnd(getActivity(), "PointDeloyFragment");
        System.out.println("PointDeploy.OnPause===>");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    public void pauseCamera() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        ambientLightManager.stop();
        beepManager.close();
        cameraManager.closeDriver();
        historyManager = null;
        if (!hasSurface) {
            surfaceHolder.removeCallback(this);
        }
        if(mProgressUtils!=null){
            mProgressUtils.destroyProgress();
        }
    }

    @Override
    public void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.zxing_capture_iv_flash:
                if (!isFlashOn) {
                    isFlashOn = true;
//                    flashImageView.setBackgroundResource(R.drawable.zxing_flash_on);
                    cameraManager.setTorch(true);
                } else {
                    isFlashOn = false;
//                    flashImageView.setBackgroundResource(R.drawable.zxing_flash_off);
                    cameraManager.setTorch(false);
                }
                break;
            case R.id.zxing_capture_iv_manual:
                Intent intent = new Intent(getActivity(), DeployManualActivity.class);
                startActivityForResult(intent, REQUEST_CODE_DEPLOY);
                break;
            case R.id.deploy_iv_menu_list:
                ((MainActivity) getActivity()).getMenuDrawer().openMenu();
                break;
            default:
                break;
        }
    }

    protected BinaryBitmap loadImage(Bitmap bitmap, Context context) throws IOException {
        int lWidth = bitmap.getWidth();
        int lHeight = bitmap.getHeight();
        int[] lPixels = new int[lWidth * lHeight];
        bitmap.getPixels(lPixels, 0, lWidth, 0, 0, lWidth, lHeight);
        return new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(lWidth, lHeight, lPixels)));
    }

    private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
        // Bitmap isn't used yet -- will be used soon
        if (handler == null) {
            savedResultToShow = result;
        } else {
            if (result != null) {
                savedResultToShow = result;
            }
            if (savedResultToShow != null) {
                Message message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow);
                handler.sendMessage(message);
            }
            savedResultToShow = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * A valid barcode has been found, so give an indication of success and show the results.
     *
     * @param rawResult   The contents of the barcode.
     * @param scaleFactor amount by which thumbnail was scaled
     * @param barcode     A greyscale bitmap of the camera data which was decoded.
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        inactivityTimer.onActivity();
        ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(getActivity(), rawResult);
        boolean fromLiveScan = barcode != null;
        if (fromLiveScan) {
            historyManager.addHistoryItem(rawResult, resultHandler);
            // Then not from history, so beep/vibrate and we have an image to draw on
            beepManager.playBeepSoundAndVibrate();
            /** 将扫描图片关键信息高亮处理，如果不需要图像相关功能，可以禁用 **/
//            drawResultPoints(barcode, scaleFactor, rawResult);
        }
        /** 连续扫描 **/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (fromLiveScan && prefs.getBoolean(PreferencesActivity.KEY_BULK_MODE, false)) {
            // Wait a moment or else it will scan the same barcode continuously about 3 times
            restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
        }
        /** 自动打开网页 **/
        if (resultHandler.getDefaultButtonID() != null && prefs.getBoolean(PreferencesActivity.KEY_AUTO_OPEN_WEB,
                false)) {
            resultHandler.handleButtonPress(resultHandler.getDefaultButtonID());
            return;
        }
        processResultCustome(rawResult.getText());
    }

    /**
     * process result
     */
    private void processResultCustome(String result) {
        int currentIndex = ((MainActivity) getActivity()).getSensoroPager().getCurrentItem();
        System.out.println("currentIndex==>" + currentIndex);
        if (currentIndex != 3) {
            return;
        }
        if (TextUtils.isEmpty(result)) {
            Toast.makeText(getContext(), R.string.scan_failed, Toast.LENGTH_SHORT).show();
            return;
        }
        System.out.println("this.isResumed()==>" + this.isResumed());
        if (!this.isVisible() && !this.isResumed()) {
            return;
        }
        String scanSerialNumber = parseResultMac(result);
        if (scanSerialNumber == null) {
            Toast.makeText(getContext(), R.string.invalid_qr_code, Toast.LENGTH_SHORT).show();
        } else {
            mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(this.getActivity()).build());
            mProgressUtils.showProgress();
            RetrofitServiceHelper.INSTANCE.getDeviceDetailInfoList(scanSerialNumber.toUpperCase(),null,1).subscribeOn
                    (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>() {

                @Override
                public void onCompleted() {
                    mProgressUtils.dismissProgress();
                }

                @Override
                public void onNext(DeviceInfoListRsp deviceInfoListRsp) {
                    refresh(deviceInfoListRsp);
                }

                @Override
                public void onErrorMsg(String errorMsg) {
                    mProgressUtils.dismissProgress();
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            });
//            NetUtils.INSTANCE.getServer().getDeviceDetailInfoList(scanSerialNumber, null, 1,
//                    new Response
//                            .Listener<DeviceInfoListRsp>() {
//                        @Override
//                        public void onResponse(DeviceInfoListRsp response) {
//
//                            mProgressUtils.dismissProgress();
//                            refresh(response);
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError volleyError) {
//                            mProgressUtils.dismissProgress();
//                            if (volleyError.networkResponse != null) {
//                                String reason = new String(volleyError.networkResponse.data);
//                                try {
//                                    JSONObject jsonObject = new JSONObject(reason);
//                                    Toast.makeText(getContext(), jsonObject.getString("errmsg"), Toast.LENGTH_SHORT)
//                                            .show();
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                } catch (Exception e) {
//
//                                }
//                            } else {
//                                Toast.makeText(getContext(), R.string.tips_network_error, Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });

        }
    }

    private void refresh(DeviceInfoListRsp response) {
        try {
            Intent intent = new Intent();
            if (response.getData().size() > 0) {
                intent.setClass(getContext(), DeployActivity.class);
                intent.putExtra(EXTRA_DEVICE_INFO, response.getData().get(0));
                intent.putExtra("uid", this.getActivity().getIntent().getStringExtra("uid"));
                startActivityForResult(intent, REQUEST_CODE_DEPLOY);
            } else {
                intent.setClass(getContext(), DeployResultActivity.class);
                intent.putExtra(EXTRA_SENSOR_RESULT, -1);
                startActivityForResult(intent, REQUEST_CODE_DEPLOY);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String parseResultMac(String result) {
        String serialNumber = null;
        if (result != null) {
            String[] data = null;
            String type = null;
            data = result.split("\\|");
            // if length is 2, it is fault-tolerant hardware.
            type = data[0];
//            if (type.length() == 2) {
//                serialNumber = data[1];
//            } else {
//                serialNumber = data[0].substring(data[0].length() - 12);
//            }
            serialNumber = type;
        }
        return serialNumber;
    }

    public static byte[] getSignature(byte[] data, String baseKey) {
        Mac shaMac;
        byte[] secretBytes = baseKey.getBytes();
        byte[] signatureBytes = null;
        try {
            shaMac = Mac.getInstance(Constants.ENCODE);
            SecretKey secretKey = new SecretKeySpec(secretBytes,
                    Constants.ENCODE);
            shaMac.init(secretKey);
            signatureBytes = shaMac.doFinal(data);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return signatureBytes;
    }

    /**
     * Superimpose a line for 1D or dots for 2D to highlight the key features of the barcode.
     *
     * @param barcode     A bitmap of the captured image.
     * @param scaleFactor amount by which thumbnail was scaled
     * @param rawResult   The decoded results which contains the points to draw.
     */
    private void drawResultPoints(Bitmap barcode, float scaleFactor, Result rawResult) {
        ResultPoint[] points = rawResult.getResultPoints();
        if (points != null && points.length > 0) {
            Canvas canvas = new Canvas(barcode);
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.result_points));
            if (points.length == 2) {
                paint.setStrokeWidth(4.0f);
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
            } else if (points.length == 4 &&
                    (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A ||
                            rawResult.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
                // Hacky special case -- draw two lines, for the barcode and metadata
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
                drawLine(canvas, paint, points[2], points[3], scaleFactor);
            } else {
                paint.setStrokeWidth(10.0f);
                for (ResultPoint point : points) {
                    if (point != null) {
                        canvas.drawPoint(scaleFactor * point.getX(), scaleFactor * point.getY(), paint);
                    }
                }
            }
        }
    }

    private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b, float scaleFactor) {
        if (a != null && b != null) {
            canvas.drawLine(scaleFactor * a.getX(),
                    scaleFactor * a.getY(),
                    scaleFactor * b.getX(),
                    scaleFactor * b.getY(),
                    paint);
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the pushHandler starts the preview, which can also throw a RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
            }
            decodeOrStoreSavedBitmap(null, null);

        } catch (IOException ioe) {
            Log.e(TAG, ioe.getMessage());
//            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.e(TAG, "Unexpected error initializing camera", e);
//            displayFrameworkBugMessageAndExit();
        }
    }


    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
        resetStatusView();
    }


    private void resetStatusView() {
        statusView.setText(R.string.msg_default_status);
        statusView.setVisibility(View.VISIBLE);
        viewfinderView.setVisibility(View.VISIBLE);
    }


    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }
}
