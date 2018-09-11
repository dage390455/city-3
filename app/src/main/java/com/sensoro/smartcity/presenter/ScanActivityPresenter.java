package com.sensoro.smartcity.presenter;

import android.content.Context;
import android.content.Intent;

import com.sensoro.smartcity.activity.DeployManualActivity;
import com.sensoro.smartcity.activity.ScanActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IScanActivityView;

import static com.sensoro.smartcity.constant.Constants.EXTRA_IS_STATION_DEPLOY;

public class ScanActivityPresenter extends BasePresenter<IScanActivityView>{
    private Context mContext;

    @Override
    public void initData(Context context) {
        mContext = context;
        updateTitle();


    }

    private void updateTitle() {
        String type = ((ScanActivity) mContext).getIntent().getStringExtra("type");
        if (type!=null) {
            if(Constants.TYPE_SCAN_DEPLOY_DEVICE.equals(type)){
                getView().updateTitleText("设备部署");
                getView().updateQrTipText("对准传感器上的二维码，即可自动扫描");
            }else if(Constants.TYPE_SCAN_LOGIN.equals(type)){
                getView().updateTitleText("扫码登录");
                getView().updateQrTipText("对准登录用二维码，即可自动扫描");
                getView().setBottomVisible(false);
            }
        }
    }

    @Override
    public void onDestroy() {

    }

    public void openSNTextAc() {
        Intent intent = new Intent(mContext, DeployManualActivity.class);
        intent.putExtra(EXTRA_IS_STATION_DEPLOY, false);
        getView().startAC(intent);
    }
}
