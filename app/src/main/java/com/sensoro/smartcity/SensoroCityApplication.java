package com.sensoro.smartcity;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.github.moduth.blockcanary.BlockCanary;
import com.mapbox.mapboxsdk.Mapbox;
import com.sensoro.common.base.BaseApplication;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.manger.ActivityTaskManager;
import com.sensoro.common.manger.ThreadPoolManager;
import com.sensoro.common.model.EventData;
import com.sensoro.common.model.IbeaconSettingData;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.utils.LogUtils;
import com.sensoro.common.utils.Repause;
import com.sensoro.libbleserver.ble.entity.BLEDevice;
import com.sensoro.libbleserver.ble.entity.IBeacon;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceListener;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceManager;
import com.sensoro.smartcity.activity.SplashActivity;
import com.sensoro.smartcity.callback.BleObserver;
import com.sensoro.smartcity.push.AppBlockCanaryContext;
import com.sensoro.smartcity.push.SensoroPushListener;
import com.sensoro.smartcity.push.SensoroPushManager;
import com.sensoro.smartcity.util.NotificationUtils;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.smartcity.widget.imagepicker.view.CropImageView;
import com.sensoro.smartcity.widget.popup.GlideImageLoader;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.ui.UILifecycleListener;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by sensoro on 17/7/24.
 */

