package com.sensoro.smartcity.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.gyf.barlibrary.ImmersionBar;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.ISplashActivityView;
import com.sensoro.smartcity.presenter.SplashActivityPresenter;

public class SplashActivity extends BaseActivity<ISplashActivityView, SplashActivityPresenter> implements ISplashActivityView {

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
//        startAC(new Intent(mActivity,DeployMonitorDetailActivity.class));
        checkActivity();
//        setWindowStatusBarColor(this, R.color.white);
//        cancelFullScreen(this);
        setContentView(R.layout.activity_splash);
        mPresenter.initData(mActivity);
    }

    @Override
    protected SplashActivityPresenter createPresenter() {
        return new SplashActivityPresenter();
    }

    //避免activity多次启动
    private void checkActivity() {
        if (!this.isTaskRoot()) {
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finishAc();
                    return;
                }
            }
        }
    }

    public static void cancelFullScreen(Activity activity) {
        activity.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void setWindowStatusBarColor(Activity activity, int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(activity.getResources().getColor(colorResId));

                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startAC(Intent intent) {
        mActivity.startActivity(intent);
    }

    @Override
    public void finishAc() {
        mActivity.finish();
    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {

    }

    @Override
    public void setIntentResult(int resultCode) {

    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {

    }

    @Override
    public boolean isActivityOverrideStatusBar() {
        immersionBar = ImmersionBar.with(mActivity);
        immersionBar
                .transparentStatusBar()
                .statusBarDarkFont(false)
                .init();
        return true;
    }
}
