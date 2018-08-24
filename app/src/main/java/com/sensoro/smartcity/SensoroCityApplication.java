package com.sensoro.smartcity;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.fengmap.android.FMMapSDK;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.qiniu.android.common.FixedZone;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UploadManager;
import com.sensoro.smartcity.activity.LoginActivity;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.push.SensoroPushListener;
import com.sensoro.smartcity.push.SensoroPushManager;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.NotificationUtils;
import com.sensoro.smartcity.util.Repause;
import com.sensoro.smartcity.widget.SensoroToast;
import com.sensoro.smartcity.widget.popup.GlideImageLoader;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sensoro on 17/7/24.
 */

public class SensoroCityApplication extends MultiDexApplication implements Thread.UncaughtExceptionHandler, Repause
        .Listener, SensoroPushListener, OnResultListener<AccessToken> {

    private final List<DeviceInfo> mDeviceInfoList = Collections.synchronizedList(new ArrayList<DeviceInfo>());
    public IWXAPI api;
    private static volatile SensoroCityApplication instance;
    public int saveSearchType = Constants.TYPE_DEVICE_NAME;
    private NotificationUtils mNotificationUtils;
    private static boolean isAPPBack = true;
    private static PushHandler pushHandler;
    public UploadManager uploadManager;
    public volatile boolean hasGotToken = false;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();
        Thread.setDefaultUncaughtExceptionHandler(this);
//        customAdaptForExternal();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not initView your app in this process.
            return;
        }
        LeakCanary.install(this);
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

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(base);
//    }

    public void addData(List<DeviceInfo> list) {
        this.mDeviceInfoList.addAll(list);
    }

    public void setData(List<DeviceInfo> list) {
        this.mDeviceInfoList.clear();
        this.mDeviceInfoList.addAll(list);

    }

    /**
     * 在登录界面不推送
     *
     * @return
     */
    private boolean isNeedPush() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return !name.equals(LoginActivity.class.getName());
    }

    public List<DeviceInfo> getData() {
        return mDeviceInfoList;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mDeviceInfoList.clear();
        Repause.unregisterListener(this);
    }

    private void init() {
        if (pushHandler == null) {
            pushHandler = new PushHandler();
        }
        initORC();
        SensoroPushManager.getInstance().registerSensoroPushListener(this);
        Repause.init(this);
        Repause.registerListener(this);
        mNotificationUtils = new NotificationUtils(this);
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        api.registerApp(Constants.APP_ID);
        FMMapSDK.init(this);
        //
        initImagePicker();
        initUploadManager();
    }

    private void initORC() {
        OCR.getInstance(getApplicationContext()).initAccessTokenWithAkSk(this, getApplicationContext(),
                "D1T3OGkU9CzoVaEBnQ8ie2xG", "YD1WmK9CG2TVUDwt2MuT2XswNkimCEf7");
    }

    private void initUploadManager() {
        Configuration config = new Configuration.Builder()
                .chunkSize(200 * 1024)        // 分片上传时，每片的大小。 默认256K
//                .putThreshhold(1024 * 1024)   // 启用分片上传阀值。默认512K
                .connectTimeout(10)           // 链接超时。默认10秒
                .useHttps(true)               // 是否使用https上传域名
                .responseTimeout(60)          // 服务器响应超时。默认60秒
//                .recorder(null)           // recorder分片上传时，已上传片记录器。默认null
//                .recorder(new re, keyGen)   // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .zone(FixedZone.zone0)        // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                .build();
// 重用uploadManager。一般地，只需要创建一个uploadManager对象
        uploadManager = new UploadManager(config);
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(false);                      //显示拍照按钮
        imagePicker.setCrop(true);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(9);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        SensoroToast.makeText(SensoroCityApplication.this, "程序出错：" + t.getId() + "," + e.getMessage(),
                Toast
                        .LENGTH_SHORT).show();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onApplicationResumed() {
        isAPPBack = false;
    }

    @Override
    public void onApplicationPaused() {
        isAPPBack = true;
    }

    public static void sendMessage(Message msg) {
        pushHandler.sendMessage(msg);
    }

    @Override
    public void onPushCallBack(String message) {
        LogUtils.loge(this, "pushNotification---isAPPBack = " + isAPPBack);
        if (isAPPBack) {
            mNotificationUtils.sendNotification(message);
        }
    }

    @Override
    public void onResult(AccessToken result) {
        // 调用成功，返回AccessToken对象
        String token = result.getAccessToken();
        hasGotToken = true;
        LogUtils.loge(this, "初始化QCR成功 ： token = " + token);
    }

    @Override
    public void onError(OCRError error) {
        // 调用失败，返回OCRError子类SDKError对象
        hasGotToken = false;
        String message = error.getMessage();
        LogUtils.loge(this, message);
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