public class SensoroCityApplication extends BaseApplication implements SensoroPushListener, OnResultListener<AccessToken>, AMapLocationListener, Runnable {
    public IWXAPI api;
    private static volatile SensoroCityApplication instance;
    public static NotificationUtils mNotificationUtils;
    private static PushHandler pushHandler;
    private final Handler taskHandler = new Handler(Looper.getMainLooper());
    public volatile boolean hasGotToken = false;
    public static String VIDEO_PATH;
    public AMapLocationClient mLocationClient;
    public BLEDeviceManager bleDeviceManager;
    private final Runnable iBeaconTask = new Runnable() {
        private boolean noStateOut = true;
        private boolean noStateIn = true;
        private volatile boolean isNearby = false;
        private final Runnable inTask = new Runnable() {
            @Override
            public void run() {
                if (noStateIn) {
                    if (TextUtils.isEmpty(ibeaconSettingData.switchInMessage)) {
                        mNotificationUtils.sendNotification("进入！！");
                    } else {
                        mNotificationUtils.sendNotification(ibeaconSettingData.switchInMessage);
                    }
                }
                noStateIn = false;
                noStateOut = true;
            }
        };
        private final Runnable outTask = new Runnable() {
            @Override
            public void run() {
                if (noStateOut) {
                    if (TextUtils.isEmpty(ibeaconSettingData.switchOutMessage)) {
                        mNotificationUtils.sendNotification("出去！！！");
                    } else {
                        mNotificationUtils.sendNotification(ibeaconSettingData.switchOutMessage);
                    }
                }
                noStateOut = false;
                noStateIn = true;

            }
        };
        private volatile int outCount = 0;
        //

        @Override
        public void run() {
            if (PreferencesHelper.getInstance().getUserData().hasIBeaconSearchDemo && (ibeaconSettingData.switchOut || ibeaconSettingData.switchIn)) {
                boolean bleHasOpen = SensoroCityApplication.getInstance().bleDeviceManager.isBluetoothEnabled();
                if (bleHasOpen) {
                    try {
                        bleHasOpen = SensoroCityApplication.getInstance().bleDeviceManager.startService();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (bleHasOpen) {
                    boolean currentNearby = !mNearByIBeaconMap.isEmpty();
                    if (currentNearby) {
                        //当前在附近
                        if (isNearby) {
                            //上次记录在附近，本地记录也在附近
                            try {
                                LogUtils.loge("currentNearby---->> 上次记录在附近，本地记录也在附近 outCount = " + outCount + ",isNearby = " + isNearby + ",currentNearby = " + currentNearby);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        } else {
                            //上次记录不在附近，本次记录在附近
                            try {
                                LogUtils.loge("currentNearby---->> 上次记录不在附近，本次记录在附近 outCount = " + outCount + ",isNearby = " + isNearby + ",currentNearby = " + currentNearby);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                            if (ibeaconSettingData.switchIn) {
                                taskHandler.removeCallbacks(inTask);
                                taskHandler.removeCallbacks(outTask);
                                taskHandler.post(inTask);
                            }
                        }
                        outCount = 0;
                    } else {
                        //当前不在附近
                        if (isNearby) {
                            //上次记录在附近，本次记录不在附近
                            outCount = 1;
                            try {
                                LogUtils.loge("currentNearby---->> 上次记录在附近，本次记录不在附近 outCount = " + outCount + ",isNearby = " + isNearby + ",currentNearby = " + currentNearby);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        } else {
                            //上次记录不在附近本地记录也不在附近
                            int i = 20 - outCount * 3;
                            if (i > 0) {
                                outCount++;
                            } else {
                                if (ibeaconSettingData.switchOut) {
                                    taskHandler.removeCallbacks(outTask);
                                    taskHandler.removeCallbacks(inTask);
                                    taskHandler.post(outTask);
                                }
                                outCount = 0;
                            }
                            try {
                                LogUtils.loge("currentNearby---->> 上次记录不在附近，本次记录不在附近 outCount = " + outCount + ",isNearby = " + isNearby + ",currentNearby = " + currentNearby + ",i = " + i);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }

                    }
                    isNearby = currentNearby;
                }
            }
            taskHandler.postDelayed(iBeaconTask, 3000);
        }
    };

    private final BLEDeviceListener<BLEDevice> bleDeviceListener = new BLEDeviceListener<BLEDevice>() {
        @Override
        public void onNewDevice(BLEDevice bleDevice) {
            if (bleDevice != null) {
                String sn = bleDevice.getSn();
                if (!TextUtils.isEmpty(sn)) {
                    IBeacon iBeacon = bleDevice.iBeacon;
                    int iBeaconData = handleIBeaconData(iBeacon);
                    if (iBeaconData > 0) {
                        mNearByIBeaconMap.put(sn + iBeaconData, iBeacon);
                    }
                }
            }
        }

        @Override
        public void onGoneDevice(BLEDevice bleDevice) {
            if (bleDevice != null) {
                String sn = bleDevice.getSn();
                if (!TextUtils.isEmpty(sn)) {
                    IBeacon iBeacon = bleDevice.iBeacon;
                    int iBeaconData = handleIBeaconData(iBeacon);
                    if (iBeaconData > 0) {
                        mNearByIBeaconMap.remove(sn + iBeaconData);
                    }
                }

            }
        }

        @Override
        public void onUpdateDevices(ArrayList<BLEDevice> deviceList) {
            for (BLEDevice bleDevice : deviceList) {
                String sn = bleDevice.getSn();
                if (!TextUtils.isEmpty(sn)) {
                    IBeacon iBeacon = bleDevice.iBeacon;
                    int iBeaconData = handleIBeaconData(iBeacon);
                    if (iBeaconData > 0) {
                        mNearByIBeaconMap.put(sn + iBeaconData, iBeacon);
                    }
                }

            }
        }
    };

    private int handleIBeaconData(IBeacon iBeacon) {
        int state = 0;
        if (iBeacon != null) {
            String uuid = iBeacon.getUuid();
            int major = iBeacon.getMajor();
            int minor = iBeacon.getMinor();

            if (ibeaconSettingData.currentUUID != null) {
                if (ibeaconSettingData.currentUUID.equals(uuid)) {
                    state = 1;
                    if (ibeaconSettingData.currentMajor != null) {
                        if (ibeaconSettingData.currentMajor.equals(major)) {
                            state = 2;
                            if (ibeaconSettingData.currentMirror != null) {
                                if (ibeaconSettingData.currentMirror.equals(minor)) {
                                    state = 3;
                                }
                            }
                        }
                    }
                }
            }
        }
        return state;
    }

    private final LinkedHashMap<String, IBeacon> mNearByIBeaconMap = new LinkedHashMap<>();
    public IbeaconSettingData ibeaconSettingData = new IbeaconSettingData();

    //
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();

    }

//    private void customAdaptForExternal() {
//        /**
//         * {@link ExternalAdaptManager} 是一个管理外部三方库的适配信息和状态的管理类, 详细介绍请看 {@link ExternalAdaptManager} 的类注释
//         */
//        AutoSizeConfig.getInstance().getExternalAdaptManager()
//
//                //加入的 Activity 将会放弃屏幕适配, 一般用于三方库的 Activity, 详情请看方法注释
//                //如果不想放弃三方库页面的适配, 请用 addExternalAdaptInfoOfActivity 方法, 建议对三方库页面进行适配, 让自己的 App 更完美一点
////                .addCancelAdaptOfActivity(DefaultErrorActivity.class)
//
//                //为指定的 Activity 提供自定义适配参数, AndroidAutoSize 将会按照提供的适配参数进行适配, 详情请看方法注释
//                //一般用于三方库的 Activity, 因为三方库的设计图尺寸可能和项目自身的设计图尺寸不一致, 所以要想完美适配三方库的页面
//                //就需要提供三方库的设计图尺寸, 以及适配的方向 (以宽为基准还是高为基准?)
//                //三方库页面的设计图尺寸可能无法获知, 所以如果想让三方库的适配效果达到最好, 只有靠不断的尝试
//                //由于 AndroidAutoSize 可以让布局在所有设备上都等比例缩放, 所以只要你在一个设备上测试出了一个最完美的设计图尺寸
//                //那这个三方库页面在其他设备上也会呈现出同样的适配效果, 等比例缩放, 所以也就完成了三方库页面的屏幕适配
//                //即使在不改三方库源码的情况下也可以完美适配三方库的页面, 这就是 AndroidAutoSize 的优势
//                //但前提是三方库页面的布局使用的是 dp 和 sp, 如果布局全部使用的 px, 那 AndroidAutoSize 也将无能为力
//                //经过测试 DefaultErrorActivity 的设计图宽度在 380dp - 400dp 显示效果都是比较舒服的
//                .addExternalAdaptInfoOfActivity(DefaultErrorActivity.class, new ExternalAdaptInfo(true, 400));
//    }

    public static SensoroCityApplication getInstance() {
        return instance;
    }

    private void initSensoroSDK() {
        try {
            bleDeviceManager = BLEDeviceManager.getInstance(this);
            bleDeviceManager.setForegroundScanPeriod(7 * 1000);
            bleDeviceManager.setForegroundBetweenScanPeriod(2 * 1000);
            bleDeviceManager.setOutOfRangeDelay(10 * 1000);
            bleDeviceManager.setBackgroundMode(false);
            bleDeviceManager.setBLEDeviceListener(BleObserver.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void locate() {
        mLocationClient = new AMapLocationClient(this);
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        mLocationOption.setHttpTimeOut(20000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        taskHandler.removeCallbacksAndMessages(null);
        if (BleObserver.getInstance().isRegisterBleObserver(bleDeviceListener)) {
            BleObserver.getInstance().unregisterBleObserver(bleDeviceListener);
        }
        mLocationClient.onDestroy();
        mNearByIBeaconMap.clear();
        Beta.unInit();
    }

    public void init() {
        if (pushHandler == null) {
            pushHandler = new PushHandler();
        }
        if (mNotificationUtils == null) {
            mNotificationUtils = new NotificationUtils(this);
        }
        if (AppUtils.isAppMainProcess(this, "com.sensoro.smartcity")) {
            VIDEO_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/camera/";
            //Mapbox必须在主线程初始化
            taskHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Mapbox.getInstance(instance.getApplicationContext(), instance.getString(R.string.mapbox_access_token));
                    //只收集release版本的日志信息，升级也只针对release版本
                    if (!BuildConfig.DEBUG) {
                        initBugLy();
                    }
                }
            }, 1000);
            initSensoroSDK();
            ThreadPoolManager.getInstance().execute(this);
        }

    }

    private void initBugLy() {
        synchronized (SensoroCityApplication.class) {
            try {
//  在这里设置strategy的属性，在bugly初始化时传入
                final CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
                strategy.setAppChannel("master");  //设置渠道
                strategy.setAppVersion(BuildConfig.VERSION_NAME);      //App的版本
                strategy.setAppPackageName("com.sensoro.smartcity");  //App的包名
                //
                /**
                 * true表示app启动自动初始化升级模块;
                 * false不会自动初始化;
                 * 开发者如果担心sdk初始化影响app启动速度，可以设置为false，
                 * 在后面某个时刻手动调用Beta.init(getApplicationContext(),false);
                 */
                Beta.autoInit = false;

                /**
                 * true表示初始化时自动检查升级;
                 * false表示不会自动检查升级,需要手动调用Beta.checkUpgrade()方法;
                 */
                Beta.autoCheckUpgrade = true;

                /**
                 * 设置升级检查周期为60s(默认检查周期为0s)，60s内SDK不重复向后台请求策略);
                 */
                Beta.upgradeCheckPeriod = 60 * 20 * 1000;

                /**
                 * 设置启动延时为1s（默认延时3s），APP启动1s后初始化SDK，避免影响APP启动速度;
                 */
                Beta.initDelay = 6 * 1000;

                /**
                 * 设置通知栏大图标，largeIconId为项目中的图片资源;
                 */
//        Beta.largeIconId = R.drawable.ic_launcher;

                /**
                 * 设置状态栏小图标，smallIconId为项目中的图片资源Id;
                 */
//        Beta.smallIconId = R.drawable.ic_launcher;

                /**
                 * 设置更新弹窗默认展示的banner，defaultBannerId为项目中的图片资源Id;
                 * 当后台配置的banner拉取失败时显示此banner，默认不设置则展示“loading“;
                 */
//        Beta.defaultBannerId = R.drawable.ic_launcher;

                /**
                 * 设置sd卡的Download为更新资源保存目录;
                 * 后续更新资源会保存在此目录，需要在manifest中添加WRITE_EXTERNAL_STORAGE权限;
                 */
                Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                /**
                 * 点击过确认的弹窗在APP下次启动自动检查更新时会再次显示;
                 */
                Beta.showInterruptedStrategy = true;
                //
                Beta.upgradeDialogLayoutId = R.layout.layout_upgrade_dialog;
                /**
                 * 只允许在MainActivity上显示更新弹窗，其他activity上不显示弹窗;
                 * 不设置会默认所有activity都可以显示弹窗;
                 */
                Beta.canNotShowUpgradeActs.add(SplashActivity.class);
                //设置是否显示消息通知
                Beta.enableNotification = true;
                //设置Wifi下自动下载
                Beta.autoDownloadOnWifi = false;
                //设置是否显示弹窗中的apk信息
                Beta.canShowApkInfo = true;
                //关闭热更新
                Beta.enableHotfix = false;

                //
//                Beta.upgradeListener = new UpgradeListener() {
//                    @Override
//                    public void onUpgrade(int i, UpgradeInfo upgradeInfo, boolean isManual, boolean isSilence) {
//                        try {
//                            LogUtils.loge("Beta.upgrade onUpgrade i = " + i + ",upgradeInfo = " + upgradeInfo + ", isManual = " + isManual + ",isSilence = " + isSilence);
//                        } catch (Throwable throwable) {
//                            throwable.printStackTrace();
//                        }
//                    }
//                };
                //
//                Beta.upgradeStateListener = new UpgradeStateListener() {
//                    @Override
//                    public void onUpgradeFailed(boolean isManual) {
//                        try {
//                            LogUtils.loge("Beta.upgrade onUpgradeFailed isManual = " + isManual);
//                        } catch (Throwable throwable) {
//                            throwable.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onUpgradeSuccess(boolean isManual) {
//                        try {
//                            LogUtils.loge("Beta.upgrade onUpgradeSuccess isManual = " + isManual);
//                        } catch (Throwable throwable) {
//                            throwable.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onUpgradeNoVersion(boolean isManual) {
//                        try {
//                            LogUtils.loge("Beta.upgrade onUpgradeNoVersion isManual = " + isManual);
//                        } catch (Throwable throwable) {
//                            throwable.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onUpgrading(boolean isManual) {
//                        try {
//                            LogUtils.loge("Beta.upgrade onUpgrading isManual = " + isManual);
//                        } catch (Throwable throwable) {
//                            throwable.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onDownloadCompleted(boolean isManual) {
//                        try {
//                            LogUtils.loge("Beta.upgrade onDownloadCompleted isManual = " + isManual);
//                        } catch (Throwable throwable) {
//                            throwable.printStackTrace();
//                        }
//                    }
//                };
                //
                Beta.upgradeDialogLifecycleListener = new UILifecycleListener<UpgradeInfo>() {
                    @Override
                    public void onCreate(Context context, View view, UpgradeInfo upgradeInfo) {
                        try {
                            LogUtils.loge("Beta.upgrade onCreate");
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                        // 注：可通过这个回调方式获取布局的控件，如果设置了id，可通过findViewById方式获取，如果设置了tag，可以通过findViewWithTag，具体参考下面例子:

                        // 通过id方式获取控件，并更改imageview图片
                        TextView textView = (TextView) view.findViewById(R.id.tv_upgrade_info_url);
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    AppUtils.openNetPage(ActivityTaskManager.getInstance().getTopActivity(), "https://fir.im/g7jk");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
//                        imageView.setImageResource(R.mipmap.ic_launcher);
//
//                        // 通过tag方式获取控件，并更改布局内容
//                        TextView textView = (TextView) view.findViewWithTag("textview");
//                        textView.setText("my custom text");
//
//                        // 更多的操作：比如设置控件的点击事件
//                        imageView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(getApplicationContext(), OtherActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//                            }
//                        });
                    }

                    @Override
                    public void onStart(Context context, View view, UpgradeInfo upgradeInfo) {
                        try {
                            LogUtils.loge("Beta.upgrade onStart");
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }

                    @Override
                    public void onResume(Context context, View view, UpgradeInfo upgradeInfo) {
                        try {
                            LogUtils.loge("Beta.upgrade onResume");
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }

                    @Override
                    public void onPause(Context context, View view, UpgradeInfo upgradeInfo) {
                        try {
                            LogUtils.loge("Beta.upgrade onPause");
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }

                    @Override
                    public void onStop(Context context, View view, UpgradeInfo upgradeInfo) {
                        try {
                            LogUtils.loge("Beta.upgrade onStop");
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }

                    @Override
                    public void onDestroy(Context context, View view, UpgradeInfo upgradeInfo) {
                        try {
                            LogUtils.loge("Beta.upgrade onDestory");
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                };
                strategy.setCrashHandleCallback(new CrashHandler());
                // 统一初始化Bugly产品，包含Beta
                Bugly.setIsDevelopmentDevice(getApplicationContext(), BuildConfig.DEBUG);
                Bugly.init(getApplicationContext(), "ab6c4abe4f", BuildConfig.DEBUG, strategy);
                Beta.init(getApplicationContext(), BuildConfig.DEBUG);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private final class CrashHandler extends CrashReport.CrashHandleCallback {
        @Override
        public synchronized byte[] onCrashHandleStart2GetExtraDatas(int crashType, String errorType, String errorMessage, String errorStack) {
            EventData eventData = new EventData();
            eventData.code = Constants.EVENT_DATA_APP_CRASH;
            //
            EventBus.getDefault().post(eventData);
            try {
                LogUtils.loge("app crash ------>>>> crashType = " + crashType + "，errorType = " + errorType + "，errorMessage = " + errorMessage + "，errorStack = " + errorStack);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return super.onCrashHandleStart2GetExtraDatas(crashType, errorType, errorMessage, errorStack);
        }

    }

    private void initORC() {
        OCR.getInstance(getApplicationContext()).initAccessTokenWithAkSk(this, getApplicationContext(),
                "D1T3OGkU9CzoVaEBnQ8ie2xG", "YD1WmK9CG2TVUDwt2MuT2XswNkimCEf7");
    }


    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(false);
        //显示拍照按钮
        //TODO 去掉裁剪
        imagePicker.setCrop(false);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(9);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    @Override
    protected void onMyApplicationResumed() {
        if (mLocationClient != null) {
            mLocationClient.startLocation();
        }
    }

    @Override
    protected void onMyApplicationPaused() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
    }


    public static void sendMessage(Message msg) {
        pushHandler.sendMessage(msg);
    }

    @Override
    public void onPushCallBack(String message) {
        boolean isAPPBack = Repause.isApplicationPaused();
        try {
            LogUtils.loge("hcs", "pushNotification---isAPPBack = " + isAPPBack);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        if (isAPPBack) {
            mNotificationUtils.sendNotification(message);
        }
    }

    @Override
    public void onResult(AccessToken result) {
        // 调用成功，返回AccessToken对象
        String token = result.getAccessToken();
        hasGotToken = true;
        try {
            LogUtils.loge(this, "初始化QCR成功 ： token = " + token);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void onError(OCRError error) {
        // 调用失败，返回OCRError子类SDKError对象
        hasGotToken = false;
        String message = error.getMessage();
        try {
            LogUtils.loge(this, message);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //可在其中解析amapLocation获取相应内容。
                double lat = aMapLocation.getLatitude();//获取纬度
                double lon = aMapLocation.getLongitude();//获取经度
//            mStartPosition = new LatLng(lat, lon);
                try {
                    LogUtils.loge(this, "定位信息------->lat = " + lat + ",lon = =" + lon);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("地图错误", "定位失败, 错误码:" + aMapLocation.getErrorCode() + ", 错误信息:"
                        + aMapLocation.getErrorInfo());
            }

        }
    }

    @Override
    public void run() {
        initORC();
        SensoroPushManager.getInstance().registerSensoroPushListener(this);
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        api.registerApp(Constants.APP_ID);
//        FMMapSDK.init(this);
        initImagePicker();
        locate();
        //IBeacon相关
        SensoroCityApplication.getInstance().ibeaconSettingData.currentUUID = "70DC44C3-E2A8-4B22-A2C6-129B41A4BDBC";
        IbeaconSettingData ibeaconSettingData = PreferencesHelper.getInstance().getIbeaconSettingData();
        if (ibeaconSettingData != null) {
            SensoroCityApplication.getInstance().ibeaconSettingData = ibeaconSettingData;
        }
        if (!BleObserver.getInstance().isRegisterBleObserver(bleDeviceListener)) {
            BleObserver.getInstance().registerBleObserver(bleDeviceListener);
        }
        taskHandler.postDelayed(iBeaconTask, 500);
        //
        BlockCanary.install(this, new AppBlockCanaryContext()).start();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not initView your app in this process.
            return;
        }
        LeakCanary.install(this);

    }

    /**
     * 用handler
     */
    public static class PushHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (msg.obj instanceof String) {
                String message = (String) msg.obj;
                SensoroPushListener sensoroPushListener = SensoroPushManager.getInstance().getSensoroPushListener();
                if (sensoroPushListener != null) {
                    sensoroPushListener.onPushCallBack(message);
                }
            }
        }
    }
}
