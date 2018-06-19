package com.sensoro.smartcity.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.SurfaceHolder;

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
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.fragment.PointDeployFragment;
import com.sensoro.smartcity.imainviews.IPointDeployFragmentView;
import com.sensoro.smartcity.iwidget.IOndestroy;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.response.CityObserver;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.util.LogUtils;

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
import static com.sensoro.smartcity.constant.Constants.REQUEST_CODE_DEPLOY;

public class PointDeployFragmentPresenter extends BasePresenter<IPointDeployFragmentView> implements IOndestroy,
        SurfaceHolder.Callback {
    public CaptureActivityHandler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    private Activity mContext;
    private CameraManager cameraManager;
    private Result savedResultToShow;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, ?> decodeHints;
    private String characterSet;

    private HistoryManager historyManager;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private AmbientLightManager ambientLightManager;

    private volatile boolean isFlashOn;
    private SurfaceHolder surfaceHolder;
    private volatile boolean hasSurface;

    private CaptureActivityHandler handler;
    public static final long BULK_MODE_SCAN_DELAY_MS = 1000L;
    public static final int HISTORY_REQUEST_CODE = 0x0000bacc;
    public static final int PHOTO_REQUEST_CODE = 0x000000aa;
    private static final Collection<ResultMetadataType> DISPLAYABLE_METADATA_TYPES =
            EnumSet.of(ResultMetadataType.ISSUE_NUMBER,
                    ResultMetadataType.SUGGESTED_PRICE,
                    ResultMetadataType.ERROR_CORRECTION_LEVEL,
                    ResultMetadataType.POSSIBLE_COUNTRY);
    private static final int REQUEST_TAKE_PHOTO_PERMISSION = 0x102;
    private PointDeployFragment pointDeployFragment;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        inactivityTimer = new InactivityTimer(mContext);
        beepManager = new BeepManager(mContext);
        ambientLightManager = new AmbientLightManager(mContext);
        PreferenceManager.setDefaultValues(mContext, R.xml.preferences, false);
        hasSurface = false;
    }

    public void getFragment(PointDeployFragment pointDeployFragment) {
        this.pointDeployFragment = pointDeployFragment;
    }

    public void resumeCamera(SurfaceHolder holder) {
        try {
            this.surfaceHolder = holder;
            // historyManager must be initialized here to update the history preference
            historyManager = new HistoryManager(mContext);
            historyManager.trimHistory();

            // CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
            // want to open the camera driver and measure the screen size if we're going to show the help on
            // first launch. That led to bugs where the scanning rectangle was the wrong size and partially
            // off screen.
            cameraManager = new CameraManager(mContext);
//        viewfinderView = (ViewfinderView) mRootView.findViewById(R.id.viewfinder_view);
//        viewfinderView.setCameraManager(cameraManager);
            getView().setCameraManager(cameraManager);
            getView().resetStatusView();
//        resetStatusView();


            if (hasSurface) {
                // The activity was paused but not stopped, so the surface still exists. Therefore
                // surfaceCreated() won't be called, so initView the camera here.
                System.out.println("=====>hasSurface");
                initCamera();
            } else {
                System.out.println("=====>hasSurface.false");
                // Install the callback and wait for surfaceCreated() to initView the camera.
                surfaceHolder.addCallback(this);
            }
            getView().setCameraCapture();
            beepManager.updatePrefs();
            ambientLightManager.start(cameraManager);
            inactivityTimer.onResume();

            Intent intent = mContext.getIntent();

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
                        getView().setStatusInfo(customPromptMessage);
//                    statusView.setEditText(customPromptMessage);
                    }
                }
                characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);
                isFlashOn = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void pauseCamera() {
        try {
            if (handler != null) {
                handler.quitSynchronously();
                handler = null;
            }
            inactivityTimer.onPause();
            ambientLightManager.stop();
            beepManager.close();
            cameraManager.closeDriver();
            cameraManager = null;
            historyManager = null;
            if (!hasSurface) {
                surfaceHolder.removeCallback(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    private void refresh(DeviceInfoListRsp response) {
        try {
            Intent intent = new Intent();
            if (response.getData().size() > 0) {
                intent.setClass(mContext, DeployActivity.class);
                intent.putExtra(EXTRA_DEVICE_INFO, response.getData().get(0));
                intent.putExtra("uid", mContext.getIntent().getStringExtra("uid"));
                getView().startACForResult(intent, REQUEST_CODE_DEPLOY);
            } else {
                intent.setClass(mContext, DeployResultActivity.class);
                intent.putExtra(EXTRA_SENSOR_RESULT, -1);
                getView().startACForResult(intent, REQUEST_CODE_DEPLOY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            paint.setColor(mContext.getResources().getColor(R.color.result_points));
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

    @SuppressLint("LongLogTag")
    private void initCamera() {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            LogUtils.logd(this, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the pushHandler starts the preview, which can also throw a RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(pointDeployFragment, decodeFormats, decodeHints,
                        characterSet, cameraManager);
            }
            decodeOrStoreSavedBitmap(null, null);

        } catch (IOException ioe) {
            LogUtils.logd(this, ioe.getMessage());
//            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            LogUtils.logd(this, "Unexpected error initializing camera" + e.getMessage());
//            displayFrameworkBugMessageAndExit();
        }
    }


    private void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
        getView().resetStatusView();
    }

    protected BinaryBitmap loadImage(Bitmap bitmap, Context context) throws IOException {
        int lWidth = bitmap.getWidth();
        int lHeight = bitmap.getHeight();
        int[] lPixels = new int[lWidth * lHeight];
        bitmap.getPixels(lPixels, 0, lWidth, 0, 0, lWidth, lHeight);
        return new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(lWidth, lHeight, lPixels)));
    }

    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        inactivityTimer.onActivity();
        ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(mContext, rawResult);
        boolean fromLiveScan = barcode != null;
        if (fromLiveScan) {
            historyManager.addHistoryItem(rawResult, resultHandler);
            // Then not from history, so beep/vibrate and we have an image to draw on
            beepManager.playBeepSoundAndVibrate();
            /** 将扫描图片关键信息高亮处理，如果不需要图像相关功能，可以禁用 **/
//            drawResultPoints(barcode, scaleFactor, rawResult);
        }
        /** 连续扫描 **/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
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
        int currentIndex = ((MainActivity) mContext).getSensoroPager().getCurrentItem();
        System.out.println("currentIndex==>" + currentIndex);
        if (currentIndex != 3) {
            return;
        }
        if (TextUtils.isEmpty(result)) {
            getView().toastShort(mContext.getResources().getString(R.string.scan_failed));
            return;
        }
//        System.out.println("this.isResumed()==>" + this.isResumed());
        if (getView().isNotVisibleOrResumed()) {
            return;
        }
        String scanSerialNumber = parseResultMac(result);
        if (scanSerialNumber == null) {
            getView().toastShort(mContext.getResources().getString(R.string.invalid_qr_code));
        } else {
            scanFinish(scanSerialNumber);
        }
    }

    private void scanFinish(String scanSerialNumber) {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getDeviceDetailInfoList(scanSerialNumber.toUpperCase(), null, 1).subscribeOn
                (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>() {
            @Override
            public void onCompleted() {
                getView().dismissProgressDialog();
            }

            @Override
            public void onNext(DeviceInfoListRsp deviceInfoListRsp) {
                refresh(deviceInfoListRsp);
            }

            @Override
            public void onErrorMsg(String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    @Override
    public void onDestroy() {
        inactivityTimer.shutdown();
    }

    public void openLight() {
        isFlashOn = !isFlashOn;
        if (cameraManager != null) {
            cameraManager.setTorch(isFlashOn);
//        getView().setFlashLightState(isFlashOn);
        }

    }

    public void openSNTextAc() {
        Intent intent = new Intent(mContext, DeployManualActivity.class);
        getView().startACForResult(intent, REQUEST_CODE_DEPLOY);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            LogUtils.logd(this, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            initCamera();
            hasSurface = true;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }
}
