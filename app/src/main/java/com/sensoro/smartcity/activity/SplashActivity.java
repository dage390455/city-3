package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;

import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.ISplashActivityView;
import com.sensoro.smartcity.presenter.SplashActivityPresenter;

public class SplashActivity extends BaseActivity<ISplashActivityView, SplashActivityPresenter> implements ISplashActivityView {

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        checkActivity();
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
}
