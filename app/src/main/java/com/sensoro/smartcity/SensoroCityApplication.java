package com.sensoro.smartcity;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.github.moduth.blockcanary.BlockCanary;
import com.mapbox.mapboxsdk.Mapbox;
import com.qiniu.android.common.FixedZone;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UploadManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshInitializer;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceManager;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.push.AppBlockCanaryContext;
import com.sensoro.smartcity.push.SensoroPushListener;
import com.sensoro.smartcity.push.SensoroPushManager;
import com.sensoro.smartcity.push.ThreadPoolManager;
import com.sensoro.smartcity.util.BleObserver;
import com.sensoro.smartcity.util.DynamicTimeFormat;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.NotificationUtils;
import com.sensoro.smartcity.util.Repause;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.smartcity.widget.imagepicker.view.CropImageView;
import com.sensoro.smartcity.widget.popup.GlideImageLoader;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yixia.camera.VCamera;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Locale;

import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.external.ExternalAdaptInfo;
import me.jessyan.autosize.external.ExternalAdaptManager;
import me.jessyan.autosize.internal.CustomAdapt;
import me.jessyan.autosize.onAdaptListener;

/**
 * Created by sensoro on 17/7/24.
 */

public class SensoroCityApplication extends MultiDexApplication implements Repause
        .Listener, SensoroPushListener, OnResultListener<AccessToken>, AMapLocationListener, Runnable {
    public IWXAPI api;
    private static volatile SensoroCityApplication instance;
    private NotificationUtils mNotificationUtils;
    public boolean isAPPBack = true;
    private static PushHandler pushHandler;
    public UploadManager uploadManager;
    public volatile boolean hasGotToken = false;
    public static String VIDEO_PATH;
    public AMapLocationClient mLocationClient;
    public BLEDeviceManager bleDeviceManager;

//    static {
//        initSmartRefresh();
//    }

    private void initSmartRefresh() {
        //启用矢量图兼容
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        //设置全局默认配置（优先级最低，会被其他设置覆盖）
        SmartRefreshLayout.setDefaultRefreshInitializer(new DefaultRefreshInitializer() {
            @Override
            public void initialize(@NonNull Context context, @NonNull RefreshLayout layout) {
                //全局设置（优先级最低）
//                layout.setEnableLoadMore(false);
                layout.setEnableAutoLoadMore(true);
                layout.setEnableOverScrollDrag(false);
                layout.setEnableOverScrollBounce(true);
                layout.setEnableLoadMoreWhenContentNotFull(true);
                layout.setEnableFooterFollowWhenLoadFinished(true);
                layout.setEnableScrollContentWhenRefreshed(true);
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                //全局设置主题颜色（优先级第二低，可以覆盖 DefaultRefreshInitializer 的配置，与下面的ClassicsHeader绑定）
                layout.setPrimaryColorsId(android.R.color.white);

                String format = SensoroCityApplication.this.getResources().getString(R.string.update_from) + " %s";
                return new ClassicsHeader(context).setTimeFormat(new DynamicTimeFormat(format));
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

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
        Repause.unregisterListener(this);
        mLocationClient.onDestroy();
        Beta.unInit();
    }

    public void init() {
        initAutoSize();
        initSensoroSDK();
        VIDEO_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/camera/";
        if (pushHandler == null) {
            pushHandler = new PushHandler();
        }
        pushHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Mapbox.getInstance(instance.getApplicationContext(), instance.getString(R.string.mapbox_access_token));
            }
        }, 1000);
        ThreadPoolManager.getInstance().execute(this);
    }

    private void initAutoSize() {
        AutoSizeConfig.getInstance()
                //是否让框架支持自定义 Fragment 的适配参数, 由于这个需求是比较少见的, 所以须要使用者手动开启
                //如果没有这个需求建议不开启
//                .setCustomFragment(true)
                //是否屏蔽系统字体大小对 AndroidAutoSize 的影响, 如果为 true, App 内的字体的大小将不会跟随系统设置中字体大小的改变
                //如果为 false, 则会跟随系统设置中字体大小的改变, 默认为 false
                .setExcludeFontScale(true)
                //屏幕适配监听器
                .setOnAdaptListener(new onAdaptListener() {
                    @Override
                    public void onAdaptBefore(Object target, Activity activity) {
                        //使用以下代码, 可支持 Android 的分屏或缩放模式, 但前提是在分屏或缩放模式下当用户改变您 App 的窗口大小时
                        //系统会重绘当前的页面, 经测试在某些机型, 某些情况下系统不会重绘当前页面, ScreenUtils.getScreenSize(activity) 的参数一定要不要传 Application!!!
//                        AutoSizeConfig.getInstance().setScreenWidth(ScreenUtils.getScreenSize(activity)[0]);
//                        AutoSizeConfig.getInstance().setScreenHeight(ScreenUtils.getScreenSize(activity)[1]);
                        try {
                            LogUtils.logd(String.format(Locale.ENGLISH, "%s onAdaptBefore!", target.getClass().getName()));
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }

                    @Override
                    public void onAdaptAfter(Object target, Activity activity) {
                        try {
                            LogUtils.logd(String.format(Locale.ENGLISH, "%s onAdaptAfter!", target.getClass().getName()));
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                })

        //是否打印 AutoSize 的内部日志, 默认为 true, 如果您不想 AutoSize 打印日志, 则请设置为 false
                .setLog(false)
        //是否使用设备的实际尺寸做适配, 默认为 false, 如果设置为 false, 在以屏幕高度为基准进行适配时
        //AutoSize 会将屏幕总高度减去状态栏高度来做适配
        //设置为 true 则使用设备的实际屏幕高度, 不会减去状态栏高度
//                .setUseDeviceSize(true)

        //是否全局按照宽度进行等比例适配, 默认为 true, 如果设置为 false, AutoSize 会全局按照高度进行适配
//                .setBaseOnWidth(false)

        //设置屏幕适配逻辑策略类, 一般不用设置, 使用框架默认的就好
//                .setAutoAdaptStrategy(new AutoAdaptStrategy())
        ;
        customAdaptForExternal();
    }
    /**
     * 给外部的三方库 {@link Activity} 自定义适配参数, 因为三方库的 {@link Activity} 并不能通过实现
     * {@link CustomAdapt} 接口的方式来提供自定义适配参数 (因为远程依赖改不了源码)
     * 所以使用 {@link ExternalAdaptManager} 来替代实现接口的方式, 来提供自定义适配参数
     */
    private void customAdaptForExternal() {
        /**
         * {@link ExternalAdaptManager} 是一个管理外部三方库的适配信息和状态的管理类, 详细介绍请看 {@link ExternalAdaptManager} 的类注释
         */
        AutoSizeConfig.getInstance().getExternalAdaptManager()

                //加入的 Activity 将会放弃屏幕适配, 一般用于三方库的 Activity, 详情请看方法注释
                //如果不想放弃三方库页面的适配, 请用 addExternalAdaptInfoOfActivity 方法, 建议对三方库页面进行适配, 让自己的 App 更完美一点
//                .addCancelAdaptOfActivity(DefaultErrorActivity.class)

                //为指定的 Activity 提供自定义适配参数, AndroidAutoSize 将会按照提供的适配参数进行适配, 详情请看方法注释
                //一般用于三方库的 Activity, 因为三方库的设计图尺寸可能和项目自身的设计图尺寸不一致, 所以要想完美适配三方库的页面
                //就需要提供三方库的设计图尺寸, 以及适配的方向 (以宽为基准还是高为基准?)
                //三方库页面的设计图尺寸可能无法获知, 所以如果想让三方库的适配效果达到最好, 只有靠不断的尝试
                //由于 AndroidAutoSize 可以让布局在所有设备上都等比例缩放, 所以只要你在一个设备上测试出了一个最完美的设计图尺寸
                //那这个三方库页面在其他设备上也会呈现出同样的适配效果, 等比例缩放, 所以也就完成了三方库页面的屏幕适配
                //即使在不改三方库源码的情况下也可以完美适配三方库的页面, 这就是 AndroidAutoSize 的优势
                //但前提是三方库页面的布局使用的是 dp 和 sp, 如果布局全部使用的 px, 那 AndroidAutoSize 也将无能为力
                //经过测试 DefaultErrorActivity 的设计图宽度在 380dp - 400dp 显示效果都是比较舒服的
                .addExternalAdaptInfoOfActivity(CameraActivity.class, new ExternalAdaptInfo(true, 375));
    }
    private void initVc() {
        VIDEO_PATH += "SensoroCity";
        File file = new File(VIDEO_PATH);
        if (!file.exists()) file.mkdirs();

        //设置视频缓存路径
        VCamera.setVideoCachePath(VIDEO_PATH);

        // 开启log输出,ffmpeg输出到logcat
        VCamera.setDebugMode(false);
        // 初始化拍摄SDK，必须
        VCamera.initialize(this);
    }

    private void initBugLy() {
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
            Beta.autoInit = true;

            /**
             * true表示初始化时自动检查升级;
             * false表示不会自动检查升级,需要手动调用Beta.checkUpgrade()方法;
             */
            Beta.autoCheckUpgrade = true;

            /**
             * 设置升级检查周期为60s(默认检查周期为0s)，60s内SDK不重复向后台请求策略);
             */
            Beta.upgradeCheckPeriod = 60 * 1000;

            /**
             * 设置启动延时为1s（默认延时3s），APP启动1s后初始化SDK，避免影响APP启动速度;
             */
            Beta.initDelay = 2 * 1000;

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
//            Beta.canShowUpgradeActs.add(MainActivity.class);
//            Beta.canShowUpgradeActs.add(LoginActivity.class);
            //设置是否显示消息通知
            Beta.enableNotification = true;
            //设置Wifi下自动下载
            Beta.autoDownloadOnWifi = false;
            //设置是否显示弹窗中的apk信息
            Beta.canShowApkInfo = true;
            //关闭热更新
            Beta.enableHotfix = false;
            strategy.setCrashHandleCallback(new CrashHandler());
            // 统一初始化Bugly产品，包含Beta
            Bugly.setIsDevelopmentDevice(getApplicationContext(), BuildConfig.DEBUG);
            Bugly.init(getApplicationContext(), "ab6c4abe4f", BuildConfig.DEBUG, strategy);
            Beta.init(this.getApplicationContext(), BuildConfig.DEBUG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final class CrashHandler extends CrashReport.CrashHandleCallback {
        @Override
        public synchronized byte[] onCrashHandleStart2GetExtraDatas(int crashType, String errorType, String errorMessage, String errorStack) {
            EventData eventData = new EventData();
            eventData.code = Constants.EVENT_DATA_SESSION_ID_OVERTIME;
            EventBus.getDefault().post(eventData);
            return super.onCrashHandleStart2GetExtraDatas(crashType, errorType, errorMessage, errorStack);
        }

    }

    private void initORC() {
        OCR.getInstance(getApplicationContext()).initAccessTokenWithAkSk(this, getApplicationContext(),
                "D1T3OGkU9CzoVaEBnQ8ie2xG", "YD1WmK9CG2TVUDwt2MuT2XswNkimCEf7");
    }

    private void initUploadManager() {
        Configuration config = new Configuration.Builder()
                .chunkSize(512 * 1024)        // 分片上传时，每片的大小。 默认256K
                .putThreshhold(1024 * 1024)   // 启用分片上传阀值。默认512K
                .connectTimeout(10)           // 链接超时。默认10秒
                .useHttps(true)               // 是否使用https上传域名
                .responseTimeout(60)          // 服务器响应超时。默认60秒
                .recorder(null)           // recorder分片上传时，已上传片记录器。默认null
//                .recorder(new re, keyGen)   // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .zone(FixedZone.zone0)// 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                .build();
// 重用uploadManager。一般地，只需要创建一个uploadManager对象
        uploadManager = new UploadManager(config);
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
    public void onApplicationResumed() {
        isAPPBack = false;
        if (mLocationClient != null) {
            mLocationClient.startLocation();
        }
    }

    @Override
    public void onApplicationPaused() {
        isAPPBack = true;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
    }


    public static void sendMessage(Message msg) {
        pushHandler.sendMessage(msg);
    }

    @Override
    public void onPushCallBack(String message) {
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
        Repause.init(this);
        Repause.registerListener(this);
        mNotificationUtils = new NotificationUtils(this);
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        api.registerApp(Constants.APP_ID);
//        FMMapSDK.init(this);
        initVc();
        initImagePicker();
        initUploadManager();
        locate();
        initSmartRefresh();
        initBugLy();
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
