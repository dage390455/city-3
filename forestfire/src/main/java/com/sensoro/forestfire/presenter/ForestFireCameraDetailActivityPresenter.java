package com.sensoro.forestfire.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.server.bean.ForestFireCameraBean;
import com.sensoro.common.server.bean.ForestFireCameraDetailInfo;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.forestfire.Constants.ForestFireConstans;
import com.sensoro.forestfire.R;
import com.sensoro.forestfire.imainviews.IForestFireCameraDetailActivityView;

/**
 * @Author: jack
 * 时  间: 2019-09-17
 * 包  名: com.sensoro.forestfire.presenter
 * 简  述: <功能简述>
 */
public class ForestFireCameraDetailActivityPresenter extends BasePresenter<IForestFireCameraDetailActivityView> {
    private Activity mContext;


    private ForestFireCameraBean mForestFireCameraBean;
    private ForestFireCameraDetailInfo mForestFireCameraDetailInfo;


    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
       Bundle mBundle= getBundle(mContext);
        if(mBundle!=null){
            mForestFireCameraBean= (ForestFireCameraBean) mBundle.getSerializable(ForestFireConstans.DEVICE_CAMERA_INFO);
            mForestFireCameraDetailInfo= (ForestFireCameraDetailInfo) mBundle.getSerializable(ForestFireConstans.DEVICE_CAMERA_DETAIL);

            if(mForestFireCameraBean!=null){
                getView().updateCameraName(mForestFireCameraBean.getName());
                getView().updateCameraType(mContext.getString(R.string.forest_fire_camera_detail_device_type_name));
                getView().updateDeviceSN(mForestFireCameraBean.getSn());

                if(mForestFireCameraBean.getForestGateway()!=null){
                    getView().updateGateway(mForestFireCameraBean.getForestGateway().getName());
                }

                getView().updateTime(DateUtil.getStrTimeTodayByDevice(mContext, mForestFireCameraBean.getCreateTime()));
                if(mForestFireCameraBean.getInfo()!=null){
                    getView().updateLocation(mForestFireCameraBean.getInfo().getLongitude(),mForestFireCameraBean.getInfo().getLatitude());
                }
            }

        }
        getView().updateTitle(mContext.getString(R.string.forest_fire_camera_detail));

    }


    @Override
    public void onDestroy() {

    }

   public void startHistoryActivity(){
        Bundle bundle=new Bundle();
        bundle.putString(Constants.EXTRA_SENSOR_SN,mForestFireCameraBean.getSn());
        startActivity(ARouterConstants.ACTIVITY_ALARM_HISTORY_LOG,bundle,mContext);
   }
}
