package com.sensoro.common.base;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;

import com.alibaba.android.arouter.launcher.ARouter;
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
import com.sensoro.common.R;
import com.sensoro.common.manger.ThreadPoolManager;
import com.sensoro.common.utils.DynamicTimeFormat;
import com.sensoro.common.utils.LogUtils;
import com.sensoro.common.utils.Repause;

import java.util.Locale;

import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.onAdaptListener;

/**
 * 要想使用BaseApplication，必须在组件中实现自己的Application，并且继承BaseApplication；
 * 组件中实现的Application必须在debug包中的AndroidManifest.xml中注册，否则无法使用；
 * 组件的Application需置于java/debug文件夹中，不得放于主代码；
 * 组件中获取Context的方法必须为:ContextUtils.getContext()，不允许其他写法；
 *
 * @name BaseApplication
 */
public abstract class BaseApplication extends MultiDexApplication implements Repause.Listener {

    public static final String ROOT_PACKAGE = "com.sensoro.common";

    private static BaseApplication sInstance;
    //暂时去掉
//    private List<IApplicationDelegate> mAppDelegateList;

    public UploadManager uploadManager;

    public static BaseApplication getInstance() {
        return sInstance;
    }

    private final Runnable initTask = new Runnable() {
        @Override
        public void run() {
            Repause.init(BaseApplication.this);
            Repause.registerListener(BaseApplication.this);
            initAutoSize();
            initUploadManager();
            initSmartRefresh();
        }
    };

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

                String format = BaseApplication.this.getResources().getString(R.string.update_from) + " %s";
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
        sInstance = this;
        ContextUtils.init(this);
//        mAppDelegateList = ClassUtils.getObjectsWithInterface(this, IApplicationDelegate.class, ROOT_PACKAGE);
//        for (IApplicationDelegate delegate : mAppDelegateList) {
//            delegate.onCreate();
//        }
        ThreadPoolManager.getInstance().execute(initTask);
        if (ContextUtils.isAppDebug()) {
            //开启InstantRun之后，一定要在ARouter.init之前调用openDebug
            ARouter.openDebug();
            ARouter.openLog();
        }
        ARouter.init(BaseApplication.this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
//        for (IApplicationDelegate delegate : mAppDelegateList) {
//            delegate.onTerminate();
//        }
        Repause.unregisterListener(this);
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
//        for (IApplicationDelegate delegate : mAppDelegateList) {
//            delegate.onLowMemory();
//        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
//        for (IApplicationDelegate delegate : mAppDelegateList) {
//            delegate.onTrimMemory(level);
//        }
    }

    private void initUploadManager() {
        Configuration config = new Configuration.Builder()
                .chunkSize(512 * 1024)        // 分片上传时，每片的大小。 默认256K
                .putThreshhold(1024 * 1024)   // 启用分片上传阀值。默认512K
                .connectTimeout(10)           // 链接超时。默认10秒
                .useHttps(true)               // 是否使用https上传域名
                .responseTimeout(60)// 服务器响应超时。默认60秒
                .recorder(null)           // recorder分片上传时，已上传片记录器。默认null
//                .recorder(new re, keyGen)   // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .zone(FixedZone.zone0)// 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                .build();
// 重用uploadManager。一般地，只需要创建一个uploadManager对象
        uploadManager = new UploadManager(config);
    }

    private void initAutoSize() {
        /**
         * 给外部的三方库 {@link Activity} 自定义适配参数, 因为三方库的 {@link Activity} 并不能通过实现
         * {@link CustomAdapt} 接口的方式来提供自定义适配参数 (因为远程依赖改不了源码)
         * 所以使用 {@link ExternalAdaptManager} 来替代实现接口的方式, 来提供自定义适配参数
         */
        /**
         * {@link ExternalAdaptManager} 是一个管理外部三方库的适配信息和状态的管理类, 详细介绍请看 {@link ExternalAdaptManager} 的类注释
         */
        AutoSizeConfig autoSizeConfig = AutoSizeConfig.getInstance()
                //是否让框架支持自定义 Fragment 的适配参数, 由于这个需求是比较少见的, 所以须要使用者手动开启
                //如果没有这个需求建议不开启
//                .setCustomFragment(true)
                //是否屏蔽系统字体大小对 AndroidAutoSize 的影响, 如果为 true, App 内的字体的大小将不会跟随系统设置中字体大小的改变
                //如果为 false, 则会跟随系统设置中字体大小的改变, 默认为 false
                .setExcludeFontScale(true)
                .setDesignWidthInDp(375)
                .setDesignHeightInDp(667)
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
                .setLog(false);//是否使用设备的实际尺寸做适配, 默认为 false, 如果设置为 false, 在以屏幕高度为基准进行适配时
//AutoSize 会将屏幕总高度减去状态栏高度来做适配
//设置为 true 则使用设备的实际屏幕高度, 不会减去状态栏高度
//                .setUseDeviceSize(true)
//是否全局按照宽度进行等比例适配, 默认为 true, 如果设置为 false, AutoSize 会全局按照高度进行适配
//                .setBaseOnWidth(false)
//设置屏幕适配逻辑策略类, 一般不用设置, 使用框架默认的就好
//                .setAutoAdaptStrategy(new AutoAdaptStrategy())
        //TODO 适配第三方类的activity
//        autoSizeConfig.getExternalAdaptManager()
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
//                .addExternalAdaptInfoOfActivity(CameraActivity.class, new ExternalAdaptInfo(true, 375));
    }

    @Override
    public void onApplicationResumed() {
        onMyApplicationResumed();
    }

    @Override
    public void onApplicationPaused() {
        onMyApplicationPaused();
    }

    protected abstract void onMyApplicationResumed();

    protected abstract void onMyApplicationPaused();
}
